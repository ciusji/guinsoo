/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.dml;

import java.text.Collator;

import org.guinsoo.security.auth.AuthenticatorFactory;
import org.guinsoo.api.ErrorCode;
import org.guinsoo.command.CommandInterface;
import org.guinsoo.command.Parser;
import org.guinsoo.command.Prepared;
import org.guinsoo.compress.Compressor;
import org.guinsoo.engine.Constants;
import org.guinsoo.engine.Database;
import org.guinsoo.engine.Mode;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.engine.Setting;
import org.guinsoo.expression.Expression;
import org.guinsoo.expression.TimeZoneOperation;
import org.guinsoo.expression.ValueExpression;
import org.guinsoo.message.DbException;
import org.guinsoo.message.Trace;
import org.guinsoo.mode.DefaultNullOrdering;
import org.guinsoo.pagestore.PageStore;
import org.guinsoo.pagestore.db.SessionPageStore;
import org.guinsoo.result.ResultInterface;
import org.guinsoo.schema.Schema;
import org.guinsoo.table.Table;
import org.guinsoo.tools.CompressTool;
import org.guinsoo.util.DateTimeUtils;
import org.guinsoo.util.StringUtils;
import org.guinsoo.util.TimeZoneProvider;
import org.guinsoo.value.CompareMode;
import org.guinsoo.value.DataType;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueInteger;
import org.guinsoo.value.ValueNull;

/**
 * This class represents the statement
 * SET
 */
public class Set extends Prepared {

    private final int type;
    private Expression expression;
    private String stringValue;
    private String[] stringValueList;

    public Set(SessionLocal session, int type) {
        super(session);
        this.type = type;
    }

    public void setString(String v) {
        this.stringValue = v;
    }

    @Override
    public boolean isTransactional() {
        switch (type) {
        case SetTypes.CLUSTER:
        case SetTypes.VARIABLE:
        case SetTypes.QUERY_TIMEOUT:
        case SetTypes.LOCK_TIMEOUT:
        case SetTypes.TRACE_LEVEL_SYSTEM_OUT:
        case SetTypes.TRACE_LEVEL_FILE:
        case SetTypes.THROTTLE:
        case SetTypes.SCHEMA:
        case SetTypes.SCHEMA_SEARCH_PATH:
        case SetTypes.CATALOG:
        case SetTypes.RETENTION_TIME:
        case SetTypes.LAZY_QUERY_EXECUTION:
        case SetTypes.NON_KEYWORDS:
        case SetTypes.TIME_ZONE:
        case SetTypes.VARIABLE_BINARY:
        case SetTypes.TRUNCATE_LARGE_LENGTH:
        case SetTypes.WRITE_DELAY:
            return true;
        default:
        }
        return false;
    }

