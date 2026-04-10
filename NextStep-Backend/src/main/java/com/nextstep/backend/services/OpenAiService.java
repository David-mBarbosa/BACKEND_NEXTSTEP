package com.nextstep.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String processarMensagem(String mensagemUsuario) {
        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // O nosso Prompt de Engenharia à prova de balas
        String systemPrompt = "Você é o assistente financeiro do app NextStep. " +
                "O usuário vai informar uma transação ou fazer uma pergunta. " +
                "Sua tarefa é extrair os dados e retornar ESTRITAMENTE um JSON válido com esta estrutura: " +
                "{\"intent\": \"REGISTRAR\" ou \"CONVERSAR\", \"type\": \"Despesa\" ou \"Receita\", \"amount\": 0.0, \"category\": \"nome da categoria\", \"description\": \"descrição da transação\", \"reply\": \"Sua resposta humana e amigável confirmando a ação ou respondendo a dúvida\"}. " +
                "Não retorne nenhum texto, markdown ou formatação fora do JSON. Apenas as chaves e os valores.";

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4o-mini"); // Modelo atualizado, super rápido e barato
        body.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", mensagemUsuario)
        ));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            
            // Removemos as marcações de Markdown (```json e ```) caso a OpenAI insista em mandá-las
            String conteudo = root.path("choices").get(0).path("message").path("content").asText();
            conteudo = conteudo.replace("```json", "").replace("```", "").trim();
            
            return conteudo;
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"intent\": \"ERRO\", \"reply\": \"Desculpe, tive um problema de conexão com o meu servidor de Inteligência Artificial.\"}";
        }
    }
}