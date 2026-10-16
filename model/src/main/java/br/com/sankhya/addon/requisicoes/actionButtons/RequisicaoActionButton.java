package br.com.sankhya.addon.requisicoes.actionButtons;


import br.com.sankhya.addon.requisicoes.component.RequisicaoComponent;
import br.com.sankhya.addon.requisicoes.exception.PersistenciaException;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.studio.annotations.hooks.*;
import com.sankhya.util.TimeUtils;

@ActionButton(
    description = "Atualizar Requisição",
    instanceName = "DRQ_Requisicao",
    refreshType = RefreshTypeEnum.SELECTED_ITEMS,
    transactionType = TransactionType.AUTOMATIC,
    accessControlled = false,
    form = @Form(
        fields = {
            @Field(
                label = "Ação da Requisição",
                name = "acao",
                type = FieldType.LIST,
                options = {
                    @Option(value = "R", label = "Realizar Atendimento"),
                    @Option(value = "F", label = "Finalizar Atendimento")
                }
            )
        }
    )
)
public class RequisicaoActionButton implements AcaoRotinaJava {

    private RequisicaoComponent requisicaoComponent;

    public RequisicaoActionButton() {
        this.requisicaoComponent = new RequisicaoComponent();
    }

    @Override
    public void doAction(ContextoAcao ctx) throws PersistenciaException {
        Object acao = ctx.getParam("acao");
        boolean simNao = ctx.confirmarSimNao("Ação da Requisição",
            "Deseja realizar a ação informada no parâmetro para a(s) Requisição(ões) selecionada(s)?", 1);
        if (simNao) {
            String vAprovar = String.valueOf(acao);
            int qtdLinhas = (ctx.getLinhas()).length;
            for (int i = 0; i < qtdLinhas; i++) {
                try {
                    if (ctx.getLinhas() == null || ctx.getLinhas().length == 0) {
                        ctx.setMensagemRetorno("Selecione um registro para realizar a ação.");
                        return;
                    }

                    Registro linha = ctx.getLinhas()[i];

                    if (vAprovar.equals("R")) {
                        this.requisicaoComponent.iniciarRequisicao(linha);
                    } else if (vAprovar.equals("F")) {
                        this.requisicaoComponent.finalizarRequisicao(linha);
                        linha.setCampo("REALIZADA", "S");
                        linha.setCampo("DT_ENCERRAMENTO", TimeUtils.getNow());
                    }

                    ctx.setMensagemRetorno("<b>Requisição atualizada com sucesso!</b><br>");
                } catch (Exception e) {
                    throw new PersistenciaException(e);
                }
            }
        } else {
            ctx.setMensagemRetorno("Ação cancelada pelo usuário!");
        }
    }
}
