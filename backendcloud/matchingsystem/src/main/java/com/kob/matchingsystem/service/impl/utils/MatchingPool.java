package com.kob.matchingsystem.service.impl.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class MatchingPool extends Thread {
    private static List<Player> players = new ArrayList<>();
    private ReentrantLock lock = new ReentrantLock();
    private static RestTemplate restTemplate;
    private final static String startGameUrl = "http://127.0.0.1:3000/pk/start/game/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        MatchingPool.restTemplate = restTemplate;
    }

    public void addPlayer(Integer userId, Integer rating) {
        lock.lock();
        try {
            players.add(new Player(userId, rating, 0));
        } finally {
          lock.unlock();
        }
    }

    public void removePlayer(Integer userId) {
        lock.lock();
        try {
            List<Player> newPlayers = new ArrayList<>();
            for (Player player : players) {
                if (!player.getUserId().equals(userId)) {
                    newPlayers.add(player);
                }
            }
            players = newPlayers;
        } finally {
            lock.unlock();
        }
    }

    private void increaseWatingTime() { //将所有当前玩家的等待时间+1
        lock.lock();
        try {
            for (Player player : players) {
                player.setWaitingTime(player.getWaitingTime() + 1);
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean checkMatched(Player a, Player b) { // 判断两名玩家是否匹配
        int ratingDelta = Math.abs(a.getRating() - b.getRating());
        return ratingDelta <= Math.min(a.getWaitingTime(), b.getWaitingTime()) * 10;    // 设定策略： 都能接受才匹配

    }

    private void sendResult(Player a, Player b) { // 返回匹配结果
        System.out.println("send result: " + a + " " + b);
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("a_id", a.getUserId().toString());
        data.add("b_id", b.getUserId().toString());
        restTemplate.postForObject(startGameUrl, data, String.class);
    }

    private void matchPlayers() { // 尝试匹配所有玩家
        System.out.println("match Players: " + players.toString());
        lock.lock();
        try {
            boolean[] used = new boolean[players.size()];
            for (int i = 0; i < players.size(); i++) {                  // 从前往后匹配可以优先匹配等待时间最长的玩家
                if (used[i]) continue;
                for (int j = i + 1; j < players.size(); j++) {
                    if (used[j]) continue;
                    Player a = players.get(i);
                    Player b = players.get(j);
                    if (checkMatched(a, b)) {
                        used[i] = used[j] = true;
                        sendResult(a, b);
                        break;
                    }
                }
            }
            List <Player> newPlayers = new ArrayList<>();
            for (int i = 0; i < players.size(); i++) {
                if (!used[i]) newPlayers.add(players.get(i));
            }
            players = newPlayers;

        } finally {
            lock.unlock();
        }
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(1000);
                increaseWatingTime();
                matchPlayers();
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }

        }

    }
}
