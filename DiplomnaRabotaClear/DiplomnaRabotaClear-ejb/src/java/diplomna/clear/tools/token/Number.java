/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diplomna.clear.tools.token;

/**
 *
 *@author Maria Shindarova
 */
public class Number  extends TokenA{
    public static String tokenType="number";
            
    public Number(char currSymbol){
        value = new StringBuilder();
        value.append(currSymbol);
        waitingNormalization=true;
        
    }
    public Number(){
        value = new StringBuilder();
        waitingNormalization=true;
    }
    
    @Override
    public void add(char nextChar) {
       value.append(nextChar);
    }

    @Override
    public void addAll(StringBuilder nextChar) {
        value.append(nextChar.toString());
    }

    @Override
    public void endOfToken() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exchangeLastLeterTranscription(String excahgeWithThis) {
        if((this.transcribtion==null)||(transcribtion.length()<1)){
            return;
        }
        transcribtion.delete(transcribtion.length()-lastLetterLength-1, lastLetterLength);
        transcribtion.append(excahgeWithThis);
        
    }
        @Override
    public String getType() {
        return tokenType;
    }

    @Override
    public void addStress(int atPosition) {
        return;
    }
}
