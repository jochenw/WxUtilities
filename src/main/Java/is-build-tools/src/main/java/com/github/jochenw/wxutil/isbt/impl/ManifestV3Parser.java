package com.github.jochenw.wxutil.isbt.impl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xml.sax.InputSource;

/** A parser for manifest.v3 files.
 */
public class ManifestV3Parser {
	public static class ManifestV3 {
		
	}

	protected List<String> asServiceList(Map<String,Object> pRecord, String pListName) {
		@SuppressWarnings("unchecked")
		final Map<String,Object> map = (Map<String,Object>) pRecord.get(pListName);
		final List<String> list = new ArrayList<String>();
		map.forEach((k,v) -> {
			if (v == null  ||  v instanceof String) {
				list.add(k);
			} else {
				throw new IllegalStateException("Unexpected value in service list for key " + k + ": " + pListName);
			}
		});
		list.sort(String::compareToIgnoreCase);
		return list;
	}

	public ManifestV3 parse(Map<String,Object> pValues) {
		final boolean enabled = "yes".equals(pValues.get("enabled"));
		final boolean systemPackage = "yes".equals(pValues.get("system_package"));
		final String version = (String) pValues.get("version");
		final List<String> startUpServices = asServiceList(pValues, "startup_services");
		final List<String> shutdownServices = asServiceList(pValues, "shutdown_services");
		final List<String> replicationServices = asServiceList(pValues, "replication_services");
		final List<String> requiredPackages = asPackageList(pValues, "requires");
	}
	
	public static ManifestV3 parse(InputSource pInputSource) {
		final Map<String,Object> manifestValues = WmNodeParser.parse(pInputSource);
		return new ManifestV3Parser().parse(manifestValues);
	}

	public static ManifestV3 parse(Path pFile) {
		final Map<String,Object> manifestValues = WmNodeParser.parse(pFile);
		return new ManifestV3Parser().parse(manifestValues);
	}
}
