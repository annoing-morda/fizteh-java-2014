package ru.fizteh.fivt.students.dmitry_morozov.junit;

import java.util.List;
import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public class DBCollection implements TableProvider {
    private String dirPath;
    private FileMap maps;

    public DBCollection(String dirPath) throws IllegalArgumentException {
        this.dirPath = dirPath;
        maps = null;
        if (dirPath == null) {
            throw new IllegalArgumentException("path is null");
        }
        if (dirPath.endsWith("/") && !dirPath.equals("/")) {
            String tmp = "";
            for (int i = 0; i < dirPath.length() - 1; i++) {
                tmp += dirPath.charAt(i);
            }
            dirPath = tmp;
        }
        try {
            maps = new FileMap(dirPath + "/tables_info.dat");
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
            res = new ReversableMFHM(name);
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
        maps.put(name, dirPath + "/" + name);
        File dir = new File(dirPath + "/" + name);
        if (!dir.mkdirs()) {
            throw new IllegalArgumentException();
        }
        ReversableMFHM res;
        try {
            res = new ReversableMFHM(dirPath + "/" + name);
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
                System.err.println("deleting table from disk failed");
            } else {
                maps.remove(name);
            }
        } else { // Database not found.
            throw new IllegalStateException();
        }
    }

    public void emeregencyExit() {
        try {
            maps.exit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
