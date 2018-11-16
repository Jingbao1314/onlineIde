package Server;


import WorkerRunables.WorkRunable;
import WorkerRunables.WorkThreadPool;
import com.google.gson.Gson;
import contrual.SaveProject;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import pojo.Data;
import pojo.DockerFile;
import pojo.Message;
import pojo.Status;

import static io.netty.handler.codec.http.HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.apache.http.HttpHeaders.CONTENT_LENGTH;

/**
 * Created by jingbao on 18-11-1.
 */
public class OutHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        Message message= (Message) msg;
        Status status=new Status();
        status.setStatus("1");//接收成功
        String res=new Gson().toJson(status);
        FullHttpResponse response = null;
        response = new DefaultFullHttpResponse(HTTP_1_1,
                OK, Unpooled.wrappedBuffer(res.getBytes("UTF-8")));
        response.headers().set(CONTENT_TYPE, "application/json");
        response.headers().setInt(CONTENT_LENGTH,
                response.content().readableBytes());
        response.headers().set(ACCESS_CONTROL_ALLOW_ORIGIN,"*");
        ctx.write(response);
        ctx.flush();
        WorkRunable run=new WorkRunable();
        run.setMessage(message);
        WorkThreadPool.doWork(run);
    }


}
