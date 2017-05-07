package com.security.csrf.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class SecureTonkensVO {

	private final List<String> viewPathDirectories;
	private final String tokens;
	private final String validationPage;

	public SecureTonkensVO(final String tokens, final String validationpage,
			final String... paths) {
		this.viewPathDirectories = Collections
				.unmodifiableList(new CopyOnWriteArrayList<String>(Arrays
						.asList(paths)));
		this.tokens = tokens;
		this.validationPage = validationpage;

	}

	public List<String> getViewPathDirectories() {
		return viewPathDirectories;
	}

	public String getTokens() {
		return tokens;
	}

	public String getValidationPage() {
		return validationPage;
	}
}
