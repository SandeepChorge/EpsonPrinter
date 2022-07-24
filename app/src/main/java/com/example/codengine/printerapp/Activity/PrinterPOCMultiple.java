package com.example.codengine.printerapp.Activity;


import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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
    CountDownTimer countDownTimer;
    EditText prefix;

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
        if (prefix.getText().toString().isEmpty()){
            res = "Tst_"+res;
        }else {
            res = prefix.getText().toString()+"_"+res;
        }

        return res;
    }
    SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public ArrayList<MyPrinter> printers;
    public ArrayList<Task> tasks;

    private void printDetail(){
        makeLog("NAME "+Thread.currentThread().getId()
                +"\t ID "+Thread.currentThread().getName()
                +"\t GRP "+Thread.currentThread().getThreadGroup().toString()
                +"\t GRP_NM "+Thread.currentThread().getThreadGroup().getName()
                +"\t PARENT "+Thread.currentThread().getThreadGroup().getParent().getName());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiple);
        init();

        Log.e("CORE "+Manager.CORE_POOL_SIZE,"\tMAX "+Manager.MAX_POOL_SIZE);
            printers = new ArrayList<>();
            tasks = new ArrayList<>();
        countDownTimer = new CountDownTimer(1200000,2000) {
            @Override
            public void onTick(long l) {
                makeLog("ACTIVIE THREADS "+Manager.getManagerInstance().getActivaionCount());
            }

            @Override
            public void onFinish() {

            }
        };
        countDownTimer.start();
        PrinterExceptions.context = this;


        //PrinterExceptions.appendLog("CORE_POOL_SIZE: "+Manager.CORE_POOL_SIZE+"\t MAX_POOL_SIZE: "+Manager.MAX_POOL_SIZE);

       /* makeLog("INSIDE onCreate");
        printDetail();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Thread.sleep(3000);
                    printDetail();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            printDetail();
                        }
                    }).start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void finalize() throws Throwable {
                super.finalize();
            }
        }).start();*/




        try {
            com.epson.epos2.Log.setLogSettings(context, com.epson.epos2.Log.PERIOD_PERMANENT, com.epson.epos2.Log.OUTPUT_STORAGE, null, 0, 50, com.epson.epos2.Log.LOGLEVEL_LOW);
        } catch (Epos2Exception e) {
            Log.d("PrintL", e.getLocalizedMessage());
            e.printStackTrace();
        }

       // Log.e("SDK INT ","-->"+SDK_INT);
     //   printThreadDetails("OnCreateMethod");



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

                        printers.add(pritner1);

                        Task task = new Task(pritner1);
                        tasks.add(task);
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

                            printers.add(pritner2);
                            Task task2 = new Task(pritner2);
                            tasks.add(task2);
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

                            printers.add(pritner3);
                            Task task3 = new Task(pritner3);
                            tasks.add(task3);
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

                            printers.add(pritner4);
                            Task task4 = new Task(pritner4);
                            tasks.add(task4);
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

        prefix = (EditText) findViewById(R.id.prefix);
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

        initDiscover();

        findViewById(R.id.shareData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {


                    File folder =  context.getExternalCacheDir();
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
                       Toast.makeText(context,"FILE NOT FOUND ",Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception ex){ex.printStackTrace();}
            }
        });
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
                    String res= resultTv.getText().toString();
                    res = res+"\n"+msg;
                    resultTv.setText(res);

                }catch (Exception ex){
                    makeLog("EXCEPTIONIN makeToastCallback-- Msg "+msg);
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onMyPrinterCallback(String printerName, PrinterEvents printerEvents) {
        try {

        if (printerEvents!=null) {
            if (printerEvents.getToastMsg().equalsIgnoreCase("KILL") &&
            printerEvents.getPrinter()!=null){
                int index = printers.indexOf(printerEvents.getPrinter());

                if (index>-1 && index<printers.size()){
                    printers.get(index).callFinalize();
                    tasks.get(index).killTask();
                    printers.remove(index);
                    tasks.remove(index);

                }
                 makeEntry(printerName,"printers size "+printers.size()+"\ttasks size "+tasks.size());
            }else if(printerEvents.getToastMsg().equalsIgnoreCase("SUCCESS")){

                String str = resultTv.getText().toString();
                resultTv.setText(str+"\n"+printerName+" SUCCESS");

            }
        }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void makeEntry(String printerName,String msg){
        String str = Thread.currentThread().getId()+", "
                +Thread.currentThread().getName()+", "
                +printerName+", "
                +"onMyPrinterCallback,"
                +","
                +msg;

        PrinterExceptions.appendLog(str);
    }

    private void makeLog(String msg){
        Log.e("PrinterPOCMultiple","==> "+msg);
    }

    @Override
    protected void onDestroy() {
        countDownTimer.cancel();
        Manager.getManagerInstance().ShutDownThreadPool();
        super.onDestroy();
    }
}