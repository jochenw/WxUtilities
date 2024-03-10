package com.github.jochenw.wxutils.logng.svc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiConsumer;

import com.github.jochenw.afw.core.util.Objects;
import com.github.jochenw.afw.di.api.IComponentFactory;
import com.github.jochenw.wxutils.logng.app.IIsFacade;
import com.github.jochenw.wxutils.logng.fmt.LayoutParser;
import com.github.jochenw.wxutils.logng.fmt.LayoutParser.InvalidLayoutException;
import com.github.jochenw.wxutils.logng.fmt.LayoutParser.Listener;
import com.github.jochenw.wxutis.logng.api.ILogEngine;
import com.github.jochenw.wxutis.logng.api.ILogEvent.Level;
import com.softwareag.util.IDataMap;

public class PubUiEditPrefsSvc extends IIsSvc {
	private Map<String, ILogEngine<?>> engines;
	private IIsFacade isFacade;

	@Override
	public void init(IComponentFactory pComponentFactory) throws Exception {
		super.init(pComponentFactory);
		@SuppressWarnings("unchecked")
		final Map<String, ILogEngine<?>> map = (Map<String, ILogEngine<?>>)
				pComponentFactory.requireInstance(Map.class, ILogEngine.class.getName());
		engines = map;
		isFacade = pComponentFactory.requireInstance(IIsFacade.class);
	}

	@Override
	public Object[] run(IDataMap pInput) throws Exception {
		final boolean editing = pInput.getAsBoolean("edit", Boolean.FALSE).booleanValue();
		final List<String> updates = new ArrayList<>();
		final BiConsumer<String,String> updater = (k,v) -> {
			updates.add(k);
			updates.add(v);
		};
		String errorMsg = null;
		if (editing) {
			try {
				checkDefaultLogLevelUpdated(pInput, updater);
				checkDefaultMaxSizeUpdated(pInput, updater);
				checkDefaultMaxGenerationsUpdated(pInput, updater);
				checkDefaultEngineUpdated(pInput, updater);
				checkDefaultLayoutUpdated(pInput, updater);
				checkDefaultLogDirUpdated(pInput, updater);
			} catch (IllegalArgumentException e) {
				errorMsg = e.getMessage();
			}
		}
		Properties properties = getComponentFactory().requireInstance(Properties.class);
		return result("default.logLevel", properties.getProperty("default.logLevel"),
				      "default.maxSize", properties.getProperty("default.maxSize"),
				      "default.maxGenerations", properties.getProperty("default.maxGenerations"),
				      "default.engineId", properties.getProperty("default.engineId"),
				      "default.layout", properties.getProperty("default.layout"),
				      "default.logDir", properties.getProperty("default.logDir"),
					  "error", errorMsg,
					  "options.logLevel", getOptionsLogLevel(),
					  "options.engineId", getOptionsEngineId());
	}

	protected String[] getOptionsLogLevel() {
		final Level[] levels = Level.values();
		final String[] levelNames = new String[levels.length];
		for (int i = 0;  i < levelNames.length;  i++) {
			levelNames[i] = levels[i].name().toUpperCase();
		}
		return levelNames;
	}

	protected String[] getOptionsEngineId() {
		final List<String> engineIds = new ArrayList<>(engines.keySet());
		engineIds.sort(String::compareToIgnoreCase);
		return engineIds.toArray(new String[engineIds.size()]);
	}
	protected void checkDefaultLogDirUpdated(IDataMap pInput, final BiConsumer<String, String> pUpdater) {
		final String dirStr = pInput.getAsString("default.logDir");
		if (dirStr != null  &&  dirStr.length() > 0) {
			if (!isFacade.hasDir(dirStr)) {
				throw new IllegalArgumentException("Invalid value for parameter "
						+ "default.logdir: Expected existing directory, got "
						+ dirStr);
			}
			pUpdater.accept("default.logDir", dirStr);
		}
	}

	protected void checkDefaultLayoutUpdated(IDataMap pInput, final BiConsumer<String, String> pUpdater) {
		final String layoutStr = pInput.getAsString("default.layout");
		if (layoutStr != null  &&  layoutStr.length() > 0) {
			final Listener listener = new Listener() {
			};
			try {
				new LayoutParser().parse(layoutStr, listener, null);
			} catch (InvalidLayoutException e) {
				throw new IllegalArgumentException("Invalid value for parameter "
						+ "default.layout: " + e.getMessage());
			}
			pUpdater.accept("default.layout", layoutStr);
		}
	}

	protected void checkDefaultEngineUpdated(IDataMap pInput, final BiConsumer<String, String> pUpdater) {
		final String engineId = pInput.getAsString("default.engineId");
		if (engineId != null  &&  engineId.length() > 0) {
			if (!engines.containsKey(engineId.trim())) {
				throw new IllegalArgumentException("Invalid value for parameter "
						+ "default.engineId: Expected "
						+ String.join("|", engines.keySet()));
			}
			pUpdater.accept("default.engineId", engineId);
		}
	}

	protected void checkDefaultMaxGenerationsUpdated(IDataMap pInput, final BiConsumer<String, String> pUpdater) {
		final String maxGenerationsStr = pInput.getAsString("maxGenerations");
		if (maxGenerationsStr != null  &&  maxGenerationsStr.length() > 0) {
			final Integer maxGenerations;
			try {
				maxGenerations = Integer.valueOf(maxGenerationsStr.trim());
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid value for parameter "
						+ "default.maxGenerations: Expected integer value, got "
						+ maxGenerationsStr);
			}
			pUpdater.accept("default.maxGenerations", String.valueOf(maxGenerations));
		}
	}

	protected void checkDefaultMaxSizeUpdated(IDataMap pInput, final BiConsumer<String, String> pUpdater) {
		final String maxSizeStr = pInput.getAsString("default.maxSize");
		if (maxSizeStr != null  &&  maxSizeStr.length() > 0) {
			final Long maxSize;
			try {
				maxSize = Long.valueOf(maxSizeStr.trim());
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid value for parameter "
						+ "default.maxSize: Expected long integer value, got "
						+ maxSizeStr);
			}
			pUpdater.accept("default.maxSize", String.valueOf(maxSize));
		}
	}

	protected void checkDefaultLogLevelUpdated(IDataMap pInput, final BiConsumer<String, String> pUpdater) {
		final String levelStr = pInput.getAsString("default.logLevel");
		if (levelStr != null  &&  levelStr.length() > 0) {
			final Level level;
			try {
				level = Level.valueOf(levelStr.toLowerCase().trim());
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid value for parameter "
						+ "default.logLevel: Expected "
						+ Objects.enumNamesAsString(Level.class, "|")
						+ ", got " + levelStr);
			}
			pUpdater.accept("default.logLevel", level.name());
		}
	}

}
