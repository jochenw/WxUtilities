package wx.logng.svc;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import javax.annotation.Nonnull;


public interface IIsEnvironment {
	public Object getContext();
	public String getCallingPackageName(Object pCtx);
	public String getCallingQServiceId(Object pCtx);
	public String getCallingServiceId(Object pCtx);
	public Supplier<InputStream> findFile(String pUri);
	public default @Nonnull Supplier<InputStream> requireFile(String pUri) {
		final Supplier<InputStream> supplier = findFile(pUri);
		if (supplier == null) {
			throw new NoSuchElementException("File not found: " + pUri);
		}
		return supplier;
	}
	public @Nonnull OutputStream createFile(String string);
}
