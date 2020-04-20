/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diplomna.clear.tools;
//author Maria Shindarova

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

public abstract class Encoding {
	protected int fromCharNumCapLeter;
	protected int toCharNumCapLeter;
        
        protected int fromCharNumSmall;
	protected int toCharNumSmall;
        
        protected int toLowerCase;
        
        protected HashSet<Integer> excludedChar;
        protected int comma;
        protected int hyphen;
        protected int colon;
        protected int space;
	protected int tiltedLineLeft;
        protected int tiltedLineRight;
        protected int numbersFrom;
	protected int numbersTo;
        
        protected int period;
	protected int punctuationFrom;
	protected int punctuationTo;
	protected HashSet<Integer> punctuationExtended;
	protected float[] charsVolewConsonant;
        protected int keyVolewConsonantARR;
        protected HashSet<Integer> endOfSentence;
        
        protected Map<Integer,String> differentMeanningMath;
	protected Map<Integer,String> punctoationToWord;
        protected String[][] hlpFSMGolemiBukvi;
        protected int FSMmaxStateGolemiBukvi;
        protected String[][] hlpFSMMalkiBukvi;
        protected int FSMmaxStateMalkiBukvi;
        protected String[][] hlpFSMCifri;
        protected int FSMmaxStateCifri;
        protected static Map<String,String> numbers=null;
        
        protected abstract void initialize();
        
 
    public int getNumLetterToKey(int charNum){

        if(checkIfSmallLetter(charNum)){
            return charNum-fromCharNumSmall;
        }else if(checkIfCapLetter(charNum)){
            return charNum-fromCharNumCapLeter;
        }else{
            return -1;
        }
    }
    
    public int getNumLetterToKeyVocal(int charNum){

        if(checkIfSmallLetter(charNum)){
            return charNum-fromCharNumSmall;
        }else if(checkIfCapLetter(charNum)){
            return charNum-fromCharNumCapLeter+32;
        }else{
            return -1;
        }
    }    
    public int isCharCossonan(int charNum){
        if (!checkIfChar(charNum)){
            return -1;
        }
        float charIs =getIndexCharCategARR(charNum);
        if(charIs>0){
            return 1;
        }
        return 0;
    }
    //not changing cosonnan
    public int isCharSonnarCossonan(int charNum){
        if (!checkIfChar(charNum)){
            return -1;
        }

        float charIs =getIndexCharCategARR(charNum);
        charNum=getToLowerCase(charNum);
        charNum=charNum-fromCharNumSmall +1;
    //cossonan positive number
        if((int)charIs == charNum){
            return 1;
        }
        return 0;
    }

    public int isCharSoundedCossonan(int charNum){
        if (!checkIfChar(charNum)){
            return -1;
        }

        float charIs = getIndexCharCategARR(charNum);
        charNum=getToLowerCase(charNum);
        charNum=(charNum-fromCharNumSmall +1)/100;
        if((charIs>0)&&(charIs<1)&&(charNum!=charIs)){
            return 1;
        }
        return 0;
    }
        
    public int isCharMutedCossonan(int charNum){
        
        if (!checkIfChar(charNum)){
              return -1;
        }

        float charIs = getIndexCharCategARR(charNum);
        charNum=getToLowerCase(charNum);
        charNum=charNum-fromCharNumSmall +1;
        
        if((charIs>1)&&(charNum!=(int)charIs)){
              return 1;
        }
        return 0;
        }

      public int isCharSoundedVowel(int charNum){  
          if (!checkIfChar(charNum)){
              return -1;
          }

          float charIs = getIndexCharCategARR(charNum);
          if((charIs>-1)&&(charIs<0)){
             return 1;
          }
          return 0;
      }

      public int isCharMutedVowel(int charNum){  
          if (!checkIfChar(charNum)){
              return -1;
          }

          float charIs = getIndexCharCategARR(charNum);
          if(charIs<-1){
             return 1;
          }
          return 0;
      }

