package com.kob.backend.service.impl.user.bot;

import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UpdateServiceImpl implements UpdateService {
    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> update(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        int bot_id = Integer.parseInt(data.get("bot_id"));
        String title = data.get("title");
        String description = data.get("description");
        String content = data.get("content");

        Bot bot = botMapper.selectById(bot_id);
        Map<String, String> map = new HashMap<>();

        if (bot == null) {
            map.put("response_message", "Bot不存在或已被删除");
            return map;
        }

        if (!bot.getUserId().equals(user.getId())) {
            map.put("response_message", "没有权限修改该Bot");
            return map;
        }

        if (title == null || title.length() == 0) {
            map.put("response_message", "名称不能为空");
            return map;
        }

        if (title.length() > 100) {
            map.put("response_message", "名称长度不能大于100");
            return map;
        }

        if (description == null || description.length() == 0) {
            description = "这个用户很懒，什么也没留下~";
        }

        if (description.length() > 1000) {
            map.put("response_message", "描述长度不能大于1000");
            return map;
        }

        if (content == null || content.length() == 0) {
            map.put("response_message", "代码不能为空");
            return map;
        }

        if (content.length() > 10000) {
            map.put("response_message", "代码长度不能大于100");
            return map;
        }

        Date now = new Date();
        Bot newBot = new Bot(bot_id, user.getId(), title, description, content, bot.getRating(), bot.getCreateTime(), now);
        botMapper.updateById(newBot);

        map.put("response_message", "success");
        return map;
    }
}
