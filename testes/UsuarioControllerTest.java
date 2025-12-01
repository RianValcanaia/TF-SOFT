package testes;

import controller.UsuarioController;
import model.UsuarioModel;
import bean.UsuarioBean;
import bean.EnderecoBean;

// Importações do JUnit
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*; 

// Importações do Mockito
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

// Importações de IO e SQL
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;

public class UsuarioControllerTest {

    // --- VARIÁVEIS GLOBAIS DA CLASSE ---
    @Mock 
    private Connection con;
    
    // Captura a saída do console para fazer asserts nas mensagens
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    // --- CONFIGURAÇÃO INICIAL ---
    @BeforeEach
    public void setUp() {
        // Inicializa os @Mock (como o 'con')
        MockitoAnnotations.openMocks(this);
        // Redireciona o System.out para a variável outContent
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testCadastrarUsuarioDoadorSucesso() {
        // --- 1. Preparação (Arrange) ---
        String inputSimulado = "Joao Silva\njoao@email.com\n99999999\n1\n12345678900\nMercado\nBrasil\nSC\n89200000\nJoinville\nCentro\nRua A\n100\n";
        
        InputStream in = new ByteArrayInputStream(inputSimulado.getBytes());
        System.setIn(in);

        try (MockedStatic<UsuarioModel> modelMock = mockStatic(UsuarioModel.class)) {
            UsuarioController controller = new UsuarioController();

            // --- 2. Ação (Act) ---
            controller.cadastrarUsuario(con);

            // --- 3. Verificação (Assert) ---
            modelMock.verify(() -> UsuarioModel.cadastrarUsuario(
                eq(con), 
                any(UsuarioBean.class), 
                any(EnderecoBean.class)
            ));
            
            // Verifica mensagem de sucesso
            assertTrue(outContent.toString().contains("Usuario cadastrado com ID"), "Deveria imprimir sucesso");
        }
    }

    @Test
    public void testeCadastrarUsuarioErroDeAtomicidade() {
        // --- 1. ARRANGE ---
        String input = "Mercado Falho\nmercado@falha.com\n4799999999\n1\n12345678000199\nSupermercado\nBrasil\nSC\n89200000\nJoinville\nCentro\nRua A\n100\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // --- 2. PREPARANDO O ERRO ---
        try (MockedStatic<UsuarioModel> modelMock = mockStatic(UsuarioModel.class)) {
            
            // Simula erro no banco
            modelMock.when(() -> UsuarioModel.cadastrarUsuario(any(), any(), any()))
                    .thenThrow(new RuntimeException("Falha de Atomicidade: Erro ao salvar endereço"));

            UsuarioController controller = new UsuarioController();

            // --- 3. ACT ---
            controller.cadastrarUsuario(con);

            // --- 4. ASSERT ---
            String saidaConsole = outContent.toString();
            
            assertTrue(saidaConsole.contains("Erro ao cadastrar"), 
                "O sistema deveria avisar que houve um erro.");
                
            assertTrue(saidaConsole.contains("Falha de Atomicidade"), 
                "O sistema deveria mostrar o motivo da falha.");
        }
    }
}