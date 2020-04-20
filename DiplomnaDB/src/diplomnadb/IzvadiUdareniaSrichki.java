/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diplomnadb;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author  Maria Shindarova
 */
public class IzvadiUdareniaSrichki {
     private java.util.Map<String,String> srichkiOtDuma;
    private String driver;
    private String jndiToSourceDB;
    private String uName;
    private String pWord;
    private Connection connSource=null;
    private boolean T=true; 
    private boolean F=false;            
    ////                                  а б в г д е ж з и й к л м н о п р с т у ф х ц ч ш щ ъ - ь - ю я
    private boolean checkWhereStress[] = {T,F,F,F,F,T,F,F,T,T,F,F,F,F,T,F,F,F,F,T,F,F,F,F,F,F,T,F,T,F,T,T}; 
            
    public IzvadiUdareniaSrichki(String driver, String jndiToSourceDB,String uName,String pWord) {
    	srichkiOtDuma = new HashMap<String,String>();
    	this.driver=driver;
        this.jndiToSourceDB=jndiToSourceDB;
        this.uName=uName;
        this.pWord=pWord;
    }
    
    public int findStress(){
        Statement stm=null; String query="";
        ResultSet result=null;
        FileWriter fw=null;
        
        if(connSource==null) {
            connectToDb();
        }
       try {
            query="";
            query="SELECT name_stressed FROM rechko_second.word where name_stressed!=''";
            
            stm=connSource.createStatement();
            result = stm.executeQuery(query);
            
            if(!result.isBeforeFirst()){ 
                return 0;
            }
            String stressAtHlp ="";boolean notFound=true;int i=0;String srichka;
            int CHECK, stressAtPos,before,after;
            while(result.next()){
                
                stressAtHlp="";stressAtHlp = result.getString("name_stressed");
                
                if((stressAtHlp!=null)&&(stressAtHlp!="")){

                    i=0;notFound=true;

                    while((notFound)&&(i<stressAtHlp.length())){
                        
                        if((((int)stressAtHlp.charAt(i)<1072)||((int)stressAtHlp.charAt(i)>1103))&&
                          (((int)stressAtHlp.charAt(i)<1040)||((int)stressAtHlp.charAt(i)>1071))){
                            stressAtPos=0;before=0;after=0;
                            notFound=false;
                            if(i-1>=0){
                                CHECK=0;CHECK=stressAtHlp.charAt(i-1)-1072;
                                if((CHECK<0)||(CHECK>=checkWhereStress.length)){
                                    CHECK=0;CHECK=stressAtHlp.charAt(i-1)-1040;
                                }
                                System.out.println(stressAtHlp.charAt(i-1)+" "+stressAtHlp+" "+(stressAtHlp.charAt(i-1)-1040));
                                if((CHECK<checkWhereStress.length)&&(CHECK>=0)&&(checkWhereStress[CHECK])){
                                    stressAtPos =i-1;
                                    after=1;
                                }
                            }
                            if((i+1<stressAtHlp.length())&&(stressAtPos==0)){
                                CHECK=0;CHECK=stressAtHlp.charAt(i+1)-(int)'а';
                                if((CHECK<0)||(CHECK>=checkWhereStress.length)){
                                    CHECK=0;CHECK=stressAtHlp.charAt(i+1)-(int)'А';
                                }
                                if((CHECK<checkWhereStress.length)&&(CHECK>=0)&&checkWhereStress[CHECK]){
                                    stressAtPos=i+1;
                                    before=1;
                                }
                            }
                        
                        if(stressAtPos!=0){
                            if(stressAtPos-1-before>=0){
                                srichka=stressAtHlp.charAt(stressAtPos-1-before)+""+stressAtHlp.charAt(stressAtPos);
                            }else{
                                srichka="BOT_"+stressAtHlp.charAt(stressAtPos);
                            }
                            if(!srichkiOtDuma.containsKey(srichka)){
                                srichkiOtDuma.put(srichka, stressAtHlp);
                            }
                            
                            if(stressAtPos+1+after<stressAtHlp.length()){
                                srichka=stressAtHlp.charAt(stressAtPos)+""+stressAtHlp.charAt(stressAtPos+1+after);
                            }else{
                                srichka=stressAtHlp.charAt(stressAtPos)+"_EOT";
                            }
                            if(!srichkiOtDuma.containsKey(srichka)){
                                srichkiOtDuma.put(srichka, stressAtHlp);
                            }
                            if((stressAtHlp.length()==1)&&(!srichkiOtDuma.containsKey(stressAtHlp))){
                                srichkiOtDuma.put(stressAtHlp, stressAtHlp);
                            }
                        }
                    }
                    i++;
                }
            }
            }
            Set<Map.Entry<String,String>> values = srichkiOtDuma.entrySet();
            
            Map.Entry<String,String> currVal;
            
            fw = new FileWriter("C:\\Users\\Misha\\Desktop\\srichkiResult.txt");
            Iterator<Map.Entry<String,String>> iter = values.iterator();
            
            while(iter.hasNext()){
                currVal= iter.next();
                fw.write(" / "+currVal.getKey()+" - "+currVal.getValue());
                fw.write(System.getProperty("line.separator"));
                fw.flush();
            }
            
         }catch(SQLException ex){
            Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("diplomnadb.DiplomnaDB.fillAbreviation()" + ex);
            ex.printStackTrace();
        } catch (IOException ex) {
             Logger.getLogger(IzvadiUdareniaSrichki.class.getName()).log(Level.SEVERE, null, ex);
         }finally{
            try {
            	if(stm!=null) {
            		stm.close();}
            } catch (SQLException ex1) {
                Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex1);
                ex1.printStackTrace();
            }
            try {
            	if(result!=null) {
            		result.close();
            	}
            } catch (SQLException ex1) {
                Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex1);
                ex1.printStackTrace();
            }
            try {
            if(fw!=null){
                fw.close();
            }
            } catch (IOException ex1) {
                Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex1);
                ex1.printStackTrace();
            }
        }
        return 0;
    }
    private void connectToDb(){
        
        try {
            //"com.mysql.jdbc.Driver"
            //"jdbc:mysql://localhost:3306/rechko_second""root""mysql"
            Class.forName(driver);
            if(connSource==null){
            connSource=DriverManager.getConnection(jndiToSourceDB,uName,pWord);
            }
            
         
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (SQLException ex) {
            Logger.getLogger(RetriveOmographData.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    
    }
}
