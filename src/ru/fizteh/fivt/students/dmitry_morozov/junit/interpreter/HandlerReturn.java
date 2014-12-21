package ru.fizteh.fivt.students.dmitry_morozov.junit.interpreter;


public class HandlerReturn {
    HandlerReturnResult ret;
    String message;
    public HandlerReturn(HandlerReturnResult _ret, String _message) {
        ret = _ret; 
        message = _message;
    }
    public String getMessage() {
        return message;
    }
    
    public HandlerReturnResult getVal() {
        return ret;
    }
}
