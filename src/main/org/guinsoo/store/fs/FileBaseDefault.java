/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.store.fs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Default implementation of the slow operations that need synchronization because they
 * involve the file position.
 */
public abstract class FileBaseDefault extends FileBase {

    private long position = 0;

    @Override
    public synchronized final long position() throws IOException {
        return position;
    }

    @Override
    public final synchronized FileChannel position(long newPosition) throws IOException {
        if (newPosition < 0) {
            throw new IllegalArgumentException();
        }
        position = newPosition;
        return this;
    }

    @Override
    public final synchronized int read(ByteBuffer dst) throws IOException {
        int read = read(dst, position);
        if (read > 0) {
            position += read;
        }
        return read;
    }

    @Override
    public final synchronized int write(ByteBuffer src) throws IOException {
        int written = write(src, position);
        if (written > 0) {
            position += written;
        }
        return written;
    }

    @Override
    public final synchronized FileChannel truncate(long newLength) throws IOException {
        implTruncate(newLength);
        if (newLength < position) {
            position = newLength;
        }
        return this;
    }

    /**
     * The truncate implementation.
     *
     * @param size the new size
     */
    protected abstract void implTruncate(long size) throws IOException;

}
