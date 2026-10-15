package br.com.sankhya.addon.requisicoes.util;

public enum StatusRequisicaoEnum {
    REALIZANDO("R"),
    PENDENTE("P"),
    FINALIZADA("F");

    private String valor;

    StatusRequisicaoEnum(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return this.valor;
    }
}
