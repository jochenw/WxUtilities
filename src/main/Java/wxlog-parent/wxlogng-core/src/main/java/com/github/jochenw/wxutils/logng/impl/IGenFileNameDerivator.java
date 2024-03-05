package com.github.jochenw.wxutils.logng.impl;

public interface IGenFileNameDerivator {
	public String getFileNameForGeneration(String pBaseName, int i);
	public String[] getFileNamesForGenerations(String pBaseName, int pNumGenerations);
}
