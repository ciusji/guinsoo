/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.dml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.guinsoo.mvstore.db.Store;
import org.guinsoo.api.DatabaseEventListener;
import org.guinsoo.api.ErrorCode;
import org.guinsoo.command.CommandInterface;
import org.guinsoo.command.Prepared;
import org.guinsoo.engine.Constants;
import org.guinsoo.engine.Database;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.message.DbException;
import org.guinsoo.mvstore.MVStore;
import org.guinsoo.pagestore.PageStore;
import org.guinsoo.result.ResultInterface;
import org.guinsoo.store.FileLister;
import org.guinsoo.store.fs.FileUtils;
import org.guinsoo.util.IOUtils;

/**
 * This class represents the statement
 * BACKUP
 */
public class BackupCommand extends Prepared {

    private Expression fileNameExpr;

    public BackupCommand(SessionLocal session) {
        super(session);
    }

    public void setFileName(Expression fileName) {
        this.fileNameExpr = fileName;
    }

    @Override
    public long update() {
        String name = fileNameExpr.getValue(session).getString();
        session.getUser().checkAdmin();
        backupTo(name);
        return 0;
    }

    private void backupTo(String fileName) {
        Database db = session.getDatabase();
        if (!db.isPersistent()) {
            throw DbException.get(ErrorCode.DATABASE_IS_NOT_PERSISTENT);
        }
        try {
            Store store = db.getStore();
            if (store != null) {
                store.flush();
            }
            String name = db.getName();
            name = FileUtils.getName(name);
            try (OutputStream zip = FileUtils.newOutputStream(fileName, false)) {
                ZipOutputStream out = new ZipOutputStream(zip);
                db.flush();
                if (db.getPageStore() != null) {
                    String fn = db.getName() + Constants.SUFFIX_PAGE_FILE;
                    backupPageStore(out, fn, db.getPageStore());
                }
                // synchronize on the database, to avoid concurrent temp file
                // creation / deletion / backup
                String base = FileUtils.getParent(db.getName());
                synchronized (db.getLobSyncObject()) {
                    String prefix = db.getDatabasePath();
                    String dir = FileUtils.getParent(prefix);
                    dir = FileLister.getDir(dir);
                    ArrayList<String> fileList = FileLister.getDatabaseFiles(dir, name, true);
                    for (String n : fileList) {
                        if (n.endsWith(Constants.SUFFIX_MV_FILE) && store != null) {
                            MVStore s = store.getMvStore();
                            boolean before = s.getReuseSpace();
                            s.setReuseSpace(false);
                            try {
                                InputStream in = store.getInputStream();
                                backupFile(out, base, n, in);
                            } finally {
                                s.setReuseSpace(before);
                            }
                        }
                    }
                }
                out.close();
            }
        } catch (IOException e) {
            throw DbException.convertIOException(e, fileName);
        }
    }

    private void backupPageStore(ZipOutputStream out, String fileName,
            PageStore store) throws IOException {
        Database db = session.getDatabase();
        fileName = FileUtils.getName(fileName);
        out.putNextEntry(new ZipEntry(fileName));
        int pos = 0;
        try {
            store.setBackup(true);
            while (true) {
                pos = store.copyDirect(pos, out);
                if (pos < 0) {
                    break;
                }
                int max = store.getPageCount();
                db.setProgress(DatabaseEventListener.STATE_BACKUP_FILE, fileName, pos, max);
            }
        } finally {
            store.setBackup(false);
        }
        out.closeEntry();
    }

    private static void backupFile(ZipOutputStream out, String base, String fn,
            InputStream in) throws IOException {
        String f = FileUtils.toRealPath(fn);
        base = FileUtils.toRealPath(base);
        if (!f.startsWith(base)) {
            throw DbException.getInternalError(f + " does not start with " + base);
        }
        f = f.substring(base.length());
        f = correctFileName(f);
        out.putNextEntry(new ZipEntry(f));
        IOUtils.copyAndCloseInput(in, out);
        out.closeEntry();
    }

    @Override
    public boolean isTransactional() {
        return true;
    }

    /**
     * Fix the file name, replacing backslash with slash.
     *
     * @param f the file name
     * @return the corrected file name
     */
    public static String correctFileName(String f) {
        f = f.replace('\\', '/');
        if (f.startsWith("/")) {
            f = f.substring(1);
        }
        return f;
    }

    @Override
    public boolean needRecompile() {
        return false;
    }

    @Override
    public ResultInterface queryMeta() {
        return null;
    }

    @Override
    public int getType() {
        return CommandInterface.BACKUP;
    }

}
