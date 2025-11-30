package bean;
import java.sql.Date;

/*
    Representa a entidade de Doação e seus itens associados no sistema.
    Esta classe unifica e utiliza:
        - Dados gerais da doação (tabela Doacao)
        - Dados do item doado (tabela ItemDoado)
        - Atributos específicos dos subtipos: Alimento, Vestuário e Higiene.
 */

public class DoacaoBean {
    // Tabela Doacao
    private int idDoacao;
    private Date dataCadastro;
    private String descricaoGeral;
    private String statusDoacao;  // "Disponível", "Agendada", "Coletada", "Cancelada"
    private Date dataColeta; // Data agendada para coleta da doação

    // Tabela ItemDoado
    private int idItem;
    private String descricaoItem;
    private double quantidade;
    private String unidadeMedida;

    // Subtipos (Alimento, Vestuario, Higiene)
    private String tipoItem; // "A", "V" ou "H"
    private Date dataValidade; // Alimento
    private String faixaEtaria; // Vestuario
    private String genero; // Vestuario
    private int tamanho; // Vestuario
    private double volume; // Higiene

    // Vínculos
    private String nomeDoador; // Para exibição

    // getters e setters
    // Doacao
    public int getIdDoacao() { return idDoacao; }
    public void setIdDoacao(int id) { this.idDoacao = id; }
    public Date getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(Date d) { this.dataCadastro = d; }
    public String getDescricaoGeral() { return descricaoGeral; }
    public void setDescricaoGeral(String d) { this.descricaoGeral = d; }
    public String getStatusDoacao() { return statusDoacao; }
    public void setStatusDoacao(String s) { this.statusDoacao = s; }
    public Date getDataColeta() { return dataColeta; }
    public void setDataColeta(Date d) { this.dataColeta = d; }

    //ItemDoado
    public int getIdItem() { return idItem; }
    public void setIdItem(int id) { this.idItem = id; }
    public String getDescricaoItem() { return descricaoItem; }
    public void setDescricaoItem(String d) { this.descricaoItem = d; }
    public double getQuantidade() { return quantidade; }
    public void setQuantidade(double q) { this.quantidade = q; }
    public String getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(String u) { this.unidadeMedida = u; }
    
    public String getTipoItem() { return tipoItem; }
    public void setTipoItem(String t) { this.tipoItem = t; }

    public Date getDataValidade() { return dataValidade; }
    public void setDataValidade(Date d) { this.dataValidade = d; }
    
    public String getFaixaEtaria() { return faixaEtaria; }
    public void setFaixaEtaria(String f) { this.faixaEtaria = f; }
    public String getGenero() { return genero; }
    public void setGenero(String g) { this.genero = g; }
    public int getTamanho() { return tamanho; }
    public void setTamanho(int t) { this.tamanho = t; }

    public double getVolume() { return volume; }
    public void setVolume(double volume) { this.volume = volume; }
    
    public String getNomeDoador() { return nomeDoador; }
    public void setNomeDoador(String n) { this.nomeDoador = n;}
    
    @Override
    public String toString() {
        String detalhe = "";
        if ("A".equals(tipoItem)) detalhe = " (Alimento)";
        else if ("V".equals(tipoItem)) detalhe = " (Vestuario)";
        else if ("H".equals(tipoItem)) detalhe = " (Higiene)";
        
        return "ID: " + idDoacao + " | " + descricaoGeral + " | Status: " + statusDoacao + " | Item: " + descricaoItem + detalhe;
    }
}