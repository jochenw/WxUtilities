package wx.log.ng.core.util;

import java.util.function.Function;
import java.util.function.IntFunction;

import com.github.jochenw.afw.core.util.Strings;

public class StringFormatter {
	private static final StringFormatter THE_INSTANCE = new StringFormatter("${", "}");

	public static StringFormatter getInstance() { return THE_INSTANCE; }

	private final String prefix, suffix;

	public StringFormatter(String pPrefix, String pSuffix) {
		prefix = pPrefix;
		suffix = pSuffix;
	}

	public String getPrefix() { return prefix; }
	public String getSuffix() { return suffix; }

	public static String formatted(String pValue, Function<String,Object> pNamedParameters,
			             IntFunction<Object> pNumberedParameters) {
		return getInstance().format(pValue, pNamedParameters, pNumberedParameters);
	}

	public String format(String pValue, Function<String,Object> pNamedParameters,
			IntFunction<Object> pNumberedParameters) {
		if (pValue.indexOf(prefix) == -1) {
			// The value doesn't contain any parameter references, so return it
			// unmodified.
			return pValue;
		}
		final StringBuilder sb = new StringBuilder(pValue);
		for (;;) {
			final int startOffset = sb.lastIndexOf(prefix);
			if (startOffset == -1) {
				break;
			}
			final int endOffset = sb.indexOf(suffix, startOffset+prefix.length());
			if (endOffset == -1) {
				throw new IllegalArgumentException("Unterminated parameter reference in formatter input: " + pValue);
			}
			final String variableName = sb.substring(startOffset+prefix.length(), endOffset);
			Object value;
			try {
				int variableNumber = Integer.parseInt(variableName);
				if (pNumberedParameters == null) {
					throw new IllegalStateException("Numbered parameters are not available in this context.");
				}
				value = pNumberedParameters.apply(variableNumber);
			} catch (NumberFormatException nfe) {
				if (pNamedParameters == null) {
					throw new IllegalStateException("Named parameters are not available in this context.");
				}
				value = pNamedParameters.apply(variableName);
			}
			if (value == null) {
				throw new NullPointerException("Parameter is not defined, or null: " + variableName);
			}
			final String val = asString(value);
			sb.replace(startOffset, endOffset+suffix.length(), val);
		}
		return sb.toString();
	}

	protected String asString(Object pValue) {
		return Strings.toString(pValue);
	}
}
