package com.io2c.httpproxyserver.codec;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;


public class MyHttpObjectAggregator extends HttpObjectAggregator {

    private static final int MAX_CONTENT_LENGTH = 1024 * 100;
    public MyHttpObjectAggregator(){
        super(MAX_CONTENT_LENGTH);
    }

    @Override
    protected FullHttpMessage beginAggregation(HttpMessage start, ByteBuf content) throws Exception {
        FullHttpMessage fullHttpMessage = super.beginAggregation(start, content);
        content = fullHttpMessage.content();
        int i = content.readableBytes();
        byte[] b = new byte[i];
        content.getBytes(0, b);
        System.out.println(new String(b,CharsetUtil.UTF_8));
        return fullHttpMessage;
    }




}
