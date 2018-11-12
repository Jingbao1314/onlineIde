package contrual;

import pojo.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by jingbao on 18-11-9.
 */
public class StopDocker {
    public static void stop(Data data) throws IOException, InterruptedException {
        Process process=Runtime.getRuntime().exec(new String[]
                {"/home/jingbao/桌面/dockerStop.sh",
                        data.getDockerId()},null,null);
        BufferedReader read=new BufferedReader(new InputStreamReader(process
                .getInputStream()));
        process.waitFor();
        String res="";
        String line="";
        while ((line=read.readLine())!=null){
            res=res+line;
        }
        System.out.println(res);
    }
}
