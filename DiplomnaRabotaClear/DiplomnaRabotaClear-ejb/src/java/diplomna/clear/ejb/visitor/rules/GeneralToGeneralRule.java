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
public class GeneralToGeneralRule extends RuleA{
    private String previousChar;
    private String waitForChar;
    private String changeFirstTo;
    private String changeSecondTo;
    private Encoding encoding;
    
    public GeneralToGeneralRule(String firstChar,String waitFor,String changeFirstTo,String changeSecondTo,String lang) throws Exception{
        
        encoding=UTF8Encoding.getInstance(lang);
        language=lang;
                
        if((firstChar.length()<2)||(waitFor.length()<2)) {
            throw new Exception("wrong rule instanciation GeneralToGeneralRule len");
        }
        if(!isInputCorect(firstChar)){
            throw new Exception("wrong rule instanciation GeneralToGeneralRule InputCorect(first");
        }
        if(!isInputCorect(waitFor)){
            throw new Exception("wrong rule instanciation GeneralToGeneralRule InputCorect(waitFor");    
        }
        if((!isInputCorect(changeFirstTo))&&(changeFirstTo!="")){
            throw new Exception("wrong rule instanciation GeneralToGeneralRule InputCorect(changeFirstTo");    
        }
        if((!isInputCorect(changeSecondTo))&&(changeSecondTo!="")){
            throw new Exception("wrong rule instanciation GeneralToGeneralRule InputCorect(changeSecond");    
        }
        
        this.changeSecondTo=changeSecondTo;
        this.changeFirstTo=changeFirstTo;
        this.waitForChar=waitFor;
        this.previousChar=firstChar;
    
    }
    
    
    @Override
    public boolean isThisMyRule(char previousSymbol, char currSymbol)throws Exception{
        String prevSymbolType,currSymbolType;
        
        prevSymbolType=checkGeneralInput(previousSymbol);
        currSymbolType=checkGeneralInput(currSymbol);
        
        if((previousChar.length()==3)&&(prevSymbolType.length()>1)){
            prevSymbolType=prevSymbolType.substring(1);
        }
        if((waitForChar.length()==3)&&(currSymbolType.length()>1)){
            currSymbolType=currSymbolType.substring(1);
        }
        if((prevSymbolType==previousChar)&&(currSymbolType==waitForChar)){
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
        //"SVOW" "SCOSS" => sounded
        if(changeFirstTo.charAt(0)=='S'){
            return encoding.getSoundedChar(first)+"";
        }else if(changeFirstTo.charAt(0)=='M'){
        //"MCOSS" "MVOW"
            return encoding.getMuttedChar(first)+"";
       }else{
            return first+"";
       }
    }

    @Override
    public String howToChangeSecond(char second) {
    
        if(changeSecondTo==""){
            return second+"";
        }
        //"SVOW" "SCOSS" => sounded
        if(changeSecondTo.charAt(0)=='S'){
            return encoding.getSoundedChar(second)+"";
        }else if(changeSecondTo.charAt(0)=='M'){
        //"MCOSS" "MVOW"
            return encoding.getMuttedChar(second)+"";
       }else{
            return second+"";
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
