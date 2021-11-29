import java.net.*;
import java.io.*;

public class Cliente
{
    public static final String HOST_PADRAO  = "localhost";
    public static final int    PORTA_PADRAO = 3000;

    public static void main (String[] args)
    {
        if (args.length>2)
        {
            System.err.println ("Uso esperado: java Cliente [HOST [PORTA]]\n");
            return;
        }

        Socket conexao=null;
        try
        {
            String host = Cliente.HOST_PADRAO;
            int    porta= Cliente.PORTA_PADRAO;

            if (args.length>0)
                host = args[0];

            if (args.length==2)
                porta = Integer.parseInt(args[1]);

            conexao = new Socket (host, porta);
        }
        catch (Exception erro)
        {
            System.err.println ("Indique o servidor e a porta corretos!\n");
            return;
        }

        ObjectOutputStream transmissor=null;
        try
        {
            transmissor =
                    new ObjectOutputStream(
                            conexao.getOutputStream());
        }
        catch (Exception erro)
        {
            System.err.println ("Indique o servidor e a porta corretos!\n");
            return;
        }

        ObjectInputStream receptor=null;
        try
        {
            receptor =
                    new ObjectInputStream(
                            conexao.getInputStream());
        }
        catch (Exception erro)
        {
            System.err.println ("Indique o servidor e a porta corretos!\n");
            return;
        }

        Parceiro servidor=null;
        try
        {
            servidor =
                    new Parceiro (conexao, receptor, transmissor);
        }
        catch (Exception erro)
        {
            System.err.println ("Indique o servidor e a porta corretos!\n");
            return;
        }

        TratadoraDeComunicadoDeDesligamento tratadoraDeComunicadoDeDesligamento = null;
        try
        {
            tratadoraDeComunicadoDeDesligamento = new TratadoraDeComunicadoDeDesligamento (servidor);
        }
        catch (Exception erro)
        {} // sei que servidor foi instanciado

        tratadoraDeComunicadoDeDesligamento.start();


        PilhaDeDescarte pilhaDeDescarte = new PilhaDeDescarte();




        char jogarDeNovo = 'S';
        int soma = 0;
        int opcao = 0;
        Teclado teclado = new Teclado();
        String opcoesComprarDaPilha = "";
        Carta cartaComprada = null;
        Mao mao = null;
        Comunicado comunicado = null;
        boolean reiniciar = false;

        try
        {
            servidor.receba(new PedidoDeInicio());
        }
        catch (Exception e)
        {}

        System.out.println("Esperando os jogadores se conectarerm...");
        comunicado = null;
        do {
            try {
                comunicado = (Comunicado) servidor.espie();
            }
            catch (Exception e)
            {}
        } while (!(comunicado instanceof ComunicadoDeInicio));

        try {
            comunicado = servidor.envie();
        } catch (Exception e)
        {}


        do
        {
            try {
                if(pilhaDeDescarte.getQtdCartas() == 0)
                    System.out.println("=============== JOGO 21 ===============");

                System.out.print("\nEstas sao suas cartas: ");

                if (pilhaDeDescarte.getQtdCartas() == 0) {
                    servidor.receba(new PedidoDeMao());

                    comunicado = null;
                    do {
                        comunicado = (Comunicado) servidor.espie();
                    } while (!(comunicado instanceof Mao));

                    mao = (Mao) servidor.envie();
                }

                System.out.println(mao.toString());
                soma = mao.getValorTotal();

                System.out.println("\nSua soma e : " + soma);

                System.out.println("ESPERE SUA VEZ...");

                boolean eSuaVez = false;
                PedidoDeVez pedidoDeVez = new PedidoDeVez();
                while(!eSuaVez){
                    servidor.receba(pedidoDeVez);

                    comunicado = null;
                    do {
                        comunicado = (Comunicado) servidor.espie();
                        if (comunicado instanceof ComunicadoDeReiniciar) {
                            reiniciar = true;
                            break;
                        }
                    } while (!(comunicado instanceof ComunicadoDeVez));
                    comunicado = servidor.envie();

                    eSuaVez = true;
                    System.out.println("AGORA É SUA VEZ:");
                }

                if (reiniciar == true)
                {
                    System.out.println ("\nUm jogador bateu 21!!");
                    for(;;) {
                        System.out.print("\n Voce deseja jogar novamente? [S/N]");

                        try {
                            jogarDeNovo = Character.toUpperCase(Teclado.getUmChar());
                        } catch (Exception erro) {
                            System.err.println("Opcao invalida!\n");
                            continue;
                        }

                        if ("SN".indexOf(jogarDeNovo) == -1) {
                            System.err.println("Opcao invalida!\n");
                            continue;
                        }
                        break;
                    }
                    if (jogarDeNovo =='N') {
                        servidor.receba(new ComunicadoDeDesligamento());
                        System.exit(0);
                    }
                    else {
                        pilhaDeDescarte.zerar();
                        reiniciar = false;
                        continue;
                    }
                }

                servidor.receba(new PedidoPilhaDeDescarte());
                comunicado = null;
                do {
                    comunicado = (Comunicado) servidor.espie();
                } while (!(comunicado instanceof PilhaDeDescarte));
                pilhaDeDescarte = (PilhaDeDescarte) servidor.envie();
                for(;;){

                    if (pilhaDeDescarte.getQtdCartas() == 0) {
                        try {
                            System.out.print("\n\nDigite 1 para comprar do monte: ");
                            opcao = teclado.getUmInt();
                            if (opcao != 1) {
                                System.out.println("Opcao invalida");
                                continue;
                            }
                        } catch (Exception erro) {
                            System.out.println("Opcao invalida");
                            continue;
                        }
                        break;
                    } else {
                        try {

                            System.out.println("\n Pilha de descartes: " + pilhaDeDescarte.toString());
                            System.out.print("\n\nDigite 1 para comprar do monte ou 2 para comprar da pilha de descartes: ");
                            opcao = teclado.getUmInt();
                            if (opcao != 1 && opcao != 2) {
                                System.out.println("Opcao invalida");
                                continue;
                            }
                        } catch (Exception erro) {
                            System.out.println("Opcao invalida");
                            continue;
                        }
                        break;
                    }

                }

                if (opcao == 1) {
                    servidor.receba(new PedidoDeCompra());
                    comunicado = null;
                    do {
                        comunicado = (Comunicado) servidor.espie();
                    } while (!(comunicado instanceof Carta));
                    cartaComprada = (Carta) servidor.envie();
                } else
                {


                    for(int i = 1; i <= pilhaDeDescarte.getQtdCartas(); i++)
                    {
                        opcoesComprarDaPilha += " " + i + " ";
                    }
                    char opcaoASerComprada = ' ';
                    boolean ehValida = false;


                    do {
                        System.out.println(pilhaDeDescarte.toString());
                        System.out.println("Qual carta da pilha de descartes voce deseja comprar? [" + opcoesComprarDaPilha + "]: ");
                        try
                        {
                            opcaoASerComprada = teclado.getUmChar();
                            if(opcoesComprarDaPilha.indexOf(opcaoASerComprada) == -1)
                                ehValida = false;
                            else
                                ehValida = true;
                        }catch (Exception erro)
                        {
                            System.out.println("Opcao invalida");
                        }

                    } while(ehValida != true);

                    int pos = Character.getNumericValue(opcaoASerComprada)-1;
                    servidor.receba(new PedidoDaPilhaDeDescarte(pos));
                    comunicado = null;
                    do {
                        comunicado = (Comunicado) servidor.espie();
                    }
                    while (!(comunicado instanceof Carta));
                    cartaComprada = (Carta) servidor.envie();
                }
                System.out.println(cartaComprada.toString());
                mao.comprarOuPegarDoMonte(cartaComprada);
                System.out.print("\nAgora, estas sao suas cartas: " + mao.toString() + "\n");

                soma = mao.getValorTotal();
                System.out.println("\nSua soma e: " + soma + "\n");

                System.out.print("\nQual carta voce deseja descartar[1, 2, 3, 4]? ");
                int opcaoCartaASerDescartada = 0;
                boolean ehValida = false;
                do {
                    try {
                        opcaoCartaASerDescartada = teclado.getUmInt();
                        if (opcaoCartaASerDescartada <1 || opcaoCartaASerDescartada > 4)
                            ehValida = false;
                        else
                            ehValida = true;
                    } catch (Exception erro) {
                        System.out.print("Opcao invalida");
                    }
                } while (!ehValida);

                switch (opcaoCartaASerDescartada) {
                    case 1: {
                        Carta carta1 = mao.descartar(0);
                        pilhaDeDescarte.adicionarNaPilha(carta1);
                        servidor.receba(carta1);
                    }
                    break;
                    case 2:
                        Carta carta2 = mao.descartar(1);
                        pilhaDeDescarte.adicionarNaPilha(carta2);
                        servidor.receba(carta2);
                        break;
                    case 3:
                        Carta carta3 = mao.descartar(2);
                        pilhaDeDescarte.adicionarNaPilha(carta3);
                        servidor.receba(carta3);
                        break;
                    case 4:
                        Carta carta4 = mao.descartar(3);
                        pilhaDeDescarte.adicionarNaPilha(carta4);
                        servidor.receba(carta4);
                        break;
                }
                System.out.print("Agora, estas sao suas cartas: " + mao.toString() + "\n");
                soma = mao.getValorTotal();
                System.out.println("Sua soma agora e: " + soma);

                if (soma == 21) {
                    ComunicadoDeVitoria comunicadoDeVitoria = new ComunicadoDeVitoria();
                    try {
                        servidor.receba(comunicadoDeVitoria);
                        System.out.println("Você ganhou!!" );
                    } catch (Exception erro) {
                        System.err.println("Erro de transmissao");
                    }
                }
                else{
                    servidor.receba(new ComunicadoDeFimDeVez());
                    continue;
                }

                for(;;) {
                    System.out.print("\n Voce deseja jogar novamente? [S/N]");

                    try {
                        jogarDeNovo = Character.toUpperCase(Teclado.getUmChar());
                    } catch (Exception erro) {
                        System.err.println("Opcao invalida!\n");
                        continue;
                    }

                    if ("SN".indexOf(jogarDeNovo) == -1) {
                        System.err.println("Opcao invalida!\n");
                        continue;
                    }
                    break;
                }

                if(jogarDeNovo == 'S'){
                    servidor.receba(new ComunicadoDeReiniciar());

                    pilhaDeDescarte.zerar();
                }

            }catch (Exception erro)
            {
                erro.printStackTrace();
                System.err.println("Erro de comunicacao com o servidor;");
                System.err.println("Tente novamente!");
                System.err.println("Caso o erro persista, termine o programa");
                System.err.println("e volte a tentar mais tarde!\n");
            }
        }
        while (jogarDeNovo == 'S');

        try
        {
            servidor.receba (new PedidoParaSair ());
        }
        catch (Exception erro)
        {}

        System.out.println ("Obrigado por usar este programa!");
        System.exit(0);
    }


}

