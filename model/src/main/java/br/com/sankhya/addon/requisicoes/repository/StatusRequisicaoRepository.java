package br.com.sankhya.addon.requisicoes.repository;

import br.com.sankhya.addon.requisicoes.exception.PersistenciaException;
import br.com.sankhya.addon.requisicoes.util.StatusRequisicaoEnum;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

import java.math.BigDecimal;

public class StatusRequisicaoRepository {
    private static final String DRQ_REQUISICAOSTATUS = "DRQ_StatusRequisicao";

    public void atualizarStatusParaRealizando(Registro registro) throws Exception {
        JapeWrapper jw = getWrapper(DRQ_REQUISICAOSTATUS);

        if (!StatusRequisicaoEnum.REALIZANDO.getValor().equals(registro.getCampo("STATUS"))) {
            jw.prepareToUpdateByPK(registro.getCampo("ID_REQ"))
                .set("STATUS", StatusRequisicaoEnum.REALIZANDO.getValor())
                .update();
        }
    }

    public void atualizarStatusParaFinalizada(Registro registro) throws Exception {
        JapeWrapper jw = getWrapper(DRQ_REQUISICAOSTATUS);

        if (!StatusRequisicaoEnum.REALIZANDO.getValor().equals(registro.getCampo("STATUS"))) {
            jw.prepareToUpdateByPK(registro.getCampo("ID_REQ"))
                .set("STATUS", StatusRequisicaoEnum.FINALIZADA.getValor())
                .update();
        }
    }

    public void inserir(Long idRequisicao, String status) throws PersistenciaException {
        try {
            JapeWrapper jw = getWrapper(DRQ_REQUISICAOSTATUS);
            jw.create()
                .set("ID_REQ", BigDecimal.valueOf(idRequisicao))
                .set("STATUS", status)
                .save();
        } catch (Exception e) {
            throw new PersistenciaException(e);
        }
    }

    private JapeWrapper getWrapper(String nomeEntidade) {
        return JapeFactory.dao(nomeEntidade);
    }
}
