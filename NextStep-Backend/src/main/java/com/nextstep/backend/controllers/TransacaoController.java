package com.nextstep.backend.controllers;

import com.nextstep.backend.dtos.TransacaoDTO;
import com.nextstep.backend.dtos.RelatorioCategoriaDTO;
import com.nextstep.backend.models.Transacao;
import com.nextstep.backend.models.Usuario;
import com.nextstep.backend.repositories.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoRepository transacaoRepository;

    private Usuario getUsuarioLogado() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PostMapping
    public ResponseEntity<Transacao> criarTransacao(@RequestBody TransacaoDTO data) {
        Usuario usuarioLogado = getUsuarioLogado();

        Transacao t = new Transacao();
        t.setDate(data.date());
        t.setType(data.type());
        t.setCategory(data.category());
        t.setDescription(data.description());
        t.setAmount(data.amount());
        t.setUsuario(usuarioLogado);

        return ResponseEntity.ok(transacaoRepository.save(t));
    }

    @GetMapping
    public ResponseEntity<List<Transacao>> listarTransacoes() {
        Usuario usuarioLogado = getUsuarioLogado();
        return ResponseEntity.ok(transacaoRepository.findByUsuarioId(usuarioLogado.getId()));
    }

    @GetMapping("/resumo")
    public ResponseEntity<Map<String, Double>> obterResumo() {
        Usuario usuarioLogado = getUsuarioLogado();
        String id = usuarioLogado.getId();

        Double totalReceitas = transacaoRepository.sumReceitasByUsuarioId(id);
        Double totalDespesas = transacaoRepository.sumDespesasByUsuarioId(id);
        
        if (totalReceitas == null) totalReceitas = 0.0;
        if (totalDespesas == null) totalDespesas = 0.0;
        
        Double saldo = totalReceitas - totalDespesas;

        Map<String, Double> resumo = new HashMap<>();
        resumo.put("receitas", totalReceitas);
        resumo.put("despesas", totalDespesas);
        resumo.put("saldo", saldo);

        return ResponseEntity.ok(resumo);
    }

    @GetMapping("/relatorio")
    public ResponseEntity<List<RelatorioCategoriaDTO>> obterRelatorio() {
        Usuario usuarioLogado = getUsuarioLogado();
        return ResponseEntity.ok(transacaoRepository.findRelatorioByUsuarioId(usuarioLogado.getId()));
    }

    // --- NOVAS ROTAS DE EDIÇÃO E EXCLUSÃO ---

    @PutMapping("/{id}")
    public ResponseEntity<Transacao> atualizarTransacao(@PathVariable Long id, @RequestBody TransacaoDTO data) {
        Usuario usuarioLogado = getUsuarioLogado();
        Optional<Transacao> transacaoOpt = transacaoRepository.findById(id);

        if (transacaoOpt.isPresent()) {
            Transacao t = transacaoOpt.get();
            
            // Trava de segurança: garantir que o usuário não edite a transação de outro
            if (!t.getUsuario().getId().equals(usuarioLogado.getId())) {
                return ResponseEntity.status(403).build();
            }

            t.setDate(data.date());
            t.setType(data.type());
            t.setCategory(data.category());
            t.setDescription(data.description());
            t.setAmount(data.amount());

            return ResponseEntity.ok(transacaoRepository.save(t));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTransacao(@PathVariable Long id) {
        Usuario usuarioLogado = getUsuarioLogado();
        Optional<Transacao> transacaoOpt = transacaoRepository.findById(id);

        if (transacaoOpt.isPresent()) {
            Transacao t = transacaoOpt.get();
            
            // Trava de segurança: garantir que o usuário não delete a transação de outro
            if (!t.getUsuario().getId().equals(usuarioLogado.getId())) {
                return ResponseEntity.status(403).build();
            }

            transacaoRepository.delete(t);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}