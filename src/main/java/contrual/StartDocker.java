package contrual;

import org.apache.log4j.Logger;
import pojo.Data;
import pojo.Status;
import utils.RedisOperating;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by jingbao on 18-11-7.
 */
public class StartDocker {
    public static void main(String[] args) throws IOException, InterruptedException {

//        Process process=Runtime.getRuntime().exec(new String[]
//                {"/home/jingbao/桌面/startDocker.sh",
//                        "/home/jingbao/桌面/MAC",
//                        "/data"},null,null);
//        BufferedReader read=new BufferedReader(new InputStreamReader(process
//                .getInputStream()));
//        process.waitFor();
//        String res="";
//        String line="";
//        while ((line=read.readLine())!=null){
//            res=res+line;
//        }
        Data data=new Data();
        data.setMac("qqqq");
        createDocker(data);

    }
    private static Logger log = Logger.getLogger(StartDocker.class);


    public static String exitDocker(Data data){
        String dockerId="";
        RedisOperating op=new RedisOperating();
        if (op.exists(data.getMac()+"_docker")){
            dockerId=op.get(data.getMac()+"_docker");
        }else {
            try {
                dockerId=createDocker(data).getDockerId();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return dockerId;

    }
    public static Data createDocker(Data data) throws IOException, InterruptedException {
        File file=new File("/home/jingbao/桌面/"+data.getMac());
       if (!file.exists()){
           file.mkdir();
       }
        RedisOperating operating=new RedisOperating();
        if (data.getDockerId()==null||data.equals("")){
            Process process=Runtime.getRuntime().exec(new String[]
                    {"/home/jingbao/桌面/shell/startDocker.sh","7777",
                            "/home/jingbao/桌面/"+data.getMac(),
                            "/home"},null,null);
            BufferedReader read=new BufferedReader(new InputStreamReader(process
                    .getInputStream()));
            process.waitFor();
            String res="";
            String line="";
            while ((line=read.readLine())!=null){
                res=res+line;
            }
            System.out.println();
            if (res==""||res.equals("")){
            }else {
                String[] id=res.split("[  ]");
                operating.set(data.getMac()+"_docker",id[8]);
                data.setDockerId(id[8]);
            }
        }
        return data;
    }
}