    @Override
    public long update() {
        Database database = session.getDatabase();
        String name = SetTypes.getTypeName(type);
        switch (type) {
        case SetTypes.ALLOW_LITERALS: {
            session.getUser().checkAdmin();
            int value = getIntValue();
            if (value < 0 || value > 2) {
                throw DbException.getInvalidValueException("ALLOW_LITERALS", value);
            }
            synchronized (database) {
                database.setAllowLiterals(value);
                addOrUpdateSetting(name, null, value);
            }
            break;
        }
        case SetTypes.CACHE_SIZE: {
            session.getUser().checkAdmin();
            int value = getIntValue();
            if (value < 0) {
                throw DbException.getInvalidValueException("CACHE_SIZE", value);
            }
            synchronized (database) {
                database.setCacheSize(value);
                addOrUpdateSetting(name, null, value);
            }
            break;
        }
        case SetTypes.CLUSTER: {
            if (Constants.CLUSTERING_ENABLED.equals(stringValue)) {
                // this value is used when connecting
                // ignore, as the cluster setting is checked later
                break;
            }
            String value = StringUtils.quoteStringSQL(stringValue);
            if (!value.equals(database.getCluster())) {
                if (!value.equals(Constants.CLUSTERING_DISABLED)) {
                    // anybody can disable the cluster
                    // (if he can't access a cluster node)
                    session.getUser().checkAdmin();
                }
                database.setCluster(value);
                // use the system session so that the current transaction
                // (if any) is not committed
                SessionLocal sysSession = database.getSystemSession();
                synchronized (sysSession) {
                    synchronized (database) {
                        addOrUpdateSetting(sysSession, name, value, 0);
                        sysSession.commit(true);
                    }
                }
            }
            break;
        }
        case SetTypes.COLLATION: {
            session.getUser().checkAdmin();
            CompareMode compareMode;
            StringBuilder buff = new StringBuilder(stringValue);
            if (stringValue.equals(CompareMode.OFF)) {
                compareMode = CompareMode.getInstance(null, 0);
            } else {
                int strength = getIntValue();
                buff.append(" STRENGTH ");
                if (strength == Collator.IDENTICAL) {
                    buff.append("IDENTICAL");
                } else if (strength == Collator.PRIMARY) {
                    buff.append("PRIMARY");
                } else if (strength == Collator.SECONDARY) {
                    buff.append("SECONDARY");
                } else if (strength == Collator.TERTIARY) {
                    buff.append("TERTIARY");
                }
                compareMode = CompareMode.getInstance(stringValue, strength);
            }
            synchronized (database) {
                CompareMode old = database.getCompareMode();
                if (old.equals(compareMode)) {
                    break;
                }
                Table table = database.getFirstUserTable();
                if (table != null) {
                    throw DbException.get(ErrorCode.COLLATION_CHANGE_WITH_DATA_TABLE_1, table.getTraceSQL());
                }
                addOrUpdateSetting(name, buff.toString(), 0);
                database.setCompareMode(compareMode);
            }
            break;
        }
        case SetTypes.COMPRESS_LOB: {
            session.getUser().checkAdmin();
            int algo = CompressTool.getCompressAlgorithm(stringValue);
            synchronized (database) {
                database.setLobCompressionAlgorithm(algo == Compressor.NO ? null : stringValue);
                addOrUpdateSetting(name, stringValue, 0);
            }
            break;
        }
        case SetTypes.CREATE_BUILD: {
            session.getUser().checkAdmin();
            if (database.isStarting()) {
                // just ignore the command if not starting
                // this avoids problems when running recovery scripts
                int value = getIntValue();
                database.setCreateBuild(value);
                synchronized (database) {
                    addOrUpdateSetting(name, null, value);
                }
            }
            break;
        }
        case SetTypes.DATABASE_EVENT_LISTENER: {
            session.getUser().checkAdmin();
            database.setEventListenerClass(stringValue);
            break;
        }
        case SetTypes.DB_CLOSE_DELAY: {
            session.getUser().checkAdmin();
            int value = getIntValue();
            if (value == -1) {
                // -1 is a special value for in-memory databases,
                // which means "keep the DB alive and use the same
                // DB for all connections"
            } else if (value < 0) {
                throw DbException.getInvalidValueException("DB_CLOSE_DELAY", value);
            }
            synchronized (database) {
                database.setCloseDelay(value);
                addOrUpdateSetting(name, null, value);
            }
            break;
        }
        case SetTypes.DEFAULT_LOCK_TIMEOUT: {
            session.getUser().checkAdmin();
            int value = getIntValue();
            if (value < 0) {
                throw DbException.getInvalidValueException("DEFAULT_LOCK_TIMEOUT", value);
            }
            synchronized (database) {
                addOrUpdateSetting(name, null, value);
            }
            break;
        }
        case SetTypes.DEFAULT_TABLE_TYPE: {
            session.getUser().checkAdmin();
            int value = getIntValue();
            synchronized (database) {
                database.setDefaultTableType(value);
                addOrUpdateSetting(name, null, value);
            }
            break;
        }
        case SetTypes.EXCLUSIVE: {
            session.getUser().checkAdmin();
            int value = getIntValue();
            switch (value) {
            case 0:
                if (!database.unsetExclusiveSession(session)) {
                    throw DbException.get(ErrorCode.DATABASE_IS_IN_EXCLUSIVE_MODE);
                }
                break;
            case 1:
                if (!database.setExclusiveSession(session, false)) {
                    throw DbException.get(ErrorCode.DATABASE_IS_IN_EXCLUSIVE_MODE);
                }
                break;
            case 2:
                if (!database.setExclusiveSession(session, true)) {
                    throw DbException.get(ErrorCode.DATABASE_IS_IN_EXCLUSIVE_MODE);
                }
                break;
            default:
                throw DbException.getInvalidValueException("EXCLUSIVE", value);
            }
            break;
        }
        case SetTypes.JAVA_OBJECT_SERIALIZER: {
            session.getUser().checkAdmin();
            synchronized (database) {
                Table table = database.getFirstUserTable();
                if (table != null) {
                    throw DbException.get(ErrorCode.JAVA_OBJECT_SERIALIZER_CHANGE_WITH_DATA_TABLE,
                            table.getTraceSQL());
                }
                database.setJavaObjectSerializerName(stringValue);
                addOrUpdateSetting(name, stringValue, 0);
            }
            break;
        }
        case SetTypes.IGNORECASE: {
            session.getUser().checkAdmin();
            int value = getIntValue();
            synchronized (database) {
                database.setIgnoreCase(value == 1);
                addOrUpdateSetting(name, null, value);
            }
            break;
        }
        case SetTypes.LOCK_MODE: {
            session.getUser().checkAdmin();
            int value = getIntValue();
            synchronized (database) {
                database.setLockMode(value);
                addOrUpdateSetting(name, null, value);
            }
            break;
        }
        case SetTypes.LOCK_TIMEOUT: {
            int value = getIntValue();
            if (value < 0) {
                throw DbException.getInvalidValueException("LOCK_TIMEOUT", value);
            }
            session.setLockTimeout(value);
            break;
        }
        case SetTypes.LOG: {
            int value = getIntValue();
            if (database.isMVStore()) {
                throw DbException.getUnsupportedException("MV_STORE=TRUE && LOG");
            }
            PageStore pageStore = database.getPageStore();
            if (pageStore != null && value != pageStore.getLogMode()) {
                session.getUser().checkAdmin();
                pageStore.setLogMode(value);
            }
            break;
        }
        case SetTypes.MAX_LENGTH_INPLACE_LOB: {
            session.getUser().checkAdmin();
            int value = getIntValue();
            if (value < 0) {
                throw DbException.getInvalidValueException("MAX_LENGTH_INPLACE_LOB", value);
            }
            synchronized (database) {
                database.setMaxLengthInplaceLob(value);
                addOrUpdateSetting(name, null, value);
            }
            break;
        }
        case SetTypes.MAX_LOG_SIZE: {
            session.getUser().checkAdmin();
            int value = getIntValue();
            if (value < 0) {
                throw DbException.getInvalidValueException("MAX_LOG_SIZE", value);
            }
            synchronized (database) {
                database.setMaxLogSize((long) value * (1024 * 1024));
                addOrUpdateSetting(name, null, value);
            }
            break;
        }
        case SetTypes.MAX_MEMORY_ROWS: {
            session.getUser().checkAdmin();
            int value = getIntValue();
            if (value < 0) {
                throw DbException.getInvalidValueException("MAX_MEMORY_ROWS", value);
            }
            synchronized (database) {
                database.setMaxMemoryRows(value);
                addOrUpdateSetting(name, null, value);
            }
            break;
        }
        case SetTypes.MAX_MEMORY_UNDO: {
            session.getUser().checkAdmin();
            int value = getIntValue();
            if (value < 0) {
                throw DbException.getInvalidValueException("MAX_MEMORY_UNDO", value);
            }
            synchronized (database) {
                database.setMaxMemoryUndo(value);
                addOrUpdateSetting(name, null, value);
            }
            break;
        }
        case SetTypes.MAX_OPERATION_MEMORY: {
            session.getUser().checkAdmin();
            int value = getIntValue();
            if (value < 0) {
                throw DbException.getInvalidValueException("MAX_OPERATION_MEMORY", value);
            }
            database.setMaxOperationMemory(value);
            break;
        }
        case SetTypes.MODE: {
            Mode mode = Mode.getInstance(stringValue);
            if (mode == null) {
                throw DbException.get(ErrorCode.UNKNOWN_MODE_1, stringValue);
            }
            if (database.getMode() != mode) {
                session.getUser().checkAdmin();
                database.setMode(mode);
            }
            break;
        }
        case SetTypes.OPTIMIZE_REUSE_RESULTS: {
            session.getUser().checkAdmin();
            database.setOptimizeReuseResults(getIntValue() != 0);
            break;
        }
        case SetTypes.QUERY_TIMEOUT: {
            int value = getIntValue();
            if (value < 0) {
                throw DbException.getInvalidValueException("QUERY_TIMEOUT", value);
            }
            session.setQueryTimeout(value);
            break;
        }
        case SetTypes.REDO_LOG_BINARY: {
            int value = getIntValue();
            if (session instanceof SessionPageStore) {
                ((SessionPageStore) session).setRedoLogBinary(value == 1);
            } else {
                DbException.getUnsupportedException("MV_STORE + SET REDO_LOG_BINARY");
            }
            break;
        }
        case SetTypes.REFERENTIAL_INTEGRITY: {
            session.getUser().checkAdmin();
            int value = getIntValue();
            if (value < 0 || value > 1) {
                throw DbException.getInvalidValueException("REFERENTIAL_INTEGRITY", value);
            }
            database.setReferentialIntegrity(value == 1);
            break;
        }
        case SetTypes.QUERY_STATISTICS: {
            session.getUser().checkAdmin();
            int value = getIntValue();
            if (value < 0 || value > 1) {
                throw DbException.getInvalidValueException("QUERY_STATISTICS", value);
            }
            database.setQueryStatistics(value == 1);
            break;
        }
        case SetTypes.QUERY_STATISTICS_MAX_ENTRIES: {
            session.getUser().checkAdmin();
            int value = getIntValue();
            if (value < 1) {
                throw DbException.getInvalidValueException("QUERY_STATISTICS_MAX_ENTRIES", value);
            }
            database.setQueryStatisticsMaxEntries(value);
            break;
        }
        case SetTypes.SCHEMA: {
            Schema schema = database.getSchema(expression.optimize(session).getValue(session).getString());
            session.setCurrentSchema(schema);
            break;
        }
        case SetTypes.SCHEMA_SEARCH_PATH: {
            session.setSchemaSearchPath(stringValueList);
            break;
        }
        case SetTypes.CATALOG: {
            String shortName = database.getShortName();
            String value = expression.optimize(session).getValue(session).getString();
            if (value == null || !database.equalsIdentifiers(shortName, value)
                    && !database.equalsIdentifiers(shortName, value.trim())) {
                throw DbException.get(ErrorCode.DATABASE_NOT_FOUND_1, stringValue);
            }
            break;
        }
        case SetTypes.TRACE_LEVEL_FILE:
            session.getUser().checkAdmin();
            if (getPersistedObjectId() == 0) {
                // don't set the property when opening the database
                // this is for compatibility with older versions, because
                // this setting was persistent
                database.getTraceSystem().setLevelFile(getIntValue());
            }
            break;
        case SetTypes.TRACE_LEVEL_SYSTEM_OUT:
            session.getUser().checkAdmin();
            if (getPersistedObjectId() == 0) {
                // don't set the property when opening the database
                // this is for compatibility with older versions, because
                // this setting was persistent
                database.getTraceSystem().setLevelSystemOut(getIntValue());
            }
            break;
        case SetTypes.TRACE_MAX_FILE_SIZE: {
            session.getUser().checkAdmin();
            int value = getIntValue();
            if (value < 0) {
                throw DbException.getInvalidValueException("TRACE_MAX_FILE_SIZE", value);
            }
            int size = value * (1024 * 1024);
            synchronized (database) {
                database.getTraceSystem().setMaxFileSize(size);
                addOrUpdateSetting(name, null, value);
            }
            break;
        }
        case SetTypes.THROTTLE: {
            int value = getIntValue();
            if (value < 0) {
                throw DbException.getInvalidValueException("THROTTLE", value);
            }
            session.setThrottle(value);
            break;
        }
        case SetTypes.UNDO_LOG: {
            int value = getIntValue();
            if (value < 0 || value > 1) {
                throw DbException.getInvalidValueException("UNDO_LOG", value);
            }
            session.setUndoLogEnabled(value == 1);
            break;
        }
        case SetTypes.VARIABLE: {
            Expression expr = expression.optimize(session);
            session.setVariable(stringValue, expr.getValue(session));
            break;
        }
        case SetTypes.WRITE_DELAY: {
            session.getUser().checkAdmin();
            int value = getIntValue();
            if (value < 0) {
                throw DbException.getInvalidValueException("WRITE_DELAY", value);
            }
            synchronized (database) {
                database.setWriteDelay(value);
                addOrUpdateSetting(name, null, value);
            }
            break;
        }
        case SetTypes.RETENTION_TIME: {
            session.getUser().checkAdmin();
            int value = getIntValue();
            if (value < 0) {
                throw DbException.getInvalidValueException("RETENTION_TIME", value);
            }
            synchronized (database) {
                database.setRetentionTime(value);
                addOrUpdateSetting(name, null, value);
            }
            break;
        }
        case SetTypes.FORCE_JOIN_ORDER: {
            int value = getIntValue();
            if (value != 0 && value != 1) {
                throw DbException.getInvalidValueException("FORCE_JOIN_ORDER",
                        value);
            }
            session.setForceJoinOrder(value == 1);
            break;
        }
        case SetTypes.LAZY_QUERY_EXECUTION: {
            int value = getIntValue();
            if (value != 0 && value != 1) {
                throw DbException.getInvalidValueException("LAZY_QUERY_EXECUTION",
                        value);
            }
            session.setLazyQueryExecution(value == 1);
            break;
        }
        case SetTypes.BUILTIN_ALIAS_OVERRIDE: {
            session.getUser().checkAdmin();
            int value = getIntValue();
            if (value != 0 && value != 1) {
                throw DbException.getInvalidValueException("BUILTIN_ALIAS_OVERRIDE",
                        value);
            }
            database.setAllowBuiltinAliasOverride(value == 1);
            break;
        }
        case SetTypes.AUTHENTICATOR: {
            session.getUser().checkAdmin();
            boolean value = expression.optimize(session).getBooleanValue(session);
            try {
                synchronized (database) {
                    if (value) {
                        database.setAuthenticator(AuthenticatorFactory.createAuthenticator());
                    } else {
                        database.setAuthenticator(null);
                    }
                    addOrUpdateSetting(name, value ? "TRUE" : "FALSE", 0);
                }
            } catch (Exception e) {
                // Errors during start are ignored to allow to open the database
                if (database.isStarting()) {
                    database.getTrace(Trace.DATABASE).error(e,
                            "{0}: failed to set authenticator during database start ", expression.toString());
                } else {
                    throw DbException.convert(e);
                }
            }
            break;
        }
        case SetTypes.IGNORE_CATALOGS: {
            session.getUser().checkAdmin();
            int value = getIntValue();
            synchronized (database) {
                database.setIgnoreCatalogs(value == 1);
                addOrUpdateSetting(name, null, value);
            }
            break;
        }
        case SetTypes.NON_KEYWORDS:
            session.setNonKeywords(Parser.parseNonKeywords(stringValueList));
            break;
        case SetTypes.TIME_ZONE:
            session.setTimeZone(expression == null ? DateTimeUtils.getTimeZone()
                    : parseTimeZone(expression.getValue(session)));
            break;
        case SetTypes.VARIABLE_BINARY:
            session.setVariableBinary(expression.getBooleanValue(session));
            break;
        case SetTypes.DEFAULT_NULL_ORDERING: {
            DefaultNullOrdering defaultNullOrdering;
            try {
                defaultNullOrdering = DefaultNullOrdering.valueOf(StringUtils.toUpperEnglish(stringValue));
            } catch (RuntimeException e) {
                throw DbException.getInvalidValueException("DEFAULT_NULL_ORDERING", stringValue);
            }
            if (database.getDefaultNullOrdering() != defaultNullOrdering) {
                session.getUser().checkAdmin();
                database.setDefaultNullOrdering(defaultNullOrdering);
            }
            break;
        }
        case SetTypes.TRUNCATE_LARGE_LENGTH:
            session.setTruncateLargeLength(expression.getBooleanValue(session));
            break;
        default:
            throw DbException.getInternalError("type="+type);
        }
        // the meta data information has changed
        database.getNextModificationDataId();
        // query caches might be affected as well, for example
        // when changing the compatibility mode
        database.getNextModificationMetaId();
        return 0;
    }

