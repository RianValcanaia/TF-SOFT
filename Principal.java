import java.sql.Connection;
import java.util.Scanner;
import utils.Conexao;
import controller.UsuarioController;
import controller.DoacaoController;
import controller.SolicitacaoController;

public class Principal {
    public static void main(String[] args) {
        Connection con = new Conexao().getConnection();
        
        if (con == null) {
            System.out.println("Falha na conexão com o banco de dados. Encerrando.");
            return;
        }

        Scanner in = new Scanner(System.in);
        UsuarioController usuarioCtrl = new UsuarioController();
        DoacaoController doacaoCtrl = new DoacaoController();
        SolicitacaoController solicitacaoCtrl = new SolicitacaoController();

        int opcao = 0;

        do {
            limpaTela();
            System.out.println("\n#########################################");
            System.out.println("      SISTEMA DE GESTÃO DE DOAÇÕES");
            System.out.println("#########################################");
            System.out.println("1 - Cadastrar");
            System.out.println("2 - Listar");
            System.out.println("3 - Atualizar");
            System.out.println("4 - Deletar");
            System.out.println("0 - Sair");
            System.out.print("Opcao: ");
            
            try {
                opcao = Integer.parseInt(in.nextLine());
            } catch (NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1: // MENU CADASTRAR
                    menuCadastrar(in, usuarioCtrl, doacaoCtrl, solicitacaoCtrl, con);
                    break;
                case 2: // MENU LISTAR
                    menuListar(in, usuarioCtrl, doacaoCtrl, solicitacaoCtrl, con);
                    break;
                case 3: // MENU ATUALIZAR
                    menuAtualizar(in, usuarioCtrl, doacaoCtrl, solicitacaoCtrl, con);
                    break;
                case 4: // MENU DELETAR
                    menuDeletar(in, usuarioCtrl, doacaoCtrl, solicitacaoCtrl, con);
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opcao invalida!");
            }

        } while (opcao != 0);
    }

    // --- SUBMENUS ---

    private static void menuCadastrar(Scanner in, UsuarioController uCtrl, DoacaoController dCtrl, SolicitacaoController sCtrl, Connection con) {
        int op = 0;
        do{
            limpaTela();
            System.out.println("\n--- MENU CADASTRAR ---");
            System.out.println("1 - Cadastrar usuário");
            System.out.println("2 - Cadastrar doação");
            System.out.println("3 - Cadastrar item à doação");
            System.out.println("4 - Cadastrar solicitação");
            System.out.println("0 - Voltar");
            System.out.print("Opção: ");

            try {
                op = Integer.parseInt(in.nextLine());
            } catch (NumberFormatException e) {
                op = -1;
            }

            limpaTela();
            switch(op) {
                case 1: uCtrl.cadastrarUsuario(con); break;
                case 2: dCtrl.cadastrarDoacao(con); break;
                case 3: dCtrl.adicionarItem(con); break;
                case 4: sCtrl.cadastrarSolicitacao(con); break;
                default: System.out.println("Opção inválida.");
            }
            if (op != 0) pausa(in);
        }while(op!=0);
    }

    private static void menuListar(Scanner in, UsuarioController uCtrl, DoacaoController dCtrl, SolicitacaoController sCtrl, Connection con) {
        int op = 0;
        do{
            limpaTela();
            System.out.println("\n--- MENU LISTAR ---");
            System.out.println("1 - Listar usuários");
            System.out.println("2 - Listar doadores");
            System.out.println("3 - Listar receptores");
            System.out.println("4 - Listar todas as doações");
            System.out.println("5 - Listar solicitações"); 
            System.out.println("6 - Ranking de doadores com doações acima da média");
            System.out.println("7 - Listar itens de uma doação");
            System.out.println("0 - Voltar");
            System.out.print("Opção: ");
            
            try {
                op = Integer.parseInt(in.nextLine());
            } catch (NumberFormatException e) {
                op = -1;
            }
            limpaTela();

            switch(op) {
                case 1: uCtrl.listarTodosUsuarios(con); break;
                case 2: uCtrl.listarDoadores(con); break;
                case 3: uCtrl.listarReceptores(con); break;
                case 4: dCtrl.listarCompletoDoacoes(con); break;
                case 5: sCtrl.listarSolicitacoes(con); break;
                case 6: uCtrl.listarRankingDoadores(con); break;
                case 7: dCtrl.listarItensDaDoacao(con); break;
                default: System.out.println("Opção inválida.");
            }
            if (op != 0) pausa(in);
        } while(op!=0);
    }

    private static void menuAtualizar(Scanner in, UsuarioController uCtrl, DoacaoController dCtrl, SolicitacaoController sCtrl, Connection con) {
        int op = 0;
        do{
            limpaTela();
            System.out.println("\n--- MENU ATUALIZAR ---");
            System.out.println("1 - Atualizar usuário");
            System.out.println("2 - Atualizar Doação (Status)");
            System.out.println("3 - Atualizar Item da Doação (Qtd)");
            System.out.println("4 - Atualizar Solicitação (Status)");
            System.out.println("0 - Voltar");
            System.out.print("Opção: ");
            
            try {
                op = Integer.parseInt(in.nextLine());
            } catch (NumberFormatException e) {
                op = -1;
            }

            limpaTela();

            switch(op) {
                case 1: uCtrl.atualizar(con); break;
                case 2: dCtrl.atualizarDoacao(con); break;
                case 3: dCtrl.atualizarItem(con); break;
                case 4: sCtrl.atualizar(con); break;
                default: System.out.println("Opcao invalida.");
            }
            if (op != 0) pausa(in);
        }while(op!=0);
    }

    private static void menuDeletar(Scanner in, UsuarioController uCtrl, DoacaoController dCtrl, SolicitacaoController sCtrl, Connection con) {
        int op = 0;
        do{
            limpaTela();
            System.out.println("\n--- MENU DELETAR ---");
            System.out.println("1 - Deletar Usuario");
            System.out.println("2 - Deletar Doacao");
            System.out.println("3 - Deletar Item da Doacao");
            System.out.println("4 - Deletar Solicitacao");
            System.out.println("0 - Voltar");
            System.out.print("Opcao: ");
            
            try {
                op = Integer.parseInt(in.nextLine());
            } catch (NumberFormatException e) {
                op = -1;
            }
            
            limpaTela();
            switch(op) {
                case 1: uCtrl.deletar(con); break;
                case 2: dCtrl.deletarDoacao(con); break;
                case 3: dCtrl.deletarItem(con); break;
                case 4: sCtrl.deletar(con); break;
                default: System.out.println("Opcao invalida.");
            }
            if (op != 0) pausa(in);
        }while(op!=0);

    }

    private static void limpaTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void pausa(Scanner in) {
        System.out.println("\nPressione ENTER para continuar...");
        in.nextLine();
    }
}