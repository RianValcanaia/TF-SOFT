package bean;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class EnderecoBeanTest {

    @Test
    public void testConstrutorVazio() {
        // Testa se o construtor padrão cria um objeto não nulo
        EnderecoBean e = new EnderecoBean();
        assertNotNull(e, "O construtor vazio deve criar um objeto válido.");
    }

    @Test
    public void testConstrutorComParametros() {
        // Testa se o construtor com parâmetros preenche corretamente os atributos
        EnderecoBean e = new EnderecoBean(
                "Brasil",
                "SC",
                88000000,
                "Florianópolis",
                "Centro",
                "Rua das Flores",
                123,
                -27.595f,
                -48.548f
        );

        assertEquals("Brasil", e.getPais());
        assertEquals("SC", e.getEstado());
        assertEquals(88000000, e.getCep());
        assertEquals("Florianópolis", e.getCidade());
        assertEquals("Centro", e.getBairro());
        assertEquals("Rua das Flores", e.getRua());
        assertEquals(123, e.getNumero());
        assertEquals(-27.595f, e.getLatitude());
        assertEquals(-48.548f, e.getLongitude());
    }

    @Test
    public void testGettersSetters() {
        // Verifica se os setters alteram os valores e getters os retornam corretamente
        EnderecoBean e = new EnderecoBean();

        e.setIdEndereco(1);
        e.setPais("Brasil");
        e.setEstado("SP");
        e.setCep(12345678);
        e.setCidade("São Paulo");
        e.setBairro("Centro");
        e.setRua("Av. Paulista");
        e.setNumero(1000);
        e.setLatitude(-23.56f);

        assertEquals(1, e.getIdEndereco());
        assertEquals("Brasil", e.getPais());
        assertEquals("SP", e.getEstado());
        assertEquals(12345678, e.getCep());
        assertEquals("São Paulo", e.getCidade());
        assertEquals("Centro", e.getBairro());
        assertEquals("Av. Paulista", e.getRua());
        assertEquals(1000, e.getNumero());
        assertEquals(-23.56f, e.getLatitude());
    }

    @Test
    public void testToString() {
        // Testa se o método toString() monta corretamente a string
        EnderecoBean e = new EnderecoBean();
        e.setRua("Rua A");
        e.setNumero(50);
        e.setCidade("Curitiba");

        String esperado = "Rua A, 50 - Curitiba";
        assertEquals(esperado, e.toString(), "O método toString() deve formatar corretamente o endereço.");
    }
}
