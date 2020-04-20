package diplomna.clear.tools.token;

import diplomna.clear.ejb.visitor.VisitorI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maria Shindarova
 */
public abstract class TokenA implements TokenI{
    
    protected StringBuilder value;
    protected boolean waitingNormalization;
    protected StringBuilder transcribtion=null;
    protected int lastLetterLength=1;
    protected int baseTokenId=0;

    public int getBaseTokenId() {
        return baseTokenId;
    }

    public void setBaseTokenId(int baseTokenId) {
        this.baseTokenId = baseTokenId;
    }
    
    public StringBuilder getTranscribtion() {
        return transcribtion;
    }

    
    public StringBuilder getValue() {
        return value;
    }
    
    public abstract void add(char nextChar);
    public abstract void addAll(StringBuilder nextChar);
    public abstract void endOfToken();
    public abstract void addStress(int atPosition);
    
    public boolean getWaitingNormalization(){
        return waitingNormalization;
    }
    public String toString(){
        String hlp="";
        if((transcribtion!=null)&&(transcribtion.length()>0)){
            hlp=transcribtion.toString();
        }
        return value.toString()+" {-"+hlp+"-}"+" {-"+baseTokenId+"-}";
    }

    public int length() {
        return value.length();
    }

    public void nowWaitNormalization() {
        waitingNormalization=true;
    }

    public void noNeedNormalization(){
        waitingNormalization=false;
    }

    public Character getSymbolAt(int position) {
        if((value.length()>position)&&(position>=0)){
            return value.charAt(position);
        }
        return null;
    }    

////Part Of VISITOR Design Pattern
    public void accept(VisitorI visitor){
        try {
            visitor.visit(this);
        } catch (Exception ex) {
            Logger.getLogger(TokenA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addTrancribtion(StringBuilder transcribtion){
        this.transcribtion=transcribtion;
    }

    public void addLengthLastLetter(int lastLetterLength){
        this.lastLetterLength=lastLetterLength;
    }
    
    public int getTrancribtionLen(){
        if(transcribtion!=null){
            return this.transcribtion.length();
        }else{
            return 0;
        }
    }    
    
    public int getCapitalLetterCNT() {
        return 0;
    }
}
