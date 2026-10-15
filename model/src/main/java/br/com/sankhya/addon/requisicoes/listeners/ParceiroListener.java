package br.com.sankhya.addon.requisicoes.listeners;

import br.com.sankhya.addon.requisicoes.component.RequisicaoComponent;
import br.com.sankhya.addon.requisicoes.exception.PersistenciaException;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.PersistenceEventAdapter;
import br.com.sankhya.studio.annotations.Listener;

import java.math.BigDecimal;

@Listener(instanceNames = "Parceiro")
public class ParceiroListener extends PersistenceEventAdapter {

    private final RequisicaoComponent requisicaoComponent;

    public ParceiroListener() {
        this.requisicaoComponent = new RequisicaoComponent();
    }

    @Override
    public void afterUpdate(PersistenceEvent event) throws PersistenciaException {
        try {
            if (event.getModifingFields().isModifing("EMAIL")) {
                BigDecimal codparc = (BigDecimal) event.getEntityProperty("CODPARC");
                String email = (String) event.getEntityProperty("EMAIL");
                this.requisicaoComponent.gerenciarEmailParceiro(codparc.longValue(), email);
            }
        } catch (Exception e) {
            throw new PersistenciaException(e);
        }
    }
}
