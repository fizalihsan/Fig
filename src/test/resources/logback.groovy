import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
//statusListener(OnConsoleStatusListener)
def logPattern = {
    pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS}  %-5level [%thread] %logger{36} - %msg%n"
}

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder, logPattern)
}

//Setting the log level of the root, and adding appenders
root(INFO, ["CONSOLE"])