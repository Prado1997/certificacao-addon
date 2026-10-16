package br.com.sankhya.addon.requisicoes.repository;


import br.com.sankhya.addon.requisicoes.exception.PersistenciaException;
import br.com.sankhya.addon.requisicoes.util.StatusRequisicaoEnum;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import com.sankhya.util.JdbcUtils;
import com.sankhya.util.StringUtils;
import com.sankhya.util.TimeUtils;
import helper.JapeHelper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

public class RequisicaoRepository {
    private static final String DRQ_REQUISICAO = "DRQ_Requisicao";

    public void avisoRequisicaoJob() throws Exception {
        try {
            JapeHelper.executeWithTX(() -> {
                Map<StatusRequisicaoEnum, Integer> totais = contarPorStatus();

                EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
                DynamicVO avisoSistema = (DynamicVO) dwfEntityFacade.getDefaultValueObjectInstance("AvisoSistema");
                avisoSistema.setProperty("IDENTIFICADOR", "REQUISICOES INTERNAS");
                avisoSistema.setProperty("IMPORTANCIA", BigDecimal.valueOf(2));
                avisoSistema.setProperty("TITULO", "Quantidade de Requisições Internas");
                avisoSistema.setProperty("DESCRICAO", String.format("Pendentes: %d | Realizando: %d | Finalizadas: %d",
                    totais.get(StatusRequisicaoEnum.PENDENTE),
                    totais.get(StatusRequisicaoEnum.REALIZANDO),
                    totais.get(StatusRequisicaoEnum.FINALIZADA)));
                avisoSistema.setProperty("SOLUCAO", "Acesse a tela de Requisições Internas para uma análise detalhada.");
                avisoSistema.setProperty("TIPO", "P");
                avisoSistema.setProperty("CODUSUREMETENTE", BigDecimal.ZERO);
                avisoSistema.setProperty("CODUSU", BigDecimal.ZERO);
                avisoSistema.setProperty("DHCRIACAO", TimeUtils.getNow());
                dwfEntityFacade.createEntity("AvisoSistema", (EntityVO) avisoSistema);
            });
        } catch (Exception e) {
            throw new PersistenciaException(e);
        }
    }

    private Map<StatusRequisicaoEnum, Integer> contarPorStatus() throws PersistenciaException {
        Map<StatusRequisicaoEnum, Integer> totais = new EnumMap<>(StatusRequisicaoEnum.class);
        for (StatusRequisicaoEnum status : StatusRequisicaoEnum.values()) {
            totais.put(status, 0);
        }

        JdbcWrapper jdbc = null;
        NativeSql sql = null;
        ResultSet rs = null;
        try {
            jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
            jdbc.openSession();
            sql = new NativeSql(jdbc);
            // LEFT JOIN + COALESCE: requisição sem linha de status conta como Pendente
            sql.appendSql("SELECT COALESCE(ST.STATUS, 'P') AS STATUS, COUNT(1) AS QTD "
                + "FROM TRQ_REQUISICAO RQ "
                + "LEFT JOIN TRQ_STATUS_REQUISICAO ST ON ST.ID_REQ = RQ.ID_REQ "
                + "GROUP BY COALESCE(ST.STATUS, 'P')");
            rs = sql.executeQuery();
            while (rs.next()) {
                for (StatusRequisicaoEnum status : StatusRequisicaoEnum.values()) {
                    if (status.getValor().equals(rs.getString("STATUS"))) {
                        totais.put(status, rs.getInt("QTD"));
                    }
                }
            }
            return totais;
        } catch (Exception e) {
            throw new PersistenciaException(e);
        } finally {
            JdbcUtils.closeResultSet(rs);
            NativeSql.releaseResources(sql);
            JdbcWrapper.closeSession(jdbc);
        }
    }

    //

    //    p
    public String buscarEmailParceiro(Long idParceiro) throws Exception {
        try {
            JapeWrapper jw = getWrapper(DynamicEntityNames.PARCEIRO);
            DynamicVO parcVO = jw.findByPK(BigDecimal.valueOf(idParceiro));
            return parcVO.asString("EMAIL");
        } catch (Exception e) {
            throw new PersistenciaException(e);
        }
    }

    public void atualizarEmail(Long idParceiro, String email) throws Exception {
        JapeWrapper jw = getWrapper(DRQ_REQUISICAO);
        for (DynamicVO dynamicVO : getRequisicoesParceiro(idParceiro, jw)) {
            jw.prepareToUpdate(dynamicVO)
                .set("EMAIL_PARCEIRO", email)
                .update();
        }
    }

    public Collection<DynamicVO> getRequisicoesParceiro(Long idParceiro, JapeWrapper jw) throws Exception {
        return jw.find("PARCEIRO = ?", BigDecimal.valueOf(idParceiro));
    }


    public DynamicVO inserir(Long idParceiro, Long idPedido, Long idCentroResultado) throws Exception {
        DynamicVO requisicaoVO = null;
        try {
            JapeWrapper jw = getWrapper(DRQ_REQUISICAO);
            requisicaoVO = jw.create()
                .set("PARCEIRO", BigDecimal.valueOf(idParceiro))
                .set("COD_REQUISICAO_PORTAL", BigDecimal.valueOf(idPedido))
                .set("CENTRORESULTADO", BigDecimal.valueOf(idCentroResultado))
                .save();
        } catch (Exception e) {
            throw new PersistenciaException(e);
        }

        return requisicaoVO;
    }

    public String buscarParceiroBloqueado(Long idParceiro) throws Exception {
        try {
            JapeWrapper jw = getWrapper(DynamicEntityNames.PARCEIRO);
            DynamicVO parcVO = jw.findByPK(BigDecimal.valueOf(idParceiro));
            return StringUtils.getValueOrDefault(parcVO.asString("BLOQUEAR"), "N");
        } catch (Exception e) {
            throw new PersistenciaException(e);
        }
    }

    public Collection<DynamicVO> listarRequisicoesParceiro(Long idParceiro) throws Exception {
        try {
            JapeWrapper jw = getWrapper(DRQ_REQUISICAO);
            return getViagensParceiro(idParceiro, jw);
        } catch (Exception e) {
            throw new PersistenciaException(e);
        }
    }

    public Collection<DynamicVO> getViagensParceiro(Long idParceiro, JapeWrapper jw) throws Exception {
        return jw.find("PARCEIRO = ?", BigDecimal.valueOf(idParceiro));
    }

    private JapeWrapper getWrapper(String nomeEntidade) {
        return JapeFactory.dao(nomeEntidade);
    }
}
