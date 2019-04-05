package contrual;

import RabbitServer.GlobalInput;
import RabbitServer.InputConsume;
import RabbitServer.MessageProduct;
import Server.Server;
import WorkerRunables.DebugRunable;
import WorkerRunables.ProcessRunable;
import WorkerRunables.WorkThreadPool;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import pojo.Data;
import pojo.Status;
import utils.AddPointUtil;
import utils.CustomSystemUtil;
import utils.FileUtils;
import utils.RedisOperating;
import websocketServer.Global;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by jingbao on 18-11-12.
 */
public class Run {
    private static Logger log = Logger.getLogger(Run.class);
    public static Status doRun(Data data) throws IOException, InterruptedException, TimeoutException {
        System.out.println
                ("doRun---------------------------------------------------");
        Status status=null;
        JSONObject json = JSONObject.parseObject(data.getData());
        String path = json.getString("path");//主函数路径
        String alterPath = json.getString("alterList");//修改过的路径
        String lineNum=json.getString("lineNum");
        System.out.println(lineNum+"----------------------LINENUM------------");
        Data newData=new Data();
        newData.setDockerId(data.getDockerId());
        newData.setMac(data.getMac());
        newData.setData(alterPath);
        SaveProject.doSave(newData);//-------------后期优化-----------------------
        if (lineNum==null){
            if(exitInput(path,data.getMac())){
                path="/home/"+data.getMac()+"_input.py";
                status=inputRun(path,data);
            }else {
                path="/home"+"/"+path;
                status=run(path,data);
            }

        }else {
            String debugPath="/home/jingbao/桌面/"+data.getMac()+"/"+data.getMac()
                    +"_debug"+path;
            status=debug(path,data,lineNum,debugPath);//"/home/"+data.getMac()
            // +path
        }
        return status;
    }


    public static Status debug(String path,Data data,String lineNum,String debugPath)
            throws
            IOException, InterruptedException {
        Status status=new Status();
        String filePath="/home/jingbao/桌面/"+data.getMac()+path;
        addPoint(filePath,lineNum,debugPath);
        path="/home/"+data.getMac()+"_debug"+path;
        DebugRunable runable=new DebugRunable(path,data);///home/jingbao/桌面/MAC/xxx.py
        WorkThreadPool.doWork(runable);
        RedisOperating op=new RedisOperating();
        String port="7777";//op.get(data.getMac()+"_port")
        status.setStatus("1");
        status.setData("http://"+ CustomSystemUtil.INTRANET_IP+":"+port);
        return status;
    }

