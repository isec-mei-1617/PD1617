package beans;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ejb.Local;
import jpaentidades.TUtilizadores;
import pdtp.UtilizadorEstado;

/**
 *
 * @author diogo
 */
@Local
public interface LeiloeiraLocal {

    /**
     *
     * @param name
     * @return
     */
    boolean existeUtilizador(String name);

    /**
     *
     * @param name
     * @return
     */
    boolean logOff(String name);

    /**
     *
     * @param nome
     * @param morada
     * @param username
     * @param password
     * @return
     */
    boolean registaUtilizador(String nome, String morada, String username, String password);

    /**
     *
     * @param username
     * @param password
     * @return
     */
    boolean loginUtilizador(String username, String password);

    /**
     *
     * @return
     */
    ArrayList<String> getUsernameInscritos();

    /**
     *
     * @return
     */
    ArrayList<String> getUsernamesOnline();

    /**
     *
     * @param valor
     * @param username
     * @return
     */
    Double addSaldo(Double valor, String username);

    /**
     *
     * @param username
     * @return
     */
    Double getSaldo(String username);

    /**
     *
     * @return
     */
    public HashMap<String, TUtilizadores> getUtilizadores();

    /**
     *
     * @param username
     * @return
     */
    boolean ativaUtilizador(String username);

    /**
     *
     * @param estado
     * @return
     */
    ArrayList<String> getUtilizadoresEstado(UtilizadorEstado estado);

    /**
     *
     * @param username
     * @return
     */
    String getDadosUtilizador(String username);

    /**
     *
     * @param username
     * @param nome
     * @param morada
     * @return
     */
    boolean atualizaDadosUtilizador(String username, String nome, String morada);

    /**
     *
     * @param denunciador
     * @param denunciado
     * @param razao
     * @return
     */
    boolean pedirSuspensaoUtilizador(String denunciador, String denunciado, String razao);

    /**
     *
     * @return
     */
    HashMap<String, String> getPedidosSuspensao();

    /**
     *
     * @param username
     * @return
     */
    boolean suspendeUtilizador(String username);

    /**
     *
     * @param username
     */
    void setLastAction(String username);

    /**
     *
     * @param remetente
     * @param destinatario
     * @param texto
     * @param assunto
     * @return
     */
    boolean addMensagem(String remetente, String destinatario, String texto, String assunto);

    /**
     *
     * @param username
     * @return
     */
    ArrayList<Mensagem> getMensagensUtilizador(String username);

    /**
     *
     * @param username
     * @return
     */
    List<Object> getTMensagensUtilizador(String username);

    /**
     *
     * @param username
     * @return
     */
    int obtemNumTMensagens(String username);

    /**
     *
     * @param username
     * @param id
     * @return
     */
    Object obtemMensagemById(String username, Integer id);

    /**
     *
     * @param username
     * @param id
     * @return
     */
    Object obtemMensagemByIdEnviada(String username, Integer id);

    /**
     *
     * @param username
     * @param range
     * @return
     */
    List<Object> obtemMensagensRange(String username, int[] range);

    /**
     *
     * @param username
     * @param id
     * @param destinatario
     * @param texto
     * @param assunto
     * @return
     */
    boolean alteraMensagem(String username, Integer id, String destinatario, String texto, String assunto);

    /**
     *
     * @param username
     * @param id
     * @param lida
     * @return
     */
    boolean alteraMensagemParaLida(String username, Integer id, Boolean lida);

    /**
     *
     * @param nomeCategoria
     * @return
     */
    boolean adicionarCategoria(String nomeCategoria);

    /**
     *
     * @return
     */
    List<String> obterCategorias();

    /**
     *
     * @param nomeCategoria
     * @return
     */
    boolean eliminaCategoria(String nomeCategoria);

    /**
     *
     * @param nomeCategoria
     * @param novoNomeCategoria
     * @return
     */
    boolean modificaCategoria(String nomeCategoria, String novoNomeCategoria);

    /**
     *
     * @param username
     * @param password
     * @return
     */
    boolean pedirReativacaoUsername(String username, String password);

    /**
     *
     * @param username
     * @param password
     * @return
     */
    boolean verificaPassword(String username, String password);

    /**
     *
     * @param username
     * @param password
     * @return
     */
    boolean alteraPassword(String username, String password);

    /**
     *
     * @param categoria
     * @param username
     * @param descricao
     * @param precoInicial
     * @param precoComprarJa
     * @param dataLimite
     * @return
     */
    boolean addItem(String categoria, String username, String descricao, Double precoInicial, Double precoComprarJa, Timestamp dataLimite);

    /**
     *
     * @param id
     * @param categria
     * @param descricao
     * @param precoInicial
     * @param precoComprarJa
     * @param dataLimite
     * @return
     * @throws beans.SessionException
     */
    boolean alterarItem(Long id, String username, String categoria, String descricao, Double precoInicial, Double precoComprarJa, Timestamp dataLimite) throws SessionException;

