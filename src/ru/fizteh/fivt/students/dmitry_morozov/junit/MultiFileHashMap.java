package ru.fizteh.fivt.students.dmitry_morozov.junit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.TreeSet;

import ru.fizteh.fivt.storage.strings.Table;

public class MultiFileHashMap implements Table {
    private static final int FILE_MAPS_CNT = 256;
    private static final int DIRS_CNT = 16;
    private static final String DIR_SUFFIX = ".dir";
    private static final String FILE_SUFFIX = ".dat";
    private File rootDir;
    private BitSet openedMaps;
    private FileMap[] maps;
    private String name;
    int size;

    public MultiFileHashMap(String path) throws BadDBFileException {
        String[] splittedPath = path.split(File.separator);
        name = splittedPath[splittedPath.length - 1];
        size = 0;
        rootDir = Utils.safeMkDir(path);
        openedMaps = new BitSet(FILE_MAPS_CNT);
        openedMaps.clear();
        maps = new FileMap[FILE_MAPS_CNT];
        for (int i = 0; i < DIRS_CNT; ++i) {
            String suffix = File.separator;
            suffix += Integer.toString(i);
            suffix += DIR_SUFFIX;
            Utils.safeMkDir(path + suffix);
        }
    }

    public String put(String key, String value) throws IllegalArgumentException {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        int mapNum = Math.abs(key.hashCode() % FILE_MAPS_CNT);
        if (openedMaps.get(mapNum)) {
            return maps[mapNum].put(key, value);
        } else {
            String curPath = getPath(mapNum);
            try {
                maps[mapNum] = new FileMap(curPath);
            } catch (IOException | BadDBFileException e) {
                throw new BadDBFileException(e.getMessage());
            }
            openedMaps.set(mapNum);
            return maps[mapNum].put(key, value);
        }
    }

    public String get(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        int mapNum = Math.abs(key.hashCode() % FILE_MAPS_CNT);
        if (openedMaps.get(mapNum)) {
            return maps[mapNum].get(key);
        } else {
            String curPath = getPath(mapNum);
            try {
                maps[mapNum] = new FileMap(curPath);
            } catch (IOException | BadDBFileException e) {
                throw new BadDBFileException(e.getMessage());
            }
            openedMaps.set(mapNum);
            return maps[mapNum].get(key);
        }
    }

    public String remove(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        int mapNum = Math.abs(key.hashCode() % FILE_MAPS_CNT);
        if (openedMaps.get(mapNum)) {
            return maps[mapNum].remove(key);
        } else {
            String curPath = getPath(mapNum);
            try {
                maps[mapNum] = new FileMap(curPath);
            } catch (IOException | BadDBFileException e) {
                throw new BadDBFileException(e.getMessage());
            }
            openedMaps.set(mapNum);
            return maps[mapNum].remove(key);
        }
    }

    public List<String> list() {
        List<String> res = new ArrayList<String>();
        for (int i = 0; i < FILE_MAPS_CNT; i++) {
            List<String> t;
            if (openedMaps.get(i)) {
                t = maps[i].list();
                res.addAll(t);
            } else {
                String curPath = getPath(i);
                try {
                    maps[i] = new FileMap(curPath);
                } catch (IOException | BadDBFileException e) {
                    throw new BadDBFileException(e.getMessage());
                }
                t = maps[i].list();
                res.addAll(t);
                openedMaps.set(i);
            }
        }
        return res;

    }

    public void exit() throws IOException {
        TreeSet<Integer> toDelete = new TreeSet<Integer>();
        for (int i = 0; i < FILE_MAPS_CNT; i++) {
            if (openedMaps.get(i)) {
                if (maps[i].isEmpty()) {
                    toDelete.add(i);
                }
                maps[i].close();
            }
        }
        for (Integer num : toDelete) {
            File tdel = new File(getPath(num));
            tdel.delete();
        }
        for (int i = 0; i < DIRS_CNT; i++) {
            String suffix = File.separator;
            suffix += Integer.toString(i);
            suffix += DIR_SUFFIX;
            File tdir = new File(rootDir.getAbsolutePath() + suffix);
            tdir.delete(); // Won't be deleted if there're any files.
        }
    }

    /**
     * @return Path to database file by number of database
     */
    private String getPath(int hash) {
        int ndir = Math.abs(hash % DIRS_CNT);
        int nfile = Math.abs(hash / DIRS_CNT % DIRS_CNT);
        String path = rootDir.getAbsolutePath();
        String suffix = File.separator;
        suffix += Integer.toString(ndir) + DIR_SUFFIX;
        suffix += Integer.toString(nfile) + FILE_SUFFIX;
        path += suffix;

        return path;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int size() {
        int res = 0;
        for (int i = 0; i < FILE_MAPS_CNT; ++i) {
            if (openedMaps.get(i)) {
                res += maps[i].size();
            } else {
                String curPath = getPath(i);
                try {
                    maps[i] = new FileMap(curPath);
                    res += maps[i].size();
                    openedMaps.set(i);
                } catch (IOException | BadDBFileException e) {
                    throw new BadDBFileException(e.getMessage());
                }
            }
        }
        return res;
    }

    @Override
    public int commit() {
        return 0;
    }

    @Override
    public int rollback() {
        return 0;
    }
}
