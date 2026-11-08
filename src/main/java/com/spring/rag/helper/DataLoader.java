package com.spring.rag.helper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader {

   private final VectorStore vectorStore;

   @Value("classpath:/Zenvora_HR_Policy_Handbook_2026.pdf")
   private Resource resource;


   @PostConstruct
   void init() {
       // STEP 1: Read document using Apache Tika
       TikaDocumentReader reader = new TikaDocumentReader(resource);
       List<Document> documents = reader.read();

       // STEP 2: Add source metadata
       documents.forEach(document -> document.getMetadata().put("HR RELATED INFO", resource.getFilename()));

       // STEP 3: Split into smaller chunks
       TokenTextSplitter splitter = TokenTextSplitter.builder()
                       .withChunkSize(500)
                       .withMinChunkSizeChars(300)
                       .withMinChunkLengthToEmbed(5)
                       .withMaxNumChunks(1000)
                       .build();

       List<Document> chunks = splitter.split(documents);
       vectorStore.add(chunks);

   }

   public String getDataFromVectorStore(String question) {
       StringBuffer data = new StringBuffer();
       SearchRequest searchRequest = SearchRequest.builder()
               .query(question).topK(3).similarityThreshold(0.5).build();

       List<Document> documentList = vectorStore.similaritySearch(searchRequest);
       documentList.stream().forEach( document -> data.append(document.toString()+"\n"));

       return data.toString();
   }

}