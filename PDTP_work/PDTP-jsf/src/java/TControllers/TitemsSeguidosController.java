package TControllers;

import autenticacao.Util;
import beans.ClientUtilizadorRemote;
import beans.SessionException;
import jpafacades.TitemsSeguidosFacade;
import jpaentidades.TitemsSeguidos;
import beans.util.JsfUtil;
import beans.util.PaginationHelper;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;
import jpaentidades.TItens;

@Named("titemsSeguidosController")
@SessionScoped
public class TitemsSeguidosController implements Serializable {

    private ClientUtilizadorRemote remoteSession;
     
     @PostConstruct
    public void init() {
        HttpSession session = Util.getSession();
        System.out.println("-----SESSAO "+session.getAttribute("sessaoUser"));
        remoteSession = (ClientUtilizadorRemote) session.getAttribute("sessaoUser");
    }
    
    
    private TitemsSeguidos current;
    private DataModel items = null;
     private DataModel itemsobj = null;
    @EJB
    private jpafacades.TitemsSeguidosFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public TitemsSeguidosController() {
    }

    public TitemsSeguidos getSelected() {
        if (current == null) {
            current = new TitemsSeguidos();
            selectedItemIndex = -1;
        }
        return current;
    }

    private TitemsSeguidosFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }

            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (TitemsSeguidos) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new TitemsSeguidos();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleItenstudo").getString("TitemsSeguidosCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleItenstudo").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (TitemsSeguidos) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleItenstudo").getString("TitemsSeguidosUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleItenstudo").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (TitemsSeguidos) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleItenstudo").getString("TitemsSeguidosDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleItenstudo").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }
    
    public DataModel getItemsobj() {
        if (itemsobj == null) {
           PaginationHelper paginationobj = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override               
                public DataModel createPageDataModel() {
                    try {
                            //return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                         List<Object> itenseguidos = remoteSession.getItensSeguidosObj();
                         for (Object o: itenseguidos){
                             System.out.println("obj---"+o);
                         }
                            return new ListDataModel( (List<TItens>)(List<?>) itenseguidos);
                           // return itemsseg;
                    } catch (SessionException ex) {
                        Logger.getLogger(TitemsSeguidosController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return null;
                }
            };
            
            itemsobj = paginationobj.createPageDataModel();
        }
        return itemsobj;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public TitemsSeguidos getTitemsSeguidos(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = TitemsSeguidos.class)
    public static class TitemsSeguidosControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TitemsSeguidosController controller = (TitemsSeguidosController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "titemsSeguidosController");
            return controller.getTitemsSeguidos(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof TitemsSeguidos) {
                TitemsSeguidos o = (TitemsSeguidos) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + TitemsSeguidos.class.getName());
            }
        }

    }

}
