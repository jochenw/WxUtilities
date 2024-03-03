package com.github.jochenw.wxutils.logng.impl;

import java.nio.file.Path;

/** Interface of an object, which creates a new file generation
 * by rotating the various generations.
 */
public interface IGenFileRotator {
	public void rotate(Path pDir, String[] pGenerations);
}
