/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.store.fs.mem;

/**
 * A memory file system that compresses blocks to conserve memory.
 */
public class FilePathMemLZF extends FilePathMem {

    @Override
    public FilePathMem getPath(String path) {
        FilePathMemLZF p = new FilePathMemLZF();
        p.name = getCanonicalPath(path);
        return p;
    }

    @Override
    boolean compressed() {
        return true;
    }

    @Override
    public String getScheme() {
        return "memLZF";
    }

}