package contrual;

import org.apache.log4j.Logger;
import pojo.Data;
import pojo.Status;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by jingbao on 18-11-12.
 */
public class Install {
    private static Logger log = Logger.getLogger(Install.class);
    public static String path="/home/jingbao/桌面/shell/install.sh";
    public static Status doInstall(Data data){
        Status status=new Status();
        String name=data.getData();
        String dockerid=data.getDockerId();
        try {
            //Process ps=Runtime.getRuntime().exec(path);
            Process ps = Runtime.getRuntime().exec(new String[]{path,
                    dockerid,name});
            ps.waitFor();
            //读取输入流
            // ps = Runtime.getRuntime().exec(new String[]{"这是传入的参数"});
            InputStream in = ps.getInputStream();
            InputStream error = ps.getErrorStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(error));;

            if(error.available() != 0){
                status.setStatus("0");
            }else {
                status.setStatus("1");
                status.setData("install success");
                return status;
            }
            StringBuffer result=new StringBuffer();
            String line="";
            while ((line=reader.readLine())!=null){
                result.append(line);
            }
            status.setData("install fail");
            System.out.println("这是返回值:"+result);
            reader.close();
        } catch (IOException e) {
            log.error("Install doInstall:"+System.currentTimeMillis(),e);
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return status;

    }
}
