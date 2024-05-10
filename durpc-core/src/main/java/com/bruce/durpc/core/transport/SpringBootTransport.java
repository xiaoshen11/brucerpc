package com.bruce.durpc.core.transport;

import com.bruce.durpc.core.api.RpcRequest;
import com.bruce.durpc.core.api.RpcResponse;
import com.bruce.durpc.core.provider.ProviderInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Transport for spring boot endpoint.
 * @date 2024/5/10
 */
@RestController
public class SpringBootTransport {

    @Autowired
    ProviderInvoker providerInvoker;

    @RequestMapping("/durpc")
    public RpcResponse<Object> invoke(@RequestBody RpcRequest request){
        return providerInvoker.invoke(request);
    }
}
