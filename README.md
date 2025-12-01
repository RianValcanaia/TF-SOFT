<div align="center" id="topo">

<img src="https://media.giphy.com/media/iIqmM5tTjmpOB9mpbn/giphy.gif" width="200px" alt="Gif animado"/>

# <code><strong> Testes unitários na plataforma de doações </strong></code>

<em>Projeto final da disciplina de Engenharia de Software.</em>

[![Java Usage](https://img.shields.io/badge/Java-100%25-orange?style=for-the-badge&logo=java)]()
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue?style=for-the-badge&logo=postgresql)]()
[![Status](https://img.shields.io/badge/Status-Concluído-green?style=for-the-badge)]()
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Visite%20meu%20perfil-blue?style=for-the-badge&logo=linkedin)](https://www.linkedin.com/in/rian-carlos-valcanaia-b2b487168/)

</div>

## Índice

Índice
- [📌 Objetivos](#-objetivos)  
- [🧱 Estruturas de Dados](#-estruturas-de-dados)
- [🧰 Funcionalidades](#-funcionalidades)
- [📂 Como executar](#-como-executar)
- [👨‍🏫 Envolvidos](#-envolvidos)

- [📅 Curso](#-curso)
- [📄 Código-fonte](#-código-fonte)

## 📌 Objetivos

O objetivo desse trabalho é desenvolver testes unitários em um projeto. Os testes unitários servem para testar isoladamente pequenas partes do código, como métodos e classes, para garantir que a lógica interna funcione exatamente como o esperado. Eles são fundamentais para assegurar a qualidade do software e prevenir que alterações futuras quebrem funcionalidades já existentes.

[⬆ Voltar ao topo](#topo)

## 🧱 Estruturas de Dados
A modelagem de dados segue o padrão EER, mapeado para classes Java (Beans).

### 🔹Estrutura do projeto
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

### 🔹 Testes

- `DoacaoBeanTest`: Testa a classe DoacaoBean.

- `DoacaoControllerTest` Testa a classe DoacaoController.

- `DoacaoModelTest`: Testa a classe DoacaoModel.

- `EnderecoBeanTest`: Testa a classe EnderecoBean.

- `SolicitacaoBeanTest`: Testa a classe SolicitacaoBean.

- `SolicitacaoControllerTest`: Testa a classe SolicitacaoController.

- `SolicitacaoModelTest`: Testa a classe SolicitacaoModel.

- `UsuarioBeanTest`: Testa a classe UsuarioBean.

- `UsuarioControllerTest`: Testa a classe UsuarioController.

- `UsuarioModelTest`: Testa a classe UsuarioModel.

### 🔹  Modelos (Models)

As classes Model executam as queries SQL diretamente via JDBC.
- `CRUD Completo`: Inserção, Leitura, Atualização e Deleção (com tratamento de chaves estrangeiras).

- `Relatórios com JOINs`: Listagem detalhada de doações com nomes dos doadores e itens.

- `Relatório Agregado`: Um destaque do sistema é o método listaTopDoadores, que utiliza Subquery, COUNT, AVG, GROUP BY e HAVING para filtrar os doadores mais ativos.

[⬆ Voltar ao topo](#topo)

## 📂 Como executar
O projeto possui as dependências configuradas. Para rodar a bateria de testes é necessário ter o Maven e o JDK 21 instalados.

### 🔹 Instalando o Maven 
    ```bash
    # Verifificar se o Maven já está instalado
    mvn -v

    # Instalar o Maven (Ubuntu)
    sudo apt update
    sudo apt install maven -y

    # Verificar se o Java (JDK) está instalado
    java -version
    javac -version
    
    # Instalar o JDK 21 (caso não tenha)
    sudo apt install openjdk-21-jdk -y
    ```

### 🔹 Rodar os testes
    ```bash
    # No diretório raiz do projeto (onde fica o pom.xml)
    mvn test
    ```

[⬆ Voltar ao topo](#topo)

## 👨‍🏫 Envolvidos
* **Professora**: Rebeca Schroeder Freitas
* **Estudantes**:
  * [Rian Carlos Valcanaia](https://github.com/RianValcanaia)
  * [Matheus Azevedo de Sá](https://github.com/Math-Az)
  * [Lucas Oliveira Macedo](https://github.com/lucasomac0)

[⬆ Voltar ao topo](#topo)

## 📅 Curso

* **Universidade**: Universidade do Estado de Santa Catarina (UDESC)
* **Disciplina**: Engenharia de Software
* **Semestre**: 5º

[⬆ Voltar ao topo](#topo)

## 📄 Código-fonte

🔗 [https://github.com/RianValcanaia/TF-SOFT](https://github.com/RianValcanaia/TF-SOFT)

[⬆ Voltar ao topo](#topo)
