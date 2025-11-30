package model;

import java.sql.*;
import java.util.ArrayList;
import bean.DoacaoBean;

/*
    Realiza todas as operações de banco de dados relacionadas às Doações.
    Responsável por:
        - Verificar existência de doações e itens.
        - Cadastrar doações e vincular ao doador e endereço de coleta.
        - Adicionar itens a uma doação, incluindo inserções nas tabelas específicas
          (alimento, vestuário ou higiene).
        - Listar doações completas com informações do doador.
        - Listar itens pertencentes a uma doação.
        - Atualizar status de doações e quantidade de itens.
        - Excluir doações e todos os vínculos relacionados
          (itens, endereço de coleta, doador e solicitações).
        - Excluir itens individualmente, removendo também das tabelas específicas.
    Atua diretamente com SQL e transações (commit/rollback) para garantir integridade.
*/

public class DoacaoModel {
    // ---- Verificações de Existência ----
    public static boolean existeDoacao(Connection con, int idDoacao) throws SQLException {
        String sql = "SELECT COUNT(*) FROM doacao WHERE id_doacao = ?";
        PreparedStatement st = con.prepareStatement(sql);
        st.setInt(1, idDoacao);
        ResultSet rs = st.executeQuery();
        rs.next();
        boolean existe = rs.getInt(1) > 0;
        rs.close();
        st.close();
        return existe;
    }

    public static boolean existeItem(Connection con, int idItem) throws SQLException {
        String sql = "SELECT COUNT(*) FROM itemdoado WHERE id_item = ?";
        PreparedStatement st = con.prepareStatement(sql);
        st.setInt(1, idItem);
        ResultSet rs = st.executeQuery();
        rs.next();
        boolean existe = rs.getInt(1) > 0;
        rs.close();
        st.close();
        return existe;
    }
    
