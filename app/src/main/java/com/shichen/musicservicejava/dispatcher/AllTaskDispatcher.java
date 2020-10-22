package com.shichen.musicservicejava.dispatcher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author shichen 754314442@qq.com
 * Created by shichen on 2020/4/4.
 */
public class AllTaskDispatcher {
    private static AllTaskDispatcher INSTANCE;
    private ExecutorService diskExecutor;//执行数据库读写操作
    private ExecutorService netExecutor;//执行网络耗时操作
    private ExecutorService ioExecutor;//执行文件，计算等耗时操作
    private AllTaskDispatcher(){
        diskExecutor= Executors.newCachedThreadPool();
        netExecutor=Executors.newCachedThreadPool();
        ioExecutor =Executors.newCachedThreadPool();
    }
    public static AllTaskDispatcher getInstance(){
        if (INSTANCE==null){
            synchronized (AllTaskDispatcher.class){
                if (INSTANCE==null){
                    INSTANCE=new AllTaskDispatcher();
                }
            }
        }
        return INSTANCE;
    }

    public ExecutorService diskExecutor() {
        return diskExecutor;
    }

    public ExecutorService netExecutor() {
        return netExecutor;
    }

    public ExecutorService ioExecutor() {
        return ioExecutor;
    }
}
