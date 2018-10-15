package chat.servlets;

import chat.constants.Constants;
import chat.utils.ServletUtils;
import chat.utils.SessionUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import engine.table.SingleTableEntry;
import engine.table.TableManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class TableServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        TableManager tableManager = ServletUtils.getTableManager(getServletContext());
        String username = SessionUtils.getUsername(request);
        if (username == null) {
            response.sendRedirect(request.getContextPath() + "/index.html");
        }

        /*
        verify chat version given from the user is a valid number. if not it is considered an error and nothing is returned back
        Obviousley the UI should be ready for such a case and handle it properly
         */
        int tableVersion = ServletUtils.getIntParameter(request, Constants.TABLE_VERSION_PARAMETER);
        if (tableVersion == Constants.INT_PARAMETER_ERROR) {
            return;
        }

        /*
        Synchronizing as minimum as I can to fetch only the relevant information from the chat manager and then only processing and sending this information onward
        Note that the synchronization here is on the ServletContext, and the one that also synchronized on it is the chat servlet when adding new chat lines.
         */
        int tableManagerVersion = 0;
        List<SingleTableEntry> tableEntries;
        synchronized (getServletContext()) {
            tableManagerVersion = tableManager.getVersion();
            tableEntries = tableManager.getTableEntries(tableVersion);
        }

        // log and create the response json string
        TableAndVersion tav = new TableAndVersion(tableEntries, tableManagerVersion);
        try {
            logServerMessage("Server Chat version: " + tableManagerVersion + ", User '" + username + "' Chat version: " + tableVersion);
            logServerMessage(tableManager.toString());

            try (PrintWriter out = response.getWriter()) {
                out.print(tableManager.toString());
                out.flush();
            }
        }
        catch(Exception e)
        {
            PrintWriter out = response.getWriter();
            out.print(e.getMessage());
            out.flush();
        }
    }

    private void logServerMessage(String message){
        System.out.println(message);
    }
    
    class TableAndVersion {

        final private List<SingleTableEntry> entries;
        final private int version;

        public TableAndVersion(List<SingleTableEntry> entries, int version) {
            this.entries = entries;
            this.version = version;
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
