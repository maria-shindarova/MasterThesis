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
public abstract class RuleA implements RuleI{
    
    private final static String WORD_BEGIN= "BOT";
    private final static String WORD_END= "EOT";
    private final static String COSSONAN= "COS";
    private final static String SOUNDED_COSSONAN= "SCOS";
    private final static String SONAR_COSSONAN= "OCOS";
    private final static String MUTED_COSSONAN= "MCOS";
    private final static String VOWEL= "VOW";
    private final static String MUTED_VOWEL= "MVOW";
    private final static String SOUNDED_VOWEL= "SVOW";
    
    protected Encoding encoding;
    protected String language;
    
    protected boolean isInputCorect(String input){
        switch(input){
            case WORD_BEGIN: return true;
            case WORD_END: return true;
            case COSSONAN:return true;
            case SOUNDED_COSSONAN:return true;
            case MUTED_COSSONAN:return true;
            case SONAR_COSSONAN:return true;
            case VOWEL:return true;
            case MUTED_VOWEL:return true;
            case SOUNDED_VOWEL:return true;
            
            default:return false;
        }
    }
    
    protected String checkGeneralInput(char input) throws Exception{
        int checkCoss,checkIfVow,isItSounded,isItMuted;
        
        if(encoding==null){
             encoding=UTF8Encoding.getInstance(language);
        }
        if(!Character.isAlphabetic(input)){
            return "";
        }
      
        checkCoss=encoding.checkIfConsonant((int)input);
        isItSounded=encoding.checkIfSoundedChar((int)input);
        
        if(checkCoss==1){
          
            if(isItSounded==1){
                return SOUNDED_COSSONAN;
            }else if(isItSounded==-1){
                return "";
            }else{
                isItMuted=encoding.isCharMutedCossonan((int)input);
                if(isItMuted==1){
                    return MUTED_COSSONAN;
                }else{
                    return SONAR_COSSONAN;
                }
                
            }
        }
        
        checkIfVow=encoding.checkIfVowel(input);
        
        if(checkIfVow==1){
            if(isItSounded==1){
                return MUTED_VOWEL;
            }else if(isItSounded==-1){
                return "";
            }else{
                return SOUNDED_VOWEL;
            }
        }else if(input==' '){
           return WORD_BEGIN;
        }else{
            return ""; 
        }
    }
    
    public static String getCodeWORD_BEGIN() {
        return WORD_BEGIN;
    }

    public static String getCodeWORD_END() {
        return WORD_END;
    }

    public static String getCodeCOSSONAN() {
        return COSSONAN;
    }

    public static String getCodeSOUNDED_COSSONAN() {
        return SOUNDED_COSSONAN;
    }

    public static String getCodeMUTED_COSSONAN() {
        return MUTED_COSSONAN;
    }
    
    public static String getCodeSONAR_COSSONAN() {
        return SONAR_COSSONAN;
    }
    
    public static String getCodeVOWEL() {
        return VOWEL;
    }

    public static String getCodeMUTED_VOWEL() {
        return MUTED_VOWEL;
    }

    public static String getCodeSOUNDED_VOWEL() {
        return SOUNDED_VOWEL;
    }
}