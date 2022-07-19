package com.example.codengine.printerapp.PrinterEssentials;

import android.Manifest;
import android.app.Activity;
import android.os.CountDownTimer;
import android.widget.Toast;

import com.epson.epos2.Epos2Exception;
import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DiscoverPrinter {

    private static boolean isDiscoveryStarted = false;
    private static boolean isFoundIP = false;

    private String ERR = "";
    private String TAG = "DISVOERY_PRINT";
    Activity context;
    final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

    public static ArrayList<DeviceInfo> availableDevices = new ArrayList<>();
    private static String updatedTimeStamp = null;
    CountDownTimer countDownTimer;
    DiscoveryResults discoveryResults;

    public DiscoverPrinter(Activity context,DiscoveryResults discoveryResults){
        this.context = context;
        this.discoveryResults = discoveryResults;

        countDownTimer = new CountDownTimer(10000,1000) {
            @Override
            public void onTick(long l) {
                long res = l/1000;
                makeLog("COUNTER_ON_TICK "+res);
              /*  if (res == 3){
                    makeLog("STOPPING DISCOVERY"+res);
                    DiscoveryStop();
                }
*/
                if (res == 0 && isDiscoveryStarted){//isFoundIP ||
                    makeLog("STOPPING ON COUNTER TICK 0 ");
                    //isFoundIP = false;
                    if (isDiscoveryStarted){
                        makeLog("COUNTER_ON_FINISH : STOPPING DISCOVERY");
                        DiscoveryStop();
                    }else {
                        makeLog("COUNTER_ON_FINISH : NO NEED TO STOP DISCOVERY");
                    }
                }
            }

            @Override
            public void onFinish() {

            }
        };
    }

    public static boolean isIsDiscoveryStarted() {
        return isDiscoveryStarted;
    }

    public void DiscoveryStop(){
        try {
            if (isDiscoveryStarted){
                Discovery.stop();
               /* if (discoveryResults!=null){
                    discoveryResults.onDiscoveryResults(isDiscoveryStarted, DiscoveryEssentials.DisEventTypeEnum.DEVICEINFO,ERR);
                }*/
                isDiscoveryStarted = false;
                context.runOnUiThread(new Runnable() {
                    @Override
                    public synchronized void run() {
                        if (countDownTimer!=null){
                            makeLog("CANCELLING COUNTDOWN TIMER");
                            countDownTimer.cancel();
                        }
                    }
                });
                makeLog("DISCOVERY STOPPED");


                triggerCallback(DisEventEnum.STOPPED,"DISCOVERY STOPPED",false);


            }else {
                makeLog("DISCOVERY WAS NOT STOPPED BECAUSE NEVER STARTED");
            }
        }catch (Epos2Exception e){
            isDiscoveryStarted = false;
            ERR = "DISCOVERY_STOP_ERR : "+PrinterExceptions.getDiscoveryStopException(TAG,e);
            reportException(ERR,context);

            triggerCallback(DisEventEnum.STOPPED,ERR,true);

        }catch (Exception e){
            isDiscoveryStarted = false;
            ERR = "DISCOVERY_STOP_ERR_2 : "+e.getMessage();
            e.printStackTrace();
            reportException(ERR,context);
            triggerCallback(DisEventEnum.STOPPED,ERR,true);

        }
    }

    public void DisoveryStart(String ipAddresToFound){
        //printThreadDetails("discoverPrinter");
        if (!isDiscoveryStarted) {


            try {

                countDownTimer.start();
                makeLog("DISCOVERY STARTED");
                FilterOption filterOption = new FilterOption();
                filterOption.setPortType(Discovery.PORTTYPE_TCP);
                filterOption.setDeviceModel(Discovery.MODEL_ALL);
                filterOption.setEpsonFilter(Discovery.FILTER_NAME);
                filterOption.setDeviceType(Discovery.TYPE_PRINTER);
                isDiscoveryStarted = true;

                Discovery.start(context, filterOption, new DiscoveryListener() {
                    @Override
                    public void onDiscovery(DeviceInfo deviceInfo) {
                        try {

                            boolean gotDetails = false;
                            if (deviceInfo != null) {
                                if (deviceInfo.getDeviceType() == Discovery.TYPE_PRINTER) {
                                    gotDetails = true;
                                    deviceInfo.getTarget();
                                    deviceInfo.getDeviceName();
                                    deviceInfo.getIpAddress();

                                    availableDevices.add(deviceInfo);
                                    updatedTimeStamp = getCurrentDate(DATE_FORMAT);
                                }
                            } else {
                                makeLog("DeviceInfo in disconvery is null");
                            }

                            if (gotDetails) {
                                ERR = //"TARGET: " + deviceInfo.getTarget() + "\t" +
                                        "NAME: " + deviceInfo.getDeviceName() + "\t" +
                                        "IPADDRESS: " + deviceInfo.getIpAddress() + "\t" +
                                        "TIME "+updatedTimeStamp;
                                        //"MAC ADDR: " + deviceInfo.getMacAddress();


                                triggerCallback(DisEventEnum.DEVICEINFO,ERR,false);
                                if (ipAddresToFound!=null && !ipAddresToFound.isEmpty() && ipAddresToFound.equalsIgnoreCase(deviceInfo.getIpAddress())){
                                    makeLog("FOUND IP LOOKING FOR CALLING DISCOVERY_STOP");//
                                    context.runOnUiThread(new Runnable() {
                                        @Override
                                        public synchronized void run() {
                                            DiscoveryStop();
                                        }
                                    });

                                    //isFoundIP = true;
                                }
                            } else {
                                ERR = "GOT NO PRINTER DETAILS";
                            }
                            makeLog(ERR);
                        }/*catch (Epos2Exception e){
                        isDiscoveryStarted = false;
                        ERR = "ON_DISCOVERY_ERR : "+PrinterExceptions.getDiscoveryException(TAG,e);
                        reportException(ERR,context);
                    }*/ catch (Exception e) {
                            isDiscoveryStarted = false;
                            ERR = "ON_DISCOVERY_ERR_2 : " + e.getMessage();
                            e.printStackTrace();
                            reportException(ERR, context);

                            triggerCallback(DisEventEnum.DEVICEINFO,ERR,true);
                        }

                    }
                });

                triggerCallback(DisEventEnum.STARTED,"DISCOVERY_STARTED",false);

            } catch (Epos2Exception e) {
                ERR = "DISCOVERY_START_ERR : " + PrinterExceptions.getDiscoveryException(TAG, e);
                reportException(ERR, context);
                isDiscoveryStarted = false;
                triggerCallback(DisEventEnum.STARTED,ERR,true);


            } catch (Exception ex) {
                ex.printStackTrace();
                ERR = "DISCOVERY_START_ERR_2 : " + ex.getMessage();
                reportException(ERR, context);
                isDiscoveryStarted = false;
                triggerCallback(DisEventEnum.STARTED,ERR,true);

            }
        }else {
            reportException("DISCOVERY_START_3: DISCOVERY IS ALREADY RUNNING",context);
            triggerCallback(DisEventEnum.STARTED,ERR,false);
        }
    }

    private void reportException(String err, Activity context) {
        context.runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                makeLog(err);
                Toast.makeText(context, ""+err, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void makeLog(String discovering_pRinter) {
        //Log.e("DISCOVER_PRINTER ","- "+discovering_pRinter);
        discovering_pRinter = "DIS_PRIN "+discovering_pRinter;
        PrinterExceptions.appendLog(discovering_pRinter);
    }

    private void triggerCallback(DisEventEnum eventEnum, String result, boolean isError){
        if (discoveryResults!=null) {
            DiscoveryEvents event = new DiscoveryEvents();
            event.eventEnum = eventEnum;
            event.isDiscovering = isDiscoveryStarted;
            event.result = result;
            event.iserror = isError;
            discoveryResults.onDiscoveryResults(event);
        }
    }

    /**
     * Get today's date in any format.
     *
     * @param dateFormat pass format for get like: "yyyy-MM-dd hh:mm:ss"
     * @return current date in string format
     */
    public String getCurrentDate(String dateFormat) {
        Date d = new Date();
        return new SimpleDateFormat(dateFormat, Locale.US).format(d.getTime());
    }

    public interface DiscoveryResults
    {
        public void onDiscoveryResults(DiscoveryEvents discoveryEvent);
    }
}
