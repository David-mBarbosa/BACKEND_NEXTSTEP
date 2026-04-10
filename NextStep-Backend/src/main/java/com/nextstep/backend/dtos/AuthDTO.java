package com.nextstep.backend.dtos;

// Este é o formato exato do JSON que o seu Angular e Android vão enviar!
public record AuthDTO(String email, String senha) {
}