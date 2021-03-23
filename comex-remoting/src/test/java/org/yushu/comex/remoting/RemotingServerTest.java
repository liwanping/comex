package org.yushu.comex.remoting;

import io.netty.channel.ChannelHandlerContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.yushu.comex.remoting.annotation.CFNullable;
import org.yushu.comex.remoting.exception.RemotingCommandException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
        RequestHeader requestHeader = new RequestHeader();
        requestHeader.setCount(1);
        requestHeader.setMessageTitle("Welcome");
        RemotingCommand request = RemotingCommand.createRequestCommand(0, requestHeader);
        RemotingCommand response = remotingClient.invokeSync("localhost:8888", request, 1000 * 3);
        assertNotNull(response);
        assertThat(response.getExtFields()).hasSize(2);
    }

    @Test
    public void testInvokeOneway() throws InterruptedException, RemotingConnectException,
            RemotingTimeoutException, RemotingTooMuchRequestException, RemotingSendRequestException {

        RemotingCommand request = RemotingCommand.createRequestCommand(0, null);
        request.setRemark("messi");
        remotingClient.invokeOneway("localhost:8888", request, 1000 * 3);
    }

    @Test
    public void testInvokeAsync() throws InterruptedException, RemotingConnectException,
            RemotingTimeoutException, RemotingTooMuchRequestException, RemotingSendRequestException {

        final CountDownLatch latch = new CountDownLatch(1);
        RemotingCommand request = RemotingCommand.createRequestCommand(0, null);
        request.setRemark("messi");
        remotingClient.invokeAsync("localhost:8888", request, 1000 * 3, new InvokeCallback() {
            @Override
            public void operationComplete(ResponseFuture responseFuture) {
                latch.countDown();
                assertNotNull(responseFuture);
                assertThat(responseFuture.getResponseCommand().getExtFields()).hasSize(2);
            }
        });
        latch.await();
    }

    static class RequestHeader implements CommandCustomHeader {
        @CFNullable
        private Integer count;
        @CFNullable
        private String messageTitle;

        @Override
        public void checkFields() throws RemotingCommandException {
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public String getMessageTitle() {
            return messageTitle;
        }

        public void setMessageTitle(String messageTitle) {
            this.messageTitle = messageTitle;
        }
    }
}


