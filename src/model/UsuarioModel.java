package model;

import java.sql.*;
import java.util.ArrayList;
import bean.UsuarioBean;
import bean.EnderecoBean;

/*
    Realiza todas as operações de banco de dados relacionadas aos Usuários.
    Responsável por:
        - Verificar existência de usuários, doadores e receptores.
        - Cadastrar usuários completos (endereço + usuário + perfil: doador/receptor).
        - Listar usuários, doadores e receptores com suas informações associadas.
        - Gerar relatório de doadores acima da média usando subconsulta e agregação.
        - Atualizar dados básicos (nome, email, telefone).
        - Excluir usuários removendo todos os vínculos: endereços, doador, receptor,
          e relações com doações.
    Atua diretamente com SQL e transações para preservar a integridade dos dados.
*/


public class UsuarioModel {
    // ---- Verificações ----
    public static boolean existeUsuario(Connection con, int idUsuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuario WHERE id_usuario = ?";
        PreparedStatement st = con.prepareStatement(sql);
        st.setInt(1, idUsuario);
        ResultSet rs = st.executeQuery();
        rs.next();
        boolean existe = rs.getInt(1) > 0;
        rs.close();
        st.close();
        return existe;
    }

    public static boolean ehDoador(Connection con, int idUsuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM doador WHERE id_usuario = ?";
        PreparedStatement st = con.prepareStatement(sql);
        st.setInt(1, idUsuario);
        ResultSet rs = st.executeQuery();
        rs.next();
        boolean ehDoador = rs.getInt(1) > 0;
        rs.close();
        st.close();
        return ehDoador;
    }

    public static boolean ehReceptor(Connection con, int idUsuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM receptor WHERE id_usuario = ?";
        PreparedStatement st = con.prepareStatement(sql);
        st.setInt(1, idUsuario);
        ResultSet rs = st.executeQuery();
        rs.next();
        boolean ehReceptor = rs.getInt(1) > 0;
        rs.close();
        st.close();
        return ehReceptor;
    }

    // Cadastra o usuario adicionando nas tabelas: endereco, usuario, doador/receptor, rel_enderecousuario
    public static void cadastrarUsuario(Connection con, UsuarioBean user, EnderecoBean end) throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            con.setAutoCommit(false);
            
            // Adiciona na tabela endereco
            String sqlEnd = "INSERT INTO endereco (pais, estado, cep, cidade, bairro, rua, numero) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id_endereco";
            st = con.prepareStatement(sqlEnd);
            st.setString(1, end.getPais());
            st.setString(2, end.getEstado());
            st.setInt(3, end.getCep());
            st.setString(4, end.getCidade());
            st.setString(5, end.getBairro());
            st.setString(6, end.getRua());
            st.setInt(7, end.getNumero());
            rs = st.executeQuery();
            rs.next();
            int idEndereco = rs.getInt(1);
            st.close();

            // cria o usuário na tabela usuario
            String sqlUser = "INSERT INTO usuario (nome, email, telefone) VALUES (?, ?, ?) RETURNING id_usuario";
            st = con.prepareStatement(sqlUser);
            st.setString(1, user.getNome());
            st.setString(2, user.getEmail());
            st.setLong(3, user.getTelefone());
            rs = st.executeQuery();
            rs.next();
            int idUsuario = rs.getInt(1);
            user.setIdUsuario(idUsuario);
            st.close();

            // Doador ou Receptor
            if ("DOADOR".equalsIgnoreCase(user.getTipoUsuario())) {
                st = con.prepareStatement("INSERT INTO doador (id_usuario, cnpj, tipoestabelecimento) VALUES (?, ?, ?)");
                st.setInt(1, idUsuario);
                st.setLong(2, user.getCnpj());
                st.setString(3, user.getTipoEstabelecimento());
                st.execute();
            } else {
                st = con.prepareStatement("INSERT INTO receptor (id_usuario, cpf_cnpj, nomeresponsavel) VALUES (?, ?, ?)");
                st.setInt(1, idUsuario);
                st.setLong(2, user.getCpfCnpjReceptor());
                st.setString(3, user.getNomeResponsavel());
                st.execute();
            }
            st.close();

            //
            st = con.prepareStatement("INSERT INTO rel_enderecousuario (id_endereco, id_usuario) VALUES (?, ?)");
            st.setInt(1, idEndereco);
            st.setInt(2, idUsuario);
            st.execute();

            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    // Subconsulta e agregacao, usuario que tem doacoes acima da media
    public static ArrayList<String> listaTopDoadores(Connection con) throws SQLException {
        ArrayList<String> relatorio = new ArrayList<>();
        Statement st = con.createStatement();
        
        String sql = "SELECT u.id_usuario, u.nome, u.email, u.telefone, COUNT(rc.id_doacao) as qtd_doacoes " +
                     "FROM usuario u " +
                     "JOIN rel_cadastradoacao rc ON u.id_usuario = rc.id_usuario " +
                     "GROUP BY u.id_usuario, u.nome, u.email, u.telefone " +
                     "HAVING COUNT(rc.id_doacao) >= (" +
                     "    SELECT AVG(sub.contagem) FROM (" +
                     "        SELECT COUNT(id_doacao) as contagem " +
                     "        FROM rel_cadastradoacao " +
                     "        GROUP BY id_usuario" +
                     "    ) as sub" +
                     ") " +
                     "ORDER BY qtd_doacoes DESC";

        ResultSet rs = st.executeQuery(sql);
        while(rs.next()) {
            String linha = 
                "ID: " + rs.getInt("id_usuario") +
                " | Nome: " + rs.getString("nome") +
                " | Email: " + rs.getString("email") +
                " | Telefone: " + rs.getString("telefone") +
                " | Total Doações: " + rs.getInt("qtd_doacoes");
            relatorio.add(linha);
        }
        return relatorio;
    }

