/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diplomna.clear.ejb.vocal;

import diplomna.clear.tools.Encoding;
import diplomna.clear.tools.UTF8Encoding;
import diplomna.clear.tools.token.TokenI;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Maria Shindarova
 */
@Stateless
public class VocalizeInput implements VocalizeInputLocal {
    static final String[] keys={"a","b","v","g","d","e","j","z","i","i_kr","k","l","m","n","o","p","r","s","t","u","f","h","c","ch","sh","sht","yy","","yyy","","iu","ia",
                                "a1","b1","v1","g1","d1","e1","j1","z1","i1","i_kr1","k1","l1","m1","n1","o1","p1","r1","s1","t1","u1","f1","h1","c1","ch1","sh1","sht1","yy1","","yyy1","","iu1","ia1",};
    Encoding encoding;
    String language="BG";
    byte[]word=null;
    String resourcesPath="resource\\diphon\\";
    int lenPreviousDiphone=0,transcribtionLen=0,writeFrom=0;
    //\DiplomnaRabotaClear\DiplomnaRabotaClear-ejb\src\java\diplomna\clear\ejb\vocal\resource\diphon
    
    public byte[] vocalize(String transcription,String language){// TokenI currToken,String language){
       char previousChar='\0',currChar;
       word=null;lenPreviousDiphone=0;transcribtionLen=0;writeFrom=0;
       //String transcription = currToken.getTranscribtion().toString();
       transcribtionLen=transcription.length();
        
        for(int i=0;i<=transcribtionLen;i++){
            if(i<transcription.length()){
                currChar=transcription.charAt(i);
            }else{
                currChar=previousChar;
            }
            processChar(currChar,i,previousChar,transcription.length());
            previousChar=currChar;
        }
        return word;
    }
    
    private void processChar(char currChar, int leterPosition,char prevChar,int lenTranscr){
        int key,tmpArrLen; 
        String fileName;
        File currFile=null;
        byte [] currLetter;
        boolean isItDiphone;
        
        fileName=genFileNameForChar((int)currChar, false);
        if(fileName.length()>0){
            if(leterPosition==0){
                fileName=fileName+"_bot.au";
            }else if(leterPosition==lenTranscr){
                fileName=fileName+"_eot.au";        
            }else{
                fileName= genFileNameForChar((int)prevChar, false)+fileName ;
                fileName=fileName +".au";
               
            }
            //System.out.println("234325356786957634321242536758675653423 vocalizator resorce path "+this.getClass().getResource("20_Pause.au").getPath());

            try{
                resourcesPath = this.getClass().getResource(fileName).getPath();
            }catch(NullPointerException ex){
                resourcesPath ="C:\\Users\\Misha\\Desktop\\diplomna\\DiplomnaRabotaClear\\DiplomnaRabotaClear-ejb\\build\\classes\\diplomna\\clear\\ejb\\vocal\\resource\\diphon\\"+fileName;
            }            
            currFile=new File(resourcesPath);

            if(currFile.isFile()&&currFile.canRead()){
                isItDiphone=true;
                readFile(isItDiphone,currFile,leterPosition);
                return;
            }
        }//else{
            fileName=genFileNameForChar((int)currChar, true);
            if(fileName.length()>0){
                if(leterPosition==0){
                    fileName=fileName+"_bot.au";
                }else if(leterPosition==lenTranscr){
                    fileName=fileName+"_eot.au";        
                }else{
                    fileName=  genFileNameForChar((int)prevChar, true)+fileName;
                    fileName=fileName +".au";
                }
                try{
                    resourcesPath = this.getClass().getResource(fileName).getPath();
                }catch(NullPointerException ex){
                    resourcesPath ="C:\\Users\\Misha\\Desktop\\diplomna\\DiplomnaRabotaClear\\DiplomnaRabotaClear-ejb\\build\\classes\\diplomna\\clear\\ejb\\vocal\\resource\\diphon\\"+fileName;
                }  
                currFile=new File(resourcesPath);

                if(currFile.isFile()&&currFile.canRead()){
                    isItDiphone=true;
                    readFile(isItDiphone,currFile,leterPosition);
                    return;
                }
            }//else{
                isItDiphone=true;
                fileName=genFileNameForChar((int)currChar, true);
                if(fileName.length()>0){
                   readFile(isItDiphone,new File(this.getClass().getResource(fileName+".au").getPath()),leterPosition);
                }
                
                fileName=genFileNameForChar((int)currChar, true);
                if(fileName.length()>0){
                  readFile(isItDiphone,new File(this.getClass().getResource(fileName+".au").getPath()),leterPosition);
                }
//currLetter=merge(first,second,first.length/2);
        //    }
       //}
//        tmpArrLen = currLetter.length;
//        //merge(word,currLetter,lenPreviousDiphone);
//        lenPreviousDiphone=tmpArrLen;
    }
    
