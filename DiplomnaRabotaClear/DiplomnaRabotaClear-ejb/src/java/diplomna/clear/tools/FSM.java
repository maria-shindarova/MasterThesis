/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diplomna.clear.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Maria Shindarova
 */
public class FSM {

    private final Map<String,Integer> FSM;
    private final int stateApproved;
    private Encoding encoding;
    private final static char CHAR_SMALL_CODE='c';
    private final static char CHAR_BIG_CODE='C';
    private final static char Number_CODE='n';

//FSM
//index arr= state 0    1   2  3 4 5 6  
//               " 1" " 1" c2
//                    /2 
//                    \2    
//less consuming version of fsm with array not hashtable
    
    public FSM(int stateApproved,String states[][],String language)throws Exception{
        this.stateApproved=stateApproved;
        this.encoding=UTF8Encoding.getInstance(language);
   //CHAR_CODE='c' ; Number_CODE='n'
   //states[0]curr state states[1]curr symbol states[2]to state
        FSM=new HashMap<>(states.length);
        String key, hlp; int value;
        for(int i =0;i<states.length;i++){
            key=states[i][0]+states[i][1];
            hlp=states[i][2];
            value = Integer.parseInt(hlp);
            if(!FSM.containsKey(key)){
                FSM.put(key, value);
            }
        }
    }
  //null if state not found - not in the language of this FSM or state approved
    public Integer nextState(int currState,char currSymbol){
        int len=0;String curr="";
        if (currState>=stateApproved){
            return null;
        }
        //len=FSM[currState].length;
        if (encoding.checkIfSmallLetter(currSymbol)){
            currSymbol=CHAR_SMALL_CODE;
        }else if(encoding.checkIfCapLetter(currSymbol)){
            currSymbol=CHAR_BIG_CODE;
        }else if(encoding.checkIfNumber(currSymbol)){
            currSymbol=Number_CODE;
        }else{
            //return null;
        }
       
        if(FSM.containsKey(String.valueOf(currState)+currSymbol)){
            return FSM.get(String.valueOf(currState)+currSymbol);
        }
        return null;
    }
    public boolean isStateApproved(int currState){
        return (stateApproved==currState);
    }
    public int getStateApproved(){
        return stateApproved;
    }
    public static char getCAP_CHAR_CODE() {
        return CHAR_BIG_CODE;
    }
    public static char getSMALL_CHAR_CODE() {
        return CHAR_SMALL_CODE;
    }
    public static char getNumber_CODE() {
        return Number_CODE;
    } 
 }