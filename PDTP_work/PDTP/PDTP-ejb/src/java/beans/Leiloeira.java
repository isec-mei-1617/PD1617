package beans;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TimerService;
import javax.persistence.EntityTransaction;
import jpaentidades.TCategoria;
import jpaentidades.TDenunciasItens;
import jpaentidades.TDenunciasVendedores;
import jpaentidades.TItens;
import jpaentidades.TLicitacoes;
import jpaentidades.TMensagens;
import jpaentidades.TNewsletters;
import jpaentidades.TUtilizadores;
import jpaentidades.TVendas;
import jpaentidades.TitemsAVenda;
import jpaentidades.TitemsJaPagos;
import jpaentidades.TitemsPorPagar;
import jpaentidades.TitemsSeguidos;
import jpafacades.TCategoriaFacade;
import jpafacades.TDenunciasItensFacade;
import jpafacades.TDenunciasVendedoresFacade;
import jpafacades.TItensFacade;
import jpafacades.TLicitacoesFacade;
import jpafacades.TMensagensFacade;
import jpafacades.TNewslettersFacade;
import jpafacades.TUtilizadoresFacade;
import jpafacades.TVendasFacade;
import jpafacades.TitemsAVendaFacade;
import jpafacades.TitemsJaPagosFacade;
import jpafacades.TitemsPorPagarFacade;
import jpafacades.TitemsSeguidosFacade;

import pdtp.ItemEstados;

import pdtp.UtilizadorEstado;
import pdtp.VendaEstados;


/*
--Tabela alteradas que tem de ser recriadas
drop table t_utilizadores cascade;
drop table t_mensagens cascade;

drop table t_itens cascade;
drop table t_vendas cascade;
drop table t_itemsAVenda cascade;
direct comparability between those and the annotations of a CDI bean: @RequestScoped --> @Stateless, @SessionScoped --> @Stateful , @ApplicationScoped --> @Singleton.

 */
/**
 *
 * @author diogo
 */
@Singleton
public class Leiloeira implements LeiloeiraLocal {

    @EJB
    private DAOLocal DAO;

    @EJB
    private TCategoriaFacade tCatagorias;
    @EJB
    private TUtilizadoresFacade tUtilizadores;
    @EJB
    private TNewslettersFacade tNewsletters;
    @EJB
    private TItensFacade tItens;
    @EJB
    private TMensagensFacade tMensagens;
    @EJB
    private TitemsAVendaFacade tItensAVenda;
    @EJB
    private TitemsJaPagosFacade tItensJaPagos;
    @EJB
    private TitemsPorPagarFacade tItensPorPagar;
    @EJB
    private TitemsSeguidosFacade tItensSeguindos;
    @EJB
    private TLicitacoesFacade tLicitacoes;
    @EJB
    private TDenunciasItensFacade tDenunciasItens;
    @EJB
    private TDenunciasVendedoresFacade tDenunciasVendedores;
    @EJB
    private TVendasFacade tVendas;

    @Resource
    TimerService timerService;

    public Leiloeira() {
    }

    private void addAdmin() {
        //Registar o ADMIN se ainda n�o exist
        if (!existeUtilizador("admin")) {
            Logger.getLogger(getClass().getName()).log(Level.INFO, "A registar o administrador");
            TUtilizadores user = new TUtilizadores();
            user.setUsername("admin");
            user.setPassword("admin");
            user.setMorada("Morada do Sistema");
            user.setNome("Administrador");
            user.setEstado(UtilizadorEstado.ATIVO);
            TNewsletters news = new TNewsletters("Registo do Administrador", "O Administrador foi inserido no sistema.");

            EntityTransaction trans = DAO.getEntityManager().getTransaction();
            trans.begin();
            tUtilizadores.create(user);
            tNewsletters.create(news);
            trans.commit();

        }
    }

    /**
     * termina os itens nas respetivas horas de fim
     */
    @Schedule(second = "*/29", minute = "*", hour = "*")
    public void checkItensDataFinal() {
        Timestamp now = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        //Refacturing para usar os itens da base de dados
        for (TItens it : tItens.findByNamedQuery("TItens.findByEstado", "estado", ItemEstados.INICIADA)) {
            if (it.getDatafim().after(now)) {
                terminaItem(((TItens) it));
            }
        }
    }

    /**
     *
     * @throws InterruptedException faz logout aos utilizadodres com 4 minutos
     * de inatividade
     */
    @Schedule(second = "*/30", minute = "*", hour = "*")
    public void checkInactivity() throws InterruptedException {
        long now = LocalDateTime.now()
                .toInstant(ZoneOffset.UTC).getEpochSecond();
        for (TUtilizadores u : tUtilizadores.findByNamedQuery("TUtilizadores.findByLogged", "logged", true)) {
            if (u.fromLastActionFromNow(now) > 240) {
                u.setLogged(false);
                tUtilizadores.editWithCommit(u);
                System.out.println("---Session Timeout user " + ((TUtilizadores) u).getUsername());
            }
        }
    }

    /**
     * Le dados do disco quando inicia
     */
    @PostConstruct
    public void loadstate() {
        this.addAdmin();
        //timerService.notifyAll();
        //DAO.getEntityManager();
        try (ObjectInputStream ois
                = new ObjectInputStream(
                        new BufferedInputStream(
                                new FileInputStream("/tmp/LeiloeiraDados")))) {
//            categorias = (ArrayList<String>) ois.readObject();

        } catch (Exception e) {
            //Utilizadors = fica com o objecto vazio criado no construtor
        }
    }

    /**
     * Grava dados em disco antes de sair
     */
    @PreDestroy
    public void saveState() {
        try (ObjectOutputStream oos
                = new ObjectOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream("/tmp/LeiloeiraDados")))) {
//            oos.writeObject(categorias);
        } catch (Exception e) {

        }
    }

