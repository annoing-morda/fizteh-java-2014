package ru.fizteh.fivt.students.dmitry_morozov.junit;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class MyTableProviderFactory implements TableProviderFactory {

    @Override
    public TableProvider create(String dir) throws IllegalArgumentException {
        TableProvider res;
        res = new DBCollection(dir);
        return res;
    }

}