    public static ArrayList<UsuarioBean> listaUsuarios(Connection con) throws SQLException {
        ArrayList<UsuarioBean> list = new ArrayList<>();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT id_usuario, nome, email, telefone FROM usuario ORDER BY id_usuario");

        while(rs.next()) {
            UsuarioBean u = new UsuarioBean();
            u.setIdUsuario(rs.getInt(1));
            u.setNome(rs.getString(2));
            u.setEmail(rs.getString(3));
            u.setTelefone(rs.getLong(4));
            list.add(u);
        }
        return list;
    }

    public static ArrayList<UsuarioBean> listaDoadores(Connection con) throws SQLException {
        ArrayList<UsuarioBean> list = new ArrayList<>();
        Statement st = con.createStatement();
        String sql = "SELECT u.id_usuario, u.nome, u.email, u.telefone, d.cnpj, d.tipoestabelecimento FROM usuario u JOIN doador d ON u.id_usuario = d.id_usuario";
        ResultSet rs = st.executeQuery(sql);
        while(rs.next()) {
            UsuarioBean u = new UsuarioBean();
            u.setIdUsuario(rs.getInt(1));
            u.setNome(rs.getString(2));
            u.setEmail(rs.getString(3));
            u.setTelefone(rs.getLong(4));
            u.setCnpj(rs.getLong(5));
            u.setTipoEstabelecimento(rs.getString(6));
            u.setTipoUsuario("DOADOR");
            list.add(u);
        }
        return list;
    }

    public static ArrayList<UsuarioBean> listaReceptores(Connection con) throws SQLException {
        ArrayList<UsuarioBean> list = new ArrayList<>();
        Statement st = con.createStatement();
        String sql = "SELECT u.id_usuario, u.nome, u.email, u.telefone, r.cpf_cnpj, r.nomeresponsavel FROM usuario u JOIN receptor r ON u.id_usuario = r.id_usuario";
        ResultSet rs = st.executeQuery(sql);
        while(rs.next()) {
            UsuarioBean u = new UsuarioBean();
            u.setIdUsuario(rs.getInt(1));
            u.setNome(rs.getString(2));
            u.setEmail(rs.getString(3));
            u.setTelefone(rs.getLong(4));
            u.setCpfCnpjReceptor(rs.getLong(5));
            u.setNomeResponsavel(rs.getString(6));
            u.setTipoUsuario("RECEPTOR");
            list.add(u);
        }
        return list;
    }

    public static void update(Connection con, int idUsuario, String novoNome, String novoEmail, long novoTelefone) throws SQLException {
        String sql = "UPDATE usuario SET nome = ?, email = ?, telefone = ? WHERE id_usuario = ?";
        PreparedStatement st = con.prepareStatement(sql);
        st.setString(1, novoNome);
        st.setString(2, novoEmail);
        st.setLong(3, novoTelefone);
        st.setInt(4, idUsuario);
        st.executeUpdate();
        st.close();
    }

    public static void delete(Connection con, int idUsuario) throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {
            con.setAutoCommit(false);
            
            // Apagar todos os endereços vinculados ao usuário
            ArrayList<Integer> idsEnderecos = new ArrayList<>();
            st = con.prepareStatement("SELECT id_endereco FROM rel_enderecousuario WHERE id_usuario = ?");
            st.setInt(1, idUsuario);
            rs = st.executeQuery();
            while(rs.next()) {
                idsEnderecos.add(rs.getInt("id_endereco"));
            }
            rs.close();
            st.close();
            
            
            for (int idEnd : idsEnderecos) {
                /// atualiza rel_enderecocoleta para cada endereco do user
                st = con.prepareStatement("UPDATE rel_enderecocoleta SET id_endereco = NULL WHERE id_endereco = ?");
                st.setInt(1, idEnd);
                st.executeUpdate();
                st.close();
                
                // remove rel_enderecousuario do user
                st = con.prepareStatement("DELETE FROM rel_enderecousuario WHERE id_endereco = ?");
                st.setInt(1, idEnd);
                st.executeUpdate();
                st.close();
                
                // remove o endereco
                st = con.prepareStatement("DELETE FROM endereco WHERE id_endereco = ?");
                st.setInt(1, idEnd);
                st.executeUpdate();
                st.close();
            }

            // remove user da rel_cadastradoacao
            st = con.prepareStatement("DELETE FROM rel_cadastradoacao WHERE id_usuario = ?");
            st.setInt(1, idUsuario);
            st.executeUpdate();
            st.close();
            
            // remove de doador
            st = con.prepareStatement("DELETE FROM doador WHERE id_usuario = ?");
            st.setInt(1, idUsuario);
            st.executeUpdate();
            st.close();
            
            // remove de receptor
            st = con.prepareStatement("DELETE FROM receptor WHERE id_usuario = ?");
            st.setInt(1, idUsuario);
            st.executeUpdate();
            st.close();

            // deleta o usuario
            st = con.prepareStatement("DELETE FROM usuario WHERE id_usuario = ?");
            st.setInt(1, idUsuario);
            st.executeUpdate();
            st.close();
            
            con.commit();
            
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            if (rs != null) rs.close();
            if (st != null) st.close();
        }
    }
}