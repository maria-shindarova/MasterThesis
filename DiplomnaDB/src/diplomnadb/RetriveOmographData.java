package diplomnadb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Maria Shindarova
 */

public class RetriveOmographData {

    private Map<String,String> omograph= new HashMap();
    private Map<String,Map<Integer,Integer>> used = new HashMap();
    private Connection conn=null;
    private QueueDiplomnaOmograph queue;
    private String driver;
    private String jndiToSourceDB;
    private String uName;
    private String pWord;
    
    public RetriveOmographData(String driver, String jndiToSourceDB,String uName,String pWord){
        this.driver=driver;
        this.jndiToSourceDB=jndiToSourceDB;
        this.uName=uName;
        this.pWord=pWord;
        queue = new QueueDiplomnaOmograph();
              
    }
    
    public void retriveData() {
        //"com.mysql.jdbc.Driver"
       //"jdbc:mysql://localhost:3306/rechko_second""root""mysql"
        connectToDb();
        fillOmographs();
        omographContext();
        calculateOmographContext();
        
        
        //sled redakcia v words otmetka + novata tablica kum neia pole za udarenie
        try {
                if(conn!=null)
                    conn.close();
                conn=null;
            } catch (SQLException ex1) {
                Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex1);
            }
        
    }
    
    private void omographContext(){
        String fileName = "C:\\Users\\Misha\\Desktop\\testRetriveOmographData.txt",
//"C:\\Users\\Misha\\Desktop\\resource_diplomna\\re4nici\\wikiDump.xml",
                line="";
        BufferedReader bf=null; StringBuilder word = new StringBuilder();
        String prevLine="",omographFound=""; boolean checked = false;
        int saveNextWords=0;FileReader fr=null;
        char currChar;
        try{
            fr=new FileReader(fileName);
            
            while((currChar = (char)fr.read())!=-1){
                    if(((int)currChar>=1072)&&((int)currChar<=1103)){
                        word.append((char)currChar);
                        checked = false;
                    }else if(((int)currChar>=1040)&&((int)currChar<=1071)){
                        word.append((char)(currChar+1072-1040));
                        checked = false;
                    }else{
                        if((!checked)&&(word.length()>0)){
                            if(omograph.containsKey(word.toString())){
                                omographFound=word.toString();
                                prevLineInContext(omographFound);
                                saveNextWords = 6;
                                queue.addElement("");
                                if(saveNextWords>0){
                                    saveNextWords=0;
                                }
                            }else if(saveNextWords>0){
                                addWordForContext(omographFound,word);
                                saveNextWords--;
                                queue.addElement(word.toString());
                            }else{
                                omographFound="";
                                queue.addElement(word.toString());
                            }
                            checked = true;
                        }
                        word.delete(0,word.length());
                    }
                //}// end for
                prevLine=line;
                line="";
            }//end  while
        
        }catch (FileNotFoundException ex) {
            Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("diplomnadb.DiplomnaDB.fillAbreviation()" + ex);
            ex.printStackTrace();
        }catch (IOException ex) {
            Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("diplomnadb.DiplomnaDB.fillAbreviation()" + ex);
            ex.printStackTrace();
        }finally{
            try {
                bf.close();
            } catch (IOException ex) {
                Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void addWordForContext(String omographCurr, StringBuilder wordContext){
        PreparedStatement stm=null; String query="";ResultSet result=null;
        int baseWordId=0;
        if(conn==null){
            connectToDb();
        }
        try {
            query="";
            query="SELECT base_word_id FROM rechko_second.derivative_form where name = ?";
            
            stm=conn.prepareStatement(query);
            stm.setString(1, wordContext.toString());
            result = stm.executeQuery();
            if(result.isBeforeFirst()){
                result.first();
                baseWordId = result.getInt("base_word_id");
            }
        }catch(SQLException ex){
            Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("diplomnadb.DiplomnaDB.fillAbreviation()" + ex);
            ex.printStackTrace();
        }finally{
            try {
                if(stm!=null)
                    stm.close();
            } catch (SQLException ex1) {
                Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex1);
            } try {
                if(result!=null)
                    result.close();
            } catch (SQLException ex1) {
                Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex1);
            }
            
        }
        String baseOmograph;
       if(omograph.containsKey(omographCurr)&&(baseWordId!=0)){
           baseOmograph = omograph.get(omographCurr);
           Map<Integer,Integer> tmp;
           if(used.containsKey(baseOmograph)){
               tmp = used.get(baseOmograph);
               if(tmp.containsKey(baseWordId)){
                   tmp.put(baseWordId,tmp.get(baseWordId)+1);
               }else{
                tmp.put(baseWordId, 1);
              }
               used.put(baseOmograph, tmp);
           }
           
       }
    }
    
    private void prevLineInContext(String omographCurr){
        String[] res =queue.getAll();
        int i=0;boolean notOmograph=true;
        
        while((i<res.length)&&(notOmograph)){
            if(res[i]==""){
                notOmograph=false;
            }else{
                addWordForContext(omographCurr,new StringBuilder(res[i]));
            }
          i++;
        }
    }
    
    private void fillOmographs(){    
        
        String fileName = "\\build\\classes\\resource\\omograph.txt",line="";
        BufferedReader bf = null; StringBuilder currWord = new StringBuilder();
        StringBuilder stress = new StringBuilder();
        int i=0; boolean notFound = true;
                        
        try{
            bf = new BufferedReader(new FileReader(System.getProperty("user.dir")+fileName));
           
            while((line=bf.readLine())!=null){
                i =0;notFound=true;
                while((i<line.length())&&(notFound)){
                    if(((int)line.charAt(i)>=1072)&&((int)line.charAt(i)<=1103)){
                        currWord.append((char)((int)line.charAt(i)));
                    }else if(((int)line.charAt(i)>=1040)&&((int)line.charAt(i)<=1071)){
                        currWord.append(line.charAt(i)+1072-1040);
                    }else if(line.charAt(i)=='/'){
                        if(omograph.containsKey(currWord.toString())){
                            notFound=false;
                        }
                        if(stress.length()>0){
                            stress.append('/');
                        }
                    }else if(((int)line.charAt(i)>='0')&&((int)line.charAt(i)<='9')){
                        stress.append(line.charAt(i));
                    }
                     i++;
                }//end inner while
                            
                if(notFound){
                    processOmograph(currWord.toString(),stress);
                }
//                process stress?!
                
                stress.delete(0, stress.length()) ;
                currWord.delete(0, currWord.length());
                line="";
            }//end outer while
            
            System.out.println("diplomnadb.RetriveOmographData.fillOmographs()");
        
        }catch (FileNotFoundException ex) {
            Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("diplomnadb.DiplomnaDB.fillAbreviation()" + ex);
            ex.printStackTrace();
        }catch (IOException ex) {
            Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("diplomnadb.DiplomnaDB.fillAbreviation()" + ex);
            ex.printStackTrace();
        }finally{
            try {
                bf.close();
            } catch (IOException ex) {
                Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void processOmograph(String currWord,StringBuilder stress) {
        Statement stm=null; String query="";
        Statement stmAll=null; String queryAll="";
        int i=0;boolean notFound=true;
        ResultSet result=null, resultAll=null;String name="";int base_word_id=0;
        
        if(conn==null){
            connectToDb();
        }
        
        try {
            query="";
            query="SELECT base_word_id FROM rechko_second.derivative_form where name = '"+currWord+"'";
            
            stm=conn.createStatement();
            result = stm.executeQuery(query);
                       
            //while(result.next()){ 
            result.first();
            base_word_id=0; base_word_id=result.getInt("base_word_id");
            if(!used.containsKey(currWord)){
                used.put(currWord,new HashMap());
                
                queryAll="";
                queryAll="SELECT name FROM rechko_second.derivative_form where base_word_id= '"+result.getInt("base_word_id")+"'";
                stmAll=conn.createStatement();
                resultAll =null;
                resultAll = stmAll.executeQuery(queryAll);

                while(resultAll.next()){
                    i=0;notFound=true;
                    name="";name=resultAll.getString("name");

                    while((notFound)&&(i<name.length())){
                        if(name.charAt(i)==' '){
                            notFound=false;
                        }
                        i++;
                    }
                    if(notFound){
                        if(!omograph.containsKey(name)){
                            
                            omograph.put(name,currWord);
                            
                        }
                    }
                }
            }
            //}
               
         }catch(SQLException ex){
             ex.printStackTrace();
        }finally{
            try {
                if(stm!=null)
                    stm.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
            try {
                if(resultAll!=null)
                    resultAll.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            try {
                if(result!=null)
                    result.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
        }
    }
   private void connectToDb(){
        
        try {
            //"com.mysql.jdbc.Driver"
            //"jdbc:mysql://localhost:3306/rechko_second""root""mysql"
            Class.forName(driver);
            conn=DriverManager.getConnection(jndiToSourceDB,uName,pWord);
         
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (SQLException ex) {
            Logger.getLogger(RetriveOmographData.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    
    }
   private void calculateOmographContext(){
       Map<Integer,Integer> omogrphContext;
       String neededWord;
       Set<String> keysUsed= used.keySet();
       Set<Integer> keysContextWords;
       Iterator<String> iteratorUsed =keysUsed.iterator();
       Iterator<Integer> iteratorContext;
       int hlpContext;
        BufferedWriter bf=null;         
        try{
            bf = new BufferedWriter(new FileWriter("C:\\Users\\Misha\\Desktop\\result.txt"));
            
        while(iteratorUsed.hasNext()){
           neededWord=iteratorUsed.next();
           omogrphContext= used.get(neededWord);
           bf.append(neededWord);
           keysContextWords=omogrphContext.keySet();
           
           iteratorContext=keysContextWords.iterator();  
           while (iteratorContext.hasNext()){
               hlpContext=iteratorContext.next();
               bf.append("//"+hlpContext+"/ "+omogrphContext.get(hlpContext)+" ");
           }
           bf.flush();
           bf.newLine();
       }
        }catch (IOException ex) {
            Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("diplomnadb.DiplomnaDB.fillAbreviation()" + ex);
            ex.printStackTrace();
        }finally{
            try {
                if(bf!=null)
                    bf.close();
            } catch (IOException ex) {
                Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
   }
}
