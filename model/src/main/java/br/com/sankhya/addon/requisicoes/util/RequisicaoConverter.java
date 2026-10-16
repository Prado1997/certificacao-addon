package br.com.sankhya.addon.requisicoes.util;

import br.com.sankhya.addon.requisicoes.dto.RequisicaoDTO;
import br.com.sankhya.jape.vo.DynamicVO;
import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.StringUtils;

public class RequisicaoConverter {
    public static RequisicaoDTO toDTO(DynamicVO requisicaoVO) {
        if (requisicaoVO == null) {
            return null;
        }
        RequisicaoDTO requisicaoDTO = new RequisicaoDTO();
        requisicaoDTO.setIdParceiro(BigDecimalUtil.getValueOrZero(requisicaoVO.asBigDecimal("PARCEIRO")).longValue());
        requisicaoDTO.setIdPedido(BigDecimalUtil.getValueOrZero(requisicaoVO.asBigDecimal("COD_REQUISICAO_PORTAL")).longValue());
        requisicaoDTO.setIdCentroResultado(BigDecimalUtil.getValueOrZero(requisicaoVO.asBigDecimal("CENTRORESULTADO")).longValue());
        requisicaoDTO.setEmailParceiro(StringUtils.getValueOrDefault(requisicaoVO.asString("EMAIL_PARCEIRO"), "-"));
        return requisicaoDTO;
    }
}
