package contrual;

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
            BufferedReader reader ;

            if(in.available() != 0){
                status.setStatus("1");
                reader = new BufferedReader(new InputStreamReader(in));
            }else {
                status.setStatus("0");
                reader = new BufferedReader(new InputStreamReader(error));
            }
            StringBuffer result=new StringBuffer();
            String line="";
            while ((line=reader.readLine())!=null){
                result.append(line+"\n");
            }
            status.setData(result.toString());
            System.out.println("这是返回值:"+result);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return status;

    }
}
