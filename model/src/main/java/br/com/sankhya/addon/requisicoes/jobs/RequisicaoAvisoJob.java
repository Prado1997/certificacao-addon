package br.com.sankhya.addon.requisicoes.jobs;

import br.com.sankhya.addon.requisicoes.component.RequisicaoComponent;
import br.com.sankhya.studio.annotations.Job;
import br.com.sankhya.studio.annotations.enums.EJBTransactionType;
import br.com.sankhya.studio.stereotypes.IJob;

import java.util.logging.Logger;

@Job(serviceName = "RequisicaoAvisoJobSP",
    transactionType = EJBTransactionType.Supports,
    frequency = "&3600000")

public class RequisicaoAvisoJob extends IJob {

    private RequisicaoComponent requisicaoComponent;

    public RequisicaoAvisoJob() {
        this.requisicaoComponent = new RequisicaoComponent();
    }

    private static final Logger logger = Logger.getLogger(RequisicaoAvisoJob.class.getName());

    @Override
    public void onSchedule() {
        try {
            this.requisicaoComponent.registrarAvisoJob();
        } catch (Exception e) {
            logger.warning("Erro ao executar Job RequisicaoAvisoJob: " + e.getMessage());
        }
    }
}
