package com.cuijian.aimeeting.monitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/9/30 13:49
 **/

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonitorContext implements Serializable {
    private String userId;


}
