package com.demo.controller;

import com.demo.service.WechatService;
import com.demo.utils.Sha1Util;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class WechatController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private WechatService wechatService;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test(){
        return "test";
    }

    @RequestMapping(value = "/wechat")
    public void wechat(){
        String signature=request.getParameter("signature");
        String timestamp=request.getParameter("timestamp");
        String nonce=request.getParameter("nonce");
        String echostr=request.getParameter("echostr");
        if(StringUtils.isNoneBlank(signature)&&StringUtils.isNoneBlank(timestamp)&&
                StringUtils.isNoneBlank(nonce)&&StringUtils.isNoneBlank(echostr)){
            String token="zhanghc";
            List<String> list = new ArrayList<String>();
            list.add(token);
            list.add(timestamp);
            list.add(nonce);
            Collections.sort(list);
            String str = "";
            for (String string : list) {
                str+=string;
            }
            String shaStr = Sha1Util.getSha1(str);
//			System.out.println(shaStr);
            if(shaStr.trim().equals(signature.trim())){
                try {
//					System.out.println(echostr);
                    response.getWriter().write(echostr);
//					System.out.println("success");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return;
        }

        try {
            InputStream inputStream=request.getInputStream();
            SAXReader reader = new SAXReader();
            Document document=reader.read(inputStream);
            Element root=document.getRootElement();
            //获取消息类型
            String msgType=root.elementText("MsgType");
            //获取事件类型
            String event=root.elementText("Event");

            //发送方帐号（一个OpenID）
            String fromUserName=root.element("FromUserName").getText();
            //开发者微信号
            String toUserName=root.element("ToUserName").getText();
            String content=root.elementText("Content");

            if (StringUtils.isNotBlank(msgType) && msgType.trim().equals("text")) {
//                System.out.println(fromUserName);
//                System.out.println(content);
                content = wechatService.processtext(root);

            }else if(StringUtils.isNotBlank(msgType) && msgType.trim().equals("event")){
                if(event.equals("subscribe")){
                    content = "欢迎关注杭州黑科技！\n" +
                            "-----功能列表-----\n" +
                            "杭州麻将计分器\n" +
                            "（目前只支持4人）\n" +
                            "-----使用说明-----\n" +
                            "回复o创建麻将房间\n" +
                            "回复c关闭麻将房间\n" +
                            "回复房间号加入房间\n" +
                            "回复1表示自摸\n" +
                            "回复2表示暴头\n" +
                            "回复3表示飘财\n" +
                            "回复4表示杠飘\n" +
                            "回复5表示杠杠飘\n" +
                            "注:1~5只能由每局赢家输入\n"+
                            "回复9撤销上次输入\n" +
                            "回复0查询输赢\n" +
                            "-----输赢分数-----\n" +
                            "自摸3分，暴头5分\n" +
                            "飘财10分，杠飘20分\n" +
                            "杠杠飘40分";
                }
            }

//               输出内容
            StringBuffer str=new StringBuffer();
            str.append("<xml>");
            str.append("<ToUserName><![CDATA["+fromUserName+"]]></ToUserName>");
            str.append("<FromUserName><![CDATA["+toUserName+"]]></FromUserName>");
            str.append("<CreateTime>"+System.currentTimeMillis()+"</CreateTime>");
            str.append("<MsgType><![CDATA[text]]></MsgType>");
            str.append("<Content><![CDATA["+content+"]]></Content>");
            str.append("</xml>");
            send(str.toString());

            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;

    }

    public void send(String string){
        try {
            //防止中文回复乱码。
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
