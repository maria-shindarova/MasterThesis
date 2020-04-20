package diplomna.clear.ejb.lex;

import diplomna.clear.ejb.normalization.NormalizeWordsLocal;
import diplomna.clear.ejb.visitor.AddTranscriptionVisitor;
import diplomna.clear.tools.Encoding;
import diplomna.clear.tools.FSM;
import diplomna.clear.tools.UTF8Encoding;
import diplomna.clear.tools.token.Punctuation;
import diplomna.clear.tools.token.Space;
import diplomna.clear.tools.token.TokenI;
import diplomna.clear.tools.token.Word;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import static javax.ejb.TransactionAttributeType.REQUIRED;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
/**
 *
 * @author Maria Shindarova
 */
@Stateful
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(value = REQUIRED)

public class Lexer implements LexerLocal {
    @EJB
    NormalizeWordsLocal normalize;
    
    private AddTranscriptionVisitor addTransciption;
    private Encoding encoding;
    private ArrayList<TokenI> tokensInText;
    private FSM cifriFSM;
    private FSM bukviMalkiFSM;
    private FSM bukviGolemiFSM;
    private StringBuilder waitIfNewToken;
    private int state;
    private String language;

///call from facade
    public ArrayList<TokenI> lexerAction(String inputText,String language) throws Exception{
        int currChar=0;
        TokenI currToken=null;
        String typeOfPrevSymbols = "",typeOfCurrSymbol="";
        this.language=language;
        
        if(state!=1){
            initialization();
        }
        if(encoding==null){
            initializationWithParam();
        }

        if((inputText==null)||(inputText.length()<=0)||(encoding==null)){
             throw new Exception("=====this input is not valid ");
        }
        
        if(tokensInText.size()>0){
            secondCallInitialization();
        }              
        
        typeOfPrevSymbols=encoding.checkSymbolType(inputText.charAt(0));
        currToken = tokenFactory(typeOfPrevSymbols, null);
        tokensInText.add(currToken);
        
        for(int i =0;i<inputText.length();i++){
            currChar=-1;typeOfCurrSymbol=""; 
            
            currChar=(int)inputText.charAt(i);
            typeOfCurrSymbol=encoding.checkSymbolType(currChar);

            if(encoding.checkIfSymbolIsAcceptable(typeOfCurrSymbol)){
        
                if((typeOfPrevSymbols.equals(typeOfCurrSymbol))||
                    (typeOfCurrSymbol.charAt(0)==typeOfPrevSymbols.charAt(0))){                   
                    // add current symbol to current token-symbol is of the same type as previous       
                    currToken=addSameTypeSymbol(currToken,currChar,typeOfCurrSymbol);
                }else{
                    // different type of symbol from the previous- could be:new token, eos, abreviat, 12/12/2000, 1.1, 1,1, съкращение с точка (doc. X.Xiksov t.nar) съкращение c tire (d-r)
                    currToken=checkSymbolFSM(currToken,(char)currChar,typeOfPrevSymbols);
                }
                if((currToken.length()==1)&&(state==1)){
                  typeOfPrevSymbols =typeOfCurrSymbol;
                }
             
             //addTransciption.addCurrSymbol((char)currChar, language,currToken);

          }// end if symbol acceptable else any non cyrilic letter, or symbol
        }//end loop
        
        addTranscriptionNormalize(currToken,false);
        
        return tokensInText;
    
    }
    
    private TokenI checkSymbolFSM(TokenI currToken,char currSymbol,String typeOfSymbolPrev) throws Exception{
// new token, eos, 12/12/2000, 1.1, 1,1, abreviat, съкращение с точка (doc. X.Xiksov t.nar)
//съкращение c tire (d-r г-н)
        
        TokenI tokenHLP=currToken;
        Integer newState=null;
        int stateApproved=0;
     
        if((typeOfSymbolPrev.equals("letter"))&&(!bukviMalkiFSM.isStateApproved(state))){
        
            newState=bukviMalkiFSM.nextState(state, currSymbol);
            stateApproved=bukviMalkiFSM.getStateApproved();
        
        }else if((typeOfSymbolPrev=="letterCap")&&(currToken.length()==1)
                                &&(!bukviGolemiFSM.isStateApproved(state))){
        
            newState=bukviGolemiFSM.nextState(state, currSymbol);
            stateApproved=bukviGolemiFSM.getStateApproved();
            
        }else if((typeOfSymbolPrev.equals("number"))&&(!cifriFSM.isStateApproved(state))){
        
            newState=cifriFSM.nextState(state, currSymbol);
            stateApproved=cifriFSM.getStateApproved();
            
        }else{
           newState=null; 
        }

        //newState=null is result from FSM lookup or preveous process
        //this chars are not in the language of FSM
        if(newState==null){
           state=1;
            /// create tokens from waiting chars
            if(waitIfNewToken.length()>0){
                waitIfNewToken.append(currSymbol);
                tokenHLP =addWaitingTokens(currToken,waitIfNewToken,false);
                waitIfNewToken.delete(0, waitIfNewToken.length());
            }else{
                tokenHLP=createNewToken(currSymbol,currToken,false);
                if(!tokenHLP.getWaitingNormalization()){
                    addTransciption.addCurrSymbol(currSymbol,language,tokenHLP);
                }
            }
            return tokenHLP;
        }else if(stateApproved==newState){
            //this chars are in the language of this FSM - 
            currToken.addAll(waitIfNewToken.append(currSymbol));
            //normalization needed

                addTransciption.waitingNormalization(currToken);

            waitIfNewToken.delete(0, waitIfNewToken.length());
            state=1;
        }else{
            //wait to find out are thouse chars in the language of FSM
            waitIfNewToken.append(currSymbol);
            state=newState;
        }
        return tokenHLP;
  }
  private void addTranscriptionNormalize(TokenI currToken,boolean alreadyNormalized) throws Exception{
         String normalized=""; 
////nar i t pak 6te se normalizirat
        if((currToken.getType()=="word")&&(!currToken.getWaitingNormalization())
                &&(!alreadyNormalized)){
        //ako e word proveri v baza
        //ako ia niama currToken.nowWaitNormalization();
        }
        
        if((currToken.getWaitingNormalization())&&(!alreadyNormalized)){
            normalized=normalize.normalize(currToken, language);
            
            if(tokensInText.size()>0){
                tokensInText.remove(tokensInText.size()-1);
            }
            
            currToken=addWaitingTokens(null,new StringBuilder(normalized),true);
            
        }else{
            currToken.accept(addTransciption);
 
        }
                //normalize, finish transcript
 
  } 
  private TokenI createNewToken(char currSymbol,TokenI currToken,boolean alreadyNormalized) throws Exception{
       TokenI newToken;
       
       if(currToken!=null){
           addTranscriptionNormalize(currToken,alreadyNormalized);
       }
            try {
              newToken = tokenFactory(encoding.checkSymbolType(currSymbol),currSymbol);
           } catch (Exception ex) {
              Logger.getLogger(Lexer.class.getName()).log(Level.SEVERE, null, ex);
              return null;
           }
           state=1;
           tokensInText.add(newToken);
        
        
        return newToken;
    }

