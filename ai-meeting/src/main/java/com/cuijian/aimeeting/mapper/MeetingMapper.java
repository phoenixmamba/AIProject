package com.cuijian.aimeeting.mapper;

import com.cuijian.aimeeting.entity.Meeting;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/9/26 10:23
 **/

public interface MeetingMapper extends BaseMapper<Meeting> {
    // 如需自定义SQL，可以在这里添加方法并在XML中实现
}
