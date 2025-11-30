package testes;

// Importacoes de dependencias
import bean.DoacaoBean;
import model.DoacaoModel;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

// Junit 5
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// mockito
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

// Asserts e verifications
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

public class DoacaoModelTest {  
    // cria mocks para substituir dependências externas do model, simulando o comportamento delas
    @Mock private Connection con;
    @Mock private PreparedStatement pstmt; 
    @Mock private Statement stmt; 
    @Mock private ResultSet rs;

    // serve como um reset para os mocks antes de cada teste, define o que deve ser retornado quando métodos específicos forem chamados
    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        
        // sempre que pedir um PreparedStatement, retorna o mock
        when(con.prepareStatement(anyString())).thenReturn(pstmt);
        // sempre que pedir um Statement simples, retorna o  mock
        when(con.createStatement()).thenReturn(stmt);
        // sempre que executar uma query, retorna o mock
        when(pstmt.executeQuery()).thenReturn(rs);
        when(stmt.executeQuery(anyString())).thenReturn(rs);
    }

    // ======== Testes do existeDoacao / existeItem ========
    @Test
    public void testeExisteDoacaoTrue() throws SQLException {
        // rs.next() retornando true, indicando que tem um resultado
        when(rs.next()).thenReturn(true);
        // rs.getInt(1) retorna 1, achou um registro
        when(rs.getInt(1)).thenReturn(1);

        boolean existe = DoacaoModel.existeDoacao(con, 10);
        // verifica se o resultado eh true
        assertTrue(existe);
    }

    @Test
    public void testeExisteDoacaoFalse() throws SQLException {
        // rs.next() retornando true, indicando que tem um resultado
        when(rs.next()).thenReturn(true);
        // rs.getInt(1) retorna 0, nao achou registro
        when(rs.getInt(1)).thenReturn(0);

        boolean existe = DoacaoModel.existeDoacao(con, 99);
        // verifica se o resultado eh false
        assertFalse(existe);
    }

    @Test
    public void testeExisteItem() throws SQLException {
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(1);

        boolean existe = DoacaoModel.existeItem(con, 5);
        assertTrue(existe);
    }

    // ========= Testes do cadastrarDoacao / adicionarItem =========

    @Test
    public void testeCadastrarDoacaoSucesso() throws SQLException {
        DoacaoBean doacao = new DoacaoBean();
        doacao.setDataCadastro(Date.valueOf(LocalDate.now()));
        doacao.setDescricaoGeral("Doação Teste");
        doacao.setDataColeta(Date.valueOf(LocalDate.now().plusDays(1)));

        // retorno do ID da doacao gerada 
        when(rs.next()).thenReturn(true); 
        when(rs.getInt(1)).thenReturn(100); // ID da doacao

        /* O codigo busca um endereco do usuario logo depois.
           Precisamos simular que ele achou um endereco (ID 50)
           para testar o fluxo completo incluindo rel_enderecocoleta.
        */
        when(rs.getInt(1)).thenReturn(100).thenReturn(50); 

        // acao
        DoacaoModel.cadastrarDoacao(con, doacao, 1);

        // verificacao
        verify(con).setAutoCommit(false);
        // esperamos prepares para: 
            // Insert Doacao, 
            // Insert Rel_Cadastra, 
            // Select Endereco (pode nao acontecer e ta tudo bem),  
            // Insert Rel_Coleta
        verify(con, atLeast(3)).prepareStatement(anyString());
        verify(con).commit();  // commit deve ter acontecido
        verify(con).setAutoCommit(true);  // deve restaurar autoCommit
    }

    @Test
    public void testeCadastrarDoacaoRollback() throws SQLException {
        DoacaoBean doacao = new DoacaoBean();
        doacao.setDataCadastro(Date.valueOf(LocalDate.now()));

        // simula erro logo no primeiro executeQuery
        when(pstmt.executeQuery()).thenThrow(new SQLException("Erro SQL"));

        try {
            DoacaoModel.cadastrarDoacao(con, doacao, 1);
            fail("Deveria lançar SQLException");
        } catch (SQLException e) {
            // se entrar aqui deu boa
        }

        verify(con).rollback();  // verifica se a transacao foi desfeita
        verify(con, never()).commit();  // commit nao deve ter acontecido
    }

    @Test
    public void testeAdicionarItemAlimento() throws SQLException {
        DoacaoBean item = new DoacaoBean();
        item.setDescricaoItem("Arroz");
        item.setQuantidade(5);
        item.setUnidadeMedida("kg");
        item.setTipoItem("A"); 
        item.setDataValidade(Date.valueOf("2025-12-31"));
        item.setIdDoacao(10);

        // mock do ID gerado para o item
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(200);

        DoacaoModel.adicionarItem(con, item);

        // Verificacoes
        verify(con).setAutoCommit(false);
        // Inserts: itemdoado, rel_itemdoacao, alimento
        verify(con, times(3)).prepareStatement(anyString());
        // Verifica se inseriu na tabela especifica de alimento
        verify(con).prepareStatement(contains("INSERT INTO alimento"));
        verify(con).commit();  // verifica se deu commit no final
    }

    @Test
    public void testeAdicionarItemVestuario() throws SQLException {
        DoacaoBean item = new DoacaoBean();
        item.setDescricaoItem("Camisa");
        item.setTipoItem("V"); 
        item.setFaixaEtaria("Adulto");
        item.setGenero("M");
        item.setTamanho(40);
        item.setIdDoacao(10);

        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(201);

        DoacaoModel.adicionarItem(con, item);

        verify(con).setAutoCommit(false);
        verify(con, times(3)).prepareStatement(anyString());
        verify(con).prepareStatement(contains("INSERT INTO vestuario"));
        verify(con).commit();
    }

    @Test
    public void testeAdicionarItemHigiene() throws SQLException {
        DoacaoBean item = new DoacaoBean();

        item.setDescricaoItem("Shampoo");
        item.setTipoItem("H"); 
        item.setVolume(500.0);
        item.setIdDoacao(10);

        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(201);

        DoacaoModel.adicionarItem(con, item);

        verify(con).setAutoCommit(false);
        verify(con, times(3)).prepareStatement(anyString());
        verify(con).prepareStatement(contains("INSERT INTO higiene"));
        verify(con).commit();
    }

    // ========== Testes de listagem (Doacao / Itens)==========
    @Test
    public void testeListaCompletaDoacoes() throws SQLException {
        // ResultSet com 1 linha
        when(rs.next()).thenReturn(true, false);
        
        when(rs.getInt(1)).thenReturn(10);
        when(rs.getString(2)).thenReturn("Descricao Geral");
        when(rs.getString(3)).thenReturn("Disponivel");
        when(rs.getDate(4)).thenReturn(Date.valueOf("2025-01-01"));
        when(rs.getString(5)).thenReturn("Joao Doador");

        ArrayList<DoacaoBean> lista = DoacaoModel.listaCompletaDoacoes(con);

        assertNotNull(lista);
        assertEquals(1, lista.size());
        assertEquals("Joao Doador", lista.get(0).getNomeDoador());
    }

    @Test
    public void testeListaItensDaDoacao() throws SQLException {
        when(rs.next()).thenReturn(true, false);
        
        when(rs.getInt(1)).thenReturn(55);
        when(rs.getString(2)).thenReturn("Feijao");
        when(rs.getDouble(3)).thenReturn(2.0);
        when(rs.getString(4)).thenReturn("kg");

        ArrayList<String> lista = DoacaoModel.listaItensDaDoacao(con, 10);

        assertNotNull(lista);
        assertFalse(lista.isEmpty());
        assertTrue(lista.get(0).contains("Feijao"));
    }

    // ========= Testes de UPDATE (updateDoacao / updateItem) ==========

    @Test
    public void testeUpdateDoacao() throws SQLException {
        DoacaoModel.updateDoacao(con, 10, "Coletada");

        verify(con).prepareStatement(contains("UPDATE doacao SET statusdoacao"));
        verify(pstmt).setString(1, "Coletada");
        verify(pstmt).setInt(2, 10);
        verify(pstmt).executeUpdate();
    }

    @Test
    public void testeUpdateDoacaoErro() throws SQLException {
        // erro ao montar o statment
        when(con.prepareStatement(contains("UPDATE doacao")))
                .thenThrow(new SQLException("Erro no UPDATE"));

        try {
            DoacaoModel.updateDoacao(con, 10, "Coletada");
            fail("Era esperado SQLException");
        } catch (SQLException e) {
            // entrar aqui eh o esperado
        }

        // nao pode chamar o executeUpdate
        verify(pstmt, never()).executeUpdate();
    }

    @Test
    public void testeUpdateItem() throws SQLException {
        DoacaoModel.updateItem(con, 50, 10.5);

        verify(con).prepareStatement(contains("UPDATE itemdoado SET quantidade"));
        verify(pstmt).setDouble(1, 10.5);
        verify(pstmt).setInt(2, 50);
        verify(pstmt).executeUpdate();
    }

    @Test
    public void testeUpdateItemErro() throws SQLException {
        // quando preparar, ok
        when(con.prepareStatement(contains("UPDATE itemdoado SET quantidade"))).thenReturn(pstmt);

        // quando executar, da erro
        when(pstmt.executeUpdate()).thenThrow(new SQLException("Falha no update"));

        try {
            DoacaoModel.updateItem(con, 50, 10.5);
            fail("Era esperado SQLException");
        } catch (SQLException e) {
            // tem que entrar auqi
        }
        
        verify(pstmt).setDouble(1, 10.5);
        verify(pstmt).setInt(2, 50);
        verify(pstmt).executeUpdate(); // executou e falhou
    }

    // ========== Testes de DELETE (deleteDoacao / deleteItem) ===========

    @Test
    public void testeDeleteDoacaoSucesso() throws SQLException {
        /* O deleteDoacao primeiro busca itens (rel_itemdoacao).
           simulano que nao ha itens (rs.next = false) para testar a
           logica principal de delecao da doacao e seus vinculos diretos.
        */
        when(rs.next()).thenReturn(false); 

        DoacaoModel.deleteDoacao(con, 10);

        verify(con).setAutoCommit(false);
        
        // Verifica se tentou deletar das tabelas de relacionamento e da doacao
        verify(con).prepareStatement(contains("DELETE FROM rel_cadastradoacao"));
        verify(con).prepareStatement(contains("DELETE FROM rel_enderecocoleta"));
        verify(con).prepareStatement(contains("DELETE FROM rel_solicitadoacao"));
        verify(con).prepareStatement(contains("DELETE FROM doacao"));
        
        verify(con).commit();
    }

    @Test
    public void testeDeleteItemSucesso() throws SQLException {
        DoacaoModel.deleteItem(con, 55);

        verify(con).setAutoCommit(false);
        
        /* metodo deleteItemIndividual tenta deletar de todas as tabelas específicas 
           (alimento, vestuario, higiene) sequencialmente para garantir.
        */
        verify(con).prepareStatement(contains("DELETE FROM alimento"));
        verify(con).prepareStatement(contains("DELETE FROM vestuario"));
        verify(con).prepareStatement(contains("DELETE FROM higiene"));
        verify(con).prepareStatement(contains("DELETE FROM rel_itemdoacao"));
        verify(con).prepareStatement(contains("DELETE FROM itemdoado"));

        verify(con).commit();
    }

    @Test
    public void testeDeleteDoacaoRollback() throws SQLException {
        // simula erro ao tentar deletar a doacao final
        when(con.prepareStatement(contains("DELETE FROM doacao"))).thenThrow(new SQLException("Erro FK"));
        
        // sem itens para chegar query de delecao
        when(rs.next()).thenReturn(false);

        try {
            DoacaoModel.deleteDoacao(con, 10);
            fail("Deveria lançar SQLException");
        } catch (SQLException e) {
            // se entrar aqui deu boa
        } 

        // verifica se deu rollback e nao comitou (essa palavra nao existe eu sei)
        verify(con).rollback();
        verify(con, never()).commit();
    }

    @Test
    public void testeDeleteItemRollback() throws SQLException {
        // simula erro ao tentar deletar o item final
        when(con.prepareStatement(contains("DELETE FROM itemdoado")))
                .thenThrow(new SQLException("Erro ao deletar item"));

        try {
            DoacaoModel.deleteItem(con, 55);
            fail("Deveria lançar SQLException");
        } catch (SQLException e) {
            // se entrar aqui deu boa
        }

        // Verifica se de rollback e comitou
        verify(con).rollback();
        verify(con, never()).commit();
    }

}