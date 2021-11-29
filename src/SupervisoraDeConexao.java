import java.io.*;
import java.net.*;
import java.util.*;

public class SupervisoraDeConexao extends Thread
{
    private double              valor=0;
    private Parceiro            usuario;
    private Socket              conexao;
    private ArrayList<Parceiro> usuarios;
    private Baralho             baralho;
    private static int qtdJogadores	=0;
    private PilhaDeDescarte pilhaDeDescarte;
    private Parceiro parceiro = usuario;

    public SupervisoraDeConexao
            (Socket conexao, ArrayList<Parceiro> usuarios, Baralho baralho, PilhaDeDescarte pilhaDeDescarte)
            throws Exception
    {
        if (conexao==null)
            throw new Exception ("Conexao ausente");

        if (usuarios==null)
            throw new Exception ("Usuarios ausentes");

        if (baralho ==null)
            throw new Exception("Baralho ausente");

        this.conexao  = conexao;
        this.usuarios = usuarios;
        this.baralho = baralho;
        this.pilhaDeDescarte = pilhaDeDescarte;
    }


    public void run ()
    {
        ObjectOutputStream transmissor;
        try
        {
            transmissor =
                    new ObjectOutputStream(
                            this.conexao.getOutputStream());
        }
        catch (Exception erro)
        {
            return;
        }

        ObjectInputStream receptor=null;
        try
        {
            receptor=
                    new ObjectInputStream(
                            this.conexao.getInputStream());
        }
        catch (Exception err0)
        {
            try
            {
                transmissor.close();
            }
            catch (Exception falha)
            {} // so tentando fechar antes de acabar a thread

            return;
        }

        try
        {
            this.usuario =
                    new Parceiro (this.conexao,
                            receptor,
                            transmissor);
        }
        catch (Exception erro)
        {} // sei que passei os parametros corretos



        try
        {
            synchronized (this.usuarios)
            {
                this.usuarios.add (this.usuario);


                this.qtdJogadores++;
                if(this.qtdJogadores == 3)
                    for(Parceiro usuario: this.usuarios)
                    {
                        usuario.receba(new ComunicadoDeInicio());
                    }
                else if(this.qtdJogadores > 3)
                {
                    this.usuario.receba(new ComunicadoDeDesligamento());
                }
            }
            for(;;)
            {
                Comunicado comunicado = this.usuario.envie();

                if (comunicado==null)
                    return;
                System.out.println("comunicado != null");
                if (comunicado instanceof PedidoDeCompra)
                {
                    System.out.println("comunicado == pedido de compra");
                    this.usuario.compra (baralho.comprar());
                }
                else if (comunicado instanceof Carta)
                {
                    System.out.println("comunicado == descarte");
                    this.pilhaDeDescarte.adicionarNaPilha((Carta)comunicado);
                }
                else if (comunicado instanceof PedidoDeMao)
                {
                    System.out.println("comunicado == pedidoDeMao");
                    Mao mao = new Mao(baralho.comprar(), baralho.comprar(), baralho.comprar());
                    this.usuario.receba(mao);
                    System.out.println("mandou a mao");

                }
                else if (comunicado instanceof PedidoPilhaDeDescarte)
                {
                    System.out.println("comunicado == pedidoPilhaDeDescarte");
                    this.usuario.receba(this.pilhaDeDescarte);
                }
                else if(comunicado instanceof PedidoDaPilhaDeDescarte)
                {
                    this.usuario.receba(this.pilhaDeDescarte.getCarta(((PedidoDaPilhaDeDescarte) comunicado).getPos()));
                }
                else if(comunicado instanceof ComunicadoDeReiniciar)
                {
                    this.pilhaDeDescarte.zerar();
                    this.baralho.start();
                    this.usuarios.get(1).receba(new ComunicadoDeReiniciar());
                    this.usuarios.get(2).receba(new ComunicadoDeReiniciar());
                }
                else if(comunicado instanceof PedidoDeVez)
                {
                    System.out.println("comunicado == pedidoDeVez");
                    if(this.usuario == usuarios.get(0))
                    {
                        this.usuario.receba(new ComunicadoDeVez());
                        System.out.println("mandou");
                    }
                }
                else if(comunicado instanceof ComunicadoDeFimDeVez)
                {
                        parceiro = usuarios.get(0);
                        usuarios.set(0, usuarios.get(1));
                        usuarios.set(1, usuarios.get(2));
                        usuarios.set(2, parceiro);

                    this.usuarios.get(0).receba(new ComunicadoDeVez());
                }
                else if (comunicado instanceof PedidoParaSair)
                {
                    synchronized (this.usuarios)
                    {
                        this.usuarios.remove (this.usuario);
                    }
                    this.usuario.adeus();
                }
                else if (comunicado instanceof ComunicadoDeDesligamento)
                {
                    synchronized (this.usuarios)
                    {
                        this.usuarios.remove (this.usuario);
                        this.usuarios.get(1).receba(new ComunicadoDeDesligamento());
                        this.usuarios.get(2).receba(new ComunicadoDeDesligamento());
                    }
                    this.usuario.adeus();
                }
            }
        }
        catch (Exception erro)
        {
            try
            {
                transmissor.close ();
                receptor   .close ();
            }
            catch (Exception falha)
            {} // so tentando fechar antes de acabar a thread

            return;
        }
    }
}

