package br.com.sankhya.addon.requisicoes.callbacks;

import br.com.sankhya.addon.requisicoes.component.RequisicaoComponent;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.modelcore.custommodule.ICustomCallBack;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.studio.annotations.hooks.Callback;
import br.com.sankhya.studio.annotations.hooks.CallbackEvent;
import br.com.sankhya.studio.annotations.hooks.CallbackWhen;
import com.sankhya.util.BigDecimalUtil;
import helper.JapeHelper;

import java.math.BigDecimal;
import java.util.Map;
import java.util.logging.Logger;

@Callback(when = CallbackWhen.AFTER,
    event = CallbackEvent.PROCESS_CONFIRMATION,
    description = "Criação de Requisição no Callback")

public class RequisicaoCallback implements ICustomCallBack {

    private final RequisicaoComponent requisicaoComponent;
    private final Logger logger = Logger.getLogger(RequisicaoCallback.class.getName());
    private static final BigDecimal TOP_REQUISICAO = BigDecimal.valueOf(1801);

    public RequisicaoCallback() {
        this.requisicaoComponent = new RequisicaoComponent();
    }

    @Override
    public Object call(String id, Map<String, Object> data) {
        logger.info("Iniciando RequisicaoCallback");
        BigDecimal nuNota = (BigDecimal) data.get("nunota");
        if (nuNota == null || data.get("error") != null) {
            return null;
        }

        try {
            JapeHelper.executeWithTX(() -> {
                DynamicVO cabVO = JapeFactory.dao(DynamicEntityNames.CABECALHO_NOTA).findByPK(nuNota);

                if (cabVO.asBigDecimal("CODTIPOPER").intValue() != TOP_REQUISICAO.intValue()) {
                    return;
                }

                BigDecimal codParc = BigDecimalUtil.getValueOrZero(cabVO.asBigDecimal("CODPARC"));
                BigDecimal codCenCus = BigDecimalUtil.getValueOrZero(cabVO.asBigDecimal("CODCENCUS"));
                this.requisicaoComponent.criarRequisicao(codParc.longValue(), nuNota.longValue(), codCenCus.longValue());
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
