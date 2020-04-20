/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diplomna.clear.ejb.normalization;

import diplomna.clear.tools.Encoding;
import diplomna.clear.tools.UTF8Encoding;
import diplomna.clear.tools.token.TokenI;
import java.util.ArrayList;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Maria Shindarova
 */
@Stateless
public class NormalizeWords implements NormalizeWordsLocal {
    @PersistenceContext(unitName = "DiplomnaRabotaClear-ejbPU")
    EntityManager em;
    
    private Encoding encoding;
    private static Map<String,String> numbers=null;
    
    @Override
    public String normalize(TokenI forNormalization, String lang) throws Exception {
        StringBuilder toNormal=forNormalization.getValue();
        encoding=UTF8Encoding.getInstance(lang);
        
        if(encoding.checkIfChar(toNormal.charAt(0))){
            return processChars(toNormal,forNormalization);
        }else if(encoding.checkIfNumber(toNormal.charAt(0))){
            
            return processNumber(toNormal);
        }else if(forNormalization.getType()=="punctuation"){
            return processPunctuation(toNormal);
        }else{
            return toNormal.toString();
        }
    }
    
    private String processChars(StringBuilder toNormal,TokenI forNormalization){
        int i=0,cntCap=0;boolean found=false;
        //ArrayList<Normalize> result; 
        ArrayList<String> result; 
        char hlp;
 
        if(toNormal.charAt(toNormal.length()-1)=='.'){
            toNormal.deleteCharAt(toNormal.length()-1);
        }        
        if(forNormalization.getCapitalLetterCNT()>0){
            i=0;found=false;cntCap=0;
            while((i<toNormal.length())&&(!found)){
                if(encoding.checkIfCapLetter(toNormal.charAt(i))){
                   hlp=(char) encoding.getToLowerCase(toNormal.charAt(i));
                   toNormal.setCharAt(i,hlp);
                   cntCap++;
                   if(forNormalization.getCapitalLetterCNT()==cntCap){
                       found=true;
                   }
                }
                i++;
            }
        }

        result=(ArrayList<String>)
                //em.createNativeQuery("SELECT meaning FROM diplomna.normalize WHERE word = '"+toNormal.toString()+"'")                
                em.createNativeQuery("SELECT meaning FROM diplomna.normalize use index(for_normalization) WHERE word = '"+toNormal.toString()+"' limit 1")
//                        createNamedQuery("Normalize.findByWord")
//                .setParameter("word",toNormal.toString())
                .getResultList();
       
        if(result.size()<=0){
            return toNormal.toString();
        }else{
            return result.get(0);//.getMeaning();
        }
    }

    private String processNumber(StringBuilder toNormal){
        StringBuilder checkThis=new StringBuilder();
        StringBuilder result=new StringBuilder();
        String differentMath="",numHlp="";
        
        for(int i=0;i<toNormal.length();i++){
            differentMath=encoding.differentMeanningMath(toNormal.charAt(i));
            if(differentMath!=""){
                numHlp = buildNum(checkThis);
                result.append(numHlp);
                result.append(differentMath);
                //differentMath=""; 
                checkThis.delete(0, checkThis.length());
            }else{
                checkThis.append(toNormal.charAt(i));
            }
            differentMath="";
        }
        if(checkThis.length()>0){
            numHlp = buildNum(checkThis);
            result.append(numHlp);
        }
        return result.toString();
    }
    
    private String buildNum(StringBuilder checkThis){
       
        StringBuilder result=new StringBuilder();
        StringBuilder hlpRes=new StringBuilder();
        int from=checkThis.length()-3;
        int to=checkThis.length();
        int cnt=0;String hlp = "00";
        
        if(numbers==null){
            numbers=encoding.getNumberToWord();
        }
                	        	        	        
        do{
            if(from<0){
                from=0;
            }
            while(cnt<(to-from)){
                if(numbers.containsKey(checkThis.substring(from+cnt, to))){
                    if(!checkThis.substring(from+cnt, to).equals("0")) {
                        if(cnt==2)
                              hlpRes.append(numbers.get("concat")+" ");
                        hlpRes.append(numbers.get(checkThis.substring(from+cnt, to)));
                        hlpRes.append(" ");
                    }
                    break;
                }else if(numbers.containsKey(checkThis.charAt(from+cnt)+hlp.substring(cnt))){
                    hlpRes.append(numbers.get(checkThis.charAt(from+cnt)+hlp.substring(cnt)));
                    hlpRes.append(" ");
                }
                
                cnt++;
           }
            cnt=0;

            if(to>0){
                if((to==1)&&(Integer.valueOf(checkThis.charAt(0)+"")==1)&&(checkThis.length()>2)){
                        hlpRes.delete(0, hlpRes.length());
                        hlpRes.append(numbers.get(String.valueOf((int)Math.pow((double)10,(double)(checkThis.length()-to)))));
                        hlpRes.append(" ");
                
                }else if(numbers.containsKey(String.valueOf(checkThis.length()-to+1)+'x')){
                    hlpRes.append(numbers.get(String.valueOf(checkThis.length()-to+1)+'x'));
                    hlpRes.append(" ");
                }
            }

            from=from-3;
            to=to-3;
            result.insert(0,hlpRes);
            hlpRes.delete(0, hlpRes.length());
        }while(to>=0);//end outer loop


        return result.substring(0, result.length()-1);
     }
    
    private String processPunctuation(StringBuilder normalize){
        String punctoationToWord = encoding.punctoationToWord(normalize.charAt(0));
        
        if(punctoationToWord!=""){
            return punctoationToWord;
        }
        return normalize.toString();
    }
}
