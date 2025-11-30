<div align="center" id="topo">

<img src="https://media.giphy.com/media/iIqmM5tTjmpOB9mpbn/giphy.gif" width="200px" alt="Gif animado"/>

# <code><strong> Plataforma de Doações </strong></code>

<em>Projeto final da disciplina de Banco de Dados 1.</em>

[![Java Usage](https://img.shields.io/badge/Java-100%25-orange?style=for-the-badge&logo=java)]()
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue?style=for-the-badge&logo=postgresql)]()
[![Status](https://img.shields.io/badge/Status-Concluído-green?style=for-the-badge)]()
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Visite%20meu%20perfil-blue?style=for-the-badge&logo=linkedin)](https://www.linkedin.com/in/rian-carlos-valcanaia-b2b487168/)

</div>

## Índice

Índice
- [📌 Objetivos](#-objetivos)  
- [📥 Entradas do sistema](#-entradas-do-sistema)
- [🧱 Estruturas de Dados](#-estruturas-de-dados)
- [🧰 Funcionalidades](#-funcionalidades)
- [📊 Exemplo de Execução](#-exemplo-de-execução)
- [📂 Como executar](#-como-executar)
- [👨‍🏫 Envolvidos](#-envolvidos)
- [📅 Curso](#-curso)
- [📄 Código-fonte](#-código-fonte)

## 📌 Objetivos

Este projeto tem como propósito técnico o desenvolvimento de um Banco de Dados Relacional robusto, integrado a uma aplicação Java via JDBC.

Do ponto de vista social e de engenharia, a plataforma busca solucionar dois problemas críticos:

- `O desperdício de produtos`: Excedentes de estabelecimentos comerciais (alimentos próximos ao vencimento, vestuário de coleções passadas, etc.).

- `A vulnerabilidade social`: A carência de itens essenciais por parte de instituições e pessoas necessitadas.

A solução atua como uma ponte digital entre Doadores (restaurantes, mercados, lojas) e Receptores (ONGs, instituições de caridade), otimizando e formalizando o processo de doação com segurança e rastreabilidade.

[⬆ Voltar ao topo](#topo)

## 📥 Entradas do sistema
O sistema interage com o usuário para receber as informações necessárias para a persistência e lógica de negócios:

Dados Cadastrais: Informações de Pessoa Jurídica (Doadores) ou Responsáveis (Receptores) e endereços completos para logística.

Inventário de Doação: Descrição, validade (para alimentos), tamanho/gênero (para vestuário), volume (para higiene) e datas de coleta.

Comandos de Fluxo: Interações de menu para registrar solicitações, atualizar status de entrega e gerar relatórios. Assim como comandos de deletar.

[⬆ Voltar ao topo](#topo)

## 🧱 Estruturas de Dados
A modelagem de dados segue o padrão EER, mapeado para classes Java (Beans).

- `Usuario`: Entidade pai que armazena dados comuns (Nome, Email, Telefone).

    - `Doador (Especialização de Usuário)`: Possui CNPJ e Tipo de Estabelecimento.

    - `Receptor (Especialização de Usuário)`: Possui CPF/CNPJ e Nome do Responsável.

- `Doacao`: Entidade central que registra a oferta contém:

    - `ItemDoado`: Detalha o produto, especializado em:

    - `Alimento`: Controla data de validade.

    - `Vestuario`: Controla faixa etária, gênero e tamanho.

    - `Higiene`: Controla volume.

- `Solicitacao`: Entidade que liga um Receptor a uma Doação, controlando o status do pedido.

[⬆ Voltar ao topo](#topo)

## 🧰 Funcionalidades
### 🔹 Controladores (Controllers)

- `UsuarioController`: Gerencia o CRUD de perfis e endereços.

- `DoacaoController`: Implementa a lógica de cadastro de itens e atualização de status.

- `SolicitacaoController`: Gerencia a interação entre Receptor e Doação.

### 🔸  Modelos (Models)

As classes Model executam as queries SQL diretamente via JDBC.
- `CRUD Completo`: Inserção, Leitura, Atualização e Deleção (com tratamento de chaves estrangeiras).

- `Relatórios com JOINs`: Listagem detalhada de doações com nomes dos doadores e itens.

- `Relatório Agregado`: Um destaque do sistema é o método listaTopDoadores, que utiliza Subquery, COUNT, AVG, GROUP BY e HAVING para filtrar os doadores mais ativos.

[⬆ Voltar ao topo](#topo)

## 📊 Exemplo de Execução
1. O sistema inicia conectando ao banco de dados PostgreSQL.

2. O Menu Principal oferece: Cadastrar, Listar, Atualizar e Deletar.

3. O usuário cadastra um Doador (ex: Mercado X) e seus dados de endereço.

4. O Doador cadastra uma Doação (ex: "Cesta Básica") e adiciona Itens (ex: "Arroz", Tipo Alimento, Validade 2025).

5. O usuário cadastra um Receptor (ex: ONG Y).

6. O Receptor visualiza as doações disponíveis e cria uma Solicitação para a "Cesta Básica".

7. O sistema permite gerar um relatório de "Ranking de Doadores" para ver quem está doando acima da média da plataforma.

[⬆ Voltar ao topo](#topo)

## 📂 Como executar
Para compilar e executar o projeto, você precisará do JDK instalado e de um servidor PostgreSQL rodando.

1. `Configuração do Banco de Dados`: Crie um banco de dados chamado plataforma_doacao e execute o script SQL `docs/db.txt` para criar as tabelas.

2. `Configuração da Conexão`: Edite o arquivo src/utils/Conexao.java com suas credenciais do postgres:

    ``` Java
    String url = "jdbc:postgresql://localhost:5432/plataforma_doacao";
    String user = "seu_usuario";
    String senha = "sua_senha";
    ``` 

3. `Rodar a aplicação`
    ``` bash
    # Compilar
    javac -d build/classes -cp postgresql-42.6.0.jar Principal.java src/**/*.java

    # Rodar
    java -cp "build/classes:postgresql-42.6.0.jar" Principal
    ``` 

[⬆ Voltar ao topo](#topo)

## 👨‍🏫 Envolvidos
* **Professora**: Rebeca Schroeder Freitas
* **Estudantes**:
  * [Rian Carlos Valcanaia](https://github.com/RianValcanaia)
  * [Matheus Azevedo de Sá](https://github.com/Math-Az)

[⬆ Voltar ao topo](#topo)

## 📅 Curso

* **Universidade**: Universidade do Estado de Santa Catarina (UDESC)
* **Disciplina**: Banco de Dados 1
* **Semestre**: 5º

[⬆ Voltar ao topo](#topo)

## 📄 Código-fonte

🔗 [https://github.com/RianValcanaia/TF-BAN](https://github.com/RianValcanaia/TF-BAN)

[⬆ Voltar ao topo](#topo)
