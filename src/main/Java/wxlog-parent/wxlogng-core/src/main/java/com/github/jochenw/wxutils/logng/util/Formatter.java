package com.github.jochenw.wxutils.logng.util;

import java.util.function.Function;
import java.util.function.IntFunction;

import com.github.jochenw.afw.core.util.NotImplementedException;

public class Formatter {
	public String format(String pFmtString, IntFunction<String> pNumberedParameters,
			             Function<String,Object> pNamedParameters) {
		throw new NotImplementedException();
	}
}
