package contrual;

import RabbitServer.GlobalInput;
import RabbitServer.InputConsume;
import RabbitServer.MessageProduct;
import WorkerRunables.DebugRunable;
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

import java.io.*;
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
        System.out.println(lineNum+"/*/*/*/-/*/*-/-*/*/*/*/*-/-*/*/-*-/*/*/*-");
        Data newData=new Data();
        newData.setDockerId(data.getDockerId());
        newData.setMac(data.getMac());
        newData.setData(alterPath);
        SaveProject.doSave(newData);//-------------后期优化-----------------------
        if (lineNum==null){
//            path="/home"+path;
            status=run(path,data);
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
//        File fileReader=new File(path);
//        FileInputStream in=new FileInputStream(fileReader);
//        BufferedReader bufferedReader = new BufferedReader(new
//                InputStreamReader(in));
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
        System.out.println(path+"--+-+-+-+++++++++++++++++++++++++++++++++++");
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
       Boolean flag=exitInput(path,data.getMac());
        path="/home"+path;
       if (!flag){
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
           StringBuffer result=new StringBuffer();
           String line="";
           while ((line=reader.readLine())!=null){
               result.append(line+"\n");
           }
           status.setData(result.toString());
           System.out.println("Run ok--------------------------------------"+result);
       }else {
           Process ps = Runtime.getRuntime().exec(new
                   String[]{"/home/jingbao/桌面/shell/run.sh",data.getDockerId(),
                   path});//"/home/"+data.getMac()+path

           //读取标准输入流
           BufferedWriter brOutput = new BufferedWriter(new OutputStreamWriter
                   (ps.getOutputStream()));
           GlobalInput.map.put(data.getMac()+"_input",brOutput);

           InputConsume in=new InputConsume();
           in.setOut(brOutput);
           in.setMac_input(data.getMac()+"_input");
           in.consume();

           //读取标准输出流
           BufferedReader brInput =new BufferedReader(new InputStreamReader(ps
                   .getInputStream()));
           String line = null;
           Status input=new Status();
           while ((line=brInput.readLine()) != null) {
               System.out.println
                       (line+"-*-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-//-/-");
               input.setData(line);
               input.setStatus("1");
               new MessageProduct().direct(new Gson().toJson(input),data.getMac()
                       +"_channel");


           }

           //读取标准错误流
           BufferedReader brError = new BufferedReader(new InputStreamReader(ps
                   .getErrorStream()));
           String errline = null;
           while ((errline = brError.readLine()) != null) {
               System.out.println(errline);
           }

           int flags=ps.waitFor();
           if(flags!=0){
               System.out.println("程序异常终止.");
           }


       }
        return status;
    }



    public static Boolean exitInput(String path,String mac){
        Boolean flag=false;
        String res=FileUtils.read("/home/jingbao/桌面/"+mac+path);
        Pattern p=Pattern.compile("(input([^>]*))");
        Matcher m=p.matcher(res);
        System.out.println( m.groupCount());
        if (m.find()){
            flag=true;
        }
        return flag;


    }

    public static void main(String[] args) {
        exitInput("","");
//        System.out.println("http://"+ CustomSystemUtil.INTRANET_IP+":"+7721);
//        exitFile("/home/jingbao/桌面/MAC/MAC_debug/xxx.py");
//        System.out.println(new Gson().fromJson("[\"1\",\"2\"]",String[]
//                .class)[1]);

    }
}
