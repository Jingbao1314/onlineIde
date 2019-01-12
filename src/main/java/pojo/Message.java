package pojo;

import io.netty.handler.codec.http.FullHttpRequest;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by jingbao on 18-8-14.
 */
public class Message extends MyJson{
    FullHttpRequest fhr=null;
    private String url="";
    private String data="";
    private String dockerId="";

    public String getDockerId() {
        return dockerId;
    }
    public void setDockerId(String dockerId) {
        this.dockerId = dockerId;
    }
    public FullHttpRequest getFhr() {
        return fhr;
    }

    public void setFhr(FullHttpRequest fhr) {
        this.fhr = fhr;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Message(String url, String data, String dockerId) {
        this.url = url;
        this.data = data;
        this.dockerId = dockerId;
    }
    public Message(){}

    @Override
    public String toString() {
        StringBuffer sb=new StringBuffer("{");//"{"
        int flag=0;
        if (!getData().equals("")){
            sb.append("\"data\":"+"\""+getData()+"\"");
            flag=1;
        }
        if (!getDockerId().equals("")){
            if (flag==1){
                sb.append(",\"dockerId\":"+"\""+getDockerId()+"\"");
            }else {
                sb.append("\"dockerId\":"+"\""+getDockerId()+"\"");
                flag=1;
            }


        }
        if (!getUrl().equals("")){
            if (flag==1){
                sb.append(",\"url\":"+"\""+getUrl()+"\"");
            }else {
                sb.append("\"url\":"+"\""+getUrl()+"\"");
                flag=1;
            }
        }
        sb.append("}");
        return sb.toString();
    }

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Message m=new Message();
        m.setData("xxxx");
        m.setDockerId("1234");
        m.setUrl("/test");
        System.out.println(m.toJson());
    }

}
