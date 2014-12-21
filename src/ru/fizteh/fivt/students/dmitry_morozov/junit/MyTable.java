package ru.fizteh.fivt.students.dmitry_morozov.junit;

import java.util.Stack;

public class MyTable extends MultiFileHashMap {
    Stack<ChangingCommand> opStack;

    public MyTable(String path) throws BadDBFileException {
        super(path);
        opStack = new Stack<>();
    }

    public String put(String key, String value) throws IllegalArgumentException {
        String result = super.put(key, value);
        if (result == null) { // If PUT has just pushed new value
            opStack.push(new ChangingCommand(CommandName.REMOVE, key, ""));
        } else { // If PUT has overwritten old value
            opStack.push(new ChangingCommand(CommandName.PUT, key, result));
        }
        return result;
    }

    public String remove(String key) throws IllegalArgumentException {
        String result = super.remove(key);
        if (result != null) { // Key found.
            opStack.push(new ChangingCommand(CommandName.PUT, key, result));
        }
        return result;
    }

    public int rollback() {
        int cnt = opStack.size();
        while (!opStack.isEmpty()) {
            ChangingCommand t = opStack.pop();
            if (t.cName == CommandName.PUT) {
                super.put(t.args[0], t.args[1]);
            } else {
                super.remove(t.args[0]);
            }
        }
        return cnt;
    }

    public int commit() {
        int cnt = opStack.size();
        opStack.clear();
        return cnt;
    }

    public int getUnsavedChanges() {
        return opStack.size();
    }
}
