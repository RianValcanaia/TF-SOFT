package bean;
import java.sql.Date;

/*
    Representa uma solicitação de doação feita por um Receptor.
    Armazena o estado do pedido (Pendente, Aceita, Recusada), a data,
    e mantêm os vínculos (IDs) entre quem solicitou (Receptor) e o que foi
    solicitado (Doacao). Também contém campos auxiliares para exibição de
    nomes e descrições em relatórios (JOINs).
 */

public class SolicitacaoBean {
    private int idSolicitacao;
    private Date dataSolicitacao;
    private String status;  // "Pendente", "Aceita", "Recusada"
    private int idReceptor;
    private int idDoacao;
    
    // Campos para exibição (JOINs)
    private int idUsuarioReceptor;
    private String nomeReceptor; 
    private String descricaoGeralDoacao;

    public int getIdSolicitacao() { return idSolicitacao; }
    public void setIdSolicitacao(int id) { this.idSolicitacao = id; }
    
    public Date getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(Date d) { this.dataSolicitacao = d; }
    
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
    
    public int getIdReceptor() { return idReceptor; }
    public void setIdReceptor(int id) { this.idReceptor = id; }
    
    public int getIdDoacao() { return idDoacao; }
    public void setIdDoacao(int id) { this.idDoacao = id; }
    
    public int getIdUsuarioReceptor() { return idUsuarioReceptor; }
    public void setIdUsuarioReceptor(int id) { this.idUsuarioReceptor = id; }

    public String getNomeReceptor() { return nomeReceptor; }
    public void setNomeReceptor(String n) { this.nomeReceptor = n; }

    public String getDescricaoGeralDoacao() { return descricaoGeralDoacao; }
    public void setDescricaoGeralDoacao(String dg) { this.descricaoGeralDoacao = dg; }

    @Override
    public String toString() {
        // ToString mais completo para o relatório
        return "ID: " + idSolicitacao + 
               " | Data: " + dataSolicitacao +
               " | Status: " + status + 
               " | Receptor: " + (nomeReceptor != null ? nomeReceptor : idReceptor) +
               " | Doacao: " + (descricaoGeralDoacao != null ? descricaoGeralDoacao : idDoacao);
    }
}