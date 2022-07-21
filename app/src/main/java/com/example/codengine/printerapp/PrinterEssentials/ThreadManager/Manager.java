package com.example.codengine.printerapp.PrinterEssentials.ThreadManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Manager {


    public static int CORE_POOL_SIZE = 5;
    public static int MAX_POOL_SIZE = 10;
    private static final int KEEP_ALIVE_TIME = 30;

    private static Manager managerInstance = null;

    //Queue for all the Tasks
    final BlockingQueue<Runnable> WorkQueue;
    private final ThreadPoolExecutor threadPoolExecutor;

    static {

        /*
        Static instance of Manager
         */
        managerInstance = new Manager();

    }

    /*
    Make sure Manager is a SingleTon Hence private;
     */

    private Manager() {

        WorkQueue = new LinkedBlockingQueue<Runnable>();
        threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.MILLISECONDS, WorkQueue);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
    }

    public void runTask(Runnable runnable) {
        threadPoolExecutor.execute(runnable);

    }

    public static Manager getManagerInstance() {
        return managerInstance;
    }
}
