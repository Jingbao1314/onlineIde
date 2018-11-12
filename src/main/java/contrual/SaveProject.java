package contrual;

import Server.WorkThreadPool;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GetObjectRequest;
import com.google.gson.Gson;
import pojo.Data;
import pojo.DockerFile;
import pojo.Status;
import utils.RedisOperating;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import static contrual.StartDocker.createDocker;

/**
 * Created by jingbao on 18-11-6.
 */
public class SaveProject {
    public SaveProject(){}
    public static Status doSave(Data data) throws IOException,
            InterruptedException {
        String dcokerFile=data.getData();
        DockerFile[] list=new Gson().fromJson(dcokerFile,DockerFile[]
                .class);
        String [] filesList=new String[list.length];
        for (int i=0;i<list.length;i++) {
            filesList[i]=list[i].getFileUrl();
        }
        Status status=null;
        RedisOperating op=new RedisOperating();
        if (op.exists(data.getMac())){

        }else {
            createDocker(data);
        }
        for (String str:filesList) {
            status=save("/home/jingbao/桌面/"+data.getMac()+str,"");
        }
        StopThread stopThread=new StopThread(data);
        WorkThreadPool.doWork(stopThread);
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

    public static void load(){
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = "http://oss-cn-beijing.aliyuncs.com/";
// 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
        String accessKeyId = "LTAIsv2R1KV4srqB";
        String accessKeySecret = "LY5iLLre4MDkHDapdC4k3yC0FqSWNZ";
        String bucketName = "new---server";
        String objectName = "a.py";

// 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);

// 下载OSS文件到本地文件。如果指定的本地文件存在会覆盖，不存在则新建。
        ossClient.getObject(new GetObjectRequest(bucketName, objectName),
                new File("/home/jingbao/桌面/dataHubTest/a.py"));

// 关闭OSSClient。
        ossClient.shutdown();

    }

    public static void main(String[] args) throws IOException {
//        SaveProject.save("/home/jingbao/桌面/pachong3/src/jdbc/test.txt","");
    }
}
