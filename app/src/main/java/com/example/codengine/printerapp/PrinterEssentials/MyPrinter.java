package com.example.codengine.printerapp.PrinterEssentials;



import static com.example.codengine.printerapp.Activity.PrinterPOCMultiple.BAR_PRINTER;
import static com.example.codengine.printerapp.Activity.PrinterPOCMultiple.KITCHEN_COLD;
import static com.example.codengine.printerapp.Activity.PrinterPOCMultiple.KITCHEN_HOT;
import static com.example.codengine.printerapp.Activity.PrinterPOCMultiple.KITCHEN_MISC;

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
import com.example.codengine.printerapp.PrinterEssentials.ThreadManager.Manager;
import com.example.codengine.printerapp.R;
import com.example.codengine.printerapp.Utils.AppUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class MyPrinter implements ReceiveListener {

    String METHOD_NAME = "";
    boolean isError = false;

    Activity context;
    public String printerName;
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

    boolean isGotResult = false;

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

        reportException("Printer Type "+printerType+" Pritner Model "+printerModel);

    }

    private void setError(boolean isError){
        this.isError = isError;
    }

    @Override
    public void onPtrReceive(Printer printergot, int code, PrinterStatusInfo printerStatusInfo, String s) {
        METHOD_NAME = "onPtrReceive";
        try{
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.e("NAME IN ","-"+printerName+" printer null "+(printer==null));
                        setError(false);
                        reportException("Switched to UI thread in onPtrReceive");
                        ERR = PrinterExceptions.getPtrReceiveCode(printerName,code);
                        RetryEnum result = RetryEnum.RETRY_AND_DISCONNECT;
                        switch (code){
                            case Epos2CallbackCode.CODE_SUCCESS:
                                result = RetryEnum.DISCONNECT;
                                reportSuccess();
                                break;
                            case Epos2CallbackCode.CODE_PRINTING:
                                //makeToast(printerName+" ON_PTR_RECEIVE "+ERR);
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

                        //reportException(printerName+" OnPtr : "+ERR);
                        reportException(ERR);
                        RetryEnum finalResult = result;

                        Manager.getManagerInstance().runTask(new Runnable() {
                            @Override
                            public void run() {
                                //makeToast("triggering on new thread in UI thread");
                                retryPrinterConnection(finalResult,ERR);
                            }
                        });
                        /*context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });*/

                    }catch (Exception ex){
                        ex.printStackTrace();
                        setError(true);
                        reportException("Exception in OnPTR Receive runOnUI");
                    }
                }
            });
        }catch (Exception ex){
            setError(true);
            ex.printStackTrace();
            reportException("Exception in OnPTR Receive for runOnUI");
        }
    }

    public interface MyPrinterCallback{
        public void onMyPrinterCallback(String printerName,PrinterEvents printerEvents);
    }


    public void proceedPrint(){
        METHOD_NAME = "proceedPrint";
        setError(false);
        try {
            reportException("proceedPrint Started");
            initializePrinter();
        }catch (Exception ex){
            setError(true);
            reportException("EXCEPTION IN PRoceed Print "+ex.getMessage());
            ex.printStackTrace();
        }
    }

    void initializePrinter(){
        METHOD_NAME= "initializePrinter";
        try{
            setError(false);


            printer = new Printer(printerType, printerModel, context);
            printer.setReceiveEventListener(this);
            reportException("initializePrinter done");

            if (getMyDataArrayList()!=null && getMyDataArrayList().size()>0){

                boolean canProceed = true;
                for (int i=0;i<getMyDataArrayList().size();i++){
                 //makeLog("In "+i+" ITEM in FOR LOOP CREATING DATA");
                     if (!createPrintData(getMyDataArrayList().get(i).getData())){
                         canProceed = false;
                         //reportException("SOMETHING WRONG WITH CREATE DATA "+i+" BREAKING FLOW");
                         break;
                     }
                }

                if (canProceed) {
                    //makeLog("CAN PROCEED IS TRUE SO CONNECTING PRINTER");
                    connectPrinter();
                }else{
                    reportException("CAN PROCEED IS FALSE SO RETURNING FALSE ");

                }

            }else {
               reportException("NO DATA TO BE BUILD");
                //return false;
            }
           // return true;
        }catch (Epos2Exception e){
            setError(true);
            ERR = "INITIALIZE_ERR : "+PrinterExceptions.getInitializeException(printerName,e);
            reportException(ERR);
            retryPrinterConnection(RetryEnum.SHOW_ERROR_POPUP,ERR);
            //return false;
        }
    }

    SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    boolean createPrintData(String data){
        METHOD_NAME = "createPrintData";
        try{
            setError(false);
            String method = "";
            Bitmap logoData = BitmapFactory.decodeResource(context.getResources(), R.drawable.cash);
            StringBuilder textData = new StringBuilder();
            final int barcodeWidth = 2;
            final int barcodeHeight = 100;
            if (printer == null) {
                reportException("Printer is null in createPrintData returning");
                return false;
            }
            printer.addTextStyle(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.COLOR_1);
            printer.addFeedLine(1);
          /* if (printerName.contains(KITCHEN_HOT)) {
               printer.addTextStyle(Printer.PARAM_DEFAULT, Printer.TRUE, Printer.TRUE, Printer.COLOR_1);
           }else if (printerName.contains(KITCHEN_COLD)) {
                printer.addTextStyle(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.COLOR_2);
            }
           else if (printerName.contains(KITCHEN_MISC)) {
               printer.addTextStyle(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.COLOR_3);
           }else if (printerName.contains(BAR_PRINTER)) {
               printer.addTextStyle(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.COLOR_4);
           }
*/
                method = "addTextAlign";
                printer.addTextAlign(Printer.ALIGN_CENTER);

              /*  method = "addImage";
                printer.addImage(logoData, 0, 0,
                        logoData.getWidth(),
                        logoData.getHeight(),
                        Printer.COLOR_1,
                        Printer.MODE_MONO,
                        Printer.HALFTONE_DITHER,
                        Printer.PARAM_DEFAULT,
                        Printer.COMPRESS_AUTO);
                        */

                printer.addTextSize(1, 1);

           // textData.append("\n");
            method = "addText";
            printer.addText("" + printerName+" Print Success");



            method = "addFeedLine";
            printer.addFeedLine(1);

            method = "addText";
            printer.addText(""+data);

            method = "addFeedLine";
            printer.addFeedLine(1);

            printer.addFeedLine(1);
            printer.addText("COLOR PRINT");


            printer.addTextSize(1, 2);
            textData.append("\n");
            textData.append("SANDEEP");
            printer.addText(textData.toString());
            textData.delete(0, textData.length());


            printer.addFeedLine(1);
            printer.addTextSize(1, 1);
            textData.append("\n");
            textData.append("CHORGE");
            textData.append("\n");
            printer.addText(textData.toString());
            textData.delete(0, textData.length());

            /*printer.addTextSize(1, 2);
            printer.addTextFont(Printer.FONT_A);
            printer.addFeedLine(1);
            printer.addText("FONT A");
            printer.addTextSize(2, 2);
            printer.addLineSpace(20);
            printer.addFeedLine(1);
            printer.addText("FONT A");
*/
     /*       printer.addTextFont(Printer.FONT_B);
            printer.addFeedLine(1);
            printer.addText("FONT B");
            printer.addFeedLine(1);
            printer.addText("FONT B");

            printer.addTextFont(Printer.FONT_C);
            printer.addFeedLine(1);
            printer.addText("FONT C");
            printer.addFeedLine(1);
            printer.addText("FONT C");

            printer.addTextFont(Printer.FONT_D);
            printer.addFeedLine(1);
            printer.addText("FONT D");
            printer.addFeedLine(1);
            printer.addText("FONT D");
            printer.addLineSpace(2);*/

            printer.addFeedLine(1);
            printer.addFeedLine(1);
                method = "addCut";
                printer.addCut(Printer.CUT_FEED);

                textData = null;
                reportException("createPrintData done");
            return true;

        }catch (Exception ex){
            setError(true);
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

    void connectPrinter(){

        METHOD_NAME = "connectPrinter";
        try{
            setError(false);
            if (printer == null) {
             reportException("PRINTER IS NULL SO RETURN TO PROCEED PRINT METHOD");
                proceedPrint();
            }

            String tcp = "TCP:" + IPAddress.trim();

            reportException("Trying to connect to "+tcp);
            if (printerType == Printer.TM_U220)
                printer.connect(tcp, 30000);
            else
                printer.connect(tcp, Print.PARAM_DEFAULT);

            reportException("connectPrinter done");

            //Checking Printer Status
            METHOD_NAME = "printerStatus";
            boolean canPrint = true;
            PrinterStatusInfo status = printer.getStatus();
            reportException("PrinterStatusInfo check done");

            if (!isPrintable(status)) {
                canPrint = false;
                reportException("PRINTER_STATUS "+makeErrorMessage(status));
            }

            reportException("isPrintable check done Can Print "+canPrint);
            if (!canPrint){
                setError(true);
                boolean shouldShowRetry = true;
                RetryEnum retryEnum = RetryEnum.DISCONNECT_AND_ERROR_POPUP;
                if (status.getCoverOpen() == Printer.TRUE) {
                    retryEnum = RetryEnum.DISCONNECT_AND_RETRY_POPUP;
                    ERR = context.getString(R.string.handlingmsg_err_cover_open);
                    ///makeLog("Reboot Printer : ERR "+makeErrorMessage(status));
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
               // return false;
            }else
            {
                sendDataToPrinter();
            }

            //return true;
        }catch (Epos2Exception e){
            setError(true);
            ERR = "CONNECT_PRINTER_ERR : "+PrinterExceptions.getConnectException(printerName,e);
            reportException(ERR);
            if (e.getErrorStatus() == Epos2Exception.ERR_ILLEGAL)
                retryPrinterConnection(RetryEnum.RETRY_AND_DISCONNECT,ERR);
            else
                retryPrinterConnection(RetryEnum.AUTO_RETRY_NO_DISCONNECT,ERR);
           // return false;
        }
    }
    void sendDataToPrinter(){
        METHOD_NAME="sendDataToPrinter";
        try{
            setError(false);
            printer.sendData(Printer.PARAM_DEFAULT);
            reportException("sendDataToPrinter done");
           /* while (!isGotResult){
                Log.e("IN WHILE","DID NOT GET RESULT");
            }*/
           // return true;
        }catch (Epos2Exception e){
            setError(true);
            ERR = "SEND_DATA_ERR : "+PrinterExceptions.getSendDataException(printerName,e);
            reportException(ERR);
            retryPrinterConnection(RetryEnum.RETRY_AND_DISCONNECT,ERR);
            //return false;
        }
    }

    boolean disconnectPrinter(boolean toBeDisconnect){
        METHOD_NAME = "disconnectPrinter";
        try{
                setError(false);
            if (printer == null){
                reportException("Printer is null so no need to disconnect");
                return true;
            }

            if (toBeDisconnect) {
                try {
                    printer.disconnect();
                    reportException("disconnect done");
                } catch (Epos2Exception e) {
                    setError(true);
                    ERR = "DISCONNECT_ERR : "+ PrinterExceptions.getDisconnectPrinterException(printerName,e);
                    reportException(ERR);
                    retryPrinterConnection(RetryEnum.SHOW_ERROR_POPUP,ERR);
                }catch (Exception ex){
                    setError(true);
                    ERR = "EXCP_DISCONNECT_ERR : "+ex.getMessage() ;
                    reportException(ERR);
                    retryPrinterConnection(RetryEnum.SHOW_ERROR_POPUP,ERR);
                }
            }

            if(clearPrinter()){
                reportException("Finalized Process in ClearPrinter");
            }else {
                reportException("Unable To Clear Printer");
            }
            return true;
        }catch (Exception e){
            setError(true);
            reportException("DISCONNECT_PRINTER_METHODS ERROR");
            return false;
        }
    }
    boolean clearPrinter(){
        METHOD_NAME = "cleanPrinter";
        try{
            setError(false);
            if (printer == null){
                reportException(" ALready null in clearPrinter");
                return true;
            }

            printer.clearCommandBuffer();
            printer.setReceiveEventListener(null);
            printer =null;
            reportException("ClearPrinter done");
            return true;
        }catch (Exception e){
            setError(true);
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
                Toast.makeText(context, printerName+""+msg, Toast.LENGTH_SHORT).show();
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

        //makeLog("Toast-- Msg "+msg);
        makeLog(msg);
    }
    private void makeLog(String msg){

        String str = Thread.currentThread().getId()+", "
                +Thread.currentThread().getName()+", "
                +printerName+", "
                +METHOD_NAME+", "
                +isError+", "
                +msg;
        PrinterExceptions.appendLog(str);
    }

    private void reportException(String reason){
     /*   if (!reason.contains(printerName))
            reason = printerName + " : "+reason;*/
        //makeToast(reason);
        makeLog(reason);
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
        METHOD_NAME = "retryAllowed";
        if(counter<3){
            return true;
        }else {
            reportException("Clearing printer because attempted 3 times and failed");
            clearPrinter();
            errorDialog(false,"Tried 3 times. Cannot connect to "
                    +printerName+". Please check printer");


            return  false;
        }
    }
    private void retryPrinterConnection(RetryEnum retryEnum, String error) {
        METHOD_NAME = "retryPrinterConnection";
        setError(false);
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
                makeKill();
                break;
            case DISCONNECT_AND_ERROR_POPUP:
                disconnectPrinter(true);
                errorDialog(false,error);
                break;
            case DO_NOTHING:
                reportException(" WILL DO NOTHING");
                break;
            case DISCONNECT_AND_RETRY_POPUP:
                /*In case if printerStatus is not printable*/
                if(disconnectPrinter(true)){
                    errorDialog(true,error);
                }else {
                    reportException(" unable to disconnect and displaying retry alert");
                    errorDialog(true,error);
                }
                break;
        }
    }

    private  void errorDialog(boolean withRetry,String error){
        METHOD_NAME = "errorDialog";
        setError(false);
        try {

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    // appUtils.showAlertDialogwithMessage(context,);
                    if (withRetry){
                        showAlertDialogWithMessage(error);

                    }else {
                        reportException(error);
                        appUtils.showAlertDialogwithMessage(context, error);
                        makeKill();
                        //Thread.currentThread().interrupt();
                    }

                }catch (Exception ex){
                    setError(true);
                    reportException("Exception while displaying popup dialog");
                    ex.printStackTrace();
                }
            }
        });

        }catch (Exception ex) {
            setError(true);
            reportException(ex.getMessage());
        }
    }

    private void retry() {
        METHOD_NAME = "Retry";
        try {
            setError(false);
            counter = counter + 1;
            long tmOut = getTimeOut();
            reportException(" Thread Sleep for "+tmOut+ " Milliseconds before retry again" );
            Thread.sleep(tmOut);
            reportException(" Retrying Again count: "+counter);
            if (printer !=null)
                connectPrinter();
            else
                proceedPrint();

        }catch (Exception ex){
            setError(true);
            reportException(" Exception while Thread sleep in Retry");
            ex.printStackTrace();
        }
    }

    //AlertDialog is not cancellable, displays error message
    public void showAlertDialogWithMessage(String exception) {
        try {

        METHOD_NAME = "DLG_MSG_RETRY";
        setError(false);
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
                    reportException("Retry Count Exceeded");
                    appUtils.showAlertDialogwithMessage(context,printerName+" Retry Count Exceeded");
                    makeKill();
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

        }catch (Exception ex){
            setError(true);
            reportException(""+ex.getMessage());
        }
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
            reportException(res);

        }catch (Exception ex){
            reportException("EXCEPTN printThreadDetails "+ex.getMessage());
            ex.printStackTrace();
        }
    }


    private void makeKill(){
        if (myPrinterCallback!=null){
            PrinterEvents events = new PrinterEvents();
            events.setToastMsg("KILL");
            events.setPrinter(this);
            myPrinterCallback.onMyPrinterCallback(printerName,events);
        }
    }

    private void reportSuccess(){
        if (myPrinterCallback!=null){
            PrinterEvents events = new PrinterEvents();
            events.setToastMsg("SUCCESS");
            events.setPrinter(this);
            myPrinterCallback.onMyPrinterCallback(printerName,events);
        }
    }

    public void callFinalize(){
        METHOD_NAME = "callFinalize";
        setError(false);
        try {
               finalize();
            reportException("callFinalize done");
        }catch (Exception ex){
            setError(true);
            reportException("callFinalize Err");
            ex.printStackTrace();
        } catch (Throwable throwable) {
            setError(true);
            throwable.printStackTrace();
            reportException("callFinalize Err2");
        }
    }

    @Override
    protected void finalize() throws Throwable {

       /* if (METHOD_NAME == "sendDataToPrinter"){
                printers.add(this);
                reportException("IN FINALIZE ADDED TO LIST");
        }else {*/
            this.context = null;
            this.IPAddress = null;
            this.data = null;
            this.myPrinterCallback = null;
            appUtils = null;
            super.finalize();
        //}
       // super.finalize();
    }
}
