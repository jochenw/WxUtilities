package wx.logng.layout;

import java.time.format.DateTimeFormatter;

import com.github.jochenw.afw.core.util.NotImplementedException;


/** This factory class produces layouts, based on a specification pattern.
 * The specification pattern works roughly like a specification pattern
 * for the {@link DateTimeFormatter}. A typical specification pattern
 * would be "%dt %ll [%pi;%sq;%mi;%ti] %ms%lt".
 * Within the specification pattern, the following id's may be used:
 * <pre>
 *   Id                  Description
 *   ------------------  --------------
 *   %dt                 Date, and time of the log event. The extended
 *   %dt{<datePattern>}  form is used to specify a particular date/time
 *                       datePattern, which must be suitable for
 *                       DateTimeFormatter.ofPattern string. By default
 *                       (without an explicitly specified pattern),
 *                       the format of DateTimeFormatter.ISO_DATE_TIME
 *                       will be used.
 *   %ll                 The log events log level.
 *   %pi                 The package id (aka package name), that has
 *                       created the log event.
 *   %si                 The service id (unqualified service name,
 *                       without the folder part) of the service, that
 *                       has created the log event.
 *   %sq                 The qualified service id (qualified service name,
 *                       with the folder part), that has created the log
 *                       event.
 *   %ti                 The thread id (thread name, that has created the
 *                       log event.
 *   %mi                 The message id, if any, or null. In general, a
 *                       message id will only be present for audit
 *                       logging.
 *   %ms                 The actual message.
 *   %lt                 The line terminator. The extended form specifies
 *   %lt{\n}             a specific line terminator, the system default
 *                       will be used for the simple form.
 * </pre>
 * Internally, the layout is being created by using a {@link LayoutParser}.
 */
public class DefaultLayoutFactory implements ILayoutFactory {
	@Override
	public ILayout create(String pLayoutSpecification) {
		throw new NotImplementedException();
	}
}
