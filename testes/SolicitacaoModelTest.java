package testes;

// Importacoes de dependencias do model solicitacao
import bean.SolicitacaoBean;
import model.SolicitacaoModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// Junit 5
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// mockito
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

// Asserts e verifications
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

public class SolicitacaoModelTest {

    // cria mocks para substituir dependências externas do model, simulando o comportamento delas
    @Mock private Connection con;
    @Mock private PreparedStatement pstmt; 
    @Mock private Statement stmt; 
    @Mock private ResultSet rs;

    // serve como um reset para os mocks antes de cada teste, define o que deve ser retornado quando métodos específicos forem chamados
    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        
        // Sempre que pedir um PreparedStatement, retorna o mock
        when(con.prepareStatement(anyString())).thenReturn(pstmt);
        // Sempre que pedir um Statement simples, retorna o  mock
        when(con.createStatement()).thenReturn(stmt);
        // Sempre que executar uma query, retorna o mock
        when(pstmt.executeQuery()).thenReturn(rs);
        when(stmt.executeQuery(anyString())).thenReturn(rs);
    }

    // ========= Testes do existeSolicitacao =========
    @Test
    public void testeExisteSolicitacaoTrue() throws SQLException {
        // rs.next() retornando true, indicando que tem um resultado
        when(rs.next()).thenReturn(true);
        // rs.getInt(1) retorna 1, achou um registro
        when(rs.getInt(1)).thenReturn(1);

        boolean existe = SolicitacaoModel.existeSolicitacao(con, 50);

        assertTrue(existe);
        // Verifica se o SQL correto foi preparado (opcional, mas bom para garantir)
        verify(con).prepareStatement(contains("SELECT COUNT(*) FROM solicitacao"));
    }

    @Test
    public void testeExisteSolicitacaoFalse() throws SQLException {
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(0);

        boolean existe = SolicitacaoModel.existeSolicitacao(con, 50);

        assertFalse(existe);
    }

    // ========= Testes do criarSolicitacao =========
    @Test
    public void testeCriarSolicitacaoSucesso() throws SQLException {
        /*
            O metodo faz 3 inserts:
                - na tabela solicitacao, retornando o id gerado
                - na tabela rel_solicitadoacao
                - na tabela rel_solicita
        */
        
        // primeiro retorna um ID gerado 
        when(rs.next()).thenReturn(true); 
        when(rs.getInt(1)).thenReturn(100); // ID gerado 100

        // acao
        SolicitacaoModel.criarSolicitacao(con, 1, 2);

        // verificacao
        // se o autoCommit foi desligado para iniciar transacao
        verify(con).setAutoCommit(false);
        
        // se ele tentou fazer 3 prepares de statement (3 inserts)
        verify(con, times(3)).prepareStatement(anyString());
        
        // se o commit foi chamado no final
        verify(con).commit();
        
        // se o autoCommit voltou pra true no finally
        verify(con).setAutoCommit(true);
    }

    @Test
    public void testeCriarSolicitacaoErroRollback() throws SQLException {
        // simulando um erro de sql no meio do processo
        // Quando tentar executar o query, lança exceção
        when(pstmt.executeQuery()).thenThrow(new SQLException("Erro simulado de banco"));

        // acao
        try {
            SolicitacaoModel.criarSolicitacao(con, 1, 2);
            fail("Deveria ter lançado SQLException");
        } catch (SQLException e) {
            // eh para jogar aqui
        }

        // verificacao 
        // se deu erro tem que ter chamado rollback
        verify(con).rollback();
        // e nao pode ter chamado commit
        verify(con, never()).commit();
    }

    // ========= Testes do listaSolicitacoesDetalhadas =========
    @Test
    public void testeListaSolicitacoesDetalhadas() throws SQLException {
        // result tem 1 linha e depois acaba
        when(rs.next()).thenReturn(true, false);
        
        // Simulamos os dados vindos das colunas do banco 
        when(rs.getInt("id_solicitacao")).thenReturn(10);
        when(rs.getDate("datasolicitacao")).thenReturn(new java.sql.Date(System.currentTimeMillis()));
        when(rs.getString("status")).thenReturn("Pendente");
        when(rs.getInt("id_usuario")).thenReturn(5);
        when(rs.getString("nome_receptor")).thenReturn("Maria");
        when(rs.getString("nome_doacao")).thenReturn("Cesta Básica");

        // acao
        ArrayList<SolicitacaoBean> lista = SolicitacaoModel.listaSolicitacoesDetalhadas(con);

        // verificacao
        assertNotNull(lista);
        assertEquals(1, lista.size()); // Veio 1 item
        
        SolicitacaoBean item = lista.get(0);
        assertEquals(10, item.getIdSolicitacao());
        assertEquals("Maria", item.getNomeReceptor());
        assertEquals("Cesta Básica", item.getDescricaoGeralDoacao());
    }
}