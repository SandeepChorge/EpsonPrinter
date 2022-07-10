package com.example.codengine.printerapp;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.epson.epos2.Epos2Exception;
import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;

public class MainActivity extends AppCompatActivity {
    String ERR = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        discoverPrinter();
    }

    private void discoverPrinter() {

        try {
            makeLog("Discovering PRinter");
            FilterOption filterOption = new FilterOption();
            filterOption.setPortType(Discovery.PORTTYPE_TCP);
            filterOption.setDeviceModel(Discovery.MODEL_ALL);
            filterOption.setEpsonFilter(Discovery.FILTER_NAME);
            filterOption.setDeviceType(Discovery.TYPE_PRINTER);
            Discovery.start(MainActivity.this, filterOption, new DiscoveryListener() {
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

                            }
                        } else {
                            makeLog("DeviceInfo in disconvery is null");
                        }


                        if (gotDetails) {
                             ERR = "TARGET: " + deviceInfo.getTarget() + "\n" +
                                    "NAME: " + deviceInfo.getDeviceName() + "\n" +
                                    "IPADDRESS: " + deviceInfo.getIpAddress() + "\n" +
                                    "MAC ADDR: " + deviceInfo.getMacAddress();
                            //Discovery.stop();
                        } else {
                            ERR = "GOT NO PRINTER DETAILS";
                        }
                        makeLog(ERR);
                        //retryPrinterConnection(RetryEnum.SHOW_ERROR_POPUP, ERR);
                    } /*catch (Epos2Exception e) {
                        ERR = "DISCOVERY_ERR_2 : " + getDiscoveryException(e);
                        makeLog(ERR);

                    }*/ catch (Exception e) {
                        ERR = "DISCOVERY_ERR_3 : " + e.getMessage();
                        e.printStackTrace();
                        makeLog(ERR);

                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            makeLog("Exception in discover "+e.getMessage());
        }
    }
    private void makeLog(String msg) {
        Log.e("--> ","--> "+msg);
    }

    public String getDiscoveryException(Epos2Exception e){
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
        //makeLog(result);
        return result;
    }
}

