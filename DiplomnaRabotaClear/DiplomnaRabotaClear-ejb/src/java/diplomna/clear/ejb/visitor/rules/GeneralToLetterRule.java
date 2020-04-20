/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diplomna.clear.ejb.visitor.rules;

import diplomna.clear.tools.Encoding;
import diplomna.clear.tools.UTF8Encoding;

/**
 *
 * @author Maria Shindarova
 */
public class GeneralToLetterRule  extends RuleA{
    private String previousChar;
    private char waitForChar;
    private String changeFirstTo;
    private String changeSecondTo;
    private Encoding encoding;
    
    public GeneralToLetterRule(String firstChar,char waitFor,String changeFirstTo,String changeSecondTo,String lang) throws Exception{
  
        encoding=UTF8Encoding.getInstance(lang);
        language=lang;

        if(firstChar.length()<2) {
            throw new Exception("wrong rule instanciation GeneralToLetterRule len");
        }
        if(!isInputCorect(firstChar)){
            throw new Exception("wrong rule instanciation GeneralToLetterRule incorect inp");
        }
        if(!encoding.checkIfChar(waitFor)){
            throw new Exception("wrong rule instanciation GeneralToLetterRule not char "+waitFor);
        }
        if((!isInputCorect(changeFirstTo))&&(changeFirstTo!="")){
            throw new Exception("wrong rule instanciation GeneralToLetterRule changeFirstTo");
        }
        
        this.changeSecondTo=changeSecondTo;
        this.previousChar=firstChar;
        this.waitForChar=waitFor;
        this.changeFirstTo=changeFirstTo;
    }
    
    
    @Override
    public boolean isThisMyRule(char previousSymbol, char currSymbol)throws Exception{
        String prevSymbolType;
        prevSymbolType=checkGeneralInput(previousSymbol);
        //
        if((previousChar.length()==3)&&(prevSymbolType.length()>1)){
            prevSymbolType=prevSymbolType.substring(1);
        }
        
        if((prevSymbolType==previousChar)&&(currSymbol==waitForChar)){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public String howToChangeFirst(char first) {
        
        if(changeFirstTo==""){
            return first+"";
        }
        //"SVOW" "SCOS" => sounded
        if(changeFirstTo.charAt(0)=='S'){
            return encoding.getSoundedChar(first)+"";
        }else if(changeFirstTo.charAt(0)=='M'){
        //"MCOS" "MVOW" =>muted
            return encoding.getMuttedChar(first)+"";
       }else{
            return first+"";
       }
    }

    @Override
    public String howToChangeSecond(char second) {
    
        if(changeSecondTo==""){
            return second+"";
        }else{
            return changeSecondTo;
        }        
    }

    @Override
    public boolean isThisMyRule(char firstSymbol) throws Exception {
        String firstSymbolType;
        firstSymbolType=checkGeneralInput(firstSymbol);
        
        if((previousChar.length()==3)&&(firstSymbolType.length()>1)){
            firstSymbolType=firstSymbolType.substring(1);
        }
        if(firstSymbolType==previousChar){
            return true;
        }else{
            return false;
        }
    }
}