package bean;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe UsuarioBean.
 * Verificam se os getters e setters funcionam corretamente
 * e se o método toString retorna o formato esperado.
 */
public class UsuarioBeanTest {

    @Test
    public void testSettersAndGetters() {
        // Testa se os setters armazenam os valores corretamente
        // e se os getters os retornam sem alteração.
        UsuarioBean u = new UsuarioBean();

        u.setIdUsuario(10);
        u.setNome("Maria Silva");
        u.setEmail("maria@example.com");
        u.setTelefone(48987654321L);

        u.setCnpj(12345678000199L);
        u.setTipoEstabelecimento("Restaurante");

        u.setCpfCnpjReceptor(98765432100L);
        u.setNomeResponsavel("João Alberto");

        u.setTipoUsuario("DOADOR");

        // Verifica cada campo individualmente
        assertEquals(10, u.getIdUsuario());
        assertEquals("Maria Silva", u.getNome());
        assertEquals("maria@example.com", u.getEmail());
        assertEquals(48987654321L, u.getTelefone());

        assertEquals(12345678000199L, u.getCnpj());
        assertEquals("Restaurante", u.getTipoEstabelecimento());

        assertEquals(98765432100L, u.getCpfCnpjReceptor());
        assertEquals("João Alberto", u.getNomeResponsavel());

        assertEquals("DOADOR", u.getTipoUsuario());
    }

    @Test
    public void testToString() {
        // Testa se o método toString retorna exatamente o formato definido.
        UsuarioBean u = new UsuarioBean();
        u.setIdUsuario(5);
        u.setNome("Carlos");
        u.setTipoUsuario("RECEPTOR");

        // Formato esperado: "ID - Nome (TipoUsuario)"
        assertEquals("5 - Carlos (RECEPTOR)", u.toString());
    }
}
