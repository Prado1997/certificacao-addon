package br.com.sankhya.addon.requisicoes.component;

import br.com.sankhya.addon.requisicoes.dto.RequisicaoDTO;
import br.com.sankhya.addon.requisicoes.exception.PersistenciaException;
import br.com.sankhya.addon.requisicoes.repository.RequisicaoRepository;
import br.com.sankhya.addon.requisicoes.repository.StatusRequisicaoRepository;
import br.com.sankhya.addon.requisicoes.util.RequisicaoConverter;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;

import java.util.Collection;
import java.util.stream.Collectors;

public class RequisicaoComponent {
    private StatusRequisicaoRepository statusRequisicaoRepository;
    private RequisicaoRepository requisicaoRepository;

    public RequisicaoComponent() {
        this.statusRequisicaoRepository = new StatusRequisicaoRepository();
        this.requisicaoRepository = new RequisicaoRepository();
    }

    public void iniciarRequisicao(Registro registro) throws PersistenciaException {
        try {
            this.statusRequisicaoRepository.atualizarStatusParaRealizando(registro);
        } catch (Exception e) {
            throw new PersistenciaException(e.getMessage(), e.getCause());
        }
    }

    public void finalizarRequisicao(Registro registro) throws PersistenciaException {
        try {
            this.statusRequisicaoRepository.atualizarStatusParaFinalizada(registro);
        } catch (Exception e) {
            throw new PersistenciaException(e.getMessage(), e.getCause());
        }
    }

    public void registrarAvisoJob() throws Exception {
        this.requisicaoRepository.avisoRequisicaoJob();
    }

    public void gerenciarEmailParceiro(Long idParceiro, String email) throws PersistenciaException {
        try {
            this.requisicaoRepository.atualizarEmail(idParceiro, email);
        } catch (Exception e) {
            throw new PersistenciaException(e.getMessage(), e.getCause());
        }
    }

    public void criarRequisicao(Long idParceiro, Long idPedido, Long idCentroResultado) throws Exception {
        this.requisicaoRepository.inserir(idParceiro, idPedido, idCentroResultado);
    }

    public Collection<RequisicaoDTO> listarRequisicoesParceiro(Long idParceiro) throws Exception {
        Collection<DynamicVO> listarRequisicoesParceiro = this.requisicaoRepository.listarRequisicoesParceiro(idParceiro);
        return listarRequisicoesParceiro.stream()
            .map(RequisicaoConverter::toDTO)
            .collect(Collectors.toList());
    }

}
