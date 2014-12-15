package ru.fizteh.fivt.students.dmitry_morozov.junit.tests;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.dmitry_morozov.junit.MyTableProviderFactory;

public class MyTableProviderFactoryTest {
    private TableProviderFactory factory;
    
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();
    
    @Before
    public void before() {
        factory = new MyTableProviderFactory();
    }
    
    @Test
    public void create() throws IOException {
        factory.create(tmpFolder.newFolder().getAbsolutePath());
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void createNull() {
        factory.create(null);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void createWrong() throws IOException {
        factory.create(tmpFolder.newFile().getAbsolutePath());
    }

}
