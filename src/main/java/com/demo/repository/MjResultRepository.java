package com.demo.repository;

import com.demo.entity.MjResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MjResultRepository extends JpaRepository<MjResult,Integer> {

    public List<MjResult> findByOpenIdAndGameIdAndStatus(String openId, Integer gameId,Boolean status);

    public List<MjResult> findByOpenIdNotAndGameIdAndStatus(String openId, Integer gameId,Boolean status);

    public List<MjResult> findByOpenIdAndStatusOrderByCreatTimeDesc(String openId, Boolean status);

}
