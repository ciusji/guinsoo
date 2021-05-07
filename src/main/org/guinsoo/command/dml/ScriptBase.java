/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.dml;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.guinsoo.pagestore.db.LobStorageBackend;
import org.guinsoo.api.ErrorCode;
import org.guinsoo.command.Prepared;
import org.guinsoo.engine.Constants;
import org.guinsoo.engine.Database;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.engine.SysProperties;
import org.guinsoo.expression.Expression;
import org.guinsoo.message.DbException;
import org.guinsoo.security.SHA256;
import org.guinsoo.store.DataHandler;
import org.guinsoo.store.FileStore;
import org.guinsoo.store.FileStoreInputStream;
import org.guinsoo.store.FileStoreOutputStream;
import org.guinsoo.store.fs.FileUtils;
import org.guinsoo.tools.CompressTool;
import org.guinsoo.util.IOUtils;
import org.guinsoo.util.SmallLRUCache;
import org.guinsoo.util.StringUtils;
import org.guinsoo.util.TempFileDeleter;
import org.guinsoo.value.CompareMode;

/**
 * This class is the base for RunScriptCommand and ScriptCommand.
 */
abstract class ScriptBase extends Prepared implements DataHandler {

    /**
     * The default name of the script file if .zip compression is used.
     */
    private static final String SCRIPT_SQL = "script.sql";

    /**
     * The output stream.
     */
    protected OutputStream out;

    /**
     * The input stream.
     */
    protected InputStream in;

    /**
     * The file name (if set).
     */
    private Expression fileNameExpr;

    private Expression password;

    private String fileName;

    private String cipher;
    private FileStore store;
    private String compressionAlgorithm;

    ScriptBase(SessionLocal session) {
        super(session);
    }

    public void setCipher(String c) {
        cipher = c;
    }

    private boolean isEncrypted() {
        return cipher != null;
    }

    public void setPassword(Expression password) {
        this.password = password;
    }

    public void setFileNameExpr(Expression file) {
        this.fileNameExpr = file;
    }

    protected String getFileName() {
        if (fileNameExpr != null && fileName == null) {
            fileName = fileNameExpr.optimize(session).getValue(session).getString();
            if (fileName == null || StringUtils.isWhitespaceOrEmpty(fileName)) {
                fileName = "script.sql";
            }
            fileName = SysProperties.getScriptDirectory() + fileName;
        }
        return fileName;
    }

    @Override
    public boolean isTransactional() {
        return false;
    }

    /**
     * Delete the target file.
     */
    void deleteStore() {
        String file = getFileName();
        if (file != null) {
            FileUtils.delete(file);
        }
    }

    private void initStore() {
        Database db = session.getDatabase();
        byte[] key = null;
        if (cipher != null && password != null) {
            char[] pass = password.optimize(session).
                    getValue(session).getString().toCharArray();
            key = SHA256.getKeyPasswordHash("script", pass);
        }
        String file = getFileName();
        store = FileStore.open(db, file, "rw", cipher, key);
        store.setCheckedWriting(false);
        store.init();
    }

    /**
     * Open the output stream.
     */
    void openOutput() {
        String file = getFileName();
        if (file == null) {
            return;
        }
        if (isEncrypted()) {
            initStore();
            out = new FileStoreOutputStream(store, this, compressionAlgorithm);
            // always use a big buffer, otherwise end-of-block is written a lot
            out = new BufferedOutputStream(out, Constants.IO_BUFFER_SIZE_COMPRESS);
        } else {
            OutputStream o;
            try {
                o = FileUtils.newOutputStream(file, false);
            } catch (IOException e) {
                throw DbException.convertIOException(e, null);
            }
            out = new BufferedOutputStream(o, Constants.IO_BUFFER_SIZE);
            out = CompressTool.wrapOutputStream(out, compressionAlgorithm, SCRIPT_SQL);
        }
    }

    /**
     * Open the input stream.
     */
    void openInput() {
        String file = getFileName();
        if (file == null) {
            return;
        }
        if (isEncrypted()) {
            initStore();
            in = new FileStoreInputStream(store, this, compressionAlgorithm != null, false);
        } else {
            InputStream inStream;
            try {
                inStream = FileUtils.newInputStream(file);
            } catch (IOException e) {
                throw DbException.convertIOException(e, file);
            }
            in = new BufferedInputStream(inStream, Constants.IO_BUFFER_SIZE);
            in = CompressTool.wrapInputStream(in, compressionAlgorithm, SCRIPT_SQL);
            if (in == null) {
                throw DbException.get(ErrorCode.FILE_NOT_FOUND_1, SCRIPT_SQL + " in " + file);
            }
        }
    }

    /**
     * Close input and output streams.
     */
    void closeIO() {
        IOUtils.closeSilently(out);
        out = null;
        IOUtils.closeSilently(in);
        in = null;
        if (store != null) {
            store.closeSilently();
            store = null;
        }
    }

    @Override
    public boolean needRecompile() {
        return false;
    }

    @Override
    public String getDatabasePath() {
        return null;
    }

    @Override
    public FileStore openFile(String name, String mode, boolean mustExist) {
        return null;
    }

    @Override
    public void checkPowerOff() {
        session.getDatabase().checkPowerOff();
    }

    @Override
    public void checkWritingAllowed() {
        session.getDatabase().checkWritingAllowed();
    }

    @Override
    public int getMaxLengthInplaceLob() {
        return session.getDatabase().getMaxLengthInplaceLob();
    }

    @Override
    public TempFileDeleter getTempFileDeleter() {
        return session.getDatabase().getTempFileDeleter();
    }

    @Override
    public String getLobCompressionAlgorithm(int type) {
        return session.getDatabase().getLobCompressionAlgorithm(type);
    }

    public void setCompressionAlgorithm(String algorithm) {
        this.compressionAlgorithm = algorithm;
    }

    @Override
    public Object getLobSyncObject() {
        return this;
    }

    @Override
    public SmallLRUCache<String, String[]> getLobFileListCache() {
        return null;
    }

    @Override
    public LobStorageBackend getLobStorage() {
        return null;
    }

    @Override
    public int readLob(long lobId, byte[] hmac, long offset, byte[] buff, int off, int length) {
        throw DbException.getInternalError();
    }

    @Override
    public CompareMode getCompareMode() {
        return session.getDataHandler().getCompareMode();
    }
}
