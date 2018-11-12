package Servlets;


import contrual.SaveProject;
import pojo.Data;
import pojo.Status;
import utils.RedisOperating;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by jingbao on 18-11-1.
 */
public class MainServlet {
    public static Status doServlet(Data data,String url) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Status status=new Status();
        String[] urls=url.split("[/]");
        switch (urls[1]){
            case "SaveProject":
                System.out.println("SaveProject Start");
                Class cls = Class.forName("contrual."+urls[1]);
                java.lang.reflect.Constructor constructor=cls.getConstructor();
                SaveProject save= (SaveProject) constructor.newInstance();
                Method doSave=cls.getMethod(urls[2], Data.class);
                status= (Status) doSave.invoke(save,data);
        }
        return status;

    }

    public static void main(String[] args) {
        RedisOperating operating=new RedisOperating();
        operating.del("MacTest");
    }
}
