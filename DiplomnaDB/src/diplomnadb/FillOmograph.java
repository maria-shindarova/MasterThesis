package diplomnadb;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

//@author Maria Shindarova

public class FillOmograph {
	private String driver;
    private String jndiToDestDB;
    private String uName;
    private String pWord;
    private Connection connDest=null;
    private int MAX_CONTEXT_FOR_OMOGRAPH=40;
    private int ID_OMOGRPH_CONTEXT=1;
    private int ID_OMOGRPH=1;
    private int From_ID=1;
    private String prevWord;
    
	public FillOmograph(String driver, String jndiToDestDB,String uName,String pWord) {
    	this.driver=driver;
        this.jndiToDestDB=jndiToDestDB;
        this.uName=uName;
        this.pWord=pWord;
	}
	
	public void run() {
		String fileName = "\\build\\classes\\resource\\omograf.txt" ;
		char currChar;
		InputStreamReader fr=null;
		StringBuilder word=new StringBuilder();
		int[] result = new int[MAX_CONTEXT_FOR_OMOGRAPH];
		int atPos=-1,id=0;
		boolean presisted=false;
		try{
            fr = new InputStreamReader(new FileInputStream(System.getProperty("user.dir")+fileName),"UTF-8");
                      
            while(fr.ready()){
            	currChar=(char)fr.read();
                if(((int)currChar>=1072)&&((int)currChar<=1103)){
                	if(!presisted) {
                		insertInDb(word.toString(),result);
                	}
                	
                	word.append(currChar);
                	
                }else if(((int)currChar>=1040)&&((int)currChar<=1071)){
                	if(!presisted) {
                		insertInDb(word.toString(),result);
                	}
                	word.append((char)(currChar+1072-1040));
                }else if(((int)currChar>=(int)'0')&&((int)currChar<=(int)'9')){
                	result[atPos]=result[atPos]*10+(int)currChar-(int)'0';
                	presisted=false;
                }else {
                	atPos++;//?????????????
                }
                if(atPos>=MAX_CONTEXT_FOR_OMOGRAPH) {
                	presisted=true;
            		insertInDb(word.toString(),result);                	
                }
            }
            
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
                if(fr!=null)
                    fr.close();
            } catch (IOException ex) {
                Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex);
            }try {
                if(connDest!=null)
                    connDest.close();
                connDest=null;
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
            connDest=DriverManager.getConnection(jndiToDestDB+"?allowMultiQueries=true",uName,pWord);
         
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DiplomnaDB.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (SQLException ex) {
            Logger.getLogger(RetriveOmographData.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    
    }
    
    private void insertInDb(String wordCurr,int[]values) {
    	//int[]values at[0] miasto na udarenie at[1-n] basova duma id
    	PreparedStatement stmOmoContext=null, stmOmoAll=null,stmInsInWord=null;
    	
    	if(connDest==null) {
    		connectToDb();
    	}
    	
    	StringBuilder tmp=new StringBuilder();
    	
    	StringBuilder query= new StringBuilder("insert into diplomna.omgraf_context(id,word,stress,");
    	
    	for(int i=0;((i<MAX_CONTEXT_FOR_OMOGRAPH)&&(i<values.length));i++) {
    		query.append("wordContextBaseId");
    		query.append(i+1);
    		query.append(",");
    		tmp.append("?,");
    	}
    	query.deleteCharAt(query.length()-1);
    	query.append(" ) values (");
    	
    	tmp.deleteCharAt(tmp.length()-1);
    	query.append(tmp);
    	query.append(")");
    	
    	try {
    		stmOmoContext=connDest.prepareStatement(query.toString());
    		stmOmoContext.setInt(1, ID_OMOGRPH_CONTEXT);
    		stmOmoContext.setString(2, wordCurr);
    		for(int i=0;((i<MAX_CONTEXT_FOR_OMOGRAPH)&&(i<values.length));i++) {
    			stmOmoContext.setInt(i+3,values[i]);
    		}
    		stmOmoContext.executeUpdate();

    	} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if(stmOmoContext!=null) {
					stmOmoContext.close();
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		ID_OMOGRPH_CONTEXT++;
	    
		if(!prevWord.equals(wordCurr)) {
			StringBuilder queryOmId = new StringBuilder("insert into diplomna.omgraf_id(id,word,");
			
			tmp.delete(0, tmp.length());
			
			for(int i=From_ID;i<ID_OMOGRPH_CONTEXT-1;i++) {
				queryOmId.append("id_omogrph");
				queryOmId.append(i);
				queryOmId.append(",");
	    		tmp.append("?,");
			}
			queryOmId.deleteCharAt(queryOmId.length()-1);
			queryOmId.append(" ) values (");
	    	
	    	tmp.deleteCharAt(tmp.length()-1);
	    	queryOmId.append(tmp);
	    	queryOmId.append(")");
	       	
	    	try {
	    		stmOmoAll=connDest.prepareStatement(queryOmId.toString());
	    		stmOmoAll.setInt(1, ID_OMOGRPH);
	    		stmOmoAll.setString(2, wordCurr);
	    		
	    		for(int i=From_ID;i<ID_OMOGRPH_CONTEXT-1;i++) { 
	    			stmOmoAll.setInt(i+3,i);
	    		}
	    		stmOmoAll.executeUpdate();

	    	} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				try {
					if(stmOmoAll!=null) {
						stmOmoAll.close();
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}	    	
			String queryWord ="update diplomna.words set omographId=? where name=?";///ID_OMOGRPH wordCurr
	    	
			try {
	    		stmInsInWord=connDest.prepareStatement(queryWord);
	    		stmInsInWord.setInt(1, ID_OMOGRPH);
	    		stmInsInWord.setString(2, wordCurr);
	    		stmOmoAll.executeUpdate();

	    	} catch (SQLException e) {
	    		// TODO Auto-generated catch block
	    		e.printStackTrace();
	    	}finally {
				try {
					if(stmInsInWord!=null) {
						stmInsInWord.close();
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}	    	
			
			prevWord=wordCurr;
			ID_OMOGRPH++;
			From_ID=ID_OMOGRPH_CONTEXT-1;
		}
    }
}