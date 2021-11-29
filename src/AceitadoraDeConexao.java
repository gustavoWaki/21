import java.lang.management.CompilationMXBean;
import java.net.*;
import java.util.*;

public class AceitadoraDeConexao extends Thread
{
    private ServerSocket        pedido;
    private ArrayList<Parceiro> usuarios;
    private Baralho             baralho;
    private PilhaDeDescarte     pilhaDeDescarte;

    public AceitadoraDeConexao
            (String porta, ArrayList<Parceiro> usuarios, Baralho baralho, PilhaDeDescarte pilhaDeDescarte)
            throws Exception
    {
        if (porta==null)
            throw new Exception ("Porta ausente");

        try
        {
            this.pedido =
                    new ServerSocket (Integer.parseInt(porta));
        }
        catch (Exception  erro)
        {
            throw new Exception ("Porta invalida");
        }

        if (usuarios==null)
            throw new Exception ("Usuarios ausentes");

        this.usuarios = usuarios;
        this.baralho = baralho;
        this.pilhaDeDescarte = pilhaDeDescarte;
    }

    public void run ()
    {
        for(;;)
        {
            Socket conexao=null;
            try
            {
                conexao = this.pedido.accept();
            }
            catch (Exception erro)
            {
                continue;
            }

            SupervisoraDeConexao supervisoraDeConexao=null;
            try
            {
                supervisoraDeConexao =
                        new SupervisoraDeConexao (conexao, usuarios, baralho, pilhaDeDescarte);
            }
            catch (Exception erro)
            {} // sei que passei parametros corretos para o construtor
            supervisoraDeConexao.start();
        }
    }
}
