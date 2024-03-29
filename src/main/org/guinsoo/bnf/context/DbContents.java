/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.bnf.context;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.guinsoo.engine.Session;
import org.guinsoo.jdbc.JdbcConnection;
import org.guinsoo.util.ParserUtil;
import org.guinsoo.util.StringUtils;
import org.guinsoo.util.Utils;

/**
 * Keeps meta data information about a database.
 * This class is used by the Guinsoo Console.
 */
public class DbContents {

    private DbSchema[] schemas;
    private DbSchema defaultSchema;
    private boolean isOracle;
    private boolean isGuinsoo;
    private boolean isPostgreSQL;
    private boolean isDerby;
    private boolean isSQLite;
    private boolean isMySQL;
    private boolean isFirebird;
    private boolean isMSSQLServer;
    private boolean isDB2;

    private boolean databaseToUpper, databaseToLower;

    private boolean mayHaveStandardViews = true;

    /**
     * @return the default schema.
     */
    public DbSchema getDefaultSchema() {
        return defaultSchema;
    }

    /**
     * @return true if this is an Apache Derby database.
     */
    public boolean isDerby() {
        return isDerby;
    }

    /**
     * @return true if this is a Firebird database.
     */
    public boolean isFirebird() {
        return isFirebird;
    }

    /**
     * @return true if this is a Guinsoo database.
     */
    public boolean isGuinsoo() {
        return isGuinsoo;
    }

    /**
     * @return true if this is a MS SQL Server database.
     */
    public boolean isMSSQLServer() {
        return isMSSQLServer;
    }

    /**
     * @return true if this is a MySQL database.
     */
    public boolean isMySQL() {
        return isMySQL;
    }

    /**
     * @return true if this is an Oracle database.
     */
    public boolean isOracle() {
        return isOracle;
    }

    /**
     * @return true if this is a PostgreSQL database.
     */
    public boolean isPostgreSQL() {
        return isPostgreSQL;
    }

    /**
     * @return true if this is an SQLite database.
     */
    public boolean isSQLite() {
        return isSQLite;
    }

    /**
     * @return true if this is an IBM DB2 database.
     */
    public boolean isDB2() {
        return isDB2;
    }

    /**
     * @return the list of schemas.
     */
    public DbSchema[] getSchemas() {
        return schemas;
    }

    /**
     * Returns whether standard INFORMATION_SCHEMA.VIEWS may be supported.
     *
     * @return whether standard INFORMATION_SCHEMA.VIEWS may be supported
     */
    public boolean mayHaveStandardViews() {
        return mayHaveStandardViews;
    }

    /**
     * @param mayHaveStandardViews
     *            whether standard INFORMATION_SCHEMA.VIEWS is detected as
     *            supported
     */
    public void setMayHaveStandardViews(boolean mayHaveStandardViews) {
        this.mayHaveStandardViews = mayHaveStandardViews;
    }

    /**
     * Read the contents of this database from the database meta data.
     *
     * @param url the database URL
     * @param conn the connection
     */
    public synchronized void readContents(String url, Connection conn)
            throws SQLException {
        isGuinsoo = url.startsWith("jdbc:guinsoo:");
        isDB2 = url.startsWith("jdbc:db2:");
        isSQLite = url.startsWith("jdbc:sqlite:");
        isOracle = url.startsWith("jdbc:oracle:");
        // the Vertica engine is based on PostgreSQL
        isPostgreSQL = url.startsWith("jdbc:postgresql:") || url.startsWith("jdbc:vertica:");
        // isHSQLDB = url.startsWith("jdbc:hsqldb:");
        isMySQL = url.startsWith("jdbc:mysql:");
        isDerby = url.startsWith("jdbc:derby:");
        isFirebird = url.startsWith("jdbc:firebirdsql:");
        isMSSQLServer = url.startsWith("jdbc:sqlserver:");
        if (isGuinsoo) {
            Session.StaticSettings settings = ((JdbcConnection) conn).getStaticSettings();
            databaseToUpper = settings.databaseToUpper;
            databaseToLower = settings.databaseToLower;
        }else if (isMySQL || isPostgreSQL) {
            databaseToUpper = false;
            databaseToLower = true;
        } else {
            databaseToUpper = true;
            databaseToLower = false;
        }
        DatabaseMetaData meta = conn.getMetaData();
        String defaultSchemaName = getDefaultSchemaName(meta);
        String[] schemaNames = getSchemaNames(meta);
        schemas = new DbSchema[schemaNames.length];
        for (int i = 0; i < schemaNames.length; i++) {
            String schemaName = schemaNames[i];
            boolean isDefault = defaultSchemaName == null ||
                    defaultSchemaName.equals(schemaName);
            DbSchema schema = new DbSchema(this, schemaName, isDefault);
            if (isDefault) {
                defaultSchema = schema;
            }
            schemas[i] = schema;
            String[] tableTypes = { "TABLE", "SYSTEM TABLE", "VIEW",
                    "SYSTEM VIEW", "TABLE LINK", "SYNONYM", "EXTERNAL" };
            schema.readTables(meta, tableTypes);
            if (!isPostgreSQL && !isDB2) {
                schema.readProcedures(meta);
            }
        }
        if (defaultSchema == null) {
            String best = null;
            for (DbSchema schema : schemas) {
                if ("dbo".equals(schema.name)) {
                    // MS SQL Server
                    defaultSchema = schema;
                    break;
                }
                if (defaultSchema == null ||
                        best == null ||
                        schema.name.length() < best.length()) {
                    best = schema.name;
                    defaultSchema = schema;
                }
            }
        }
    }

