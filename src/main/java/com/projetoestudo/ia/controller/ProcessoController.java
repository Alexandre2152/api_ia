package com.projetoestudo.ia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projetoestudo.ia.record.ProcessoDto;
import com.projetoestudo.ia.service.PdfProcessoExtractor;
import com.projetoestudo.ia.utils.JsonExtractor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/processo")
public class ProcessoController {

    private final PdfProcessoExtractor extractor;

    @Autowired
    @Qualifier("ollamaChatModel")
    private final OllamaChatModel ollamaChatModel;

    @Value("${ocr.tessdata-path}") // Exemplo: src/main/resources/tessdata
    private String tessdataPath;

    public ProcessoController(PdfProcessoExtractor extractor, OllamaChatModel ollamaChatModel) {
        this.extractor = extractor;
        this.ollamaChatModel = ollamaChatModel;
    }

    @PostMapping(value = "/extrair", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProcessoDto extrairProcesso(@RequestParam("file") MultipartFile file) throws Exception {
        String numero = extractor.extrairTextoOuOCR(file, tessdataPath);

        if (numero == null) {
            return new ProcessoDto("Número de processo não encontrado");
        }

        // Usar LLaMA para validar ou gerar uma resposta contextual (opcional)
        String prompt = "O número de processo encontrado neste documento é: " + numero + ". Responda no formato JSON assim: { \"numeroProcesso\": \"...\" }";

        String respostaJson = ollamaChatModel.call(new Prompt(new UserMessage(prompt)))
                .getResult()
                .getOutput()
                .getText();

        return JsonExtractor.parseJsonSeguro(respostaJson);
    }

}
