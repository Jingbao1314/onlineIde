package Server;

import java.util.concurrent.*;

/**
 * Created by jingbao on 18-8-14.
 */
public class WorkThreadPool {
    public static ExecutorService pool= Executors.newCachedThreadPool();
    public static void doWork(Runnable runnable){
        pool.execute(runnable);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
       for (int i=0;i<3;i++){
           Callable callable=new Callable() {
               @Override
               public Object call() throws Exception {
                   return "7721";
               }
           };
           Future<Object> res=pool.submit(callable);
           System.out.println(res.get());

       }
        pool.shutdown();
    }
}
