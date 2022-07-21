package com.example.codengine.printerapp.Activity;


import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.example.codengine.printerapp.PrinterEssentials.DiscoverPrinter;
import com.example.codengine.printerapp.PrinterEssentials.DiscoveryEvents;
import com.example.codengine.printerapp.PrinterEssentials.MyData;
import com.example.codengine.printerapp.PrinterEssentials.MyPrinter;
import com.example.codengine.printerapp.PrinterEssentials.PrinterEvents;
import com.example.codengine.printerapp.PrinterEssentials.PrinterExceptions;
import com.example.codengine.printerapp.PrinterEssentials.ThreadManager.Manager;
import com.example.codengine.printerapp.PrinterEssentials.ThreadManager.Task;
import com.example.codengine.printerapp.R;
import com.example.codengine.printerapp.Utils.AppUtils;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PrinterPOCMultiple extends AppCompatActivity implements MyPrinter.MyPrinterCallback {

    private static final String KITCHEN_HOT = "HP";
    private static final String KITCHEN_COLD = "CP";
    private static final String KITCHEN_MISC = "MP";
    private static final String BAR_PRINTER = "BP";
    public static Printer printer = null;
    EditText ipaddressEd,ipaddressEd2,ipaddressEd3,ipaddressEd4;
    CheckBox isCheck1,isCheck2,isCheck3,isCheck4;
    Button printDataBtn;
    TextView resultTv;
    Context context;

    AppUtils appUtils;

    Button discoverBtn;
    TextView discoverRes;

    int ColdCounter = 1,BarCounter = 1,HotCounter = 1,MiscCounter = 1;
    private String getPrinterName(String PrinterName) {
        String res = "";
        switch (PrinterName) {
            case KITCHEN_COLD:
                res = KITCHEN_COLD + "_" + ColdCounter;
                ColdCounter = ColdCounter + 1;
                break;
            case KITCHEN_HOT:
                res = KITCHEN_HOT + "_" + HotCounter;
                HotCounter = HotCounter + 1;
                break;
            case KITCHEN_MISC:
                res = KITCHEN_MISC + "_" + MiscCounter;
                MiscCounter = MiscCounter + 1;
                break;
            case BAR_PRINTER:
                res = BAR_PRINTER + "_" + BarCounter;
                BarCounter = BarCounter + 1;
                break;
        }
        return res;
    }
    SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiple);

        PrinterExceptions.context = this;

        Manager.CORE_POOL_SIZE = 10;//Runtime.getRuntime().availableProcessors()*2;
        Manager.MAX_POOL_SIZE = Runtime.getRuntime().availableProcessors()*2;//10;
        PrinterExceptions.appendLog("CORE_POOL_SIZE: "+Manager.CORE_POOL_SIZE+"\t MAX_POOL_SIZE: "+Manager.MAX_POOL_SIZE);

        init();
        try {
            com.epson.epos2.Log.setLogSettings(context, com.epson.epos2.Log.PERIOD_PERMANENT, com.epson.epos2.Log.OUTPUT_STORAGE, null, 0, 50, com.epson.epos2.Log.LOGLEVEL_LOW);
        } catch (Epos2Exception e) {
            Log.d("PrintL", e.getLocalizedMessage());
            e.printStackTrace();
        }

        Log.e("SDK INT ","-->"+SDK_INT);
     //   printThreadDetails("OnCreateMethod");

       /* findViewById(R.id.shareData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    File folder =  context.getExternalCacheDir();//context.getFilesDir().getParentFile();
                    //File logFile = new File(folder+File.separator+"RETRY_PRINTER_LOG.txt");
                    File logFile = new File(folder,"PRINTER_APP_LOG.txt");
                    if (logFile.exists())
                    {

                        Log.e("I HRER","YEP");
                        Uri path = FileProvider.getUriForFile(PrinterPOCMultiple.this,"com.example.codengine.printerapp", logFile);
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_STREAM, path);
                        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(i);
                    }else {
                        Log.e("NO","FOUND");
                    }
                }catch (Exception ex){ex.printStackTrace();}
            }
        });*/
      /*  DISCOVER discoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                   PrinterExceptions.appendLog("---------------------------------NEW-------------------------------- ");
                    DiscoverPrinter discoverPrinter = new DiscoverPrinter(PrinterPOCMultiple.this, new DiscoverPrinter.DiscoveryResults() {
                        @Override
                        public void onDiscoveryResults(DiscoveryEvents discoveryEvent) {
                            try {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public synchronized void run() {
                                        try {


                                                String res  = discoverRes.getText().toString();
                                                res = res+"\n"+discoveryEvent.result;
                                                discoverRes.setText(res);


                                        }catch (Exception e){
                                            e.printStackTrace();
                                            discoverRes.setText(discoverRes.getText().toString()+"\n"+e.getMessage());
                                        }
                                    }
                                });


                            }catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    });
                    discoverPrinter.DisoveryStart(null);
                }catch (Exception ex){
                    ex.printStackTrace();
                }


            }
        });*/
       /*
       STACK TRACE AS LIKE EPSON
       try{
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            String callHierarchyName = "";
            int length = stackTrace.length;

            for(int i = 0; i < 3; ++i) {
                if (length >= 4 + i && stackTrace[3 + i].getFileName() != null) {
                    callHierarchyName = callHierarchyName + " at " + stackTrace[3 + i].getFileName() + ":" + stackTrace[3 + i].getLineNumber();
                }
            }

            makeLog("CALL"+callHierarchyName);
        }catch (Exception ex){
            ex.printStackTrace();
        }*/


        printDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //printThreadDetails("OnClickMethod");
                if (!ipaddressEd.getText().toString().isEmpty() //){
                ){
                    try {

                        ArrayList<MyData> arrayList = new ArrayList<>();
                        arrayList.add(new MyData("START: "+date.format(System.currentTimeMillis())));

                        MyPrinter pritner1 = new MyPrinter(PrinterPOCMultiple.this,
                                getPrinterName(KITCHEN_HOT), "DATA1",
                                ipaddressEd.getText().toString().trim(),
                                getPrinterModel(isCheck1.isChecked()),
                                0,
                                PrinterPOCMultiple.this);
                        pritner1.setMyDataArrayList(arrayList);

                        Task task = new Task(pritner1);
                        Manager.getManagerInstance().runTask(task);


                        ArrayList<MyData> arrayList2 = new ArrayList<>();
                        arrayList2.add(new MyData("START: "+date.format(System.currentTimeMillis())));
                        if(!ipaddressEd2.getText().toString().isEmpty()) {
                            MyPrinter pritner2 = new MyPrinter(PrinterPOCMultiple.this,
                                    getPrinterName(KITCHEN_COLD), "DATA2",
                                    ipaddressEd2.getText().toString().trim(),
                                    getPrinterModel(isCheck2.isChecked()),
                                    0,
                                    PrinterPOCMultiple.this);
                            pritner2.setMyDataArrayList(arrayList2);

                            Task task2 = new Task(pritner2);
                            Manager.getManagerInstance().runTask(task2);

                            /* new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    makeLog("Running "+KITCHEN_COLD+" "+ColdCounter);
                                    pritner2.proceedPrint();
                                }
                            }).start();*/
                        }

                        //MISC
                        ArrayList<MyData> arrayList3 = new ArrayList<>();
                        arrayList3.add(new MyData("START: "+date.format(System.currentTimeMillis())));
                        if(!ipaddressEd3.getText().toString().isEmpty()) {
                            MyPrinter pritner3 = new MyPrinter(PrinterPOCMultiple.this,
                                    getPrinterName(KITCHEN_MISC), "DATA2",
                                    ipaddressEd3.getText().toString().trim(),
                                    getPrinterModel(isCheck3.isChecked()),
                                    0,
                                    PrinterPOCMultiple.this);
                            pritner3.setMyDataArrayList(arrayList3);

                            Task task3 = new Task(pritner3);
                            Manager.getManagerInstance().runTask(task3);
                            /* new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    makeLog("Running "+KITCHEN_MISC+" "+MiscCounter);
                                    pritner3.proceedPrint();
                                }
                            }).start();*/
                        }


                        ArrayList<MyData> arrayList4 = new ArrayList<>();
                        arrayList4.add(new MyData("START: "+date.format(System.currentTimeMillis())));

                        if(!ipaddressEd4.getText().toString().isEmpty()) {
                            MyPrinter pritner4 = new MyPrinter(PrinterPOCMultiple.this,
                                    getPrinterName(BAR_PRINTER), "DATA2",
                                    ipaddressEd4.getText().toString().trim(),
                                    getPrinterModel(isCheck4.isChecked()),
                                    0,
                                    PrinterPOCMultiple.this);
                            pritner4.setMyDataArrayList(arrayList4);

                            Task task4 = new Task(pritner4);
                            Manager.getManagerInstance().runTask(task4);
                           /* new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    makeLog("Running "+BAR_PRINTER+" "+BarCounter);
                                    pritner4.proceedPrint();
                                }
                            }).start();*/
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

        initDiscover();
    }


    public void initDiscover(){
        discoverBtn = (Button) findViewById(R.id.btnDiscover);
        discoverRes = (TextView) findViewById(R.id.discoverRes);
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


  /*

   PERMISSION
   try {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show();

                    *//*if (SDK_INT >= Build.VERSION_CODES.R) {
                        if (!Environment.isExternalStorageManager()) {
                            Log.e("ALL PERMISSIONS","NO ALL PERM");
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }else {
                            Log.e("ALL PERMISSIONS","GRANTED");
                        }
                        }*//*

            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(context, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        String[] Permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };//Manifest.permission.MANAGE_EXTERNAL_STORAGE};

        String[] Permissions2 = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        String[] perm ;
        if (SDK_INT >= Build.VERSION_CODES.R){
            perm = Permissions;
        }else {
            perm = Permissions2;
        }

        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(perm)
                .check();




    }catch (Exception ex){
        ex.printStackTrace();
        PrinterExceptions.appendLog("Exception in set Settings "+ex.getMessage());
    }*/

}