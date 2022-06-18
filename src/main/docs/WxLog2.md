# WxLog2 - The next generation of IS logging services

WxLog2 is an IS package, which provides a comprehensive suite of logging services.
As such, it acts as a successor to various packages, like th old WxLog (the original
package, based on Log4j), or the new WxLog (the successor, based on Logback).

WxLog2 exceeds it's predecessors by providing enhanced features:

1. An abstraction layer, which separates the logging services from the actual logging
   framework. For example, WxLog2 relieves the IS developer of the need to understand
   the syntax of framework specific configuration files, like log4j.xml, logback.xml,
   or whatever else. Instead the IS developer must understand WxLog2 (which he, or she,
   must do anyways), and that's it.
2. A set of wrapper services, that are more convenient to use, because they provide
   extended possibilities, like embedding of parameters into the log messages, or
   logging exceptions.
3. The ability to read log files remotely, searching for specific log events, or
   download log files to the developers local machine. These abilities are provided
   by a simple, yet powerful web UI.

## Services

In this section, we'll provide a complete list of all the services, that are available
to users of the WxLog2 package. These services are divided into a set of folders:

| Folder name | Full name         | Description |
|-------------|-------------------|-------------|
| plain       |wx.log2.pub.plain  | The most basic logging services. These are plain, and simple to learn, so they should be sufficient for general use. On the other hand, experienced users might prefer convenient wrappers, like the services from the "params" package. |
| params      |wx.log2.pub.params | A set of services, which provide the ability to embed parameters (run time values) into the log messages. |
| audit       |wx.log2.pub.audit  | A set of services, that are designed for audit logging. Log messages are not provided as free form strings, but by referencing a message id from a catalog of available messages. |

