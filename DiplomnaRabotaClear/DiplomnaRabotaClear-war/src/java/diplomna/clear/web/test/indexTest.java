/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.http://localhost:8080/DiplomnaRabotaClear-war/indexTest
 */
package diplomna.clear.web.test;

import diplomna.clear.ejb.facade.FacadeLocal;
import diplomna.clear.tools.token.TokenI;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.spi.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Maria Shindarova
 */
public class indexTest extends HttpServlet {
@EJB
FacadeLocal facade;
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session;
        InitialContext ic;
        FacadeLocal facade=null;
        response.setContentType("text/html;charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet index</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h3>Servlet index at " + request.getContextPath() + "</h3>");
            out.println("<h3>=======================Test=======================</h3>");
            
            session=request.getSession();
            facade = (FacadeLocal) session.getAttribute("LEXER");
            if(facade==null){
                try{
                    ic = new InitialContext();
                    facade=(FacadeLocal)ic.lookup("java:global/DiplomnaRabotaClear/DiplomnaRabotaClear-ejb/Facade!diplomna.clear.ejb.facade.FacadeLocal");
//  java:global/DiplomnaRabotaClear/DiplomnaRabotaClear-ejb/Facade!diplomna.clear.ejb.facade.FacadeLocal
//  java:app/DiplomnaRabotaClear-ejb/Facade!diplomna.clear.ejb.facade.FacadeLocal

                } catch (NamingException ex) {
                    Logger.getLogger(indexTest.class.getName()).log(Level.SEVERE, null, ex);
                    out.println(ex);
                    out.println("<h3>-------------"+ex+"</h3>");
                }
                session.setAttribute("LEXER", facade);
              }
            ArrayList<TokenI>text;
            String color="green";
            try {
            
            out.println("<h3>=======================Test LEXER NORMALIZATOR TRANSCRIPTION=======================</h3>");
                text = facade.testLexNormTranskr("с изход т.нар хиляда дали! 40 401989");
                out.println("<h5>----- Test case for: с изход т.нар хиляда дали! 40 401989</h5>");
                //TEST GENERATED LEN
                testLength(out, text,getSteamTestCase1());
                //TEST TOKENISATION
                testTokenisation(out, text, getSteamTestCase1());
                
                //TEST  LEXER NORMALIZATOR TRANSCRIPTION
                testLexerNormalTrancr(out, text, getAllLexNormTranscriptTestCase1());
                //TEST NORMALIZATION
                testNormalization(out, text, getNormalizationTestCase1());
                
                out.println("<h3>----------------TEST NEW CALL-------------------</h3><br>");
                out.println("<h5>----- Test case for: изход</h5>");
                text=null;
                text = facade.testLexNormTranskr("изход");
                //TEST GENERATED LEN
                testLength(out, text,getSteamTestCase2());
                //TEST TOKENISATION
                testTokenisation(out, text, getSteamTestCase2());
                
                //TEST LEXER NORMALIZATOR TRANSCRIPTION
                testLexerNormalTrancr(out, text, getAllLexNormTranscriptTestCase2() );
                               
            out.println("<h3>=======================Test PARSER STRESS ALL=======================</h3>");    
                out.println("<h5>----- Test case for: красива морска вълна</h5>");
                text=null;
                text = facade.testAllWithoutVocal("красива морска вълна");
                
                testStressOmograf(out,text,getOmografStressCase1(),4);

                out.println("<h5>----- Test case for: с изход т.нар хиляда дали! 40 401989 морска вълна</h5>");                
                text=null;
                text = facade.testAllWithoutVocal("с изход т.нар хиляда дали! 40 401989 морска вълна");
                testAllGeneratedData(out,text,getALLCase1());
               
                out.println("<h5>----- Test case for: изход</h5>");                
                text=null;
                text = facade.testAllWithoutVocal("изход");
                testAllGeneratedData(out,text,getAllCase2());
                
            } catch (Exception ex) {
                Logger.getLogger(indexTest.class.getName()).log(Level.SEVERE, null, ex);
                out.println(ex);
                out.println("<h3>-------------"+ex+"</h3>");
            }
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    
    private void testLength(PrintWriter out,ArrayList<TokenI>text,String[] testCase){
        String color="green";
        out.println(" <Br>");   
        out.println("<h3>------------------TEST GENERATED LEN-----------------</h3><br>");
        String msg1 = "PASSED";;
        if(text.size()!=testCase.length){
            color="red"; 
            msg1 = "NOT PASSED";
        }
        out.println("<h3 style='color: "+color+"'>"+msg1+"text.len"+text.size()+"</h3><br>"); 
        out.println("<hr>");
    }
    