      public Character getMuttedChar(int charNum){
          if (!checkIfChar(charNum)){
              return null;
          }

          float charIs = getIndexCharCategARR(charNum);
          int hlp=100;
          int charNumHLP=getToLowerCase(charNum);
          charNumHLP=charNumHLP-fromCharNumSmall +1;
          if(charIs<0){
              hlp=-100;
          }
          if((charIs>-1)&&(charIs<1)){
              charIs=(int)(charIs*hlp);
              if(charNumHLP==(int)charIs){
                  return (char)charNum;//null;
              }
              return (char)((int)charIs+fromCharNumSmall-1);
          }
          return (char)charNum;
      }

      public int checkIfSoundedChar(int charNum){
          if (!checkIfChar(charNum)){
              return -1;
          }

          float charIs = getIndexCharCategARR(charNum);
          int hlp=100;
          charNum=getToLowerCase(charNum);
          charNum=charNum-fromCharNumSmall +1;
          if(charIs<0){
              hlp=-100;
          }
          if((charIs<1)&&(charIs>-1)){
              charIs=charIs*hlp;
              if(charNum==(int)charIs){
                  return 0;/// this letters don't have sounded/muted
              }
              return 1;//input is sounded
          }
          return 0;//input is not sounded
      }

      public Character getSoundedChar(int charNum){
          if (!checkIfChar(charNum)){
              return null;
          }

          float charIs = getIndexCharCategARR(charNum);
          int hlp=1;
          int charNumHLP=getToLowerCase(charNum);
          charNumHLP=charNumHLP-fromCharNumSmall +1;
          if(charIs<0){
              hlp=-1;
          }
          if((charIs>=1)||(charIs<=-1)){
              charIs=charIs*hlp;
              if(charNumHLP==(int)charIs){
                  return (char)charNum;
              }
              return (char)((int)charIs+fromCharNumSmall-1);
          }
          return (char)charNum;
      }

      protected float getIndexCharCategARR(int charNum){
          if (!checkIfChar(charNum)){
              return -1;
          }

          float charIs;
          charNum=getToLowerCase(charNum);
          charNum=charNum-fromCharNumSmall +1;
          //ccc 0.2 9 -9 
          return charsVolewConsonant[charNum];
      }

      public int checkIfVowel(int charNum){
      //glasna
          String type;
          int typeOfLetter;
//            if(!checkIfChar(charNum)){
//                return -1;
//            }
          type=checkSymbolType(charNum);

          if(type.equals("letter")){
              keyVolewConsonantARR=charNum-fromCharNumSmall+1;
          }else if(type.equals("letterCAP")){
              keyVolewConsonantARR=charNum-fromCharNumCapLeter+1;
          }else{
              return -1;
          }

          typeOfLetter = (int)charsVolewConsonant[keyVolewConsonantARR];
          if(typeOfLetter<0){
              return 1;//it is Vowel 
          }else{
              return 0;//it is not Vowel
          }
      }        

      public int checkIfConsonant(int charNum){
              //syglasna
          String typeSmallCap;
          float typeOfLetter;
          if(Character.isWhitespace(charNum)){
              return -1;}
          if(!checkIfChar(charNum)){
              return -1;
          }
          typeSmallCap=checkSymbolType(charNum);

          if(typeSmallCap.equals("letter")){
              keyVolewConsonantARR=charNum-fromCharNumSmall+1;
          }else if(typeSmallCap.equals("letterCAP")){
              keyVolewConsonantARR=charNum-fromCharNumCapLeter+1;
          }else{
              return -1;
          }

          typeOfLetter = charsVolewConsonant[keyVolewConsonantARR];
          if(typeOfLetter>0){
              return 1;//cossonan
          }else{
              return 0;//vowell
          }
      }
      public int getToUpperCase(int charNum) {
          if (!checkIfChar(charNum)){
              return -1;
          }

          int hlp =1;

          if((charNum>=fromCharNumSmall)&&(charNum<=toCharNumSmall)){
              if(fromCharNumCapLeter<fromCharNumSmall){
                  hlp=-1;
              }
              return charNum+(toLowerCase*hlp);
          }else{
              return charNum;
          }
      }
      
