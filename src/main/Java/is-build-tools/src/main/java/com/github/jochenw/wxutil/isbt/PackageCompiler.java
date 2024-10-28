package com.github.jochenw.wxutil.isbt;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class PackageCompiler {
	public static class Input {
		private final List<Path> packageDirs;
		private final String packageName;

		public Input(List<Path> pPackageDirs, String pPackageName) {
			packageDirs = pPackageDirs;
			packageName = pPackageName;
		}

		public List<Path> getPackageDirs() { return packageDirs; }
		public String getPackageName() { return packageName; }
	}

	public static class Data {
		private final Input input;
		private final List<Path> classPathElements = new ArrayList<Path>();
		private final String packageName;
		private final Path packageDir;
		private final List<Path> packageDirs;
		private Path codeClassesDir, codeSourceDir, codeJarsDir;

		public Data(Input pInput, String pPackageName, Path pPackageDir, List<Path> pPackageDirs) {
			input = pInput;
			packageName = pPackageName;
			packageDir = pPackageDir;
			packageDirs = pPackageDirs;
		}

		public Input getInput() { return input; }
		public List<Path> getClassPathElements() { return classPathElements; }
		public String getPackageName() { return packageName; }
		public Path getPackageDir() { return packageDir; }
		public List<Path> getPackageDirs() { return packageDirs; }

		public void addClassPathElement(Path pPath) { classPathElements.add(pPath); }
	}

	protected Path findPackageDir(List<Path> pPackageDirs, String pPackageName) {
		for (Path packagesDir : pPackageDirs) {
			final Path pkgDir = packagesDir.resolve(pPackageName);
			if (Files.isDirectory(pkgDir)) {
				return pkgDir;
			}
		}
		return null;
	}

	protected String getPathList(List<Path> pPaths, String pSep) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0;  i < pPaths.size();  i++) {
			if (i > 0) {
				sb.append(pSep);
			}
			sb.append(pPaths.get(i));
		}
		return sb.toString();
	}

	public void run(Input pInput) {
		final String packageName = pInput.getPackageName();
		final List<Path> packageDirs = pInput.getPackageDirs();
		final Path packageDir = findPackageDir(packageDirs, packageName);
		if (packageDir == null) {
			throw new IllegalArgumentException("Package " + packageName + " not found in"
					+ " either of the following package directories: "
					+ getPathList(packageDirs, ", "));
		}
		run(new Data(pInput, packageName, packageDir, packageDirs));
	}

	public void run(Data pData) {
		if (pData.codeClassesDir != null) {
			pData.codeClassesDir = pData.getPackageDir().resolve("code/classes");
		}
		if (pData.codeSourceDir != null) {
			pData.codeSourceDir = pData.getPackageDir().resolve("code/source");
		}
		if (pData.codeJarsDir != null) {
			pData.codeJarsDir = pData.getPackageDir().resolve("code/jars");
		}
		try {
			runCompiler(pData);
		} catch (IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
	}

	protected void findRequiredPackages(Data pData) {
		
	}

	protected void runCompiler(Data pData) throws IOException {
		JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
		if (javaCompiler == null) {
			throw new IllegalStateException("No Java compiler found.");
		}
		Files.createDirectories(pData.codeClassesDir);
		findClasspathForRequiredPackages(pData);
	}
}
