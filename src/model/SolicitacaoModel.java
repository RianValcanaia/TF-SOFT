package model;

import java.sql.*;
import java.util.ArrayList;
import bean.SolicitacaoBean;

/*
    Manipula todas as operações de banco relacionadas às Solicitações de Doação.
    Responsável por:
        - Verificar se uma solicitação existe.
        - Criar novas solicitações e registrar vínculos com doação e receptor.
        - Listar solicitações com informações detalhadas (doação, receptor, status).
        - Atualizar o status da solicitação (ex.: Pendente → Aceita/Recusada).
        - Excluir solicitações removendo todos os vínculos associados.
    Utiliza transações (commit/rollback) para garantir integridade dos dados.
*/

public class SolicitacaoModel {
    // Verifica se a solicitação existe
    public static boolean existeSolicitacao(Connection con, int idSolicitacao) throws SQLException {
        String sql = "SELECT COUNT(*) FROM solicitacao WHERE id_solicitacao = ?";
        PreparedStatement st = con.prepareStatement(sql);
        st.setInt(1, idSolicitacao);
        ResultSet rs = st.executeQuery();
        boolean exists = false;
        if (rs.next()) {
            exists = rs.getInt(1) > 0;
        }
        rs.close();
        st.close();
        return exists;
    }

    // cria uma nova solicitacao, adiciona nas tabelas soliticacao, rel_solicitadoacao, rel_solicita
    public static void criarSolicitacao(Connection con, int idDoacao, int idReceptor) throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            con.setAutoCommit(false);

            // insere na tabela Solicitacao
            String sqlSol = "INSERT INTO solicitacao (datasolicitacao, status) VALUES (CURRENT_DATE, 'Pendente') RETURNING id_solicitacao";
            st = con.prepareStatement(sqlSol);
            rs = st.executeQuery();
            
            if (rs.next()) {
                int idSolicitacao = rs.getInt(1);
                st.close();

                // insere na rel_solicitadoacao 
                st = con.prepareStatement("INSERT INTO rel_solicitadoacao (id_solicitacao, id_doacao) VALUES (?, ?)");
                st.setInt(1, idSolicitacao);
                st.setInt(2, idDoacao);
                st.execute();
                st.close();

                // insere na rel_solicita
                st = con.prepareStatement("INSERT INTO rel_solicita (id_solicitacao, id_usuario) VALUES (?, ?)");
                st.setInt(1, idSolicitacao);
                st.setInt(2, idReceptor);
                st.execute();
                st.close();
            }
            
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
    
    // lista fazendo join de: solicitacao, rel_solicita, usuario, rel_solicitadoacao, doacao
    public static ArrayList<SolicitacaoBean> listaSolicitacoesDetalhadas(Connection con) throws SQLException {
        ArrayList<SolicitacaoBean> list = new ArrayList<>();
        Statement st = con.createStatement();
        
        String sql = "SELECT s.id_solicitacao, s.datasolicitacao, s.status, u.id_usuario, u.nome AS nome_receptor, d.descricaogeral AS nome_doacao " +
                     "FROM solicitacao s " +
                     "JOIN rel_solicita rs ON s.id_solicitacao = rs.id_solicitacao " +
                     "JOIN usuario u ON rs.id_usuario = u.id_usuario " +
                     "JOIN rel_solicitadoacao rsd ON s.id_solicitacao = rsd.id_solicitacao " +
                     "JOIN doacao d ON rsd.id_doacao = d.id_doacao " +
                     "ORDER BY s.id_solicitacao";

        ResultSet rs = st.executeQuery(sql);
        
        while(rs.next()){
            SolicitacaoBean sb = new SolicitacaoBean();
            sb.setIdSolicitacao(rs.getInt("id_solicitacao"));
            sb.setDataSolicitacao(rs.getDate("datasolicitacao"));
            sb.setStatus(rs.getString("status"));
            
            sb.setIdReceptor(rs.getInt("id_usuario"));
            sb.setNomeReceptor(rs.getString("nome_receptor"));
            sb.setDescricaoGeralDoacao(rs.getString("nome_doacao"));
            
            list.add(sb);
        }
        rs.close();
        st.close();
        return list;
    }

    public static void updateStatus(Connection con, int idSolicitacao, String novoStatus) throws SQLException {
        String sql = "UPDATE solicitacao SET status = ? WHERE id_solicitacao = ?";
        PreparedStatement st = null;
        try{
            st = con.prepareStatement(sql);
            st.setString(1, novoStatus);
            st.setInt(2, idSolicitacao);
            st.executeUpdate();
        } finally {
            if (st != null) st.close();
        }
    }

    public static void delete(Connection con, int idSolicitacao) throws SQLException {
        PreparedStatement st = null;

        try {
            con.setAutoCommit(false); 

            // renive rel_solicitacaodoacao
            String sqlRelDoacao = "DELETE FROM rel_solicitadoacao WHERE id_solicitacao = ?";
            st = con.prepareStatement(sqlRelDoacao);
            st.setInt(1, idSolicitacao);
            st.executeUpdate();
            st.close();

            // remove rel_solicita
            String sqlRelReceptor = "DELETE FROM rel_solicita WHERE id_solicitacao = ?";
            st = con.prepareStatement(sqlRelReceptor);
            st.setInt(1, idSolicitacao);
            st.executeUpdate();
            st.close();

            // remove solicitacao
            String sqlSolicitacao = "DELETE FROM solicitacao WHERE id_solicitacao = ?";
            st = con.prepareStatement(sqlSolicitacao);
            st.setInt(1, idSolicitacao);
            st.executeUpdate();
            st.close();

            con.commit(); 
        } catch (SQLException e) {
            con.rollback(); // Desfaz tudo se der erro
            throw e;
        } finally {
            con.setAutoCommit(true); // Restaura o comportamento padrão
            if (st != null) st.close();
        }
    }
}