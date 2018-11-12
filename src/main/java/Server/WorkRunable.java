package Server;


import RabbitServer.MessageConsume;
import RabbitServer.MessageProduct;
import Servlets.MainServlet;
import com.google.gson.Gson;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderUtil;
import pojo.Data;
import pojo.Message;
import pojo.Status;
import websocketServer.Global;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by jingbao on 18-8-14.
 */
public class WorkRunable implements Runnable{
    private String result="";
    private Message message=null;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public void run() {
        Data data=new Gson().fromJson(message.getData(), Data.class);
        System.out.println(new Gson().toJson(data));
        Status status= null;
        try {
            status = MainServlet.doServlet(data,message.getUrl());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        String res=new Gson().toJson(status);
        System.out.println(Global.map.get(data.getMac()+"_channel"));
        try {
            new MessageProduct().direct(res,data.getMac()+"_channel");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
