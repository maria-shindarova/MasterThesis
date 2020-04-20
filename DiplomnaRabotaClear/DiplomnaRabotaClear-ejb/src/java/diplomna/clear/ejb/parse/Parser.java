package diplomna.clear.ejb.parse;

import diplomna.clear.ejb.visitor.VisitorI;
import diplomna.clear.ejb.vocal.VocalizeInputLocal;
import diplomna.clear.tools.token.TokenI;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import static javax.ejb.TransactionAttributeType.REQUIRED;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * @author Maria Shindarova
 */
@Stateful
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(value = REQUIRED)

public class Parser implements ParserLocal,VisitorI {
    @EJB
    VocalizeInputLocal vocalizator;
   
    @PersistenceContext(unitName = "DiplomnaRabotaClear-ejbPU")
    EntityManager em;
    
    private int vocalizedCNT;
    private int vocalizedMAX;
    private String absPath;
    private String fileName;
    private FileOutputStream fOut;
    private ArrayList<TokenI> tokens;
    private int lastOmographID;
    private Map<Integer,Integer> myContext;
    private int lastFoundAt;
    private LinkedList<String> pending;
    private final String TYPE_OF_FILE = ".wav";
    private final int contextOfOmographMAX = 12;
    private String language;
    private Map<StringBuilder,ArrayList<Object[]>> saveQuery;
    private ArrayList<TokenI> vocalizedForUpdate;
    
    public String parserAction(ArrayList<TokenI> tokens, String language, boolean test){
        TokenI currToken;boolean postpone=false, notInTheTable;
        int omographId=0,baseWordId=0, foundOmographPosition=0,stress=0,sameWordsNum=0;
        String vocalizePath="";ArrayList<Object[]> result;
        
        String query="SELECT stressPlace, base_word_id, omograph_id, vocalizePath, broi_ednakvi FROM diplomna.words use index(for_words) WHERE word= '";
        this.tokens=tokens;
        this.language=language;
        
        fileName = tokens.hashCode()+TYPE_OF_FILE;
        if(vocalizedCNT!=-1){
            initialize();
        }        
        for(int i=0;i<tokens.size();i++){
            vocalizePath = "";postpone=false;omographId=0;baseWordId=0;stress=0;sameWordsNum=0;notInTheTable=true;
            
            currToken=tokens.get(i);
            
            if(currToken.getType().charAt(0)=='w'){// word
                if(saveQuery.containsKey(currToken.getValue())){
                    result=saveQuery.get(currToken.getValue());
                    notInTheTable=false;
                }else{
                    result=(ArrayList<Object[]>)em.createNativeQuery(query+currToken.getValue()+"' limit 1").getResultList();
                }
                if(result.size()>0){
                    if(notInTheTable)
                        saveQuery.put(currToken.getValue(), result);
                    vocalizePath =getResult(result.get(0),3,false);
                    baseWordId=getResult(result.get(0),1);
                    sameWordsNum=getResult(result.get(0),4);
                    omographId=getResult(result.get(0),2);
                    stress=getResult(result.get(0),0);
                    
                    postpone=dealWithOmograph(omographId,currToken,baseWordId,foundOmographPosition);
                    dealWithStress(stress,baseWordId,omographId,postpone,currToken,sameWordsNum);
                }else{
                    postpone=dealWithOmograph(currToken,foundOmographPosition);
                    dealWithStress(0,i*-1,0,postpone,currToken,0);
                }
                
                if(postpone)
                    foundOmographPosition=i;

                postpone=dealWithContext(postpone,currToken,foundOmographPosition,i,sameWordsNum);
            }else{//not word
            	addAtToken(currToken, i*-1, -1);
            	vocalizePath=currToken.getTranscribtion()+".wav";
            }
            
            addTokenIdIfMissed(omographId,currToken,baseWordId,i);
            
            if(!test){
                dealWithVocalization(vocalizePath,currToken,postpone,foundOmographPosition);
            }
       }
        if(fOut!=null){
          try {
              fOut.close();
          } catch (IOException ex) {
              Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
          }
      }
//async method
      addInTheTable();
      return fileName;
    }
    
    private int getResult(Object[] queryResult,int atPosition){
        if((queryResult.length>atPosition)&&(queryResult[atPosition]!=null)){
            return (Integer)queryResult[atPosition];
        }else{
            return 0;
        }
    }
    
