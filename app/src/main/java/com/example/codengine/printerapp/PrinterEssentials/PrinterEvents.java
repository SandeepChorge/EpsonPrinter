package com.example.codengine.printerapp.PrinterEssentials;

public class PrinterEvents {

    boolean isToast = false;
    String toastMsg = "";

    MyPrinter printer;

    public MyPrinter getPrinter() {
        return printer;
    }

    public void setPrinter(MyPrinter printer) {
        this.printer = printer;
    }

    public boolean isToast() {
        return isToast;
    }

    public void setToast(boolean toast) {
        isToast = toast;
    }

    public String getToastMsg() {
        return toastMsg;
    }

    public void setToastMsg(String toastMsg) {
        this.toastMsg = toastMsg;
    }


}

