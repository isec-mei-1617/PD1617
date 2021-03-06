package beans;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;

/**
 *
 * @author diogo
 */
//@Named
@Stateless
public class ClientVisitante extends ClientBase implements ClientVisitanteRemote {

    @Override
    public boolean loginUtilizador(String username, String password) {
        return leiloeira.loginUtilizador(username, password);
    }

    @Override
    public boolean inscreveUtilizador(String nome, String morada, String username, String password) {
        return leiloeira.registaUtilizador(nome, morada, username, password);
    }

    @Override
    public boolean pedirReativacaoUsername(String username, String password) {
        return leiloeira.pedirReativacaoUsername(username, password);
    }

    @Override
    public boolean existeUsername(String username) {
        return leiloeira.existeUtilizador(username);
    }

    @Override
    public boolean isAdmin(String username) {
        return "admin".equals(username);
    }

    @Override
    public List<Object> obtemUtilizadores(UtilizadorTipoLista lista) throws SessionException {
        if (lista == UtilizadorTipoLista.LISTA_USER_ALL) {
            return super.obtemUtilizadores(lista);
        }
        return null;
    }

    @Override
    public int obtemNumUtilizador(UtilizadorTipoLista lista) throws SessionException {
        if (lista == UtilizadorTipoLista.LISTA_USER_ALL) {
            return super.obtemNumUtilizador(lista);
        }
        return 0;
    }

    @Override
    public Object obtemUtilizadorById(String id) throws SessionException {
        return super.obtemUtilizadorById(id);
    }

    @Override
    public List<Object> obtemUtilizadoresRange(UtilizadorTipoLista lista, int[] range) throws SessionException {
        if (lista == UtilizadorTipoLista.LISTA_USER_ALL) {
            return super.obtemUtilizadoresRange(lista, range);
        }
        return null;

    }

    @Override
    public List<Object> obtemNewsletter(){
        return super.obtemNewsletter();
    }

    @Override
    public int obtemNumNewsletter(){
        return super.obtemNumNewsletter();
    }

    @Override
    public Object obtemNewsletterById(Integer id){
        return super.obtemNewsletterById(id);
    }

    @Override
    public List<Object> obtemNewsletterRange(int[] range){
        return super.obtemNewsletterRange(range);
    }

    @Override
    public List<Object> obtemItens(ItensTipoLista lista) throws SessionException {
        if( lista == ItensTipoLista.LISTA_ITENS_ULTIMOS_VENDIDOS ){
            return super.obtemItens(lista);
        }
        return new ArrayList<>();
    }

    @Override
    public int obtemNumItens(ItensTipoLista lista) throws SessionException {
        if( lista == ItensTipoLista.LISTA_ITENS_ULTIMOS_VENDIDOS ){
            return super.obtemNumItens(lista);
        }
        return 0;
    }

    @Override
    public Object obtemItensById(Integer id) throws SessionException {
        return super.obtemItensById(id);
    }

    @Override
    public List<Object> obtemItensRange(ItensTipoLista lista, int[] range) throws SessionException {
        if( lista == ItensTipoLista.LISTA_ITENS_ULTIMOS_VENDIDOS ){
            return super.obtemItensRange(lista, range);
        }
        return new ArrayList<>();
    }

}
