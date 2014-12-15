package ru.fizteh.fivt.students.dmitry_morozov.junit;

enum CommandName{
    PUT,
    REMOVE
};

public class ChangingCommand {
    public CommandName _c;
    public String [] args;
    public ChangingCommand(CommandName c, String arg1, String arg2) {
        _c = c;
        if (_c == CommandName.PUT) {
            args = new String [2];
            args[0] = arg1;
            args[1] = arg2;
        }
        if (_c == CommandName.REMOVE) {
            args = new String [1];
            args[0] = arg1;
        }
    }

}
