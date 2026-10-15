package br.com.sankhya.addon.requisicoes.component;

import br.com.sankhya.addon.requisicoes.exception.PersistenciaException;
import br.com.sankhya.addon.requisicoes.repository.RequisicaoRepository;
import br.com.sankhya.addon.requisicoes.repository.StatusRequisicaoRepository;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
//
//    public void criarViagem(Long idParceiro, Long idPedido) throws Exception {
//        LocalDateTime dataSaida = LocalDateTime.now().plusDays(2);
//        LocalDateTime dataChegadaPrevista = LocalDateTime.now().plusDays(15);
//
//        DynamicVO viagemVO = this.viagemRepository.inserir(idParceiro, idPedido, dataSaida, dataChegadaPrevista);
//        if (viagemVO != null) {
//            BigDecimal idViagem = viagemVO.asBigDecimal("ID_VIAGEM");
//            String status = StatusViagemEnum.PENDENTE.getValor();
//            this.statusViagemRepository.inserir(idViagem.longValue(), status);
//        }
//    }
//
//    public Collection<ViagemDTO> listarViagensParceiro(Long idParceiro) throws Exception {
//        Collection<DynamicVO> listarViagensParceiro = this.viagemRepository.listarViagensParceiro(idParceiro);
//        return listarViagensParceiro.stream()
//            .map(ViagemConverter::toDTO)
//            .collect(Collectors.toList());
//    }

}
