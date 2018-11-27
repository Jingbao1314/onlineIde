package Server;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import pojo.Message;

/**
 * Created by jingbao on 18-11-1.
 */
public class InHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        Message message=new Message();
//        System.out.println("read");
        try {
            FullHttpRequest fhr = (FullHttpRequest) msg;
//            System.out.println(fhr.headers().get("Cookies"));
//            System.out.println("请求的URL："+fhr.uri());
            message.setUrl(fhr.uri());
//            message.setFhr(fhr);
            ByteBuf buf = fhr.content();
            HttpHeaders head=fhr.headers();
            byte[] result1 = new byte[buf.readableBytes()];
            buf.readBytes(result1);
            String data=new String(result1,"utf8");
            System.out.println("----------------------------读取的数据："+data);
            message.setData(data);
            ctx.write(message);
//            System.out.println("write");
        }catch (Exception e){

            e.printStackTrace();
        }
    }

}
