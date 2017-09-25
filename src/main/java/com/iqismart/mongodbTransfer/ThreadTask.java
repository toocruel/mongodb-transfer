/************************************************************************************
 * Create at 2017/1/19
 *
 * Author:song.ty
 *
 *************************************************************************************/

package com.iqismart.mongodbTransfer;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 线程任务
 *
 * @author song.ty
 * @create 2017-01-19-16:00
 **/
public class ThreadTask {

    /**
     * 商品相关线程池
     * solr 同步
     */
    public static Executor THREADPOOL_DEFAULT = Executors.newFixedThreadPool(100);



    private ThreadTask() {

    }

    private  static ThreadTask instance = null;
    public static ThreadTask getInstance(){
        if(instance == null){
            instance = new ThreadTask();
        }
        return instance;
    }

    /**
     * 默认线程池 执行任务
     * @param runnable
     */
    public void run(Runnable runnable){
        try {
            THREADPOOL_DEFAULT.execute(runnable);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 指定线程池 执行任务
     * @param executor
     * @param runnable
     */
    public void run(Executor executor,Runnable runnable){
        try {
            executor.execute(runnable);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
