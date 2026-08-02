package com.outdoor.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.outdoor.entity.Message;

/**
 * 消息通知服务接口
 */
public interface MessageService extends IService<Message> {

    /**
     * 发送消息
     * @param message 消息信息
     * @return 是否成功
     */
    boolean send(Message message);

    /**
     * 分页查询用户消息
     * @param page 页码
     * @param size 每页数量
     * @param userId 用户ID
     * @param type 消息类型
     * @return 消息列表
     */
    IPage<Message> pageByUser(Integer page, Integer size, Long userId, Integer type);

    /**
     * 获取会话列表（私信）
     * @param page 页码
     * @param size 每页数量
     * @param userId 用户ID
     * @return 会话列表
     */
    IPage<java.util.Map<String, Object>> pageConversations(Integer page, Integer size, Long userId);

    /**
     * 获取与某用户的聊天记录
     * @param userId 当前用户ID
     * @param otherUserId 对方用户ID
     * @return 聊天记录列表
     */
    java.util.List<Message> getChatHistory(Long userId, Long otherUserId);

    /**
     * 标记与某用户的聊天为已读
     * @param userId 当前用户ID
     * @param otherUserId 对方用户ID
     * @return 是否成功
     */
    boolean markChatRead(Long userId, Long otherUserId);

    /**
     * 统计未读消息数
     * @param userId 用户ID
     * @return 未读消息数
     */
    Integer countUnread(Long userId);

    /**
     * 标记消息为已读
     * @param id 消息ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markRead(Long id, Long userId);

    /**
     * 标记所有消息为已读
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markAllRead(Long userId);
}
