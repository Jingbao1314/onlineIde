package contrual;

import com.alibaba.fastjson.JSONObject;
import pojo.Data;
import pojo.Status;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
        System.out.println(alterPath+"****************************"+path);
        Data newData=new Data();
        newData.setDockerId(data.getDockerId());
        newData.setMac(data.getMac());
        newData.setData(alterPath);
        SaveProject.doSave(newData);
//        Runtime.getRuntime().exec(new
//                String[]{"/home/jingbao/桌面/shell/start.sh",data.getDockerId()});
        Process pro = Runtime.getRuntime().exec(new
                String[]{"/home/jingbao/桌面/shell/run.sh",data.getDockerId(),
                "/home/"+data.getMac()+path});
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
}
