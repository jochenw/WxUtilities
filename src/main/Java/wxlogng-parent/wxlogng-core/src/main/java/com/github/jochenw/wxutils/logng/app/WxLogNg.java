package com.github.jochenw.wxutils.logng.app;

import java.util.function.Supplier;

import com.github.jochenw.afw.core.util.Objects;
import com.github.jochenw.afw.di.api.Application;
import com.github.jochenw.afw.di.api.Module;
import com.github.jochenw.afw.di.api.Scopes;
import com.github.jochenw.wxutils.logng.impl.DefaultGenFileNameDerivator;
import com.github.jochenw.wxutils.logng.impl.DefaultLoggerRegistry;
import com.github.jochenw.wxutils.logng.impl.IGenFileNameDerivator;
import com.github.jochenw.wxutils.logng.is.DefaultIsFacade;
import com.github.jochenw.wxutils.logng.svc.FailLogSvc;
import com.github.jochenw.wxutils.logng.svc.IIsSvc;
import com.github.jochenw.wxutils.logng.svc.ParmsLogSvc;
import com.github.jochenw.wxutils.logng.svc.PlainLogEnabledSvc;
import com.github.jochenw.wxutils.logng.svc.PlainLogSvc;
import com.github.jochenw.wxutis.logng.api.ILoggerRegistry;

public class WxLogNg extends Application {
	private static WxLogNg THE_INSTANCE;
	
	public WxLogNg(Supplier<Module> pModuleSupplier) {
		super(getModuleSupplier(pModuleSupplier));
	}

	private static Supplier<Module> getModuleSupplier(Supplier<Module> pModuleSupplier) {
		return () -> Objects.notNull(pModuleSupplier.get(), MODULE);
	}

	public static void setInstance(WxLogNg pInstance) {
		synchronized(WxLogNg.class) {
			THE_INSTANCE = pInstance;
		}
	}

	public static WxLogNg getInstance() {
		synchronized(WxLogNg.class) {
			return THE_INSTANCE;
		}
	}

	public static WxLogNg getInstance(Module pModule) {
		synchronized(WxLogNg.class) {
			if (THE_INSTANCE == null) {
				THE_INSTANCE = Application.of(WxLogNg.class, pModule);
			}
			return THE_INSTANCE;
		}
	}

	public static Module MODULE = (b) -> {
		b.bind(ILoggerRegistry.class).to(DefaultLoggerRegistry.class).in(Scopes.SINGLETON);
		b.bind(IIsSvc.class, PlainLogSvc.class.getName()).to(PlainLogSvc.class).in(Scopes.SINGLETON);
		b.bind(IIsSvc.class, PlainLogEnabledSvc.class.getName()).to(PlainLogEnabledSvc.class).in(Scopes.SINGLETON);
		b.bind(IIsSvc.class, ParmsLogSvc.class.getName()).to(ParmsLogSvc.class).in(Scopes.SINGLETON);
		b.bind(IIsSvc.class, FailLogSvc.class.getName()).to(FailLogSvc.class).in(Scopes.SINGLETON);
		b.bind(IIsFacade.class).to(DefaultIsFacade.class).in(Scopes.SINGLETON);
		b.bind(IGenFileNameDerivator.class).to(DefaultGenFileNameDerivator.class).in(Scopes.SINGLETON);
	};
}
