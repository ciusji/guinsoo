/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This adapter sends log output to SLF4J. SLF4J supports multiple
 * implementations such as Logback, Log4j, Jakarta Commons Logging (JCL), JDK
 * 1.4 logging, x4juli, and Simple Log. To use SLF4J, you need to add the
 * required jar files to the classpath, and set the trace level to 4 when
 * opening a database:
 *
 * <pre>
 * jdbc:guinsoo:&tilde;/test;TRACE_LEVEL_FILE=4
 * </pre>
 *
 * The logger name is 'guinsoodatabase'.
 */
public class TraceWriterAdapter implements TraceWriter {

    private String name;
    private final Logger logger = LoggerFactory.getLogger("guinsoodatabase");

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isEnabled(int level) {
        switch (level) {
        case TraceSystem.DEBUG:
            return logger.isDebugEnabled();
        case TraceSystem.INFO:
            return logger.isInfoEnabled();
        case TraceSystem.ERROR:
            return logger.isErrorEnabled();
        default:
            return false;
        }
    }

    @Override
    public void write(int level, int moduleId, String s, Throwable t) {
        write(level, Trace.MODULE_NAMES[moduleId], s, t);
    }

    @Override
    public void write(int level, String module, String s, Throwable t) {
        if (isEnabled(level)) {
            if (name != null) {
                s = name + ":" + module + " " + s;
            } else {
                s = module + " " + s;
            }
            switch (level) {
            case TraceSystem.DEBUG:
                logger.debug(s, t);
                break;
            case TraceSystem.INFO:
                logger.info(s, t);
                break;
            case TraceSystem.ERROR:
                logger.error(s, t);
                break;
            default:
            }
        }
    }

}
