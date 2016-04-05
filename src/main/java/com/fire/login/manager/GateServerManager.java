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
 * <p>
 * 登录服务器无需确保同一账户同时在2地登录分配同一个网关。只需往先登录的网关1发送下线指令即可，网关1在收到下线指令前的所有操作都视为生效，
 * 登录服务器收到网关1的下线确认消息后即可分配网关2给用户。需要保证的是同一账户同时只能对应一个逻辑服务器。
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
    // <uid, gate>
    private ConcurrentMap<Integer, GateServer> userToGate;

    public static final GateServerManager INSTANCE = new GateServerManager();

    private GateServerManager() {
        counter = new AtomicInteger();
        userToGate = new ConcurrentHashMap<>();
    }

    /**
     * 为帐号{@code account}寻找一个合适的网关服务器
     * 
     * @param account
     * @return
     */
    public static GateServer getGateServer(int uid) {
        return INSTANCE.doGet(uid);
    }

    /**
     * 先从本地缓存查找，查找失败则按照取模规则分配一个新的网关
     * 
     * @param account
     * @return
     */
    private GateServer doGet(int uid) {
        GateServer gate = userToGate.get(uid);
        if (gate == null) {
            // TODO 网关选择策略
            gate = gateServer[Math.abs(counter.getAndIncrement() % gateServer.length)];
            // gate = gateServer[uid % gateServer.length];
            gate = userToGate.putIfAbsent(uid, gate);
        }
        return gate;
    }

    /**
     * 删除用户与网关服务器对应关系
     * 
     * @param uid
     */
    public static void removeGateServer(int uid) {
        INSTANCE.doRemove(uid);
    }

    /**
     * 删除本地缓存记录
     * 
     * @param uid
     */
    private void doRemove(int uid) {
        userToGate.remove(uid);
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
