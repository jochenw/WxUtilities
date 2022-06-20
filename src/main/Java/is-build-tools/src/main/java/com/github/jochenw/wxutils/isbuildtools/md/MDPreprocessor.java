package com.github.jochenw.wxutils.isbuildtools.md;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.github.jochenw.afw.core.log.app.IAppLog;
import com.github.jochenw.afw.core.util.Exceptions;
import com.github.jochenw.afw.core.util.FileUtils;

public class MDPreprocessor {
	private @Inject IAppLog log;
	
	public void read(Path pMdFile, Path pBaseDir, Path pOutputFile) {
		log.info("Creating output file" + pOutputFile);
		FileUtils.createDirectoryFor(pOutputFile);
		try (BufferedWriter w = Files.newBufferedWriter(pOutputFile, StandardCharsets.UTF_8)) {
			read(pMdFile, pBaseDir, w);
		} catch (IOException e) {
			throw Exceptions.show(e);
		}
		log.info("Closing output file.");
	}

	protected void read(Path pMdFile, Path pBaseDir, BufferedWriter pWriter) {
		log.debug("Reading input file: " + pMdFile.toAbsolutePath());
		try (BufferedReader br = Files.newBufferedReader(pMdFile, StandardCharsets.UTF_8)) {
			for (;;) {
				final String line = br.readLine();
				if (line == null) {
					break;
				} else if (line.startsWith("#include")) {
					final String fileSpec = line.substring("#include".length()).trim();
					log.debug("Found request to include input file: " + fileSpec);
					final Path mdFile = findInputFile(fileSpec, pMdFile.getParent(), pBaseDir);
					log.debug("Input fle resolved to " + mdFile);
					read(mdFile, pBaseDir, pWriter);
				} else {
					pWriter.write(line);
					pWriter.newLine();
				}
			}
		} catch (IOException e) {
			throw Exceptions.show(e);
		}
	}

	protected Path findInputFile(String pFileSpec, Path pBaseDir1, Path pBaseDir2) {
		final String fileName;
		if (pFileSpec.startsWith("\"")) {
			if (pFileSpec.endsWith("\"")) {
				fileName = pFileSpec.substring(1, pFileSpec.length()-1);
			} else {
				throw new IllegalStateException("Invalid file specification for #include:"
						+ " Expected terminating '\"', got " + pFileSpec);
			}
		} else if (pFileSpec.startsWith("'")) {
			if (pFileSpec.endsWith("'")) {
				fileName = pFileSpec.substring(1, pFileSpec.length()-1);
			} else {
				throw new IllegalStateException("Invalid file specification for #include:"
						+ " Expected terminating '\"', got " + pFileSpec);
			}
		} else {
			fileName = pFileSpec;
		}
		if (fileName.startsWith("./")) {
			final List<String> files = new ArrayList<>();
			final String relativeFileName = fileName.substring(2);
			if (pBaseDir1 != null) {
				final Path p1 = pBaseDir1.resolve(relativeFileName);
				if (Files.isRegularFile(p1)) {
					return p1;
				} else {
					files.add(p1.toString());
				}
			}
			if (pBaseDir2 != null) {
				final Path p2 = pBaseDir2.resolve(relativeFileName);
				if (Files.isRegularFile(p2)) {
					return p2;
				} else {
					files.add(p2.toString());
				}
			}
			final Path p3 = Paths.get(relativeFileName);
			if (Files.isRegularFile(p3)) {
				return p3;
			} else {
				files.add(p3.toString());
			}
			throw new IllegalStateException("Neither of the following files found"
					+ " for #include, and file specification " + pFileSpec + ": "
					+ String.join(", ", files));
		} else {
			final Path p = Paths.get(fileName);
			if (!p.isAbsolute()) {
				throw new IllegalStateException("Invalid file specification for #include:"
						+ " Expected absolute path, or a relative path with prefix ./, got "
						+ pFileSpec);
			}
			return p;
		}
		
	}
}
