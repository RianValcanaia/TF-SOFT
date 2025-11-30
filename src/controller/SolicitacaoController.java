package controller;

import java.sql.Connection;
import java.util.Scanner;
import model.SolicitacaoModel;
import model.DoacaoModel;
import model.UsuarioModel;
import bean.SolicitacaoBean;

/*
    Controla o fluxo de Solicitações de Doação (Interesse do Receptor).
    Responsável por:
        - Interagir com o usuário para registrar o interesse em uma doação (criarSolicitacao).
        - Listar as solicitações com detalhes (nomes) para facilitar a leitura.
        - Atualizar o status do pedido (Pendente -> Aceita/Recusada).
*/

public class SolicitacaoController {
    private Scanner in = new Scanner(System.in);
    
    public void cadastrarSolicitacao(Connection con) {
        System.out.println("--- SOLICITAR DOAÇÃO ---");
        
        try{
            System.out.print("ID da doação desejada: ");
            int idDoacao = in.nextInt();

            while (!DoacaoModel.existeDoacao(con, idDoacao)) {
                System.out.println("ID de doação inválido. Tente novamente.");
                System.out.print("ID da doação desejada: ");
                idDoacao = in.nextInt();
            }
            in.nextLine();

            System.out.print("ID do seu usuário (Receptor): ");
            int idReceptor = in.nextInt();
            while(!UsuarioModel.ehReceptor(con, idReceptor)) {
                System.out.println("ID de usuário inválido ou não é receptor. Tente novamente.");
                System.out.print("ID do seu usuário (Receptor): ");
                idReceptor = in.nextInt();
            }
            in.nextLine();
            
            SolicitacaoModel.criarSolicitacao(con, idDoacao, idReceptor);
            System.out.println("Solicitacao registrada! Status: Pendente");
        }catch(Exception e) {
            System.out.println("Erro ao cadastrar: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void listarSolicitacoes(Connection con) {
        try {
            System.out.println("--- RELATÓRIO DE SOLICITAÇÕES ---");
            
            for(SolicitacaoBean s : SolicitacaoModel.listaSolicitacoesDetalhadas(con)) {
                System.out.println(s.toString());
            }
        } catch(Exception e) {
            System.out.println("Erro ao listar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void atualizar(Connection con) {
        System.out.println("--- ATUALIZAR SOLICITAÇÃO ---");
        try {
            System.out.print("ID da solicitação: ");
            int id = in.nextInt();
            while(!SolicitacaoModel.existeSolicitacao(con, id)) {
                System.out.println("ID de solicitação inválido. Tente novamente.");
                System.out.print("ID da solicitação: ");
                id = in.nextInt();
            }
            in.nextLine();

            System.out.print("Novo Status (Aprovada/Recusada/Entregue): ");
            String status = in.nextLine();

            SolicitacaoModel.updateStatus(con, id, status);
            System.out.println("Solicitação atualizada.");
        } catch(Exception e) {
            System.out.println("Erro ao atualizar: " + e.getMessage());
        }
    }

    public void deletar(Connection con) {
        System.out.println("--- DELETAR SOLICITAÇÃO ---");
        try {
            System.out.print("ID da solicitação: ");
            int id = in.nextInt();
            
            while(!SolicitacaoModel.existeSolicitacao(con, id)) {
                System.out.println("ID de solicitação inválido. Tente novamente.");
                System.out.print("ID da solicitação: ");
                id = in.nextInt();
            }
            in.nextLine();

            SolicitacaoModel.delete(con, id);
            System.out.println("Solicitação removida.");
        } catch(Exception e) {
            System.out.println("Erro ao deletar: " + e.getMessage());
        }
    }
}