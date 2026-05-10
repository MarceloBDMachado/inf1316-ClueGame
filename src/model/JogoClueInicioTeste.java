package model;

import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class JogoClueInicioTeste {

    private JogoClueFacade jogo;

    @Before
    public void setUp() {
        jogo = new JogoClueFacade();
    }

    @Test
    public void testRolarDados() {
        for (int i = 0; i < 100; i++) {
            int[] resultado = jogo.rolarDados();

            assertTrue("ERRO: dado 1 menor que 1", resultado[0] >= 1);
            assertTrue("ERRO: dado 1 maior que 6", resultado[0] <= 6);

            assertTrue("ERRO: dado 2 menor que 1", resultado[1] >= 1);
            assertTrue("ERRO: dado 2 maior que 6", resultado[1] <= 6);
        }
    }

    @Test
    public void testPreparacaoE_DistribuicaoDeCartas() {
        int numJogadores = 4;
        jogo.prepararPartida(numJogadores);

        assertNotNull("ERRO: envelope vazio", jogo.getEnvelopeConfidencial());

        int totalCartasDistribuidas = 0;
        for (int i = 1; i <= numJogadores; i++) {
            List<Carta> mao = jogo.getMaosJogadores().get(i);
            assertNotNull("ERRO: mão vazia", mao);
            assertTrue("ERRO: mão menor que esperado", mao.size() >= 4);
            totalCartasDistribuidas += mao.size();
        }

        assertEquals("ERRO: todas as cartas não foram distribuídas", 18, totalCartasDistribuidas);
    }

    @Test
    public void testMapeamentoDeMovimento() {
        List<Casa> casasAlcancaveis = jogo.mapearCasasPossiveis("Coronel Mostarda", 2);

        assertNotNull("ERRO: lista de casas nulas", casasAlcancaveis);
        assertFalse("ERRO: nao tem casa alcasáveis", casasAlcancaveis.isEmpty());
    }

    @Test
    public void testDeslocamentoDoPiao() {
        Piao coronel = jogo.getPiao("Coronel Mostarda");
        assertNotNull(coronel);
        assertEquals(0, coronel.getPosicaoAtual().getX());
        assertEquals(16, coronel.getPosicaoAtual().getY());

        boolean moveu = jogo.deslocarPiao("Coronel Mostarda", 0, 17);

        assertTrue("ERRO: movimento para casa não valida", moveu);
        assertEquals("ERRO: posição X não foi atualizada", 0, coronel.getPosicaoAtual().getX());
        assertEquals("ERRO: posição Y não foi atualizada", 17, coronel.getPosicaoAtual().getY());
    }
}