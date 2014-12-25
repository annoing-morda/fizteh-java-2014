package ru.fizteh.fivt.students.dmitry_morozov.junit;

import java.util.List;
import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public class DBCollection implements TableProvider {
    private String dirPath;
    private FileMap maps;
    private static final String INFO_FILE_NAME = "tables_info.dat";

    public DBCollection(String dirPath) throws IllegalArgumentException {
        this.dirPath = dirPath;
        maps = null;
        if (dirPath == null) {
            throw new IllegalArgumentException("path is null");
        }
        if (dirPath.endsWith(File.separator) && !dirPath.equals(File.separator)) {
            this.dirPath = dirPath.substring(0, dirPath.length() - 1);
        }
        try {
            maps = new FileMap(dirPath + File.separator + INFO_FILE_NAME);
        } catch (BadDBFileException | IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public List<String> showTables() { // Actually doesn't
                                       // throw anything.
        return maps.list();
    }

    @Override
    public Table getTable(String name) throws IllegalArgumentException {
        String fullPath = maps.get(name);
        if (fullPath == null) {
            return null;
        }
        Table res;
        try {
            res = new TableWithTransactions(name);
        } catch (BadDBFileException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return res;
    }

    @Override
    public Table createTable(String name) {
        // TODO Auto-generated method stub
        if (maps.get(name) != null) {
            return null;
        }
        maps.put(name, dirPath + File.separator + name);
        File dir = new File(dirPath + File.separator + name);
        if (!dir.mkdirs()) {
            throw new IllegalArgumentException();
        }
        TableWithTransactions res;
        try {
            res = new TableWithTransactions(dirPath + File.separator + name);
        } catch (BadDBFileException e) {
            maps.remove(name);
            throw new IllegalStateException();
        }
        return res;
    }

    @Override
    public void removeTable(String name) {
        String tname = maps.get(name);
        if (tname != null) { // Database found.
            if (!Utils.removeDirectory(tname)) {
                throw new IllegalStateException("Couldn't remove directory");
            } else {
                maps.remove(name);
            }
        } else { // Database not found.
            throw new IllegalStateException();
        }
    }

    public void close() throws IOException {
        maps.close();
    }
}
