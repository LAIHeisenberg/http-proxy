package com.io2c.httpproxyserver.codec;

import com.alibaba.fastjson.JSONObject;
import com.io2c.httpproxyserver.HttpProxyServer;
import com.io2c.httpproxyserver.parser.HttpRequestParser;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

public class MyByteToMessageDecoder extends ByteToMessageDecoder {


    final String s = "HTTP/1.1 200 \n" +
            "Vary: Origin\n" +
            "Vary: Access-Control-Request-Method\n" +
            "Vary: Access-Control-Request-Headers\n" +
            "X-Content-Type-Options: nosniff\n" +
            "X-XSS-Protection: 1; mode=block\n" +
            "Cache-Control: no-cache, no-store, max-age=0, must-revalidate\n" +
            "Pragma: no-cache\n" +
            "Expires: 0\n" +
            "Content-Type: application/json;charset=UTF-8\n" +
            "Content-Length: 27\n" +
            "Date: Thu, 08 Sep 2022 10:01:10 GMT\n" +
            "Keep-Alive: timeout=60\n" +
            "Connection: keep-alive\n" +
            "\n" +
            "{\"code\":\"orumv7gjpddg7c0k\"}";

    final String s2 = "HTTP/1.1 200 \n" +
            "Vary: Origin\n" +
            "Vary: Access-Control-Request-Method\n" +
            "Vary: Access-Control-Request-Headers\n" +
            "X-Content-Type-Options: nosniff\n" +
            "X-XSS-Protection: 1; mode=block\n" +
            "Cache-Control: no-cache, no-store, max-age=0, must-revalidate\n" +
            "Pragma: no-cache\n" +
            "Expires: 0\n" +
            "Content-Type: application/json;charset=UTF-8\n" +
            "Content-Length: 27\n" +
            "Date: Thu, 08 Sep 2022 16:51:47 GMT\n" +
            "Keep-Alive: timeout=60\n" +
            "Connection: keep-alive\n" +
            "\n" +
            "{\"code\":\"hello world!\"}";

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        int c = in.readableBytes();

        byte[] readBytes = new byte[c];

        in.getBytes(0,readBytes);

        String inHttpRequestRaw = new String(readBytes, 0, c, CharsetUtil.UTF_8);
        System.out.println(inHttpRequestRaw);
        System.out.println(ctx.channel().attr(HttpProxyServer.httpUriAttributeKey).get());
        System.out.println(ctx.channel().attr(HttpProxyServer.httpMethodAttributeKey).get());
        try {
            HttpRequestParser httpRequestParser = new HttpRequestParser();
            httpRequestParser.parseRequest(inHttpRequestRaw);
            if ("HTTP/1.1 200".equals(httpRequestParser.getRequestLine().trim())){
                String contentType = httpRequestParser.getHeaderParam("Content-Type");
                if (contentType.contains("application/json")){
                    System.out.println(inHttpRequestRaw);
                    String messageBody = httpRequestParser.getMessageBody();
                    JSONObject jsonObject = JSONObject.parseObject(messageBody);
                    jsonObject.put("code","7ato7831ouw6cccc");
                    httpRequestParser.setMessageBody(jsonObject.toJSONString());
                    in.clear();
                    String rewriteHttpRequest = httpRequestParser.getHttpRequestRaw();
                    System.out.println("-----------------");
                    System.out.println(rewriteHttpRequest);
                    in.writeBytes(rewriteHttpRequest.getBytes());
//                    in.writeBytes(s2.getBytes());
                }
            }
        }catch (Exception e){

        }

//      ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(1024);
//      buffer.setBytes(0,resp.toString().getBytes());
//      out.add(buffer.readBytes(buffer.readableBytes()));

        out.add(in.readBytes(in.readableBytes()));
//        HttpRequestParser httpRequestParser = new HttpRequestParser();
//        httpRequestParser.parseRequest(s);

//        String messageBody = httpRequestParser.getMessageBody();

//        if (done) {
//            int readable = actualReadableBytes();
//            if (readable == 0) {
//                // if non is readable just return null
//                // https://github.com/netty/netty/issues/1159
//                return;
//            }
//            out.add(buffer.readBytes(readable));
//        } else {
//            int oldSize = out.size();
//            super.decode(ctx, buffer, out);
//            if (failOnMissingResponse) {
//                int size = out.size();
//                for (int i = oldSize; i < size; i++) {
//                    decrement(out.get(i));
//                }
//            }
//        }


    }

}
