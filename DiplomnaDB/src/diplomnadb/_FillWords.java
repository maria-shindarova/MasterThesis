/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diplomnadb;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Maria Shindarova
 */
public class _FillWords {
    
    public void fillWordsAndStress(){
        PreparedStatement stm=null; String query="";Connection conn=null,connW=null,conCHK=null;
        PreparedStatement stmOutAll=null; String queryOutAll="";Connection connOut=null;
        PreparedStatement stmOutStress=null; String queryOutStress="";
        PreparedStatement stmChk=null; String queryChk="";Connection connChk=null;
        
        ResultSet rs=null,result=null;
        String word=""; int stress=0,baseWordId=0;

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(_FillWords.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        try {
            query="";
            
            conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/rechko_second?allowMultiQueries=true","root","mysql");
            query="SELECT name,base_word_id,id FROM rechko_second.derivative_form";
            stm=conn.prepareStatement(query,java.sql.ResultSet.TYPE_FORWARD_ONLY,
                    java.sql.ResultSet.CONCUR_READ_ONLY);
            stm.setFetchSize(Integer.MIN_VALUE);
            
            result = stm.executeQuery(query);
     
            connW=DriverManager.getConnection("jdbc:mysql://localhost:3306/rechko_second?allowMultiQueries=true","root","mysql");
            
            while(result.next()){
                 stress =0;word="";baseWordId=0;
                if(connOut==null){
                    
                    connOut=DriverManager.getConnection("jdbc:mysql://localhost:3306/diplomna?allowMultiQueries=true","root","mysql");
                    queryOutAll="INSERT INTO diplomna.words (word,stressPlace,base_word_id) value (?,?,?);";
                    stmOutAll = connOut.prepareStatement(queryOutAll);
                }
                word=result.getString("name");
                baseWordId=result.getInt("base_word_id");

                    stress=findStress(baseWordId,connW);
                   stmOutAll.setString(1,word);
                   stmOutAll.setInt(2, stress);
                   stmOutAll.setInt(3, baseWordId);
                   stmOutAll.executeUpdate();
            }
               
         }catch(SQLException ex){
            Logger.getLogger(_FillWords.class.getName()).log(Level.SEVERE, null, ex);
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
                if(stmOutStress!=null)
                    stmOutStress.close();
            } catch (SQLException ex1) {
                ex1.printStackTrace();
                Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex1);
            }try {
                if(stmOutAll!=null)
                    stmOutAll.close();
            } catch (SQLException ex1) {
                ex1.printStackTrace();
                Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex1);
            }try {
                if(stmChk!=null)  
                    stmChk.close();
            } catch (SQLException ex1) {
                ex1.printStackTrace();
                Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex1);
            }try {
                if(connOut!=null)  
                    connOut.close();
            } catch (SQLException ex1) {
                ex1.printStackTrace();
                Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex1);
            }try {
                if(connChk!=null)  
                 connChk.close();
            } catch (SQLException ex1) {
                ex1.printStackTrace();
                Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex1);
            }try {
                if(conn!=null)  
                    conn.close();
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
    
    private int findStress(int baseWordId,Connection conn){
        Statement stm=null; String query="";
       try {
            query="";
            query="SELECT name_stressed FROM rechko_second.word where id="+baseWordId;
            
            stm=conn.createStatement();
            ResultSet result = stm.executeQuery(query);
            
            if(!result.isBeforeFirst()){ 
                return 0;
            }
            
            int id=0;String stressAtHlp ="";int stressAt=0;
            boolean notFound=true;int i=0;
                    
            result.first();
                  
            stressAtHlp="";stressAtHlp="";stressAt=0;
            stressAtHlp = result.getString("name_stressed");
            i=0;notFound=true;
                
            while((notFound)&&(i<stressAtHlp.length())){
                if((((int)stressAtHlp.charAt(i)<1072)||((int)stressAtHlp.charAt(i)>1103))&&
                  (((int)stressAtHlp.charAt(i)<1040)||((int)stressAtHlp.charAt(i)>1071))){

                    notFound=false;
                }
                i++;
            }
            if(!notFound){
                return i-1;
            }else{
                return 0;
            }   
         }catch(SQLException ex){
            Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("diplomnadb.DiplomnaDB.fillAbreviation()" + ex);
            ex.printStackTrace();
        }finally{
            try {
                stm.close();
            } catch (SQLException ex1) {
                Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex1);
                ex1.printStackTrace();
            }
        }
        return 0;
    }
}