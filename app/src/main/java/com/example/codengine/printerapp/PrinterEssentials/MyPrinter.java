package com.example.codengine.printerapp.PrinterEssentials;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.epson.epos2.Epos2CallbackCode;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.epson.epos2.printer.StatusChangeListener;
import com.epson.eposprint.Print;
import com.example.codengine.printerapp.R;
import com.example.codengine.printerapp.Utils.AppUtils;

import java.util.ArrayList;


public class MyPrinter implements ReceiveListener {

    Activity context;
    String printerName;
    String data;
    String IPAddress;
    int printerType;
    int printerModel;
    MyPrinterCallback myPrinterCallback;
    Printer printer;
    AppUtils appUtils;

    private int counter = 0;
    private long RETRY_TIMEOUT = 2000;
    String ERR = "";

    ArrayList<MyData> myDataArrayList;

    public ArrayList<MyData> getMyDataArrayList() {
        return myDataArrayList;
    }

    public void setMyDataArrayList(ArrayList<MyData> myDataArrayList) {
        this.myDataArrayList = myDataArrayList;
    }

    /*Printer.TM_U220;
            Printer.MODEL_ANK;*/
    public MyPrinter(Activity context,String printerName,String data,
                     String IPAddress, int printerType, int printerModel,
                     MyPrinterCallback myPrinterCallback){

        this.context = context;
        this.printerName = printerName;
        this.IPAddress = IPAddress;
        this.data = data;
        this.printerType = printerType;
        this.printerModel = printerModel;
        this.myPrinterCallback = myPrinterCallback;
        appUtils = new AppUtils();

        makeLog(printerName+"Printer Type "+printerType+" Pritner Model "+printerModel);

    }

