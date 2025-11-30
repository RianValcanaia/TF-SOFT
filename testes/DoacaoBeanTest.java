package bean;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.sql.Date;

public class DoacaoBeanTest {

    @Test
    public void testSettersAndGetters() {
        DoacaoBean d = new DoacaoBean();

        d.setIdDoacao(10);
        d.setDescricaoGeral("Doação de roupas");
        d.setStatusDoacao("Disponível");
        d.setDescricaoItem("Camiseta");
        d.setTipoItem("V");
        d.setQuantidade(5.0);
        d.setUnidadeMedida("unidade");

        Date hoje = Date.valueOf("2024-01-01");
        d.setDataCadastro(hoje);

        assertEquals(10, d.getIdDoacao());
        assertEquals("Doação de roupas", d.getDescricaoGeral());
        assertEquals("Disponível", d.getStatusDoacao());
        assertEquals("Camiseta", d.getDescricaoItem());
        assertEquals("V", d.getTipoItem());
        assertEquals(5.0, d.getQuantidade());
        assertEquals("unidade", d.getUnidadeMedida());
        assertEquals(hoje, d.getDataCadastro());
    }

    @Test
    public void testToStringVestuario() {
        DoacaoBean d = new DoacaoBean();
        d.setIdDoacao(1);
        d.setDescricaoGeral("Doação teste");
        d.setStatusDoacao("Disponível");
        d.setDescricaoItem("Blusa");
        d.setTipoItem("V");

        String txt = d.toString();

        assertTrue(txt.contains("Blusa"));
        assertTrue(txt.contains("(Vestuario)"));
    }

    @Test
    public void testToStringAlimento() {
        DoacaoBean d = new DoacaoBean();
        d.setIdDoacao(2);
        d.setDescricaoGeral("Doação de arroz");
        d.setStatusDoacao("Agendada");
        d.setDescricaoItem("Arroz");
        d.setTipoItem("A");

        String txt = d.toString();

        assertTrue(txt.contains("(Alimento)"));
    }

    @Test
    public void testToStringHigiene() {
        DoacaoBean d = new DoacaoBean();
        d.setIdDoacao(3);
        d.setDescricaoGeral("Doação de sabonete");
        d.setStatusDoacao("Coletada");
        d.setDescricaoItem("Sabonete");
        d.setTipoItem("H");

        String txt = d.toString();

        assertTrue(txt.contains("(Higiene)"));
    }
}