      public int getToLowerCase(int charNum) {
          if (!checkIfChar(charNum)){
              return -1;
          }

          int hlp =1;

          if((charNum>=fromCharNumCapLeter)&&(charNum<=toCharNumCapLeter)){
              if(fromCharNumCapLeter>fromCharNumSmall){
                  hlp=-1;
              }
              return charNum+(toLowerCase*hlp);
          }else{
              return charNum;
          }
      }
      public boolean checkIfSymbolIsAcceptable(int charNum){
              if((charNum>=fromCharNumCapLeter)&&(charNum<=toCharNumCapLeter)&&(!excludedChar.contains(charNum))) {
                  return true;
              }else if((charNum>=fromCharNumSmall)&&(charNum<=toCharNumSmall)&&(!excludedChar.contains(charNum))) {
                  return true;
              }else if(((charNum>=punctuationFrom)&&(charNum<=punctuationTo))||(punctuationExtended.contains(charNum))) {
                  return true;
              }else if((charNum>=numbersFrom)&&(charNum<=numbersTo)) {
                  return true;
              }else if(charNum==space) {
                  return true;
              }else{
                  return false;
              }
      }

      public boolean checkIfSymbolIsAcceptable(String symbolType){
              if(symbolType.equals("letterCAP")) {
                  return true;
              }else if(symbolType.equals("letter")) {
                  return true;
              }else if(symbolType.equals("punctoation")) {
                  return true;
              }else if(symbolType.equals("number")) {
                  return true;
              }else if(symbolType.equals("space")) {
                  return true;
              }else{
                  return false;
              }
          }

      public boolean checkIfPunctiation(int charNum){
          if(((charNum>=punctuationFrom)&&(charNum<=punctuationTo))||
                  (punctuationExtended.contains(charNum))) {
              return true;
          }else{
              return false;
          }
      }

      public boolean checkIfChar(int charNum){
              if((charNum>=fromCharNumCapLeter)&&(charNum<=toCharNumCapLeter)&&(!excludedChar.contains(charNum))) {
                  return true;
              }else if((charNum>=fromCharNumSmall)&&(charNum<=toCharNumSmall)&&(!excludedChar.contains(charNum))) {
                  return true;
              }else{
                  return false;
              }
      }
      public boolean checkIfCapLetter(int charNum){
              if((charNum>=fromCharNumCapLeter)&&(charNum<=toCharNumCapLeter)&&(!excludedChar.contains(charNum))) {
                  return true;
              }else{
                  return false;
              }
      }
      public boolean checkIfSmallLetter(int charNum){
              if((charNum>=fromCharNumSmall)&&(charNum<=toCharNumSmall)&&(!excludedChar.contains(charNum))) {
                  return true;
              }else{
                  return false;
              }
      }
      public boolean checkIfComma(int charNum){
          if(charNum==comma) {
              return true;
          }else{
             return false;
          }
      }

      public boolean checkIfCommaOrPeriod(int charNum){
          if((charNum==comma)||(charNum==period)){
              return true;
          }else{
             return false;
          }
      }

      public boolean checkIfHYPHEN(int charNum){
          if(charNum==hyphen) {
              return true;
          }else{
             return false;
          }
      }

      public boolean checkIfColon(int charNum){
          if(charNum==colon) {
              return true;
          }else{
             return false;
          }
      }

      public boolean checkIfTilltedLine(int charNum){
          if((charNum==tiltedLineLeft)||(charNum==tiltedLineRight)){
              return true;
          }else{
             return false;
          }
      }

      public boolean checkIfPeriod(int charNum){
          if(charNum==period) {
              return true;
          }else{
             return false;
          }
      }

      public boolean checkIfNumber(int charNum){
          if((charNum>=numbersFrom)&&(charNum<=numbersTo)) {
              return true;
          }else{
             return false;
          }
      }

      public boolean checkIfSpace(int charNum){
          if(space==charNum){
              return true;
          }else{
             return false;
          }
      }

