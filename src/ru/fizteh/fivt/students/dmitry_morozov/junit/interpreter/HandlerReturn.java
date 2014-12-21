package ru.fizteh.fivt.students.dmitry_morozov.junit.interpreter;


public class HandlerReturn {
    HandlerReturnResult ret;
    String message;
    public HandlerReturn(HandlerReturnResult argRet, String argMessage) {
        ret = argRet; 
        message = argMessage;
    }
    public String getMessage() {
        return message;
    }
    
    public HandlerReturnResult getVal() {
        return ret;
    }
}
