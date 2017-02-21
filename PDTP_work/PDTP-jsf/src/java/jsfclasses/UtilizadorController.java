/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsfclasses;

import beans.ClientUtilizadorRemote;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import java.util.TreeMap;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author diogo
 */
@Named("UtilizadorController")
@SessionScoped
public class UtilizadorController extends VisitanteController implements Serializable {

    @EJB
    private ClientUtilizadorRemote client;
    private ArrayList<Menu> menus;
    private String seccao;


    public UtilizadorController() {
        super();
        seccao="Utilizador";
        String subseccao = new String();
        ArrayList<String> paginas = new ArrayList<>();
        menus = new ArrayList<Menu>();
        
        Menu menuUtilizador = new Menu("menu1","");
        menuUtilizador.setTituloMenu("Utilizador");
        menuUtilizador.addMenuPage("Inicio");
        menuUtilizador.addMenuPage("Minhas Mensagens");
        menuUtilizador.addMenuPage("Enviar Mensagem");
        menus.add(menuUtilizador);
        Menu menuUtilizadorConta = new Menu("menu2",seccao);
        menuUtilizadorConta.setTituloMenu("Conta");
        menuUtilizadorConta.addMenuPage("Consultar dados");
        menuUtilizadorConta.addMenuPage("Atualizar dados");
        menuUtilizadorConta.addMenuPage("Alterar password");
        menuUtilizadorConta.addMenuPage("Pedir Suspensao");
        menus.add(menuUtilizadorConta);
        Menu menuUtilizadorSaldo = new Menu("menu3",seccao);
        menuUtilizadorSaldo.setTituloMenu("Itens");
        menuUtilizadorSaldo.addMenuPage("Colocar Item a venda");
        menuUtilizadorSaldo.addMenuPage("Meus Itens a venda");
        menuUtilizadorSaldo.addMenuPage("Itens seguidos");
        menuUtilizadorSaldo.addMenuPage("Historial");
        menuUtilizadorSaldo.addMenuPage("Lista de Itens");
        menuUtilizadorSaldo.addMenuPage("Consultar Item");
        menuUtilizadorSaldo.addMenuPage("Itens comprados");
        menuUtilizadorSaldo.addMenuPage("Itens por pagar");
        menuUtilizadorSaldo.addMenuPage("Concluir transacao");
        menus.add(menuUtilizadorSaldo);
        Menu menuUtilizadorItens = new Menu("menu4",seccao);
        menuUtilizadorItens.setTituloMenu("Saldo");
        menuUtilizadorItens.addMenuPage("Ver Saldo");
        menuUtilizadorItens.addMenuPage("Carregar Saldo");
        menus.add(menuUtilizadorItens);
    }

    public ArrayList<Menu> getMenus() {

        return menus;
    }
}