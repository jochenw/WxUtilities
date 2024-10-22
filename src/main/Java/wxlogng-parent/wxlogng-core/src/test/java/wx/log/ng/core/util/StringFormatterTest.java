package wx.log.ng.core.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class StringFormatterTest {
	/** Test formatting a string with a few simple, named parameters.
	 */
	@Test
	void testSimpleNamedParameters() {
		final Map<String,Object> parameters = new HashMap<>();
		parameters.put("foo", "bar");
		parameters.put("answer", Integer.valueOf(42));
		parameters.put("boolean", Boolean.FALSE);
		final String input = "Hello, ${foo}, the expected result is ${boolean}, and the answer is: ${answer}.";
		final String expectedOutput = "Hello, bar, the expected result is false, and the answer is: 42.";
		final String actualOutput = StringFormatter.formatted(input, parameters::get, null);
		assertEquals(expectedOutput, actualOutput);
	}

	/** Test formatting a string with a few simple, numbered parameters.
	 */
	@Test
	void testSimpleNumberedParameters() {
		final List<Object> list = new ArrayList<>();
		list.add("bar");
		list.add(Integer.valueOf(42));
		list.add(Boolean.FALSE);
		final String input = "Hello, ${0}, the expected result is ${2}, and the answer is: ${1}.";
		final String expectedOutput = "Hello, bar, the expected result is false, and the answer is: 42.";
		final String actualOutput = StringFormatter.formatted(input, null, list::get);
		assertEquals(expectedOutput, actualOutput);
	}

	/** Test referencing an undefined, named parameter.
	 */
	@Test
	void testMissingNamedParameter() {
		final Map<String,Object> parameters = new HashMap<>();
		parameters.put("foo", "bar");
		parameters.put("boolean", Boolean.FALSE);
		final String input = "Hello, ${foo}, the expected result is ${boolean}, and the answer is: ${answer}.";
		try {
			StringFormatter.formatted(input, parameters::get, null);
			fail("Expected Exception");
		} catch (NullPointerException e) {
			assertEquals("Parameter is not defined, or null: answer", e.getMessage());
		}
	}

	/** Test referencing a named parameter with the value null.
	 */
	@Test
	void testNullNamedParameter() {
		final Map<String,Object> parameters = new HashMap<>();
		parameters.put("foo", "bar");
		parameters.put("answer", null);
		parameters.put("boolean", Boolean.FALSE);
		final String input = "Hello, ${foo}, the expected result is ${boolean}, and the answer is: ${answer}.";
		try {
			StringFormatter.formatted(input, parameters::get, null);
			fail("Expected Exception");
		} catch (NullPointerException e) {
			assertEquals("Parameter is not defined, or null: answer", e.getMessage());
		}
	}

	/** Test referencing an invalid numbered parameter.
	 */
	@Test
	void testNullNumberedParameter() {
		final List<Object> list = new ArrayList<>();
		list.add("bar");
		list.add(null);
		list.add(Boolean.FALSE);
		final String input = "Hello, ${0}, the expected result is ${2}, and the answer is: ${1}.";
		try {
			StringFormatter.formatted(input, null, list::get);
			fail("Expected Exception");
		} catch (NullPointerException e) {
			assertEquals("Parameter is not defined, or null: 1", e.getMessage());
		}
	}

	/** Test referencing an invalid numbered parameter.
	 */
	@Test
	void testMissingNumberedParameter() {
		final List<Object> list = new ArrayList<>();
		list.add("bar");
		list.add(Boolean.FALSE);
		final String input = "Hello, ${0}, the expected result is ${1}, and the answer is: ${2}.";
		try {
			StringFormatter.formatted(input, null, (i) -> i < list.size() ? list.get(i) : null);
			fail("Expected Exception");
		} catch (NullPointerException e) {
			assertEquals("Parameter is not defined, or null: 2", e.getMessage());
		}
	}

	/** Test referencing a named parameter, if only numbered parameters are available.
	 */
	@Test
	void testNumberedParametersOnlyContext() {
		final List<Object> list = new ArrayList<>();
		list.add("bar");
		list.add(Boolean.FALSE);
		final String input = "Hello, ${foo}, the expected result is ${1}, and the answer is: ${0}.";
		try {
			StringFormatter.formatted(input, null, list::get);
			fail("Expected Exception");
		} catch (IllegalStateException e) {
			assertEquals("Named parameters are not available in this context.", e.getMessage());
		}
	}

	/** Test referencing a named parameter, if only numbered parameters are available.
	 */
	@Test
	void testNamedParametersOnlyContext() {
		final Map<String,Object> parameters = new HashMap<>();
		parameters.put("foo", "bar");
		parameters.put("answer", Integer.valueOf(42));
		parameters.put("boolean", Boolean.FALSE);
		final String input = "Hello, ${0}, the expected result is ${boolean}, and the answer is: ${answer}.";
		try {
			StringFormatter.formatted(input, parameters::get, null);
			fail("Expected Exception");
		} catch (IllegalStateException e) {
			assertEquals("Numbered parameters are not available in this context.", e.getMessage());
		}
	}

	/** Test formatting a string without parameter references.
	 */
	@Test
	void testNoParameterReferences() {
		assertEquals("string", StringFormatter.formatted("string", null, null));
	}

	/** Test an unterminated parameter reference.
	 */
	@Test
	void testUnterminatedParameterReference() {
		try {
			StringFormatter.formatted("Hello, ${name", null, null);
			fail("Expected Exception");
		} catch (IllegalArgumentException e) {
			assertEquals("Unterminated parameter reference in formatter input: Hello, ${name", e.getMessage());
		}
	}
	/** Not actual testing, just to complete coverage.	 * 
	 */
	@Test
	void testCoverage() {
		final StringFormatter sf = StringFormatter.getInstance();
		assertEquals("${", sf.getPrefix());
		assertEquals("}", sf.getSuffix());
	}
}
