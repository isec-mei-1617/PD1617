/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsfclasses;

import gui.Menu;
import autenticacao.Util;
import beans.ClientVisitanteRemote;
import beans.ClientWebSession;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import jpaentidades.TUtilizadores;
import jsfclasses.util.JsfUtil;

/**
 *
 * @author diogo
 */
@Named("VisitanteController")
@SessionScoped
public class VisitanteController /*extends TUtilizadoresController*/ implements Serializable { // extends TUtilizadoresController

    @EJB
    private ClientVisitanteRemote client;
    
    @EJB
    ClientWebSession webSession;

    private UIComponent loginButton;

    private boolean usernameCheck = true;

    protected TUtilizadores current;

    protected String seccao;
    protected ArrayList<Menu> menus;

    public VisitanteController() {
        super();
        this.seccao = "Visitante";
        menus = new ArrayList<>();
        Menu menuVisitante = new Menu("menu1", "");
        menuVisitante.setTituloMenu("Visitante");
        menuVisitante.addMenuPage("Inicio");
        menuVisitante.addMenuPage("Registo");
        menuVisitante.addMenuPage("Vendas Recentes");
        menuVisitante.addMenuPage("Reativar Conta");
        menuVisitante.addMenuPage("Newsletter");
        menus.add(menuVisitante);
    }

    @PostConstruct
    public void init() {
        //session = null;
        HttpSession session = Util.getSession();
        session.setAttribute("sessaoUser", client);
        webSession.setUserName("");
        webSession.setObjSessao(client);
    }

    public ArrayList<Menu> getMenus() {
        return menus;
    }

    public TUtilizadores getCurrent() {
        return current;
    }

    public String login() {

        boolean ok = client.loginUtilizador(current.getUsername(), current.getPassword());
        if (ok) {
            //HttpSession session = SessionUtils.getSession();
            HttpSession session = Util.getSession();
            session.setAttribute("username", current.getUsername());
            webSession.setUserName(current.getUsername());
            if (client.isAdmin(current.getUsername())) {
                return "/Administrador/Inicio";
            }
            return "/Utilizador/Inicio";

        } else {
            FacesContext ctx = FacesContext.getCurrentInstance();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login invalido", "Login invalido");
            ctx.addMessage(loginButton.getClientId(ctx), msg);
            return "/Visitante/Inicio";
        }
    }

    public UIComponent getLoginButton() {
        return loginButton;
    }

    public void setLoginButton(UIComponent loginButton) {
        this.loginButton = loginButton;
    }

    public String getUsernameCheck() {
        if (usernameCheck == true) {
            return null;
        } else {
            return "ERRO: j� existe um username igual";
        }
    }

    public void checkUsername() {
        // usernameCheck =false;
        //System.out.println("-------" + current.getUsername());
        if (client.existeUsername(getCurrent().getUsername())) {
            usernameCheck = false;
        } else {
            usernameCheck = true;
        }
    }

    public TUtilizadores getSelected() {
        if (current == null) {
            current = new TUtilizadores();
        }
        return current;
    }

    public String create() {
        if (usernameCheck == true) {
            if (client.inscreveUtilizador(current.getNome(), current.getMorada(), current.getUsername(), current.getPassword())) {
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("TUtilizadoresCreated"));
                return "/Inicio.xhtml";
            }
        }
        JsfUtil.addErrorMessage("Erro de Registo");
        return null;
    }
    
    public String reativaConta(){
            if (client.pedirReativacaoUsername(current.getUsername(),current.getPassword())) {
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("TUtilizadoresRecurado"));
                return "/Inicio.xhtml";
            }
        JsfUtil.addErrorMessage("Erro de Registo");
        return null;        
    }
}
