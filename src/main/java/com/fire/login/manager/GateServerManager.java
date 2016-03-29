/**
 * 
 */
package com.fire.login.manager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fire.login.Component;
import com.fire.login.Config;
import com.fire.login.util.BaseUtil;

/**
 * 网关服务器管理类
 * 
 * @author lhl
 *
 *         2016年3月29日 下午2:35:20
 */
public class GateServerManager implements Component
{
    private static final Logger LOG = LoggerFactory.getLogger(GateServerManager.class);
    private GateServer[] gateServer;
    private AtomicInteger counter;
    // <account, gate>
    private ConcurrentMap<String, GateServer> accountToGate;

    public static final GateServerManager INSTANCE = new GateServerManager();

    private GateServerManager() {
        counter = new AtomicInteger();
        accountToGate = new ConcurrentHashMap<>();
    }

    /**
     * 为帐号{@code account}寻找一个合适的网关服务器
     * 
     * @param account
     * @return
     */
    public static GateServer getGateServer(String account) {
        return INSTANCE.doGet(account);
    }

    /**
     * 先从本地缓存查找，查找失败则按照取模规则分配一个新的网关
     * 
     * @param account
     * @return
     */
    private GateServer doGet(String account) {
        GateServer gate = accountToGate.get(account);
        if (gate == null) {
            gate = gateServer[Math.abs(counter.getAndIncrement() % gateServer.length)];
            gate = accountToGate.putIfAbsent(account, gate);
        }
        return gate;
    }

    /**
     * 删除帐号{@code account}与网关服务器对应关系
     * 
     * @param account
     */
    public static void removeGateServer(String account) {
        INSTANCE.doRemove(account);
    }

    /**
     * 删除本地缓存记录
     * 
     * @param account
     */
    private void doRemove(String account) {
        accountToGate.remove(account);
    }

    @Override
    public void start() throws Exception {
        String gateServerList = Config.getString("GATE_SERVER_LIST");
        if (BaseUtil.isNullOrEmpty(gateServerList)) {
            throw new IllegalStateException("There must be at least one gate server");
        }

        String[] ipPortPairs = gateServerList.trim().split("\\|");
        GateServer[] array = new GateServer[ipPortPairs.length];
        for (int i = 0; i < ipPortPairs.length; i++) {
            String ipPortPair = ipPortPairs[i];
            String[] temp = ipPortPair.split(":");
            array[i] = new GateServer(temp[0], temp[1]);
            LOG.debug("Gate server found, {}", ipPortPair);
        }
        this.gateServer = array;
    }

    @Override
    public void stop() throws Exception {
    }

    public static class GateServer
    {
        public String ip;
        public String port;

        GateServer(String ip, String port) {
            this.ip = ip;
            this.port = port;
        }
    }
}
