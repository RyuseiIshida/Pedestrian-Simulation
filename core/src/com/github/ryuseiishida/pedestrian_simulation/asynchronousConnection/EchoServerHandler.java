package com.github.ryuseiishida.pedestrian_simulation.asynchronousConnection;

import java.net.InetSocketAddress;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import java.time.LocalDateTime;
import java.util.*;

/**
 * サーバ側アプリケーションロジック
 */
class EchoServerHandler extends SimpleChannelHandler {
    String replyMsgJP = "ありがとう！";
    String replyMsgEN = "Thank you !";

    Random randomGenerator = new Random();

    /**
     * クライアントから電文を受信した際に呼び出されるメソッド
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) {
        String msg = (String) event.getMessage(); // 受信電文を取りだす
        System.out.println(LocalDateTime.now());
        System.out.println("\t" + msg + " is recieved from " +
                ((InetSocketAddress) event.getRemoteAddress()).getHostName() + ".");

        try {
            Thread.sleep(1000);
            // クライアントに送信
            switch (randomGenerator.nextInt() % 2) {
                case 0:
                    ctx.getChannel().write(this.replyMsgJP);
                    break;
                case 1:
                    ctx.getChannel().write(this.replyMsgEN);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
