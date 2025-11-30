package controller;

import java.sql.Connection;
import java.util.Scanner;
import bean.UsuarioBean;
import bean.EnderecoBean;
import model.UsuarioModel;

/*
    Controla todas as operações relacionadas aos usuários.
    Responsável por:
        - Cadastrar usuários (coleta dados pessoais e endereço pelo console).
        - Listar usuários, doadores, receptores e ranking de doadores.
        - Atualizar informações de usuários existentes.
        - Excluir usuários após validar que o ID existe.
    Atua como ponte entre a entrada do usuário (Scanner) e o modelo (UsuarioModel).
*/

public class UsuarioController {
    
    private Scanner in = new Scanner(System.in);

    public void cadastrarUsuario(Connection con) {
        UsuarioBean user = new UsuarioBean();
        EnderecoBean end = new EnderecoBean();

        System.out.println("--- CADASTRO DE USUARIO ---");
        System.out.print("Nome: "); user.setNome(in.nextLine());
        System.out.print("Email: "); user.setEmail(in.next()); in.nextLine();
        System.out.print("Telefone: "); user.setTelefone(in.nextLong()); in.nextLine();
        
        System.out.print("Tipo (1-Doador / 2-Receptor): ");
        int tipo = in.nextInt();
        in.nextLine(); 
        
        if (tipo == 1) {
            user.setTipoUsuario("DOADOR");
            System.out.print("CNPJ: "); user.setCnpj(in.nextLong()); in.nextLine();
            System.out.print("Tipo Estabelecimento (Loja/Mercado): "); user.setTipoEstabelecimento(in.nextLine());
        } else {
            user.setTipoUsuario("RECEPTOR");
            System.out.print("CPF/CNPJ: "); user.setCpfCnpjReceptor(in.nextLong()); in.nextLine();
            System.out.print("Responsavel: "); user.setNomeResponsavel(in.nextLine());
        }

        System.out.println("--- ENDERECO ---");
        System.out.print("Pais: "); end.setPais(in.nextLine());
        System.out.print("Estado: "); end.setEstado(in.nextLine());
        System.out.print("CEP: "); end.setCep(in.nextInt()); in.nextLine();
        System.out.print("Cidade: "); end.setCidade(in.nextLine());
        System.out.print("Bairro: "); end.setBairro(in.nextLine());
        System.out.print("Rua: "); end.setRua(in.nextLine());
        System.out.print("Numero: "); end.setNumero(in.nextInt()); in.nextLine();
        // coordenadas geográficas ficariam com a API

        try {
            UsuarioModel.cadastrarUsuario(con, user, end);
            System.out.println("Usuario cadastrado com ID: " + user.getIdUsuario());
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar: " + e.getMessage());
        }
    }
    
    public void listarTodosUsuarios(Connection con) {
        try {
            System.out.println("--- LISTA GERAL DE USUARIOS ---");
            for(UsuarioBean u : UsuarioModel.listaUsuarios(con)) {
                System.out.println("ID: " + u.getIdUsuario() + " | Nome: " + u.getNome() + " | Email: " + u.getEmail() + " | Telefone: " + u.getTelefone());
            }
        } catch (Exception e) {
            System.out.println("Erro listar: " + e.getMessage());
        }
    }

    public void listarDoadores(Connection con) {
        try {
            System.out.println("--- LISTA DE DOADORES ---");
            for(UsuarioBean u : UsuarioModel.listaDoadores(con)) {
                System.out.println("ID: " + u.getIdUsuario() + " | Nome: " + u.getNome() + " | Tipo: " + u.getTipoEstabelecimento());
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar doadores: " + e.getMessage());
        }
    }
    
    public void listarRankingDoadores(Connection con) {
        try {
            System.out.println("--- RANKING DE DOADORES (ACIMA DA MEDIA) ---");
            for(String s : UsuarioModel.listaTopDoadores(con)) {
                System.out.println(s);
            }
        } catch (Exception e) {
            System.out.println("Erro ao gerar ranking: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void listarReceptores(Connection con) {
        try {
            System.out.println("--- LISTA DE RECEPTORES ---");
            for(UsuarioBean u : UsuarioModel.listaReceptores(con)) {
                System.out.println("ID: " + u.getIdUsuario() + " | Nome: " + u.getNome() + " | Responsavel: " + u.getNomeResponsavel());
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar receptores: " + e.getMessage());
        }
    }

    public void atualizar(Connection con) {
        System.out.println("--- ATUALIZAR USUARIO ---");

        try{
            System.out.print("ID do Usuario: ");
            int id = in.nextInt();

            while(!UsuarioModel.existeUsuario(con, id)) {
                System.out.print("ID invalido. Digite novamente: ");
                id = in.nextInt();
            }
            in.nextLine(); // buffer

            System.out.print("Nome: ");
            String nome = in.nextLine();

            System.out.print("Novo Email: ");
            String email = in.next();

            System.out.print("Novo Telefone: ");
            long tel = in.nextLong();
            in.nextLine(); // buffer

            
            UsuarioModel.update(con, id, nome, email, tel);
            System.out.println("Usuario atualizado!");

        } catch (Exception e) {
            System.out.println("Erro ao atualizar: " + e.getMessage());
        }
    }

    public void deletar(Connection con) {
        System.out.println("--- DELETAR USUARIO ---");
        try {
            System.out.print("ID do Usuario a excluir: ");
            int id = in.nextInt();
            while(!UsuarioModel.existeUsuario(con, id)) {
                System.out.print("ID invalido. Digite novamente: ");
                id = in.nextInt();
            }
            in.nextLine();

            UsuarioModel.delete(con, id);
            System.out.println("Usuario deletado com sucesso.");
        } catch (Exception e) {
            System.out.println("Erro ao deletar (verifique se o usuario tem vinculos): " + e.getMessage());
        }
    }
}