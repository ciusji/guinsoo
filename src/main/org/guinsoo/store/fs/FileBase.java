/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.store.fs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * The base class for file implementations.
 */
public abstract class FileBase extends FileChannel {

    @Override
    public synchronized int read(ByteBuffer dst, long position)
            throws IOException {
        long oldPos = position();
        position(position);
        int len = read(dst);
        position(oldPos);
        return len;
    }

    @Override
    public synchronized int write(ByteBuffer src, long position)
            throws IOException {
        long oldPos = position();
        position(position);
        int len = write(src);
        position(oldPos);
        return len;
    }

    @Override
    public void force(boolean metaData) throws IOException {
        // ignore
    }

    @Override
    protected void implCloseChannel() throws IOException {
        // ignore
    }

    @Override
    public FileLock lock(long position, long size, boolean shared)
            throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MappedByteBuffer map(MapMode mode, long position, long size)
            throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long read(ByteBuffer[] dsts, int offset, int length)
            throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long transferFrom(ReadableByteChannel src, long position, long count)
            throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long transferTo(long position, long count, WritableByteChannel target)
            throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileLock tryLock(long position, long size, boolean shared)
            throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long write(ByteBuffer[] srcs, int offset, int length)
            throws IOException {
        throw new UnsupportedOperationException();
    }

}
