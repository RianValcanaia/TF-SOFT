package testes;

// importacao de dependencias
import bean.EnderecoBean;
import bean.UsuarioBean;
import model.UsuarioModel;

import java.sql.*;
import java.util.ArrayList;

// junit
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// mockito
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

// assets e verificacoes
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

public class UsuarioModelTest {
    // cria mocks para substituir dependencias externas do model, simulando o comportamento delas
    @Mock private Connection con;
    @Mock private PreparedStatement pstmt;
    @Mock private Statement stmt;
    @Mock private ResultSet rs;

    // serve como um reset para os mocks antes de cada teste, define o que deve ser retornado quando métodos específicos forem chamados
    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);

        // sempre que pedir um Preparedstatement retorna o mock
        when(con.prepareStatement(anyString())).thenReturn(pstmt);
        // sempre que pedir um Statement retorna o mock
        when(con.createStatement()).thenReturn(stmt);
        // sempre que executar uma query retorna o mock do ResultSet
        when(pstmt.executeQuery()).thenReturn(rs);
        when(stmt.executeQuery(anyString())).thenReturn(rs);
    }

    // ========= Teste de verificacoes basicas ==========
    @Test
    public void testeExisteUsuario() throws SQLException {
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(1);

        boolean existe = UsuarioModel.existeUsuario(con, 10);
        assertTrue(existe);
    }

    @Test
    public void testeNaoExisteUsuario() throws SQLException {
        when(rs.next()).thenReturn(false); // db nao encontrou 

        boolean existe = UsuarioModel.existeUsuario(con, 10);

        assertFalse(existe);
    }

    @Test
    public void testeEhDoador() throws SQLException {
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(1);

        boolean ehDoador = UsuarioModel.ehDoador(con, 10);
        assertTrue(ehDoador);
    }

    @Test
    public void testeNaoEhDoador() throws SQLException {
        when(rs.next()).thenReturn(false); // db nao encontrou

        boolean ehDoador = UsuarioModel.ehDoador(con, 10);

        assertFalse(ehDoador);
    }

    @Test
    public void testeEhReceptor() throws SQLException {
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(1);

        boolean ehReceptor = UsuarioModel.ehReceptor(con, 10);
        assertTrue(ehReceptor);
    }

    @Test
    public void testeNaoEhReceptor() throws SQLException {
        when(rs.next()).thenReturn(false); // db nao encontrou

        boolean ehReceptor = UsuarioModel.ehReceptor(con, 10);

        assertFalse(ehReceptor);
    }

    // ========= Testes de cadastro de usuario =========
    @Test
    public void testeCadastrarUsuarioComoDoador() throws SQLException {
        UsuarioBean user = new UsuarioBean();
        user.setNome("Mercado Teste");
        user.setTipoUsuario("DOADOR"); // para o if
        user.setCnpj(123456780001L);
        user.setTipoEstabelecimento("Mercado");

        EnderecoBean end = new EnderecoBean();
        end.setRua("Rua Teste");

        /*
           O metodo faz dois inserts que retornam ID (endereco e usuario).
           simulando que o primeiro retorna ID 50 e o segundo ID 100 .
         */
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(50).thenReturn(100);

        // acao
        UsuarioModel.cadastrarUsuario(con, user, end);

        // verificacao
        verify(con).setAutoCommit(false);
        verify(con).prepareStatement(contains("INSERT INTO endereco"));
        verify(con).prepareStatement(contains("INSERT INTO usuario"));
        verify(con).prepareStatement(contains("INSERT INTO doador"));
        verify(con).prepareStatement(contains("INSERT INTO rel_enderecousuario"));

        verify(con).commit();
    }

    @Test
    public void testeCadastrarUsuarioComoReceptor() throws SQLException {
        UsuarioBean user = new UsuarioBean();
        user.setNome("Ong Teste");
        user.setTipoUsuario("RECEPTOR"); // para o else
        user.setCpfCnpjReceptor(98765432100L);
        user.setNomeResponsavel("Joao");

        EnderecoBean end = new EnderecoBean();

        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(50).thenReturn(100);

        // acao
        UsuarioModel.cadastrarUsuario(con, user, end);
        
        // verificacao
        verify(con).setAutoCommit(false);
        verify(con).prepareStatement(contains("INSERT INTO endereco"));
        verify(con).prepareStatement(contains("INSERT INTO usuario"));
        verify(con).prepareStatement(contains("INSERT INTO receptor"));
        verify(con, never()).prepareStatement(contains("INSERT INTO doador"));
        verify(con).commit();
    }

    @Test
    public void testeCadastrarErroRollback() throws SQLException {
        UsuarioBean user = new UsuarioBean();
        EnderecoBean end = new EnderecoBean();

        // simula erro logo no primeiro insert
        when(pstmt.executeQuery()).thenThrow(new SQLException("Falha SQL"));

        try {
            UsuarioModel.cadastrarUsuario(con, user, end);
            fail("Deveria lançar exceção");
        } catch (SQLException e) {
            // ok
        }

        verify(con).rollback();
        verify(con, never()).commit();
    }

    // ========= Testes de listagem de usuarios =========

    @Test
    public void testeListaUsuarios() throws SQLException {
        when(rs.next()).thenReturn(true, false);
        when(rs.getInt(1)).thenReturn(1);
        when(rs.getString(2)).thenReturn("Ligia");

        ArrayList<UsuarioBean> lista = UsuarioModel.listaUsuarios(con);
        
        assertNotNull(lista);
        assertEquals(1, lista.size());
        assertEquals("Ligia", lista.get(0).getNome());
    }

    @Test
    public void testeListaDoadores() throws SQLException {
        when(rs.next()).thenReturn(true, false);
        when(rs.getString(6)).thenReturn("Supermercado"); 

        ArrayList<UsuarioBean> lista = UsuarioModel.listaDoadores(con);

        assertNotNull(lista);
        assertEquals("DOADOR", lista.get(0).getTipoUsuario());  // list adiciona doador
        assertEquals("Supermercado", lista.get(0).getTipoEstabelecimento());
    }

    @Test
    public void testeListaReceptores() throws SQLException {
        when(rs.next()).thenReturn(true, false);
        when(rs.getString(6)).thenReturn("Responsavel Teste");

        ArrayList<UsuarioBean> lista = UsuarioModel.listaReceptores(con);

        assertNotNull(lista);
        assertEquals("RECEPTOR", lista.get(0).getTipoUsuario());  // list adiciona receptor
        assertEquals("Responsavel Teste", lista.get(0).getNomeResponsavel());
    }

    @Test
    public void testeListaTopDoadores() throws SQLException {
        // O método retorna Strings formatadas
        when(rs.next()).thenReturn(true, false);
        when(rs.getInt("id_usuario")).thenReturn(10);
        when(rs.getString("nome")).thenReturn("Top Doador");
        when(rs.getInt("qtd_doacoes")).thenReturn(50);

        ArrayList<String> lista = UsuarioModel.listaTopDoadores(con);

        assertNotNull(lista);
        assertTrue(lista.get(0).contains("Top Doador"));
        assertTrue(lista.get(0).contains("Total Doações: 50"));
    }

    // ========== Testes de update e delete de usuario ==========
    @Test
    public void testeUpdateUsuario() throws SQLException {
        UsuarioModel.update(con, 1, "Novo Nome", "email@teste.com", 123456L);

        verify(con).prepareStatement(contains("UPDATE usuario SET"));
        verify(pstmt).setString(1, "Novo Nome");
        verify(pstmt).executeUpdate();
    }

    @Test
    public void testeDeleteUsuarioSucesso() throws SQLException {
        /*
            busca ennderecos vinculados (SELECT ... rel_enderecousuario).
            itera sobre esses endereços e deleta/atualiza tabelas relacionadas.
            deleta das tabelas de usuário (rel_cadastra, doador, receptor, usuario).
         */

        // ususario tem 1 endereco vinculado
        when(rs.next()).thenReturn(true, false); // 1 retorno
        when(rs.getInt("id_endereco")).thenReturn(99);

        // acao
        UsuarioModel.delete(con, 10);

        // verificacoes
        verify(con).setAutoCommit(false);
        verify(con).prepareStatement(contains("SELECT id_endereco FROM rel_enderecousuario"));
        verify(con).prepareStatement(contains("UPDATE rel_enderecocoleta SET id_endereco = NULL"));
        // verifica pelo menos uma vez o delete do endereco 99
        verify(pstmt, atLeastOnce()).setInt(1, 99); 
        verify(con).prepareStatement(contains("DELETE FROM doador"));
        verify(con).prepareStatement(contains("DELETE FROM receptor"));
        verify(con).prepareStatement(contains("DELETE FROM usuario"));

        verify(con).commit();
    }
    
    @Test
    public void testeDeleteUsuarioSemEndereco() throws SQLException {
        // simula usuario sem endereco cadastrado (rs.next retorna false logo de cara)
        when(rs.next()).thenReturn(false);

        UsuarioModel.delete(con, 10);

        // nao deve deletar endereco
        verify(con, never()).prepareStatement(contains("DELETE FROM endereco"));
        
        // mas deleta usuario 
        verify(con).prepareStatement(contains("DELETE FROM usuario"));
        verify(con).commit();
    }
}