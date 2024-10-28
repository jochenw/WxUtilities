package com.github.jochenw.wxutil.isbt.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;


class WmNodeParserTest {
	protected void assertNullValue(Map<String,Object> pMap, String pKey) {
		assertNotNull(pMap);
		assertNotNull(pKey);
		assertTrue(pMap.containsKey(pKey));
		assertNull(pMap.get(pKey));
	}
	protected void assertKVPair(Object pKvPair, String pKey, String pValue) {
		assertNotNull(pKvPair);
		@SuppressWarnings("unchecked")
		final Map<String,Object> map = (Map<String,Object>) pKvPair;
		assertEquals(pKey, map.get("key"));
		assertEquals(pValue, map.get("value"));
	}
	@Test
	void testParseManifest() {
		final Path manifestFile = Paths.get("src/test/resources/com/github/jochenw/wxutil/isbt/impl/manifest.v3");
		assertTrue(Files.isRegularFile(manifestFile));
		final Map<String,Object> manifest = WmNodeParser.parse(manifestFile);
		assertEquals(14, manifest.size());
		assertEquals("yes", manifest.get("enabled"));
		assertEquals("no", manifest.get("system_package"));
		assertEquals("1.0", manifest.get("version"));
		@SuppressWarnings("unchecked")
		final Map<String,Object> startupServices = (Map<String,Object>) manifest.get("startup_services");
		assertNotNull(startupServices);
		assertEquals(1, startupServices.size());
		assertNullValue(startupServices, "wx.logNg.admin:startUp");
		@SuppressWarnings("unchecked")
		final Map<String,Object> shutdownServices = (Map<String,Object>) manifest.get("shutdown_services");
		assertNotNull(shutdownServices);
		assertEquals(1, shutdownServices.size());
		assertNullValue(shutdownServices, "wx.logNg.admin:shutDown");
		assertNullValue(manifest, "replication_services");
		assertNullValue(manifest, "requires");
		assertEquals("Default", manifest.get("listACL"));
		assertEquals("yes", manifest.get("webappLoad"));
		assertEquals("yes", manifest.get("reloadWithDependentPackage"));
		assertEquals("", manifest.get("build"));
		assertEquals("", manifest.get("description"));
		assertEquals("22-02-2024 13:28:27 CET", manifest.get("created_date"));
		@SuppressWarnings("unchecked")
		final List<Object> extProperties = (List<Object>) manifest.get("extendedProperties");
		assertNotNull(extProperties);
		assertEquals(4, extProperties.size());
		assertKVPair(extProperties.get(0), "displayName", "webMethods extended, Logging, Next Gen");
		assertKVPair(extProperties.get(1), "trusted", "no");
		assertKVPair(extProperties.get(2), "status", "Development");
		assertKVPair(extProperties.get(3), "tags", "");
		
	}

}
