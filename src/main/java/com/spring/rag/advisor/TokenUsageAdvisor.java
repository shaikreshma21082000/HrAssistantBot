package com.spring.rag.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientMessageAggregator;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import reactor.core.publisher.Flux;

public class TokenUsageAdvisor implements CallAdvisor,StreamAdvisor {

    private static final Logger logger = LoggerFactory.getLogger(TokenUsageAdvisor.class);

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {

        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        ChatResponseMetadata metadata = chatClientResponse.chatResponse().getMetadata();
        if (metadata != null) {
            logger.info("Token count ="+metadata.getUsage().getTotalTokens());
        }

        return chatClientResponse;
    }

    @Override
    public String getName() {
        return "TokenUsageData";
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest request, StreamAdvisorChain chain) {
        logger.info(request.toString());
        Flux<ChatClientResponse> responses = chain.nextStream(request);
        return new ChatClientMessageAggregator().aggregateChatClientResponse(responses, this::logResponse);
    }

    private void logResponse(ChatClientResponse response) {
        if (response == null || response.chatResponse() == null) {
            return;
        }
        ChatResponseMetadata metadata = response.chatResponse().getMetadata();
        if (metadata != null && metadata.getUsage() != null) {
            logger.info("Token count = {}", metadata.getUsage().getTotalTokens());
        }
    }
}