    private String genFileNameForChar(int neededChar, boolean tryed){
        int key;String fileName;
        if(tryed){
           key = encoding.getNumLetterToKey((int)neededChar); 
           if((key<0)||(key>31)){
                return "";
            }
        }else{    
            key = encoding.getNumLetterToKeyVocal((int)neededChar);
            if((key<0)||(key>63)){
                return "";
            }
        }   
            
        fileName=keys[key];
        if(fileName=="") {
            return ""; 
        }
        return fileName;
    }
    
    private void merge(boolean isItDiphone,byte[]current,int totalReadFromCurr,int bytesPerFrame,long currFileSize,int leterPosition){
        int from =writeFrom;

        if(word==null){
            initializeArray(bytesPerFrame,currFileSize);
        }
        if(totalReadFromCurr>0){
            from=from+totalReadFromCurr;
        }
        if(word.length<=from+currFileSize+lenPreviousDiphone){
            resizeArray(bytesPerFrame,currFileSize,leterPosition);
        }
        for(int i=from;i<from+current.length;i++){
            if(word[i]==0){
                word[i]=(byte)current[i-from];
            }else {
                word[i]=(byte)((word[i]+current[i-from])/2);        
            }
        }
    }
    
    private void readFile(boolean isItDiphone,File currFile,int leterPosition){
        int bytesPerFrame,bytesRead=0,totalOfBytesRead=0,hlp=0; 
        AudioInputStream ais=null;
        byte[] audioBytes;
      
        try {    
            ais =AudioSystem.getAudioInputStream(currFile);//resourcesPath+fileName));
            bytesPerFrame = ais.getFormat().getFrameSize();
            if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
                    bytesPerFrame = 1;
            } 
    
            audioBytes = new byte[ais.available()];// * bytesPerFrame];
            bytesRead=ais.read(audioBytes);

            while(bytesRead!=-1){
                merge(isItDiphone,audioBytes,totalOfBytesRead,bytesPerFrame,currFile.length(),leterPosition);
                totalOfBytesRead=bytesRead+totalOfBytesRead;
                bytesRead=0;
                bytesRead=ais.read(audioBytes);
            }
            hlp=(totalOfBytesRead/2)%bytesPerFrame;
            if(hlp>0){
                hlp=(totalOfBytesRead/2)-hlp+bytesPerFrame;
            }else{
                hlp=totalOfBytesRead/2;
            }
            writeFrom=writeFrom+hlp;
            lenPreviousDiphone=totalOfBytesRead;
                    
        } catch (UnsupportedAudioFileException | IOException ex) {
            Logger.getLogger(VocalizeInput.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            if(ais!=null){ 
                try {
                    ais.close();
                } catch (IOException ex) {
                    Logger.getLogger(VocalizeInput.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    private void resizeArray(int bytesPerFrame,long currFileSize,int leterPosition){
        byte[] newByteArr;
        int size;
        
        if(currFileSize>lenPreviousDiphone){
            size=(int)currFileSize;
        }else{
            size=lenPreviousDiphone;
        }
        
        //size=(transcribtionLen-leterPosition-1)*size+writeFrom+lenPreviousDiphone;
        size=(word.length/leterPosition)*transcribtionLen+size;
        if(size<=word.length+lenPreviousDiphone){
            size=size+(size/3);
        }
        int hlp =size%bytesPerFrame;
	if(hlp>0) {
            size=size-hlp+bytesPerFrame;
	}
        newByteArr=new byte[size];
        for(int i =0;i<word.length;i++){
            newByteArr[i]=word[i];
        }
        word=newByteArr;
    }

    private void initializeArray(int bytesPerFrame,long currFileSize){
//        int size = (int) Math.rint((ais.getFormat().getFrameRate() * ais.getFormat().getFrameSize()) / rate);
//		size = (size + format.getFrameSize()) - (size % format.getFrameSize());
//		// size = size - size % format.getFrameSize();
//		size = Math.min(size, file.getByteLength());
       
        int  size=0, hlp=0;
        size=(int)(currFileSize*(transcribtionLen+2));
        hlp= (int)(size%bytesPerFrame);
        
        if(hlp>0){
            size=size+bytesPerFrame-hlp;
        }
        
        word=new byte[size];
    }
    
    @PostConstruct
    private void initialize(){
        try {
            encoding=UTF8Encoding.getInstance(language);
        } catch (Exception ex) {
            Logger.getLogger(VocalizeInput.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}