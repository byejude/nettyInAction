package server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TimeServerHandleExecutePool {
    private ExecutorService executorService;

    public TimeServerHandleExecutePool(int maxPoolSize,int queueSize) {
        //ThreadPoolExecutor 参数为核心线程数 最大线程数 非核心线程的闲置超时时间  指定keepAliveTime的单位 线程池中的任务队列
        executorService = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), maxPoolSize, 120L,
                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueSize));
    }
    public void execute(java.lang.Runnable task){
            executorService.execute(task);
        }
    }