    // Somente cadastra uma doacao, modificando as tabelas: doacao, rel_cadastradoacao e rel_enderecocoleta
    public static void cadastrarDoacao(Connection con, DoacaoBean doacao, int idDoador) throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {
            con.setAutoCommit(false);
            String sqlDoacao = "INSERT INTO doacao (datacadastro, descricaogeral, statusdoacao) VALUES (?, ?, 'Disponivel') RETURNING id_doacao";
            st = con.prepareStatement(sqlDoacao);
            st.setDate(1, doacao.getDataCadastro()); 
            st.setString(2, doacao.getDescricaoGeral());
            // pega o id_doacao gerado
            rs = st.executeQuery();
            
            if (rs.next()) {
                int idDoacao = rs.getInt(1);
                doacao.setIdDoacao(idDoacao);
                st.close();

                // adiciona em rel_cadastradoacao
                st = con.prepareStatement("INSERT INTO rel_cadastradoacao (id_doacao, id_usuario) VALUES (?, ?)");
                st.setInt(1, idDoacao);
                st.setInt(2, idDoador);
                st.execute();
                st.close();
                
                // busca o primeiro endereço do usuário para usar como local de coleta padrão
                String sqlBuscaEnd = "SELECT id_endereco FROM rel_enderecousuario WHERE id_usuario = ? LIMIT 1";
                st = con.prepareStatement(sqlBuscaEnd);
                st.setInt(1, idDoador);
                rs = st.executeQuery();
                
                int idEndereco = 0;
                if (rs.next()) {
                    idEndereco = rs.getInt(1);
                }
                st.close();
                
                // se tem endereço cadastrado, cria ele, é para ter, mas por garantia verificamos
                if (idEndereco > 0) {
                    st = con.prepareStatement("INSERT INTO rel_enderecocoleta (id_doacao, id_endereco, datahora) VALUES (?, ?, ?)");
                    st.setInt(1, idDoacao);
                    st.setInt(2, idEndereco);
                    st.setDate(3, doacao.getDataColeta());
                    st.execute();
                    st.close();
                }
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

    // Adiciona um item a uma doação, modificando as tabelas: itemdoado, rel_itemdoacao e a tabela específica conforme o tipo
    public static void adicionarItem(Connection con, DoacaoBean item) throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            con.setAutoCommit(false);

            // Insere na tabela ItemDoado
            String sqlItem = "INSERT INTO itemdoado (descricao, quantidade, unidademedida) VALUES (?, ?, ?) RETURNING id_item";
            st = con.prepareStatement(sqlItem);
            st.setString(1, item.getDescricaoItem());
            st.setDouble(2, item.getQuantidade());
            st.setString(3, item.getUnidadeMedida());
            rs = st.executeQuery();
            
            if (rs.next()) {
                int idItem = rs.getInt(1);
                st.close();

                // Vincula item com a doacao na tabela rel_itemdoacao
                st = con.prepareStatement("INSERT INTO rel_itemdoacao (id_item, id_doacao) VALUES (?, ?)");
                st.setInt(1, idItem);
                st.setInt(2, item.getIdDoacao()); 
                st.execute();
                st.close();

                // Insere na tabela específica conforme o tipo
                String tipo = item.getTipoItem();
                
                if ("A".equalsIgnoreCase(tipo)) {
                    st = con.prepareStatement("INSERT INTO alimento (id_item, datavalidade) VALUES (?, ?)");
                    st.setInt(1, idItem);
                    st.setDate(2, item.getDataValidade());
                    st.execute();
                
                } else if ("V".equalsIgnoreCase(tipo)) {
                    st = con.prepareStatement("INSERT INTO vestuario (id_item, faixaetaria, genero, tamanho) VALUES (?, ?, ?, ?)");
                    st.setInt(1, idItem);
                    st.setString(2, item.getFaixaEtaria());
                    st.setString(3, item.getGenero());
                    st.setInt(4, item.getTamanho());
                    st.execute();
                
                } else if ("H".equalsIgnoreCase(tipo)) {
                    st = con.prepareStatement("INSERT INTO higiene (id_item, volume) VALUES (?, ?)");
                    st.setInt(1, idItem);
                    st.setDouble(2, item.getVolume());
                    st.execute();
                }
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

    // Lista completa de doacoes, join doacao, rel_cadastradoacao e usuario
    public static ArrayList<DoacaoBean> listaCompletaDoacoes(Connection con) throws SQLException {
        ArrayList<DoacaoBean> list = new ArrayList<>();
        Statement st = con.createStatement();
        String sql = "SELECT d.id_doacao, d.descricaogeral, d.statusdoacao, d.datacadastro, u.nome " +
                     "FROM doacao d " +
                     "LEFT JOIN rel_cadastradoacao cd ON d.id_doacao = cd.id_doacao " +
                     "LEFT JOIN usuario u ON cd.id_usuario = u.id_usuario " +
                     "ORDER BY d.id_doacao";
                     
        ResultSet rs = st.executeQuery(sql);
        while(rs.next()) {
            DoacaoBean d = new DoacaoBean();
            d.setIdDoacao(rs.getInt(1));
            d.setDescricaoGeral(rs.getString(2));
            d.setStatusDoacao(rs.getString(3));
            d.setDataCadastro(rs.getDate(4));
            d.setNomeDoador(rs.getString(5));
            list.add(d);
        }
        return list;
    }

    // Lista itens de uma doação específica join itemdoado e rel_itemdoacao
    public static ArrayList<String> listaItensDaDoacao(Connection con, int idDoacao) throws SQLException {
        ArrayList<String> list = new ArrayList<>();
        String sql = "SELECT i.id_item, i.descricao, i.quantidade, i.unidademedida " +
                     "FROM itemdoado i " +
                     "JOIN rel_itemdoacao rel ON i.id_item = rel.id_item " +
                     "WHERE rel.id_doacao = ?";
        PreparedStatement st = con.prepareStatement(sql);
        st.setInt(1, idDoacao);
        ResultSet rs = st.executeQuery();
        while(rs.next()) {
            list.add("ID Item: " + rs.getInt(1) + " | " + rs.getString(2) + " (" + rs.getDouble(3) + " " + rs.getString(4) + ")");
        }
        return list;
    }
    
    // Atualiza o status da doação
    public static void updateDoacao(Connection con, int idDoacao, String novoStatus) throws SQLException {
        String sql = "UPDATE doacao SET statusdoacao = ? WHERE id_doacao = ?";
        PreparedStatement st = con.prepareStatement(sql);
        st.setString(1, novoStatus);
        st.setInt(2, idDoacao);
        st.executeUpdate();
    }

    // Atualiza a quantidade de um item doado
    public static void updateItem(Connection con, int idItem, double novaQtd) throws SQLException {
        String sql = "UPDATE itemdoado SET quantidade = ? WHERE id_item = ?";
        PreparedStatement st = con.prepareStatement(sql);
        st.setDouble(1, novaQtd);
        st.setInt(2, idItem);
        st.executeUpdate();
    }

    public static void deleteDoacao(Connection con, int idDoacao) throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            con.setAutoCommit(false);

            // itens que pertencem à doação
            String sqlBuscaItens = "SELECT id_item FROM rel_itemdoacao WHERE id_doacao = ?";
            st = con.prepareStatement(sqlBuscaItens);
            st.setInt(1, idDoacao);
            rs = st.executeQuery();

            while (rs.next()) {
                int idItem = rs.getInt("id_item");
                // remove o item individualmente por categoria
                deleteItemIndividual(con, idItem);
            }
            rs.close();
            st.close();
            
            // Remove vínculo com Doador
            st = con.prepareStatement("DELETE FROM rel_cadastradoacao WHERE id_doacao = ?");
            st.setInt(1, idDoacao);
            st.executeUpdate();
            st.close();

            // Remove vínculo com Endereço de Coleta
            st = con.prepareStatement("DELETE FROM rel_enderecocoleta WHERE id_doacao = ?");
            st.setInt(1, idDoacao);
            st.executeUpdate();
            st.close();
            
            // Remove vínculo com Solicitações 
            st = con.prepareStatement("DELETE FROM rel_solicitadoacao WHERE id_doacao = ?");
            st.setInt(1, idDoacao);
            st.executeUpdate();
            st.close();

            // Deletar a Doação
            st = con.prepareStatement("DELETE FROM doacao WHERE id_doacao = ?");
            st.setInt(1, idDoacao);
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

    // deleta um item específico
    public static void deleteItem(Connection con, int idItem) throws SQLException {
        try {
            con.setAutoCommit(false);  // garante que eliminamos de todas as tabelas restantes caso uma falhar
            deleteItemIndividual(con, idItem);
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    // Deleta de uma tabela especifica
    private static void deleteItemIndividual(Connection con, int idItem) throws SQLException {
        PreparedStatement st;

        // Remove das tabelas específicas
        st = con.prepareStatement("DELETE FROM alimento WHERE id_item = ?");
        st.setInt(1, idItem);
        st.executeUpdate();
        st.close();

        st = con.prepareStatement("DELETE FROM vestuario WHERE id_item = ?");
        st.setInt(1, idItem);
        st.executeUpdate();
        st.close();

        st = con.prepareStatement("DELETE FROM higiene WHERE id_item = ?");
        st.setInt(1, idItem);
        st.executeUpdate();
        st.close();

        st = con.prepareStatement("DELETE FROM rel_itemdoacao WHERE id_item = ?");
        st.setInt(1, idItem);
        st.executeUpdate();
        st.close();

        st = con.prepareStatement("DELETE FROM itemdoado WHERE id_item = ?");
        st.setInt(1, idItem);
        st.executeUpdate();
        st.close();
    }
}