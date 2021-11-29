import java.sql.PreparedStatement;

public class PilhaDeDescarte extends Comunicado{
    private Carta[] pilhaDeDescarte = new Carta[52];
    private int qtd = 0;

    public synchronized Carta getCarta(int pos) throws Exception
    {
        if(this.pilhaDeDescarte[pos] == null)
            throw new Exception("Posição inválida");


        Carta carta = this.pilhaDeDescarte[pos];

        for(int i = pos; i < this.qtd; i++)
        {
            this.pilhaDeDescarte[i] = this.pilhaDeDescarte[i+1];
        }

        this.qtd--;
        return carta;
    }

    public synchronized void adicionarNaPilha(Carta carta)
    {
        this.pilhaDeDescarte[qtd] = carta;
        this.qtd++;
    }

    public synchronized String toString()
    {
        String toString = "";

        for(int i = 0; i < qtd; i++)
        {
            toString += pilhaDeDescarte[i].toString() + " ";
        }

        return toString;
    }

    public synchronized int getQtdCartas()
    {
        return this.qtd;
    }

    public synchronized void zerar(){
        for (int i = 0;i<52;i++){
            pilhaDeDescarte[i] = null;
        }
        qtd = 0;
    }

}
