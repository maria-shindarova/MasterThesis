/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diplomna.clear.tools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author author Maria Shindarova 
 * 
 * //http://htmlpurifier.org/docs/enduser-utf8.html
 * SINGELTON Design Pattern
 */
public class UTF8Encoding extends Encoding{
    //SINGELTON Design Pattern
    private static UTF8Encoding utf8encod=null;
    private static String lang;
   
    private UTF8Encoding(){
         initialize();
    }
         //SINGELTON Design Pattern
    protected void initialize(){
         if(lang=="BG"){
             initializeBG();
         }
    }    
//SINGELTON Design Pattern
    public static UTF8Encoding getInstance(String language)throws Exception{
         if(utf8encod==null){
             lang =language;
             utf8encod=new UTF8Encoding();
         }
         if(language.equals(lang)){
            return utf8encod;
         }else{
             throw new Exception("Difference in input data!");
         }
    }
     private void initializeBG() {
		 fromCharNumCapLeter=1040;
                 toCharNumCapLeter=1071;
                 
		 fromCharNumSmall=1072;
                 toCharNumSmall=1103;
		 
                 excludedChar=new HashSet<Integer>();
		 excludedChar.addAll(Arrays.asList(1067,1069,1099,1101));
                 
                 toLowerCase=32;
		 space = 32;
                 colon=58;
                 numbersFrom=48;
                 numbersTo=57;
                 punctuationFrom=33;
		 punctuationTo=47;
                 period = 46;
                 //58-64 91-96 123-126
		 punctuationExtended=new HashSet<Integer>();
		 punctuationExtended.addAll(Arrays.asList(58,59,60,61,62,63,64,91,92,93,94,95,96,123,124,125,126));
                                  
                 endOfSentence=new HashSet<Integer>();
                 endOfSentence.addAll(Arrays.asList(33,46,59,63));
                 comma=44;
                 hyphen=45;
                 tiltedLineRight=47;
                 tiltedLineLeft=92;
                 differentMeanningMath=new HashMap();
                 differentMeanningMath.put(37, "процента");
                 differentMeanningMath.put(44, "цяло и");
                 //differentMeanningMath.put(45, "минус");
                 differentMeanningMath.put(46, "цяло и");
                 differentMeanningMath.put(47, "--дата");//47 e /
                 differentMeanningMath.put(58, "и");// 58 e :
                 differentMeanningMath.put(92, "--дата");//92 e \

                 punctoationToWord=new HashMap(5);
                 punctoationToWord.put(35, "номер");
                 punctoationToWord.put(36, "долара");        
                 punctoationToWord.put(37, "процента");
                 punctoationToWord.put(64, "маймунско а"); 
                  
//гласни а,ъ,о,у,е,и съгласни-звучни(б,в,г,д,ж,з,дж,дз)беззвучни (п,ф,к,т,ш,с,х,ч,ц).
//[трудно определяне а:ъ; трябват правила о:у; ////недопустимо :е:и].
//  [а:ъ; о:у; е:и] //[б-п;в-ф;г-к;д-т;ж-ш;з-с] //не се променят сонарни - л,м,н,р,й
//negative vowels; consonant positive;1>ToMuted>0; number leads to sounded or muted sound
////                                 а      б     в     г     д    е       ж    з    и й  к  л  м  н   о    п  р с  т  у ф  х  ц   ч  ш  щ   ъ  --  ь   --   ю    я
                              //0    1      2     3     4     5    6       7    8    9 1011 12 13 14   15  16,1718,19,2021,22 |23 |24|25|26 |27|28|29  |30| 31 | 32                                         
charsVolewConsonant=new float[]{0,-0.27f,0.16f,0.21f,0.11f,0.19f,-0.09f,0.25f,0.18f,-6,10,4,12,13,14,-0.20f,2,17,8,5,-15,3,220,230,240,7,25, -1,0,0.28f,0,0.29f,0.3f};

hlpFSMCifri =new String[][]{{"1"," ","2"},
          {"1",".","3"},{"1",",","3"},{"1",":","3"},
          {"1","/","3"},{"1","-","3"},
          {"2",".","3"},{"2",",","3"},{"2","/","3"},
          {"2","-","3"},{"2",":","3"},
          {"3"," ","4"},{"3",FSM.getNumber_CODE()+"","5"},
          {"4",FSM.getNumber_CODE()+"","5"}};

FSMmaxStateCifri =5;

hlpFSMMalkiBukvi=new String[][]{//{"1"," ","2"},
            {"1",".","3"},{"1","-","3"},{"1","/","3"},
            {"2",".","3"},{"2","-","3"},{"2","/","3"},
            //{"3"," ","4"},
            {"3",FSM.getSMALL_CHAR_CODE()+"","5"},
            {"4",FSM.getSMALL_CHAR_CODE()+"","5"}};

FSMmaxStateMalkiBukvi=5;

hlpFSMGolemiBukvi=new String[][]{
                {"1"," ","2"},{"1",".","3"},{"1","-","3"},{"1","/","3"},
                {"2",".","3"},{"2","-","3"},{"2","/","3"},
                {"3"," ","4"},{"3",FSM.getSMALL_CHAR_CODE()+"","5"},
                {"4",FSM.getSMALL_CHAR_CODE()+"","5"}};

FSMmaxStateGolemiBukvi=5;

numbers=new HashMap(){{
        put("0","нула");put("1","едно");put("2","две");put("3","три");
        put("4","четири");put("5","пет");put("6","шест");put("7","седем");
        put("8","осем");put("9","девет");put("10","десет");put("11","единайсет");
        put("12","дванайсет");put("13","тринайсет");put("14","четиринайсет");
        put("15","петнайсет");put("16","шестнайсет");put("17","седемнайсет");
        put("18","осемнайсет");put("19","деветнайсет");put("20","двайсет");
        put("30","трийсет");put("40","четиридесет");put("50","петдесет");
        put("60","шейсет");put("70","седемдесет");put("80","осемдесет");
        put("90","деветдесет");put("100","сто");put("200","двеста");
        put("300","триста");put("400","четиристотин");put("500","петстотин");
        put("600","шестстотин");put("700","седемстотин");put("800","осемстотин");
        put("900","деветстотин");put("1000","хиляда");put("4x","хиляди");
        put("1000000","един милион");put("7x","милиона");put("concat","и");}};

   } 
  
 }
 