    public static void addPoint(String path,String lineNum,String debug) throws IOException {
        exitFile(debug);
        FileReader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String codeLine;
        String[] points = new Gson().fromJson(lineNum,String[].class);
        StringBuffer pointFile = new StringBuffer();
        pointFile.append("import web_pdb\n");
        String spaces="";

        for(int i=0,j=0;(codeLine=bufferedReader.readLine())!=null;  i++){
            spaces = AddPointUtil.getBeginningSpaceNumber(codeLine);
//            pointFile.append(codeLine+"\n");
            if (j<points.length&&i==Integer.parseInt(points[j])-1){
                pointFile.append(spaces).append("web_pdb.set_trace()\n");
                j++;
            }
//            if(j<points.length&&i==Integer.parseInt(points[j])-2){
//                pointFile.append(spaces).append("web_pdb.set_trace()\n");
//                j++;
//            }
            pointFile.append(codeLine+"\n");
        }
        System.out.println(pointFile);
        FileOutputStream fileOutputStream = new FileOutputStream(debug);
        fileOutputStream.write(String.valueOf(pointFile).getBytes());
        fileOutputStream.flush();
        fileOutputStream.close();
        bufferedReader.close();

    }
    public static void exitFile(String path){
        System.out.println(path+"-----------------------PATH-----------------");
        boolean flag=true;
        File file=new File(path);
        String filePath="/";
        String[] list=path.split("[/]");
        for (int i=0;i<list.length-1;i++) {
            if (i==(list.length-2)){
                filePath=filePath+list[i];
            }else {
                filePath=filePath+list[i]+"/";
            }
        }
        File dir=new File(filePath);
        if (!dir.exists()){
            if (dir.mkdirs()){
                System.out.println("OK");
            }else {
                System.out.println("BAD");
                flag=false;

            }
        }
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Status run(String path,Data data) throws
            IOException,
            InterruptedException, TimeoutException {
        Status status=new Status();
//        String projectName=data.getMac()+"_"+path.split("[/]")[2];
//        RedisOperating op=new RedisOperating();
//        String type=op.get(projectName);
//        String run="docker exec "+data.getDockerId()+" "+type+" "+path;
        String run="docker exec "+data.getDockerId()+" python3.7 "+path;
        Process pro = Runtime.getRuntime().exec(run);
        if (pro.getErrorStream().available()!=0){
            BufferedReader err=new BufferedReader(new InputStreamReader(pro
                    .getErrorStream()));
            StringBuffer errorRes =new StringBuffer();
            String errLine="";
            while ((errLine=err.readLine())!=null){
                errorRes.append(errLine+"\n");
            }
            status.setStatus("0");
            status.setData(errorRes.toString());
            return status;
        }
        BufferedReader reader=new BufferedReader(new InputStreamReader(pro
                .getInputStream()));
        int flag=0;
        while (flag==0){
            flag=pro.getInputStream().available();
        }
        StringBuffer result=new StringBuffer();
        String line="";
        int lineNum=1;
        while ((line=reader.readLine())!=null&lineNum<500){
            result.append(line+"\n");
            lineNum++;
        }
        if(line!=null){
            result.append(line+"\n");
            System.out.println("NEW THREAD");
            Server.process_map.put(data.getMac()+"_process",pro);
            ProcessRunable processRunable=new ProcessRunable();
            processRunable.setProcess(pro);
            processRunable.setMac(data.getMac());
            processRunable.setIn(reader);
            WorkThreadPool.doWork(processRunable);
        }
        status.setData(result.toString());
        status.setStatus("1");
        System.out.println("Run ok--------------------------------------"+result);
//        if (pro.getErrorStream().available()!=0){
//            BufferedReader err=new BufferedReader(new InputStreamReader(pro
//                    .getErrorStream()));
//            StringBuffer errorRes =new StringBuffer();
//            String errLine="";
//            while ((errLine=err.readLine())!=null){
//                errorRes.append(errLine+"\n");
//            }
//            status.setStatus("0");
//            status.setData(errorRes.toString());
//        }
        return status;
    }







    public static Boolean exitInput(String path,String mac){
        String add="import sys\n" +
                "temp=open('/home/jingbao_temp', 'r')\n" +
                "sys.stdin=temp\n";
        Boolean flag=false;
        String res=FileUtils.read("/home/jingbao/桌面/"+mac+"/"+path);
        Pattern p=Pattern.compile("(input([^>]*))");
        Matcher m=p.matcher(res);
//        System.out.println( m.groupCount());
        if (m.find()){
            flag=true;
            res=add+res;
            String input="/home/jingbao/桌面/"+mac+"/"+mac+"_input.py";
            try {
                boolean flag_add=FileUtils.write(input,res);
                FileUtils.write("/home/jingbao/桌面/"+mac+"/jingbao_temp","");
                File file=new File("/home/jingbao/桌面/"+mac+"/jingbao_temp");
                if (!file.exists()){
                    file.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;


    }

    public static Status inputRun(String path,Data data) throws IOException {
        Status status=new Status();
        status.setStatus("1");
        String sh="docker exec "+data.getDockerId()+" python3.7 "+path;
        Process pro = Runtime.getRuntime().exec(sh);

        if (pro.getErrorStream().available()!=0){
            BufferedReader err=new BufferedReader(new InputStreamReader(pro
                    .getErrorStream()));
            StringBuffer errorRes =new StringBuffer();
            String errLine="";
            while ((errLine=err.readLine())!=null){
                errorRes.append(errLine+"\n");
            }
            if (errorRes.toString().contains("EOF when reading a line")){
                status.setStatus("2");
            }else {
                status.setStatus("0");
                status.setData(errorRes.toString());
                return status;
            }
        }
        BufferedReader reader=new BufferedReader(new InputStreamReader(pro
                .getInputStream()));
        int flag=0;
        while (flag==0){
            flag=pro.getInputStream().available();
        }
        StringBuffer result=new StringBuffer();
        String line="";
        int lineNum=1;
        while ((line=reader.readLine())!=null&lineNum<500){
            result.append(line+"\n");
            lineNum++;
        }
        if(line!=null){
            result.append(line+"\n");
            Server.process_map.put(data.getMac()+"_process",pro);
            ProcessRunable processRunable=new ProcessRunable();
            processRunable.setProcess(pro);
            processRunable.setMac(data.getMac());
            processRunable.setIn(reader);
            WorkThreadPool.doWork(processRunable);
        }
        status.setData(result.toString());
        System.out.println("inputRunok--------------------------------------"+result);
        return status;

    }


    public static void main(String[] args) throws IOException {
//        String add="import sys\n" +
//                "temp=open('/home/jingbao_temp', 'r')\n" +
//                "sys.stdin=temp\n";
//        String input="/home/jingbao/桌面/MAC/MAC_input.py";
//        boolean flag_add=FileUtils.write(input,add);
    }
}
