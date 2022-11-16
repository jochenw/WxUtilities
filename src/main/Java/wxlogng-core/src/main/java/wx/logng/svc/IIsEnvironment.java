package wx.logng.svc;

public interface IIsEnvironment {
	public Object getContext();
	public String getCallingPackageName(Object pCtx);
	public String getCallingQServiceId(Object pCtx);
	public String getCallingServiceId(Object pCtx);
}
