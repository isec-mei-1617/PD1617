package pdtp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Schedule;
import javax.ejb.Singleton;

@Singleton
public class Leiloeira implements LeiloeiraLocal {

    HashMap<String, Utilizador> utilizadores = new HashMap<>();
    List<Leilao> leiloes = new ArrayList<>();

    Random rnd = new Random();
    int secretNum;

    public Leiloeira() {
        newNumber();
    }

    @Override
    public ArrayList<String> getHiScores() {
        Collection<Utilizador> todos = utilizadores.values();
        ArrayList<String> hisc = new ArrayList<>(); //pouco eficiente
        todos.forEach((j) -> {
            hisc.add(j.toString()); // ou so j
        });
        return hisc;
        // nao atualiza timetampo porque pode nem estar logado;
    }





    @Override
    public boolean existeUtilizador(String username) {
        if (username == null) {
            return false;
        }
        Utilizador j = utilizadores.get(username);
        if (j==null) // sera que nao fica null?
        {
            return false;
        }
        return true;
    }

    @Override
    public boolean registaUtilizador(String nome, String morada, String username, String password) {
        if (!existeUtilizador(username)) {
            utilizadores.put(username, new Utilizador(nome, morada, username, password));
            return true;
        }
        return false;
    }

    @Override
    public boolean loginUtilizador(String username, String password) {
        Utilizador j = utilizadores.get(username);
        if (j != null) {
            //ja existe
            if (j.getPassword().equalsIgnoreCase(password)) {
                if (j.isLogged()) // esta logado -Z nao deixa repetir user
                {
                    return false;
                } else {
                    j.setLogged();
                    j.setLastAction();
                    return true;
                }
            }
        }
        return false;
    }

   

    private void newNumber() {
        secretNum = rnd.nextInt(100) + 1;
        //avisa todos que o numero mudou
        Collection<Utilizador> todos = utilizadores.values();
        ArrayList<String> hisc = new ArrayList<>(); // pouco eficiente
        todos.forEach((j) -> {
            j.resetAdvised();
        });
    }

    @Override
    public boolean logOff(String username) {
        if (username == null) //quem?
        {
            return false;
        }
        Utilizador j = utilizadores.get(username);
        if (j == null) {
            return false; //nao conheco
        }
        if (!j.isLogged()) {
            return false;
        }
        j.resetLogged(); //unloga
        return true;
    }

    @Schedule(second = "*/5", minute = "*", hour = "*")
    public void checkInactivity() throws InterruptedException {
        long now = LocalDateTime.now()
                .toInstant(ZoneOffset.UTC).getEpochSecond();
        Collection<Utilizador> todos = utilizadores.values();
        for (Utilizador j : todos) {
            if (j.isLogged()) {
                if (j.fromLastActionFromNoew(now) > 60) {
                    j.resetLogged();
                }
            }
        }
    }

    @PostConstruct
    public void loadstate() {
        try (ObjectInputStream ois
                = new ObjectInputStream(
                        new BufferedInputStream(
                                new FileInputStream("/tmp/LeiloeiraUtilizadores")))) {
            utilizadores = (HashMap<String, Utilizador>) ois.readObject();
        } catch (Exception e) {
            //Utilizadors = fica com o objecto vazio criado no construtor
        }
    }

    @PreDestroy
    public void saveState() {
        try (ObjectOutputStream oos
                = new ObjectOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream("/tmp/LeiloeiraUtilizadores")))) {
            oos.writeObject(utilizadores);
        } catch (Exception e) {

        }
    }

    @Override
    public ArrayList getLogged() {
        ArrayList logados = new ArrayList<>();
        Collection<Utilizador> todos = utilizadores.values();
        for (Utilizador j : todos) {
            if (j.isLogged()) {
                logados.add(j.getUsername());
            }
        }
        return logados;
    }

}
