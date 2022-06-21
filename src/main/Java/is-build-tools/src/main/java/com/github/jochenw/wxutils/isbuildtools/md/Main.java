package com.github.jochenw.wxutils.isbuildtools.md;

import java.nio.file.Path;

import com.github.jochenw.afw.core.cli.Cli;
import com.github.jochenw.afw.core.log.app.DefaultAppLog;
import com.github.jochenw.afw.core.log.app.IAppLog;
import com.github.jochenw.afw.core.log.app.IAppLog.Level;
import com.github.jochenw.afw.core.log.app.SystemOutAppLog;
import com.github.jochenw.afw.core.util.Exceptions;
import com.github.jochenw.afw.core.util.Holder;
import com.github.jochenw.afw.di.api.ComponentFactoryBuilder;
import com.github.jochenw.afw.di.api.IComponentFactory;
import com.github.jochenw.afw.di.api.Scopes;
import com.github.jochenw.wxutils.isbuildtools.actions.MDPreprocessorAction;
import com.github.jochenw.wxutils.isbuildtools.actions.MDPreprocessorAction.Options;

public class Main {
	public void run(String[] pArgs) {
		final Holder<Level> logLevelHolder = new Holder<Level>();
		final Holder<Path> logFileHolder = new Holder<Path>();
		final Options options = Cli.of(new Options())
			.pathOption("inputFile",  "input", "in").fileRequired().existsRequired().handler((c,p) -> c.getBean().setMdFile(p)).end()
			.pathOption("baseDir", "dir").dirRequired().existsRequired().handler((c,p) -> c.getBean().setBaseDir(p)).end()
			.pathOption("outputFile", "output", "out").handler((c,p) -> c.getBean().setOutputFile(p)).end()
			.pathOption("logFile", "lf").handler((c,p) -> logFileHolder.set(p)).end()
			.enumOption(IAppLog.Level.class, "logLevel", "ll").handler((c,l) -> logLevelHolder.set(l)).end()
			.beanValidator((opt) -> {
				if (opt.getMdFile() == null) {
					return "Required option missing: --inputFile";
				}
				if (opt.getOutputFile() == null) {
					return "Required option missing: --outputFile";
				}
				return null;
			}).parse(pArgs);
		final IComponentFactory cf = new ComponentFactoryBuilder().module((b) -> {
			b.bind(MDPreprocessorAction.class).in(Scopes.SINGLETON);
			b.bind(MDPreprocessor.class).in(Scopes.SINGLETON);
			b.bind(IAppLog.class).toInstance(IAppLog.of(logLevelHolder.get(), logFileHolder.get()));
		}).build();
		try {
			final MDPreprocessorAction action = cf.requireInstance(MDPreprocessorAction.class);
			action.run(options);
		} catch (Exception e) {
			throw Exceptions.show(e);
		}
	}
	public static void main(String[] pArgs) {
		new Main().run(pArgs);
	}

}
