/**
 * 
 */
package com.fire.login.handler;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.fire.login.http.HttpHandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author lhl
 *
 *         2016年3月29日 下午1:53:10
 */
public abstract class StringResponseHttpHandler implements HttpHandler
{
    /**
     * 发送应答，完成后将关闭连接
     * 
     * @param channel
     * @param response
     */
    protected void sendResponse(Channel channel, String response) {
        ByteBuf bb = Unpooled.copiedBuffer(response.getBytes(UTF_8));
        FullHttpResponse httpResp = new DefaultFullHttpResponse(HTTP_1_1, OK, bb);
        httpResp.headers().set(CONTENT_TYPE, "text/plain");
        httpResp.headers().set(CONTENT_LENGTH, httpResp.content().readableBytes());
        channel.writeAndFlush(httpResp).addListener(ChannelFutureListener.CLOSE);
    }
}
