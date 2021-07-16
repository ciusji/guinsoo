/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.store.fs.retry;

import java.io.IOException;
import java.nio.channels.FileChannel;

import org.guinsoo.store.fs.FilePathWrapper;

/**
 * A file system that re-opens and re-tries the operation if the file was
 * closed, because a thread was interrupted. This will clear the interrupt flag.
 * It is mainly useful for applications that call Thread.interrupt by mistake.
 */
public class FilePathRetryOnInterrupt extends FilePathWrapper {

    /**
     * The prefix.
     */
    static final String SCHEME = "retry";

    @Override
    public FileChannel open(String mode) throws IOException {
        return new FileRetryOnInterrupt(name.substring(getScheme().length() + 1), mode);
    }

    @Override
    public String getScheme() {
        return SCHEME;
    }

}

