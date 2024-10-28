package com.github.jochenw.wxutils.isbt.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.github.jochenw.wxutils.isbt.util.PatternMatcher.RePredicate;

class PatternMatcherTest {
	@Test
	void testCodeClassesPattern() {
		final RePredicate codeClassesPredicate = PatternMatcher.of("code/classes/**");
		assertTrue(codeClassesPredicate.isCaseSensitive());
		assertEquals("^code\\/classes\\/.*$", codeClassesPredicate.getPatternString());
		assertNotNull(codeClassesPredicate);
		assertFalse(codeClassesPredicate.test("code/.gitignore"));
		assertTrue(codeClassesPredicate.test("code/classes/"));
		assertTrue(codeClassesPredicate.test("code/classes/wx/log/admin/startUp/node.idf"));
	}

	@Test
	void testTextFilesPattern( ) {
		final RePredicate textFilePredicate = PatternMatcher.of("**/*.txt");
		assertTrue(textFilePredicate.isCaseSensitive());
		assertEquals("^.*\\/[^/]*\\.txt$", textFilePredicate.getPatternString());
		assertNotNull(textFilePredicate);
		assertFalse(textFilePredicate.test("foo.txt"));
		assertTrue(textFilePredicate.test("/foo.txt"));
		assertFalse(textFilePredicate.test("/foo.tXt"));
	}

	@Test
	void testTextFilesPatternCaseInsensitive( ) {
		final RePredicate textFilePredicate = PatternMatcher.of("**/*.txt/i");
		assertFalse(textFilePredicate.isCaseSensitive());
		assertEquals("^.*\\/[^/]*\\.txt$", textFilePredicate.getPatternString());
		assertNotNull(textFilePredicate);
		assertFalse(textFilePredicate.test("foo.txt"));
		assertTrue(textFilePredicate.test("/foo.txt"));
		assertTrue(textFilePredicate.test("/foo.tXt"));
	}

	@Test
	void testRegExp( ) {
		final RePredicate textFilePredicate = PatternMatcher.of("re:^.*\\/[^/]*\\.(txt|log)$");
		assertTrue(textFilePredicate.isCaseSensitive());
		assertEquals("^.*\\/[^/]*\\.(txt|log)$", textFilePredicate.getPatternString());
		assertNotNull(textFilePredicate);
		assertFalse(textFilePredicate.test("foo.txt"));
		assertFalse(textFilePredicate.test("foo.log"));
		assertTrue(textFilePredicate.test("/foo.txt"));
		assertTrue(textFilePredicate.test("/foo.log"));
		assertFalse(textFilePredicate.test("/foo.tXt"));
	}

}
