package com.example.codengine.printerapp.PrinterEssentials.ThreadManager;

import com.example.codengine.printerapp.PrinterEssentials.MyPrinter;
import com.example.codengine.printerapp.PrinterEssentials.PrinterExceptions;

public class Task implements Runnable{

    MyPrinter myPrinter;
    String METHOD_NAME = "";
    boolean isError = false;
    public Task(MyPrinter myPrinter){
        this.myPrinter = myPrinter;
    }

    @Override
    public void run() {
        isError = false;
        METHOD_NAME = "run";
        makeLog("TASK RUN");
        myPrinter.proceedPrint();
    }

    @Override
    protected void finalize()  {
        METHOD_NAME = "finalize";
        isError = false;
        try {
            super.finalize();
            makeLog("FINALIZED THREAD");
        } catch (Throwable e) {
            isError = true;
            makeLog(e.getMessage());
            e.printStackTrace();
        }
    }

    private void makeLog(String msg){

        String str = Thread.currentThread().getId()+", "
                +Thread.currentThread().getName()+", "
                +myPrinter.printerName+", "
                +METHOD_NAME+", "
                +isError+", "
                +msg;
        PrinterExceptions.appendLog(str);
    }
}