    private TokenI tokenFactory(String type,Character currSymbol) {
	/// alternativa na switch
        boolean endOfSentence=false;
	
        if(type=="letter") {
            if(currSymbol==null){
                return new Word(language);
            }else{
                return new Word(false,currSymbol,language);
            }
        }else if(type=="letterCAP"){
            if(currSymbol==null){
                return new Word(language);
            }else{
                return new Word(true,currSymbol,language);
            }
	}else if(type=="number"){
            if(currSymbol==null){
                return new diplomna.clear.tools.token.Number();
            }else{
                return new diplomna.clear.tools.token.Number(currSymbol);
            }
	}else if(type=="space"){
            if(currSymbol==null){
                return new Space();
            }else{
                return new Space(currSymbol);
            }
	}else if(type=="punctoation"){
            if(currSymbol==null){
                return new Punctuation();
            }else{
                if(encoding.checkIfEndOfSentenceSymb(currSymbol)){
                    endOfSentence=true;
                }
                if(encoding.punctoationToWord(currSymbol)!=""){
                    Punctuation hlp =new Punctuation(endOfSentence,currSymbol);
                    hlp.nowWaitNormalization();
                    return hlp;
                }else{
                    return new Punctuation(endOfSentence,currSymbol);
                }
            }
	}else if(type=="ERR"){
            return null;
	}else {
            return null;
	}	   
   }
    
private TokenI addWaitingTokens(TokenI currToken,StringBuilder addThis,boolean alreadyNormalized) throws Exception{
    
    String typePrevChar="",typeCurrChar="";
    if(currToken!=null){
        typePrevChar =encoding.checkSymbolType(currToken.getSymbolAt(0));
    }else{
        typePrevChar ="No_type";
    }
    for(int i=0;i<addThis.length();i++){
        typeCurrChar=encoding.checkSymbolType(addThis.charAt(i));

        if((typeCurrChar==typePrevChar)||(typeCurrChar.charAt(0)==typePrevChar.charAt(0))){    
            
            currToken.add(addThis.charAt(i));
            if(!currToken.getWaitingNormalization()){
                addTransciption.addCurrSymbol(addThis.charAt(i),language,currToken);
            }
       }else{ 
            
            currToken =createNewToken(addThis.charAt(i),currToken,alreadyNormalized);

            if(!currToken.getWaitingNormalization()){
                addTransciption.addCurrSymbol(addThis.charAt(i),language,currToken);
            }
            typePrevChar=typeCurrChar;
        }
    }
    if(currToken.getTranscribtion()==null){
        addTranscriptionNormalize(currToken,true);
    }
    return currToken;
}
      
    private TokenI addSameTypeSymbol(TokenI currToken,int currChar,String typeOfCurrSymbol) throws Exception{
        
        TokenI newToken=currToken; 
        
        if(state>1){
            newToken=checkSymbolFSM(currToken,(char)currChar,typeOfCurrSymbol);//encoding.checkSymbolType(currChar));
        }else{
            currToken.add((char)currChar);
            if(!currToken.getWaitingNormalization()){
                addTransciption.addCurrSymbol((char)currChar, language,currToken);
            }
        }
         return newToken;
     }
    
    @PostConstruct
    private void initialization(){
        state=1;
        waitIfNewToken=new StringBuilder();
        tokensInText=new ArrayList();
        encoding=null;
    }
    
    private void initializationWithParam() throws Exception{
      
        encoding=UTF8Encoding.getInstance(language);
        setLanguage(language);
        addTransciption= new AddTranscriptionVisitor(language);
    }
    
    private void setLanguage(String language) throws Exception {
                    
      cifriFSM=new FSM(encoding.getFSMmaxStateCifri(),
                       encoding.getFSMsetCifri(),
                       language);
      
      bukviMalkiFSM = new FSM(encoding.getFSMmaxStateMalkiBukvi(),
                              encoding.getFSMsetMalkiBukvi(),
                              language);
      
      bukviGolemiFSM =new FSM(encoding.getFSMmaxStateGolemiBukvi(),
                              encoding.getFSMsetGolemiBukvi(),
                              language);
        
    }
    
    private void secondCallInitialization(){
        state=1;
        waitIfNewToken.delete(0, waitIfNewToken.length());
        tokensInText=new ArrayList();
        addTransciption.prepareForNextToken(null);
    }
}
