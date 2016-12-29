
package controladores;

import java.util.ArrayList;
import menus.MenuVisitante;
import menus.OpcaoMenu;
import remotebeans.ClientAdminRemote;
import remotebeans.ClientUtilizadorRemote;
import remotebeans.ClientVisitanteRemote;
import static pdtprcse.PDTPrcse.controlador;
import static pdtprcse.PDTPrcse.menu;
import pdtprcse.ReferenciaVisitante;

public class ControladorAdministrador extends ControladorUserAdmin{

    private ClientAdminRemote ligacao;
    public ControladorAdministrador(ClientAdminRemote ligacao) {
        super(ligacao);
        this.ligacao=ligacao;
    }
    @Override
    public void logOff() {
        if (ligacao.logOff()) {
            System.out.println("\nlog off");
            ReferenciaVisitante refVisitante = new ReferenciaVisitante();
            ClientVisitanteRemote ligVisitante = refVisitante.getLigacao();
            controlador = new ControladorVisitante(ligVisitante);
            menu = new MenuVisitante(ligVisitante, (ControladorVisitante) controlador);

        } else {
            System.out.println("ERRO: accao nao aceite");
        }
    }
    public OpcaoMenu consultarDenuncias() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void consultarAdesoes() {
        ArrayList<String> pedidos = ligacao.getUtilizadoresPedidos();
        System.out.print("Pedidos de ativacao de Utilizador: ");
        for (String pedido : pedidos){
            System.out.print(pedido.concat(" "));
        }
        System.out.print("\n");
    }

    public OpcaoMenu cancelarItens() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public OpcaoMenu suspenderContas() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public OpcaoMenu reativarContas() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public OpcaoMenu mudarPassword() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public OpcaoMenu enviarMensagens() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public OpcaoMenu consultarUtilizador() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public OpcaoMenu consultarItem() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public OpcaoMenu gerirCategorias() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void ativarConta() {
        System.out.print("Ativar username -> ");
        String username = sc.next();
        sc.skip("\n");
        if (ligacao.ativaUtilizador(username)){
            System.out.println("Utilizador ativado");
        }else{
            System.out.println("ERRO: Utilizador nao ativado");
        }
    }


    
}
