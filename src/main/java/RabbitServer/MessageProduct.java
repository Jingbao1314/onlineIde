package RabbitServer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;

/**
 * Created by jingbao on 18-11-1.
 */
public class MessageProduct {

    public void direct(String data,String queueName) throws IOException
    {//direct模式
        Connection connection =getConnection();

        Channel channel = connection.createChannel();
        String exchangeName = "exchangeWS";
        String routingKey = queueName;
        channel.exchangeDeclare(exchangeName,"direct");
        channel.queueDeclare(queueName,false,false,false,null);
        channel.queueBind(queueName,exchangeName,routingKey);
        channel.basicPublish(exchangeName,routingKey,null,data.getBytes());
        //发送消息
//        System.out.println("produce msg :"+data);
        channel.close();
        connection.close();

    }
    public Connection getConnection(){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("test");
        factory.setPassword("123456");
        factory.setVirtualHost("/");
        Connection connection= null;
        try {
            connection=  factory.newConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;

    }

    public static void main(String[] args) throws IOException {
//       new MessageProduct().direct("xxxxxxx","MacTest_channel");
//       new MessageProduct().direct("xxxxxxx","MacTest_channel");
        new MessageProduct().direct("12345678","MAC_input");
    }
}
