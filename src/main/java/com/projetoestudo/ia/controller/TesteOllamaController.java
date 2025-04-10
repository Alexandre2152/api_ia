package com.projetoestudo.ia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projetoestudo.ia.record.RecomenadoDto;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/ollama")
public class TesteOllamaController {

    private final OllamaChatModel ollamaChatModel;

    public TesteOllamaController(OllamaChatModel ollamaChatModel) {
        this.ollamaChatModel = ollamaChatModel;
    }

    @RequestMapping("/informations")
    public String getInformation(@RequestParam(value = "message", defaultValue = "Quais os 3 ultimos livros bestsellers dos ultimos anos?") String message) {
        return ollamaChatModel.call(message);
    }

    @RequestMapping("/informations1")
    public ChatResponse getInformation1(@RequestParam(value = "message", defaultValue = "Quais os 3 ultimos livros bestsellers dos ultimos anos?") String message) {
        return ollamaChatModel.call(new Prompt(message));
    }

    @RequestMapping("/informations2")
    public String getInformation2(@RequestParam(value = "book", defaultValue = "Dom Quixote") String book) {
        PromptTemplate promptTemplate = new PromptTemplate("Por favor me forneça uma breve sinopse do livro {book}.");
        promptTemplate.add("book", book);
        var response = this.ollamaChatModel.call(promptTemplate.create());
        return response.getResult().getOutput().getText();
    }

    @RequestMapping("/informations3")
    public Flux<String> getInformationStream(@RequestParam(value = "message", defaultValue = "Quais os livros 3 ultimos bestsellers dos ultimos anos?") String message) {
        return ollamaChatModel.stream(message);
    }

    @RequestMapping("/informations4")
    public Flux<ChatResponse> getInformationStream2(@RequestParam(value = "message", defaultValue = "Quais os 3 ultimos livros bestsellers dos ultimos anos?") String message) {
        return ollamaChatModel.stream(new Prompt(message));
    }

    @RequestMapping("/informations5")
    public RecomenadoDto getInformationRecord(@RequestParam(value = "message", defaultValue = "Quais os 3 ultimos livros bestsellers dos ultimos anos?") String message) {
        var resposta = ollamaChatModel.call(new Prompt(message))
                .getResult()
                .getOutput()
                .getText();

        try {
            return new ObjectMapper().readValue(resposta, RecomenadoDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter resposta para RecomenadoDto", e);
        }
    }
}