    private void testTokenisation(PrintWriter out,ArrayList<TokenI>text,String[] TestCaseLex){
        out.println("<h3>--------------TEST TOKENISATION---------------------</h3><br>");
        StringBuilder msg=new StringBuilder();
        String color="green";
                
        for(int i=0;i<text.size();i++){
            if (!text.get(i).getValue().toString().equals(TestCaseLex[i])){
                if(msg.length()==0)
                   msg.append("FAILD ERR AT: ");
                color="red"; 
                msg.append(" at line "+i+", ");
            }
        }
        if(msg.length()==0)
             msg.append("PASSED");

        out.println("<h3 style='color: "+color+"'>"+msg+"</h3><br>"); 
        out.println(" <Br>");
        TestCaseLex=null;
        out.println("<hr>");
    }
    
    private void testLexerNormalTrancr(PrintWriter out,ArrayList<TokenI>text,String[] TestCaseLex){
        
        out.println("<h3>--------------TEST LEXER, NORMALIZE & TRANSCRIPTION---------------------</h3><br>");
        StringBuilder msg=new StringBuilder("");
        String color="green";
                
        for(int i=0;i<text.size();i++){
            if (!text.get(i).toString().equals(TestCaseLex[i])){
                out.println("-"+text.get(i).toString()+"-  -"+TestCaseLex[i]+"-");
                if(msg.length()==0)
                   msg.append("FAILD ERR AT: ");
                color="red"; 
                msg.append(" at line "+i+", ");
            }
        }
         if(msg.length()==0)
             msg.append("PASSED");
        out.println("<h3 style='color: "+color+"'>"+msg+"</h3><br>"); 

        out.println(" <Br>");
        out.println("<hr>");
    }
    
    private void testNormalization(PrintWriter out,ArrayList<TokenI>text,String[] TestCaseLex){
        out.println("<h3>--------------TEST NORMALIZATION---------------------</h3><br>");
        StringBuilder msg=new StringBuilder("");
        String color="green";

        for(int i=0;i<text.size();i++){
            if((!text.get(i).getValue().toString().equals(TestCaseLex[i]))&&(TestCaseLex[i]!="")){
                if(msg.length()==0)
                   msg.append("FAILD ERR AT: ");
                color="red"; 
                msg.append(" at line "+i+", ");
            }
        }
         if(msg.length()==0)
             msg.append("PASSED");
        out.println("<h3 style='color: "+color+"'>"+msg+"</h3><br>"); 
        out.println(" <Br>");
        out.println("<hr>");    
    
    }
    private void testStressOmograf(PrintWriter out,ArrayList<TokenI>text,String[] TestCaseLex,int positionOmograph){
        out.println("<h3>--------------TEST Omograph Stress---------------------</h3><br>");
        StringBuilder msg=new StringBuilder();StringBuilder tmp=new StringBuilder();
        String color="green";

        for(int i=0;i<text.size();i++){
            
            if((!text.get(i).getTranscribtion().toString().equals(TestCaseLex[i]))&&(TestCaseLex[i]!="")){
                if(i==positionOmograph){
                    if(msg.length()==0)
                        msg.append("FAILD ERR AT: ");
                    color="red"; 
                    msg.append(" at position "+i+", ");
                }else{
                    if(tmp.length()==0)
                        tmp.append(" Other problems AT: ");
                    tmp.append(" at position "+i+", ");
                }
            }
        }
         if(msg.length()==0)
             msg.append("PASSED");
        msg.append(tmp);
        out.println("<h3 style='color: "+color+"'>"+msg+"</h3><br>"); 
        out.println(" <Br>");
        out.println("<hr>"); 
    
    }
    
