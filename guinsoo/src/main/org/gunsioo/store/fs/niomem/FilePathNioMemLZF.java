/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.store.fs.niomem;

/**
 * A memory file system that compresses blocks to conserve memory.
 */
public class FilePathNioMemLZF extends FilePathNioMem {

    @Override
    boolean compressed() {
        return true;
    }

    @Override
    public FilePathNioMem getPath(String path) {
        if (!path.startsWith(getScheme())) {
            throw new IllegalArgumentException(path +
                    " doesn't start with " + getScheme());
        }
        int idx1 = path.indexOf(':');
        int idx2 = path.lastIndexOf(':');
        final FilePathNioMemLZF p = new FilePathNioMemLZF();
        if (idx1 != -1 && idx1 != idx2) {
            p.compressLaterCachePercent = Float.parseFloat(path.substring(idx1 + 1, idx2));
        }
        p.name = getCanonicalPath(path);
        return p;
    }

    @Override
    protected boolean isRoot() {
        return name.lastIndexOf(':') == name.length() - 1;
    }

    @Override
    public String getScheme() {
        return "nioMemLZF";
    }

}