/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diplomna.clear.ejb.facade;

import diplomna.clear.tools.token.TokenI;
import java.util.ArrayList;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.jws.WebService;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author Misha
 */
@WebService(serviceName = "Vocalize")
@Stateful
public class Vocalize {

    @EJB
    private FacadeLocal ejbRef;// Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Web Service Operation")

    @WebMethod(operationName = "processInstructions")
    public String processInstructions(@WebParam(name = "inputText") String inputText) throws Exception {
        return ejbRef.processInstructions(inputText);
    }

    @WebMethod(operationName = "testLexNormTranskr")
    public ArrayList<TokenI> testLexNormTranskr(@WebParam(name = "inputText") String inputText) throws Exception {
        return ejbRef.testLexNormTranskr(inputText);
    }

    @WebMethod(operationName = "testAllWithoutVocal")
    public ArrayList<TokenI> testAllWithoutVocal(@WebParam(name = "inputText") String inputText) throws Exception {
        return ejbRef.testAllWithoutVocal(inputText);
    }
    
}
