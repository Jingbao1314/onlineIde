package pojo;

import io.netty.handler.codec.http.FullHttpRequest;

/**
 * Created by jingbao on 18-8-14.
 */
public class Message {
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
}