    private String getResult(Object[] queryResult,int atPosition,boolean val){
        if((queryResult.length>atPosition)&&(queryResult[atPosition]!=null)){
            return (String)queryResult[atPosition];
       }else{
            return "";
        }
    }

    private boolean dealWithOmograph(int omographID,TokenI currToken,int baseWordId,int foundOmographPosition){
        int omographIdFound=0;

        if((omographID==0)||(omographID==-1)){
           return false;
        }else {
            if(myContext==null) {
                myContext=new HashMap<>();
            }
//                
//        	if((tokens.get(foundOmographPosition).getBaseTokenId()==0)&&(myContext.containsKey(lastOmographID))){
//        		addAtToken(tokens.get(foundOmographPosition),baseWordId,myContext.get(lastOmographID));
//        	}
        }
        
        if((myContext.isEmpty())||(lastOmographID!=omographID)){
            omographIdFound = fillMyContext(omographID,currToken);
        
            if(omographIdFound!=-1){
                lastOmographID=omographIdFound;//omographID;
                return true;
            }else{
                return false;
            }
        }
        if(myContext.containsKey(omographID)){
            return true;
         }
        return false;
    }
    
    private boolean dealWithOmograph(TokenI currToken,int foundOmographPosition){
        return dealWithOmograph(-2,currToken,0,foundOmographPosition);
    }
    
    private void dealWithStress(int stress,int baseWordId,int omographId,boolean postpone,TokenI currToken,int numberOfSameWords){
        if((postpone)||(omographId>0)){
            return;
        }
        if((stress<=0)||(currToken.getTrancribtionLen()<=stress)){//||(numberOfSameWords>1)){
        	addAtToken(currToken, baseWordId, -1);
        }else {
        	addAtToken(currToken,baseWordId,stress);
        }
    }
    
    private void dealWithVocalization(String vocalizePath,TokenI currToken,boolean postpone,int foundOmographPosition){
        byte[] wordVokalized=null;boolean vocalized;
        if(pending==null) {
           pending=new LinkedList<>();
        }
        if(postpone){
            
            pending.add(currToken.getTranscribtion().toString());
            return;
        }

        if(pending.size()>0){
            if(tokens.get(foundOmographPosition).getBaseTokenId()==0){
                if((vocalizePath!="")||(currToken.getType().charAt(0) !='w')) {//word
                    pending.add(vocalizePath);
                    return;
                }else{
                    pending.add(currToken.getTranscribtion().toString());
                    return;
                }
            }else{
                addPending();
            }
        }
        vocalized = false;
        if((vocalizePath!="")||(currToken.getType().charAt(0) !='w')) {//word
            vocalized = addSavedInFile(vocalizePath);
        }
        if(!vocalized){
            wordVokalized = vocalizator.vocalize(currToken.getTranscribtion().toString(), language);
            addInFile(wordVokalized,currToken.getTranscribtion().toString(),currToken);
        }
    }
    
    private void addPending(){
        byte[] wordVokalized=null;
        boolean vocalized = false;
        String first="";
        
        for(int i =0;i<pending.size();i++){
            first="";
            first=pending.pollFirst();
            
            if(first.endsWith(TYPE_OF_FILE)){
                 vocalized = addSavedInFile(first);
            }else{
                wordVokalized = vocalizator.vocalize(first, language);
                addInFile(wordVokalized,first,null);
            }
        }
    }

    private int fillMyContext(int omographID,TokenI currToken){
        String query="SELECT * FROM diplomna.omograf_id ", queryTail;
        ArrayList<String[]> result;
        
        if(omographID>0){
             queryTail= "WHERE id = "+omographID;
        }else{
            queryTail = "use index('for_omograph') WHERE word = '"+currToken.getValue()+"'";
        }
        
        result=(ArrayList<String[]>)em.createNativeQuery(query+queryTail).getResultList();
        if(result.size()==0){
            return -1;
        }
        
        int tmp = Integer.parseInt(result.get(0)[0]);
        if((omographID==0)&&(tmp>0)){
            omographID=tmp;
        }else {
        	return - 1;
        }
        
        if((omographID==lastOmographID)&&(!myContext.isEmpty())) {
        	return omographID;
        }else {
        	myContext.clear();
        }
        // default stress
        myContext.put(omographID, Integer.parseInt(result.get(0)[1]));
        int stressAt=0;
        
        query="SELECT * FROM diplomna.omograf_context WHERE id=";
        ArrayList<String[]>tmpResult;
        
        for(int i =3;i<result.get(0).length;i++){
        	stressAt=0;
        	
        	tmpResult=(ArrayList<String[]>)em.createNativeQuery(query+result.get(0)[i]).getResultList();
        	if(tmpResult.isEmpty()){
        		return omographID;
        	}
        	stressAt=Integer.parseInt(tmpResult.get(0)[3]);

        	for(int j=4;j<tmpResult.get(0).length;j++){
                    myContext.put(Integer.parseInt(tmpResult.get(0)[j]),stressAt);
        	}
        }
        
        return omographID;
    }
    
