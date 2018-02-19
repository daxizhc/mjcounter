package com.demo.repository;

import com.demo.entity.MjGame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MjGameRepository extends JpaRepository<MjGame,Integer> {
    public List<MjGame> findByOpenIdAndEndTimeIsNull(String openId);
}
