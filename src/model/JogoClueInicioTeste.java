package model;

import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class JogoClueInicioTeste {

    private JogoClueInicio jogo;

    @Before
    public void setUp() {
        jogo = new JogoClueInicio();
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
    public void testeDistrCartas() {
        for (int numJogadores = 3; numJogadores <= 6; numJogadores++) {

            JogoClueInicio jogoTeste = new JogoClueInicio();
            jogoTeste.prepararPartida(numJogadores);

            assertNotNull("ERRO: envelope vazio para " + numJogadores + " jogadores",
                    jogoTeste.getEnvelopeConfidencial());

            int totalCartasDistribuidas = 0;
            int minCartas = 18 / numJogadores;

            for (int i = 1; i <= numJogadores; i++) {
                List<Carta> mao = jogoTeste.getMaosJogadores().get(i);

                assertNotNull("ERRO: mão vazia para o jogador " + i, mao);
                assertTrue("ERRO: jogador " + i + " recebeu menos cartas que o esperado", mao.size() >= minCartas);

                totalCartasDistribuidas += mao.size();
            }

            assertEquals("ERRO: total de cartas na mesa incorreto para " + numJogadores + " jogadores",
                    18, totalCartasDistribuidas);
        }
    }

    @Test
    public void testeMovimento() {
        List<Casa> casasAlcancaveis = jogo.mapearCasasPossiveis("Coronel Mostarda", 2);

        assertNotNull("ERRO: lista de casas nulas", casasAlcancaveis);
        assertFalse("ERRO: nao tem casa alcasáveis", casasAlcancaveis.isEmpty());
    }

    @Test
    public void testeMovPiao() {
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