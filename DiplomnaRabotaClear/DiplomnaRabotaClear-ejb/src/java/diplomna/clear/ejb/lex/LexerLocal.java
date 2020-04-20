/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diplomna.clear.ejb.lex;

import diplomna.clear.tools.token.TokenI;
import java.util.ArrayList;
import javax.ejb.Local;

/**
 *
 * @author Maria Shindarova
 */
@Local
public interface LexerLocal {
     public ArrayList<TokenI> lexerAction(String inputText,String language) throws Exception;    
}
