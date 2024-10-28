package com.github.jochenw.wxutils.isbt;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.github.jochenw.afw.core.function.Functions.FailableConsumer;
import com.github.jochenw.afw.core.util.Holder;
import com.github.jochenw.afw.di.util.Exceptions;
import com.github.jochenw.wxutils.isbt.Logger.Level;

class IsPackageClonerTest {
	public static interface TestDetails {
		IsPackageCloner getIpc();
		String[] getInclusions();
		String[] getExclusions();
		Path getPackagesDir();
		Path getTargetPackageDir();
		Path getTimestampDir();
		default Path getTimestampFile() { return getTimestampDir().resolve("WxLogNg.timestamp"); }
		default FileTime getTimestampFileTime() {
			try {
				return Files.getLastModifiedTime(getTimestampFile());
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}
	protected IsPackageCloner newIpc() throws IOException {
		final IsPackageCloner ipc = new IsPackageCloner();
		final Logger logger = new Logger() {
			@Override
			protected void log(Level pLevel, String pMsg) {
				System.out.println(pLevel + " " + pMsg);
			}
		};
		logger.setLevel(Level.trace);
		ipc.setLogger(logger);
		return ipc;
	}

	/** Test cloning to an empty target directory.
	 */
	@Test
	void testInitialClone() {
		final FileTime now = FileTime.from(Instant.now());
		final FailableConsumer<TestDetails,?> consumer = (td) -> {
			final IsPackageCloner.Input input = new IsPackageCloner.Input("WxLogNg",
					td.getInclusions(), td.getExclusions(), td.getPackagesDir(),
					td.getTargetPackageDir(), td.getTimestampDir(), false);
			td.getIpc().run(input);
		};
		final TestDetails td = run(consumer);
		// We have been cloning to a new, empty directory. So, the timestamp must
		// be new.
		assertTrue(now.compareTo(td.getTimestampFileTime()) <= 0);
	}

	/** Test cloning to a target directory, which is uptodate.
	 */
	@Test
	void testTargetUptodate() {
		final Holder<FileTime> timestampAfterInitialClone = Holder.of(null);
		final FailableConsumer<TestDetails,?> consumer = (td) -> {
			final IsPackageCloner.Input input = new IsPackageCloner.Input("WxLogNg",
					td.getInclusions(), td.getExclusions(), td.getPackagesDir(),
					td.getTargetPackageDir(), td.getTimestampDir(), false);
			td.getIpc().run(input);
			timestampAfterInitialClone.set(td.getTimestampFileTime());
			// Run the clone again. No changes have been made, so nothing
			// should happen.
			td.getIpc().run(input);
		};
		final TestDetails td = run(consumer);
		// We have been cloning to a directory with no changes since the initial
		// clone. So, nothing has happened, and the timestamp remains the same.
		assertTrue(timestampAfterInitialClone.get().compareTo(td.getTimestampFileTime()) == 0);
	}
	
	
	protected TestDetails run(FailableConsumer<TestDetails,?> pConsumer) {
		final Path packagesPath = Paths.get("../../IS/packages");
		assumeTrue(Files.isDirectory(packagesPath), "Packages directory not found.");
		final Path wxLogNgSourcePath = packagesPath.resolve("WxLogNg");
		assumeTrue(Files.isDirectory(wxLogNgSourcePath), "WxLogNg package not found.");
		final Path testDir = Paths.get("target/unit-tests/IsPackageClonerTest");
		final Path tempDir, timeStampDir;
		final IsPackageCloner ipc;
		try {
			tempDir = Files.createTempDirectory(testDir, "pkg");
			timeStampDir = testDir.resolve("timestamps");
			Files.createDirectories(timeStampDir);
			ipc = newIpc();
			final TestDetails testDetails = new TestDetails() {
				@Override public Path getTargetPackageDir() { return tempDir; }
				@Override public Path getPackagesDir() { return packagesPath; }
				@Override public IsPackageCloner getIpc() { return ipc; }
				@Override public String[] getInclusions() { return null; }
				@Override
				public String[] getExclusions() {
					final String[] exclusions = {"code/classes/**"};
					return exclusions;
				}
				@Override public Path getTimestampDir() { return timeStampDir; }
			};
			pConsumer.accept(testDetails);
			return testDetails;
		} catch (Throwable t) {
			throw Exceptions.show(t);
		}
	}
}
