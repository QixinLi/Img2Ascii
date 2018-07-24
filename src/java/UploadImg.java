/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author lee
 */
@WebServlet(urlPatterns = {"/UploadImg"})
public class UploadImg extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String image = request.getParameter("image");   
            String lastindex = request.getParameter("lastname");
            
            String header ="data:image/"+lastindex+";base64,";   
            if(image.indexOf(header) != 0){  
                response.getWriter().print(wrapJSON(false,""));  
                return;  
            }
            // 去掉头部  
            image = image.substring(header.length());  
            // 写入磁盘  
            boolean success = false;  
            String imgcode="";
            for(int i=0;i<3;i++){
                Random ra=new Random();
                int raint=ra.nextInt(10);
                imgcode+=raint;
            }
            Base64.Decoder decoder = Base64.getDecoder();  
            try{  
                byte[] decodedBytes = decoder.decode(image);  
                String imgFilePath="";
                if(lastindex.equals("gif")){
                    imgFilePath = STATIC.dir+"uploadimg//"+imgcode+".gif";
                }
                else if(lastindex.equals("jpeg")){
                    imgFilePath = STATIC.dir+"uploadimg//"+imgcode+".jpeg";
                }
                else if(lastindex.equals("png")){
                    imgFilePath = STATIC.dir+"uploadimg//"+imgcode+".jpeg";
                }
                else{
                    response.getWriter().print("{\"success\":\"格式解析失败！\"}");  
                }
                FileOutputStream outp = new FileOutputStream(imgFilePath);  
                outp.write(decodedBytes);  
                outp.close();  
                success = true;
            }catch(Exception e){  
                success = false;  
                e.printStackTrace();
            } 
            if(lastindex.equals("gif")){
                ImageToChar.Gif2Ascii(imgcode+".gif",imgcode+".gif");
            }
            else{
                ImageToChar.Jpeg2Ascii(imgcode+".jpeg",imgcode+".jpeg");
            }
            
            response.getWriter().print(wrapJSON(success,imgcode));  
        }  
        
    }
 private String wrapJSON(boolean success,String code){  
        if(success)
        {
            return "{\"success\":\"图片上传成功！\",\"imgcode\":\""+code+"\"}"; 
        }
        else
        {
            return "{\"success\":\"图片上传失败！\"}"; 
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

}
