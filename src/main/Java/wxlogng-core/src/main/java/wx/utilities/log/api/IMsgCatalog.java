package wx.utilities.log.api;

import java.util.function.Consumer;

import wx.utilities.log.api.ILogger.Level;


public interface IMsgCatalog {
	public interface IMsg {
		public String getId();
		public String getMsgId();
		public Level getLevel();
	}
	public interface IMsgList {
		public String getId();
		public String getMsgListId();
		public IMsg getMessage(String pId);
		public void foreach(Consumer<IMsg> pConsumer);
	}
	public String getId();
	public String getCatalogId();
	public IMsgList getMessageList(String pId);
	public void foreach(Consumer<IMsgList> pConsumer);
}
