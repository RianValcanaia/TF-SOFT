package bean;

/*
    Representa as informações de localização (Endereço).
    Utilizada para compor os dados de cadastro de um Usuário 
    e também para definir o local de coleta de uma Doação.
*/

public class EnderecoBean {
    private int idEndereco;
    private String pais;
    private String estado;
    private int cep;
    private String cidade;
    private String bairro;
    private String rua;
    private int numero;
    private float latitude;
    private float longitude;

    // Construtor 
    public EnderecoBean() {}

    public EnderecoBean(String pais, 
                        String estado, 
                        int cep,
                        String cidade, 
                        String bairro, 
                        String rua,
                        int numero, 
                        float latitude,
                        float longitude) {
        this.pais = pais;
        this.estado = estado;
        this.cep = cep;
        this.cidade = cidade;
        this.bairro = bairro;
        this.rua = rua;
        this.numero = numero;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters e Setters
    public int getIdEndereco() { return idEndereco; }
    public void setIdEndereco(int idEndereco) { this.idEndereco = idEndereco; }
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public int getCep() { return cep; }
    public void setCep(int cep) { this.cep = cep; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }
    public float getLatitude() { return latitude; }
    public void setLatitude(float latitude) { this.latitude = latitude; }
    public float getLongitude() { return longitude; }
    public void setLongitude(float longitude) { this.longitude = longitude; }
    
    
    @Override
    public String toString() {
        return rua + ", " + numero + " - " + cidade;
    }
}