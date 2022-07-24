package com.example.codengine.printerapp.Activity;

public class CommentedCode {


    //==========================================================================================================================
    //            COOMENTED CODE FROM PRINTER_POC_MULTIPLE_ACTIVITY
    //==========================================================================================================================

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
        });

          DISCOVER discoverBtn.setOnClickListener(new View.OnClickListener() {
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
        });

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

    }


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


    //==========================================================================================================================
}
