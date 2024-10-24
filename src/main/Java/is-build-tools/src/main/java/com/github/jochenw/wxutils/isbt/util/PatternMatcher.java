package com.github.jochenw.wxutils.isbt.util;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class PatternMatcher {
	private static final PatternMatcher THE_INSTANCE = new PatternMatcher();

	public static class RePredicate implements Predicate<String> {
		private final String patternStr;
		private final boolean caseSensitive;
		private final Pattern pattern;
		public RePredicate(boolean pCaseSensitive, String pPatternStr) {
			caseSensitive = pCaseSensitive;
			patternStr = pPatternStr;
			if (caseSensitive) {
				pattern = Pattern.compile(patternStr);
			} else {
				pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
			}
		}
		public boolean isCaseSensitive() { return caseSensitive; }
		public String getPatternString() { return patternStr; }
		@Override public boolean test(String pValue) { return pattern.matcher(pValue).matches(); }
	}
	public static RePredicate of(String pPattern) {
		return THE_INSTANCE.create(pPattern);
	}
	public RePredicate create(String pPattern) {
		String pattern = Objects.requireNonNull(pPattern);
		boolean caseSensitive = true;
		if (pPattern.endsWith("/i")) {
			caseSensitive = false;
			pattern = pattern.substring(0, pattern.length()-2);
		}
		final String patternStr;
		if (pattern.startsWith("re:")) {
			patternStr = pattern.substring(3);
		} else {
			patternStr = globToRegex(pattern);
		}
		return new RePredicate(caseSensitive, patternStr);
	}

	protected String globToRegex(String pGlobPattern) {
		final StringBuilder sb = new StringBuilder();
		sb.append('^');
		for (int i = 0; i < pGlobPattern.length();  i++) {
			final char c = pGlobPattern.charAt(i);
			switch(c) {
			case '?':
				sb.append("[^/]");
				break;
			case '*':
				boolean fullGlob = false;
				if (i < pGlobPattern.length()) {
					if (pGlobPattern.charAt(i) == '*') {
						i++;
						fullGlob = true;
					}
				}
				if (fullGlob) {
					sb.append(".*");
				} else {
					sb.append("[^/]");
				}
				break;
			default:
				if (Character.isAlphabetic(c)  ||  Character.isDigit(c)  ||  c == '_') {
					sb.append(c);
				} else {
					sb.append("\\");
					sb.append(c);
				}
			}
		}
		sb.append('$');
		return sb.toString();
	}
}