    private boolean dealWithContext(boolean postpone,TokenI currToken,int foundOmograph,int atPosition,int sameWordsNumber){
        int baseWordIdCurr,baseWordIdPrev;
        
        if((postpone)||(sameWordsNumber>1)){
            return true;
        }
        if((tokens.get(foundOmograph).getBaseTokenId()!=0)||(atPosition==0)){
            return false;
        }

//        if((myContext.isEmpty())||(atPosition-foundOmograph>contextOfOmographMAX)){
//            return false;
//        }
        if(myContext==null){
            myContext=new HashMap<>();
        }
        if((atPosition-foundOmograph>=contextOfOmographMAX)&&
                                     (myContext.containsKey(lastOmographID))){
            addAtToken(tokens.get(foundOmograph),lastOmographID,myContext.get(lastOmographID));
            return false;
        }
        if(lastFoundAt < foundOmograph-contextOfOmographMAX-1){
            lastFoundAt=foundOmograph;
        }
        int i=foundOmograph -(atPosition-foundOmograph);
        
        while((i<lastFoundAt)&&(!tokens.get(i).getType().equals("word")&&(i>=0))){
            i--;
        }
        lastFoundAt=i;
        if((i>=0)&&(tokens.get(i).getType().equals("word"))){
            baseWordIdPrev = tokens.get(i).getBaseTokenId();
            if(myContext.containsKey(baseWordIdPrev)){
                addAtToken(tokens.get(foundOmograph),lastOmographID ,myContext.get(baseWordIdPrev));
                return false;
            }
        }
        baseWordIdCurr = currToken.getBaseTokenId(); 
        
        if(myContext.containsKey(baseWordIdCurr)){
            addAtToken(tokens.get(atPosition),lastOmographID,myContext.get(baseWordIdCurr));
            return false;
        }
        return true;
    }

//	@Override
//	public void visit(TokenI currToken) throws Exception {
//		currToken.accept(this);
//		
//	}
	@Override
    public void visit(TokenI currToken) throws Exception {
       
//        if(stressPosition!=-1){
//            currToken.setBaseTokenId(baseWordId);
//            currToken.addStress(stressPosition);
//        }else if(stressAtOmograph!=-1){
//            currToken.setBaseTokenId(omograph_id_saved);
//            currToken.addStress(stressAtOmograph);
//        }else{
//        
//        }
//        
//        stressAtOmograph=-1;stressPosition=-1;
    }
    private void addAtToken(TokenI updateThis,int baseWordId,int stress){
        
    	updateThis.setBaseTokenId(baseWordId);
        if(stress>0) {
        	updateThis.addStress(stress);
        }
        
    }
    private void addTokenIdIfMissed(int omographId,TokenI currToken,int baseWordId, int atPosition) {
        
        if(currToken.getBaseTokenId()!=0){
            return;
        }
        if(omographId<=0){
            return;
        }
        if(baseWordId>0){
            addAtToken(currToken,baseWordId,0);
        }else{
            addAtToken(currToken,atPosition*-1,0);
        }
    }   

