package com.github.ryuseiishida.pedestrian_simulation.asynchronousConnection;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

/**
 * クライアント側メインクラス
 */
class EchoClient {
    ChannelFuture future;
    ClientBootstrap bootstrap;

    EchoClient() {
        ChannelFactory factory =
                new NioClientSocketChannelFactory( // client
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()
                );

        this.bootstrap = new ClientBootstrap(factory);
        this.bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
                ChannelPipeline pipeline = Channels.pipeline();
                // Downstream(送信)
                pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                pipeline.addLast("stringEncoder", new StringEncoder());
                // Upstream(受信)
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(8192, 0, 4, 0, 4));
                pipeline.addLast("stringDecoder", new StringDecoder());
                // Application Logic Handler
                pipeline.addLast("handler", new EchoClientHandler()); // client

                return pipeline;
            }
        });
    }

    public void launch() {
        this.future = this.bootstrap.connect(new InetSocketAddress("192.168.50.120", 9999)); // 9999番ポートにconnect
    }

    public void exit() {
        this.future.getChannel().getCloseFuture().awaitUninterruptibly();
        this.bootstrap.releaseExternalResources();
    }

    public static void main(String[] args) {
        EchoClient aProcess = new EchoClient();

        try {
//	    long start = System.currentTimeMillis(), current;
//		long end = start + Integer.parseInt(args[0])*1000;
//		int interval = Integer.parseInt(args[1])*1000;
            long start = System.currentTimeMillis(), current;
            long end = start + Integer.parseInt("5") * 1000;
            int interval = Integer.parseInt("5") * 1000;

            System.out.println("\n\n*** Client process is requesting! ***");
            do {
                aProcess.launch();
                Thread.sleep(interval);
                current = System.currentTimeMillis();
            } while (current < end);
        } catch (Exception e) {
            e.printStackTrace();
        }
        aProcess.exit();
        System.out.println("\n\n*** Client process is finished! ***");
    }
}
