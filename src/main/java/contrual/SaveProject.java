package contrual;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import pojo.Data;
import pojo.DockerFile;
import pojo.Status;
import utils.OssUtil;
import utils.RedisOperating;

import java.io.File;
import java.io.IOException;

import static contrual.StartDocker.createDocker;

/**
 * Created by jingbao on 18-11-6.
 */
public class SaveProject {
    private static Logger log = Logger.getLogger(SaveProject.class);
    public SaveProject(){}
    public static Status doSave(Data data) throws IOException,
            InterruptedException {
//        System.out.println(new Gson().toJson(data));
        Status status=null;
        String dcokerFile=data.getData();
        DockerFile[] list=new Gson().fromJson(dcokerFile,DockerFile[]
                .class);
        if (list==null|dcokerFile==""|dcokerFile==null){

        } else{
            String [] filesList=new String[list.length];
            for (int i=0;i<list.length;i++) {
                filesList[i]=list[i].getFileUrl();
            }
            RedisOperating op=new RedisOperating();
//            if (op.exists(data.getMac())){
//            }else {
//                createDocker(data);
//            }
            for (String str:filesList) {
                status=save("/home/jingbao/桌面/"+data.getMac()+"/"+str,"");
            }
            OssUtil.load(filesList,data.getMac());
        }
//        StopThread stopThread=new StopThread(data);
//        WorkThreadPool.doWork(stopThread);
        return status;
//        fileUrls[0]="/home/jingbao/桌面/saveProject.sh";
//        Process process=Runtime.getRuntime().exec(fileUrls,null,null);
//        BufferedReader read=new BufferedReader(new InputStreamReader(process
//                .getInputStream()));
//        BufferedReader error=new BufferedReader(new InputStreamReader(process
//                .getErrorStream()));
//        process.waitFor();
//        String res="";
//        String line="";
//        while ((line=error.readLine())!=null){
//            res=res+line;
//        }
//        if (line==null||line.equals("")){
//            System.out.println("OSS");
//        }else {
//            System.out.println("ERROR");
//        }
////        System.out.println(res);
//        error.close();
//        read.close();
    }

    public static Status save(String path,String ProjectName) throws IOException {
        Status status=new Status();
        File files=new File(path);
        if (files.exists()){
            status.setStatus("1");
            System.out.println("存在");
        }else {
            String filePath="/";
            String[] list=path.split("[/]");
            for (int i=0;i<list.length-1;i++) {
                if (i==(list.length-2)){
                    filePath=filePath+list[i];
                }else {
                    filePath=filePath+list[i]+"/";
                }
            }
            File file=new File(filePath);
            if (!file.exists()){
                if (file.mkdirs()){
                    System.out.println("OK");
                    status.setStatus("1");
                }else {
                    System.out.println("BAD");
                    status.setStatus("0");
                    return status;
                }
            }
           if ( files.createNewFile()){
               System.out.println("成功");
               status.setStatus("1");
           }else {
               System.out.println("失败");
               status.setStatus("0");
               return status;
           }
            System.out.println("创建");
        }
//        load();

        return status;
    }


    public static void main(String[] args) throws IOException {
//        SaveProject.save("/home/jingbao/桌面/pachong3/src/jdbc/test.txt","");
    }
}
