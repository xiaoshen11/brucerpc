package com.bruce.durpc.core.api;

import lombok.Data;

/**
 * @date 2024/3/7
 */
@Data
public class RpcRequest {

    private String service; //接口
    private String methodSign; //方法签名
    private Object[] args; //参数


}
