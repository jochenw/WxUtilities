package com.github.jochenw.wxutils.isbuildtools.md;

import com.github.jochenw.afw.core.cli.Cli;
import com.github.jochenw.afw.core.util.Exceptions;
import com.github.jochenw.wxutils.isbuildtools.actions.MDPreprocessorAction;
import com.github.jochenw.wxutils.isbuildtools.actions.MDPreprocessorAction.Options;

public class Main {

	public void run(String[] pArgs) {
		final Options options = new Options();
		new Cli<Options>(options)
			.pathOption("inputFile",  "input", "in").fileRequired().existsRequired().handler((c,p) -> options.setMdFile(p)).end()
			.pathOption("baseDir", "dir").dirRequired().existsRequired().handler((c,p) -> options.setBaseDir(p)).end()
			.pathOption("outputFile", "output", "out").handler((c,p) -> options.setOutputFile(p)).end()
			.beanValidator((opt) -> {
				if (opt.getMdFile() == null) {
					return "Required option missing: --inputFile";
				}
				if (opt.getOutputFile() == null) {
					return "Required option missing: --outputFile";
				}
				return null;
			}).parse(pArgs);
		try {
			new MDPreprocessorAction().run(options);
		} catch (Exception e) {
			throw Exceptions.show(e);
		}
	}
	public static void main(String[] pArgs) {
		new Main().run(pArgs);
	}

}