    private static TimeZoneProvider parseTimeZone(Value v) {
        if (DataType.isCharacterStringType(v.getValueType())) {
            TimeZoneProvider timeZone;
            try {
                timeZone = TimeZoneProvider.ofId(v.getString());
            } catch (IllegalArgumentException ex) {
                throw DbException.getInvalidValueException("time zone", v.getTraceSQL());
            }
            return timeZone;
        } else if (v == ValueNull.INSTANCE) {
            throw DbException.getInvalidValueException("TIME ZONE", v);
        }
        return TimeZoneProvider.ofOffset(TimeZoneOperation.parseInterval(v));
    }

    private int getIntValue() {
        expression = expression.optimize(session);
        return expression.getValue(session).getInt();
    }

    public void setInt(int value) {
        this.expression = ValueExpression.get(ValueInteger.get(value));
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    private void addOrUpdateSetting(String name, String s, int v) {
        addOrUpdateSetting(session, name, s, v);
    }

    private void addOrUpdateSetting(SessionLocal session, String name, String s, int v) {
        Database database = session.getDatabase();
        assert Thread.holdsLock(database);
        if (database.isReadOnly()) {
            return;
        }
        Setting setting = database.findSetting(name);
        boolean addNew = false;
        if (setting == null) {
            addNew = true;
            int id = getObjectId();
            setting = new Setting(database, id, name);
        }
        if (s != null) {
            if (!addNew && setting.getStringValue().equals(s)) {
                return;
            }
            setting.setStringValue(s);
        } else {
            if (!addNew && setting.getIntValue() == v) {
                return;
            }
            setting.setIntValue(v);
        }
        if (addNew) {
            database.addDatabaseObject(session, setting);
        } else {
            database.updateMeta(session, setting);
        }
    }

    @Override
    public boolean needRecompile() {
        return false;
    }

    @Override
    public ResultInterface queryMeta() {
        return null;
    }

    public void setStringArray(String[] list) {
        this.stringValueList = list;
    }

    @Override
    public int getType() {
        return CommandInterface.SET;
    }

}
