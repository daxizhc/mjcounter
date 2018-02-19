package com.demo.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class MjGameOpenid {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer gameId;

    private String openId;

    // 0:close 1:open
    private Boolean status;

    public MjGameOpenid() {
    }

    public MjGameOpenid(Integer gameId, String openId, Boolean status) {
        this.gameId = gameId;
        this.openId = openId;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
