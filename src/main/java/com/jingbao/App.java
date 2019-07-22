package com.jingbao;

import com.jingbao.runType.MoreThreadType;
import com.jingbao.runType.NettyRun;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, InterruptedException {
        NettyRun server=new MoreThreadType();
        server.run(7721);
    }
}
