package ru.fizteh.fivt.students.dmitry_morozov.junit.interpreter;


public class HandlerReturn {
    HandlerReturnResult ret;
    String res;
    public HandlerReturn(HandlerReturnResult rret, String rres) {
        ret = rret; 
        res = rres;
    }
    public String getMessage() {
        return res;
    }
    
    public HandlerReturnResult getVal() {
        return ret;
    }
}
