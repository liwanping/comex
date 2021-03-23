package org.yushu.comex.remoting;

/**
 * @author: frank.li
 * @date: 2021/3/23
 */
public interface RemotingService {

    void start();

    void shutdown();

    void registerRPCHook(RPCHook rpcHook);
}
