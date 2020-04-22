package com.github.ryuseiishida.pedestrian_simulation.asynchronousConnection;

import java.net.InetSocketAddress;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import java.time.LocalDateTime;

/**
 * クライアント側アプリケーションロジック
 */
class EchoClientHandler extends SimpleChannelHandler {
    /**
     * サーバに接続した際に呼び出されるメソッド
     */
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent event) {
        ctx.getChannel().write("Hello!");
    }
    
    /**
     * サーバから電文を受信した際に呼び出されるメソッド
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) {
        String msg = (String) event.getMessage();
	System.out.println(LocalDateTime.now());
	System.out.println("\t" + msg + " is recieved from " +
			   ((InetSocketAddress)event.getRemoteAddress()).getHostName() + ".");
    }
}
