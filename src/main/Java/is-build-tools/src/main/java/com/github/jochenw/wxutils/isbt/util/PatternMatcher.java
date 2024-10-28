package com.github.jochenw.wxutils.isbt.util;

import java.lang.reflect.Array;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.github.jochenw.afw.core.function.Predicates;

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
		for (int i = 0; i < pGlobPattern.length();  ) {
			final char c = pGlobPattern.charAt(i++);  // Consume the next character
			switch(c) {
			case '?':
				sb.append("[^/]");
				break;
			case '*':
				// Is this a single '*', or a "**"?
				boolean doubleWildcard = false;
				if (i < pGlobPattern.length()) {
					if (pGlobPattern.charAt(i) == '*') {
						i++; // Consume the second '*' character
						doubleWildcard = true;
					}
				}
				if (doubleWildcard) {
					sb.append(".*");
				} else {
					sb.append("[^/]*");
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

	public static Predicate<String> of(String[] pInclusions, String[] pExclusions) {
		final Predicate<String> inclusionsPredicate = of(pInclusions);
		final Predicate<String> exclusionsPredicate = of(pExclusions);
		if (inclusionsPredicate == null) {
			if (exclusionsPredicate == null) {
				return Predicates.alwaysTrue();
			} else {
				return exclusionsPredicate.negate();
			}
		} else {
			if (exclusionsPredicate == null) {
				return inclusionsPredicate;
			} else {
				return (s) -> inclusionsPredicate.test(s)  &&  !exclusionsPredicate.test(s);
			}
		}
	}

	public static Predicate<String> of(String[] pPatterns) {
		if (pPatterns == null  ||  pPatterns.length == 0) {
			return null;
		}
		@SuppressWarnings("unchecked")
		final Predicate<String>[] predicates = (Predicate<String>[]) Array.newInstance(Predicate.class, pPatterns.length);
		for (int i = 0;  i < pPatterns.length;  i++) {
			predicates[i] = of(pPatterns[i]);
		}
		if (predicates.length == 1) {
			return predicates[0];
		} else {
			return (s) -> {
				for (Predicate<String> pred : predicates) {
					if (!pred.test(s)) {
						return false;
					}
				}
				return true;
			};
		}
	}
}
