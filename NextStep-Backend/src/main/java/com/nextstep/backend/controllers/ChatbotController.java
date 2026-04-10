package com.nextstep.backend.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextstep.backend.dtos.ChatRequestDTO;
import com.nextstep.backend.dtos.ChatResponseDTO;
import com.nextstep.backend.models.Transacao;
import com.nextstep.backend.models.Usuario;
import com.nextstep.backend.repositories.TransacaoRepository;
import com.nextstep.backend.services.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/chatbot")
public class ChatbotController {

    @Autowired
    private OpenAiService openAiService;

    @Autowired
    private TransacaoRepository transacaoRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Usuario getUsuarioLogado() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PostMapping
    public ResponseEntity<ChatResponseDTO> conversar(@RequestBody ChatRequestDTO request) {
        Usuario usuarioLogado = getUsuarioLogado();

        // 1. Envia a mensagem para a OpenAI extrair os dados
        String jsonRespostaIA = openAiService.processarMensagem(request.getMessage());

        try {
            // 2. Lê a resposta estruturada (JSON) da IA
            JsonNode iaNode = objectMapper.readTree(jsonRespostaIA);
            String intent = iaNode.path("intent").asText();
            String reply = iaNode.path("reply").asText();

            // 3. Se a intenção for REGISTRAR, salva no banco automaticamente!
            if ("REGISTRAR".equalsIgnoreCase(intent)) {
                Transacao t = new Transacao();
                t.setType(iaNode.path("type").asText());
                t.setAmount(iaNode.path("amount").asDouble());
                t.setCategory(iaNode.path("category").asText());
                t.setDescription(iaNode.path("description").asText());
                t.setDate(LocalDate.now().toString()); 
                t.setUsuario(usuarioLogado);

                transacaoRepository.save(t);
            }

            // 4. Devolve a resposta humana
            return ResponseEntity.ok(new ChatResponseDTO(reply));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new ChatResponseDTO("Desculpe, entendi o que você disse, mas os dados não vieram estruturados corretamente para salvar."));
        }
    }
}