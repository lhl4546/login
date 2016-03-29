/**
 * 
 */
package com.fire.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fire.login.http.HttpServer;
import com.fire.login.http.HttpServerDispatcher;
import com.fire.login.manager.GateServerManager;

/**
 * @author lhl
 *
 *         2016年3月28日 下午3:28:09
 */
public class LoginServer implements Component
{
    static {
        Config.parse("app.properties");
    }

    private static final Logger LOG = LoggerFactory.getLogger(LoginServer.class);
    private HttpServerDispatcher dispatcher = new HttpServerDispatcher();
    private HttpServer httpServer = new HttpServer(dispatcher);

    public static void main(String[] args) {
        LoginServer loginServer = new LoginServer();
        loginServer.start();
    }

    @Override
    public void start() {
        try {
            GateServerManager.INSTANCE.start();
            dispatcher.start();
            httpServer.start();
            addShutdownHook();
            LOG.debug("Login server start successfully");
        } catch (Exception e) {
            LOG.error("Login server start failed", e);
        }
    }

    private void addShutdownHook() {
        Runnable action = () -> {
            LOG.debug("Shutdown hook has been triggered");
            stop();
        };
        Thread hook = new Thread(action);
        Runtime.getRuntime().addShutdownHook(hook);
    }

    @Override
    public void stop() {
        try {
            httpServer.stop();
            dispatcher.stop();
            GateServerManager.INSTANCE.stop();
            LOG.debug("Login server stop");
        } catch (Exception e) {
            LOG.error("Login server stop failed", e);
        }
    }
}
