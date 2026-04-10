package com.nextstep.backend.dtos;

public class RelatorioCategoriaDTO {
    private String category;
    private String type;
    private Double total;

    public RelatorioCategoriaDTO(String category, String type, Double total) {
        this.category = category;
        this.type = type;
        this.total = total;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
}