package com.projetoestudo.ia.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projetoestudo.ia.record.ProcessoDto;

public class JsonExtractor {

    public static String extrairJson(String resposta) {
        int start = resposta.indexOf("{");
        int end = resposta.lastIndexOf("}");

        if (start != -1 && end != -1 && end > start) {
            return resposta.substring(start, end + 1).trim();
        }
        return null;
    }

    public static ProcessoDto parseJsonSeguro(String resposta) {
        String jsonLimpo = extrairJson(resposta);
        if (jsonLimpo == null) {
            return new ProcessoDto("Erro: JSON não encontrado na resposta.");
        }

        try {
            return new ObjectMapper().readValue(jsonLimpo, ProcessoDto.class);
        } catch (Exception e) {
            return new ProcessoDto("Erro ao converter JSON: " + jsonLimpo);
        }
    }

}
