package com.demo.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class MjResult {

    @Id
    @GeneratedValue
    private Integer resultId;

    private String openId;

    private Integer result;

    private Date creatTime;

    private Integer gameId;

    private Boolean status;

    public MjResult() {
    }

    public MjResult(String openId, Integer result, Date creatTime, Integer gameId, Boolean status) {
        this.openId = openId;
        this.result = result;
        this.creatTime = creatTime;
        this.gameId = gameId;
        this.status = status;
    }

    public Integer getResultId() {
        return resultId;
    }

    public void setResultId(Integer resultId) {
        this.resultId = resultId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public Date getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Date creatTime) {
        this.creatTime = creatTime;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
