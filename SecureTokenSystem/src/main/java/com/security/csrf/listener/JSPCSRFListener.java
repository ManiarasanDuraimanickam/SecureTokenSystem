package com.security.csrf.listener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class JSPCSRFListener implements ServletContextListener {
	Logger LOG = Logger.getGlobal();
	private static final String CSRFSecurity_ViewPath = "CSRFSecurity_ViewPath";
	private static final String VALIDATION_PAGE = "SecureTonken_Validation";
	private static SecureTonkens st = null;
	private StringBuilder tokens;
	private List<String> viewPathDirectories = new CopyOnWriteArrayList<String>();

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// TODO Auto-generated method stub

		String[] viewPaths = sce.getServletContext()
				.getInitParameter(CSRFSecurity_ViewPath).split(",");
		String validationPage = sce.getServletContext().getInitParameter(
				VALIDATION_PAGE);
		Path path1 = null;
		LinkOption[] linkOptions = { LinkOption.NOFOLLOW_LINKS };
		try {
			path1 = Paths.get(sce.getServletContext().getRealPath(""));
			this.setViewPathDirectories(path1.toString());
			for (String pths : viewPaths) {
				path1 = Paths
						.get(sce.getServletContext().getRealPath(""), pths);
				if (Files.isDirectory(path1, linkOptions)) {
					this.setViewPathDirectories(path1.toString());
				}
			}
		} finally {
			path1 = null;
			viewPaths = null;
		}
		st = new JSPCSRFListener.SecureTonkens(validationPage);

		try {
			this.viewPathDirectories.parallelStream().forEach((path) -> {
				try {
					Files.list(Paths.get(path)).filter(x -> {
						if (Files.isDirectory(x, linkOptions)) {
							viewPathDirectories.add(x.toString());
						}
						return Files.isRegularFile(x, linkOptions);
					}).filter(x -> {
						return x.toString().endsWith(".jsp");
					}).forEach(x -> {
						writeFile(x);
					});
				} catch (Exception e) {
					LOG.log(Level.ALL, e.getMessage(), e);
				}
			});
		} finally {
			viewPathDirectories = null;
		}
	}

	private void writeFile(Path path) {
		try {
			List<String> fileLines = Files.readAllLines(path);
			fileLines = (List<String>) fileLines.parallelStream().map(s -> {
				return s.replace("</form>", this.getTokens().toString());
			}).collect(Collectors.toList());
			Files.write(path, fileLines, StandardOpenOption.CREATE,
					StandardOpenOption.WRITE,
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			LOG.log(Level.INFO, e.getMessage(), e);
		}
	}

	protected StringBuilder getTokens() {
		if (tokens == null) {
			tokens = new StringBuilder();
			tokens.append(System.lineSeparator());
			tokens.append("<input type=\"hidden\" name=\"_csfrID_01\" value=\"<%=request.getParameter(\"_csfr01_02\")!=null?request.getParameter(\"_csfr01_02\"):response.getHeader(\"_csfr01_02\")%>\" />");
			tokens.append(System.lineSeparator());
			tokens.append("<input type=\"hidden\" name=\"_csfrID_03\" value=\"<%=request.getParameter(\"_csfr03_1\")!=null?request.getParameter(\"_csfr03_1\"):response.getHeader(\"_csfr03_1\")%>\" />");
			tokens.append(System.lineSeparator());
			tokens.append("</form>");
		}
		return tokens;
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}

	private void setViewPathDirectories(String path) {
		if (!this.viewPathDirectories.contains(path)) {
			this.viewPathDirectories.add(path);
		}
	}

	public static SecureTonkens getSecureTonkens() {
		return st;
	}

	public final class SecureTonkens {

		private final String validationPage;

		public SecureTonkens(final String validationpage) {
			this.validationPage = validationpage;
		}

		public String getValidationPage() {
			return validationPage;
		}
	}

}
