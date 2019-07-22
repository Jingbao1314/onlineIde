package com.jingbao.handler;

import com.google.gson.Gson;
import com.jingbao.load.ServiceEntity;
import com.jingbao.load.ServiceLoad;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static io.netty.handler.codec.http.HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.stomp.StompHeaders.CONTENT_LENGTH;

/**
 * Created by jingbao on 18-11-1.
 */
public class MyHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        FullHttpRequest fhr = (FullHttpRequest) msg;
        ByteBuf buf = fhr.content();
        byte[] res = new byte[buf.readableBytes()];
        buf.readBytes(res);
        String data=new String(res,"utf8");
        ServiceEntity serviceEntity= ServiceLoad.urlMapping.get(fhr.uri());
        Method method=serviceEntity.getMethod();
        Class clazz=serviceEntity.getClazz();
        Object obj=clazz.getConstructor().newInstance();
        String result="{}";
        Type type=method.getGenericReturnType();
        Gson gson=new Gson();
        if (method.getParameterTypes()==null||method.getParameterTypes()
                .length==0){
            if (!type.toString().equals("void")){
                result=  gson.toJson(method.invoke(obj));
            }else {
                method.invoke(obj);
            }
        }else {
            if (!type.toString().equals("void")){
                result=  gson.toJson(method.invoke(obj,data));
            }else {
                method.invoke(obj,data);
            }

        }

        FullHttpResponse response = null;
        response = new DefaultFullHttpResponse(HTTP_1_1,
                OK, Unpooled.wrappedBuffer(result.getBytes("UTF-8")));
        response.headers().set(CONTENT_TYPE, "application/json");
        response.headers().setInt(CONTENT_LENGTH,
                response.content().readableBytes());
        response.headers().set(ACCESS_CONTROL_ALLOW_ORIGIN,"*");
        ctx.write(response);
        ctx.flush();
    }

}
