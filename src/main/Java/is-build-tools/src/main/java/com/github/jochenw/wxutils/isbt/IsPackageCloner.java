package com.github.jochenw.wxutils.isbt;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.github.jochenw.afw.core.function.Functions.FailableBiConsumer;
import com.github.jochenw.afw.core.util.FileUtils;
import com.github.jochenw.afw.core.util.Holder;
import com.github.jochenw.wxutils.isbt.util.PatternMatcher;

/** The package cloner takes as input a package directory in the source
 * tree, and creates a copy in the target tree. The purpose of the
 * copy is to permit automated transformations, and the like,
 * which we wouldn't do in the original source tree.
 */
public class IsPackageCloner {
	public static class CopyableFile {
		private final Path sourceFile;
		private final String targetFileName;
		public CopyableFile(Path pSourceFile, String pTargetFileName) {
			sourceFile = pSourceFile;
			targetFileName = pTargetFileName;
		}
	}
	public static class Input {
		private final String packageName;
		private final Path srcDir, targetDir, timeStampDir;
		private final List<Predicate<String>> exclusions = new ArrayList<>();
		private final boolean cleanTarget;
		private final List<CopyableFile> codeJarFiles = new ArrayList<>();
		private final List<CopyableFile> codeSourceJarFiles = new ArrayList<>();

		private Input(String pPackageName,
				      Path pSrcDir, Path pTargetDir, Path pTimestampDir,
				      boolean pCleanTarget) {
			packageName = pPackageName;
			srcDir = pSrcDir;
			targetDir = pTargetDir;
			timeStampDir = pTimestampDir;
			cleanTarget = pCleanTarget;
		}

		public void addJarFile(Path pSourceFile, String pTargetFile) {
			codeJarFiles.add(new CopyableFile(pSourceFile, pTargetFile));
		}

		public void addSourceJarFile(Path pSourceFile, String pTargetFile) {
			codeSourceJarFiles.add(new CopyableFile(pSourceFile, pTargetFile));
		}

		public void addExclusion(String pPattern) {
			exclusions.add(PatternMatcher.of(pPattern));
		}
	}
	public static class Data {
		private final Input input;
		private final Path packageSourceDir, packageTargetDir;
		private Path timestampFile;

		public Data(Input pInput) {
			input = pInput;
			packageSourceDir = pInput.srcDir.resolve(pInput.packageName);
			packageTargetDir = pInput.targetDir.resolve(pInput.packageName);
		}

		public Input getInput() { return input; }
		public Path getPackageSourceDir() { return packageSourceDir; }
		public Path getPackageTargetDir() { return packageTargetDir; }
	}

	protected BasicFileAttributes getFileAttributes(Path pPath) {
		try {
			return Files.readAttributes(pPath, BasicFileAttributes.class);
		} catch (IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
	}

	protected FileTime findSourceTimestamp(Data pData) {
		final Holder<FileTime> holder = Holder.of(null);
		final Consumer<BasicFileAttributes> fileConsumer = (bfa) -> {
			final FileTime fileTime = bfa.lastModifiedTime();
			final FileTime currentTimestamp = holder.get();
			if (currentTimestamp == null  ||  currentTimestamp.compareTo(fileTime) < 0) {
				holder.set(fileTime);
			}
		};
		final Consumer<CopyableFile> jarFileConsumer = (cf) -> {
			fileConsumer.accept(getFileAttributes(cf.sourceFile));
		};
		walkPackageDirectory(pData, (p, bfa) -> fileConsumer.accept(bfa));
		pData.input.codeJarFiles.forEach(jarFileConsumer);
		pData.input.codeSourceJarFiles.forEach(jarFileConsumer);
		return holder.get();
	}

	private void walkPackageDirectory(Data pData, final FailableBiConsumer<Path,BasicFileAttributes,IOException> pFileConsumer) {
		final FileVisitor<Path> fv = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path pFile, BasicFileAttributes pAttrs) throws IOException {
				pFileConsumer.accept(pFile,pAttrs);
				return super.visitFile(pFile, pAttrs);
			}
		};
		try {
			Files.walkFileTree(pData.getPackageSourceDir(), fv);
		} catch (IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
	}

	protected FileTime findTargetTimestamp(Data pData) {
		final Path timestampFile = pData.timestampFile;
		if (Files.isRegularFile(timestampFile)) {
			return getFileAttributes(timestampFile).creationTime();
		} else {
			return null;
		}
	}

	public void run(Input pInput) {
		final Data data = new Data(pInput);
		data.timestampFile = pInput.timeStampDir.resolve(pInput.packageName + ".timestamp");

		if (pInput.cleanTarget) {
			// No need to check the timestamp of the source files.
			FileUtils.removeDirectory(data.getPackageTargetDir());
		} else {
			if (checkUptodate(data)) {
				return; // Nothing to do.
			}
		}
		copyPackageDirectory(data);
	}

	private void copyPackageDirectory(final Data pData) {
		final Path packageSourceDir = pData.getPackageSourceDir();
		final Path packageTargetDir = pData.getPackageTargetDir();
		walkPackageDirectory(pData, (p, bfa) -> {
			final Path relativePath = packageSourceDir.relativize(p);
			final Path targetFile = packageTargetDir.resolve(relativePath);
			FileUtils.createDirectoryFor(targetFile);
			Files.copy(p, targetFile);
		});
		createTimestampFile(pData);
	}

	private void createTimestampFile(final Data pData) {
		final Path timestampFile = pData.timestampFile;
		FileUtils.createDirectoryFor(timestampFile);
		try (OutputStream out = Files.newOutputStream(timestampFile)) {
			// Write nothing, just create an empty file with the current timestamp.
		} catch (IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
	}

	protected boolean checkUptodate(final Data data) {
		// Find the latest target of the source files
		final FileTime sourceTimeStamp = findSourceTimestamp(data);
		// Find the timestamp of the target files.
		final FileTime targetTimestamp = findTargetTimestamp(data);
		if (sourceTimeStamp == null) {
			return false;
		} else if (targetTimestamp == null) {
			return false;
		} else {
			return targetTimestamp.compareTo(targetTimestamp) > 0;
		}
	}
}
