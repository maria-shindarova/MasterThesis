/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diplomna.clear.tools.token;

import diplomna.clear.tools.Encoding;
import diplomna.clear.tools.UTF8Encoding;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maria Shindarova
 */
public class Word extends TokenA{

    private int isItCap;
    private Encoding encoding;
    public static String tokenType="word";
    
    public Word(String lang){
        value = new StringBuilder();
        waitingNormalization=false;
        //this.tokenType="word";
        try {
            encoding=UTF8Encoding.getInstance(lang);
        } catch (Exception ex) {
            Logger.getLogger(Word.class.getName()).log(Level.SEVERE, null, ex+" ------worng language----- ");
        }
    }
    
    public Word(boolean isItCap,char currSymbol,String lang){
        value = new StringBuilder();
        value.append(currSymbol);
        waitingNormalization=false;
        if(isItCap){
            this.isItCap++;
        }
        //this.tokenType="word";
        try {
            encoding=UTF8Encoding.getInstance(lang);
        } catch (Exception ex) {
            Logger.getLogger(Word.class.getName()).log(Level.SEVERE, null, ex+" ------worng language----- ");
        }
    }
    @Override
    public int getCapitalLetterCNT(){
        return isItCap;
    }
    @Override
    public void add(char nextChar) {
        value.append(nextChar);
        if(encoding.checkIfCapLetter(nextChar)){
            isItCap++;
            if(value.length()>1){
                waitingNormalization=true;
            }
        }
        if((isItCap>1)&&(waitingNormalization!=true)){
            waitingNormalization=true;
        }
    }

    @Override
    public void addAll(StringBuilder nextChar) {
        value.append(nextChar.toString());
        if(waitingNormalization!=true){
            waitingNormalization=true;
        }
    }

    @Override
    public void endOfToken() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exchangeLastLeterTranscription(String excahgeWithThis) {
        
        if((this.transcribtion==null)||(transcribtion.length()-lastLetterLength<1)){
            return;
        }
        
        transcribtion.delete(transcribtion.length()-lastLetterLength, transcribtion.length()); 
        transcribtion.append(excahgeWithThis);
    }
    
    @Override
    public String getType() {
        return tokenType;
    }
    @Override
    public void addStress(int atPosition){ 
        boolean check=true;
        
        if((this.transcribtion==null)||(transcribtion.length()<atPosition)||(atPosition<0)){
            return;
        }
        check = checkWhereStress(atPosition-1);
                
        if((!check)&&(atPosition<transcribtion.length()-1)){
              checkWhereStress(atPosition);          
        }else{
            return;
        }      
        
    }
    
    private boolean checkWhereStress(int atPosition){
//transcribtion.replace(atPosition-1,atPosition,""+(char)(encoding.getToUpperCase((int)transcribtion.charAt(atPosition-1))));
        int check;
        
        check = encoding.checkIfVowel(transcribtion.charAt(atPosition));
        
        if(check==1){
            transcribtion.replace(atPosition,atPosition+1,""+(char)(encoding.getToUpperCase((int)transcribtion.charAt(atPosition))));
            return true;
        }
        
        return false;
    }
}
