package com.spring.rag;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
class MemoryStrategyApplicationTests {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	void initializesChatMemoryTable() {
		jdbcTemplate.queryForObject("SELECT COUNT(*) FROM SPRING_AI_CHAT_MEMORY", Integer.class);
	}

}
