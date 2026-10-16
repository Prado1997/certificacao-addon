package br.com.sankhya.addon.requisicoes.dto;

import lombok.Data;

@Data
public class RequisicaoDTO {
    private Long idParceiro;
    private Long idPedido;
    private Long idCentroResultado;
    private String emailParceiro;
}
