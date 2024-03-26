package com.bruce.durpc.core.api;

import lombok.Data;
import lombok.ToString;

/**
 * @date 2024/3/7
 */
@Data
@ToString
public class RpcRequest {

    private String service; //接口
    private String methodSign; //方法签名
    private Object[] args; //参数


}
