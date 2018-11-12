package com.jingbao;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GetObjectRequest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, InterruptedException {
        Process process=Runtime.getRuntime().exec(new String[]
                {"/home/jingbao/桌面/saveProject.sh",
                        "/home/jingbao/桌面/dataHubTest/c.py",
                        "/home/jingbao/桌面/dataHubTest/a.py",
                "/home/jingbao/桌面/dataHubTest/q.py"},null,null);
        BufferedReader read=new BufferedReader(new InputStreamReader(process
                .getErrorStream()));
        process.waitFor();
        String res="";
        String line="";
        while ((line=read.readLine())!=null){
            res=res+line;
            System.out.println(res);
        }
        if (line==null||line.equals("")){
            System.out.println("OSS");
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

        }else {
            System.out.println("ERROR");
        }
    }
}
