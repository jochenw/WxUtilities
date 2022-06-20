### The folder "plain"

This folder contains the most basic logging services. In particular, here you will find the service *wx.log2.pub.plain.logMessage*, which is **the** logging service in the sense, that all other logging services
are implemented by calling this service, directly, or indirectly.

When learning WxLog2, it is probably recommendable to begin with this package, as it is the most obvious,
and easy to understand. On the other hand, you will sooner or later find yourself dealing a lot with
cumbersome, and non-productive tasks, like concatenizing strings into a message. At that point, you
will be able to understand the reasons for packages, like "params", or "audit", with it's improved, and
more mature API, and you should be happy to find out, that there are easier solutions.

Here's an overview of the "plain" folder:

  - The quintessential logging service: [logMessage](#the-service-plain-logmessage)
  - A service for logging messages with level *trace*: [traceMessage](#the-service-plain-tracemessage)
  - A service for logging messages with level *debug*: [debugMessage](#the-service-plain-debugmessage)
  - A service for logging messages with level *info*: [infoMessage](#the-service-plain-infomessage)
  - A service for logging messages with level *warn*: [warnMessage](#the-service-plain-warnmessage)
  - A service for logging messages with level *error*: [errorMessage](#the-service-plain-errormessage)
  - A service for logging messages with level *fatal*: [fatalMessage](#the-service-plain-fatalmessage)

  
#### The service "plain.logMessage"

This is the most basic of all logging services. In the end, **all** other logging services are implemented by
calling this one. So, understanding this service is important: First, because all the input parameters,
will be introduced here. Once you understand the concepts, and parameters, though, you will find, that
you rarely use these services, because you will prefer the more convenient wrappers.

**Input Parameters**

There are a lot of input parameters here, but most are optional:

| Name       | Required / Default | Description                        |
|------------|--------------------|------------------------------------|
| message    | Yes.               | The message, that is being logged. |
| level      | No, default "info" | The log level, either of "trace", "debug", "info", "warn", "error", or "fatal". |
| loggerId   | Yes.               | An identifier for the log level, that is being used. A package can have multiple log files by using multiple logger id's. On the other hand, packages can share a log file by sharing a logger id. |
| packageId  | No, defaults to calling package | Name of the package, that is logging the message. |
| messageId  | Yes, for audit loggers only.    | If the message wasn't given by the caller, but read from a catalog file by supplying a message id, then this parameter will supply the id. Audit loggers use this to ensure, that only
predefined messages may be logged, as defined by the catalog. |
| serviceId  | No, defaults to calling service | Unqualified name (without the namespace) of the service, that is
logging the message. |
| qServiceId | No, defaults to calling service | Qualified name (With the namespace) of the service, that is
logging the message. |

**Output Parameters**

None

**Exceptions**

| Type                    | Decription |
|-------------------------|------------|
| NullPointerException    | Either of the required parameters (message, and loggerId) is null. |
| IllegalArgumentException| Either of the required parameters (message, and loggerId) is empty, or a parameter is otherwise invalid. |
| IOException             | Writing to a log file failed, and an I/O error occurred. |
| SQLException            | Writing to a database via JDBC failed, and an I/O error occurred. |

**Notes**

- This service will be rarely used directly. In general, using a wrapper service will be preferrable. The
  most commonly used, (because they are closest to this one), are [*traceMessage*](#the-service-plain-tracemessage],
  [*debugMessage*](#the-service-plain-debugmessage], [*infoMessage*](#the-service-plain-infomessage],
  [*warnMessage*](#the-service-plain-warnmessage], [*errorMessage*](#the-service-plain-errormessage], and
  [*fatalMessage*](#the-service-plain-fatalmessage], which are doing mostly the same, except that they provide
  an explicit value for the *level* parameter.

**See also**

- [*traceMessage*](#the-service-plain-tracemessage]
- [*debugMessage*](#the-service-plain-debugmessage]
- [*infoMessage*](#the-service-plain-infomessage]
- [*warnMessage*](#the-service-plain-warnmessage]
- [*errorMessage*](#the-service-plain-errormessage]
- [*fatalMessage*](#the-service-plain-fatalmessage]


### The service plain.traceMessage

This is a very simple wrapper for the service [logMessage](#the-service-plain-logmessage): IIt eliminates basically
all the optional parameters (taking the default values), except for the *level* parameter, which is being fixed to
*trace*. As a consequence, only the required parameters *message*, and *loggerId* remain, making this service
very convenient to use.

**Input Parameters**

| Name       | Required / Default | Description                        |
|------------|--------------------|------------------------------------|
| message    | Yes.               | The message, that is being logged. |
| loggerId   | Yes.               | An identifier for the log level, that is being used. A package can have multiple 

**Output Parameters**

None

**Exceptions**

| Type                    | Decription |
|-------------------------|------------|
| NullPointerException    | Either of the required parameters (message, and loggerId) is null. |
| IllegalArgumentException| Either of the required parameters (message, and loggerId) is empty, or a parameter is otherwise invalid. |
| IOException             | Writing to a log file failed, and an I/O error occurred. |
| SQLException            | Writing to a database via JDBC failed, and an I/O error occurred. |

**See also**

- [*logMessage*](#the-service-plain-logmessage]
- [*debugMessage*](#the-service-plain-debugmessage]
- [*infoMessage*](#the-service-plain-infomessage]
- [*warnMessage*](#the-service-plain-warnmessage]
- [*errorMessage*](#the-service-plain-errormessage]
- [*fatalMessage*](#the-service-plain-fatalmessage]


### The service plain.debugMessage

This is a very simple wrapper for the service [logMessage](#the-service-plain-logmessage): IIt eliminates basically
all the optional parameters (taking the default values), except for the *level* parameter, which is being fixed to
*debug*. As a consequence, only the required parameters *message*, and *loggerId* remain, making this service
very convenient to use.

**Input Parameters**

| Name       | Required / Default | Description                        |
|------------|--------------------|------------------------------------|
| message    | Yes.               | The message, that is being logged. |
| loggerId   | Yes.               | An identifier for the log level, that is being used. A package can have multiple 

**Output Parameters**

None

**Exceptions**

| Type                    | Decription |
|-------------------------|------------|
| NullPointerException    | Either of the required parameters (message, and loggerId) is null. |
| IllegalArgumentException| Either of the required parameters (message, and loggerId) is empty, or a parameter is otherwise invalid. |
| IOException             | Writing to a log file failed, and an I/O error occurred. |
| SQLException            | Writing to a database via JDBC failed, and an I/O error occurred. |

**See also**

- [*logMessage*](#the-service-plain-logmessage]
- [*traceMessage*](#the-service-plain-debugmessage]
- [*infoMessage*](#the-service-plain-infomessage]
- [*warnMessage*](#the-service-plain-warnmessage]
- [*errorMessage*](#the-service-plain-errormessage]
- [*fatalMessage*](#the-service-plain-fatalmessage]


### The service plain.infoMessage

This is a very simple wrapper for the service [logMessage](#the-service-plain-logmessage): IIt eliminates basically
all the optional parameters (taking the default values), except for the *level* parameter, which is being fixed to
*info*. As a consequence, only the required parameters *message*, and *loggerId* remain, making this service
very convenient to use.

**Input Parameters**

| Name       | Required / Default | Description                        |
|------------|--------------------|------------------------------------|
| message    | Yes.               | The message, that is being logged. |
| loggerId   | Yes.               | An identifier for the log level, that is being used. A package can have multiple 

**Output Parameters**

None

**Exceptions**

| Type                    | Decription |
|-------------------------|------------|
| NullPointerException    | Either of the required parameters (message, and loggerId) is null. |
| IllegalArgumentException| Either of the required parameters (message, and loggerId) is empty, or a parameter is otherwise invalid. |
| IOException             | Writing to a log file failed, and an I/O error occurred. |
| SQLException            | Writing to a database via JDBC failed, and an I/O error occurred. |

**See also**

- [*logMessage*](#the-service-plain-logmessage]
- [*traceMessage*](#the-service-plain-debugmessage]
- [*debugMessage*](#the-service-plain-infomessage]
- [*warnMessage*](#the-service-plain-warnmessage]
- [*errorMessage*](#the-service-plain-errormessage]
- [*fatalMessage*](#the-service-plain-fatalmessage]


### The service plain.warnMessage

This is a very simple wrapper for the service [logMessage](#the-service-plain-logmessage): IIt eliminates basically
all the optional parameters (taking the default values), except for the *level* parameter, which is being fixed to
*warn*. As a consequence, only the required parameters *message*, and *loggerId* remain, making this service
very convenient to use.

**Input Parameters**

| Name       | Required / Default | Description                        |
|------------|--------------------|------------------------------------|
| message    | Yes.               | The message, that is being logged. |
| loggerId   | Yes.               | An identifier for the log level, that is being used. A package can have multiple 

**Output Parameters**

None

**Exceptions**

| Type                    | Decription |
|-------------------------|------------|
| NullPointerException    | Either of the required parameters (message, and loggerId) is null. |
| IllegalArgumentException| Either of the required parameters (message, and loggerId) is empty, or a parameter is otherwise invalid. |
| IOException             | Writing to a log file failed, and an I/O error occurred. |
| SQLException            | Writing to a database via JDBC failed, and an I/O error occurred. |

**See also**

- [*logMessage*](#the-service-plain-logmessage]
- [*traceMessage*](#the-service-plain-debugmessage]
- [*debugMessage*](#the-service-plain-infomessage]
- [*infoMessage*](#the-service-plain-warnmessage]
- [*errorMessage*](#the-service-plain-errormessage]
- [*fatalMessage*](#the-service-plain-fatalmessage]


### The service plain.errorMessage

This is a very simple wrapper for the service [logMessage](#the-service-plain-logmessage): IIt eliminates basically
all the optional parameters (taking the default values), except for the *level* parameter, which is being fixed to
*warn*. As a consequence, only the required parameters *message*, and *loggerId* remain, making this service
very convenient to use.

**Input Parameters**

| Name       | Required / Default | Description                        |
|------------|--------------------|------------------------------------|
| message    | Yes.               | The message, that is being logged. |
| loggerId   | Yes.               | An identifier for the log level, that is being used. A package can have multiple 

**Output Parameters**

None

**Exceptions**

| Type                    | Decription |
|-------------------------|------------|
| NullPointerException    | Either of the required parameters (message, and loggerId) is null. |
| IllegalArgumentException| Either of the required parameters (message, and loggerId) is empty, or a parameter is otherwise invalid. |
| IOException             | Writing to a log file failed, and an I/O error occurred. |
| SQLException            | Writing to a database via JDBC failed, and an I/O error occurred. |

**See also**

- [*logMessage*](#the-service-plain-logmessage]
- [*traceMessage*](#the-service-plain-debugmessage]
- [*debugMessage*](#the-service-plain-infomessage]
- [*infoMessage*](#the-service-plain-warnmessage]
- [*warnMessage*](#the-service-plain-errormessage]
- [*fatalMessage*](#the-service-plain-fatalmessage]


### The service plain.fatalMessage

This is a very simple wrapper for the service [logMessage](#the-service-plain-logmessage): IIt eliminates basically
all the optional parameters (taking the default values), except for the *level* parameter, which is being fixed to
*fatal*. As a consequence, only the required parameters *message*, and *loggerId* remain, making this service
very convenient to use.

**Input Parameters**

| Name       | Required / Default | Description                        |
|------------|--------------------|------------------------------------|
| message    | Yes.               | The message, that is being logged. |
| loggerId   | Yes.               | An identifier for the log level, that is being used. A package can have multiple 

**Output Parameters**

None

**Exceptions**

| Type                    | Decription |
|-------------------------|------------|
| NullPointerException    | Either of the required parameters (message, and loggerId) is null. |
| IllegalArgumentException| Either of the required parameters (message, and loggerId) is empty, or a parameter is otherwise invalid. |
| IOException             | Writing to a log file failed, and an I/O error occurred. |
| SQLException            | Writing to a database via JDBC failed, and an I/O error occurred. |

**See also**

- [*logMessage*](#the-service-plain-logmessage]
- [*traceMessage*](#the-service-plain-debugmessage]
- [*debugMessage*](#the-service-plain-infomessage]
- [*infoMessage*](#the-service-plain-warnmessage]
- [*warnMessage*](#the-service-plain-errormessage]
- [*errorMessage*](#the-service-plain-fatalmessage]
