public class Carta extends Comunicado
{
    private String texto;
    private int valor;

    public Carta(String texto, int valor){
        this.texto = texto;
        this.valor = valor;
    }


    public int getValor(){
        return valor;
    }

    public String toString(){
        return "|"+texto+"|";
    }

    public boolean equals(Object obj){
        if (this == obj) return true;

        if (obj == null) return false;

        if (this.getClass()!=obj.getClass()) return false;

        Carta x = (Carta)obj;

        if(this.texto == x.texto) return true;

        return false;
    }

    public int HashCode(){
        int random = 371;

        random = 7 * random + new Integer ((int)this.texto.charAt(1)).hashCode();
        random = 11 * random + new Integer ((int)this.texto.charAt(0)).hashCode();
        random = 13 * random + new Integer (this.valor).hashCode();

        if (random < 0) random = -random;

        return random;
    }


    public int compareTo(Carta carta){
        return this.texto.compareTo(carta.texto);
    }

    public Carta(Carta carta) throws Exception{
        if(carta == null)
            throw new Exception("Classe inválida");

        this.texto = carta.toString();
        this.valor = carta.getValor();
    }


    public Object clone ()
    {
        Carta ret = null;

        try
        {
            ret = new Carta(this);
        }
        catch (Exception e)
        {}

        return ret;
    }

}

