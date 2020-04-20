/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diplomna.clear.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.json.Json;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javax.json.JsonObject;
import diplomna.clear.ejb.facade.FacadeLocal;
import diplomna.clear.web.test.indexTest;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import javax.servlet.annotation.WebServlet;

/**
 *
 * @author Misha
 */
@WebServlet(name="index")
public class index extends HttpServlet {
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
		String serverIP,serverPort,server;
		response.setContentType("text/html;charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<mata http-eqiv='Content-Type' content='text/html; charset=utf-8'/>");
            out.println("<script type=\"text/javascript\" src=\"ServerComunication.js\"></script>");
            out.println("<title>Дипломна Работа</title>");            
            out.println("</head>");
            out.println("<body>");
            
            serverIP=request.getServerName();
    		serverPort=Integer.toString(request.getServerPort());
    		server="http://"+serverIP+":"+serverPort+request.getContextPath();
    		
    		out.println("<input type='hidden' id='server' value='"+server+"'>" );
            out.println(
"            <table>\n" +
"             <tr>\n" +
"                <td>\n" +
"                    <div><p>Моля, въведете текст:</p></div>\n" +
"                    <textarea id='textPotrebitel' rows='40%' cols='80%'></textarea>\n" +
"                </td>\n" +
"                <td>\n" +
"                    <img src='"+ request.getContextPath() +"\\picture\\btn1.jpg' onclick='javascript:sendMSG()' width='60' height='60' alt='Submit'/>\n" +
"                </td></table>");
            out.println("<input type='hidden' id='textPotrebitelLen' value=0>");
            out.println("<audio src='' type='hidden' id='audio' volume=1.0></audio>");
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
    	
    	String header = request.getHeader("Content-type");
    	//String parameter =request.("processText").toString();
        
    	BufferedReader reader;
        StringBuilder parameter;
        System.out.println(" header   "+header+" request "+request.toString());   
    	if((header!=null)&&(header.startsWith("app"))) {
            
            parameter=new StringBuilder();
            reader = request.getReader();
            while(reader.ready()){
                parameter.append(reader.read());
            }
            if(parameter.length()==0){
                parameter.append(request.getParameter("processText").toString());
            
            }
            
    		processXMLHttpRequest(parameter.toString(),request, response);
    	}else {
    		processRequest(request, response);
    	}
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
    	request.setCharacterEncoding("UTF-8");
        String header = request.getHeader("Content-type");
    	//String parameter =request.("processText").toString();
    	BufferedReader reader;
        StringBuilder parameter;
        String encode, test;

    	if((header!=null)&&(header.startsWith("app"))) {
            
            parameter=new StringBuilder();
            reader = request.getReader();
            
            while(reader.ready()){
                parameter.append(reader.readLine());
            }
            encode= new String(parameter);
            test = request.getParameter("processText").toString();
           
            if(parameter.length()==0){
                parameter.append(request.getParameter("processText").toString().getBytes(StandardCharsets.UTF_8));
            }

    		processXMLHttpRequest(parameter.toString(),request, response);
    	}else {
    		processRequest(request, response);
    	}
    }
    
    protected void processXMLHttpRequest(String parameter,HttpServletRequest request, HttpServletResponse response){
    	HttpSession session; InitialContext ic; FacadeLocal facade=null;
    	String urlResult=""; JsonObject jsonOBJ;
    	
    	session=request.getSession();
        facade = (FacadeLocal) session.getAttribute("LEXER");

        if(facade==null){
            try{
                ic = new InitialContext();
                facade=(FacadeLocal)ic.lookup("java:global/DiplomnaRabotaClear/DiplomnaRabotaClear-ejb/Facade!diplomna.clear.ejb.facade.FacadeLocal");
                session.setAttribute("LEXER", facade);
            
            } catch (NamingException ex) {
                Logger.getLogger(indexTest.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
                try (PrintWriter out = response.getWriter()) {
                	ex.printStackTrace();
                	out.println("<h1>-------------"+ex+"</h1>");
                }catch (Exception e) {
                	e.printStackTrace();
                	Logger.getLogger(indexTest.class.getName()).log(Level.SEVERE, null, e);
				}
            }
        }
        
        try {
			
        	urlResult=facade.processInstructions(parameter);
		          System.out.println("generated urlResult  "+urlResult);
        } catch (Exception e) {
			e.printStackTrace();
			Logger.getLogger(indexTest.class.getName()).log(Level.SEVERE, null, e); 
			try (PrintWriter out = response.getWriter()) {
             	out.println("<h1>-------------"+e+"</h1>");
             }catch (Exception ex) {
             	ex.printStackTrace();
             	Logger.getLogger(indexTest.class.getName()).log(Level.SEVERE, null, ex);
			 }
		}
        
        response.setContentType("application/json");
        response.setLocale(Locale.PRC);
        //response.setHeader("Access-Control-Allow-Origin", "*");
        response.setStatus(200);
        try (PrintWriter out = response.getWriter()) {
        	jsonOBJ=Json.createObjectBuilder()
        			.add("urlResult",urlResult)
        			.build();
            
            out.println(jsonOBJ);
            response.flushBuffer();
            out.flush();
            out.close();
    }catch (Exception e) {
    	e.printStackTrace();
    	Logger.getLogger(indexTest.class.getName()).log(Level.SEVERE, null, e);
	}
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

}