/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.store.fs.async;

import java.io.IOException;
import java.nio.channels.FileChannel;

import org.guinsoo.store.fs.FilePathWrapper;

/**
 * This file system stores files on disk and uses
 * java.nio.channels.AsynchronousFileChannel to access the files.
 */
public class FilePathAsync extends FilePathWrapper {

    @Override
    public FileChannel open(String mode) throws IOException {
        return new FileAsync(name.substring(getScheme().length() + 1), mode);
    }

    @Override
    public String getScheme() {
        return "async";
    }

}