    private String[] getSchemaNames(DatabaseMetaData meta) throws SQLException {
        if (isMySQL || isSQLite) {
            return new String[] { "" };
        } else if (isFirebird) {
            return new String[] { null };
        }
        ResultSet rs = meta.getSchemas();
        ArrayList<String> schemaList = Utils.newSmallArrayList();
        while (rs.next()) {
            String schema = rs.getString("TABLE_SCHEM");
            String[] ignoreNames = null;
            if (isOracle) {
                ignoreNames = new String[] { "CTXSYS", "DIP", "DBSNMP",
                        "DMSYS", "EXFSYS", "FLOWS_020100", "FLOWS_FILES",
                        "MDDATA", "MDSYS", "MGMT_VIEW", "OLAPSYS", "ORDSYS",
                        "ORDPLUGINS", "OUTLN", "SI_INFORMTN_SCHEMA", "SYS",
                        "SYSMAN", "SYSTEM", "TSMSYS", "WMSYS", "XDB" };
            } else if (isMSSQLServer) {
                ignoreNames = new String[] { "sys", "db_accessadmin",
                        "db_backupoperator", "db_datareader", "db_datawriter",
                        "db_ddladmin", "db_denydatareader",
                        "db_denydatawriter", "db_owner", "db_securityadmin" };
            } else if (isDB2) {
                ignoreNames = new String[] { "NULLID", "SYSFUN",
                        "SYSIBMINTERNAL", "SYSIBMTS", "SYSPROC", "SYSPUBLIC",
                        // not empty, but not sure what they contain
                        "SYSCAT",  "SYSIBM", "SYSIBMADM",
                        "SYSSTAT", "SYSTOOLS",
                };

            }
            if (ignoreNames != null) {
                for (String ignore : ignoreNames) {
                    if (ignore.equals(schema)) {
                        schema = null;
                        break;
                    }
                }
            }
            if (schema == null) {
                continue;
            }
            schemaList.add(schema);
        }
        rs.close();
        return schemaList.toArray(new String[0]);
    }

    private String getDefaultSchemaName(DatabaseMetaData meta) {
        String defaultSchemaName = "";
        try {
            if (isGuinsoo) {
                return meta.storesLowerCaseIdentifiers() ? "public" : "PUBLIC";
            } else if (isOracle) {
                return meta.getUserName();
            } else if (isPostgreSQL) {
                return "public";
            } else if (isMySQL) {
                return "";
            } else if (isDerby) {
                return StringUtils.toUpperEnglish(meta.getUserName());
            } else if (isFirebird) {
                return null;
            }
        } catch (SQLException e) {
            // Ignore
        }
        return defaultSchemaName;
    }

    /**
     * Add double quotes around an identifier if required.
     *
     * @param identifier the identifier
     * @return the quoted identifier
     */
    public String quoteIdentifier(String identifier) {
        if (identifier == null) {
            return null;
        }
        if (ParserUtil.isSimpleIdentifier(identifier, databaseToUpper, databaseToLower)) {
            return identifier;
        }
        return StringUtils.quoteIdentifier(identifier);
    }

}
