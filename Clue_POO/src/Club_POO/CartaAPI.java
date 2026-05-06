package Club_POO;

import java.util.*;

public class CartaAPI {
    private Map<String, Carta> todasAsCartas = new HashMap<>();
    private List<Carta> envelopeVitoria = new ArrayList<>();
    
    public CartaAPI() {
        inicializarCartas();
    }

    private void inicializarCartas() {
        // 1. Suspeitos
        todasAsCartas.put("Srta. Rose", new Carta("Srta. Rose", Carta.TipoCarta.SUSPEITO));
        todasAsCartas.put("Coronel Mostarda", new Carta("Coronel Mostarda", Carta.TipoCarta.SUSPEITO));
        todasAsCartas.put("Professor Plum", new Carta("Professor Plum", Carta.TipoCarta.SUSPEITO));
        todasAsCartas.put("Sr. Marinho", new Carta("Sr. Marinho", Carta.TipoCarta.SUSPEITO));
        todasAsCartas.put("Dona Violeta", new Carta("Dona Violeta", Carta.TipoCarta.SUSPEITO));
        todasAsCartas.put("Dona Branca", new Carta("Dona Branca", Carta.TipoCarta.SUSPEITO));

        // 2. Armas
        todasAsCartas.put("Corda", new Carta("Corda", Carta.TipoCarta.ARMA));
        todasAsCartas.put("Cano de Ferro", new Carta("Cano de Ferro", Carta.TipoCarta.ARMA));
        todasAsCartas.put("Faca", new Carta("Faca", Carta.TipoCarta.ARMA));
        todasAsCartas.put("Chave Inglesa", new Carta("Chave Inglesa", Carta.TipoCarta.ARMA));
        todasAsCartas.put("Castiçal", new Carta("Castiçal", Carta.TipoCarta.ARMA));
        todasAsCartas.put("Pistola", new Carta("Pistola", Carta.TipoCarta.ARMA));

        // 3. Aposentos
        todasAsCartas.put("Cozinha", new Carta("Cozinha", Carta.TipoCarta.APOSENTO));
        todasAsCartas.put("Salão de Festas", new Carta("Salão de Festas", Carta.TipoCarta.APOSENTO));
        todasAsCartas.put("Salão de Jogos", new Carta("Salão de Jogos", Carta.TipoCarta.APOSENTO));
        todasAsCartas.put("Biblioteca", new Carta("Biblioteca", Carta.TipoCarta.APOSENTO));
        todasAsCartas.put("Escritório", new Carta("Escritório", Carta.TipoCarta.APOSENTO));
        todasAsCartas.put("Sala de Estar", new Carta("Sala de Estar", Carta.TipoCarta.APOSENTO));
        todasAsCartas.put("Sala de Jantar", new Carta("Sala de Jantar", Carta.TipoCarta.APOSENTO));
        todasAsCartas.put("Terraço", new Carta("Terraço", Carta.TipoCarta.APOSENTO));
        todasAsCartas.put("Hall", new Carta("Hall", Carta.TipoCarta.APOSENTO));
    }

    
    public void prepararEnvelopes() {
    }
}