package com.example.codengine.printerapp.PrinterEssentials;


import static android.os.Build.VERSION.SDK_INT;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.epson.epos2.Epos2CallbackCode;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class PrinterExceptions {

    public static Context context;

    public static String getInitializeException(String key, Epos2Exception e){
        String result ="";

        switch (e.getErrorStatus()){
            case Epos2Exception.ERR_PARAM:
                result = "An invalid parameter was passed";
                break;
            case Epos2Exception.ERR_MEMORY:
                result = "Memory necessary for processing could not be allocated.";
                break;
            case Epos2Exception.ERR_UNSUPPORTED:
                result = "A model name or language not supported was specified";
                break;
            default:
                result = e.getErrorStatus()+" UNKNOWN ERROR FOUND";
                break;
        }
        makeLog(key,result,e);
        return result;
    }

    public static String getAddTextException(String key, Epos2Exception e){

        String result ="";

        switch (e.getErrorStatus()){
            case Epos2Exception.ERR_PARAM:
                result = "An invalid parameter was passed";
                break;
            case Epos2Exception.ERR_MEMORY:
                result = "Memory necessary for processing could not be allocated.";
                break;
            case Epos2Exception.ERR_FAILURE:
                result = "An unknown error occurred.";
                break;
            default:
                result = e.getErrorStatus()+" UNKNOWN ERROR FOUND";
                break;
        }
        makeLog(key,result,e);
        return result;
    }

    public static String getConnectException(String key, Epos2Exception e){

        String result ="";

        switch (e.getErrorStatus()){
            case Epos2Exception.ERR_PARAM:
                result = "An invalid parameter was specified";
                break;
            case Epos2Exception.ERR_CONNECT:
                result = "Failed to open the device.";
                break;
            case Epos2Exception.ERR_TIMEOUT:
                result = "Failed to communicate with the devices within the specified time.";
                break;
            case Epos2Exception.ERR_ILLEGAL:
                result = "Tried to start communication with a printer with which communication had been already established.";
                break;
            case Epos2Exception.ERR_MEMORY:
                result = "Necessary memory could not be allocated..";
                break;
            case Epos2Exception.ERR_PROCESSING:
                result = "Could not run the process";
                break;
            case Epos2Exception.ERR_NOT_FOUND:
                result = "The device could not be found";
                break;
            case Epos2Exception.ERR_IN_USE:
                result = "The device was in use.";
                break;
            case Epos2Exception.ERR_TYPE_INVALID:
                result = "The device type is different.";
                break;
            default:
                result = e.getErrorStatus()+" UNKNOWN ERROR FOUND";
                break;
        }
        makeLog(key,result,e);
        return result;
    }

    public static String getSendDataException(String key, Epos2Exception e){

        String result ="";

        switch (e.getErrorStatus()){
            case Epos2Exception.ERR_PARAM:
                result = "An invalid parameter was passed";
                break;
            case Epos2Exception.ERR_MEMORY:
                result = "Necessary memory could not be allocated..";
                break;
            case Epos2Exception.ERR_FAILURE:
                result = "An unknown error occurred.";
                break;
            case Epos2Exception.ERR_ILLEGAL:
                result = "The control commands have not been buffered.This API was called while no communication had been started.";
                break;
            case Epos2Exception.ERR_PROCESSING:
                result = "Could not run the process";
                break;
            default:
                result = e.getErrorStatus()+" UNKNOWN ERROR FOUND";
                break;
        }
        makeLog(key,result,e);
        return result;
    }

    public static String getDisconnectPrinterException(String key, Epos2Exception e){

        String result ="";

        switch (e.getErrorStatus()){
            case Epos2Exception.ERR_MEMORY:
                result = "Necessary memory could not be allocated..";
                break;
            case Epos2Exception.ERR_FAILURE:
                result = "An unknown error occurred.";
                break;
            case Epos2Exception.ERR_ILLEGAL:
                result = "Tried to end communication where it had not been established";
                break;
            case Epos2Exception.ERR_PROCESSING:
                result = "Could not run the process";
                break;
            default:
                result = e.getErrorStatus()+" UNKNOWN ERROR FOUND";
                break;
        }
        makeLog(key,result,e);
        return result;
    }

    public static String getBeginEndTransactionException(String key, Epos2Exception e){

        String result ="";

        switch (e.getErrorStatus()){
            case Epos2Exception.ERR_FAILURE:
                result = "An unknown error occurred.";
                break;
            case Epos2Exception.ERR_ILLEGAL:
                result = "This API was called while no communication had been started. Another transaction had been already started by this function.";
                break;
            default:
                result = e.getErrorStatus()+" UNKNOWN ERROR FOUND";
                break;
        }
        makeLog(key,result,e);
        return result;
    }

    public static String getPtrReceiveCode(String key, int code){

        String result ="";

        switch (code){
            case Epos2CallbackCode.CODE_SUCCESS:
                result = "Print succeeded.";
                break;
            case Epos2CallbackCode.CODE_PRINTING:
                result = "Printing";
                break;
            case Epos2CallbackCode.CODE_ERR_AUTORECOVER:
                result = "Automatic recovery error occurred.";
                break;
            case Epos2CallbackCode.CODE_ERR_COVER_OPEN:
                result = "Cover open error occurred";
                break;
            case Epos2CallbackCode.CODE_ERR_CUTTER:
                result = "Auto cutter error occurred";
                break;
            case Epos2CallbackCode.CODE_ERR_MECHANICAL:
                result = "Mechanical error occurred";
                break;
            case Epos2CallbackCode.CODE_ERR_EMPTY:
                result = "No paper is left in the roll paper end detector.";
                break;
            case Epos2CallbackCode.CODE_ERR_UNRECOVERABLE:
                result = "Unrecoverable error occurred.";
                break;
            case Epos2CallbackCode.CODE_ERR_FAILURE:
                result = "Error exists in the requested document syntax";
                break;
            case Epos2CallbackCode.CODE_ERR_NOT_FOUND:
                result = "Printer specified by the device ID does not exist.";
                break;
            case Epos2CallbackCode.CODE_ERR_SYSTEM:
                result = "Error occurred with the printing system.";
                break;
            case Epos2CallbackCode.CODE_ERR_PORT:
                result = "Error was detected with the communication port.";
                break;
            case Epos2CallbackCode.CODE_ERR_TIMEOUT:
                result = "Print timeout occurred";
                break;
            case Epos2CallbackCode.CODE_ERR_JOB_NOT_FOUND:
                result = "Specified print job ID does not exist.";
                break;
            case Epos2CallbackCode.CODE_ERR_SPOOLER:
                result = "Print queue is full";
                break;
            case Epos2CallbackCode.CODE_ERR_BATTERY_LOW:
                result = "Battery has run out";
                break;
            case Epos2CallbackCode.CODE_ERR_TOO_MANY_REQUESTS:
                result = "The number of print jobs sent to the printer has exceeded the allowable limit.";
                break;
            case Epos2CallbackCode.CODE_ERR_REQUEST_ENTITY_TOO_LARGE:
                result = "The size of the print job data exceeds the capacity of the printer.";
                break;

            default:
                result = code+" UNKNOWN ERROR FOUND";
                break;
        }
        makeLog(key,result,null);
        return result;
    }

    public static String getDiscoveryException(String key, Epos2Exception e){
        String result ="";

        switch (e.getErrorStatus()){

            case Epos2Exception.ERR_PARAM:
                result = "An invalid parameter was passed";
                break;
            case Epos2Exception.ERR_ILLEGAL:
                result = "Tried to start search when search had been already done. Bluetooth is OFF. There is no permission for the position information.";
                break;

            case Epos2Exception.ERR_MEMORY:
                result = "Memory necessary for processing could not be allocated.";
                break;
            case Epos2Exception.ERR_FAILURE:
                result = "An unknown error occurred.";
                break;
            case Epos2Exception.ERR_PROCESSING:
                result = "Could not run the process.";
                break;
            default:
                result = e.getErrorStatus()+" UNKNOWN ERROR FOUND";
                break;
        }
        makeLog(key,result,e);
        return result;
    }

    public static String getDiscoveryStopException(String key, Epos2Exception e){
        String result ="";

        switch (e.getErrorStatus()){
            case Epos2Exception.ERR_ILLEGAL:
                result = "Tried to stop a search while it had not been started.";
                break;

            case Epos2Exception.ERR_FAILURE:
                result = "An unknown error occurred.";
                break;
            default:
                result = e.getErrorStatus()+" UNKNOWN ERROR FOUND";
                break;
        }
        makeLog(key,result,e);
        return result;
    }

    private static void makeLog(String key, String result, Epos2Exception e){
        Log.e(key+"",result);
       // appendLog("ERROR--KEY "+key+ " RESULT "+result +" Epos2Exception "+(e!=null?e.getMessage():"EXCEPTN NULL"));

    }


    public static void appendLog( String text)
    {

        //    File folder =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        try
        {
        if (context==null){
            //Log.e("COntext is null","Not able to store");
            return;
        }
            File folder =  context.getExternalCacheDir();//context.getFilesDir().getParentFile();
        File logFile = new File(folder,"PRINTER_APP_LOG.txt");
        if (!logFile.exists())
        {
            try
            {
                //logFile.getParentFile().mkdirs();
                logFile.createNewFile();

                //Log.e("LOG FILE ","--> "+logFile.getAbsolutePath());
            }
            catch (Exception e)
            {
                Log.e("LOG FILE ","EXCCEPTION");

                e.printStackTrace();
            }
        }else
        {
            //Log.e("LOG FILE ","--> "+logFile.getAbsolutePath());
        //Log.e("FILE EXISTS","YEP");
        }

            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String datePrefix = date.format(System.currentTimeMillis());
            //BufferedWriter for performance, true to set append to file flag
            String entry = datePrefix+", "+text;

            Log.e(", "," "+entry);

            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.newLine();
            buf.append(entry);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String getPtrStatusChangeEvent(String key, Epos2Exception e){

        String result ="";

        switch (e.getErrorStatus()){
            case Printer.EVENT_ONLINE:
                result = "Printer is online ";
                break;
            case Printer.EVENT_OFFLINE:
                result = "Printer is offline";
                break;

            case Printer.EVENT_POWER_OFF:
                result = "Power off";
                break;
            case Printer.EVENT_COVER_CLOSE:
                result = "Cover close.";
                break;
            case Printer.EVENT_COVER_OPEN:
                result = "Cover open.";
                break;
            case Printer.EVENT_PAPER_OK:
                result = "Paper remains";
                break;
            case Printer.EVENT_PAPER_NEAR_END:
                result = "Paper has almost run out";
                break;
            case Printer.EVENT_PAPER_EMPTY:
                result = "Paper has run out.";
                break;
            case Printer.EVENT_DRAWER_HIGH:
                result = "Drawer kick connector pin No.3 status = \"H\"";
                break;
            case Printer.EVENT_DRAWER_LOW:
                result = "Drawer kick connector pin No.3 status = \"L\"";
                break;
            case Printer.EVENT_BATTERY_ENOUGH:
                result = "Battery is enough";
                break;
            case Printer.EVENT_BATTERY_EMPTY:
                result = "Battery has run out.";
                break;

        }
        makeLog(key,result,e);
        return result;
    }

}

