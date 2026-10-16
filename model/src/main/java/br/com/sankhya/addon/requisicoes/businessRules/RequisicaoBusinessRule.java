package br.com.sankhya.addon.requisicoes.businessRules;

import br.com.sankhya.addon.requisicoes.component.RequisicaoComponent;
import br.com.sankhya.addon.requisicoes.repository.RequisicaoRepository;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.comercial.ContextoRegra;
import br.com.sankhya.modelcore.comercial.Regra;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.studio.annotations.hooks.BusinessRule;

import java.math.BigDecimal;
import java.util.logging.Logger;

@BusinessRule(description = "Restringir Parceiros bloqueados via BusinessRule")
public class RequisicaoBusinessRule implements Regra {

    private static final Logger logger = Logger.getLogger(RequisicaoBusinessRule.class.getName());
    private static final BigDecimal TOP_REQUISICAO = BigDecimal.valueOf(1801);

    private final RequisicaoRepository requisicaoRepository;

    public RequisicaoBusinessRule() {
        this.requisicaoRepository = new RequisicaoRepository();
    }

    @Override
    public void beforeInsert(ContextoRegra ctx) throws Exception {
        String entidade = ctx.getPrePersistEntityState().getDao().getEntityName();
        if (!DynamicEntityNames.CABECALHO_NOTA.equals(entidade)) {
            return;
        }

        DynamicVO notaVO = ctx.getPrePersistEntityState().getNewVO();
        BigDecimal codTipOper = notaVO.asBigDecimal("CODTIPOPER");
        if (codTipOper == null || codTipOper.compareTo(TOP_REQUISICAO) != 0) {
            return;
        }

        BigDecimal codparc = notaVO.asBigDecimal("CODPARC");
        String bloqueado = this.requisicaoRepository.buscarParceiroBloqueado(codparc.longValue());

        if ("S".equals(bloqueado)) {
            throw new RuntimeException("Não é permitida a criação de Requisição para um Parceiro bloqueado.");
        }
    }

    @Override
    public void beforeUpdate(ContextoRegra ctx) throws Exception {
        String entidade = ctx.getPrePersistEntityState().getDao().getEntityName();
        if (!DynamicEntityNames.CABECALHO_NOTA.equals(entidade)) {
            return;
        }

        DynamicVO notaVO = ctx.getPrePersistEntityState().getNewVO();
        BigDecimal codTipOper = notaVO.asBigDecimal("CODTIPOPER");
        if (codTipOper == null || codTipOper.compareTo(TOP_REQUISICAO) != 0) {
            return;
        }

        BigDecimal codparc = notaVO.asBigDecimal("CODPARC");
        String bloqueado = this.requisicaoRepository.buscarParceiroBloqueado(codparc.longValue());

        if ("S".equals(bloqueado)) {
            throw new RuntimeException("Não é permitida a criação de Requisição para um Parceiro bloqueado.");
        }
    }

    @Override
    public void beforeDelete(ContextoRegra ctx) throws Exception {

    }

    @Override
    public void afterInsert(ContextoRegra ctx) throws Exception {

    }

    @Override
    public void afterUpdate(ContextoRegra ctx) throws Exception {

    }

    @Override
    public void afterDelete(ContextoRegra ctx) throws Exception {

    }
}
