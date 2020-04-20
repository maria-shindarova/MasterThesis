/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diplomna.clear.ejb.visitor.rules;

/**
 *
 * @author Maria Shindarova
 */
public interface RuleI {
    public boolean isThisMyRule(char previousSymbol,char currSymbol)throws Exception;
    public boolean isThisMyRule(char firstSymbol)throws Exception;
    public String howToChangeFirst(char first);
    public String howToChangeSecond(char second);
    
}