    private void addInFile(byte[] vocalized,String fileName,TokenI currToken){
        ByteArrayInputStream bIn=null;
        FileOutputStream saveFOut=null;
         byte[] fileArray;
         
        if((vocalized==null)||(vocalized.length==0)){
            return;
        }
        try{
            bIn = new ByteArrayInputStream(vocalized);
           
            if(fOut==null){
                openOutStream();
            }
            fileArray=new byte[1024];
 
           while((bIn.read(fileArray))!=-1){
               fOut.write(fileArray);
               fOut.flush();
               if((needToSaveWord())&&(currToken!=null)){
                   if(saveFOut==null){//?v preDestroy?
                	  // em.createNativeQuery("UPDATE diplomna.parametrization_vocalization SET vocalized="+vocalizedCNT+" where id=1");
                	   if(currToken.getBaseTokenId()>0){
                               if(vocalizedForUpdate == null)
                                   vocalizedForUpdate=new ArrayList<>();
                                vocalizedForUpdate.add(currToken);
//                               em.createNativeQuery("UPDATE diplomna.words SET words.vocalizePath='"+absPath+fileName+".wav' WHERE id="+currToken.getBaseTokenId());
                           }
                            saveFOut=new FileOutputStream(absPath+fileName+".wav");
                   }
                   saveFOut.write(fileArray);
                   saveFOut.flush();
               }
           }
           
       }catch(Exception e){
           System.out.println("!!!!!!!!!!! Exception "+e.getMessage());
           e.printStackTrace();
       }finally{
            if(bIn!=null){
                try {
                    bIn.close();
                } catch (IOException ex) {
                    Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
                }
            }  
            if(saveFOut!=null){
                try {
                    saveFOut.close();
                } catch (IOException ex) {
                    Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
                }
            }    
        }
    } 
    private boolean addSavedInFile(String vocalizedPath) {
    	FileInputStream fIn=null;
        FileOutputStream saveFOut=null;
         byte[] fileArray;
         
        if((vocalizedPath=="")||(vocalizedPath.length()==0)){
            return false;
        }
        try{

            if(fOut==null){
                openOutStream();
            }
            fIn = new FileInputStream(absPath+vocalizedPath);
           

            fileArray=new byte[1024];
 
           while((fIn.read(fileArray))!=-1){
               fOut.write(fileArray);
               fOut.flush();
           }
           return true;           
       }catch(Exception e){
           return false;
       }finally{
            if(fIn!=null){
                try {
                    fIn.close();
                } catch (IOException ex) {
                    Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
                }
            }    
       }
    }     	

    private boolean needToSaveWord(){
    	if(vocalizedCNT==-1){
    		String queryParam="select vocalized,vocalized_max from diplomna.parametrization_vocalization where id=1";
    		ArrayList<Object[]>param=(ArrayList<Object[]>)em.createNativeQuery(queryParam)
                                                            .getResultList();
    		vocalizedCNT =(Integer)param.get(0)[0];
    		vocalizedMAX=(Integer)param.get(0)[1];    
    	}
    	if(vocalizedCNT>=vocalizedMAX){
            return false;  
    	}else{
            //vocalizedCNT++;
            return true;
    	}
    } 
    
    private void openOutStream() throws FileNotFoundException{
        if(fOut==null){
            if(absPath ==""){
               absPath = new File("").getAbsolutePath().substring(0, new File("").getAbsolutePath().length()-3)+"generatedAudio\\";
            }
            fOut = new FileOutputStream(absPath+fileName,true);
        }
    }
    
    @PostConstruct
    private void initialize(){
        saveQuery =new HashMap();
    	vocalizedCNT=-1;
        vocalizedMAX=-1;
        absPath="";
        fOut=null;
        myContext=null;
        lastOmographID=0;
        lastFoundAt=0;
        fileName="";
        pending= null;
        absPath = new File("").getAbsolutePath().substring(0, new File("").getAbsolutePath().length()-3)+"generatedAudio\\";
        vocalizedForUpdate=null;
    }
    //@PreDestroy
    @Asynchronous
     private void addInTheTable(){
     //faster ejb reaction due to slow input
         em.createNativeQuery("UPDATE diplomna.parametrization_vocalization SET vocalized="+vocalizedCNT+" where id=1");
         if((vocalizedForUpdate!=null)&&(vocalizedForUpdate.size()>0)){
             for(int i =0; i<vocalizedForUpdate.size();i++){
                em.createNativeQuery("UPDATE diplomna.words SET words.vocalizePath='"+absPath+vocalizedForUpdate.get(i).getTranscribtion().toString()+".wav' WHERE id="+vocalizedForUpdate.get(i).getBaseTokenId());
             }
        }
     }
}