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
 *@author Maria Shindarova
 */
public class CharToCharRule extends RuleA{
    private char previousChar;
    private char waitForChar;
    private String changeFirstTo;
    private String changeSecondTo;
    private Encoding encoding;
    
    public CharToCharRule(char firstChar,char waitFor,String changeFirstTo,String changeSecondTo,String lang) throws Exception{
        
        encoding=UTF8Encoding.getInstance(lang);
        language=lang;
        
        if(!encoding.checkIfChar(firstChar)){
            throw new Exception("wrong rule instanciation CharToCharRule checkIfChar(first");
        }
        if(!encoding.checkIfChar(waitFor)){
            throw new Exception("wrong rule instanciation CharToCharRule checkIfChar(waitFor");
        }
        this.changeFirstTo=changeFirstTo;
        this.waitForChar=waitFor;
        this.changeSecondTo=changeSecondTo;
        this.previousChar=firstChar;
    }
    
    @Override
    public boolean isThisMyRule(char previousSymbol, char currSymbol) throws Exception {
        if((previousChar==previousSymbol)&&(waitForChar==currSymbol)){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean isThisMyRule(char firstSymbol) throws Exception {
        if(previousChar==firstSymbol){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public String howToChangeFirst(char first) {
        return changeFirstTo;
    }

    @Override
    public String howToChangeSecond(char second) {
        return changeSecondTo;
    }
}
