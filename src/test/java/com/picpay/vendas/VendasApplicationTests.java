package com.picpay.vendas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"app.client-id=test-client",
		"integrations.produto.api-version=v1"
})
class VendasApplicationTests {

	@Test
	void contextLoads() {
	}

}
