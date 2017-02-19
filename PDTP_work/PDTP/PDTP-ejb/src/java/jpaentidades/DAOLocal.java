/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpaentidades;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Local;
import javax.persistence.EntityManager;

/**
 *
 * @author eugenio
 */
@Local
public interface DAOLocal {

    EntityManager getEntityManager();

    public void create(Object entity);

    void createWithCommit(Object entity);

    public void edit(Object entity);

    public void editWithCommit(Object entity);
    
    public void remove(Object entity);

    public Object find(Class s,Object id);

    public List<Object> findAll(Class s);

    public List<Object> findByNamedQuery(Class s, String nameQuery, String nameParam, Object valeu);

    public List<Object> findRange(Class s,int[] range);

    public int count(Class s);

}
