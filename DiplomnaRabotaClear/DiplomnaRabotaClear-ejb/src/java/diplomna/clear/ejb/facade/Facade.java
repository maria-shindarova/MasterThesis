package diplomna.clear.ejb.facade;

import diplomna.clear.ejb.lex.LexerLocal;
import diplomna.clear.tools.token.TokenI;
import java.util.ArrayList;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import diplomna.clear.ejb.parse.ParserLocal;

/**
 *
 * @author Maria Shindarova
 */
@Stateful
public class Facade implements FacadeLocal {
    @EJB
    private LexerLocal lexer;
    @EJB
    private ParserLocal parser;

    @Override
   public String processInstructions(String inputText)throws Exception{
       String language = "BG";
       ArrayList<TokenI> tokenizedInput;//=new ArrayList<>();
       String vocalized=null;
       
       tokenizedInput=lexer.lexerAction(inputText,language);
       vocalized=parser.parserAction(tokenizedInput,language,false);
              
       return vocalized;
   }
    
   public ArrayList<TokenI> testAllWithoutVocal(String inputText)throws Exception{
	ArrayList<TokenI> tokenizedInput;
       
       tokenizedInput=lexer.lexerAction(inputText,"BG");
       String vocalized=parser.parserAction(tokenizedInput,"BG",true);
       return tokenizedInput;
   }
   
   public ArrayList<TokenI> testLexNormTranskr(String inputText)throws Exception{
       
       ArrayList<TokenI> tokenizedInput;
       
       tokenizedInput=lexer.lexerAction(inputText,"BG");
       
       return tokenizedInput;
   }
}
