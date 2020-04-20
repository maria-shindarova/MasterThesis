/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diplomna.clear.ejb.parse;

import diplomna.clear.tools.token.TokenI;
import java.io.File;
import java.util.ArrayList;
import javax.ejb.Local;

/**
 *
 * @author Maria Shindarova
 */
@Local
public interface ParserLocal {
    public String parserAction(ArrayList<TokenI> tokens,String language,boolean test);
}
