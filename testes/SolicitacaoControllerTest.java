package testes;

import controller.SolicitacaoController;
import model.DoacaoModel;
import model.SolicitacaoModel;
import model.UsuarioModel;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.ByteArrayInputStream;
import java.sql.Connection;

import static org.mockito.Mockito.*;

public class SolicitacaoControllerTest {

    @Test
    public void testCadastrarSolicitacaoFluxoCompleto() {
        // --- Arrange ---
        // ID Doacao (100) -> ID Receptor (20)
        String input = "100\n20\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Connection mockCon = mock(Connection.class);

        // Mockamos 3 models pois o controller depende de todos eles
        try (MockedStatic<DoacaoModel> doacaoMock = mockStatic(DoacaoModel.class);
             MockedStatic<UsuarioModel> usuarioMock = mockStatic(UsuarioModel.class);
             MockedStatic<SolicitacaoModel> solicitacaoMock = mockStatic(SolicitacaoModel.class)) {

            // Comportamentos esperados dos mocks
            doacaoMock.when(() -> DoacaoModel.existeDoacao(mockCon, 100)).thenReturn(true);
            usuarioMock.when(() -> UsuarioModel.ehReceptor(mockCon, 20)).thenReturn(true);

            SolicitacaoController controller = new SolicitacaoController();

            // --- Act ---
            controller.cadastrarSolicitacao(mockCon);

            // --- Assert ---
            // Verifica se a solicitação foi efetivamente criada no final
            solicitacaoMock.verify(() -> SolicitacaoModel.criarSolicitacao(mockCon, 100, 20));
        }
    }

    @Test
    public void testAtualizarStatusSolicitacao() {
        // ID Solicitaçao (5) -> Novo Status (Aprovada)
        String input = "5\nAprovada\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Connection mockCon = mock(Connection.class);

        try (MockedStatic<SolicitacaoModel> solicitacaoMock = mockStatic(SolicitacaoModel.class)) {
            // Simula que a solicitação 5 existe
            solicitacaoMock.when(() -> SolicitacaoModel.existeSolicitacao(mockCon, 5)).thenReturn(true);

            SolicitacaoController controller = new SolicitacaoController();
            controller.atualizar(mockCon);

            // Verifica se chamou o update
            solicitacaoMock.verify(() -> SolicitacaoModel.updateStatus(mockCon, 5, "Aprovada"));
        }
    }
}