package com.outdoor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.outdoor.common.Constants;
import com.outdoor.entity.Message;
import com.outdoor.mapper.MessageMapper;
import com.outdoor.service.MessageService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 消息通知服务实现类
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Override
    public boolean send(Message message) {
        message.setIsRead(0);
        return save(message);
    }

    @Override
    public IPage<Message> pageByUser(Integer page, Integer size, Long userId, Integer type) {
        Page<Message> pageParam = new Page<>(page, size);
        return baseMapper.selectMessagePage(pageParam, userId, type);
    }

    @Override
    public IPage<Map<String, Object>> pageConversations(Integer page, Integer size, Long userId) {
        Page<Map<String, Object>> pageParam = new Page<>(page, size);
        return baseMapper.selectConversations(pageParam, userId);
    }

    @Override
    public List<Message> getChatHistory(Long userId, Long otherUserId) {
        return baseMapper.selectChatHistory(userId, otherUserId);
    }

    @Override
    public boolean markChatRead(Long userId, Long otherUserId) {
        LambdaUpdateWrapper<Message> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Message::getUserId, userId)
               .eq(Message::getSenderId, otherUserId)
               .eq(Message::getType, Constants.MessageType.CHAT)
               .eq(Message::getIsRead, 0)
               .set(Message::getIsRead, 1);
        update(wrapper);
        return true;
    }

    @Override
    public Integer countUnread(Long userId) {
        return baseMapper.countUnread(userId);
    }

    @Override
    public boolean markRead(Long id, Long userId) {
        Message message = getById(id);
        if (message == null || !message.getUserId().equals(userId)) {
            return false;
        }
        message.setIsRead(1);
        return updateById(message);
    }

    @Override
    public boolean markAllRead(Long userId) {
        baseMapper.markAllRead(userId);
        return true;
    }
}
