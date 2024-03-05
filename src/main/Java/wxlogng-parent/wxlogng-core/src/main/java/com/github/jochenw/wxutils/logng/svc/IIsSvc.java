package com.github.jochenw.wxutils.logng.svc;

import java.lang.reflect.Constructor;
import java.lang.reflect.UndeclaredThrowableException;
import java.time.ZonedDateTime;
import java.util.function.Consumer;

import com.github.jochenw.afw.core.util.Exceptions;
import com.github.jochenw.afw.di.api.IComponentFactory;
import com.github.jochenw.afw.di.api.IComponentFactoryAware;
import com.github.jochenw.wxutils.logng.app.IIsFacade;
import com.github.jochenw.wxutils.logng.app.WxLogNg;
import com.github.jochenw.wxutis.logng.api.ILogEvent;
import com.github.jochenw.wxutis.logng.api.ILoggerRegistry;
import com.github.jochenw.wxutis.logng.api.ILogEvent.Level;
import com.softwareag.util.IDataMap;
import com.wm.app.b2b.server.ServiceException;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;

public abstract class IIsSvc implements IComponentFactoryAware {
	private IComponentFactory componentFactory;
	private IIsFacade isFacade;

	public static Object[] NO_RESULT = null;
	
	@Override
	public void init(IComponentFactory pFactory) throws Exception {
		componentFactory = pFactory;
		isFacade = componentFactory.requireInstance(IIsFacade.class);
	}

	public IComponentFactory getComponentFactory() { return componentFactory; }
	public IIsFacade getIsFacade() { return isFacade; }

	public abstract Object[] run(IDataMap pInput) throws Exception;

	public void run(IData pPipeline) throws ServiceException {
		final IDataMap input = new IDataMap(pPipeline);
		final Object[] output;
		try {
			output = run(input);
		} catch (Exception e) {
			throw Exceptions.show(e, ServiceException.class);
		}
		applyOutput(pPipeline, output);
	}

	protected void applyOutput(IData pPipeline, Object[] pOutput) {
		if (pOutput != null) {
			final IDataCursor crsr = pPipeline.getCursor();
			for(int i = 0;  i < pOutput.length;  i += 2) {
				final Object keyObject = pOutput[i];
				final Object valueObject = pOutput[i+1];
				if (keyObject == null) {
					throw new NullPointerException("Output parameter key " + i + " is null.");
				}
				if (keyObject instanceof String) {
					final String key = (String) keyObject;
					IDataUtil.put(crsr, key, valueObject);
				} else {
					throw new IllegalStateException("Output parameter key " + i + " is not a string, but an instance of " + keyObject.getClass().getName());
				}
			}
			crsr.destroy();
		}
	}

	public static <O extends IIsSvc> void run(Class<O> pServiceType, IData pPipeline) throws ServiceException {
		getInstance(pServiceType).run(pPipeline);
	}

	public static <O extends IIsSvc> O getInstance(Class<O> pServiceType) {
		final IComponentFactory componentFactory = WxLogNg.getInstance().getComponentFactory();
		@SuppressWarnings("unchecked")
		final O svc = (O) componentFactory.getInstance(IIsSvc.class, pServiceType.getName());
		if (svc == null) {
			final Constructor<O> constructor;
			try {
				constructor = pServiceType.getConstructor();
			} catch (NoSuchMethodException|SecurityException e) {
				throw new UndeclaredThrowableException(e, "Unable to obtain a public default constructor for service class "
						+ pServiceType.getName() + ": " + e.getClass().getName() + ", " + e.getMessage());
			}
			final O service;
			try {
				service = constructor.newInstance();
			} catch (Exception e) {
				throw new UndeclaredThrowableException(e, "Unable to instantiate service class "
						+ pServiceType.getName() + ": " + e.getClass().getName() + ", " + e.getMessage());
			}
			componentFactory.init(service);
			return service;
		} else {
			return svc;
		}
	}

	protected void logWxLogMsg(Level pLevel, String pMsg) {
		final IIsFacade facade = getIsFacade();
		final ILogEvent evt = new ILogEvent() {
			@Override
			public String getSvcId() {
				return facade.getCurrentSvcId();
			}

			@Override
			public String getQSvcId() {
				return facade.getCurrentQSvcId();
			}

			@Override
			public String getPkgId() {
				return facade.getCurrentPkgId();
			}

			@Override
			public String getMsg() {
				return pMsg;
			}

			@Override
			public String getLoggerId() {
				return "WxLogNg";
			}

			@Override
			public Level getLevel() {
				return Level.info;
			}

			@Override
			public ZonedDateTime getDateTime() {
				return ZonedDateTime.now();
			}
		};
		getComponentFactory().requireInstance(ILoggerRegistry.class).log(evt);
	}
}
