package com.spring.rag.service;

import com.spring.rag.helper.DataLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final ChatClient jdbcChatClient;

    public String getInfoFromFile(String question, String userId) {

        return jdbcChatClient.prompt()
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, userId))
                .user(question)
                .call().content();
    }

}