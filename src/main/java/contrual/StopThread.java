package contrual;

import pojo.Data;

import java.io.IOException;

/**
 * Created by jingbao on 18-11-9.
 */
public class StopThread implements Runnable{
    private Data data=null;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public StopThread(Data data) {
        this.data = data;
    }
    public StopThread(){}

    @Override
    public void run() {
        try {
            StopDocker.stop(data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
