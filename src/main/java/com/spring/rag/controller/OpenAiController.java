package com.spring.rag.controller;

import com.spring.rag.service.OpenAiService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class OpenAiController {

    private final OpenAiService openAiService;

    @GetMapping("/ask-hr")
    public String getInfo(@RequestParam String question, @RequestParam String userId) {
        return openAiService.getInfoFromFile(question, userId);
    }

}
