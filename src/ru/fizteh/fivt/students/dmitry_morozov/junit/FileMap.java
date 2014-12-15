package ru.fizteh.fivt.students.dmitry_morozov.junit;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ru.fizteh.fivt.storage.strings.Table;

public class FileMap implements Table {
    private Map<String, String> table;
    private File dbFile;

    public FileMap(String path) throws BadDBFileException, IOException {
        table = new HashMap<>();
        dbFile = new File(path);
        if (!dbFile.exists()) {
            if (!dbFile.createNewFile()) {
                throw new BadDBFileException("Couldldn't create db file");
            }
        } else {
            if (!dbFile.isFile()) {
                throw new BadDBFileException("Is not a file");
            }
        }
        if (!(dbFile.setReadable(true)) && dbFile.setWritable(true)) {
            throw new BadDBFileException("Couldn't set rw options");
        }
        DataInputStream in = new DataInputStream(new FileInputStream(dbFile));

       

        while (true) {
            String key = readString(in);
            if(key == null) 
                break;
            String value = readString(in);            
            if (value == null) {
                in.close();
                throw new BadDBFileException("Couldn't set rw options");
            }            
            table.put(key, value);
        }
        in.close();

    }

    /**
     * @return String read from file. If meets end of file, returns null.
     * @throws BadDBFileException 
     * @throws IOException 
     * */

    private String readString(DataInputStream in) throws IOException, BadDBFileException {
        final int sizeOfInt= 4;
        int len;
        String res = "";
        if (in.available() >= sizeOfInt) {
            len = in.readInt();
            if (0 != len % 2 || in.available() < len) {
                in.close();
                throw new BadDBFileException("File was damaged");
            }
            len /= 2;
            while (len > 0) {
                char curChar = in.readChar();
                res += curChar;
                len--;
            }
        } else
            return null;
        return res;        
    }

    public String put(String key, String value) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        String res = table.get(key);
        table.put(key, value);
        return res;
    }

    public String get(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        String val = table.get(key);
        return val;
    }

    public String clearGet(String key) {
        return table.get(key);
    }

    public List<String> list() {
        List<String> res = new ArrayList<>();
        Set<Entry<String, String>> tableSet = table.entrySet();
        for (Entry<String, String> i : tableSet) {
            res.add(i.getKey());
        }
        return res;
    }

    public void printList(PrintWriter pw) {
        Set<Entry<String, String>> tableSet = table.entrySet();
        Iterator<Entry<String, String>> checkLast = tableSet.iterator();
        if (checkLast.hasNext()) {
            checkLast.next();
        }
        for (Entry<String, String> i : tableSet) {
            if (checkLast.hasNext()) {
                pw.print(i.getKey() + ", ");
                checkLast.next();
            } else {
                pw.print(i.getKey());
            }
        }
        pw.flush();
    }

    public void fullList(PrintWriter pw) {
        Set<Entry<String, String>> tableSet = table.entrySet();
        Iterator<Entry<String, String>> checkLast = tableSet.iterator();
        if (checkLast.hasNext()) {
            checkLast.next();
        }
        for (Entry<String, String> i : tableSet) {
            if (checkLast.hasNext()) {
                pw.println(i.getKey() + " " + i.getValue());
                checkLast.next();
            } else {
                pw.print(i.getKey() + " " + i.getValue());
            }
        }
        pw.flush();
    }

    public String remove(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        return table.remove(key);
    }

    public void exit() throws IOException {
        DataOutputStream out = new DataOutputStream(
                new FileOutputStream(dbFile));
        Set<Entry<String, String>> tableSet = table.entrySet();
        for (Entry<String, String> it : tableSet) {
            writeData(out, it.getKey());
            writeData(out, it.getValue());
        }
        out.flush();
        out.close();
    }
    
    private void writeData(DataOutputStream out, String toWrite) throws IOException {
        int len = toWrite.length();
        out.writeInt(len * 2);
        out.writeChars(toWrite);
    }

    public boolean isEmpty() {
        return table.isEmpty();
    }

    public int size() {
        return table.size();
    }

    @Override
    public String getName() {
        return dbFile.getName();
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
