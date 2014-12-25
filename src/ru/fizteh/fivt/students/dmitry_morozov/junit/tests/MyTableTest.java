package ru.fizteh.fivt.students.dmitry_morozov.junit.tests;

import java.util.List;
import java.util.TreeSet;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.dmitry_morozov.junit.*;

public class MyTableTest {
    private Table table;
    
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();
    
    @Before
    public void before() throws IOException {
        TableProviderFactory factory = new MyTableProviderFactory();
        TableProvider provider = factory.create(tmpFolder.newFolder().getAbsolutePath());
        table = provider.createTable("table");
    }

    @Test
    public void getNameTest() {
        String actualName = table.getName();
        Assert.assertEquals("table", actualName);
    }
    
    @Test
    public void getNothingTest() {
        Assert.assertNull(table.get("weather"));
    }
    
    @Test
    public void putTest() {
        Assert.assertNull(table.put("weather", "sunny"));
        Assert.assertEquals("sunny", table.put("weather", "snow"));
    }
    
    @Test
    public void getTest() {
        table.put("weather", "rain");
        Assert.assertEquals("rain", table.get("weather"));
    }

    @Test (expected = IllegalArgumentException.class)
    public void getNullTest() {
        table.get(null);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void putKeyNullTest() {
        table.put(null, "smth");
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void putValueNullTest() {
        table.put("null", null);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void putBothNullTest() {
        table.put(null, null);
    }
    
    @Test
    public void removeTest() {
        Assert.assertNull(table.remove("nothing"));
        table.put("nothing", "something");
        Assert.assertEquals("something", table.remove("nothing"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void removeNullTest() {
        table.remove(null);
    }
    
    @Test
    public void rollbackTest() {
        table.put("1", "one");
        table.put("2", "two");
        table.remove("3");
        table.remove("1");
        table.put("1", "uno");
        Assert.assertEquals(4, table.rollback());
        Assert.assertEquals(0, table.size());
    }
    
    @Test
    public void commitTest() {
        table.put("1", "one");
        table.put("2", "two");
        table.remove("3");
        table.remove("1");
        table.put("1", "uno");
        Assert.assertEquals(4, table.commit());
        Assert.assertNull(table.get("3"));
        Assert.assertEquals("two", table.get("2"));
        Assert.assertEquals("uno", table.get("1"));
    }
    
    @Test
    public void sizeTest() {
        Assert.assertEquals(0, table.size());
        table.put("1", "one");
        Assert.assertEquals(1, table.size());
        table.remove("1");
        Assert.assertEquals(0, table.size());
    }
    
    @Test 
    public void listTest() {
        table.put("1", "one");
        table.put("1", "one");
        table.put("1", "one");
        table.put("3", "one");
        table.put("4", "one");
        table.put("5", "one");
        table.remove("1");
        List<String> list = table.list();
        TreeSet<String> keySet = new TreeSet<String>();
        keySet.addAll(list);
        Assert.assertEquals(3, keySet.size());
        Assert.assertTrue(keySet.contains("3"));
        Assert.assertTrue(keySet.contains("4"));
        Assert.assertTrue(keySet.contains("5"));
        Assert.assertFalse(keySet.contains("1"));
        
    }
}
