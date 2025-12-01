package testes;

import controller.DoacaoController;
import model.DoacaoModel;
import model.UsuarioModel;
import bean.DoacaoBean;

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
import java.io.PrintStream;
import java.sql.Connection;

public class DoacaoControllerTest {

    // --- VARIÁVEIS GLOBAIS DA CLASSE ---
    @Mock 
    private Connection con;
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    // --- CONFIGURAÇÃO INICIAL ---
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testCadastrarDoacaoComSucesso() {
        // --- Arrange ---
        String input = "10\nCesta Basica\n2025-12-31\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        try (MockedStatic<UsuarioModel> userModelMock = mockStatic(UsuarioModel.class);
            MockedStatic<DoacaoModel> doacaoModelMock = mockStatic(DoacaoModel.class)) {

            userModelMock.when(() -> UsuarioModel.ehDoador(con, 10)).thenReturn(true);

            DoacaoController controller = new DoacaoController();

            // --- Act ---
            controller.cadastrarDoacao(con);

            // --- Assert ---
            doacaoModelMock.verify(() -> DoacaoModel.cadastrarDoacao(
                eq(con), 
                any(DoacaoBean.class), 
                eq(10)
            ));
            
            assertTrue(outContent.toString().contains("Doação cadastrada com sucesso"), "Deve indicar sucesso");
        }
    }
    
    @Test
    public void testAdicionarItemValidacao() {
        // --- Arrange ---
        String input = "50\nFeijao\n2\nkg\nA\n2025-10-10\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        try (MockedStatic<DoacaoModel> doacaoModelMock = mockStatic(DoacaoModel.class)) {
            doacaoModelMock.when(() -> DoacaoModel.existeDoacao(con, 50)).thenReturn(true);

            DoacaoController controller = new DoacaoController();
            
            // --- Act ---
            controller.adicionarItem(con);

            // --- Assert ---
            doacaoModelMock.verify(() -> DoacaoModel.adicionarItem(eq(con), any(DoacaoBean.class)));
            
            assertTrue(outContent.toString().contains("Item adicionado com sucesso"), "Deve indicar sucesso");
        }
    }

    @Test
    public void testeCadastrarDoacaoErroSimulado() {
        // --- ARRANGE ---
        String input = "10\nDoação Falha\n2025-12-31\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        try (MockedStatic<UsuarioModel> userMock = mockStatic(UsuarioModel.class);
            MockedStatic<DoacaoModel> doacaoMock = mockStatic(DoacaoModel.class)) {

            userMock.when(() -> UsuarioModel.ehDoador(con, 10)).thenReturn(true);

            // Simula erro grave
            doacaoMock.when(() -> DoacaoModel.cadastrarDoacao(eq(con), any(DoacaoBean.class), eq(10)))
                    .thenThrow(new RuntimeException("Erro Fatal: Falha ao inserir registro no banco"));

            DoacaoController controller = new DoacaoController();

            // --- ACT ---
            controller.cadastrarDoacao(con);

            // --- ASSERT ---
            String saidaConsole = outContent.toString();

            assertTrue(saidaConsole.contains("Erro ao criar a doação"), 
                "Deveria exibir mensagem de erro tratada.");
            
            assertTrue(saidaConsole.contains("Erro Fatal: Falha ao inserir registro"),
                "Deveria exibir o detalhe da exceção.");
        }
    }

    @Test
    public void testeAdicionarItemErroSimulado() {
        // --- ARRANGE ---
        String input = "50\nArroz\n10\nkg\nA\n2025-01-01\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        try (MockedStatic<DoacaoModel> doacaoMock = mockStatic(DoacaoModel.class)) {
            
            doacaoMock.when(() -> DoacaoModel.existeDoacao(con, 50)).thenReturn(true);

            // Simula erro grave
            doacaoMock.when(() -> DoacaoModel.adicionarItem(eq(con), any(DoacaoBean.class)))
                    .thenThrow(new RuntimeException("Rollback: Não foi possível salvar o item"));

            DoacaoController controller = new DoacaoController();

            // --- ACT ---
            controller.adicionarItem(con);

            // --- ASSERT ---
            String saidaConsole = outContent.toString();

            assertTrue(saidaConsole.contains("Erro ao adicionar item"), 
                "O controller deveria ter capturado a falha na adição do item.");
                
            assertTrue(saidaConsole.contains("Rollback: Não foi possível salvar o item"), 
                "O controller deveria mostrar a mensagem de erro original.");
        }
    }
}