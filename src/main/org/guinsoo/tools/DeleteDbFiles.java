/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.tools;

import java.sql.SQLException;
import java.util.ArrayList;

import org.guinsoo.engine.Constants;
import org.guinsoo.store.fs.FileUtils;
import org.guinsoo.store.FileLister;
import org.guinsoo.util.Tool;

/**
 * Deletes all files belonging to a database.
 * <br />
 * The database must be closed before calling this tool.
 * @guinsoo.resource
 */
public class DeleteDbFiles extends Tool {

    /**
     * Options are case sensitive. Supported options are:
     * <table>
     * <tr><td>[-help] or [-?]</td>
     * <td>Print the list of options</td></tr>
     * <tr><td>[-dir &lt;dir&gt;]</td>
     * <td>The directory (default: .)</td></tr>
     * <tr><td>[-db &lt;database&gt;]</td>
     * <td>The database name</td></tr>
     * <tr><td>[-quiet]</td>
     * <td>Do not print progress information</td></tr>
     * </table>
     * @guinsoo.resource
     *
     * @param args the command line arguments
     */
    public static void main(String... args) throws SQLException {
        new DeleteDbFiles().runTool(args);
    }

    @Override
    public void runTool(String... args) throws SQLException {
        String dir = ".";
        String db = null;
        boolean quiet = false;
        for (int i = 0; args != null && i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-dir")) {
                dir = args[++i];
            } else if (arg.equals("-db")) {
                db = args[++i];
            } else if (arg.equals("-quiet")) {
                quiet = true;
            } else if (arg.equals("-help") || arg.equals("-?")) {
                showUsage();
                return;
            } else {
                showUsageAndThrowUnsupportedOption(arg);
            }
        }
        process(dir, db, quiet);
    }

    /**
     * Deletes the database files.
     *
     * @param dir the directory
     * @param db the database name (null for all databases)
     * @param quiet don't print progress information
     */
    public static void execute(String dir, String db, boolean quiet) {
        new DeleteDbFiles().process(dir, db, quiet);
    }

    /**
     * Deletes the database files.
     *
     * @param dir the directory
     * @param db the database name (null for all databases)
     * @param quiet don't print progress information
     */
    private void process(String dir, String db, boolean quiet) {
        ArrayList<String> files = FileLister.getDatabaseFiles(dir, db, true);
        if (files.isEmpty() && !quiet) {
            printNoDatabaseFilesFound(dir, db);
        }
        for (String fileName : files) {
            process(fileName, quiet);
            if (!quiet) {
                out.println("Processed: " + fileName);
            }
        }
    }

    private static void process(String fileName, boolean quiet) {
        if (FileUtils.isDirectory(fileName)) {
            // only delete empty directories
            FileUtils.tryDelete(fileName);
        } else if (quiet || fileName.endsWith(Constants.SUFFIX_TEMP_FILE) ||
                fileName.endsWith(Constants.SUFFIX_TRACE_FILE)) {
            FileUtils.tryDelete(fileName);
        } else {
            FileUtils.delete(fileName);
        }
    }

}
