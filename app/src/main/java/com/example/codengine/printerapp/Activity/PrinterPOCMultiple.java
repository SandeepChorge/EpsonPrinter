package com.example.codengine.printerapp.Activity;


import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.epson.epos2.printer.Printer;
import com.example.codengine.printerapp.PrinterEssentials.MyPrinter;
import com.example.codengine.printerapp.PrinterEssentials.PrinterEvents;
import com.example.codengine.printerapp.PrinterEssentials.PrinterExceptions;
import com.example.codengine.printerapp.R;
import com.example.codengine.printerapp.Utils.AppUtils;

import java.util.ArrayList;

public class PrinterPOCMultiple extends AppCompatActivity implements MyPrinter.MyPrinterCallback {

    private static final String KITCHEN_HOT = "HOT_PRINTER";
    private static final String KITCHEN_COLD = "COLD_PRINTER";
    private static final String KITCHEN_MISC = "MISC_PRINTER";
    private static final String BAR_PRINTER = "BAR_PRINTER";
    public static Printer printer = null;
    EditText ipaddressEd,ipaddressEd2,ipaddressEd3,ipaddressEd4;
    CheckBox isCheck1,isCheck2,isCheck3,isCheck4;
    Button printDataBtn;
    TextView resultTv;
    Context context;

    AppUtils appUtils;



    Thread t1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiple);
        init();

        printThreadDetails("OnCreateMethod");

        //TedPermission();
       /* try {
            setLogSettings(getApplicationContext(),PERIOD_PERMANENT,OUTPUT_STORAGE,null,0,
                    0,LOGLEVEL_LOW);

        }catch (Exception ex){
            ex.printStackTrace();
            PrinterExceptions.appendLog("Exception in set Settings "+ex.getMessage());
        }*/

        printDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printThreadDetails("OnClickMethod");
                if (!ipaddressEd.getText().toString().isEmpty() //){
                ){
                    try {
                        MyPrinter pritner1 = new MyPrinter(PrinterPOCMultiple.this,
                                KITCHEN_HOT, "DATA1",
                                ipaddressEd.getText().toString().trim(),
                                getPrinterModel(isCheck1.isChecked()),
                                0,
                                PrinterPOCMultiple.this);


                        t1 = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                makeLog("In Thread 1");
                                pritner1.proceedPrint();
                            }
                        });
                        t1.start();

                        if(!ipaddressEd2.getText().toString().isEmpty()) {
                            MyPrinter pritner2 = new MyPrinter(PrinterPOCMultiple.this,
                                    KITCHEN_COLD, "DATA2",
                                    ipaddressEd2.getText().toString().trim(),
                                    getPrinterModel(isCheck2.isChecked()),
                                    0,
                                    PrinterPOCMultiple.this);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    makeLog("In Thread 2");
                                    pritner2.proceedPrint();
                                }
                            }).start();
                        }

                        //MISC
                        if(!ipaddressEd3.getText().toString().isEmpty()) {
                            MyPrinter pritner3 = new MyPrinter(PrinterPOCMultiple.this,
                                    KITCHEN_MISC, "DATA2",
                                    ipaddressEd3.getText().toString().trim(),
                                    getPrinterModel(isCheck3.isChecked()),
                                    0,
                                    PrinterPOCMultiple.this);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    makeLog("In Thread 3");
                                    pritner3.proceedPrint();
                                }
                            }).start();
                        }
                        if(!ipaddressEd4.getText().toString().isEmpty()) {
                            MyPrinter pritner4 = new MyPrinter(PrinterPOCMultiple.this,
                                    BAR_PRINTER, "DATA2",
                                    ipaddressEd4.getText().toString().trim(),
                                    getPrinterModel(isCheck4.isChecked()),
                                    0,
                                    PrinterPOCMultiple.this);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    makeLog("In Thread 4");
                                    pritner4.proceedPrint();
                                }
                            }).start();
                        }
                    }catch (Exception ex){
                        makeLog("Something went wrong "+ex.getMessage());
                    }

                }else {
                    makeLog("Please Enter Valid IP Addresses");
                }
            }
        });
    }

    private int getPrinterModel(boolean isDotMatrix){
        if(isDotMatrix){
            return Printer.TM_U220;
        }else {
            return 0;
        }
    }
    private void printThreadDetails(String methodName){
        try {

            String res = methodName +=" inActivity ThreadID: "+Thread.currentThread().getId();
            makeLog(res);

        }catch (Exception ex){
            makeLog("EXCEPTN printThreadDetails activity "+ex.getMessage());
            ex.printStackTrace();
        }
    }

    void init(){
        context = this;
        appUtils = new AppUtils();

        ipaddressEd = (EditText) findViewById(R.id.ipaddress);
        ipaddressEd2 = (EditText) findViewById(R.id.ipaddress2);
        ipaddressEd3 = (EditText) findViewById(R.id.ipaddress3);
        ipaddressEd4 = (EditText) findViewById(R.id.ipaddress4);
        printDataBtn = (Button) findViewById(R.id.printData);
        resultTv = (TextView) findViewById(R.id.result);

        isCheck1 = (CheckBox) findViewById(R.id.check1);
        isCheck2 = (CheckBox) findViewById(R.id.check2);
        isCheck3 = (CheckBox) findViewById(R.id.check3);
        isCheck4 = (CheckBox) findViewById(R.id.check4);
        //ipaddressEd.setText(appUtils.getStringPrefrences(context, constatnts.SH_APPPREFSOCKETDATA, constatnts.HOTKITCHNPRINTERIPADDRESS));;
    }

    private void makeToast(String msg){
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                try {

                    //Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show();
                    String res= resultTv.getText().toString();
                    res = res+"\n"+msg;
                    resultTv.setText(res);
                    //PrinterExceptions.appendLog("Toast-- Msg "+msg);

                }catch (Exception ex){
                    PrinterExceptions.appendLog("EXCEPTIONIN makeToastCallback-- Msg "+msg);
                    ex.printStackTrace();
                }
            }
        });



    }

    @Override
    public void onMyPrinterCallback(String printerName, PrinterEvents printerEvents) {
        if (printerEvents!=null) {
            makeToast(printerEvents.getToastMsg());
           /* if (t1!=null){
                makeLog("Is Thread Alive "+t1.isAlive());
            }else{
                makeLog("Thread Is Null ");
            }*/
        }else {
            makeToast(printerName+" PrinterEvents is null");
        }
    }
    private void makeLog(String msg){
        Log.e("PrinterPOCMultiple","==> "+msg);
        PrinterExceptions.appendLog("Log-- Msg "+msg);
        makeToast(msg);
    }

    /*public void TedPermission() {



        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
               *//* if (appUtils.isNetworkAvailableWithToast(context)) {
                    appUtils.simpleIntentFinish(context, Sync_device_activity.class, Bundle.EMPTY);
                }*//*
                PrinterExceptions.appendLog("Permissions Granted");
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                appUtils.showToast(context, getString(R.string.Permission_Denied) + "\n" + deniedPermissions.toString());
                finish();
            }
        };


        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getString(R.string.Please_give_permission_for_app_functionality))
                .setDeniedMessage(getString(R.string.If_you_reject_permission_you_can_not_use_this_service) + "\n\n" + getString(R.string.Please_turn_on_permissions_at))
                .setGotoSettingButtonText("setting")
                .setPermissions(Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .check();

    }*/
}