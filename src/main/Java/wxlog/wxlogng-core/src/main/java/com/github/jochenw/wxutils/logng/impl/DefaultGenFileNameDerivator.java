package com.github.jochenw.wxutils.logng.impl;

public class DefaultGenFileNameDerivator implements IGenFileNameDerivator {
	@Override
	public String getFileNameForGeneration(String pBaseName, int i) {
		final int offset = pBaseName.lastIndexOf('.');
		if (offset == -1) {
			return pBaseName + i;
		} else {
			final String prefix = pBaseName.substring(0, offset);
			final String extension = pBaseName.substring(offset);
			return prefix + "-" + i + extension;
		}
	}

	@Override
	public String[] getFileNamesForGenerations(String pBaseName, int pNumGenerations) {
		final String[] fileNames = new String[pNumGenerations];
		final int offset = pBaseName.lastIndexOf('.');
		fileNames[0] = pBaseName;
		if (offset == -1) {
			for (int i = 1;  i < fileNames.length;  i++) {
				fileNames[i] = pBaseName + i;
			}
		} else {
			final String prefix = pBaseName.substring(0, offset);
			final String extension = pBaseName.substring(offset);
			for (int i = 1;  i < fileNames.length;  i++) {
				fileNames[i] = prefix + "-" + i + extension;
			}
		}
		return fileNames;
	}
}
