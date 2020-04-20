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
public class CharToManyChars extends RuleA{
    
    private char previousChar;
    private char[] waitForChar;
    private char changeFirstTo;
    private String changeSecondTo;
    private Encoding encoding;
    
    public CharToManyChars (char firstChar,char[]next,char changeFirstTo,String changeSecondTo,String lang) throws Exception{

        encoding=UTF8Encoding.getInstance(lang);
        language=lang;
        
        if(!encoding.checkIfChar(firstChar)){
            throw new Exception("wrong rule instanciation CharToManyChars checkIfChar(firstChar");    
        }
         
        for(int i=0;i<next.length;i++){
             if(!encoding.checkIfChar(next[i])){
                throw new Exception("wrong rule instanciation CharToManyChars checkIfChar(second");    
            }
        }
        
        if(!encoding.checkIfChar(changeFirstTo)){
            throw new Exception("wrong rule instanciation CharToManyChars checkIfChar(changeFirstTo");    
        }
        
        this.previousChar=firstChar;
        waitForChar=next;
        this.changeFirstTo=changeFirstTo;
        this.changeSecondTo=changeSecondTo;
    }

    @Override
    public boolean isThisMyRule(char previousSymbol, char currSymbol) throws Exception {
        
        if(previousChar!=previousSymbol){
             return false;
        }
        
        for(int i=0;i<waitForChar.length;i++){
            if(waitForChar[i]==currSymbol){
                return true;
            }
        }
        
        return false;
    }

    @Override
    public boolean isThisMyRule(char firstSymbol) throws Exception {
        
        if(previousChar==firstSymbol){
             return true;
        }
        
        return false;
    }

    @Override
    public String howToChangeFirst(char first) {
        return changeFirstTo+"";
    }

    @Override
    public String howToChangeSecond(char second) {
        return changeSecondTo;
    }
}