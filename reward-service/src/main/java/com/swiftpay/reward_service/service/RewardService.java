package com.swiftpay.reward_service.service;

import com.swiftpay.reward_service.entity.Reward;

import java.util.List;

public interface RewardService {
    List<Reward> getRewardsByUserId(Long id);
    Reward sendReward(Reward reward);
}
