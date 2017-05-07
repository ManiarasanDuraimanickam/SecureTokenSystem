package com.security.csrf.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.security.csrf.listener.JSPCSRFListener;

public class CSRFActiveFilter implements Filter {

	private String name = "_csfr03_1";
	private String name1 = "_csfr01_02";

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest sr, ServletResponse srp, FilterChain fc)
			throws IOException, ServletException {
		String _csfr03_1 = (String) sr.getParameter("_csfr03_1");
		String _csfr01_02 = (String) sr.getParameter("_csfr01_02");
		if ((null == _csfr01_02 || null == _csfr03_1)
				&& (!((HttpServletRequest) sr)
				.getContextPath().concat(JSPCSRFListener.getSecureTonkens().getValidationPage()).equals( 
								((HttpServletRequest) sr).getRequestURI()))) {
			((HttpServletResponse) srp).getWriter().append(
					"Secure Token missing...");
		} else {
			((HttpServletResponse) srp).addHeader(name,
					"" + System.currentTimeMillis());
			((HttpServletResponse) srp).addHeader(name1,
					"" + System.currentTimeMillis());
			fc.doFilter(sr, srp);
		}
	}

	@Override
	public void init(FilterConfig fc) throws ServletException {

	}

}
