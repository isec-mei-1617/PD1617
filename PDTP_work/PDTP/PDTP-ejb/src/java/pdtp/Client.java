
package pdtp;

import java.util.ArrayList;
import javax.ejb.EJB;
import javax.ejb.Stateful;

@Stateful
public class Client implements ClientRemote {

    String myName;
    
    @EJB LeiloeiraLocal leiloeira;
    @Override
    public boolean LoginUtilizador(String username, String password) {
        if (myName != null)
            return false;
        if (leiloeira.loginUtilizador(username,password)){
            myName = username;
            return true;
        }
        return false;
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    @Override
    public int getMyScore() {
        return leiloeira.getMyScore(myName);
    }

    @Override
    public ArrayList<String> getHiScores() {
        return null;
    }

    @Override
    public int getMyAttempts() {
        return leiloeira.getMyAttempts(myName);
    }

    @Override
    public TryResult tryNumber(int numero) {
        if (myName == null)
            return TryResult.NoName; // testado no singleton
        
        return leiloeira.tryNumber(myName,numero);
    }

    @Override
    public boolean logOff() {
        myName = null;
        return leiloeira.logOff(myName); // Singleeton testa MyName == null
    }

    @Override
    public void test() {
    }

    @Override
    public boolean inscreveUtilizador(String nome, String morada, String username, String password) {
        return leiloeira.registaUtilizador(nome, morada, username, password);
    }

    @Override
    public boolean existeUsername(String username) {
        return leiloeira.existeUtilizador(username);
    }
    
}
