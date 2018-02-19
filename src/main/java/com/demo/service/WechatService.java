package com.demo.service;

import com.demo.entity.MjGame;
import com.demo.entity.MjGameOpenid;
import com.demo.entity.MjResult;
import com.demo.repository.MjGameOpenidRepository;
import com.demo.repository.MjGameRepository;
import com.demo.repository.MjResultRepository;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class WechatService {

    @Autowired
    private MjGameRepository mjGameRepository;

    @Autowired
    private MjGameOpenidRepository mjGameOpenidRepository;

    @Autowired
    private MjResultRepository mjResultRepository;

    private final static Integer MAX_PLAYER_NUM = 4;

    public String processtext(Element root){
        String openId = root.element("FromUserName").getText();
        String content=root.elementText("Content");
        String result = content;

        if (content.equals("o")){
            // 建房,一个人只能创建一个房间,先查询该用户创建的未关闭的房间
            List<MjGame> mjGames = mjGameRepository.findByOpenIdAndEndTimeIsNull(openId);
            if(mjGames.size() == 0){
                MjGame game = new MjGame(openId,new Date());
                game = mjGameRepository.save(game);
                if(game != null){
                    result = "创建4人房间成功~\n房间号：" + game.getGameId() +"\n请输入房间号加入~";
                }
            }else if(mjGames.size() > 0){
                result = "创建房间失败！\n您已创建了以下房间：\n";
                for(MjGame mjGame:mjGames){
                    result += mjGame.getGameId()+"\n";
                }
                result += "输入c关闭您所创建的房间";
            }
        }else if(content.equals("c")){
            // 关房,查询该人创建的房间，设置endtime，设置该房间的gameopenid为false
            closeGame(openId);
            result = "关闭房间成功～";
        }else if (isInteger(content)){
            if(content.equals("0")){
                // 看成绩
                List<MjGameOpenid> mjGameOpenids = mjGameOpenidRepository
                        .findByOpenIdAndStatus(openId,true);
                if(mjGameOpenids.size() == 0){
                    result = "您目前还没有加入任何一个房间";
                }else {
                    result = "您在房间"+mjGameOpenids.get(0).getGameId()+"\n";
                    result += getStaticResult(openId, mjGameOpenids.get(0));
                }
            }else if (content.equals("9")){
                // 撤销上次输入
                MjResult lastInput = mjResultRepository.findByOpenIdAndStatusOrderByCreatTimeDesc(openId, true)
                        .get(0);
                lastInput.setStatus(false);
                mjResultRepository.save(lastInput);
                result = "撤销上次输入成功～";
            }else if(content.equals("1") || content.equals("2") || content.equals("3") ||
                    content.equals("4") || content.equals("5")){
                //找到该用户参加的gameId
                MjGameOpenid mjGameOpenid = mjGameOpenidRepository
                        .findByOpenIdAndStatus(openId,true).get(0);
                MjResult mjResult = new MjResult(openId, Integer.parseInt(content), new Date(),
                        mjGameOpenid.getGameId(),true);
                mjResult = mjResultRepository.save(mjResult);
                if(mjResult != null){
                    result = "保存成功～\n";
                    result += getStaticResult(openId, mjGameOpenid);
                }
            }else {
                //加入一个房间，先退出之前的房间
                exitGames(openId);
                Integer gameId = Integer.parseInt(content);
                Integer num = mjGameOpenidRepository.countByGameIdAndStatus(gameId, true);
                //房间人数,暂定为4
                if(num >= MAX_PLAYER_NUM){
                    result = "加入房间失败！\n房间人数已满！";
                }else if(mjGameRepository.exists(gameId)){
                    MjGameOpenid mjGameOpenid = new MjGameOpenid(gameId, openId, true);
                    mjGameOpenid = mjGameOpenidRepository.save(mjGameOpenid);
                    if(mjGameOpenid != null){
                        num = mjGameOpenidRepository.countByGameIdAndStatus(gameId, true);
                        result = "加入房间成功～\n当前房间人数：" + num;
                    }
                }else {
                    result = "加入房间失败！\n不存在此房间号！";
                }
            }
        }

        return result;
    }

    //获取统计结果
    private String getStaticResult(String openId, MjGameOpenid mjGameOpenid) {
        String result;
        List<MjResult> mjResultList = mjResultRepository.findByOpenIdAndGameIdAndStatus(openId,
                mjGameOpenid.getGameId(),true);

        Long zimo = mjResultList.stream().filter(mjr->mjr.getResult() == 1).count();
        Long baotou = mjResultList.stream().filter(mjr->mjr.getResult() == 2).count();
        Long piaocai = mjResultList.stream().filter(mjr->mjr.getResult() == 3).count();
        Long gangpiao = mjResultList.stream().filter(mjr->mjr.getResult() == 4).count();
        Long ganggangpiao = mjResultList.stream().filter(mjr->mjr.getResult() == 5).count();

        mjResultList = mjResultRepository.findByOpenIdNotAndGameIdAndStatus(openId,
                mjGameOpenid.getGameId(),true);
        Long lzimo = mjResultList.stream().filter(mjr->mjr.getResult() == 1).count();
        Long lbaotou = mjResultList.stream().filter(mjr->mjr.getResult() == 2).count();
        Long lpiaocai = mjResultList.stream().filter(mjr->mjr.getResult() == 3).count();
        Long lgangpiao = mjResultList.stream().filter(mjr->mjr.getResult() == 4).count();
        Long lganggangpiao = mjResultList.stream().filter(mjr->mjr.getResult() == 5).count();

        result = "您胡了：\n" +
                zimo + "把自摸\n"+
                baotou + "把暴头\n"+
                piaocai + "把飘财\n"+
                gangpiao + "把杠飘\n"+
                ganggangpiao + "把杠杠飘\n"+
                "您输了：\n" +
                lzimo + "把自摸\n"+
                lbaotou + "把暴头\n"+
                lpiaocai + "把飘财\n"+
                lgangpiao + "把杠飘\n"+
                lganggangpiao + "把杠杠飘\n"+
        "赚了："+ ((zimo*3+baotou*5+piaocai*10+gangpiao*20+ganggangpiao*40)*(MAX_PLAYER_NUM-1)
                -(lzimo*3+lbaotou*5+lpiaocai*10+lgangpiao*20+lganggangpiao*40))+"元！";

        return result;
    }

    //判断字符串是否为int类型
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    //退出该用户之前的房间
    public void exitGames(String userName){
        List<MjGameOpenid> joinedGames = mjGameOpenidRepository.findByOpenIdAndStatus(userName, true);
        for(MjGameOpenid joinedGame : joinedGames){
            joinedGame.setStatus(false);
            mjGameOpenidRepository.save(joinedGame);
        }
    }

    //关闭该用户创建的房间
    public void closeGame(String userName){
        List<MjGame> mjGames = mjGameRepository.findByOpenIdAndEndTimeIsNull(userName);
        for(MjGame mjGame:mjGames){
            //先设置该game下的gameopenid的status为false
            List<MjGameOpenid> mjGameOpenids = mjGameOpenidRepository.findByGameId(mjGame.getGameId());
            mjGameOpenids.forEach(i -> i.setStatus(false));
            mjGameOpenids.forEach(i -> mjGameOpenidRepository.save(i));
            mjGame.setEndTime(new Date());
            mjGameRepository.save(mjGame);
        }
    }


}
