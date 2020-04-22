package com.github.ryuseiishida.pedestrian_simulation.asynchronousConnection;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

/**
 * サーバ側メインクラス
 */
class EchoServer {
    ServerBootstrap bootstrap;
    
    EchoServer() {
        ChannelFactory factory = 
            new NioServerSocketChannelFactory( // server
					      Executors.newCachedThreadPool(),
					      Executors.newCachedThreadPool()
					       );
        
        this.bootstrap = new ServerBootstrap(factory);
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
		    pipeline.addLast("handler", new EchoServerHandler()); // server
                
		    return pipeline;
		}
	    });
    }
    public void launch() {
        this.bootstrap.bind(new InetSocketAddress(9999)); // 9999番ポートでlisten
	System.out.println("\n\n*** Server process is listening! ***\n");
    }
    public static void main(String[] args) {
	EchoServer aProcess = new EchoServer();

	aProcess.launch();
    }
}
