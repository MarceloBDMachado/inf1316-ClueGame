package model;

import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class JogoClueFacadeTest {

    private JogoClueFacade jogo;

    @Before
    public void setUp() {
        jogo = new JogoClueFacade();
    }

    @Test
    public void testRolarDados() {
        for (int i = 0; i < 100; i++) {
            int[] resultado = jogo.rolarDados();

            assertTrue("O dado 1 deve ser maior ou igual a 1", resultado[0] >= 1);
            assertTrue("O dado 1 deve ser menor ou igual a 6", resultado[0] <= 6);

            assertTrue("O dado 2 deve ser maior ou igual a 1", resultado[1] >= 1);
            assertTrue("O dado 2 deve ser menor ou igual a 6", resultado[1] <= 6);
        }
    }

    @Test
    public void testPreparacaoE_DistribuicaoDeCartas() {
        int numJogadores = 4;
        jogo.prepararPartida(numJogadores);

        assertNotNull("O envelope confidencial deve estar instanciado", jogo.getEnvelopeConfidencial());

        int totalCartasDistribuidas = 0;
        for (int i = 1; i <= numJogadores; i++) {
            List<Carta> mao = jogo.getMaosJogadores().get(i);
            assertNotNull("A mão do jogador não deve ser nula", mao);
            assertTrue("O jogador deve ter recebido cartas", mao.size() >= 4);
            totalCartasDistribuidas += mao.size();
        }

        assertEquals("Todas as 18 cartas restantes devem ter sido distribuídas", 18, totalCartasDistribuidas);
    }

    @Test
    public void testMapeamentoDeMovimento() {
        // Modificado para usar o nome traduzido
        List<Casa> casasAlcancaveis = jogo.mapearCasasPossiveis("Coronel Mostarda", 2);

        assertNotNull("A lista de casas não deve ser nula", casasAlcancaveis);
        assertFalse("Devem haver casas alcançáveis", casasAlcancaveis.isEmpty());
    }

    @Test
    public void testDeslocamentoDoPiao() {
        // Modificado para usar o nome traduzido
        Piao coronel = jogo.getPiao("Coronel Mostarda");
        assertNotNull(coronel);
        assertEquals(0, coronel.getPosicaoAtual().getX());
        assertEquals(16, coronel.getPosicaoAtual().getY()); // Atualizei o Y pois o índice do Coronel Mostarda no array agora cai na segunda coordenada {0,16}

        // Movendo para {0, 17} que é válida a partir de {0, 16}
        boolean moveu = jogo.deslocarPiao("Coronel Mostarda", 0, 17);

        assertTrue("O deslocamento para uma casa válida deve retornar true", moveu);
        assertEquals("A posição X do pião deve ter sido atualizada", 0, coronel.getPosicaoAtual().getX());
        assertEquals("A posição Y do pião deve ter sido atualizada", 17, coronel.getPosicaoAtual().getY());
    }
}