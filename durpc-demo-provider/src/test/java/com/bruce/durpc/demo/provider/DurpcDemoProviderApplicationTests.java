package com.bruce.durpc.demo.provider;

import com.bruce.durpc.core.test.TestZKServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DurpcDemoProviderApplicationTests {

	static TestZKServer zkServer = new TestZKServer();

	@BeforeAll
	static void init() {
		zkServer.start();
	}

	@Test
	void contextLoads() {
	}

	@AfterAll
	static void destroy() {
		zkServer.stop();
	}

}
