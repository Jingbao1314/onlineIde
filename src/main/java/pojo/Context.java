package pojo;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by jingbao on 18-11-9.
 */
public class Context {
    private ChannelHandlerContext ctx=null;
    private String url="";
    private String data="";

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
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
}
