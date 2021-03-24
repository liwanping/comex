package org.yushu.comex.remoting;

import io.netty.channel.ChannelHandlerContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.yushu.comex.remoting.exception.RemotingConnectException;
import org.yushu.comex.remoting.exception.RemotingSendRequestException;
import org.yushu.comex.remoting.exception.RemotingTimeoutException;
import org.yushu.comex.remoting.exception.RemotingTooMuchRequestException;
import org.yushu.comex.remoting.netty.NettyRemotingClient;
import org.yushu.comex.remoting.netty.NettyRemotingServer;
import org.yushu.comex.remoting.netty.ResponseFuture;
import org.yushu.comex.remoting.netty.config.NettyClientConfig;
import org.yushu.comex.remoting.netty.config.NettyServerConfig;
import org.yushu.comex.remoting.netty.processor.AsyncNettyRequestProcessor;
import org.yushu.comex.remoting.protocol.RemotingCommand;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertNotNull;

/**
 * @author: frank.li
 * @date: 2021/3/23
 */
public class RemotingServerTest {

    private static RemotingServer remotingServer;
    private static RemotingClient remotingClient;

    public static RemotingServer createRemotingServer() throws InterruptedException {

        NettyServerConfig config = new NettyServerConfig();
        RemotingServer remotingServer = new NettyRemotingServer(config);
        remotingServer.registerProcessor(0, new AsyncNettyRequestProcessor() {
            @Override
            public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) {
                request.setRemark("Hi " + ctx.channel().remoteAddress());
                request.setBody(("Hi " + new String(request.getBody())).getBytes());
                return request;
            }

            @Override
            public boolean rejectRequest() {
                return false;
            }
        }, Executors.newCachedThreadPool());

        remotingServer.start();

        return remotingServer;
    }

    public static RemotingClient createRemotingClient() {
        return createRemotingClient(new NettyClientConfig());
    }

    public static RemotingClient createRemotingClient(NettyClientConfig nettyClientConfig) {
        RemotingClient client = new NettyRemotingClient(nettyClientConfig);
        client.start();
        return client;
    }

    @BeforeClass
    public static void setup() throws InterruptedException {
        remotingServer = createRemotingServer();
        remotingClient = createRemotingClient();
    }

    @AfterClass
    public static void destroy() {
        remotingClient.shutdown();
        remotingServer.shutdown();
    }

    @Test
    public void testInvokeSync() throws InterruptedException, RemotingConnectException,
            RemotingSendRequestException, RemotingTimeoutException {
        RemotingCommand request = RemotingCommand.createRequestCommand(0);
        request.setBody("testing sync".getBytes());
        RemotingCommand response = remotingClient.invokeSync("localhost:8888", request, 1000 * 3);
        assertNotNull(response);
        assertNotNull(response.getRemark());
    }

    @Test
    public void testInvokeOneway() throws InterruptedException, RemotingConnectException,
            RemotingTimeoutException, RemotingTooMuchRequestException, RemotingSendRequestException {

        RemotingCommand request = RemotingCommand.createRequestCommand(0);
        request.setBody("testing oneway".getBytes());
        remotingClient.invokeOneway("localhost:8888", request, 1000 * 3);
    }

    @Test
    public void testInvokeAsync() throws InterruptedException, RemotingConnectException,
            RemotingTimeoutException, RemotingTooMuchRequestException, RemotingSendRequestException {

        final CountDownLatch latch = new CountDownLatch(1);
        RemotingCommand request = RemotingCommand.createRequestCommand(0);
        request.setBody("testing async".getBytes());
        remotingClient.invokeAsync("localhost:8888", request, 1000 * 3, new InvokeCallback() {
            @Override
            public void operationComplete(ResponseFuture responseFuture) {
                latch.countDown();
                assertNotNull(responseFuture);
                assertNotNull(responseFuture.getResponseCommand());
                assertNotNull(responseFuture.getResponseCommand().getRemark());
            }
        });
        latch.await();
    }
}


