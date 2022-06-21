package com.github.jochenw.wxutils.isbuildtools.actions;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.github.jochenw.afw.core.log.app.DefaultAppLog;
import com.github.jochenw.afw.core.log.app.IAppLog;
import com.github.jochenw.afw.core.log.app.IAppLog.Level;
import com.github.jochenw.afw.di.api.ComponentFactoryBuilder;
import com.github.jochenw.afw.di.api.IComponentFactory;
import com.github.jochenw.afw.di.api.Module;
import com.github.jochenw.afw.di.api.Scopes;
import com.github.jochenw.wxutils.isbuildtools.actions.MDPreprocessorAction.Options;
import com.github.jochenw.wxutils.isbuildtools.md.MDPreprocessor;
import com.github.jochenw.wxutils.isbuildtools.md.Main;

class MDPreprocessorActionTest {

	/** Test for creating an MD file, using the native
	 * {@link MDPreprocessorAction}.
	 */
	@Test
	void testReadMdFileUsingAction() throws Exception {
		final Module module = (b) -> {
			final DefaultAppLog appLog = new DefaultAppLog(Level.TRACE, StandardCharsets.UTF_8, "\n", System.out);
			b.bind(IAppLog.class).toInstance(appLog);
			b.bind(MDPreprocessorAction.class);
			b.bind(MDPreprocessor.class).in(Scopes.SINGLETON);
		};
		final IComponentFactory cf = new ComponentFactoryBuilder().module(module).build();
		final Path testDir = Paths.get("target/unit-tests/MDPreprocessorActionTest");
		final Path sourceDir = Paths.get("src/test/resources/com/github/jochenw/wxutils/isbuildtools/actions/md");
		final Path outputFile = testDir.resolve("output.md");
		final Options options = new Options();
		options.setMdFile(sourceDir.resolve("README.md"));
		options.setOutputFile(outputFile);
		Files.deleteIfExists(outputFile);
		cf.requireInstance(MDPreprocessorAction.class).run(options);

		validate(sourceDir, outputFile);
	}

	protected void validate(final Path pSourceDir, final Path pOutputFile) throws IOException {
		try (BufferedReader br1 = Files.newBufferedReader(pSourceDir.resolve("expected-output.md"));
			 BufferedReader br2 = Files.newBufferedReader(pOutputFile)) {
			for (;;) {
				final String line1 = br1.readLine();
				final String line2 = br2.readLine();
				if (line1 == null) {
					assertNull(line2);
					break;
				} else {
					assertNotNull(line2);
					assertEquals(line1, line2);
				}
			}
		}
	}

	/** Test for creating an MD file, using the native
	 * MDPreprocessor's {@link Main#main(String[]) CLI}.
	 */
	@Test
	void testReadMdFileUsingMain() throws Exception {
		final Path testDir = Paths.get("target/unit-tests/MDPreprocessorActionTest");
		final Path sourceDir = Paths.get("src/test/resources/com/github/jochenw/wxutils/isbuildtools/actions/md");
		final Path outputFile = testDir.resolve("output.md");
		Files.deleteIfExists(outputFile);
		new Main().run(new String[] {
				"-inputFile", sourceDir.resolve("README.md").toString(),
				"-baseDir", sourceDir.toString(),
				"-outputFile", outputFile.toString()
		});

		validate(sourceDir, outputFile);
	}
}
