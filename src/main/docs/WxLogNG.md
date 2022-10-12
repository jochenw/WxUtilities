# WxLogNg

Logging always has been, and always will be, an underrated topic. Nevertheless, it is an important issue in all applications. That includes, in particular, the webMethods IS.

As a consequence, one of the very first Wx packages, that have been created, and used.

However, since that time, there haven't really been any enhancements, or real new features. Instead, there have been discussions over the internal workings. In particular, there has been the discussion about the logging backend, that is being used internally.

The result have been iterations of the original WxLog, that were using [Logback](https://logback.qos.ch/), or []([Log4j 2](https://logging.apache.org/log4j/2.x/)) rather than [Log4j1]https://logging.apache.org/log4j/1.2)).

The authors opinion on that topic is: The discussion of the backend is completely irrelevant, and pointless. From the point of the user (in the context of logging, that would actually be the developer), there is no real difference, apart from the necessity to learn about different flavours of config files, which is actually the only source of trouble in conjunction with *WxLog*, *WxLog2*, that the author is aware of.

In summary: Logging within IS is at a standstill since a number of years.

WxLogNg intends to change this by introducing a number of really new features:

1. So far, the logger configuration is typically delivered with the respective IS package, for example as a file *config/log4.xml*. In other words, it is effectively hard coded into the package. To change details of the configuration, a redeployment is necessary. In practice, a reconfiguration is <u>never</u> done by administrators. Instead, this becomes a development task. In WxLogNg, reconfiguration can be done very easily, overwriting the factory settings, that developers have supplied. Such reconfiguration even survives redeployment.

2. The whole point of a logging system is **not** the creation of log files. (That's something, that the predecessors are doing a decent job with.)
   
   Instead, the important part is the ability to <u>retrieve</u>, and <u>read</u> these log files. WxLogNg includes a small web UI, that allows to
   
   - Download log files from the remote server for local introspection, and/or
   - Display log files in the browser as structured information, with the abiliy to apply suitable filters (for example, time and date of the logging event, the log level, or criteria like package name, and service name.).
   - Reconfigure the logging system on-the-fly. For example, the UI allows to change a loggers level.

3. Lots of time has been spent by considerations about the logging systems runtime performance. But there's another issue, that has never been addressed: The performance of the developer (user) when creating logging statements. Let's take a single example:
   Suggest, that a REST service wishes to log the headers of the incoming request. To do so, the service needs to invoke *pub.flow:getTransportInfo*. The result will be a document of type *pub.flow:transportInfo*.
   Now, with the predecessors, in order to log the headers, the service needs to iterate over the key/value pairs in the subdocument *http.requestHdrs*. With WxLogNg, logging the headers will be as simple as invoking a log service (let's say *wx.log.ng.pub.msg:logMsgInfo* with a format string like

```
Incoming headers: {headers}
```

        , and assigning the same subdocument to the parameter *msg.namedParameters.headers*.
        
        Guess, which version takes more time? (Not to mention the question of debugging the logging code.)

4. In logging, there is generally a distinction between standard logging, and audit logging, where the latter has additional requirements. WxLogNg doesn't necessarily meet all those requirements, but, at least, it does give it a try. (More on audit logging [below](#audit-logging).

5. WxLogNg provides the ability to introduce additional logging destinations. The main purpose here is to satisfy modern logging management destinations, like [Splunk](https://www.splunk.com/), [Dynatrace](https://www.dynatrace.com/), [Prometheus](https://prometheus.io/), or a simple database. (The latter was developed, in particular, with audit logging in mind.)



In practice, these features will provide very **real value** to both developers, and administrators.
