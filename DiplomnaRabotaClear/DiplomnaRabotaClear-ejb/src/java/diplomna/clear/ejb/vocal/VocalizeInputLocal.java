/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diplomna.clear.ejb.vocal;

import diplomna.clear.tools.token.TokenI;
import javax.ejb.Local;

/**
 *
 * @author Maria Shindarova
 */
@Local
public interface VocalizeInputLocal {
    public byte[] vocalize(String transcription,String language);//TokenI currToken,String language);
  
}