    @Override
    public void onPtrReceive(Printer printergot, int code, PrinterStatusInfo printerStatusInfo, String s) {
        printThreadDetails("onPtrReceive");

        try{
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        printThreadDetails(printerName+ " switched to UI thread in onPtrReceive");
                        ERR = PrinterExceptions.getPtrReceiveCode(printerName,code);
                        RetryEnum result = RetryEnum.RETRY_AND_DISCONNECT;
                        switch (code){
                            case Epos2CallbackCode.CODE_SUCCESS:
                                //disconnect
                                result = RetryEnum.DISCONNECT;
                                break;
                            case Epos2CallbackCode.CODE_PRINTING:
                                makeToast(printerName+" ON_PTR_RECEIVE "+ERR);
                                result = RetryEnum.DO_NOTHING;
                                break;

                            case Epos2CallbackCode.CODE_ERR_CUTTER:
                            case Epos2CallbackCode.CODE_ERR_EMPTY:
                                ERR = "Paper roll is empty.Load your printer with paper roll and try again.";
                                result = RetryEnum.SHOW_RETRY_POPUP;
                                break;

                            case Epos2CallbackCode.CODE_ERR_UNRECOVERABLE:
                            case Epos2CallbackCode.CODE_ERR_NOT_FOUND:
                            case Epos2CallbackCode.CODE_ERR_SYSTEM:
                            case Epos2CallbackCode.CODE_ERR_PORT:
                            case Epos2CallbackCode.CODE_ERR_JOB_NOT_FOUND:
                            case Epos2CallbackCode.CODE_ERR_SPOOLER:
                            case Epos2CallbackCode.CODE_ERR_TOO_MANY_REQUESTS:
                            case Epos2CallbackCode.CODE_ERR_REQUEST_ENTITY_TOO_LARGE:
                                result = RetryEnum.DISCONNECT_AND_ERROR_POPUP;
                                break;

                            case Epos2CallbackCode.CODE_ERR_BATTERY_LOW:
                                ERR = "Connect battery and try again";
                                result = RetryEnum.DISCONNECT_AND_ERROR_POPUP;
                                break;

                            default:
                                result = RetryEnum.RETRY_AND_DISCONNECT;
                                break;
                        }

                        reportException(printerName+" OnPtr : "+ERR);
                        RetryEnum finalResult = result;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                makeToast(printerName+" triggering on new thread in UI thread");
                                printThreadDetails("OnPtrNEwThread");
                                retryPrinterConnection(finalResult,ERR);
                            }
                        }).start();

                    }catch (Exception ex){
                        ex.printStackTrace();
                        reportException("Exception in OnPTR Receive runOnUI");
                    }
                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
            reportException("Exception in OnPTR Receive for runOnUI");
        }




    }

    public interface MyPrinterCallback{
        public void onMyPrinterCallback(String printerName,PrinterEvents printerEvents);
    }


    public void proceedPrint(){
        printThreadDetails("proceedPrint");
        try {
            //discoverPrinter();
            makeLog(printerName+" proceedPrint Started ");
            if (initializePrinter()) {
                printer.setReceiveEventListener(this);


            /*if (createPrintData(data)){
                if (connectPrinter()){
                    if(beginTransaction()){
                        if(sendDataToPrinter()){
                            reportException(printerName+ " Sent Data to Printer");
                        }else {
                            reportException(printerName+" Unable To Send Data to Printer");
                        }
                    }
            }    }*/

            }

        }catch (Exception ex){
            reportException("EXCEPTION IN PRoceed Print "+ex.getMessage());
            ex.printStackTrace();
        }
    }

    public boolean discoverPrinter() {
        printThreadDetails("discoverPrinter");
        try {
            makeLog("Discovering PRinter");
            FilterOption filterOption = new FilterOption();
            filterOption.setPortType(Discovery.PORTTYPE_TCP);
            filterOption.setDeviceModel(Discovery.MODEL_ALL);
            filterOption.setEpsonFilter(Discovery.FILTER_NAME);
            filterOption.setDeviceType(Discovery.TYPE_PRINTER);
            Discovery.start(context, filterOption, new DiscoveryListener() {
                @Override
                public void onDiscovery(DeviceInfo deviceInfo) {
                    try {

                        boolean gotDetails = false;
                        if (deviceInfo!=null){
                            if (deviceInfo.getDeviceType() == Discovery.TYPE_PRINTER){
                                gotDetails = true;
                                deviceInfo.getTarget();
                                deviceInfo.getDeviceName();
                                deviceInfo.getIpAddress();

                            }
                        }else{
                            makeToast("DeviceInfo in disconvery is null");
                        }

                        if (gotDetails) {
                            ERR = "TARGET: " + deviceInfo.getTarget() + "\n" +
                                    "NAME: " + deviceInfo.getDeviceName() + "\n" +
                                    "IPADDRESS: " + deviceInfo.getIpAddress() + "\n" +
                                    "MAC ADDR: " + deviceInfo.getMacAddress();
                            Discovery.stop();
                        }else {
                            ERR ="GOT NO PRINTER DETAILS";
                        }
                        reportException(ERR);
                        retryPrinterConnection(RetryEnum.SHOW_ERROR_POPUP,ERR);
                    }catch (Epos2Exception e){
                        ERR = "DISCOVERY_ERR_2 : "+PrinterExceptions.getDiscoveryException(printerName,e);
                        reportException(ERR);
                        retryPrinterConnection(RetryEnum.SHOW_ERROR_POPUP,ERR);
                    }catch (Exception e){
                        ERR = "DISCOVERY_ERR_3 : "+e.getMessage();
                        e.printStackTrace();
                        reportException(ERR);
                        retryPrinterConnection(RetryEnum.SHOW_ERROR_POPUP,ERR);
                    }

                }
            });



        }catch (Epos2Exception e){
            ERR = "DISCOVERY_ERR : "+PrinterExceptions.getDiscoveryException(printerName,e);
            reportException(ERR);
            retryPrinterConnection(RetryEnum.SHOW_ERROR_POPUP,ERR);
        }

        return false;
    }

    boolean initializePrinter(){
        printThreadDetails("initializePrinter");
        try{
            printer = new Printer(printerType, printerModel, context);
            makeLog(printerName+" initializePrinter done");


            if (getMyDataArrayList()!=null && getMyDataArrayList().size()>0){

                boolean canProceed = true;
                for (int i=0;i<getMyDataArrayList().size();i++){
                 makeLog("In "+i+" ITEM in FOR LOOP CREATING DATA");
                     if (!createPrintData(getMyDataArrayList().get(i).getData())){
                         canProceed = false;
                         makeLog("SOMETHING WRONG WITH CREATE DATA "+i+" BREAKING FLOW");
                         makeToast("SOMETHING WRONG WITH CREATE DATA "+i+" BREAKING FLOW");
                         break;
                     }
                }

                if (canProceed) {
                    makeLog("CAN PROCEED IS TRUE SO CONNECTING PRINTER");
                    connectPrinter();
                }else{
                    makeToast("CAN PROCEED IS FALSE SO RETURNING FALSE ");
                    makeLog("CAN PROCEED IS FALSE SO RETURNING FALSE ");
                }

            }else {
                makeToast("NO DATA TO BE BUILD");
                makeLog("NO DATA TO BE BUILD");
                return false;
            }

            return true;
        }catch (Epos2Exception e){
            ERR = "INITIALIZE_ERR : "+PrinterExceptions.getInitializeException(printerName,e);
            reportException(ERR);
            retryPrinterConnection(RetryEnum.SHOW_ERROR_POPUP,ERR);
            return false;
        }
    }

    boolean createPrintData(String data){
        printThreadDetails("createPrintData");
        try{
            String method = "";
            Bitmap logoData = BitmapFactory.decodeResource(context.getResources(), R.drawable.cash);
            StringBuilder textData = new StringBuilder();
            final int barcodeWidth = 2;
            final int barcodeHeight = 100;
            if (printer == null) {
                makeToast(printerName+" Printer is null in createPrintData returning");
                return false;
            }


                method = "addTextAlign";
                printer.addTextAlign(Printer.ALIGN_CENTER);

                method = "addImage";
                printer.addImage(logoData, 0, 0,
                        logoData.getWidth(),
                        logoData.getHeight(),
                        Printer.COLOR_1,
                        Printer.MODE_MONO,
                        Printer.HALFTONE_DITHER,
                        Printer.PARAM_DEFAULT,
                        Printer.COMPRESS_AUTO);

                method = "addFeedLine";
                printer.addFeedLine(1);
                method = "addTextSize";
                printer.addTextSize(1, 1);

            textData.append("\n");
            method = "addText";
            printer.addText("#"+data);

                textData.append("\n");
                method = "addText";
                printer.addText("#" + printerName+" Print Success");
                method = "addText";
                printer.addText(textData.toString());
                textData.delete(0, textData.length());
                method = "addCut";
                printer.addCut(Printer.CUT_FEED);

                textData = null;
                makeLog(printerName+" createPrintData done");


                //IT IS TEMPORARY MOVED TO INITIALIZE AFTER CREATE DATA
                //connectPrinter();

            return true;

        }/*catch (Epos2Exception e){
            ERR ="CREATE_DATA_ERR : "+PrinterExceptions.getAddTextException(printerName,e);
            reportException(ERR);
            retryPrinterConnection(RetryEnum.SHOW_ERROR_POPUP,ERR);


            return false;
        }*/catch (Exception ex){

            if (ex instanceof Epos2Exception){
                Epos2Exception e = (Epos2Exception)ex;
                ERR ="CREATE_DATA_ERR : "+PrinterExceptions.getAddTextException(printerName,e);
                reportException(ERR);
                retryPrinterConnection(RetryEnum.SHOW_ERROR_POPUP,ERR);
                return false;
            }
            ex.printStackTrace();
            ERR ="CREATE_DATA_ERR_2 :" +ex.getMessage();
            reportException(ERR );
            retryPrinterConnection(RetryEnum.SHOW_ERROR_POPUP,ERR);

            return false;
        }
    }

    /*BEIN TRANSACTION IS NOT SUPPORTED FOR TM-88V and TM-220*/
    boolean beginTransaction(){
        try{
            printer.beginTransaction();
            makeLog(printerName+" beginTransaction done");

            sendDataToPrinter();
            return true;
        }catch (Epos2Exception e){
            ERR ="BEGIN_TRANSACTION_ERR : "+PrinterExceptions.getBeginEndTransactionException(printerName,e);
            reportException(ERR);
            if (e.getErrorStatus() == Epos2Exception.ERR_ILLEGAL){
                retryPrinterConnection(RetryEnum.AUTO_RETRY_NO_DISCONNECT,ERR);
            }else
            {
                retryPrinterConnection(RetryEnum.RETRY_AND_DISCONNECT,ERR);
            }
            return false;
        }
    }

    boolean connectPrinter(){
        printThreadDetails("connectPrinter");
        try{
            if (printer == null)
                proceedPrint();

            String tcp = "TCP:" + IPAddress.trim();
            //errorDialog(false,"Trying to connect to "+tcp);
            makeToast(printerName+ " Trying to connect to "+tcp);
            if (printerType == Printer.TM_U220)
                printer.connect(tcp, 30000);
            else
                printer.connect(tcp, Print.PARAM_DEFAULT);

            makeLog(printerName+" connectPrinter done");

            //Checking Printer Status
            boolean canPrint = true;
            PrinterStatusInfo status = printer.getStatus();
            makeLog(printerName+" PrinterStatusInfo check done");

            if (!isPrintable(status)) {
                canPrint = false;
                reportException(printerName+" PRINTER_STATUS "+makeErrorMessage(status));
            }
            makeLog(printerName+" isPrintable check done Can Print "+canPrint);
            if (!canPrint){
                boolean shouldShowRetry = true;
                RetryEnum retryEnum = RetryEnum.DISCONNECT_AND_ERROR_POPUP;
                if (status.getCoverOpen() == Printer.TRUE) {
                    retryEnum = RetryEnum.DISCONNECT_AND_RETRY_POPUP;
                    ERR = context.getString(R.string.handlingmsg_err_cover_open);
                }
                if (status.getPaper() == Printer.PAPER_EMPTY) {
                    retryEnum = RetryEnum.DISCONNECT_AND_RETRY_POPUP;
                    ERR += context.getString(R.string.handlingmsg_err_receipt_end);
                }else{
                    retryEnum = RetryEnum.DISCONNECT_AND_ERROR_POPUP;
                    ERR = makeErrorMessage(status);
                    reportException(printerName+" Reboot Printer : ERR "+makeErrorMessage(status));
                }

                retryPrinterConnection(retryEnum,ERR);
                return false;
            }else
            {
                //proceed for begin transaction
                //beginTransaction();
                sendDataToPrinter();
            }

            return true;
        }catch (Epos2Exception e){
            ERR = "CONNECT_PRINTER_ERR : "+PrinterExceptions.getConnectException(printerName,e);
            reportException(ERR);
            if (e.getErrorStatus() == Epos2Exception.ERR_ILLEGAL)
                retryPrinterConnection(RetryEnum.RETRY_AND_DISCONNECT,ERR);
            else
                retryPrinterConnection(RetryEnum.AUTO_RETRY_NO_DISCONNECT,ERR);
            return false;
        }
    }
    boolean sendDataToPrinter(){
        printThreadDetails("sendDataToPrinter");
        try{
            printer.sendData(Printer.PARAM_DEFAULT);
            makeLog(printerName+" sendDataToPrinter done");
            return true;
        }catch (Epos2Exception e){
            ERR = "SEND_DATA_ERR : "+PrinterExceptions.getSendDataException(printerName,e);
            reportException(ERR);
            retryPrinterConnection(RetryEnum.RETRY_AND_DISCONNECT,ERR);
            return false;
        }
    }
    boolean disconnectPrinter(boolean toBeDisconnect){
        printThreadDetails("disconnectPrinter");
        try{

            if (printer == null){
                makeToast(printerName +" Printer is null so no need to disconnect");
                return true;
            }

            if (toBeDisconnect) {
               /* context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            printThreadDetails(printerName+" On main thread disconnectPrinter");
                            printer.disconnect();
                            makeLog(printerName+" Disconnect done");
                            reportException("CHECK DISCONNECT CALLED");

                            if(clearPrinter()){
                                makeToast("Finalized Process in ClearPrinter");
                            }else {
                                makeToast("Unable To Clear Printer");
                            }
                        } catch (Epos2Exception e) {
                            ERR = "DISCONNECT_ERR : "+ PrinterExceptions.getDisconnectPrinterException(printerName,e);
                            reportException(ERR);
                            retryPrinterConnection(RetryEnum.SHOW_ERROR_POPUP,ERR);
                        }catch (Exception ex){
                            ERR = "EXCP_DISCONNECT_ERR : "+ex.getMessage() ;
                            reportException(ERR);
                            retryPrinterConnection(RetryEnum.SHOW_ERROR_POPUP,ERR);
                        }
                    }
                });*/
                try {
                    printer.disconnect();
                    makeLog(printerName+" Disconnect done");
                    reportException("CHECK DISCONNECT CALLED");
                } catch (Epos2Exception e) {
                    ERR = "DISCONNECT_ERR : "+ PrinterExceptions.getDisconnectPrinterException(printerName,e);
                    reportException(ERR);
                    retryPrinterConnection(RetryEnum.SHOW_ERROR_POPUP,ERR);
                }catch (Exception ex){
                    ERR = "EXCP_DISCONNECT_ERR : "+ex.getMessage() ;
                    reportException(ERR);
                    retryPrinterConnection(RetryEnum.SHOW_ERROR_POPUP,ERR);
                }
            }
            if(clearPrinter()){
                makeToast("Finalized Process in ClearPrinter");
            }else {
                makeToast("Unable To Clear Printer");
            }
            return true;
        }catch (Exception e){
            makeLog("DISCONNECT_PRINTER_METHODS ERROR");
            return false;
        }
    }
    boolean clearPrinter(){
        printThreadDetails("cleanPrinter");
        try{
            if (printer == null){
                makeLog(printerName+" ALready null in clearPrinter");
                return true;
            }


            printer.clearCommandBuffer();
            printer.setReceiveEventListener(null);
            printer =null;
            makeLog(printerName+" ClearPrinter done");
            return true;
        }catch (Exception e){
            reportException("CLEAR_CMDBFR_ERR : "+e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean isPrintable(PrinterStatusInfo status) {
        if (status == null) {
            return false;
        }

        if (status.getConnection() == Printer.FALSE) {
            return false;
        } else if (status.getOnline() == Printer.FALSE) {
            return false;
        } else {
            ;//print available
        }

        return true;
    }



    /*Logging Methods*/
    private void makeToast(String msg){
        context.runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show();
                if (myPrinterCallback!=null){
                    PrinterEvents events = new PrinterEvents();
                    events.setToast(true);
                    events.setToastMsg(msg);
                    myPrinterCallback.onMyPrinterCallback(printerName,events);
                }
                //String res= resultTv.getText().toString();
                //res = res+"\n"+msg;
                //resultTv.setText(res);
            }
        });

        //Log.e("MainActivity","==> "+msg);
        makeLog("Toast-- Msg "+msg);
    }
    private void makeLog(String msg){
        PrinterExceptions.appendLog(msg);
    }

    private void reportException(String reason){
        if (!reason.contains(printerName))
            reason = printerName + " : "+reason;
        makeToast(reason);
    }

    private String makeErrorMessage(PrinterStatusInfo status) {
        String msg = "";

        if (status ==null){
            msg += "PRINTER STATUS IS NULL";
            return msg;
        }

        if (status.getOnline() == Printer.FALSE) {
            msg += context.getString(R.string.handlingmsg_err_offline);
        }
        if (status.getConnection() == Printer.FALSE) {
            msg += context.getString(R.string.handlingmsg_err_no_response);
        }
        if (status.getCoverOpen() == Printer.TRUE) {
            msg += context.getString(R.string.handlingmsg_err_cover_open);
        }
        if (status.getPaper() == Printer.PAPER_EMPTY) {
            msg += context.getString(R.string.handlingmsg_err_receipt_end);
        }
        if (status.getPaperFeed() == Printer.TRUE || status.getPanelSwitch() == Printer.SWITCH_ON) {
            msg += context.getString(R.string.handlingmsg_err_paper_feed);
        }
        if (status.getErrorStatus() == Printer.MECHANICAL_ERR || status.getErrorStatus() == Printer.AUTOCUTTER_ERR) {
            msg += context.getString(R.string.handlingmsg_err_autocutter);
            msg += context.getString(R.string.handlingmsg_err_need_recover);
        }
        if (status.getErrorStatus() == Printer.UNRECOVER_ERR) {
            msg += context.getString(R.string.handlingmsg_err_unrecover);
        }
        if (status.getErrorStatus() == Printer.AUTORECOVER_ERR) {
            if (status.getAutoRecoverError() == Printer.HEAD_OVERHEAT) {
                msg += context.getString(R.string.handlingmsg_err_overheat);
                msg += context.getString(R.string.handlingmsg_err_head);
            }
            if (status.getAutoRecoverError() == Printer.MOTOR_OVERHEAT) {
                msg += context.getString(R.string.handlingmsg_err_overheat);
                msg += context.getString(R.string.handlingmsg_err_motor);
            }
            if (status.getAutoRecoverError() == Printer.BATTERY_OVERHEAT) {
                msg += context.getString(R.string.handlingmsg_err_overheat);
                msg += context.getString(R.string.handlingmsg_err_battery);
            }
            if (status.getAutoRecoverError() == Printer.WRONG_PAPER) {
                msg += context.getString(R.string.handlingmsg_err_wrong_paper);
            }
        }
        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_0) {
            msg += context.getString(R.string.handlingmsg_err_battery_real_end);
        }

        return msg;
    }


    /*RETRY LOGIC*/

    private boolean retryAllowed(){
        if(counter<3){
            return true;
        }else {
            makeLog(printerName+" Clearing printer because attempted 3 times and failed");
            clearPrinter();
            errorDialog(false,"Tried 3 times, Cannot connect to "
                    +printerName+". Please check printer");


            return  false;
        }
    }
    private void retryPrinterConnection(RetryEnum retryEnum, String error) {

        switch (retryEnum) {
            case AUTO_RETRY_NO_DISCONNECT:
                if (retryAllowed())
                    retry();
                break;
            case RETRY_AND_DISCONNECT:
                if (retryAllowed()) {
                    if (disconnectPrinter(true))
                        retry();
                }
                else
                    errorDialog(false,error);//appUtils.showAlertDialogwithMessage(context,error);
                break;
            case SHOW_RETRY_POPUP:
                errorDialog(true,error);
                break;
            case SHOW_ERROR_POPUP:
                errorDialog(false,error);
                //appUtils.showAlertDialogwithMessage(context, error);
                break;
            case DISCONNECT:
                disconnectPrinter(true);
                /*if(disconnectPrinter(true
                )){
                    reportException(printerName+" Printer Disconnected");
                }*/
                break;
            case DISCONNECT_AND_ERROR_POPUP:
                disconnectPrinter(true);
                errorDialog(false,error);
                break;
            case DO_NOTHING:
                makeToast(printerName+" WILL DO NOTHING");
                break;
            case DISCONNECT_AND_RETRY_POPUP:
                /*In case if printerStatus is not printable*/
                if(disconnectPrinter(true)){
                    errorDialog(true,error);
                }else {
                    makeLog(printerName+" unable to disconnect and displaying retry alert");
                    errorDialog(true,error);
                }
                break;
        }
    }

    private  void errorDialog(boolean withRetry,String error){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    // appUtils.showAlertDialogwithMessage(context,);
                    if (withRetry){
                        showAlertDialogWithMessage(error);
                    }else {
                        appUtils.showAlertDialogwithMessage(context, error);
                    }
                }catch (Exception ex){
                    makeLog("Exception while displaying popup dialog");
                    ex.printStackTrace();
                }
            }
        });

    }
    private void retry() {
        try {
            counter = counter + 1;
            long tmOut = getTimeOut();
            makeToast(printerName+" Thread Sleep for "+tmOut+ " Milliseconds before retry again" );
            Thread.sleep(tmOut);
            makeToast(printerName+" Retrying Again count: "+counter);
            if (printer !=null)
                connectPrinter();
            else
                proceedPrint();

        }catch (Exception ex){
            reportException(printerName+" Exception while Thread sleep in Retry");
            ex.printStackTrace();
        }
        /*//retry 3 times only
        while (counter < 3) {
            //connectPrinter(timeOut);
            if (connectPrinter()) {
                counter = 0;
                return;
            } else {
                //timeOut = timeOut + 2000;
                counter++;
            }
        }*/
    }

    //AlertDialog is not cancellable, displays error message
    public void showAlertDialogWithMessage(String exception) {

        CardView btnRetry;
        TextView txtMsg,btnOkTv;
        ImageView imgClose;

        final AlertDialog.Builder dilaog =
                new AlertDialog.Builder(context, R.style.CustomDialogs);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View view = layoutInflater.inflate(R.layout.no_data_availavle, null);
        dilaog.setView(view);
        final AlertDialog subjectAlertDialog = dilaog.create();

        btnRetry = (CardView) view.findViewById(R.id.btnOk);
        btnOkTv = (TextView) view.findViewById(R.id.btnOkTv);
        txtMsg = (TextView) view.findViewById(R.id.txtMsg);
        imgClose = view.findViewById(R.id.imgClose);
        imgClose.setVisibility(View.VISIBLE);
        txtMsg.setText(exception);
        btnOkTv.setText("Retry");
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (retryAllowed()) {
                    retry();
                }else {
                    appUtils.showAlertDialogwithMessage(context,printerName+" Retry Count Exceeded");
                }
                subjectAlertDialog.dismiss();
            }
        });

            /*imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subjectAlertDialog.dismiss();
                }
            });*/

        dilaog.setCancelable(false);
        subjectAlertDialog.setCancelable(false);
        subjectAlertDialog.show();
    }

    private long getTimeOut(){

        if (counter>1){
            RETRY_TIMEOUT = RETRY_TIMEOUT * 2;
        }

        return RETRY_TIMEOUT;
    }

    private void printThreadDetails(String methodName){
        try {

            String res = methodName +=" ThreadID: "+Thread.currentThread().getId();
            makeLog(res);

        }catch (Exception ex){
            makeLog("EXCEPTN printThreadDetails "+ex.getMessage());
            ex.printStackTrace();
        }
    }
}
