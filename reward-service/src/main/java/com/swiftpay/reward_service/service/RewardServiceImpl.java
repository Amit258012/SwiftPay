package com.swiftpay.reward_service.service;

import com.swiftpay.reward_service.entity.Reward;
import com.swiftpay.reward_service.repository.RewardRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

public class RewardServiceImpl implements RewardService {


    private RewardRepository rewardRepository;

    @Autowired
    public RewardServiceImpl(RewardRepository rewardRepository){
        this.rewardRepository = rewardRepository;
    }

    @Override
    public List<Reward> getRewardsByUserId(Long id) {
        return rewardRepository.findByUserId(id);
    }

    @Override
    public Reward sendReward(Reward reward) {
        reward.setSentAt(LocalDateTime.now());
        return rewardRepository.save(reward);
    }
}
