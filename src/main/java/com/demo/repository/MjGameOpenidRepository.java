package com.demo.repository;

import com.demo.entity.MjGameOpenid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MjGameOpenidRepository extends JpaRepository<MjGameOpenid,Integer> {

    public Integer countByGameIdAndStatus(Integer gameId, Boolean status);

    public List<MjGameOpenid> findByOpenIdAndStatus(String openId, Boolean status);

    public List<MjGameOpenid> findByGameId(Integer gameId);

    public List<MjGameOpenid> findByGameIdAndStatus(Integer gameId, Boolean status);

}
