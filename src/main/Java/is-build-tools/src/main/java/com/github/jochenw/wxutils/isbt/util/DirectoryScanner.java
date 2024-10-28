package com.github.jochenw.wxutils.isbt.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Predicate;

import com.github.jochenw.afw.core.function.Functions;
import com.github.jochenw.afw.core.function.Functions.FailableBiConsumer;

public class DirectoryScanner {
	private static final DirectoryScanner THE_INSTANCE = new DirectoryScanner();
	public static final DirectoryScanner getInstance() { return THE_INSTANCE; }

	public void scan(Path pDirectory, String[] pInclusions, String[] pExclusions, FailableBiConsumer<Path,BasicFileAttributes,?> pFileListener, boolean pRelativePaths) {
		final Predicate<String> predicate = PatternMatcher.of(pInclusions, pExclusions);

		final FileVisitor<Path> fv = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path pFile, BasicFileAttributes pAttrs) throws IOException {
				if (!pDirectory.equals(pFile)) {
					final Path relativePath = pDirectory.relativize(pFile);
					final String relativeUri = relativePath.toString().replace('\\', '/');
					if (predicate.test(relativeUri)) {
						Path path;
						if (pRelativePaths) {
							path = Paths.get(relativeUri);
						} else {
							path = pFile;
						}
						Functions.accept(pFileListener, path, pAttrs);
					}
				}
				return FileVisitResult.CONTINUE;
			}
		};
		try {
			Files.walkFileTree(pDirectory, fv);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