      public boolean checkIfEndOfSentenceSymb(int charNum){
          if(endOfSentence.contains(charNum)){
              return true;
          }else{
             return false;
          }
      }

      public String checkSymbolType(int charNum) {
          if((charNum>=fromCharNumCapLeter)&&(charNum<=toCharNumCapLeter)&&(!excludedChar.contains(charNum))) {
                  return "letterCAP";
              }else if((charNum>=fromCharNumSmall)&&(charNum<=toCharNumSmall)&&(!excludedChar.contains(charNum))) {
                  return "letter";
              }else if(((charNum>=punctuationFrom)&&(charNum<=punctuationTo))||(punctuationExtended.contains(charNum))) {
                  return "punctoation";
              }else if((charNum>=numbersFrom)&&(charNum<=numbersTo)) {
                  return "number";
              }else if(charNum==space) {
                  return "space";
              }else{
                  return "ERR";
              }
      }

      public String differentMeanningMath(char symbol){
          if(differentMeanningMath.containsKey(symbol)){
               return differentMeanningMath.get(symbol);
          }
          return "";
      }

      public String punctoationToWord(char symbol){
          if(punctoationToWord.containsKey(symbol)){
               return punctoationToWord.get(symbol);
          }
          return "";
      }
//        protected float getIndexCharCategARR(int charNum){
//            if (!checkIfChar(charNum)){
//                return -1;
//            }
//
//            float charIs;
//            charNum=getToLowerCase(charNum);
//            charNum=charNum-fromCharNumSmall +1;
//            //ccc 0.2 9 -9 
//            return charsVolewConsonant[charNum];
//        }
//        public int checkIfVowel(int charNum){
//        //glasna
//            String type;
//            int typeOfLetter;
//            if(!checkIfChar(charNum)){
//                return -1;
//            }
//            type=checkSymbolType(charNum);
//
//            if(type.equals("letter")){
//                keyVolewConsonantARR=charNum-fromCharNumSmall+1;
//            }else if(type.equals("letterCAP")){
//                keyVolewConsonantARR=charNum-fromCharNumCapLeter+1;
//            }else{
//                return -1;
//            }
//
//            typeOfLetter = (int)charsVolewConsonant[keyVolewConsonantARR];
//            if(typeOfLetter<0){
//                return 1;//it is Vowel 
//            }else{
//                return 0;//it is not Vowel
//            }
//        }        
//        
//        public int checkIfConsonant(int charNum){
//                //syglasna
//            String typeSmallCap;
//            float typeOfLetter;
//            if(Character.isWhitespace(charNum)){
//                return -1;}
//            if(!checkIfChar(charNum)){
//                return -1;
//            }
//            typeSmallCap=checkSymbolType(charNum);
//            
//            if(typeSmallCap.equals("letter")){
//                keyVolewConsonantARR=charNum-fromCharNumSmall+1;
//            }else if(typeSmallCap.equals("letterCAP")){
//                keyVolewConsonantARR=charNum-fromCharNumCapLeter+1;
//            }else{
//                return -1;
//            }
//
//            typeOfLetter = charsVolewConsonant[keyVolewConsonantARR];
//            if(typeOfLetter>0){
//                return 1;//cossonan
//            }else{
//                return 0;//vowell
//            }
//        }
        
    public int getFSMmaxStateGolemiBukvi() {
        return FSMmaxStateGolemiBukvi;
    }

    public int getFSMmaxStateMalkiBukvi() {
        return FSMmaxStateMalkiBukvi;
    }

    public int getFSMmaxStateCifri() {
        return FSMmaxStateCifri;
    }
 
    public String[][] getFSMsetGolemiBukvi() {
        return hlpFSMGolemiBukvi;
    }

    public String[][] getFSMsetMalkiBukvi() {
        return hlpFSMMalkiBukvi;
    }

    public String[][] getFSMsetCifri() {
        return hlpFSMCifri;
    }
    public Map<String,String> getNumberToWord() {
        return numbers;
    }
        
}
