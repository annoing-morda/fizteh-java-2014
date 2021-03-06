package ru.fizteh.fivt.students.dmitry_morozov.junit.interpreter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.dmitry_morozov.junit.DBCollection;
import ru.fizteh.fivt.students.dmitry_morozov.junit.TableWithTransactions;
import ru.fizteh.fivt.students.dmitry_morozov.junit.MyTableProviderFactory;

public class DBInterpreter {
    private DBCollection provider;
    private TableWithTransactions table;
    private HashMap<String, Integer> argsCount;
    private TreeSet<String> commandsForTable;

    public DBInterpreter(String path) {
        TableProviderFactory factory = new MyTableProviderFactory();
        try {
            provider = (DBCollection) factory.create(path);
            if (provider == null) {
                System.out.println("Usage: java -Dfizteh.db.dir=<path>");
                return;
            }
        } catch (IllegalArgumentException e) {
            System.out
                    .println("Usage: java -Ddb.file=<path>. Your path could have had incorrect value");
            throw new IllegalArgumentException();
        }
        table = null;
        argsCount = new HashMap<>();
        argsCount.put("use", 1);
        argsCount.put("create", 1);
        argsCount.put("drop", 1);
        argsCount.put("put", 2);
        argsCount.put("get", 1);
        argsCount.put("remove", 1);
        argsCount.put("list", 0);
        argsCount.put("size", 0);
        argsCount.put("rollback", 0);
        argsCount.put("commit", 0);
        argsCount.put("exit", 0);
        argsCount.put("show", 1);
        commandsForTable = new TreeSet<>();
        commandsForTable.add("put");
        commandsForTable.add("get");
        commandsForTable.add("remove");
        commandsForTable.add("list");
        commandsForTable.add("size");
        commandsForTable.add("rollback");
        commandsForTable.add("commit");

    }

    public HandlerReturn handle(String[] comAndParams, int bIndex, int eIndex)
            throws IOException {
        if (comAndParams.length < 1) {
            return new HandlerReturn(HandlerReturnResult.EXIT, "");
        }
        String command = comAndParams[bIndex].toLowerCase();
        Integer expectedArgsCount = argsCount.get(command);
        if (expectedArgsCount == null) {
            return new HandlerReturn(HandlerReturnResult.NO_SUCH_COMMAND,
                    command + "\n");
        } else {
            if (expectedArgsCount > eIndex - bIndex - 1) {
                return new HandlerReturn(
                        HandlerReturnResult.NOT_ENOUGH_PARAMETRES,
                        comAndParams[bIndex]);
            }
        }
        if (commandsForTable.contains(command) && table == null) {
            return new HandlerReturn(HandlerReturnResult.TABLE_NOT_CHOSEN, "");
        }
        switch (command) {
        case "put":
            return handlePut(comAndParams, bIndex + 1, eIndex);
        case "get":
            return handleGet(comAndParams, bIndex + 1, eIndex);
        case "remove":
            return handleRemove(comAndParams, bIndex + 1, eIndex);
        case "list":
            return handleList();
        case "size":
            return handleSize();
        case "rollback":
            return handleRollback(comAndParams, bIndex + 1, eIndex);
        case "commit":
            return handleCommit(comAndParams, bIndex + 1, eIndex);
        case "use":
            return handleUse(comAndParams, bIndex + 1, eIndex);
        case "create":
            return handleCreate(comAndParams, bIndex + 1, eIndex);
        case "drop":
            return handleDrop(comAndParams, bIndex + 1, eIndex);
        case "exit":
            return handleExit();
        case "show":
            return handleShowTables(comAndParams, bIndex + 1, eIndex);
        default:
            return new HandlerReturn(HandlerReturnResult.NO_SUCH_COMMAND,
                    command + "\n");
        }
    }

    public HandlerReturn handlePut(String[] comAndParams, int bIndex, int eIndex) {
        String res;
        try {
            res = table.put(comAndParams[bIndex], comAndParams[bIndex + 1]);
        } catch (IllegalArgumentException e) {
            return new HandlerReturn(HandlerReturnResult.ERROR, "");
        }
        if (res == null) {
            res = "new\n";
        } else {
            res = "overwrite\n" + res + "\n";
        }
        return new HandlerReturn(HandlerReturnResult.SUCCESS, res);
    }

    public HandlerReturn handleGet(String[] comAndParams, int bIndex, int eIndex) {
        String res;
        try {
            res = table.get(comAndParams[bIndex]);
        } catch (IllegalArgumentException e) {
            return new HandlerReturn(HandlerReturnResult.ERROR, "");
        }
        if (res == null) {
            res = "not found\n";
        } else {
            res = "found\n" + res + "\n";
        }
        return new HandlerReturn(HandlerReturnResult.SUCCESS, res);
    }

    public HandlerReturn handleRemove(String[] comAndParams, int bIndex,
            int eIndex) {
        String res;
        String key = comAndParams[bIndex];
        try {
            res = table.get(key);
        } catch (IllegalArgumentException e) {
            return new HandlerReturn(HandlerReturnResult.ERROR, "");
        }
        if (res == null) {
            res = "not found\n";
        } else {
            res = "removed\n";
            table.remove(key);
        }
        return new HandlerReturn(HandlerReturnResult.SUCCESS, res);
    }

