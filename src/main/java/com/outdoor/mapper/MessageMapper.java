package com.outdoor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.outdoor.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * 消息通知Mapper接口
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 分页查询用户消息（带发送者信息）
     * @param page 分页参数
     * @param userId 用户ID
     * @param type 消息类型
     * @return 消息列表
     */
    IPage<Message> selectMessagePage(Page<Message> page, 
                                     @Param("userId") Long userId,
                                     @Param("type") Integer type);

    /**
     * 查询会话列表（私信）
     * @param page 分页参数
     * @param userId 用户ID
     * @return 会话列表
     */
    IPage<Map<String, Object>> selectConversations(Page<Map<String, Object>> page,
                                                   @Param("userId") Long userId);

    /**
     * 查询与某用户的聊天记录
     * @param userId 当前用户ID
     * @param otherUserId 对方用户ID
     * @return 聊天记录列表
     */
    List<Message> selectChatHistory(@Param("userId") Long userId,
                                    @Param("otherUserId") Long otherUserId);

    /**
     * 统计未读消息数
     * @param userId 用户ID
     * @return 未读消息数
     */
    Integer countUnread(@Param("userId") Long userId);

    /**
     * 标记所有消息为已读
     * @param userId 用户ID
     */
    void markAllRead(@Param("userId") Long userId);
}
