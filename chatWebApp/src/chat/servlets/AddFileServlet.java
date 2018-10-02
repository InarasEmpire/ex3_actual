package chat.servlets;

import chat.utils.ServletUtils;
import chat.utils.SessionUtils;
import engine.table.TableManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.util.Scanner;

@WebServlet(name = "GetUserTableServlet", urlPatterns = {"/pages/addFile"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class AddFileServlet extends HttpServlet {


     @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
         response.sendRedirect("pages/gamesroom/gamesroom.html");
     }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            response.setContentType("application/json");

            PrintWriter out = response.getWriter();

            TableManager tableManager = ServletUtils.getTableManager(getServletContext());
            String username = SessionUtils.getUsername(request);
            if (username == null) {
                response.sendRedirect(request.getContextPath() + "../gamesroom.html");
            }
            // todo: exception user is not logged in

            // todo: et the filename in other way

            Part filePart = request.getPart("file1");
            String filenameString = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

            StringBuilder fileContent = new StringBuilder();
            fileContent.append(readFromInputStream(filePart.getInputStream()));

            if (filenameString != null && !filenameString.isEmpty() && filenameString.contains(".xml")) {
                //logServerMessage("Adding filename string from " + username + ": " + filenameString);
                synchronized (getServletContext()) {
                    tableManager.addTableEntry(filenameString, username, fileContent.toString());
                }
            }

          //  out.print(tableManager.toString());
        }
        catch (Exception e){
            String ss = e.getMessage();
        }
        // TODO: exception-> its not an xml file
    }

    private String readFromInputStream (InputStream inputStream){
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }

    }
