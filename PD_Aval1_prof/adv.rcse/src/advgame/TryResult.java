/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package advgame;

/**
 *
 * @author diogo
 */
public enum TryResult {
    TooBig("Demasiado grande"),
    TooSmall("Demasiado pequeno"),
    Right("Acertaste"),
    NewNumber("Novo numero"),
    NoName("Falta fornecer o nome"),
    Error("Ocorreu um erro qualquer"),
    NotLogged("Nao estas logado (timeout?)");
    private String msg;
    TryResult(String s) { msg = s;};
    public String msg() {return msg;}
    
    
}
