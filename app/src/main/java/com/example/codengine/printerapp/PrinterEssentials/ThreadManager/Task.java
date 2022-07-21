package com.example.codengine.printerapp.PrinterEssentials.ThreadManager;

import com.example.codengine.printerapp.PrinterEssentials.MyPrinter;
import com.example.codengine.printerapp.PrinterEssentials.PrinterExceptions;

public class Task implements Runnable{

    MyPrinter myPrinter;
    public Task(MyPrinter myPrinter){
        this.myPrinter = myPrinter;
    }

    @Override
    public void run() {
        PrinterExceptions.appendLog(myPrinter.printerName+" TASK RUN");
        myPrinter.proceedPrint();
    }

    @Override
    protected void finalize() throws Throwable {
        PrinterExceptions.appendLog(myPrinter.printerName+" FINALIZED THREAD");
        super.finalize();
    }
}
