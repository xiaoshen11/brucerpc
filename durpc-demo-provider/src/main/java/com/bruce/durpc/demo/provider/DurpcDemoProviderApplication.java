package com.bruce.durpc.demo.provider;

import com.bruce.duconfig.client.annotation.EnableDuConfig;
import com.bruce.durpc.core.api.RpcRequest;
import com.bruce.durpc.core.api.RpcResponse;
import com.bruce.durpc.core.config.ProviderConfig;
import com.bruce.durpc.core.config.ProviderProperties;
import com.bruce.durpc.core.provider.ProviderInvoker;
import com.bruce.durpc.core.transport.SpringBootTransport;
import com.bruce.durpc.demo.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableDuConfig
@Import({ProviderConfig.class})
public class DurpcDemoProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(DurpcDemoProviderApplication.class, args);
	}


	// 使用http + json来实现序列化

	@Autowired
	ProviderInvoker providerInvoker;

	@Autowired
	UserService userService;

	@Autowired
	SpringBootTransport transport;

	@Autowired
	ProviderProperties providerProperties;

	@RequestMapping("/")
	public RpcResponse invoke(@RequestBody RpcRequest request){
		return providerInvoker.invoke(request);
	}

	@RequestMapping("/metas")
	public String meta() {
		System.out.println(System.identityHashCode(providerProperties.getMetas()));
		return providerProperties.getMetas().toString();
	}

	@RequestMapping("/ports")
	public RpcResponse<String> ports(@RequestParam("ports") String ports){
		userService.setTimeoutPorts(ports);
		RpcResponse<String> response = new RpcResponse<>();
		response.setStatus(true);
		response.setData("OK:" + ports);
		return response;
	}

	@Bean
	ApplicationRunner getRunner(){
		return x -> {
			RpcRequest request = new RpcRequest();
			request.setService("com.bruce.durpc.demo.api.UserService");
			request.setMethodSign("findById@1_int");
			request.setArgs(new Object[]{10});

			RpcResponse rpcResponse = transport.invoke(request);
			System.out.println("return : " + rpcResponse.getData());

			RpcRequest request2 = new RpcRequest();
			request2.setService("com.bruce.durpc.demo.api.UserService");
			request2.setMethodSign("findById@2_int_java.lang.String");
			request2.setArgs(new Object[]{12,"bruce"});

			RpcResponse rpcResponse2 = transport.invoke(request2);
			System.out.println("return : " + rpcResponse2.getData());
		};
	}

}
