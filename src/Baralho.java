import java.lang.reflect.Array;

public class Baralho {
    //optei por fazer uma classe não genérica pois a classe Conjunto que você fez
    //tem métodos de adicionar, redimencionar etc que o baralho não precisará ter

    private Carta[] baralho = new Carta[52];
    private int qtd; //qtd de cartas no jogo

    //♣ ♠ ♦ ♥
    private String[] texto = {"A de paus", "2 de paus", "3 de paus", "4 de paus", "5 de paus", "6 de paus", "7 de paus", "8 de paus", "9 de paus", "10 de paus", "J de paus", "Q de paus", "K de paus",
            "A de espada", "2 de espada", "3 de espada", "4 de espada", "5 de espada", "6 de espada", "7 de espada", "8 de espada", "9 de espada", "10 de espada", "J de espada", "Q de espada", "K de espada",
            "A de ouros", "2 de ouros", "3 de ouros", "4 de ouros", "5 de ouros", "6 de ouros", "7 de ouros", "8 de ouros", "9 de ouros", "10 de ouros", "J de ouros", "Q de ouros", "K de ouros",
            "A de copas", "2 de copas", "3 de copas", "4 de copas", "5 de copas", "6 de copas", "7 de copas", "8 de copas", "9 de copas", "10 de copas", "J de copas", "Q de copas", "K de copas"};

    private int[] valor = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10,
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10,
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10,
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10};

    public Baralho()
    {
        for (byte i = 0; i < 52; i++){
            baralho[i] = new Carta(texto[i], valor[i]);
        }
        qtd = 52;
    }

    public synchronized void start(){
        for (byte i = 0; i < 52; i++){
            baralho[i] = new Carta(texto[i], valor[i]);
        }
        qtd = 52;
    }

    private void removerDoBaralho(int pos){
        for(int i = pos; i < qtd-1; i++){
            baralho[i] = baralho[i+1];
        }
        this.qtd--;
    }

    public synchronized Carta comprar(){
        int pos = (int) Math.round(Math.random() * (qtd-1));

        Carta carta = baralho[pos];
        removerDoBaralho(pos);
        return carta;
    }
}

