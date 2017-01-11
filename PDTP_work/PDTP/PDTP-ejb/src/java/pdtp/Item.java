package pdtp;


import java.io.Serializable;
import java.util.Date;
import java.util.TreeMap;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Item implements Serializable {

    private TreeMap<Double, Licitacao> licitacoes;
    private Timestamp dataInicio;
    private Timestamp dataFim;
    private Utilizador vendedor;
    private String descricao;
    private Double precoInicial;
    private Double comprarJa;
    private String categoria;
    private ItemEstados estado;
    private Venda venda;
    private Utilizador comprador;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH");
    private int itemID;
    
    public Item(int itemID, Utilizador vendedor, Double precoInicial, Double comprarJa, Timestamp dataLimite, String descricao) {
        this.vendedor = vendedor;
        this.descricao = descricao;
        this.precoInicial = precoInicial;
        this.comprarJa = comprarJa;
        this.licitacoes = new TreeMap<>();
        this.dataInicio = new Timestamp(new Date().getTime());
        this.dataFim = dataLimite;
        this.itemID = itemID;
        //this.dataFim = dataLimite;
        this.estado = ItemEstados.INICIADA;
       
    }

    public SimpleDateFormat getSdf() {
        return sdf;
    }

    public String getDataInicio() {
        return sdf.format(dataInicio);
    }

    public String getDataFim() {
         return sdf.format(dataFim);
    }
    public Timestamp getDataFimTimeStamp() {
         return dataFim;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public ItemEstados getEstado() {
        return estado;
    }

    public void setEstado(ItemEstados estado) {
        this.estado = estado;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public Utilizador getComprador() {
        return comprador;
    }

    public void setComprador(Utilizador comprador) {
        this.comprador = comprador;
    }

    public boolean addLicitacao(Utilizador licitador, Double valor) {
        if (!checkLicitador(licitador)) {
            return false;
        }
        if (!checkValor(valor)) {
            return false;
        }
        Licitacao lic = new Licitacao(this,licitador,valor);
        this.licitacoes.put(valor, lic);
        return true;
    }
    public boolean addVendaLicitacao(){
        if (this.venda!=null)
            return false;
        Timestamp now = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        if(dataFim.after(now)){
            return false;
        }
        Licitacao lic=licitacoes.lastEntry().getValue();
        this.venda = new Venda(lic);
        return true;
    }
    public boolean comprarJa(Utilizador membro) {
        if (!checkLicitador(membro)) {
            return false;
        }
        this.comprador = membro;
        this.comprador.decSaldo(comprarJa);
        this.venda = new Venda(this,membro,this.comprarJa);
        this.estado = ItemEstados.TERMINADA;
        return true;
    }

    public boolean cancelarVenda(Utilizador membro) {
        if (checkLicitador(membro)) {
            this.estado = ItemEstados.CANCELADA;
            return true;
        }
        return false;
    }

    private boolean checkLicitador(Utilizador licitador) {
        return licitador != vendedor;
    }

    private boolean checkValor(Double valor) {
        return (valor <= licitacoes.lastKey());
    }

    public Utilizador getVendedor() {
        return vendedor;
    }

    public String getDescricao() {
        return descricao;
    }

    public Double getComprarJa() {
        return comprarJa;
    }
    @Override
    public String toString(){
        StringBuilder item = new StringBuilder();
        item.append("ID: ");
        item.append(itemID);
        item.append("\nDescricao: ");
        item.append(descricao);
        item.append("\nData fim: ");
        item.append(this.getDataFim());
        item.append("\nVendedor: ");
        item.append(vendedor.getUsername());
        item.append("\n");
        item.append("\nComprar Ja: ");
        item.append(this.comprarJa);
        item.append("\n");
        item.append("\nLicitacoes: ");
        int licitacoesSize=this.licitacoes.size();
        item.append(licitacoesSize);
        item.append("\n");
        Double licAtual =0.00;
        if (licitacoesSize>0){
             
             licAtual = this.licitacoes.lastKey();
        }else {
            licAtual = this.precoInicial;
        }
        item.append("\nLicitacao Atual: ");
        item.append(Double.toString(licAtual));
        return item.toString();
    }
    public int getItemID(){
        return itemID;
    }
    
    public String toLineString(){
        StringBuilder item = new StringBuilder();
        item.append("ID: ");
         item.append(itemID);
        item.append(" Descricao: ");
        item.append(descricao);
        item.append(" Data fim: ");
        
        item.append(this.getDataFim());
        item.append(" Vendedor: ");
        item.append(vendedor.getUsername());
        item.append("\n");
        return item.toString();
    }

    public TreeMap<Double, Licitacao> getLicitacoes() {
        return licitacoes;
    }
}