//    private int getIntenCount() {
//        return itensAVenda.size();
//    }
    @Override
    public DAOLocal getDAO() {
        return DAO;
    }

    /**
     *
     * @return Lista de utilizadores ativados
     */
    @Override
    public HashMap<String, TUtilizadores> getUtilizadores() {
        HashMap<String, TUtilizadores> utilizadores = new HashMap<>();
        //Refacturing para passar a user a entidade dos utilizadores
        for (TUtilizadores u : tUtilizadores.findAll()) {
            utilizadores.put(u.getUsername(), u);
        }
        return utilizadores;
    }

    /**
     *
     * @param username
     * @return true se o userame ja existe
     */
    @Override
    public boolean existeUtilizador(String username) {
        if (username == null) {
            return false;
        }
        return tUtilizadores.find(username) != null;
    }

    /**
     *
     * @param nome
     * @param morada
     * @param username
     * @param password
     * @return true se o utilizador ficar registado
     */
    @Override
    public boolean registaUtilizador(String nome, String morada, String username, String password) {
        if (!existeUtilizador(username)) {

            TUtilizadores user = new TUtilizadores();
            user.setUsername(username);
            user.setPassword(password);
            user.setMorada(morada);
            user.setNome(nome);
            user.setEstado(UtilizadorEstado.ATIVO_PEDIDO);
            TNewsletters news = new TNewsletters("Novo utilizador", "O " + nome + " est� inscrito.");

            Logger.getLogger(getClass().getName()).log(Level.INFO, "Cria��o do utilizador");
            EntityTransaction trans = DAO.getEntityManager().getTransaction();
            trans.begin();
            tUtilizadores.create(user);
            tNewsletters.create(news);
            trans.commit();
            return true;
        } else {
            Logger.getLogger(getClass().getName()).log(Level.INFO, "O Utilizador j� existe");
            return false;
        }
    }

    /**
     *
     * @param username
     * @param password
     * @return true se o utilizador ficar logado
     */
    @Override
    public boolean loginUtilizador(String username, String password) {
        Logger.getLogger(getClass().getName()).log(Level.INFO, "A verificar o login " + username + "...");
        TUtilizadores util = tUtilizadores.find(username);
        if (util != null) {
            if (util.getPassword().equals(password)) {
                Logger.getLogger(getClass().getName()).log(Level.INFO, "A verificar o password " + username + "...");
                if ((util.getEstado() == UtilizadorEstado.ATIVO) || util.getEstado() == UtilizadorEstado.SUSPENDO_PEDIDO) {
                    if (util.isLogged()) { // esta logado -Z nao deixa repetir user
                        //TODO: Isto tem mesmo de ficar assim?
                        return true;//return false;
                    } else {
                        Logger.getLogger(getClass().getName()).log(Level.INFO, "O Utilizador " + username + " est� logado...");
                        util.setLogged(true);
                        util.setLastActionNow();
                        tUtilizadores.editWithCommit(util);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     *
     * @param username
     * @return true se o utilizador fizer loggoff
     */
    @Override
    public boolean logOff(String username) {
        TUtilizadores util = tUtilizadores.find(username);
        if (util == null) {
            return false; //nao conheco
        }
        if (!util.isLogged()) {
            return false;
        }
        //Registar o LogOut
        util.setLogged(false);
        tUtilizadores.editWithCommit(util);
        return true;
    }

    private void terminaItem(TItens it) {

        //Remover da lista dos itens � venda, determinar o comparador e o valor
        if (it.getTLicitacoesCollection().isEmpty()) {
            it.setEstado(ItemEstados.TERMINADA);
            tItens.editWithCommit(it);
            //Enviar mensagem ao vendedor
            this.addMensagem("admin", it.getVendedor().getUsername(), "Item Terminado sem comprador", it.getDescricao());
        } else {
            TLicitacoes maxLic
                    = (TLicitacoes) DAO.getEntityManager().createQuery("select t from TLicitacoes t where t.item = :item and t.valor = :valor ")
                            .setParameter("item", it)
                            .setParameter("valor", it.getLicitacaomaxima())
                            .getSingleResult();
            if (maxLic != null) {
                EntityTransaction trans = DAO.getEntityManager().getTransaction();
                trans.begin();
                it.setEstado(ItemEstados.VENDIDA);
                it.setComprador(maxLic.getLicitador());
                tItens.edit(it);
                tItensPorPagar.create(new TitemsPorPagar(maxLic.getLicitador(), it));
                TNewsletters news = new TNewsletters("Item vendido", it.getDescricao());
                tNewsletters.create(news);
                trans.commit();
                //Enviar mensagem ao vendedor
                this.addMensagem("admin", it.getVendedor().getUsername(), "Item Terminado com comprador", it.getDescricao());
                //Enviar mensagem ao comprador
                this.addMensagem("admin", maxLic.getLicitador().getUsername(), "Ganhou o item ", it.getDescricao());
                //Publicar a news
            } else {
                it.setEstado(ItemEstados.CANCELADA);
                tItens.editWithCommit(it);
                //Enviar mensagem ao comprador
                this.addMensagem("admin", it.getVendedor().getUsername(), "ERRO: Item nao Terminado com sucesso", it.getDescricao());
            }
        }

    }

    /**
     *
     * @return Lista de utilizadores inscritos, ativos e nao ativos
     */
    @Override
    public ArrayList<String> getUsernameInscritos() {
        ArrayList<String> inscritos = new ArrayList<>();
        for (TUtilizadores u : tUtilizadores.findAll()) {
            inscritos.add(u.getUsername());
        }
        return inscritos;
    }

    /**
     *
     * @return lista de utilizadores online
     */
    @Override
    public ArrayList<String> getUsernamesOnline() {
        ArrayList<String> logados = new ArrayList<String>();
        for (TUtilizadores u : tUtilizadores.findByNamedQuery("TUtilizadores.findByLogged", "logged", true)) {
            logados.add(u.getUsername());
        }
        return logados;
    }

    /**
     * Adiciona saldo a utilizador
     *
     * @param valor
     * @param username
     * @return valor de saldo se o utilizador estiver logado
     */
    @Override
    public Double addSaldo(Double valor, String username) {
        TUtilizadores util = tUtilizadores.find(username);
        if (util != null && util.isLogged() && valor > 0.0) {
            if (util.getSaldo() != null) {
                util.setSaldo(util.getSaldo() + valor);
            } else {
                util.setSaldo(valor);
            }
            tUtilizadores.editWithCommit(util);
            return util.getSaldo();
        }
        return null;
    }

    /**
     *
     * @param username
     * @return valor de saldo do utilizador
     */
    @Override
    public Double getSaldo(String username) {
        TUtilizadores util = tUtilizadores.find(username);
        if (util != null) {
            return util.getSaldo();
        }
        return null;

    }

    /**
     *
     * @param username
     * @return true se o utilizador ficar ativo
     */
    @Override
    public boolean ativaUtilizador(String username) {
        TUtilizadores util = tUtilizadores.find(username);
        if (util.getEstado() != UtilizadorEstado.ATIVO) {
            util.setEstado(UtilizadorEstado.ATIVO);
            TMensagens msg = new TMensagens();
            msg.setRemetente(tUtilizadores.find("admin"));
            msg.setDestinatario(util);
            msg.setAssunto("Conta ativada");
            msg.setTexto("Conta ativada");
            msg.setEstado(MensagemEstado.ENVIADA);
            TNewsletters news = new TNewsletters("Conta ativada", "O " + username + " foi ativado.");

            //Guardar a ativa��o do utilizador
            EntityTransaction trans = DAO.getEntityManager().getTransaction();
            trans.begin();
            tUtilizadores.edit(util);
            tNewsletters.create(news);
            tMensagens.create(msg);
            trans.commit();
            return true;
        }
        return false;
    }

    /**
     *
     * @param estado
     * @return lista de utilizadores em determinado estado
     */
    @Override
    public ArrayList<String> getUtilizadoresEstado(UtilizadorEstado estado) {
        ArrayList<String> users = new ArrayList<>();
        for (TUtilizadores u : tUtilizadores.findByNamedQuery("TUtilizadores.findByEstado", "estado", estado)) {
            users.add(u.getUsername());
        }
        return users;
    }

    /**
     *
     * @param username
     * @return dados pessoais de utilizador
     */
    @Override
    public String getDadosUtilizador(String username) {
        TUtilizadores util = tUtilizadores.find(username);
        if (util != null) {
            return util.getDados();
        }
        return null;
    }

    /**
     *
     * @param username
     * @param nome
     * @param morada
     * @return true se os dados forem atualizados
     */
    @Override
    public boolean atualizaDadosUtilizador(String username, String nome, String morada) {
        TUtilizadores util = tUtilizadores.find(username);
        if (util != null) {
            if (!nome.isEmpty()) {
                util.setNome(nome);
            }
            if (!morada.isEmpty()) {
                util.setMorada(morada);
            }
            tUtilizadores.editWithCommit(util);
            return true;
        }
        return false;
    }

    /**
     * Pedido de suspensao
     *
     * @param denunciador
     * @param denunciado
     * @param razao
     * @return true
     */
    @Override
    public boolean pedirSuspensaoUtilizador(String denunciador, String denunciado, String razao) {
        TUtilizadores util = tUtilizadores.find(denunciado);
        if (util != null) {
            util.setEstado(UtilizadorEstado.SUSPENDO_PEDIDO);
            util.setRazaopedidosuspensao(razao);
            TMensagens msg = new TMensagens();
            msg.setRemetente(tUtilizadores.find(denunciador));
            msg.setDestinatario(tUtilizadores.find("admin"));
            msg.setTexto(razao);
            msg.setAssunto("Pedido de suspensao o utilizador " + util.getUsername());
            msg.setEstado(MensagemEstado.ENVIADA);

            //Guardar a mensagem e o utilizador
            EntityTransaction trans = DAO.getEntityManager().getTransaction();
            trans.begin();
            tUtilizadores.edit(util);
            tMensagens.create(msg);
            trans.commit();

            return true;
        }
        return false;
    }

    /**
     *
     * @return
     */
    @Override
    public HashMap<String, String> getPedidosSuspensao() {
        HashMap<String, String> pedidos = new HashMap<>();
        for (TUtilizadores u : tUtilizadores.findByNamedQuery("TUtilizadores.findByEstado", "estado", UtilizadorEstado.SUSPENDO_PEDIDO)) {
            pedidos.put(u.getUsername(), u.getRazaopedidosuspensao());
        }
        return pedidos;

    }

    /**
     *
     * @param username
     * @return
     */
    @Override
    public boolean suspendeUtilizador(String username) {
        TUtilizadores util = tUtilizadores.find(username);
        if (util != null) {
            util.setEstado(UtilizadorEstado.SUSPENSO);
            TMensagens msg = new TMensagens();
            msg.setRemetente(util);
            msg.setDestinatario(tUtilizadores.find("admin"));
            msg.setAssunto("Conta suspensa");
            msg.setTexto("Conta suspensa do utilizador " + username);
            msg.setEstado(MensagemEstado.ENVIADA);

            //Guardar a ativa��o do utilizador
            EntityTransaction trans = DAO.getEntityManager().getTransaction();
            trans.begin();
            tMensagens.create(msg);
            tUtilizadores.edit(util);
            trans.commit();

            return true;
        }
        return false;
    }

    /**
     *
     * @param username
     */
    @Override
    public void setLastAction(String username) {
        TUtilizadores util = tUtilizadores.find(username);
        if (util != null) {
            util.setLastActionNow();
            tUtilizadores.editWithCommit(util);
        }
    }

    /**
     *
     * @param remetente
     * @param destinatario
     * @param texto
     * @param assunto
     * @return
     */
    @Override
    public boolean addMensagem(String remetente, String destinatario, String texto, String assunto) {
        //TODO: Refazer com utilizadores em vez de strings
        TMensagens msg = new TMensagens();
        msg.setRemetente(tUtilizadores.find(remetente));
        msg.setDestinatario(tUtilizadores.find(destinatario));
        msg.setAssunto(assunto);
        msg.setTexto(texto);
        msg.setEstado(MensagemEstado.ENVIADA);

        //Guardar a mensagem e o utilizador
        tMensagens.createWithCommit(msg);
        return true;
    }

    /**
     *
     * @param username
     * @return
     */
    @Override
    public ArrayList<Mensagem> getMensagensUtilizador(String username) {
        ArrayList<Mensagem> myMsg = new ArrayList<>();
        //Obter todas as mensagens enviadas para o utilizador
        for (Object msg : this.getTMensagensUtilizador(username)) {
            Mensagem msgRef = new Mensagem(((TMensagens) msg).getRemetente().getUsername(),
                    ((TMensagens) msg).getDestinatario().getUsername(),
                    ((TMensagens) msg).getTexto(),
                    ((TMensagens) msg).getAssunto(),
                    ((TMensagens) msg).getEstado(),
                    ((TMensagens) msg).getData());
            myMsg.add(msgRef);
        }

        return myMsg;
    }

    /**
     *
     * @param username
     * @return
     */
    @Override
    public List<Object> getTMensagensUtilizador(String username) {
        //Obter todas as mensagens enviadas para o utilizador
        return (List) tMensagens.findByNamedQuery("TMensagens.findByDestinatario", "username", new TUtilizadores(username));
    }

    @Override
    public int obtemNumTMensagens(String username) {
        //Obter o numero de mensagens do utilizador
        return tMensagens.countByNamedQuery("TMensagens.countFindByDestinatario", "username", new TUtilizadores(username));
    }

    @Override
    public Object obtemMensagemById(String username, Integer id) {
        //Obter um mensagem pelo ID e verificar se � para o utilizador
        TMensagens msg = tMensagens.find(id);
        if (msg != null) {
            if (username.equals(msg.getDestinatario().getUsername())) {
                return msg;
            }
        }
        return null;
    }

    @Override
    public Object obtemMensagemByIdEnviada(String username, Integer id) {
        //Obter um mensagem pelo ID e verificar se � para do utilizador
        TMensagens msg = tMensagens.find(id);
        if (msg != null) {
            if (username.equals(msg.getRemetente().getUsername())) {
                return msg;
            }
        }
        return null;
    }

    @Override
    public List<Object> obtemMensagensRange(String username, int[] range) {
        //Obter pelo intervalo passado de registos
        return (List) tMensagens.findRangeByNamedQuery(range, "TMensagens.findByDestinatario", "username", new TUtilizadores(username));
    }

    @Override
    public boolean alteraMensagem(String username, Integer id, String destinatario, String texto, String assunto) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        TMensagens msg = (TMensagens) this.obtemMensagemById(username, id);
        if (msg != null) {
            if (msg.getEstado() == MensagemEstado.ENVIADA) {
                msg.setAssunto(assunto);
                msg.setTexto(texto);
                msg.setDestinatario(new TUtilizadores(destinatario));
                tMensagens.editWithCommit(msg);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean alteraMensagemParaLida(String username, Integer id, Boolean lida) {
        //Marcar uma mensagem como lida
        TMensagens msg = (TMensagens) this.obtemMensagemById(username, id);
        if (msg != null) {
            if (lida) {
                msg.setEstado(MensagemEstado.LIDA);
            } else {
                msg.setEstado(MensagemEstado.ENTREGUE);
            }
            tMensagens.editWithCommit(msg);
        }
        return false;
    }

    /**
     *
     * @param nomeCategoria
     * @return
     */
    @Override
    public boolean adicionarCategoria(String nomeCategoria) {
        if (tCatagorias.findByNamedQuery("TCategoria.findByNome", "nome", nomeCategoria).isEmpty()) {
            //Se a categoria n�o existe, ent�o adiciona-se
            tCatagorias.createWithCommit(new TCategoria(nomeCategoria));
            return true;
        }
        return false;
    }

    /**
     *
     * @return
     */
    @Override
    public List<String> obterCategorias() {
        List<String> categorias = new ArrayList<>();
        for (TCategoria cat : tCatagorias.findAll()) {
            categorias.add(cat.getNome());
        }
        return categorias;
    }

    /**
     *
     * @param nomeCategoria
     * @return
     */
    @Override
    public boolean eliminaCategoria(String nomeCategoria) {
        boolean ret = false;
        for (TCategoria cat : tCatagorias.findByNamedQuery("TCategoria.findByNome", "nome", nomeCategoria)) {
            tCatagorias.removeWithCommit(cat);
            ret = true;
        }
        return ret;
    }

    /**
     *
     * @param username
     * @param password
     * @return
     */
    @Override
    public boolean pedirReativacaoUsername(String username, String password) {
        TUtilizadores util = tUtilizadores.find(username);
        if (util != null) {
            if (util.getPassword().equals(password)) {
                if (UtilizadorEstado.SUSPENSO == util.getEstado()) {
                    util.setEstado(UtilizadorEstado.REATIVACAO_PEDIDO);
                    tUtilizadores.editWithCommit(util);
                    return true;
                } else {
                }
            }
        }
        return false;
    }

    /**
     *
     * @param nomeCategoria
     * @param novoNomeCategoria
     * @return
     */
    @Override
    public boolean modificaCategoria(String nomeCategoria, String novoNomeCategoria) {
        boolean ret = false;
        for (TCategoria cat : tCatagorias.findByNamedQuery("TCategoria.findByNome", "nome", nomeCategoria)) {
            cat.setNome(novoNomeCategoria);
            tCatagorias.editWithCommit(cat);
            ret = true;
        }
        return ret;
    }

    /**
     *
     * @param username
     * @param password
     * @return
     */
    @Override
    public boolean verificaPassword(String username, String password) {
        TUtilizadores util = tUtilizadores.find(username);
        if (util != null) {
            return (password.equals(util.getPassword()));
        }
        return false;
    }

    /**
     *
     * @param username
     * @param password
     * @return
     */
    @Override
    public boolean alteraPassword(String username, String password) {
        TUtilizadores util = tUtilizadores.find(username);
        if (util != null) {
            util.setPassword(password);
            tUtilizadores.editWithCommit(util);
            return true;
        }
        return false;
    }

    /**
     *
     * @param username
     * @param categoria
     * @param descricao
     * @param precoInicial
     * @param precoComprarJa
     * @param dataLimite
     * @return
     */
    @Override
    public boolean addItem(String username, String categoria, String descricao, Double precoInicial, Double precoComprarJa, Timestamp dataLimite) {
        TUtilizadores util = tUtilizadores.find(username);
        if (util != null) {
            TItens item = new TItens();
            item.setDescricao(descricao);
            item.setPrecoinicial(precoInicial);
            item.setComprarja(precoComprarJa);
            item.setDatafim(dataLimite);
            item.setVendedor(util);
            item.setEstado(ItemEstados.INICIADA);
            item.setCategoria(categoria);
            TitemsAVenda iVenda = new TitemsAVenda(item);
            TNewsletters news = new TNewsletters("Item adicionado ", item.getDescricao());
            EntityTransaction trans = DAO.getEntityManager().getTransaction();
            trans.begin();
            tItens.create(item);
            tItensAVenda.create(iVenda);
            tNewsletters.create(news);
            trans.commit();

            return true;
        }
        return false;
    }

    @Override
    public boolean alterarItem(Long id, String username, String categoria, String descricao, Double precoInicial, Double precoComprarJa, Timestamp dataLimite) throws SessionException {
        TUtilizadores util = tUtilizadores.find(username);
        if (util != null) {
            TItens item = tItens.find(id);
            item.setDescricao(descricao);
            item.setPrecoinicial(precoInicial);
            item.setComprarja(precoComprarJa);
            item.setDatafim(dataLimite);
            item.setEstado(ItemEstados.INICIADA);
            item.setCategoria(categoria);
            tItens.editWithCommit(item);

            return true;
        }
        return false;
    }

    @Override
    public boolean eliminaItem(Long id) throws SessionException {
        tItens.remove(new TItens(id));
        return true;
    }

    /**
     *
     * @param username
     * @return
     */
    @Override
    public List<String> getItensUtilizador(String username) {
        List<String> itensUtilizador = new ArrayList<>();
        TUtilizadores util = tUtilizadores.find(username);
        if (util != null) {
            //Obter todosos itens do utilizador
            for (TItens it : tItens.findByNamedQuery("TItens.findByVendedor", "vendedor", util)) {
                itensUtilizador.add(it.toLineString());
            }
        }
        return itensUtilizador;
    }

    /**
     *
     * @return
     */
    @Override
    public int getTotalItens() {
        //Saber o n�mero de itens � venda
        return tItensAVenda.count();
    }

    /**
     *
     * @return
     */
    @Override
    public List<String> getItens() {
        List<String> itensResult = new ArrayList<>();
        //Obter todos os itens
        for (TItens d : tItens.findAll()) {
            itensResult.add(d.toLineString());
        }
        return itensResult;
    }

    /**
     *
     * @param itemId
     * @return
     */
    @Override
    public String mostraItem(long itemId) {
        TItens item = tItens.find(itemId);
        if (item == null) {
            return "ERRO: Item invalido";
        }
        return item.toString();
    }

    /**
     *
     * @param itemId
     * @return
     */
    @Override
    public String getVendedorItem(long itemId) {
        TItens item = tItens.find(itemId);
        if (item == null) {
            return "ERRO: Item invalido";
        }
        return item.getVendedor().getUsername();
    }

    /**
     *
     * @param itemId
     * @return
     */
    @Override
    public String consultarLicitacoes(long itemId) {
        StringBuilder lista = new StringBuilder();
        TItens i = tItens.find(itemId);
        if (i == null) {
            return "ERRO: Item invalido";
        }
        //Obter todas as licita��es do Item
        for (TLicitacoes licitacao : i.getTLicitacoesCollection()) {
            lista.append(licitacao.getTimestamp());
            lista.append("->");
            lista.append(Double.toString(licitacao.getValor()));
            lista.append("\n Licitador: ");
            lista.append(licitacao.getLicitador());
            lista.append("\n");
        }
        return lista.toString();
    }

    /**
     *
     * @param itemId
     * @param comprador
     * @return
     */
    @Override
    public boolean comprarJaItem(long itemId, String comprador) {
        TItens i = tItens.find(itemId);
        if (i != null) {
            TUtilizadores u = tUtilizadores.find(comprador);
            if (u != null) {
                i.setEstado(ItemEstados.VENDIDA);
                i.setComprador(u);
                TVendas venda = new TVendas();
                venda.setComprador(u);
                venda.setItem(i);
                venda.setValor(i.getLicitacaomaxima());
                venda.setEstado(VendaEstados.ESPERA);
                EntityTransaction trans = DAO.getEntityManager().getTransaction();
                trans.begin();
                tItens.edit(i);
                tVendas.create(venda);
                tItensPorPagar.create(new TitemsPorPagar(u, i));
                trans.commit();
                //Enviar mensagem ao vendedor
                this.addMensagem("admin", i.getVendedor().getUsername(), "Item Terminado com comprador", i.getDescricao());
                //Enviar mensagem ao comprador
                this.addMensagem("admin", u.getUsername(), "Ganhou o item ", i.getDescricao());
                //Publicar a news
                TNewsletters news = new TNewsletters("Item vendido", i.getDescricao());
                tNewsletters.createWithCommit(news);

            }
        }
        return false;
    }

    /**
     *
     * @param itemId
     * @param value
     * @param username
     * @return
     */
    @Override
    public boolean licitarItem(long itemId, Double value, String username) {
        TItens i = tItens.find(itemId);
        if (i != null) {
            TUtilizadores licitador = (TUtilizadores) DAO.find(TUtilizadores.class, username);
            if (licitador != null) {
                if (i.getPrecoinicial() == null || i.getPrecoinicial() >= value) {
                    //Refacturing para passar a usar e entidade dos utilizadores
                    if (i.getLicitacaomaxima() == null || i.getLicitacaomaxima() > value) {
                        //registar a licita��o
                        EntityTransaction trans = DAO.getEntityManager().getTransaction();
                        trans.begin();
                        i.setLicitacaomaxima(value);
                        tItens.edit(i);
                        TLicitacoes lic = new TLicitacoes();
                        lic.setItem(i);
                        lic.setLicitador(licitador);
                        lic.setValor(value);
                        tLicitacoes.create(lic);
                        trans.commit();
                        if (value >= i.getComprarja()) {
                            this.comprarJaItem(itemId, username);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     *
     * @param username
     * @param itemId
     * @return
     */
    @Override
    public boolean seguirItem(String username, long itemId) {
        TItens i = tItens.find(itemId);
        if (i == null) {
            return false;
        }
        TUtilizadores u = tUtilizadores.find(username);
        if (u != null) {
            //Refacturing para passar a user a entidade dos utilizadores
            if (tItensSeguindos.findByNamedQuery("TitemsSeguidos.findByItemUtilizador", "item", i, "utilizador", u).isEmpty()) {
                tItensSeguindos.createWithCommit(new TitemsSeguidos(u, i));
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param username
     * @param itemId
     * @return
     */
    @Override
    public boolean seguirItemCancelar(String username, long itemId) {
        TItens i = tItens.find(itemId);
        if (i == null) {
            return false;
        }
        TUtilizadores u = tUtilizadores.find(username);
        if (u != null) {
            //Remover o item da lista dos seguidos
            if (!tItensSeguindos.findByNamedQuery("TitemsSeguidos.findByItemUtilizador", "item", i, "utilizador", u).isEmpty()) {
                tItensSeguindos.removeWithCommit(new TitemsSeguidos(u, i));
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param username
     * @return
     */
    @Override
    public List<String> getItensSeguidos(String username) {
        List<String> result = new ArrayList<>();
        TUtilizadores u = tUtilizadores.find(username);
        if (u != null) {
            //Refacturing para passar a user a entidade dos utlizadores
            for (TitemsSeguidos seg : tItensSeguindos.findByNamedQuery("TitemsSeguidos.findByUtilizador", "utilizador", u)) {
                result.add(seg.toString());
            }
            return result;
        }
        return null;
    }

    @Override
    public List<Object> getItensSeguidosObj(String username) {
        List<Object> result = new ArrayList<>();
        TUtilizadores u = tUtilizadores.find(username);
        if (u != null) {
            for (TitemsSeguidos itemseg : tItensSeguindos.findByNamedQuery("TitemsSeguidos.findByUtilizador", "utilizador", u)) {
                System.out.println("---" + itemseg.toString());
                result.add(itemseg.getItem());
                System.out.println("ADD---" + itemseg.getItem().getDescricao());
                //result.add(((TitemsSeguidos) seg).toString());
            }
            return result;
        }
        return null;
    }

    /**
     *
     * @param username
     * @return
     */
    @Override
    public List<String> getUltimosItensVendidos() {
        List<String> result = new ArrayList<>();
        //Refacturing para passar a user a entidade dos utlizadores
        for (TVendas venda : tVendas.findByNamedQuery("TVendas.findLast", 10)) {
            result.add(venda.toString());
        }
        return result;
    }

    /**
     *
     * @param username
     * @return
     */
    @Override
    public List<String> getIensPorPagarUtilizador(String username) {
        List<String> result = new ArrayList<>();
        TUtilizadores u = tUtilizadores.find(username);
        if (u != null) {
            //Refacturing para passar a user a entidade dos utilizadores
            for (TitemsPorPagar seg : tItensPorPagar.findByNamedQuery("TitemsPorPagar.findByUtilizador", "utilizador", u)) {
                result.add(seg.toString());
            }
            return result;
        }
        return null;
    }

    /**
     *
     * @param username
     * @param itemId
     * @return
     */
    @Override
    public boolean concluirTransacao(String username, long itemId) {
        //Concluir a transa��o/pagamento
        TItens i = tItens.find(itemId);
        if (i == null) {
            return false;
        }
        TUtilizadores u = tUtilizadores.find(username);
        if (u != null) {
            //Refacturing para passar a user a entidade dos utilizadores
            TitemsPorPagar porPag = tItensPorPagar.findByNamedQuery("TitemsPorPagar.findByItemUtilizador", "item", i, "utilizador", u).get(0);
            if (porPag == null && i.getLicitacaomaxima() <= u.getSaldo()) {
                //Tirar o Saldo
                u.setSaldo(u.getSaldo() - i.getLicitacaomaxima());
                //Guardar ps dados
                EntityTransaction trans = DAO.getEntityManager().getTransaction();
                trans.begin();
                tUtilizadores.edit(u);
                tItensPorPagar.remove(porPag);
                tItensJaPagos.create(new TitemsJaPagos(u, i));
                trans.commit();
                return true;
            }
        }
        return false;

    }

    /**
     *
     * @param itemId
     * @param denunciador
     * @param razao
     * @return
     */
    @Override
    public boolean denunciarItem(long itemId, String denunciador, String razao) {
        TItens i = tItens.find(itemId);
        if (i == null) {
            return false;
        }
        TUtilizadores u = tUtilizadores.find(denunciador);
        if (u == null) {
            return false;
        }
        //refacturing para usar a entidade utilizador
        TDenunciasItens den = new TDenunciasItens();
        den.setDenunciador(u);
        den.setItem(i);
        den.setRazao(razao);
        tDenunciasItens.createWithCommit(den);
        addMensagem(denunciador, "admin", den.toString(), "denuncia item " + i.getItemid());
        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public List<String> obtemDenunciasVendedores() {
        List<String> result = new ArrayList<>();
        //Obter os vendedores denunciados
        for (TDenunciasVendedores d : tDenunciasVendedores.findAll()) {
            result.add(d.toLineString());
        }
        return result;
    }

    /**
     *
     * @return
     */
    @Override
    public List<String> obtemDenunciasItens() {
        List<String> result = new ArrayList<>();
        //Obter os itens denunciados
        for (TDenunciasItens d : tDenunciasItens.findAll()) {
            result.add(d.toLineString());
        }
        return result;
    }

    /**
     *
     * @param denunciador
     * @param vendedor
     * @param razao
     * @return
     */
    @Override
    public boolean denunciarVendedor(String denunciador, String vendedor, String razao) {
        TUtilizadores d = tUtilizadores.find(denunciador);
        if (d == null) {
            return false;
        }
        TUtilizadores v = tUtilizadores.find(vendedor);
        if (v == null) {
            return false;
        }
        //Refacturing para usar a entidade utilizador
        TDenunciasVendedores den = new TDenunciasVendedores();
        den.setDenunciador(d);
        den.setVendedor(v);
        den.setRazao(razao);
        tDenunciasVendedores.createWithCommit(den);
        addMensagem(denunciador, "admin", d.toString(), "denuncia vendedor " + v.getUsername());
        return true;
    }

    /**
     *
     * @param itemId
     * @return
     */
    @Override
    public boolean cancelarItem(long itemId) {
        TItens i = tItens.find(itemId);
        if (i != null) {
            i.setEstado(ItemEstados.CANCELADA);
            tItens.editWithCommit(i);
            return true;
        }
        return false;
    }

    @Override
    public boolean isLogged(String username) {
        TUtilizadores u = tUtilizadores.find(username);
        if (u != null) {
            return u.isLogged();
        }
        return false;
    }

    @Override
    public List<String> obtemNewsletter() {
        List<String> news = new ArrayList<>();
        for (Object n : obtemNewsletter(0)) {
            news.add(((TNewsletters) n).getAssunto() + ":" + ((TNewsletters) n).getNewsletter());
        }
        return news;
    }

    @Override
    public List<Object> obtemNewsletter(int nlastNews) {
        List list;
        if (nlastNews > 0) {
            //Encontra todas
            list = tNewsletters.findByNamedQuery("TNewsletters.findAll", "", null);
        } else {
            //Encontrar as ultimas 
            list = tNewsletters.findByNamedQuery("TNewsletters.findAll", nlastNews);
        }
        return list;
    }

    @Override
    public Object obtemUserById(String username) {
        return tUtilizadores.find(username);
    }

    @Override
    public List<Object> obtemUtilizadores(UtilizadorTipoLista lista) throws SessionException {
        List list = null;
        if (null != lista) {
            switch (lista) {
                case LISTA_USER_ALL:
                    list = tUtilizadores.findAll();
                    break;
                case LISTA_USER_ATIVOS:
                    list = tUtilizadores.findByNamedQuery("TUtilizadores.findByEstado", "estado", UtilizadorEstado.ATIVO);
                    break;
                case LISTA_USER_ADESOES:
                    list = tUtilizadores.findByNamedQuery("TUtilizadores.findByEstado", "estado", UtilizadorEstado.ATIVO_PEDIDO);
                    break;
                case LISTA_USER_REARIVAR:
                    list = tUtilizadores.findByNamedQuery("TUtilizadores.findByEstado", "estado", UtilizadorEstado.REATIVACAO_PEDIDO);
                    break;
                case LISTA_USER_SUPENDER:
                    list = tUtilizadores.findByNamedQuery("TUtilizadores.findByEstado", "estado", UtilizadorEstado.SUSPENDO_PEDIDO);
                    break;
                case LISTA_USER_ONLINE:
                    list = tUtilizadores.findByNamedQuery("TUtilizadores.findByLogged", "logged", true);
                    break;
                case LISTA_USER_DENUNCIADOS:
                    list = tUtilizadores.findByNamedQuery("TUtilizadores.findAllDenuncias", "", null);
                    break;
                default:
                    break;
            }
        }
        return list;
    }

    @Override
    public int obtemNumUtilizador(UtilizadorTipoLista lista) throws SessionException {
        int numReg = 0;
        if (null != lista) {
            switch (lista) {
                case LISTA_USER_ALL:
                    numReg = tUtilizadores.count();
                    break;
                case LISTA_USER_ATIVOS:
                    numReg = tUtilizadores.countByNamedQuery("TUtilizadores.findByEstado", "estado", UtilizadorEstado.ATIVO);
                    break;
                case LISTA_USER_ADESOES:
                    numReg = tUtilizadores.countByNamedQuery("TUtilizadores.countFindByEstado", "estado", UtilizadorEstado.ATIVO_PEDIDO);
                    break;
                case LISTA_USER_REARIVAR:
                    numReg = tUtilizadores.countByNamedQuery("TUtilizadores.countFindByEstado", "estado", UtilizadorEstado.REATIVACAO_PEDIDO);
                    break;
                case LISTA_USER_SUPENDER:
                    numReg = tUtilizadores.countByNamedQuery("TUtilizadores.countFindByEstado", "estado", UtilizadorEstado.SUSPENDO_PEDIDO);
                    break;
                case LISTA_USER_ONLINE:
                    numReg = tUtilizadores.countByNamedQuery("TUtilizadores.countFindByLogged", "logged", true);
                    break;
                case LISTA_USER_DENUNCIADOS:
                    numReg = tUtilizadores.countByNamedQuery("TUtilizadores.countfindAllDenuncias", "", null);
                    break;
                default:
                    break;
            }
        }
        return numReg;
    }

    @Override
    public List<Object> obtemUtilizadoresRange(UtilizadorTipoLista lista, int[] range) throws SessionException {
        List list = null;
        if (null != lista) {
            switch (lista) {
                case LISTA_USER_ALL:
                    list = tUtilizadores.findRange(range);
                    break;
                case LISTA_USER_ATIVOS:
                    list = tUtilizadores.findRangeByNamedQuery(range, "TUtilizadores.findByEstado", "estado", UtilizadorEstado.ATIVO);
                    break;
                case LISTA_USER_ADESOES:
                    list = tUtilizadores.findRangeByNamedQuery(range, "TUtilizadores.findByEstado", "estado", UtilizadorEstado.ATIVO_PEDIDO);
                    break;
                case LISTA_USER_REARIVAR:
                    list = tUtilizadores.findRangeByNamedQuery(range, "TUtilizadores.findByEstado", "estado", UtilizadorEstado.REATIVACAO_PEDIDO);
                    break;
                case LISTA_USER_SUPENDER:
                    list = tUtilizadores.findRangeByNamedQuery(range, "TUtilizadores.findByEstado", "estado", UtilizadorEstado.SUSPENDO_PEDIDO);
                    break;
                case LISTA_USER_ONLINE:
                    list = tUtilizadores.findRangeByNamedQuery(range, "TUtilizadores.findByLogged", "logged", true);
                    break;
                case LISTA_USER_DENUNCIADOS:
                    list = tUtilizadores.findRangeByNamedQuery(range, "TUtilizadores.findAllDenuncias", "", null);
                    break;
                default:
                    break;
            }
        }

        return list;
    }

    @Override
    public List<Object> obtemCategoriasEnt() {
        return (List) tCatagorias.findAll();
    }

    @Override
    public int obtemNumCategorias() {
        return tCatagorias.count();
    }

    @Override
    public Object obtemCategoriasById(String id) {
        return tCatagorias.find(id);
    }

    @Override
    public List<Object> obtemCategoriasRange(int[] range) {
        return (List) tCatagorias.findRange(range);
    }

    @Override
    public List<Object> obtemNewsletterEnt() {
        return (List) tNewsletters.findAll();
    }

    @Override
    public int obtemNumNewsletter() {
        return tNewsletters.count();
    }

    @Override
    public Object obtemNewsletterById(Integer id) {
        return tNewsletters.find(id);
    }

    @Override
    public List<Object> obtemNewsletterRange(int[] range) {
        return (List) tNewsletters.findRange(range);
    }

    @Override
    public Object obtemItensById(Integer id) throws SessionException {
        return tItens.find(id);
    }

    @Override
    public List<Object> obtemItens(ItensTipoLista lista) throws SessionException {
        List list = null;
        if (null != lista) {
            switch (lista) {
                case LISTA_ITENS_ALL:
                    list = tItens.findAll();
                    break;
                case LISTA_ITENS_ULTIMOS_VENDIDOS:
                    Calendar cal;
                    (cal = Calendar.getInstance()).add(Calendar.YEAR, -1);
                    list = tItens.findByNamedQuery("TItens.findByDatafim", "datafim", cal.getTime());
                    break;
                case LISTA_ITENS_DENUCIADOS:
                    list = tItens.findByNamedQuery("TItens.findAllDenuncias", "", null);
                    break;
                default:
                    break;
            }
        }
        return list;
    }

    @Override
    public int obtemNumItens(ItensTipoLista lista) throws SessionException {
        int numItem = 0;
        if (null != lista) {
            switch (lista) {
                case LISTA_ITENS_ALL:
                    numItem = tItens.count();
                    break;
                case LISTA_ITENS_ULTIMOS_VENDIDOS:
                    Calendar cal;
                    (cal = Calendar.getInstance()).add(Calendar.YEAR, -1);
                    numItem = tItens.countByNamedQuery("TItens.countFindByDatafim", "datafim", cal.getTime());
                    break;
                case LISTA_ITENS_DENUCIADOS:
                    numItem = tItens.countByNamedQuery("TItens.countFindAllDenuncias", "", null);
                    break;
                default:
                    break;
            }
        }
        return numItem;
    }

    @Override
    public List<Object> obtemItensRange(ItensTipoLista lista, int[] range) throws SessionException {
        List list = null;
        if (null != lista) {
            switch (lista) {
                case LISTA_ITENS_ALL:
                    list = tItens.findRange(range);
                    break;
                case LISTA_ITENS_ULTIMOS_VENDIDOS:
                    Calendar cal;
                    (cal = Calendar.getInstance()).add(Calendar.YEAR, -1);
                    list = tItens.findRangeByNamedQuery(range, "TItens.findByDatafim", "datafim", cal.getTime());
                    break;
                case LISTA_ITENS_DENUCIADOS:
                    list = tItens.findRangeByNamedQuery(range, "TItens.findAllDenuncias", "", null);
                    break;
                default:
                    break;
            }
        }
        return list;
    }
}
