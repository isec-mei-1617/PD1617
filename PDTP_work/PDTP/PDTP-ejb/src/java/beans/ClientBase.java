/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author eugenio
 */
public abstract class ClientBase implements ClientRemote {

    @EJB
    LeiloeiraLocal leiloeira;

    @Override
    public ArrayList<String> getUsernameInscritos() {
        return leiloeira.getUsernameInscritos();
    }

    @Override
    public List<String> getNewsletter() {
        return leiloeira.obtemNewsletter();
    }

    @Override
    public List<Object> getNewsletter(int nLastNews) {
        return leiloeira.obtemNewsletter(nLastNews);
    }

    @Override
    public ArrayList<String> getUsernamesOnline() {
        return leiloeira.getUsernamesOnline();
    }

    @Override
    public List<String> getUltimosItensVendidos() {
        return leiloeira.getUltimosItensVendidos();
    }

    @Override
    public int getTotalItens() {
        return leiloeira.getTotalItens();
    }

    @Override
    public List<Object> obtemUtilizadores(UtilizadorTipoLista lista) throws SessionException {
        return leiloeira.obtemUtilizadores(lista);
    }

    @Override
    public int obtemNumUtilizador(UtilizadorTipoLista lista) throws SessionException {
        return leiloeira.obtemNumUtilizador(lista);
    }

    @Override
    public Object obtemUtilizadorById(String id) throws SessionException {
        return leiloeira.obtemUserById(id);
    }

    @Override
    public List<Object> obtemUtilizadoresRange(UtilizadorTipoLista lista, int[] range) throws SessionException {
        return leiloeira.obtemUtilizadoresRange(lista, range);
    }

    @Override
    public List<Object> obtemNewsletter() {
        return leiloeira.obtemNewsletterEnt();
    }

    @Override
    public int obtemNumNewsletter() {
        return leiloeira.obtemNumNewsletter();
    }

    @Override
    public Object obtemNewsletterById(Integer id) {
        return leiloeira.obtemNewsletterById(id);
    }

    @Override
    public List<Object> obtemNewsletterRange(int[] range) {
        return leiloeira.obtemNewsletterRange(range);
    }

    @Override
    public List<Object> obtemItens(ItensTipoLista lista) throws SessionException {
        return leiloeira.obtemItens(lista);

    }

    @Override
    public int obtemNumItens(ItensTipoLista lista) throws SessionException {
        return leiloeira.obtemNumItens(lista);
    }

    @Override
    public Object obtemItensById(Integer id) throws SessionException {
        return leiloeira.obtemItensById(id);
    }

    @Override
    public List<Object> obtemItensRange(ItensTipoLista lista, int[] range) throws SessionException {
        return leiloeira.obtemItensRange(lista, range);
    }

}