    /**
     *
     * @param id
     * @return
     * @throws beans.SessionException
     */
    boolean eliminaItem(Long id) throws SessionException;

    /**
     *
     * @param username
     * @return
     */
    List<String> getItensUtilizador(String username);

    /**
     *
     * @return
     */
    int getTotalItens();

    /**
     *
     * @return
     */
    List<String> getItens();

    /**
     *
     * @param itemId
     * @return
     */
    String mostraItem(long itemId);

    /**
     *
     * @param itemId
     * @return
     */
    String getVendedorItem(long itemId);

    /**
     *
     * @param itemid
     * @return
     */
    String consultarLicitacoes(long itemid);

    /**
     *
     * @param itemId
     * @param comprador
     * @return
     */
    boolean comprarJaItem(long itemId, String comprador);

    /**
     *
     * @param itemId
     * @param value
     * @param username
     * @return
     */
    boolean licitarItem(long itemId, Double value, String username);

    /**
     *
     * @param username
     * @param itemId
     * @return
     */
    boolean seguirItem(String username, long itemId);

    /**
     *
     * @param username
     * @param itemId
     * @return
     */
    boolean seguirItemCancelar(String username, long itemId);

    /**
     *
     * @param username
     * @return
     */
    List<String> getItensSeguidos(String username);

    /**
     *
     * @param username
     * @return
     */
    List<String> getIensPorPagarUtilizador(String username);

    /**
     *
     * @return
     */
    List<String> getUltimosItensVendidos();

    /**
     *
     * @param username
     * @param itemId
     * @return
     */
    boolean concluirTransacao(String username, long itemId);

    /**
     *
     * @param itemId
     * @param denunciador
     * @param razao
     * @return
     */
    boolean denunciarItem(long itemId, String denunciador, String razao);

    /**
     *
     * @return
     */
    List<String> obtemDenunciasVendedores();

    /**
     *
     * @return
     */
    List<String> obtemDenunciasItens();

    /**
     *
     * @param username
     * @param vendedor
     * @param razao
     * @return
     */
    boolean denunciarVendedor(String username, String vendedor, String razao);

    /**
     *
     * @param itemId
     * @return
     */
    boolean cancelarItem(long itemId);

    public DAOLocal getDAO();

    boolean isLogged(String username);

    List<String> obtemNewsletter();

    List<Object> obtemNewsletter(int nLastNews);

    /**
     *
     * @param username
     * @return
     */
    public Object obtemUserById(String username);

    /**
     *
     * @param lista
     * @return @throws beans.SessionException
     */
    List<Object> obtemUtilizadores(UtilizadorTipoLista lista) throws SessionException;

    /**
     *
     * @param lista
     * @return @throws beans.SessionException
     */
    int obtemNumUtilizador(UtilizadorTipoLista lista) throws SessionException;

    /**
     *
     * @param lista
     * @param range
     * @return @throws beans.SessionException
     */
    List<Object> obtemUtilizadoresRange(UtilizadorTipoLista lista, int[] range) throws SessionException;

    /**
     *
     * @return @throws beans.SessionException
     */
    List<Object> obtemCategoriasEnt() throws SessionException;

    /**
     *
     * @return @throws beans.SessionException
     */
    int obtemNumCategorias() throws SessionException;

    /**
     *
     * @param id
     * @return @throws beans.SessionException
     */
    Object obtemCategoriasById(String id) throws SessionException;

    /**
     *
     * @param range
     * @return @throws beans.SessionException
     */
    List<Object> obtemCategoriasRange(int[] range) throws SessionException;
    /**
     *
     * @return @throws beans.SessionException
     */
    List<Object> obtemNewsletterEnt();

    /**
     *
     * @return @throws beans.SessionException
     */
    int obtemNumNewsletter();

    /**
     *
     * @param id
     * @return @throws beans.SessionException
     */
    Object obtemNewsletterById(Integer id);

    /**
     *
     * @param range
     * @return @throws beans.SessionException
     */
    List<Object> obtemNewsletterRange( int[] range);
        
    public List<Object> getItensSeguidosObj(String username);


        /**
     *
     * @param lista
     * @return @throws beans.SessionException
     */
    List<Object> obtemItens(ItensTipoLista lista) throws SessionException;

    /**
     *
     * @param lista
     * @return @throws beans.SessionException
     */
    int obtemNumItens(ItensTipoLista lista) throws SessionException;

    /**
     *
     * @param id
     * @return @throws beans.SessionException
     */
    Object obtemItensById(Integer id) throws SessionException;

    /**
     *
     * @param lista
     * @param range
     * @return @throws beans.SessionException
     */
    List<Object> obtemItensRange(ItensTipoLista lista, int[] range) throws SessionException;

}
