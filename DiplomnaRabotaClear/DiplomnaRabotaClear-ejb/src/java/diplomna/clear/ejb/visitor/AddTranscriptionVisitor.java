package diplomna.clear.ejb.visitor;

import diplomna.clear.ejb.visitor.rules.RuleI;
import diplomna.clear.ejb.visitor.rules.RulesAll;
import diplomna.clear.tools.Encoding;
import diplomna.clear.tools.UTF8Encoding;
import diplomna.clear.tools.token.Punctuation;
import diplomna.clear.tools.token.Space;
import diplomna.clear.tools.token.TokenI;
import diplomna.clear.tools.token.Word;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
/**
 *
 * @author Maria Shindarova
 */
public class AddTranscriptionVisitor implements VisitorI {

    private RulesAll allRulls;
    
    private RuleI expectedCharRule;
    private ArrayList<RuleI>expectedCharRules;
    
    private StringBuilder transcribtion=null;        
    private Word previousWord;
    private Character previousLetter;
    private String prevLetterChanged;
    private Encoding encoding=null;
    
    public AddTranscriptionVisitor(String lang) throws Exception{
        initialize(lang);
    }
    
    
    public void addCurrSymbol(char currChar,String lang,TokenI currToken) throws Exception{
        String typeOfToken;
        
        if(currToken.getWaitingNormalization()){
            return;
        }
        
        typeOfToken=currToken.getType();
  
        //if(encoding.checkIfChar(currChar)){
            //process Word, normalized Number and Math symbol 
        if(typeOfToken.equals(Word.tokenType)){
                processChar(currChar,currToken);
  
                expectedCharRule=allRulls.getApplicableRulesByLetter(currChar, false);
                expectedCharRules=allRulls.getApplicableRulesGeneral(currChar, false);


        }else if(typeOfToken.equals(Space.tokenType)||typeOfToken.equals(Punctuation.tokenType)){
            processOther(currChar,currToken);
        }else{
           throw new Exception("processOther() not Space or punctoation and cuurent is not char type of token is "+typeOfToken+" symbol is "+currChar);
        }

    }
      
    private void processChar(char currChar,TokenI currToken)throws Exception{    
        boolean ruleUsedFirstFlg =false,ruleUsedSecondFlg =false;
        
        if(expectedCharRule!=null){
            if(expectedCharRule.isThisMyRule(previousLetter, currChar)){
                ruleUsedFirstFlg =isThisTheRuleFirst(expectedCharRule,currChar,currToken);
                ruleUsedSecondFlg=isThisTheRuleSecond(expectedCharRule,currChar);
            }
        }

        if(expectedCharRules!=null){//&&(!ruleUsedFlg)){
          int i=0;
          
            while((i<expectedCharRules.size())&&(!(ruleUsedFirstFlg&&ruleUsedSecondFlg))){//&&(!ruleUsedFlg)){
                if(expectedCharRules.get(i).isThisMyRule(previousLetter, currChar)){
                    if(!ruleUsedFirstFlg){
                       ruleUsedFirstFlg =isThisTheRuleFirst(expectedCharRules.get(i),currChar,currToken);
                    }
                    if(!ruleUsedSecondFlg){
                        ruleUsedSecondFlg=isThisTheRuleSecond(expectedCharRules.get(i),currChar);
                    }
                }
                i++;
          }  
        }
        if((!ruleUsedFirstFlg)&&(currToken.length()>1)){
            transcribtion.append(prevLetterChanged);
           
        }

        if(!ruleUsedSecondFlg){
            previousLetter=currChar;
            prevLetterChanged=currChar+"";
        }
       
    }
    
    public void addSymbols(StringBuilder allSymbols,String lang,TokenI currToken) throws Exception{
        
        for(int i =0;i<allSymbols.length();i++){
            addCurrSymbol(allSymbols.charAt(i),lang,currToken);
        }
    }
    
    private boolean isThisTheRuleFirst(RuleI chekThisRule,char currChar,TokenI currToken){
        String changeFirst; boolean ruleUsed=false;
        changeFirst=chekThisRule.howToChangeFirst(previousLetter);
         
        if(currToken.length()<=1){
           if((changeFirst!="")&&(!changeFirst.equals(previousLetter))){
                previousWord.exchangeLastLeterTranscription(changeFirst);
                ruleUsed=true;
         
           }
         
        }else{
            if((changeFirst=="")||(changeFirst.equals(previousLetter))){
               transcribtion.append(prevLetterChanged);
            }else{
                transcribtion.append(changeFirst);
                ruleUsed=true;
            }
        }
        return ruleUsed;
    }
    
    private boolean isThisTheRuleSecond(RuleI chekThisRule,char currChar){
        String changeSecond;boolean ruleUsed=false;
        
        changeSecond=chekThisRule.howToChangeSecond(currChar);
        
        if((changeSecond=="")||(changeSecond.equals(currChar+""))){
//            previousLetter=currChar;
//            prevLetterChanged=currChar+"";
        }else{
            previousLetter=changeSecond.charAt(changeSecond.length()-1);
            prevLetterChanged=changeSecond;
            ruleUsed=true;
        }
        return ruleUsed;
  }
   
    @Override
    ////Part Of VISITOR Design Pattern
    public void visit(TokenI currToken) throws Exception {
        boolean charIsIt=false;

        if((currToken.length()>0)&&(currToken.getType()=="word")){
            charIsIt=true;//encoding.checkIfChar(currToken.getSymbolAt(0));
        }

        if((prevLetterChanged.length()>0)&&(charIsIt)){
            transcribtion.append(prevLetterChanged);
        }
        
        currToken.addTrancribtion(transcribtion);
        if(encoding.checkIfChar(currToken.getSymbolAt(0))){
          currToken.addLengthLastLetter(prevLetterChanged.length());
        }
        prepareForNextToken(currToken);
    }
    
    private void processOther(char currChar,TokenI currToken){
    
        if(encoding.checkIfEndOfSentenceSymb(currChar)){
            transcribtion.append("20Pause");
        }else{
            transcribtion.append("10Pause");
        }
    }
    
    public void prepareForNextToken(TokenI currToken){
    
        if((currToken==null)){
            previousWord =null;
            previousLetter=' ';
            prevLetterChanged="";
            expectedCharRule=null;
            expectedCharRules=allRulls.getApplicableRulesGeneral(' ', true);
            transcribtion=new StringBuilder();
            return;
        }
        if(currToken.getType()=="word"){
            previousWord =(Word) currToken;
            previousLetter=currToken.getSymbolAt(currToken.length()-1);
            prevLetterChanged=previousLetter+"";
            expectedCharRule=null;
            expectedCharRules=allRulls.getApplicableRulesGeneral(previousLetter, true);
        }
        
        transcribtion=new StringBuilder();//.delete(0, transcribtion.length());
       
    }

    public void waitingNormalization(TokenI currToken){
        prepareForNextToken(currToken);
    }    

    private void initialize(String languge) throws Exception{
        encoding=UTF8Encoding.getInstance(languge);
        allRulls = new RulesAll(languge);
        transcribtion=new StringBuilder();        
        expectedCharRule=null;
        expectedCharRules=allRulls.getApplicableRulesGeneral(' ', true);
        previousWord =null;
        
        previousLetter=' ';
        prevLetterChanged="";
    }
}