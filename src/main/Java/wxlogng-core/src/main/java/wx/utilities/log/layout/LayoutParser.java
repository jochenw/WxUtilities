package wx.utilities.log.layout;


/** This class is parsing a layout specification string, like
 * <pre>
 *   "%dt %lv [%ti;%pi;%si;%mi] %ms%lt"
 * </pre>
 * and notifies a listener for every pattern, that it has detected.
 */
public class LayoutParser {
	public interface Listener {
		public void plainText(String pText);
		public void message();
		public void lineTerminator();
		public void loggerId();
		public void messageId();
		public void threadId();
		public void packageId();
		public void serviceId();
		public void serviceName();
		public void logLevel();
		public void lineTerminator(String pTerminator);
		public void dateTime(String pPattern);
		public default void dateTime() { dateTime(null); }
	}
}
