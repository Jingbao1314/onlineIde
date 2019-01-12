package Servlets;


import com.google.gson.Gson;
import contrual.*;
import pojo.Data;
import pojo.Message;
import pojo.Status;
import utils.RedisOperating;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by jingbao on 18-11-1.
 */
public class MainServlet {
    public static Status doServlet(Data data,String url) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Status status=new Status();
        String[] urls=url.split("[/]");
        Class cls=null;
        java.lang.reflect.Constructor constructor=null;
        switch (urls[1]){
            case "SaveProject":
                System.out.println("SaveProject Start");
                cls = Class.forName("contrual."+urls[1]);
                constructor=cls.getConstructor();
                SaveProject save= (SaveProject) constructor.newInstance();
                Method doSave=cls.getMethod(urls[2], Data.class);
                status= (Status) doSave.invoke(save,data);
                break;


            case "Run":
                cls = Class.forName("contrual."+urls[1]);
                constructor=cls.getConstructor();
                Run run= (Run) constructor.newInstance();
                Method doRun=cls.getMethod(urls[2], Data.class);
                status= (Status) doRun.invoke(run,data);
                break;

            case "Install":
                cls = Class.forName("contrual."+urls[1]);
                constructor=cls.getConstructor();
                Install install= (Install) constructor.newInstance();
                Method doIntall=cls.getMethod(urls[2], Data.class);
                status= (Status) doIntall.invoke(install,data);
                break;

            case "Open":
                cls = Class.forName("contrual."+urls[1]);
                constructor=cls.getConstructor();
                Open open= (Open) constructor.newInstance();
                Method doOpen=cls.getMethod(urls[2], Data.class);
                status= (Status) doOpen.invoke(open,data);
                break;

            case "Destory":
                cls = Class.forName("contrual."+urls[1]);
                constructor=cls.getConstructor();
                Destory destory= (Destory) constructor.newInstance();
                Method doDestory=cls.getMethod(urls[2], Data.class);
                status= (Status) doDestory.invoke(destory,data);
                break;

            case "Create":
                cls = Class.forName("contrual."+urls[1]);
                constructor=cls.getConstructor();
                Create create= (Create) constructor.newInstance();
                Method doCreate=cls.getMethod(urls[2], Data.class);
                status= (Status) doCreate.invoke(create,data);
                break;
        }
        return status;
    }
    public static void exitFile(String path){
        System.out.println(path+"--+-+-+-+++++++++++++++++++++++++++++++++++");
        File file=new File(path);
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Message m=new Message();
        m.setData("xxxx");
        Gson gson=new Gson();
        System.out.println(gson.toJson(m));
    }
}
