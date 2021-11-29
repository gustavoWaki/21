public class Mao extends Comunicado
{
    private Carta[] mao = new Carta[4];
    private int soma = 0;
    private int qtd = 0;

    public Mao(Carta carta1, Carta carta2, Carta carta3)
    {
        this.mao[0] = carta1;
        this.mao[1] = carta2;
        this.mao[2] = carta3;
        this.qtd = 3;

        this.soma = carta1.getValor() + carta2.getValor() + carta3.getValor();
    }

    public String toString()
    {
        String toString = "";
        if(qtd > 3){
            for(int i = 0; i < 4; i++){
                toString = toString + this.mao[i].toString() + " ";
            }
        }
        else{
            for(int i = 0; i  < 3; i++){
                toString = toString + this.mao[i].toString() + " ";
            }
        }
        return toString;
    }

    public String getTexto (Carta carta)
    {
        return carta.toString();
    }

    public Carta comprarOuPegarDoMonte(Carta carta)
    {
        this.mao[3] = carta;
        this.qtd = 4;
        return carta;
    }

    public Carta descartar(int pos)
    {
        Carta retorno = mao[pos];
        if(pos == 3)
            this.mao[pos] = null;
        else
        {
            for (int i = pos; i < qtd-1; i++){
                this.mao[i] = this.mao[i + 1];
            }
            this.mao[3] = null;
        }
        qtd = 3;
        return retorno;
    }

    public int getValorTotal()
    {
        soma = 0;
        for(Carta carta:mao)
        {
            if (carta == null)
            {
                soma += 0;
            }
            else
                soma += carta.getValor();
        }
        return soma;
    }
}
