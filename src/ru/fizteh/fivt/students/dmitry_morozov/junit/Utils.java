package ru.fizteh.fivt.students.dmitry_morozov.junit;

import java.io.File;

public class Utils {
    /**
     * @return False is something was wrong
     */
    static boolean removeDirectory(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            System.err.println("table has been already deleted from disk");
            return false;
        }
        if (!dir.isDirectory()) {
            System.err.println("table directory has been damaged");
            return false;
        }
        File[] flist = dir.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                if (0 == flist[i].listFiles().length) {
                    if (!flist[i].delete()) {
                        return false;
                    }
                } else {
                    if (!removeDirectory(flist[i].getAbsolutePath())) {
                        return false;
                    }
                }
            } else {
                if (!flist[i].delete()) {
                    return false;
                }
            }

        }
        return dir.delete();
    }

    static File safeMkDir(String path) throws BadDBFileException {
        File dir = new File(path);
        if (dir.exists()) {
            if (!dir.isDirectory()) {
                throw new BadDBFileException("File " + path
                        + " already exists and it's not a directory");
            }
        } else {
            dir.mkdir();
        }
        return dir;
    }
}
