/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diplomnadb;

/**
 * @@author Maria Shindarova
//        https://github.com/chitanka/sites-rbe-data
//        https://rechnik.chitanka.info/about
//        http://fontsgeek.com/fonts/TmsCyr-Regular
 */
public class DiplomnaDB {
    
    public static void main(String[] args) {



//        RetriveOmographData omogr=new RetriveOmographData("com.mysql.jdbc.Driver",
//       "jdbc:mysql://localhost:3306/rechko_second","root","mysql");
//        omogr.retriveData();
        FillWords wordsAndStress= new FillWords("com.mysql.jdbc.Driver",
                                            "jdbc:mysql://localhost:3306/rechko_second",
                                            "jdbc:mysql://localhost:3306/diplomna?allowMultiQueries=true",
                                            "root","mysql");
        wordsAndStress.run();

//        FillAbreviation abr = new FillAbreviation();
//        abr.fillAbr();
//        
//        FillOmograph fillOmograph = new FillOmograph("com.mysql.jdbc.Driver",
//                                            "jdbc:mysql://localhost:3306/rechko_second",
//                                            "root","mysql");
//        fillOmograph.run();
    }

  
}
