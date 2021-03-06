/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpaentidades;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import pdtp.VendaEstados;

/**
 *
 * @author diogo
 */
@Entity
@Table(name = "t_vendas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TVendas.findAll", query = "SELECT t FROM TVendas t")
    , @NamedQuery(name = "TVendas.findLast", query = "SELECT t FROM TVendas t ORDER BY t.data DESC")
    , @NamedQuery(name = "TVendas.findByVendaid", query = "SELECT t FROM TVendas t WHERE t.vendaid = :vendaid")
    , @NamedQuery(name = "TVendas.findByEstado", query = "SELECT t FROM TVendas t WHERE t.estado = :estado")
    , @NamedQuery(name = "TVendas.findByTipo", query = "SELECT t FROM TVendas t WHERE t.tipo = :tipo")
    , @NamedQuery(name = "TVendas.findByValor", query = "SELECT t FROM TVendas t WHERE t.valor = :valor")})
public class TVendas implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "vendaid")
    private Long vendaid;
    @Size(max = 255)
    @Column(name = "estado")
    private VendaEstados estado;
    @Size(max = 255)
    @Column(name = "tipo")
    private String tipo;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private Double valor;
    @JoinColumn(name = "item", referencedColumnName = "itemid")
    @ManyToOne
    private TItens item;
    @JoinColumn(name = "comprador", referencedColumnName = "username")
    @ManyToOne
    private TUtilizadores comprador;
    @OneToMany(mappedBy = "venda")
    private Collection<TItens> tItensCollection;
    @Column(name = "data")
    @Temporal(TemporalType.TIMESTAMP)
    private Date data;

    public TVendas() {
        this.data = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    }

    public TVendas(Long vendaid) {
        this.vendaid = vendaid;
        this.data = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    }

    public Long getVendaid() {
        return vendaid;
    }

    public void setVendaid(Long vendaid) {
        this.vendaid = vendaid;
    }

    public VendaEstados getEstado() {
        return estado;
    }

    public void setEstado(VendaEstados estado) {
        this.estado = estado;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public TItens getItem() {
        return item;
    }

    public void setItem(TItens item) {
        this.item = item;
    }

    public TUtilizadores getComprador() {
        return comprador;
    }

    public void setComprador(TUtilizadores comprador) {
        this.comprador = comprador;
    }

    @XmlTransient
    public Collection<TItens> getTItensCollection() {
        return tItensCollection;
    }

    public void setTItensCollection(Collection<TItens> tItensCollection) {
        this.tItensCollection = tItensCollection;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (vendaid != null ? vendaid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TVendas)) {
            return false;
        }
        TVendas other = (TVendas) object;
        if ((this.vendaid == null && other.vendaid != null) || (this.vendaid != null && !this.vendaid.equals(other.vendaid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpaentidades.TVendas[ vendaid=" + vendaid + " ]";
    }

}
