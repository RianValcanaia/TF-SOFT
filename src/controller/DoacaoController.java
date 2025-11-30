package controller;

import java.sql.Connection;
import java.util.Scanner;
import bean.DoacaoBean;
import model.DoacaoModel;
import model.UsuarioModel;
import java.time.LocalDate;
import java.sql.Date;
/*
    Controla o gerenciamento de Usuários (Doadores e Receptores).
    Responsável por:
        - Coletar dados cadastrais comuns e endereço.
        - Aplicar a lógica de diferenciação (switch) entre Doador (CNPJ) e Receptor (CPF/Responsável).
        - Gerar relatórios específicos, como a lista de Doadores e o Ranking de Doadores acima da média.
 */

public class DoacaoController {
    private Scanner in = new Scanner(System.in);

    public void cadastrarDoacao(Connection con) {
        System.out.println("--- NOVA DOAÇÃO ---");

        DoacaoBean d = new DoacaoBean();
        LocalDate hoje = LocalDate.now();
        Date sqlDate = Date.valueOf(hoje);
        d.setDataCadastro(sqlDate);
        
        try {
            System.out.print("ID do usuario doador (deve existir): ");
            int idDoador = in.nextInt();
            
            while(!UsuarioModel.ehDoador(con, idDoador)) {
                System.out.print("ID inválido ou usuário não é doador. Informe um ID de usuário existente: ");
                idDoador = in.nextInt();
            }
            in.nextLine(); // limpar buffer

            System.out.print("Título/Descrição geral da doação: ");
            d.setDescricaoGeral(in.nextLine());

            System.out.print("Data de Coleta(AAAA-MM-DD): ");
            String dataColetaStr = in.nextLine();
            Date dataColeta = Date.valueOf(dataColetaStr);
            d.setDataColeta(dataColeta);

            DoacaoModel.cadastrarDoacao(con, d, idDoador);
            System.out.println("Doação cadastrada com sucesso!");
        }catch(Exception e) {
            System.out.println("Erro ao criar a doação: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void adicionarItem(Connection con) {
        System.out.println("--- ADICIONAR ITEM A DOACAO ---");
        
        DoacaoBean item = new DoacaoBean();

        try {
            System.out.print("Informe o ID da Doacao à qual este item pertence (precisa existir): ");
            int idDoacao = in.nextInt();

            while (!DoacaoModel.existeDoacao(con, idDoacao)) {
                System.out.print("ID inválido. Informe um ID de Doacao existente: ");
                idDoacao = in.nextInt();
            }
            item.setIdDoacao(idDoacao);
            in.nextLine(); // limpar buffer

            System.out.print("Descrição do Item (ex: Arroz, Camisa): "); 
            item.setDescricaoItem(in.nextLine());

            System.out.print("Quantidade: "); 
            item.setQuantidade(in.nextDouble());
            
            System.out.print("Unidade (kg, litros, und): "); 
            item.setUnidadeMedida(in.next());
            
            System.out.print("Tipo (A-Alimento, V-Vestuario, H-Higiene): ");
            String tipo = in.next().toUpperCase();
            item.setTipoItem(tipo);
            in.nextLine(); // limpar buffer do next()

            if ("A".equals(tipo)) {
                System.out.print("Data de Validade (AAAA-MM-DD): "); 
                String dataVal = in.nextLine();
                Date sqlDate = Date.valueOf(dataVal);
                item.setDataValidade(sqlDate);
            } else if ("V".equals(tipo)) {
                System.out.print("Genero (M/F/U): "); 
                item.setGenero(in.next());
                in.nextLine(); // limpar buffer
                
                System.out.print("Faixa Etaria (Crianca/Adulto/Idoso): ");
                item.setFaixaEtaria(in.nextLine());
                
                System.out.print("Tamanho (Numérico - ex: 40): "); 
                item.setTamanho(in.nextInt());
                in.nextLine(); // limpar buffer
            } else if ("H".equals(tipo)) {
                System.out.print("Volume (em ml ou L): "); 
                item.setVolume(in.nextDouble());
                in.nextLine(); // limpar buffer
            }

            DoacaoModel.adicionarItem(con, item);
            System.out.println("Item adicionado com sucesso à doação " + idDoacao + "!");
        } catch (Exception e) {
            System.out.println("Erro ao adicionar item: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void listarCompletoDoacoes(Connection con) {
        try {
            System.out.println("--- DOACOES DISPONIVEIS ---");
            for(DoacaoBean d : DoacaoModel.listaCompletaDoacoes(con)) {
                System.out.println(d);
            }
        } catch(Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public void listarItensDaDoacao(Connection con) {
        try {
            System.out.print("Digite o ID da Doacao para ver os itens: ");
            int id = in.nextInt();

            while(!DoacaoModel.existeDoacao(con, id)) {
                System.out.print("ID inválido. Informe um ID de Doacao existente: ");
                id = in.nextInt();
            }
            in.nextLine();
                System.out.println("--- ITENS DA DOACAO " + id + " ---");
                for (String s : DoacaoModel.listaItensDaDoacao(con, id)) {
                    System.out.println(s);
                }
        } catch(Exception e) {
            System.out.println("Erro ao listar itens: " + e.getMessage());
        }
    }

    public void atualizarDoacao(Connection con) {
        try {
            System.out.print("ID da Doacao a atualizar: ");
            int id = in.nextInt();

            while(!DoacaoModel.existeDoacao(con, id)) {
                System.out.print("ID inválido. Informe um ID de Doacao existente: ");
                id = in.nextInt();
            }
            in.nextLine(); // limpar buffer

            System.out.print("Novo Status (Disponivel/Agendada/Coletada/Cancelada): ");
            String status = in.nextLine();

            DoacaoModel.updateDoacao(con, id, status);
            System.out.println("Status da doacao atualizado.");
        } catch(Exception e) {
            System.out.println("Erro ao atualizar: " + e.getMessage());
        }
    }

    public void atualizarItem(Connection con) {
        try {
            System.out.print("ID do Item a atualizar: ");
            int id = in.nextInt();

            while(!DoacaoModel.existeItem(con, id)) {
                System.out.print("ID inválido. Informe um ID de Item existente: ");
                id = in.nextInt();
            }
            in.nextLine(); // limpar buffer

            System.out.print("Nova Quantidade: ");
            double qtd = in.nextDouble();
            in.nextLine();

            DoacaoModel.updateItem(con, id, qtd);
            System.out.println("Item atualizado.");
        } catch(Exception e) {
            System.out.println("Erro ao atualizar item: " + e.getMessage());
        }
    }

    public void deletarDoacao(Connection con) {
        try {
            System.out.print("ID da Doacao a deletar: ");
            int id = in.nextInt();

            while(!DoacaoModel.existeDoacao(con, id)) {
                System.out.print("ID inválido. Informe um ID de Doacao existente: ");
                id = in.nextInt();
            }
            in.nextLine();

            DoacaoModel.deleteDoacao(con, id);
            System.out.println("Doacao removida.");
        } catch(Exception e) {
            System.out.println("Erro ao deletar doacao: " + e.getMessage());
        }
    }

    public void deletarItem(Connection con) {
        try {
            System.out.print("ID do Item a deletar: ");
            int id = in.nextInt();

            while(!DoacaoModel.existeItem(con, id)) {
                System.out.print("ID inválido. Informe um ID de Item existente: ");
                id = in.nextInt();
            }
            in.nextLine();
            
            DoacaoModel.deleteItem(con, id);
            System.out.println("Item removido.");
        } catch(Exception e) {
            System.out.println("Erro ao deletar item: " + e.getMessage());
        }
    }
}