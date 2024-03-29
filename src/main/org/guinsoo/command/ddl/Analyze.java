/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.ddl;

import org.guinsoo.command.CommandInterface;
import org.guinsoo.engine.Constants;
import org.guinsoo.engine.Database;
import org.guinsoo.engine.Right;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.index.Cursor;
import org.guinsoo.result.Row;
import org.guinsoo.schema.Schema;
import org.guinsoo.table.Column;
import org.guinsoo.table.Table;
import org.guinsoo.table.TableType;
import org.guinsoo.util.IntIntHashMap;
import org.guinsoo.value.DataType;
import org.guinsoo.value.Value;

/**
 * This class represents the statements
 * ANALYZE and ANALYZE TABLE
 *
 * <p>
 * Updates the selectivity statistics of tables. If no table name is given, all tables are analyzed.
 * The selectivity is used by the cost based optimizer to select the best index for a given query.
 *
 */
public class Analyze extends DefineCommand {

    private static final class SelectivityData {

        private long count, distinctCount;
        private final IntIntHashMap distinctHashes;

        SelectivityData() {
            distinctHashes = new IntIntHashMap(false);
        }

        void add(Value v) {
            count++;
            int size = distinctHashes.size();
            if (size >= Constants.SELECTIVITY_DISTINCT_COUNT) {
                distinctHashes.clear();
                distinctCount += size;
            }
            // the value -1 is not supported
            distinctHashes.put(v.hashCode(), 1);
        }

        int getSelectivity() {
            int s;
            if (count == 0) {
                s = 0;
            } else {
                s = (int) (100 * (distinctCount + distinctHashes.size()) / count);
                if (s <= 0) {
                    s = 1;
                }
            }
            return s;
        }

    }

    /**
     * The sample size.
     */
    private int sampleRows;
    /**
     * used in ANALYZE TABLE...
     */
    private Table table;

    public Analyze(SessionLocal session) {
        super(session);
        sampleRows = session.getDatabase().getSettings().analyzeSample;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    @Override
    public long update() {
        session.getUser().checkAdmin();
        Database db = session.getDatabase();
        if (table != null) {
            analyzeTable(session, table, sampleRows, true);
        } else {
            for (Schema schema : db.getAllSchemasNoMeta()) {
                for (Table table : schema.getAllTablesAndViews(null)) {
                    analyzeTable(session, table, sampleRows, true);
                }
            }
        }
        return 0;
    }

    /**
     * Analyze this table.
     *
     * @param session the session
     * @param table the table
     * @param sample the number of sample rows
     * @param manual whether the command was called by the user
     */
    public static void analyzeTable(SessionLocal session, Table table, int sample, boolean manual) {
        if (table.getTableType() != TableType.TABLE //
                || table.isHidden() //
                || session == null //
                || !manual && (session.getDatabase().isSysTableLocked() || table.hasSelectTrigger()) //
                || table.isTemporary() && !table.isGlobalTemporary() //
                        && session.findLocalTempTable(table.getName()) == null //
                || table.isLockedExclusively() && !table.isLockedExclusivelyBy(session)
                || !session.getUser().hasTableRight(table, Right.SELECT) //
                // if the connection is closed and there is something to undo
                || session.getCancel() != 0) {
            return;
        }
        table.lock(session, false, false);
        Column[] columns = table.getColumns();
        int columnCount = columns.length;
        if (columnCount == 0) {
            return;
        }
        Cursor cursor = table.getScanIndex(session).find(session, null, null);
        if (cursor.next()) {
            SelectivityData[] array = new SelectivityData[columnCount];
            for (int i = 0; i < columnCount; i++) {
                Column col = columns[i];
                if (!DataType.isLargeObject(col.getType().getValueType())) {
                    array[i] = new SelectivityData();
                }
            }
            int rowNumber = 0;
            do {
                Row row = cursor.get();
                for (int i = 0; i < columnCount; i++) {
                    SelectivityData selectivity = array[i];
                    if (selectivity != null) {
                        selectivity.add(row.getValue(i));
                    }
                }
            } while ((sample <= 0 || ++rowNumber < sample) && cursor.next());
            for (int i = 0; i < columnCount; i++) {
                SelectivityData selectivity = array[i];
                if (selectivity != null) {
                    columns[i].setSelectivity(selectivity.getSelectivity());
                }
            }
        } else {
            for (int i = 0; i < columnCount; i++) {
                columns[i].setSelectivity(0);
            }
        }
        session.getDatabase().updateMeta(session, table);
    }

    public void setTop(int top) {
        this.sampleRows = top;
    }

    @Override
    public int getType() {
        return CommandInterface.ANALYZE;
    }

}
