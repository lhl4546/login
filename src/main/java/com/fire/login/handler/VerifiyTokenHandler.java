/**
 * 
 */
package com.fire.login.handler;

import java.util.List;
import java.util.Map;

import com.fire.login.http.HttpRequestHandler;

import io.netty.channel.Channel;

/**
 * 登录TOKEN验证
 * 
 * @author lhl
 *
 *         2016年3月29日 下午2:14:39
 */
@HttpRequestHandler(path = "/login.verify")
public class VerifiyTokenHandler extends StringResponseHttpHandler
{
    @Override
    public void handle(Channel channel, Map<String, List<String>> parameter) {
        // TODO do token verify
    }
}
