/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diplomna.clear.tools.token;

import diplomna.clear.ejb.visitor.VisitorI;

/**
 *
 * @author Maria Shindarova
 */
public interface TokenI {
    public StringBuilder getValue();
    public String getType();
    public void add(char nextChar);
    public void addAll(StringBuilder nextChar);
    public boolean getWaitingNormalization();
    public int length();
    public void nowWaitNormalization();
    public void noNeedNormalization();
    public void endOfToken();
    public Character getSymbolAt(int position);
    public String toString();
    public void accept(VisitorI visitor);
    public void addTrancribtion(StringBuilder transcribtion);
    public void exchangeLastLeterTranscription(String excahgeWithThis);
    public void addLengthLastLetter(int lastLetterLength);
    public int getTrancribtionLen();
    public StringBuilder getTranscribtion();
    public int getCapitalLetterCNT();
    public int getBaseTokenId();
    public void setBaseTokenId(int baseTokenId);
    public void addStress(int atPosition);
}
