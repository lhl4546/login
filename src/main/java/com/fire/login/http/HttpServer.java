package com.fire.login.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fire.login.Component;
import com.fire.login.Config;
import com.fire.login.NamedThreadFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 基于Netty实现的Http服务器
 * 
 * @author lhl
 *
 *         2016年3月28日 下午3:48:06
 */
public class HttpServer implements Component
{
    private static final Logger LOG = LoggerFactory.getLogger(HttpServer.class);
    private ServerBootstrap bootstrap;
    private EventLoopGroup bossgroup;
    private EventLoopGroup childgroup;
    private Channel serverSocket;
    private int port;
    private HttpServerDispatcher dispatcher;

    public HttpServer(HttpServerDispatcher dispatcher) {
        this.port = Config.getInt("HTTP_PORT");
        this.bossgroup = new NioEventLoopGroup(1, new NamedThreadFactory("HTTP-ACCEPTOR"));
        int netioThreads = Runtime.getRuntime().availableProcessors();
        this.childgroup = new NioEventLoopGroup(netioThreads, new NamedThreadFactory("HTTP-IO"));
        this.bootstrap = new ServerBootstrap();
        this.dispatcher = dispatcher;
    }

    @Override
    public void start() throws Exception {
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.group(bossgroup, childgroup).channel(NioServerSocketChannel.class)
                .childHandler(new HttpServerInitializer(dispatcher)).childOption(ChannelOption.SO_LINGER, 0)
                .childOption(ChannelOption.TCP_NODELAY, true);
        serverSocket = bootstrap.bind(port).sync().channel();
        LOG.debug("Http server start listen on port {}", port);
    }

    @Override
    public void stop() throws Exception {
        if (serverSocket != null) {
            serverSocket.close();
        }
        if (bossgroup != null) {
            bossgroup.shutdownGracefully();
        }
        if (childgroup != null) {
            childgroup.shutdownGracefully();
        }
        LOG.debug("Http server stop");
    }
}
