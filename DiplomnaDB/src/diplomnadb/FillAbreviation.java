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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maria Shindarova
 */
public class FillAbreviation {
    
    public void fillAbr(){
        Connection conn=null;
                
        try {
            Class.forName("com.mysql.jdbc.Driver");
            
            conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/diplomna?allowMultiQueries=true",
                        "root","mysql");
            System.out.println("Connection with MYSQL established "+conn.getMetaData().getDatabaseProductName());
 ///////////////Method calls           
            fillAbreviation(conn);
        
        } catch (SQLException ex) {
            Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }finally{
           try {
               if(conn!=null)
                    conn.close();
            } catch (SQLException ex1) {
                Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
     }


    private void fillAbreviation(Connection conn){

        Statement stm=null,stmChk=null; 
        int from=0,countSpace=0;
        boolean charFirst=false;
        String query="",queryChk, fileName = "\\build\\classes\\resource\\abrev_sukrasht.txt",line="";
        StringBuilder[] result=new StringBuilder[2];result[0]=new StringBuilder();result[1]=new StringBuilder();
        BufferedReader bf=null;
        
        try{
            bf = new BufferedReader(new FileReader(System.getProperty("user.dir")+fileName));
                      
            while((line=bf.readLine())!=null){
                query="";from=0;charFirst=false;countSpace=0;
                System.out.println("diplomnadb.DiplomnaDB.fillAbreviation()"+line);
                
                for(int i=0;i<line.length();i++){
                    if(line.charAt(i)=='|'){
                        result[from].delete(result[from].length()-countSpace, result[from].length());
                        countSpace=0;
                        from++;
                        charFirst=false;
                    }else{
                        if((Character.isAlphabetic(line.charAt(i)))||(line.charAt(i)=='.')
                                ||(line.charAt(i)=='-')||(line.charAt(i)=='-')){
                            result[from].append(line.charAt(i));
                            charFirst=true;
                            countSpace=0;
                        }else if((Character.isSpaceChar(line.charAt(i)))&&
                                 (charFirst)&&(from>0)){
                                countSpace++;
                                result[from].append(line.charAt(i));
                        }
                    }
                }

                result[from].delete(result[from].length()-countSpace, result[from].length());
                
                if(result[0].charAt(result[0].length()-1)=='.'){
                    result[0].deleteCharAt(result[0].length()-1);
                }
                System.out.println("result[0] -"+result[0]+"- result[1] -"+result[1]+"--");

                query="insert into diplomna.normalize (word, meaning) values ('"+result[0].toString()+"', '"+result[1].toString()+"')";
                    stm=conn.createStatement();
                    stm.executeUpdate(query);


                    System.out.println("Stateement done!");

                result[0].delete(0, result[0].length());
                result[1].delete(0, result[1].length());
                line="";
            }//end while

        }catch(SQLException ex){
            Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("diplomnadb.DiplomnaDB.fillAbreviation()" + ex);
            ex.printStackTrace();
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
                if(stm!=null)
                    stm.close();
            } catch (SQLException ex1) {
                Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex1);
            }
            try {
                if(bf!=null)
                    bf.close();
            } catch (IOException ex) {
                Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
