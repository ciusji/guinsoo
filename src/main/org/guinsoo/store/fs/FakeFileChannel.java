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
 * Fake file channel to use by in-memory and ZIP file systems.
 */
public class FakeFileChannel extends FileChannel {

    /**
     * No need to allocate these, they have no state
     */
    public static final FakeFileChannel INSTANCE = new FakeFileChannel();

    private FakeFileChannel() {}

    @Override
    protected void implCloseChannel() throws IOException {
        throw new IOException();
    }

    @Override
    public FileLock lock(long position, long size, boolean shared) throws IOException {
        throw new IOException();
    }

    @Override
    public MappedByteBuffer map(MapMode mode, long position, long size) throws IOException {
        throw new IOException();
    }

    @Override
    public long position() throws IOException {
        throw new IOException();
    }

    @Override
    public FileChannel position(long newPosition) throws IOException {
        throw new IOException();
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        throw new IOException();
    }

    @Override
    public int read(ByteBuffer dst, long position) throws IOException {
        throw new IOException();
    }

    @Override
    public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
        throw new IOException();
    }

    @Override
    public long size() throws IOException {
        throw new IOException();
    }

    @Override
    public long transferFrom(ReadableByteChannel src, long position, long count) throws IOException {
        throw new IOException();
    }

    @Override
    public long transferTo(long position, long count, WritableByteChannel target) throws IOException {
        throw new IOException();
    }

    @Override
    public FileChannel truncate(long size) throws IOException {
        throw new IOException();
    }

    @Override
    public FileLock tryLock(long position, long size, boolean shared) throws IOException {
        throw new IOException();
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        throw new IOException();
    }

    @Override
    public int write(ByteBuffer src, long position) throws IOException {
        throw new IOException();
    }

    @Override
    public long write(ByteBuffer[] srcs, int offset, int len) throws IOException {
        throw new IOException();
    }

    @Override
    public void force(boolean metaData) throws IOException {
        throw new IOException();
    }
}