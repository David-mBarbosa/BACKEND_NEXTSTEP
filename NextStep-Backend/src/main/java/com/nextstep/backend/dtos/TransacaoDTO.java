package com.nextstep.backend.dtos;

public record TransacaoDTO(
    String date, 
    String type, 
    String category, 
    String description, 
    Double amount
) {}