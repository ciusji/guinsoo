/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.store.fs.mem;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileLock;
import java.nio.channels.NonWritableChannelException;

import org.guinsoo.store.fs.FakeFileChannel;
import org.guinsoo.store.fs.FileBaseDefault;

/**
 * This class represents an in-memory file.
 */
class FileMem extends FileBaseDefault {

    /**
     * The file data.
     */
    final FileMemData data;

    private final boolean readOnly;
    private volatile boolean closed;

    FileMem(FileMemData data, boolean readOnly) {
        this.data = data;
        this.readOnly = readOnly;
    }

    @Override
    public long size() {
        return data.length();
    }

    @Override
    protected void implTruncate(long newLength) throws IOException {
        // compatibility with JDK FileChannel#truncate
        if (readOnly) {
            throw new NonWritableChannelException();
        }
        if (closed) {
            throw new ClosedChannelException();
        }
        if (newLength < size()) {
            data.touch(readOnly);
            data.truncate(newLength);
        }
    }

    @Override
    public int write(ByteBuffer src, long position) throws IOException {
        if (closed) {
            throw new ClosedChannelException();
        }
        if (readOnly) {
            throw new NonWritableChannelException();
        }
        int len = src.remaining();
        if (len == 0) {
            return 0;
        }
        data.touch(readOnly);
        data.readWrite(position, src.array(),
                src.arrayOffset() + src.position(), len, true);
        src.position(src.position() + len);
        return len;
    }

    @Override
    public int read(ByteBuffer dst, long position) throws IOException {
        if (closed) {
            throw new ClosedChannelException();
        }
        int len = dst.remaining();
        if (len == 0) {
            return 0;
        }
        long newPos = data.readWrite(position, dst.array(),
                dst.arrayOffset() + dst.position(), len, false);
        len = (int) (newPos - position);
        if (len <= 0) {
            return -1;
        }
        dst.position(dst.position() + len);
        return len;
    }

    @Override
    public void implCloseChannel() throws IOException {
        closed = true;
    }

    @Override
    public void force(boolean metaData) throws IOException {
        // do nothing
    }

    @Override
    public FileLock tryLock(long position, long size,
            boolean shared) throws IOException {
        if (closed) {
            throw new ClosedChannelException();
        }
        if (shared) {
            if (!data.lockShared()) {
                return null;
            }
        } else {
            if (!data.lockExclusive()) {
                return null;
            }
        }

        return new FileLock(FakeFileChannel.INSTANCE, position, size, shared) {

            @Override
            public boolean isValid() {
                return true;
            }

            @Override
            public void release() throws IOException {
                data.unlock();
            }
        };
    }

    @Override
    public String toString() {
        return closed ? "<closed>" : data.getName();
    }

}