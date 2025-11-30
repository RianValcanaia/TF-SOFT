package bean;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.Date;
import org.junit.jupiter.api.Test;

/**
 * Testes unitários para a classe SolicitacaoBean.
 * Cada teste contém comentários explicando claramente o que está sendo validado.
 */
public class SolicitacaoBeanTest {

    @Test
    public void testGettersAndSetters() {
        // Testa se os setters atribuem corretamente e se os getters retornam corretamente

        SolicitacaoBean s = new SolicitacaoBean();

        Date data = Date.valueOf("2025-01-15");

        s.setIdSolicitacao(10);
        s.setDataSolicitacao(data);
        s.setStatus("Pendente");
        s.setIdReceptor(3);
        s.setIdDoacao(50);
        s.setIdUsuarioReceptor(2);
        s.setNomeReceptor("Carlos");
        s.setDescricaoGeralDoacao("Doação de roupas");

        // Verifica se cada campo foi armazenado corretamente
        assertEquals(10, s.getIdSolicitacao());
        assertEquals(data, s.getDataSolicitacao());
        assertEquals("Pendente", s.getStatus());
        assertEquals(3, s.getIdReceptor());
        assertEquals(50, s.getIdDoacao());
        assertEquals(2, s.getIdUsuarioReceptor());
        assertEquals("Carlos", s.getNomeReceptor());
        assertEquals("Doação de roupas", s.getDescricaoGeralDoacao());
    }

    @Test
    public void testToString_UsandoCamposDeExibicao() {
        // Testa se o toString usa nomeReceptor e descricaoGeralDoacao
        // quando esses valores foram preenchidos (caso de relatório JOIN)

        SolicitacaoBean s = new SolicitacaoBean();

        s.setIdSolicitacao(1);
        s.setDataSolicitacao(Date.valueOf("2025-01-01"));
        s.setStatus("Aceita");
        s.setIdReceptor(10);
        s.setIdDoacao(20);

        s.setNomeReceptor("Maria");
        s.setDescricaoGeralDoacao("Cesta básica");

        String texto = s.toString();

        // O toString deve conter os campos *textuais* vindos dos JOINs
        assertTrue(texto.contains("Maria"));            // Testa nome do receptor no texto
        assertTrue(texto.contains("Cesta básica"));     // Testa descrição da doação
        assertTrue(texto.contains("Aceita"));           // Testa status
        assertTrue(texto.contains("2025-01-01"));       // Testa data formatada
    }

    @Test
    public void testToString_UsandoSomenteIds() {
        // Testa quando NÃO existem campos de exibição (nomeReceptor e descricaoGeralDoacao)
        // Neste caso, o toString deve usar os IDs como fallback

        SolicitacaoBean s = new SolicitacaoBean();

        s.setIdSolicitacao(5);
        s.setDataSolicitacao(Date.valueOf("2025-02-10"));
        s.setStatus("Pendente");
        s.setIdReceptor(77);
        s.setIdDoacao(99);

        // Não define nomeReceptor nem descricaoGeralDoacao → fallback deve usar IDs

        String texto = s.toString();

        assertTrue(texto.contains("77"));  // Verifica fallback para receptor
        assertTrue(texto.contains("99"));  // Verifica fallback para doação
    }
}
