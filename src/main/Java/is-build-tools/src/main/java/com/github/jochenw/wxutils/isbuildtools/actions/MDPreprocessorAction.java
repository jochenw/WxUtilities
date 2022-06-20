package com.github.jochenw.wxutils.isbuildtools.actions;

import java.nio.file.Path;

import javax.inject.Inject;

import com.github.jochenw.wxutils.isbuildtools.md.MDPreprocessor;

public class MDPreprocessorAction implements IAction<MDPreprocessorAction.Options> {
	public static class Options {
		private Path mdFile;
		private Path baseDir;
		private Path outputFile;
		public Path getMdFile() {
			return mdFile;
		}
		public void setMdFile(Path pMdFile) { mdFile = pMdFile; }
		public Path getBaseDir() { return baseDir; }
		public void setBaseDir(Path pBaseDir) { baseDir = pBaseDir; }
		public Path getOutputFile() { return outputFile; }
		public void setOutputFile(Path pOutputFile) { outputFile = pOutputFile; }
	}

	private @Inject MDPreprocessor preProcessor;

	@Override
	public void run(MDPreprocessorAction.Options pOptions) throws Exception {
		preProcessor.read(pOptions.getMdFile(), pOptions.getBaseDir(), pOptions.getOutputFile());
	}
}