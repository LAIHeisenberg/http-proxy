package com.io2c.httpproxyserver.handler.https;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.io2c.httpproxyserver.HttpProxyServer;
import com.io2c.httpproxyserver.parser.HttpRequestParser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;

import java.util.Iterator;

public class MyChannelOutboundHandler extends ChannelOutboundHandlerAdapter{

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        String uri = ctx.channel().attr(HttpProxyServer.httpUriAttributeKey).get();
        System.out.println("uri: "+uri);
        ctx.read();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        String uri = ctx.channel().attr(HttpProxyServer.httpUriAttributeKey).get();
        System.out.println("uri2: "+uri);
        FullHttpResponse response = (FullHttpResponse) msg;
        String needMaskingHeader = response.headers().get("X-Need-Masking");
        if (needMaskingHeader != null){
            ByteBuf in = response.content();
            int c = in.readableBytes();
            byte[] readBytes = new byte[c];
            in.getBytes(0,readBytes);
            String messageBody = new String(readBytes, 0, c, CharsetUtil.UTF_8);
            System.out.println(messageBody);
            try{
                if (uri.contains("/api/users") && !StringUtil.isNullOrEmpty(messageBody)){
                    JSONObject respJson = JSONObject.parseObject(messageBody);
                    JSONArray content = respJson.getJSONArray("content");
                    Iterator<Object> iterator = content.iterator();
                    while (iterator.hasNext()){
                        JSONObject next = (JSONObject) iterator.next();
                        String phone = next.getString("phone");
                        if(!StringUtil.isNullOrEmpty(phone)){
                            String hideStr = phone.substring(3, 8);
                            phone = phone.replace(hideStr,"******");
                            next.put("phone", phone);
                        }
                        String email = next.getString("email");
                        String hideEmailStr = email.substring(1, email.indexOf("@"));
                        email = email.replace(hideEmailStr,"****");
                        next.put("email", email);
                    }
                    respJson.put("content", content);
                    in.clear();
                    in = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(respJson.toJSONString(), CharsetUtil.UTF_8));
                    FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, in);
                    resp.headers().set("Content-Length", in.readableBytes());
                    resp.headers().set("Content-Type","application/json;charset:utf-8;");
                    msg = resp;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        ctx.write(msg, promise);
    }

}
