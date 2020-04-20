/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diplomnadb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Misha
 */
public class FillWords {
    private java.util.Map<Integer,Integer> baseIdToStress;
    private String driver;
    private String jndiToSourceDB;
    private String jndiToDestDB;
    private String uName;
    private String pWord;
    private Connection connSource=null;
    private Connection connDest=null;
    
    public FillWords(String driver, String jndiToSourceDB,String jndiToDestDB,String uName,String pWord) {
    	baseIdToStress = new HashMap<Integer, Integer>();
    	this.driver=driver;
        this.jndiToSourceDB=jndiToSourceDB;
        this.jndiToDestDB=jndiToDestDB;
        this.uName=uName;
        this.pWord=pWord;
    }
    public void run() {
    	fillWordsAndStress();
    	fillBasewords();
    	
    	try {
            if(connDest!=null)
                connDest.close();
            connDest=null;
        } catch (SQLException ex1) {
            Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex1);
        }
    	try {
            if(connSource!=null)
                connSource.close();
            connSource=null;
        } catch (SQLException ex1) {
            Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex1);
        }
    }
    
    private void fillWordsAndStress(){
        PreparedStatement stm=null; String query="";
        PreparedStatement stmOutAll=null; String queryOutAll="";
        ResultSet rs=null,result=null;
        int stress=0,baseWordId=0;

        if(connSource==null){
            connectToDb();
        }
        try {
            query="";
            
            query="SELECT name,base_word_id,id,is_infinitive FROM rechko_second.derivative_form";
            stm=connSource.prepareStatement(query,java.sql.ResultSet.TYPE_FORWARD_ONLY,
                    java.sql.ResultSet.CONCUR_READ_ONLY);
            stm.setFetchSize(Integer.MIN_VALUE);
            
            result = stm.executeQuery(query);
                
            while(result.next()){
                 stress =0;baseWordId=0;
                if(connDest==null){
                    connectToDb();
                }
                queryOutAll="INSERT INTO diplomna.words (word,stressPlace,base_word_id) value (?,?,?);";
                stmOutAll = connDest.prepareStatement(queryOutAll);
                
                baseWordId=result.getInt("base_word_id");
                stress=findStress(baseWordId, result.getInt("is_infinitive"));
                stmOutAll.setString(1,result.getString("name"));
                stmOutAll.setInt(2, stress);
                stmOutAll.setInt(3, baseWordId);
                stmOutAll.executeUpdate();
            }
               
         }catch(SQLException ex){
            Logger.getLogger(FillWords.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("diplomnadb.DiplomnaDB.fillWords" + ex);
            ex.printStackTrace();
         }finally{
            try {
                if(stm!=null)
                    stm.close();
            } catch (SQLException ex1) {
                Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex1);
                ex1.printStackTrace();
            }try {
                if(stmOutAll!=null)
                    stmOutAll.close();
            } catch (SQLException ex1) {
                ex1.printStackTrace();
                Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex1);
            }try {
                if(result!=null)  
                    result.close();
            } catch (SQLException ex1) {
                ex1.printStackTrace();
                Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex1);
            }try {
                if(rs!=null)
                    rs.close();
            } catch (SQLException ex1) {
                ex1.printStackTrace();
                Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex1);
            }
    }
  }
    private int findStress(String stressAtHlp){
        
            boolean notFound=true;int i=0;
            i=0;notFound=true;
                
            while((notFound)&&(i<stressAtHlp.length())){
                if((((int)stressAtHlp.charAt(i)<1072)||((int)stressAtHlp.charAt(i)>1103))&&
                  (((int)stressAtHlp.charAt(i)<1040)||((int)stressAtHlp.charAt(i)>1071))){

                    notFound=false;
                }
                i++;
            }
            int res=0;
            if(!notFound){
            	res= i-1;
            }
            return res;
    }
    private int findStress(int baseWordId,int isInfinitiv){
        Statement stm=null; String query="";
        ResultSet result=null;
        
        if(baseIdToStress.containsKey(baseWordId)) {
            return baseIdToStress.get(baseWordId);
        }
        if(connSource==null) {
            connectToDb();
        }
       try {
            query="";
            query="SELECT name_stressed FROM rechko_second.word where id="+baseWordId;
            
            stm=connSource.createStatement();
            result = stm.executeQuery(query);
            
            if(!result.isBeforeFirst()){ 
                return 0;
            }
            
            String stressAtHlp ="";
            boolean notFound=true;int i=0;
                    
            result.first();
                  
            stressAtHlp="";
            stressAtHlp = result.getString("name_stressed");
            i=0;notFound=true;
                
            while((notFound)&&(i<stressAtHlp.length())){
                if((((int)stressAtHlp.charAt(i)<1072)||((int)stressAtHlp.charAt(i)>1103))&&
                  (((int)stressAtHlp.charAt(i)<1040)||((int)stressAtHlp.charAt(i)>1071))){

                    notFound=false;
                }
                i++;
            }
            int res=0;
            if(!notFound){
            	res= i-1;
            }
            if(isInfinitiv==1) {
            	baseIdToStress.put(baseWordId, res);
            }
            return res;

         }catch(SQLException ex){
            Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("diplomnadb.DiplomnaDB.fillAbreviation()" + ex);
            ex.printStackTrace();
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
        }
        return 0;
    }
    private void fillBasewords(){
        PreparedStatement stm=null; String query="";
        PreparedStatement stmOutAll=null; String queryOutAll="";
        ResultSet rs=null,result=null;
        int stress=0,baseWordId=0;

        if(connSource==null){
            connectToDb();
        }
        try {
            query="";
            
            query="SELECT name,id,name_stressed FROM rechko_second.word";
            stm=connSource.prepareStatement(query,java.sql.ResultSet.TYPE_FORWARD_ONLY,
                    java.sql.ResultSet.CONCUR_READ_ONLY);
            stm.setFetchSize(Integer.MIN_VALUE);
            
            result = stm.executeQuery(query);
                
            while(result.next()){
                 stress =0;baseWordId=0;
                 baseWordId=result.getInt("id");
                 if(!baseIdToStress.containsKey(baseWordId)) {
                	 if(connDest==null){
                		 connectToDb();
                	 }
                	 queryOutAll="INSERT INTO diplomna.words (word,stressPlace,base_word_id) value (?,?,?);";
                	 stmOutAll = connDest.prepareStatement(queryOutAll);
                
                	 stress=findStress(result.getString("name_stressed"));// triabva da e 1 no niama smisul da pulnia heshTablicata
                	 stmOutAll.setString(1,result.getString("name"));
                	 stmOutAll.setInt(2, stress);
                	 stmOutAll.setInt(3, baseWordId);
                	
                	 stmOutAll.executeUpdate();
                 }
            }
               
         }catch(SQLException ex){
            Logger.getLogger(FillWords.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("diplomnadb.DiplomnaDB.fillWords" + ex);
            ex.printStackTrace();
         }finally{
            try {
                if(stm!=null)
                    stm.close();
            } catch (SQLException ex1) {
                Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex1);
                ex1.printStackTrace();
            }try {
                if(stmOutAll!=null)
                    stmOutAll.close();
            } catch (SQLException ex1) {
                ex1.printStackTrace();
                Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex1);
            }try {
                if(result!=null)  
                    result.close();
            } catch (SQLException ex1) {
                ex1.printStackTrace();
                Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex1);
            }try {
                if(rs!=null)
                    rs.close();
            } catch (SQLException ex1) {
                ex1.printStackTrace();
                Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }
    
    private void connectToDb(){
        
        try {
            //"com.mysql.jdbc.Driver"
            //"jdbc:mysql://localhost:3306/rechko_second""root""mysql"
            Class.forName(driver);
            connSource=DriverManager.getConnection(jndiToSourceDB,uName,pWord);
            connDest=DriverManager.getConnection(jndiToDestDB,uName,pWord);
         
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (SQLException ex) {
            Logger.getLogger(RetriveOmographData.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    
    }

}