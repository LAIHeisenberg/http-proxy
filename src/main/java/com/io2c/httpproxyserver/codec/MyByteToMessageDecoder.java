package com.io2c.httpproxyserver.codec;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.io2c.httpproxyserver.parser.HttpRequestParser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;


public class MyByteToMessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        int c = in.readableBytes();
        byte[] readBytes = new byte[c];
        in.getBytes(0,readBytes);
        String inHttpRequestRaw = new String(readBytes, 0, c, CharsetUtil.UTF_8);
        try {
            HttpRequestParser httpRequestParser = new HttpRequestParser();
            httpRequestParser.parseRequest(inHttpRequestRaw);
            if ("HTTP/1.1 200".equals(httpRequestParser.getRequestLine().trim())){
                String rewriteHeader = httpRequestParser.getHeaderParam("X-Rewrite");
                if (Objects.nonNull(rewriteHeader)){
                    String messageBody = httpRequestParser.getMessageBody();
                    JSONObject respJson = JSONObject.parseObject(messageBody);

                    JSONArray content = respJson.getJSONArray("content");
                    Iterator<Object> iterator = content.iterator();
                    while (iterator.hasNext()){
                        JSONObject next = (JSONObject) iterator.next();
                        String phone = next.getString("phone");
                        String hideStr = phone.substring(3, 8);
                        phone = phone.replace(hideStr,"******");
                        next.put("phone", phone);
                        String email = next.getString("email");
                        String hideEmailStr = email.substring(1, email.indexOf("@"));
                        email = email.replace(hideEmailStr,"****");
                        next.put("email", email);
                    }
                    respJson.put("content", content);
                    httpRequestParser.setMessageBody(respJson.toJSONString());
                    in.clear();
                    byte[] bytes = httpRequestParser.getMessageBody().getBytes();
                    httpRequestParser.setHeaderParam("Content-Length", bytes.length+"");
                    String rewriteHttpRequest = httpRequestParser.getHttpRequestRaw();
                    in = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(rewriteHttpRequest, CharsetUtil.UTF_8));

                    System.out.println("bytes length: "+ bytes.length);
                    System.out.println(rewriteHttpRequest);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        out.add(in.readBytes(in.readableBytes()));
    }

}
