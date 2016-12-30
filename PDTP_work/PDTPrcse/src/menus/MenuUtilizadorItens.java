
package menus;

import controladores.ControladorUtilizador;
import beans.ClientUtilizadorRemote;

public class MenuUtilizadorItens extends Menu {

    public MenuUtilizadorItens(ClientUtilizadorRemote ligacao, ControladorUtilizador controlador) {
       super();
        titulo="Utilizador - Itens";
       this.controlador=controlador;
        //opcoes.clear();
        opcoes.add(new OpcaoMenu("Colocar Item a venda", () -> controlador.colocarItemVenda()));
        opcoes.add(new OpcaoMenu("Consultar meus Itens a venda", () -> controlador.consultarItensMeus()));
         opcoes.add(new OpcaoMenu("Consultar Itens seguidos", () -> controlador.consultarItensSeguidos()));
        opcoes.add(new OpcaoMenu("Historial de Itens", () -> controlador.historialItens()));
          opcoes.add(new OpcaoMenu("Consultar Itens por...", () -> controlador.consultarItens()));
  
        
        
        
        opcoes.add(new OpcaoMenu("Voltar", () -> controlador.mostrarMenu(new MenuUtilizador(ligacao,controlador))));
    }
}