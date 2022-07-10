package com.example.codengine.printerapp.PrinterEssentials;

public class PrinterEvents {

    boolean isToast = false;
    String toastMsg = "";

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

