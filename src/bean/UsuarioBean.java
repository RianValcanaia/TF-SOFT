package bean;

/*
    Representa um usuário do sistema, unificando os perfis de Doador e Receptor.
    Contém:
        - Dados comuns da tabela Usuario (nome, email, telefone).
        - Dados específicos da tabela Doador (CNPJ, Tipo de Estabelecimento).
        - Dados específicos da tabela Receptor (CPF/CNPJ, Nome do Responsável).
    O campo tipoUsuario define qual papel o objeto desempenha no sistema.
 */

public class UsuarioBean {
    // Tabela Usuario
    private int idUsuario;
    private String nome;
    private String email;
    private long telefone;
    
    // Tabela Doador
    private long cnpj;
    private String tipoEstabelecimento;

    // Tabela Receptor
    private long cpfCnpjReceptor;
    private String nomeResponsavel;

    // Controle interno
    private String tipoUsuario; // "DOADOR" ou "RECEPTOR"

    // Getters and Setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public long getTelefone() { return telefone; }
    public void setTelefone(long telefone) { this.telefone = telefone; }
    
    public long getCnpj() { return cnpj; }
    public void setCnpj(long cnpj) { this.cnpj = cnpj; }
    public String getTipoEstabelecimento() { return tipoEstabelecimento; }
    public void setTipoEstabelecimento(String tipo) { this.tipoEstabelecimento = tipo; }
    
    public long getCpfCnpjReceptor() { return cpfCnpjReceptor; }
    public void setCpfCnpjReceptor(long cpf) { this.cpfCnpjReceptor = cpf; }
    public String getNomeResponsavel() { return nomeResponsavel; }
    public void setNomeResponsavel(String nome) { this.nomeResponsavel = nome; }
    
    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    @Override
    public String toString() {
        return idUsuario + " - " + nome + " (" + tipoUsuario + ")";
    }
}