    public HandlerReturn handleList() {
        String res;
        res = String.join(", ", table.list());
        res += (res.equals("")) ? "" : "\n";
        return new HandlerReturn(HandlerReturnResult.SUCCESS, res);
    }

    public HandlerReturn handleSize() {
        return new HandlerReturn(HandlerReturnResult.SUCCESS, table.size()
                + "\n");
    }

    public HandlerReturn handleRollback(String[] comAndParams, int bIndex,
            int eIndex) {
        return new HandlerReturn(HandlerReturnResult.SUCCESS, table.rollback()
                + "\n");
    }

    public HandlerReturn handleCommit(String[] comAndParams, int bIndex,
            int eIndex) {
        return new HandlerReturn(HandlerReturnResult.SUCCESS, table.commit()
                + "\n");
    }

    public HandlerReturn handleUse(String[] comAndParams, int bIndex, int eIndex) {
        TableWithTransactions newTable;
        try {
            newTable = (TableWithTransactions) provider
                    .getTable(comAndParams[bIndex]);
        } catch (IllegalArgumentException e1) {
            return new HandlerReturn(HandlerReturnResult.ERROR, "");
        }
        if (newTable == null) {
            return new HandlerReturn(HandlerReturnResult.SUCCESS,
                    comAndParams[bIndex] + " not exists\n");
        }
        // If table isn't chosen.
        if (table == null) {
            table = newTable;
            return new HandlerReturn(HandlerReturnResult.SUCCESS, "using "
                    + comAndParams[bIndex] + "\n");
        }
        // If table is already chosen.
        int unsaved = table.getUnsavedChanges();
        if (unsaved > 0) {
            return new HandlerReturn(HandlerReturnResult.SUCCESS, unsaved
                    + "unsaved changes\n");
        }
        try {
            table.exit();
        } catch (IOException e) {
            System.err.println("Couldn't save" + table.getName() + "on disk");
        }
        table = newTable;
        return new HandlerReturn(HandlerReturnResult.SUCCESS, "using "
                + comAndParams[bIndex] + "\n");
    }

    public HandlerReturn handleCreate(String[] comAndParams, int bIndex,
            int eIndex) {
        Table newTable;
        try {
            newTable = provider.createTable(comAndParams[bIndex]);
        } catch (IllegalArgumentException e) {
            return new HandlerReturn(HandlerReturnResult.ERROR,
                    "wrong tablename\n");
        }
        if (newTable == null) {
            return new HandlerReturn(HandlerReturnResult.SUCCESS,
                    comAndParams[bIndex] + " exists\n");
        }
        return new HandlerReturn(HandlerReturnResult.SUCCESS, "created\n");
    }

    public HandlerReturn handleDrop(String[] comAndParams, int bIndex,
            int eIndex) {

        try {
            provider.removeTable(comAndParams[bIndex]);
        } catch (IllegalStateException e) {
            return new HandlerReturn(HandlerReturnResult.SUCCESS,
                    comAndParams[bIndex] + " not exists\n");
        } catch (IllegalArgumentException e) {
            return new HandlerReturn(HandlerReturnResult.ERROR,
                    "wrong tablename\n");
        }

        return new HandlerReturn(HandlerReturnResult.SUCCESS, "dropped\n");
    }

    public HandlerReturn handleShowTables(String[] comAndParams, int bIndex,
            int eIndex) {
        if (!comAndParams[bIndex].toLowerCase().equals("tables")) {
            return new HandlerReturn(HandlerReturnResult.NO_SUCH_COMMAND,
                    "show " + comAndParams[bIndex] + "\n");
        }
        String res = "";
        List<String> tableNames = provider.showTables();
        String tableName = table == null ? "" : table.getName();
        for (String curName : tableNames) {
            if (tableName.equals(curName)) {
                res += curName + " " + table.size() + "\n";
            } else {
                TableWithTransactions curTable = (TableWithTransactions) provider
                        .getTable(curName);
                res += curName + " " + curTable.size() + "\n";
                try {
                    curTable.exit();
                } catch (IOException e) {
                    System.err.println("Interrupted access to database: "
                            + curName);
                }
            }
        }
        return new HandlerReturn(HandlerReturnResult.SUCCESS, res);
    }

    public HandlerReturn handleExit() throws IOException {
        if (table == null) {
            provider.close();
            return new HandlerReturn(HandlerReturnResult.EXIT, "goodbye\n");
        }
        if (table.getUnsavedChanges() > 0) {
            return new HandlerReturn(HandlerReturnResult.ERROR,
                    "there're unsaved changes\n");
        }
        try {
            table.exit();
        } catch (IOException e) {
            System.err.println("Couldn't save " + table.getName());
            return new HandlerReturn(HandlerReturnResult.EXIT, "");
        }
        provider.close();
        return new HandlerReturn(HandlerReturnResult.EXIT, "goodbye\n");
    }

    public void emergencyExit() throws IOException {
        try {
            if (table != null) {
                table.exit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            provider.close();
        }
    }

}
