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
public class CharToGeneralRule extends RuleA{

    private char previousChar;
    private String waitForChar;
    private String changeFirstTo;
    private String changeSecondTo;
    private Encoding encoding;
    
    public CharToGeneralRule(char firstChar,String waitFor,String changeFirstTo,String changeSecondTos,String lang) throws Exception{

        encoding=UTF8Encoding.getInstance(lang);
        language=lang;
        
        if(waitFor.length()<2) {
            throw new Exception("wrong rule instanciation CharToGeneralRule waitFor.length");
        }
        if(!isInputCorect(waitFor)){
            throw new Exception("wrong rule instanciation CharToGeneralRule InputCorect(waitFor");
        }
        if(!encoding.checkIfChar(firstChar)){
            throw new Exception("wrong rule instanciation CharToGeneralRule checkIfChar(first");
        }
        
        if((!isInputCorect(changeSecondTo))&&(changeSecondTo!="")){
            throw new Exception("wrong rule instanciation CharToGeneralRule inputCorect(changeSecondTo");
        }
        this.changeSecondTo=changeSecondTo;
        this.changeFirstTo=changeFirstTo;
        this.previousChar=firstChar;
        this.waitForChar=waitFor;
        
    }
    @Override
    public boolean isThisMyRule(char previousSymbol, char currSymbol) throws Exception {
        String currSymbolType;
        currSymbolType=checkGeneralInput(currSymbol);
        
        if((waitForChar.length()==3)&&(currSymbolType.length()>1)){
            currSymbolType=currSymbolType.substring(1);
        }
        
        if((previousSymbol==previousChar)&&(currSymbolType==waitForChar)){
            return true;
        }
        return false;
    }

    @Override
    public boolean isThisMyRule(char firstSymbol) throws Exception {
        if(firstSymbol==previousChar){
            return true;
        }
        return false;
    }

    @Override
    public String howToChangeFirst(char first) {
        return changeFirstTo;
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
}
