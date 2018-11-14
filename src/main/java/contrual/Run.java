package contrual;

import Server.DebugRunable;
import Server.WorkThreadPool;
import com.alibaba.fastjson.JSONObject;
import pojo.Data;
import pojo.Status;
import utils.CustomSystemUtil;
import utils.RedisOperating;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;

/**
 * Created by jingbao on 18-11-12.
 */
public class Run {
    public static Status doRun(Data data) throws IOException, InterruptedException {
        System.out.println
                ("doRun---------------------------------------------------");
        Status status=new Status();
        JSONObject json = JSONObject.parseObject(data.getData());
        String path = json.getString("path");//主函数路径
        String alterPath = json.getString("alterList");//修改过的路径
        String lineNum=json.getString("lineNum");

        System.out.println(lineNum+"/*/*/*/-/*/*-/-*/*/*/*/*-/-*/*/-*-/*/*/*-");
        Data newData=new Data();
        newData.setDockerId(data.getDockerId());
        newData.setMac(data.getMac());
        newData.setData(alterPath);
        SaveProject.doSave(newData);//-------------后期优化-----------------------
        if (lineNum==null){
            path="/home/"+data.getMac()+path;
            status=run(path,data);
        }else {
//            String debugPath="/home/"+data.getMac()+"/debug"+path;
//            status=debug(path,data,lineNum,debugPath);//"/home/"+data.getMac()
            // +path
        }

        return status;
    }


    public static Status debug(String path,Data data,String lineNum,String debugPath)
            throws
            IOException, InterruptedException {
        Status status=new Status();
        addPoint(path,lineNum);
        DebugRunable runable=new DebugRunable(path,data);
        WorkThreadPool.doWork(runable);
        RedisOperating op=new RedisOperating();
        String port=op.get(data.getMac()+"_port");

        status.setStatus("1");
        status.setData("http://"+ CustomSystemUtil.INTRANET_IP+":"+port);
        return status;
    }

    public static void addPoint(String path,String lineNum){




    }




    public static Status run(String path,Data data) throws
            IOException,
            InterruptedException {
        Status status=new Status();
//        Runtime.getRuntime().exec(new
//                String[]{"/home/jingbao/桌面/shell/start.sh",data.getDockerId()});
        Process pro = Runtime.getRuntime().exec(new
                String[]{"/home/jingbao/桌面/shell/run.sh",data.getDockerId(),
                path});//"/home/"+data.getMac()+path
        pro.waitFor();
        InputStream is = pro.getInputStream();
        InputStream error = pro.getErrorStream();
        BufferedReader reader ;

        if(is.available() != 0){
            reader = new BufferedReader(new InputStreamReader(is));
            status.setStatus("1");
        }else {
            reader = new BufferedReader(new InputStreamReader(error));
            status.setStatus("0");
        }
        String result="";
        String line="";
        while ((line=reader.readLine())!=null){
            result+=line+"\n";
        }
        status.setData(result);
        System.out.println("Run ok--------------------------------------"+result);
        return status;

    }

    public static void main(String[] args) {
        System.out.println("http://"+ CustomSystemUtil.INTRANET_IP+":"+7721);
    }
}
