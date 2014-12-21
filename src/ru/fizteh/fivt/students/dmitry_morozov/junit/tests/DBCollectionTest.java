package ru.fizteh.fivt.students.dmitry_morozov.junit.tests;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.dmitry_morozov.junit.MyTableProviderFactory;

public class DBCollectionTest {
    private TableProvider provider;
    private String dirPath;

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        TableProviderFactory factory = new MyTableProviderFactory();
        dirPath = tmpFolder.newFolder().getAbsolutePath();
        provider = factory.create(dirPath);
    }
    
    @Test
    public void createTable() {
        provider.createTable("table");
    }

    @Test
    public void createTableTwice() {
        provider.createTable("table");
        Assert.assertNull(provider.createTable("table"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableNull() {
        provider.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWrong() throws IOException {
        File file = new File(dirPath + "/qqqqqqqqq.smt");
        file.createNewFile();
        provider.createTable(file.getName());
    }

    @Test
    public void getTable() {
        provider.createTable("table");
        Assert.assertNotNull(provider.getTable("table"));
    }

    @Test
    public void getUnexistingTable() {
        Assert.assertNull(provider.getTable("table"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTableNull() {
        Assert.assertNull(provider.getTable(null));
    }

    @Test
    public void getTableWrong() throws IOException {
        File file = new File(dirPath + "/qqqqqqqqq.smt");
        file.createNewFile();
        Assert.assertNull(provider.getTable(file.getName()));
    }

    @Test
    public void removeTable() {
        provider.createTable("table");
        provider.removeTable("table");
        File removed = new File(dirPath + "/table");
        Assert.assertFalse(removed.exists());
    }

    @Test(expected = IllegalStateException.class)
    public void removeUnexistingTable() {
        provider.removeTable("table");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNull() {
        provider.removeTable(null);
    }
}
