/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diplomna.clear.tools.token;

/**
 *
 * @author Maria Shindarova
 */
public class Space  extends TokenA{
    public static String tokenType="space";
    
    public Space(char currSymbol){
      
        value = new StringBuilder();
        value.append(currSymbol);
        waitingNormalization=false;
    }
    public Space(){
       value = new StringBuilder();
       waitingNormalization=false;
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
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
