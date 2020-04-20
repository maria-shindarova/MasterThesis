
package diplomna.clear.ejb.visitor.rules;

import diplomna.clear.tools.Encoding;
import diplomna.clear.tools.UTF8Encoding;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Maria Shindarova
 */
public class RulesAll {
    private ArrayList<RuleI> firstCossonant;
    private ArrayList<RuleI> firstVowel;
    private ArrayList<RuleI> firstLetterWord;
    private Map<Character,RuleI> firstConcreateChar;
    private Map<Character,RuleI> firstConcreateSymbol;
    private Encoding encoding;

    public RulesAll(String lang) throws Exception{
        firstCossonant=new ArrayList<>();
        firstVowel=new ArrayList<>();
        firstLetterWord=new ArrayList<>();
        firstConcreateChar=new HashMap<>();
        firstConcreateSymbol=new HashMap<>();
        encoding=UTF8Encoding.getInstance(lang);
        fillAllRules(lang);
    }
    
    public ArrayList<RuleI> getApplicableRulesGeneral(char first,boolean isItFirst){
        
        if(isItFirst){
            return firstLetterWord;
        }

        if(!encoding.checkIfChar(first)){
            if(firstConcreateSymbol.containsKey(first)){
                ArrayList<RuleI>hlp=new ArrayList<>();
                hlp.add(firstConcreateSymbol.get(first));
                return hlp;
            }else{
                return new ArrayList<RuleI>();
            }
        }
        if(encoding.checkIfConsonant(first)==1){
            return firstCossonant;
        }else if(encoding.checkIfVowel(first)==1){
            return firstVowel;
        }else{
            return new ArrayList<RuleI>();
        }
    }
    public RuleI getApplicableRulesByLetter(char first,boolean isItFirstLetter){
        if(!encoding.checkIfChar(first)){
            if(firstConcreateSymbol.containsKey(first)){
                return firstConcreateSymbol.get(first);
            }else{
                return null;
            } 
        }
        if(firstConcreateChar.containsKey(first)){
            return firstConcreateChar.get(first);
        }
        return null;
    }    
    private void fillAllRules(String lang) throws Exception{
        
        firstCossonant.add(new GeneralToLetterRule(RuleA.getCodeCOSSONAN(), 'ю', "", "йу",lang));
        
        firstCossonant.add(new GeneralToGeneralRule(RuleA.getCodeSOUNDED_COSSONAN(), RuleA.getCodeWORD_END(), RuleA.getCodeMUTED_COSSONAN(), "",lang));
        firstCossonant.add(new GeneralToGeneralRule(RuleA.getCodeSOUNDED_COSSONAN(), RuleA.getCodeMUTED_COSSONAN(), RuleA.getCodeMUTED_COSSONAN(), "",lang));
        firstCossonant.add(new GeneralToGeneralRule(RuleA.getCodeMUTED_COSSONAN(), RuleA.getCodeSOUNDED_COSSONAN(), RuleA.getCodeSOUNDED_COSSONAN(), "",lang));
   
        firstVowel.add(new GeneralToLetterRule(RuleA.getCodeVOWEL(), 'я', "", "йа",lang));
        
        //firstLetterWord.add(new GeneralToLetterRule(RuleA.getCodeWORD_BEGIN(), 'ю', "", "йу",lang));
        //firstLetterWord.add(new GeneralToLetterRule(RuleA.getCodeWORD_BEGIN(), 'я', "", "йа",lang));
        firstLetterWord.add(new GeneralToGeneralRule(RuleA.getCodeMUTED_COSSONAN(), RuleA.getCodeSOUNDED_COSSONAN(), RuleA.getCodeSOUNDED_COSSONAN(), "",lang));
        firstLetterWord.add(new GeneralToGeneralRule(RuleA.getCodeSOUNDED_COSSONAN(), RuleA.getCodeMUTED_COSSONAN(), RuleA.getCodeMUTED_COSSONAN(), "",lang));
        
        firstConcreateChar.put('ь', new CharToCharRule('ь', 'о', "й", "",lang));
        //firstConcreateChar.put('щ', new CharToCharRule('щ', '', "", ""));
        
        ///?
        firstConcreateChar.put('б', new CharToCharRule('б', 'о', "", "у",lang));
        firstConcreateChar.put('п', new CharToCharRule('п', 'о', "", "у",lang));
        firstConcreateChar.put('в', new CharToCharRule('в', 'о', "", "у",lang));
        firstConcreateChar.put('ф', new CharToCharRule('ф', 'о', "", "у",lang));
        firstConcreateChar.put('м', new CharToCharRule('м', 'о', "", "у",lang));
        //firstConcreateChar.put('', new CharToCharRule('', '', "", ""));
        
    }
}
