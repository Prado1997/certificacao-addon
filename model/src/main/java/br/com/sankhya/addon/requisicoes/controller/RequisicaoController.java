package br.com.sankhya.addon.requisicoes.controller;

import br.com.sankhya.addon.requisicoes.component.RequisicaoComponent;
import br.com.sankhya.addon.requisicoes.dto.RequisicaoDTO;
import br.com.sankhya.studio.annotations.Service;
import br.com.sankhya.studio.annotations.enums.EJBTransactionType;
import helper.JapeHelper;

import java.util.Collection;

@Service(serviceName = "RequisicaoControllerSP",
    transactionType = EJBTransactionType.Required)
public class RequisicaoController {

    private RequisicaoComponent requisicaoComponent;

    public RequisicaoController() {
        this.requisicaoComponent = new RequisicaoComponent();
    }

    public String testeV0() {
        return "Teste Requisicao Controller";
    }

    public RequisicaoDTO salvar(RequisicaoDTO requisicao) throws Exception {
        JapeHelper.executeWithoutTX(() ->
            this.requisicaoComponent.criarRequisicao(requisicao.getIdParceiro(), requisicao.getIdPedido(), requisicao.getIdCentroResultado())
        );
        return requisicao;
    }

    public Collection<RequisicaoDTO> listar(Long idParceiro) throws Exception {
        return JapeHelper.executeWithoutTX(() ->
            this.requisicaoComponent.listarRequisicoesParceiro(idParceiro)
        );
    }

}