    private void testAllGeneratedData(PrintWriter out,ArrayList<TokenI>text,String[] TestCaseLex){
        
        out.println( "<h3>--------------TEST GENERATED DATA FROM BACKEND---------------------</h3><br>");
        StringBuilder msg=new StringBuilder("");
        String color="green";
                
        for(int i=0;i<text.size();i++){
            if (!text.get(i).toString().equals(TestCaseLex[i])){
                out.println("-"+text.get(i).toString()+"-  -"+TestCaseLex[i]+"-");
                if(msg.length()==0)
                   msg.append("FAILD ERR AT: ");
                color="red"; 
                msg.append(" at line "+i+", ");
            }
        }
         if(msg.length()==0)
             msg.append("PASSED");
        out.println("<h3 style='color:"+color+"'>"+msg+"</h3><br>"); 

        out.println(" <Br>");
        out.println("<hr>");
    }
//    {"с {-с-} {-111847-}","{-10Pause-} {--1-}","изход {-Исхот-} {-31693-}","{-10Pause-} {--3-}",
//"така {-така-} {-105418-}","{-10Pause-} {--5-}","наречен {-наречен-} {-101732-}","{-10Pause-} {--7-}",
//"хиляда {-хИляда-} {-102948-}","{-10Pause-} {--9-}","10 дали {-дали-} {-100441-}","! {-20Pause-} {--11-}",
//"{-10Pause-} {--12-}","четиридесет {-четирИдесет-} {-102924-}","{-10Pause-} {--14-}","четиристотин {-четиристотин-} {-102945-}",
//"{-10Pause-} {--16-}","и {-и-} {-102858-}","{-10Pause-} {--18-}","едно {-едно-} {-102896-}",
//"{-10Pause-} {--20-}","хиляди {-хиляди-} {-96129-}","{-10Pause-} {--22-}","деветстотин {-деветстотин-} {-102941-}",
//"{-10Pause-} {--24-}","осемдесет {-осемдесет-} {-102910-}","{-10Pause-} {--26-}",
//"и {-и-} {-102858-}","{-10Pause-} {--28-}","девет {-девет-} {-102930-}","{-10Pause-} {--30-}",
//"морска {-морска-} {-25922-}","{-10Pause-} {--32-}","вълна {-вЪлна-} {-14210-}"}
private String[] getSteamTestCase1(){
    String[] temp= {"с"," ","изход"," ","така"," ","наречен"," ","хиляда"," ","дали","!",
    " ","четиридесет"," ","четиристотин"," ","и"," ","едно"," ","хиляди"," ","деветстотин",
    " ","осемдесет"," ","и"," ","девет"};
return temp;
}
private String[] getNormalizationTestCase1(){
    String[] temp= {"","","","","така"," ","наречен"," ","хиляда"," ","","",
    "","четиридесет"," ","четиристотин"," ","и"," ","едно"," ","хиляди"," ","деветстотин",
    " ","осемдесет"," ","и"," ","девет"};
return temp;
}
private String[] getAllLexNormTranscriptTestCase1(){
    String[] temp= {"с {-с-} {-0-}","  {-10Pause-} {-0-}","изход {-исхот-} {-0-}","  {-10Pause-} {-0-}",
"така {-така-} {-0-}","  {-10Pause-} {-0-}","наречен {-наречен-} {-0-}","  {-10Pause-} {-0-}",
"хиляда {-хиляда-} {-0-}","  {-10Pause-} {-0-}","дали {-дали-} {-0-}","! {-20Pause-} {-0-}",
"  {-10Pause-} {-0-}","четиридесет {-четиридесет-} {-0-}","  {-10Pause-} {-0-}","четиристотин {-четиристотин-} {-0-}",
"  {-10Pause-} {-0-}","и {-и-} {-0-}","  {-10Pause-} {-0-}","едно {-едно-} {-0-}",
"  {-10Pause-} {-0-}","хиляди {-хиляди-} {-0-}","  {-10Pause-} {-0-}","деветстотин {-деветстотин-} {-0-}",
"  {-10Pause-} {-0-}","осемдесет {-осемдесет-} {-0-}","  {-10Pause-} {-0-}",
"и {-и-} {-0-}","  {-10Pause-} {-0-}","девет {-девет-} {-0-}","  {-10Pause-} {-0-}"};
return temp;
}

private String[] getSteamTestCase2(){
    String[] temp= {"изход"};
return temp;
}
private String[] getAllLexNormTranscriptTestCase2(){
    String[] temp= {"изход {-исход-} {-0-}"};
return temp;
}

private String[] getOmografStressCase1(){
String[] temp= {"красИва"," ", "мОрска","10Pause","вЪлна"};
return temp;
}

private String[] getALLCase1(){
    String[] temp= {"с {-с-} {-111847-}","  {-10Pause-} {--1-}","изход {-Исхот-} {-31693-}","  {-10Pause-} {--3-}",
"така {-така-} {-105418-}","  {-10Pause-} {--5-}","наречен {-наречен-} {-101732-}","  {-10Pause-} {--7-}",
"хиляда {-хИляда-} {-102948-}","  {-10Pause-} {--9-}","дали {-дали-} {-100441-}","! {-20Pause-} {--11-}",
"  {-10Pause-} {--12-}","четиридесет {-четирИдесет-} {-102924-}","  {-10Pause-} {--14-}","четиристотин {-четиристотин-} {-102945-}",
"  {-10Pause-} {--16-}","и {-и-} {-102858-}","  {-10Pause-} {--18-}","едно {-едно-} {-102896-}",
"  {-10Pause-} {--20-}","хиляди {-хиляди-} {-96129-}","  {-10Pause-} {--22-}","деветстотин {-деветстотин-} {-102941-}",
"  {-10Pause-} {--24-}","осемдесет {-осемдесет-} {-102910-}","  {-10Pause-} {--26-}",
"и {-и-} {-102858-}","  {-10Pause-} {--28-}","девет {-девет-} {-102930-}","  {-10Pause-} {--30-}",
"морска {-морска-} {-25922-}","  {-10Pause-} {--32-}","вълна {-вЪлна-} {-14210-}"};
return temp;
}
private String[] getAllCase2(){
    String[] temp= {"изход {-Исход-} {-31693-}"};
return temp;
}
}
