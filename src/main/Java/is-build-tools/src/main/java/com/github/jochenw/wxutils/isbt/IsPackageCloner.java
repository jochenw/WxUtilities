package com.github.jochenw.wxutils.isbt;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import com.github.jochenw.afw.core.function.Functions.FailableBiConsumer;
import com.github.jochenw.afw.core.util.FileUtils;
import com.github.jochenw.afw.core.util.Holder;
import com.github.jochenw.afw.core.util.MutableInteger;
import com.github.jochenw.afw.core.util.Objects;
import com.github.jochenw.afw.core.util.Tupel;
import com.github.jochenw.wxutils.isbt.util.DirectoryScanner;


/** The package cloner takes as input a package directory in the source
 * tree, and creates a copy in the target tree. The purpose of the
 * copy is to permit automated transformations, and the like,
 * which we wouldn't do in the original source tree.
 */
public class IsPackageCloner {
	public static class Input {
		private final String packageName;
		private final String[] inclusions, exclusions;
		private final Path srcDir, targetDir, timeStampDir;
		private final boolean cleanTarget;

		public Input(String pPackageName,
				      String[] pInclusions, String[] pExclusions,
				      Path pSrcDir, Path pTargetDir, Path pTimestampDir,
				      boolean pCleanTarget) {
			packageName = pPackageName;
			inclusions = pInclusions;
			exclusions = pExclusions;
			srcDir = pSrcDir;
			targetDir = pTargetDir;
			timeStampDir = pTimestampDir;
			cleanTarget = pCleanTarget;
		}

		public String getPackageName() { return packageName; }
		public String[] getInclusions() { return inclusions; }
		public String[] getExclusions() { return exclusions; }
		public Path getSrcDir() { return srcDir; }
		public Path getTargetDir() { return targetDir; }
		public Path getTimeStampDir() { return timeStampDir; }
		public boolean isCleanTarget() { return cleanTarget; }
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

		public String[] getInclusions() { return input.getInclusions(); }
		public String[] getExclusions() { return input.getExclusions(); }
	}

	private Logger logger = Logger.NULL_LOGGER;

	public void setLogger(Logger pLogger) { logger = Objects.requireNonNull(pLogger, "Logger"); }
	public Logger getLogger() { return logger; }
	
	
	protected BasicFileAttributes getFileAttributes(Path pPath) {
		try {
			return Files.readAttributes(pPath, BasicFileAttributes.class);
		} catch (IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
	}

	protected Tupel<FileTime, Path> findSourceTimestamp(Data pData) {
		final Holder<FileTime> holderFileTime = Holder.of(null);
		final Holder<Path> holderPath = Holder.of(null);
		final FailableBiConsumer<Path,BasicFileAttributes,IOException> fileConsumer = (p,bfa) -> {
			final FileTime fileTime = bfa.lastModifiedTime();
			final FileTime currentTimestamp = holderFileTime.get();
			if (currentTimestamp == null  ||  currentTimestamp.compareTo(fileTime) < 0) {
				holderFileTime.set(fileTime);
				holderPath.set(p);
			}
		};
		walkPackageDirectory(pData, fileConsumer);
		return Tupel.of(holderFileTime.get(), holderPath.get());
	}

	private void walkPackageDirectory(Data pData, final FailableBiConsumer<Path,BasicFileAttributes,IOException> pFileConsumer) {
		DirectoryScanner.getInstance().scan(pData.getPackageSourceDir(), pData.getInclusions(),
				              pData.getExclusions(), pFileConsumer, true);
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
		logger.info("IsPackageCloner.run: packageName={}", pInput.packageName);
		final Data data = new Data(pInput);
		data.timestampFile = pInput.timeStampDir.resolve(pInput.packageName + ".timestamp");

		if (pInput.cleanTarget) {
			logger.info("ISPackageCloner.run; Clean option is given, removing target directory {}",
					    data.getPackageTargetDir());
			// No need to check the timestamp of the source files.
			FileUtils.removeDirectory(data.getPackageTargetDir());
		} else {
			if (checkUptodate(data)) {
				logger.info("ISPackageCloner.run: Uptodate ({})", pInput.packageName);
				return; // Nothing to do.
			} else {
				logger.info("ISPackageCloner.run: Not uptodate ({})", pInput.packageName);
			}
		}
		copyPackageDirectory(data);
		logger.info("ISPackageName.run: Done ({})", pInput.packageName);
	}

	private void copyPackageDirectory(final Data pData) {
		final Path packageSourceDir = pData.getPackageSourceDir();
		final Path packageTargetDir = pData.getPackageTargetDir();
		logger.debug("ISPackageCloner.copy: Performing copy of {} to {}", packageSourceDir, packageTargetDir);
		final String[] inclusions = pData.getInclusions();
		if (inclusions != null  &&  inclusions.length > 0) {
			logger.debug("ISPackageCloner.copy: Inclusions present: {}", (Object) inclusions);
		}
		final String[] exclusions = pData.getExclusions();
		if (exclusions != null  &&  exclusions.length > 0) {
			logger.debug("ISPackageCloner.copy: Exclusions present: {}", (Object) exclusions);
		}
		final MutableInteger fileCounter = new MutableInteger();
		walkPackageDirectory(pData, (p, bfa) -> {
			final Path sourceFile = packageSourceDir.resolve(p);
			final Path targetFile = packageTargetDir.resolve(p);
			FileUtils.createDirectoryFor(targetFile);
			Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
			fileCounter.inc();
		});
		logger.trace("ISPackageCloner.copy: Creating timestamp file {}", pData.timestampFile);
		createTimestampFile(pData);
		logger.debug("ISPackageCloner.copy: Done, copied {} files", fileCounter);
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
		final Tupel<FileTime,Path> sourceTupel = findSourceTimestamp(data);
		final FileTime sourceTimeStamp = sourceTupel.getAttribute1();
		// Find the timestamp of the target files.
		final FileTime targetTimestamp = findTargetTimestamp(data);
		if (sourceTimeStamp == null) {
			logger.trace("ISPackageCloner.checkUptoDate: Not uptodate (No source files?).");
			return false;
		} else if (targetTimestamp == null) {
			logger.trace("ISPackageCloner.checkUptodate: Not uptodate (No target timestamp).");
			return false;
		} else {
			final boolean result = targetTimestamp.compareTo(targetTimestamp) > 0;
			logger.trace("ISPackageCloner.checkUptodate: Uptodate (Souce timestamp from file {}"
					+ " precedes target timestamp: {}", sourceTupel.getAttribute2(),
					Boolean.valueOf(result));
			return result;
		}
	}
}
