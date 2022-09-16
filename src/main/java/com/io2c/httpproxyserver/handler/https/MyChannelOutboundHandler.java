package com.io2c.httpproxyserver.handler.https;

import com.io2c.httpproxyserver.HttpProxyServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class MyChannelOutboundHandler extends ChannelOutboundHandlerAdapter{

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        String uri = ctx.channel().attr(HttpProxyServer.httpUriAttributeKey).get();
        System.out.println("uri: "+uri);
        ctx.read();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ctx.write(msg, promise);
    }

}
