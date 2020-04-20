/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diplomnadb;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maria Shindarova
 */
public class fill_broiEdnakvi {
       
    private String driver;
    private String jndiToSourceDB;
    private String jndiToDestDB;
    private String uName;
    private String pWord;
    private Connection connSource=null;
    private Connection connDest=null;
        
    public fill_broiEdnakvi(String driver, String jndiToSourceDB,String jndiToDestDB,String uName,String pWord) {
    	
    	this.driver=driver;
        this.jndiToSourceDB=jndiToSourceDB;
        this.jndiToDestDB=jndiToDestDB;
        this.uName=uName;
        this.pWord=pWord;
    }
    
    public void run() {
        PreparedStatement stm=null, stmOutAll=null; 
        ResultSet result=null;
    	String queryOut="",query="";
                
        if(connSource==null){
            connectToDb();
        }
        try {
//            query = "select name,count(*) c from rechko_second.derivative_form group by name having(c>1)";
//            
//            stm=connSource.prepareStatement(query);
//            ,java.sql.ResultSet.TYPE_FORWARD_ONLY,
//                                            java.sql.ResultSet.CONCUR_READ_ONLY);
//            stm.setFetchSize(Integer.MIN_VALUE);
//            
//            result = stm.executeQuery(query);
            BufferedReader br =new BufferedReader( new FileReader("C:\\Users\\Misha\\Desktop\\dumiEdnakviSbroi.csv"));
           
           String line;
            queryOut="update diplomna.words set broi_ednakvi=? where word=?";
            int i =0;boolean notFound =true;int hlp=0,hlpS=0;
            //while(result.next()){
            while(br.ready()){
                line=br.readLine();
                i =line.length()-1; notFound =true;
                
                
                while((notFound)&&(i>=0)){//line.length())){
                    if(line.charAt(i)==','){
                        notFound=false;
                    }
//                    }else {
//                        if(line.charAt(i)!='"'){
//                            word.append(line.charAt(i));
//                        }
//                    }
                    i--;
                }
                hlp=0;hlpS=0;
                if((!notFound)&&(line.length()-1-i>0)){//word.length()>0)){
                    
                    stmOutAll = connDest.prepareStatement(queryOut);
                    hlp=line.charAt(line.length()-1)-'0';
                    if((hlp>=0)&&(hlp<=9)){
                        stmOutAll.setInt(1,hlp);//Integer.parseInt(line.substring(i)));//result.getInt(2));
                        
                        hlpS=0;
                        if(line.charAt(i)=='"'){
                            hlpS=1;
                        }
                        stmOutAll.setString(2, line.substring(0+hlpS,i+1-hlpS));//word.toString());//result.getString(1));
                        stmOutAll.executeUpdate();
                        System.out.println("line "+line);
                    }
                }
               // word.delete(0, word.length());
                try {
                    if(stmOutAll!=null)
                        stmOutAll.close();
                } catch (SQLException ex1) {
                    ex1.printStackTrace();
                    Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
                	
        }catch(SQLException ex){
            Logger.getLogger(FillWords.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("diplomnadb.DiplomnaDB.fillWords" + ex);
            ex.printStackTrace();
         } catch (FileNotFoundException ex) {
            Logger.getLogger(fill_broiEdnakvi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(fill_broiEdnakvi.class.getName()).log(Level.SEVERE, null, ex);
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
           }    
            
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
    }
    private void connectToDb(){
        
        try {
            //"com.mysql.jdbc.Driver"
            //"jdbc:mysql://localhost:3306/rechko_second""root""mysql"
            Class.forName(driver);
            //connSource=DriverManager.getConnection(jndiToSourceDB,uName,pWord);
            
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