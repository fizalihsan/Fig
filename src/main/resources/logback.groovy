import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender

def logPattern = {
    pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS}  %-5level [%thread] %logger{36} - %msg%n"
}

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder, logPattern)
}

appender("FILE", FileAppender) {
    file = "dependencyEngine.log"
    append = true
    encoder(PatternLayoutEncoder, logPattern)
}

//Setting the log level of the root, and adding appenders
root(DEBUG, ["CONSOLE", "FILE"])