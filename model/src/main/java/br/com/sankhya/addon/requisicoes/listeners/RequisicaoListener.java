package br.com.sankhya.addon.requisicoes.listeners;

import br.com.sankhya.addon.requisicoes.exception.PersistenciaException;
import br.com.sankhya.addon.requisicoes.repository.RequisicaoRepository;
import br.com.sankhya.addon.requisicoes.repository.StatusRequisicaoRepository;
import br.com.sankhya.addon.requisicoes.util.StatusRequisicaoEnum;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.PersistenceEventAdapter;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.studio.annotations.Listener;

import java.math.BigDecimal;

@Listener(instanceNames = "DRQ_Requisicao")
public class RequisicaoListener extends PersistenceEventAdapter {

    private final StatusRequisicaoRepository statusRequisicaoRepository;
    private final RequisicaoRepository requisicaoRepository;

    public RequisicaoListener() {
        this.statusRequisicaoRepository = new StatusRequisicaoRepository();
        this.requisicaoRepository = new RequisicaoRepository();
    }

    @Override
    public void beforeUpdate(PersistenceEvent event) throws PersistenciaException {
        try {
            if (event.getModifingFields().isModifing("PARCEIRO")) {
                DynamicVO reqVO = (DynamicVO) event.getVo();
                BigDecimal codparc = reqVO.asBigDecimal("PARCEIRO");
                String email = requisicaoRepository.buscarEmailParceiro(codparc.longValue());
                reqVO.setProperty("EMAIL_PARCEIRO", email);
            }
        } catch (Exception e) {
            throw new PersistenciaException(e);
        }
    }

    @Override
    public void afterInsert(PersistenceEvent event) throws PersistenciaException {
        try {
            DynamicVO reqVO = (DynamicVO) event.getVo();
            Long idRequisicao = reqVO.asLong("ID_REQ");

            statusRequisicaoRepository.inserir(idRequisicao, StatusRequisicaoEnum.PENDENTE.getValor());
        } catch (Exception e) {
            throw new PersistenciaException(e);
        }
    }
}
