package ru.fizteh.fivt.students.dmitry_morozov.junit;

enum CommandName {
    PUT, REMOVE
};

public class ChangingCommand {
    public CommandName cName;
    public String[] args;

    public ChangingCommand(CommandName c, String arg1, String arg2) {
        cName = c;
        if (cName == CommandName.PUT) {
            args = new String[2];
            args[0] = arg1;
            args[1] = arg2;
        }
        if (cName == CommandName.REMOVE) {
            args = new String[1];
            args[0] = arg1;
        }
    }

}
