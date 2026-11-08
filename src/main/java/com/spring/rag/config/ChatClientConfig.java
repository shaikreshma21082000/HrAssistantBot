package com.spring.rag.config;

import com.spring.rag.advisor.TokenUsageAdvisor;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.List;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;


@Configuration
@RequiredArgsConstructor
public class ChatClientConfig {

    @Value("classpath:/promptTemplate/HrSystemPrompt.st")
    Resource systemPromptResource;

    @Bean
    public ChatMemory jdbcChatMemory(JdbcChatMemoryRepository repository) {
       return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                 .maxMessages(6)
                .build();
    }


    @Bean
    public  RetrievalAugmentationAdvisor retrivalArguementAdvisor(VectorStore vectorStore){
        VectorStoreDocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .topK(3)
                .similarityThreshold(0.5)
                .build();

        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .build();

    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel,ChatMemory jdbcChatMemory, RetrievalAugmentationAdvisor retrievalAugmentationAdvisor){
        List<String> sensitiveWords = List.of("password", "personal info", "name", "address", "phone number");

        MessageChatMemoryAdvisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(jdbcChatMemory).order(3).build();
        SafeGuardAdvisor safeGuardAdvisor = SafeGuardAdvisor.builder().order(2).sensitiveWords(sensitiveWords).failureResponse("Sorry I can't respond to it. Try to reframe that question").build();

        return ChatClient.builder(chatModel)
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor(),new TokenUsageAdvisor(),safeGuardAdvisor,messageChatMemoryAdvisor,retrievalAugmentationAdvisor))
                .defaultSystem(systemPromptResource)
                .build();
    }

}
