package com.bruce.durpc.demo.provider;

import com.bruce.durpc.core.annotation.DuProvider;
import com.bruce.durpc.core.api.RpcRequest;
import com.bruce.durpc.core.api.RpcResponse;
import com.bruce.durpc.core.provider.ProviderBootstrp;
import com.bruce.durpc.core.provider.ProviderConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
@Import({ProviderConfig.class})
public class DurpcDemoProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(DurpcDemoProviderApplication.class, args);
	}


	// 使用http + json来实现序列化

	@Autowired
	ProviderBootstrp providerBootstrp;

	@RequestMapping("/")
	public RpcResponse invoke(@RequestBody RpcRequest request){
		return providerBootstrp.invoke(request);
	}

	@Bean
	ApplicationRunner getRunner(){
		return x -> {
			RpcRequest request = new RpcRequest();
			request.setService("com.bruce.durpc.demo.api.UserService");
			request.setMethodSign("findById@1_int");
			request.setArgs(new Object[]{10});

			RpcResponse rpcResponse = invoke(request);
			System.out.println("return : " + rpcResponse.getData());

			//
			RpcRequest request2 = new RpcRequest();
			request2.setService("com.bruce.durpc.demo.api.UserService");
			request2.setMethodSign("findById@2_int_java.lang.String");
			request2.setArgs(new Object[]{12,"bruce"});

			RpcResponse rpcResponse2 = invoke(request2);
			System.out.println("return : " + rpcResponse2.getData());
		};
	}

}
