/**
 * 
 */
package com.fire.login.handler;

import java.util.List;
import java.util.Map;

import com.fire.login.http.HttpRequestHandler;

import io.netty.channel.Channel;

/**
 * 用户登录
 * <p>
 * 登录流程：用户请求登录服务器校验帐号密码 -> 登录服务器校验数据并返回token -> 用户使用此token连接网关服务器 ->
 * 网关服务器请求登录服务器验证token -> 登录完成
 * 
 * @author lhl
 *
 *         2016年3月29日 下午1:50:23
 */
@HttpRequestHandler(uri = "/login.auth")
public class LoginHandler extends StringResponseHttpHandler
{
    @Override
    public void handle(Channel channel, Map<String, List<String>> parameter) {
        // TODO do login authentication
    }
}
