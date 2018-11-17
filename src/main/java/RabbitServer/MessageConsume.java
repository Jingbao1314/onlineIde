package RabbitServer;

import com.google.gson.Gson;
import com.rabbitmq.client.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import pojo.Status;
import websocketServer.Global;
import websocketServer.MyWebSocketServerHandler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by jingbao on 18-11-1.
 */
public class MessageConsume {
    private static ChannelHandlerContext ctx=null;
    private String mac="";

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }
    public static void fanoutConsume(){
        Channel channel=getChannel();
        try {
            channel.queueDeclare("queueOne1",false,false,false,null);
            //绑定队列到交换器
            channel.queueBind("queueOne1","test_exchange_fanout",""); //不设置路由键
            //统一时刻服务器只会发一条消息给消费者;
            channel.basicQos(1);
            //定义队列的消费者
            QueueingConsumer consumer = new QueueingConsumer(channel);
            //监听队列，手动返回完成
            channel.basicConsume("queueOne1",false,consumer);
            //获取消息
            while (true)
            {
                QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                String message = new String(delivery.getBody());
                System.out.println(" 前台系统：'" + message + "'");
                //手动返回
                try {
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    public static Channel getChannel(){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("test");
        factory.setPassword("123456");
        factory.setVirtualHost("/");

        Connection connection = null;
        Channel channel=null;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return channel;
    }

    public void consume() throws IOException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("test");
        factory.setPassword("123456");
        factory.setVirtualHost("/");

        Connection connection =  factory.newConnection();

        Channel channel = connection.createChannel();
        String queueName = this.mac;
        channel.queueDeclare(queueName,false,false,false,null);

        channel.basicQos(5);  //每次取5条消息

        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //消费消费
                String msg = new String(body,"utf-8");
                System.out.println("consume msg: "+msg+mac);
                send(msg);
                //手动消息确认
                getChannel().basicAck(envelope.getDeliveryTag(),false);
            }
        };


        //调用消费消息
        channel.basicConsume(queueName,false,"WS",consumer);
    }

    public void send(String res){
        System.out.println("send ----------------------------------"+mac);
        TextWebSocketFrame tws = new TextWebSocketFrame(res);
        Global.map.get(mac).channel().write(tws);
        Global.map.get(mac).channel().flush();
//
//                // 群发
//        Global.group.write(tws);
//        Global.group.flush();
//                MyWebSocketServerHandler.xxx(tws);
    }

    public static void main(String[] args) throws IOException {
    }
}
