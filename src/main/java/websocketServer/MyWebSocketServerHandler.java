package websocketServer;

import RabbitServer.MessageConsume;
import RabbitServer.MessageProduct;
import WorkerRunables.CreateDockerRunable;
import WorkerRunables.WorkRunable;
import WorkerRunables.WorkThreadPool;
import com.google.gson.Gson;
import contrual.Run;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import pojo.Data;
import pojo.Status;
import utils.FileUtils;
import utils.RedisOperating;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyWebSocketServerHandler extends
        SimpleChannelInboundHandler<Object> {


	private static final Logger logger = Logger
			.getLogger(WebSocketServerHandshaker.class.getName());

	private WebSocketServerHandshaker handshaker;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {

		// 添加
		Global.group.add(ctx.channel());

		System.out.println("客户端与服务端连接开启");

	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

		// 移除
		Global.group.remove(ctx.channel());

		System.out.println("客户端与服务端连接关闭");

	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Object msg)
			throws Exception {

		if (msg instanceof FullHttpRequest) {

			handleHttpRequest(ctx, ((FullHttpRequest) msg));

		} else if (msg instanceof WebSocketFrame) {

			handlerWebSocketFrame(ctx, (WebSocketFrame) msg);

		}

	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	private void handlerWebSocketFrame(ChannelHandlerContext ctx,
			WebSocketFrame frame) throws IOException, InterruptedException {

		// 判断是否关闭链路的指令
		if (frame instanceof CloseWebSocketFrame) {
			RedisOperating op=new RedisOperating();
			String key="";
			if (op.exists(Integer.toString(ctx.hashCode()))){
				key=op.get(Integer.toString(ctx.hashCode()));
				Global.map.remove(key);
			}
			System.out.println(key);

			handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
		}

		// 判断是否ping消息
		if (frame instanceof PingWebSocketFrame) {
			ctx.channel().write(
					new PongWebSocketFrame(frame.content().retain()));
			return;
		}


		// 返回应答消息
		if (ctx.channel().isActive()){
			String request = ((TextWebSocketFrame) frame).text();

			System.out.println("服务端收到：" + request);

			if (logger.isLoggable(Level.FINE)) {
				logger
						.fine(String.format("%s received %s", ctx.channel(),
								request));
			}
//			Status status=new Status();
//			status.setStatus("CONNECT OK");
//			TextWebSocketFrame tws = new TextWebSocketFrame(new Gson().toJson(status));
//			ctx.channel().write(tws);
//			ctx.channel().flush();
			Data data=new Gson().fromJson(request, Data.class);
			if(data.getData().equals("Connection")){
				MessageConsume messageConsum=new MessageConsume();
				Global.map.put(data.getMac()+"_channel",ctx);
				RedisOperating op=new RedisOperating();
				op.set(data.getMac()+"_channel",Integer.toString(ctx.hashCode()));
				messageConsum.setMac(data.getMac()+"_channel");
				messageConsum.consume();
				CreateDockerRunable dockerRunable=new CreateDockerRunable();
				dockerRunable.setData(data);
				WorkThreadPool.doWork(dockerRunable);
			}else if (data.getData().contains("input:")){
				String [] input=data.getData().split("[:]");
				String path="/home/jingbao/桌面/"+data.getMac()+"/jingbao_temp";
				FileUtils.write(path,input[1]+"\n");
				path="/home/"+data.getMac()+"_input.py";
				Status status=Run.inputRun(path,data);
				TextWebSocketFrame tws = new TextWebSocketFrame(new Gson().toJson(status));
				ctx.channel().write(tws);
				ctx.channel().flush();
			}
		}
	}

	private void handleHttpRequest(ChannelHandlerContext ctx,
			FullHttpRequest req) throws UnsupportedEncodingException {

		if (!req.decoderResult().isSuccess()
				|| (!"websocket".equals(req.headers().get("Upgrade")))) {

			sendHttpResponse(ctx, req, new DefaultFullHttpResponse(
					HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));

			return;
		}

		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
				"ws://0.0.0.0:7397/websocket", null, false);

		handshaker = wsFactory.newHandshaker(req);

//		System.out.println(req.uri()+"-------------------------------");
		ByteBuf buf = req.content();
		HttpHeaders head=req.headers();
		byte[] result1 = new byte[buf.readableBytes()];
		buf.readBytes(result1);
		String request=new String(result1,"utf8");
		Data data=new Gson().fromJson(request, Data.class);
//		Global.map.put(data.getMac()+"_channel",ctx);
		System.out.println(new Gson().toJson(data));

		if (handshaker == null) {
			WebSocketServerHandshakerFactory
					.sendUnsupportedVersionResponse(ctx.channel());
		} else {
			handshaker.handshake(ctx.channel(), req);
		}

	}

	private static void sendHttpResponse(ChannelHandlerContext ctx,
                                         FullHttpRequest req, DefaultFullHttpResponse res) {

		// 返回应答给客户端
		if (res.status().code() != 200) {
			ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(),
					CharsetUtil.UTF_8);
			res.content().writeBytes(buf);
			buf.release();
		}

		// 如果是非Keep-Alive，关闭连接
		ChannelFuture f = ctx.channel().writeAndFlush(res);
		if (!isKeepAlive(req) || res.status().code() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	private static boolean isKeepAlive(FullHttpRequest req) {

		return false;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {

		cause.printStackTrace();
		ctx.close();

	}


//	private static Status run(Data data){
//		String path="/home/"+data.getMac()+"_input.py";
//		String sh="docker exec "+data.getDockerId()+" python3.7 "+path;
//		try {
//			Process pro = Runtime.getRuntime().exec(sh);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//	}

}
