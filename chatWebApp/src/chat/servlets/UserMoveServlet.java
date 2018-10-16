package chat.servlets;

import Logic.Player;
import chat.constants.Constants;
import chat.utils.ServletUtils;
import chat.utils.SessionUtils;
import engine.singlegame.GamesManager;
import engine.singlegame.SingleGame;
import engine.users.UserManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class UserMoveServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //returning JSON objects, not HTML
        response.setContentType("application/json");
        try{
            String username = SessionUtils.getUsername(request);
            if (username == null) {
                response.sendRedirect(request.getContextPath() + "/index.html");
            }

            // get current game index
            String gamecolumn= request.getParameter("type");

            int gameId= ServletUtils.getIntParameter(request, Constants.GAME_INDEX_PARAMETER);
            if (gameId == Constants.INT_PARAMETER_ERROR) {
                return;
            }
            GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
            SingleGame currGame= gamesManager.getGameByIndex(gameId);

            if(username.equals(currGame.gameDescriptor.getDynamicPlayers().getActivePlayer().getName()) == false)
            {
                throw new Exception("not this players turn to play!");
            }
            if(currGame.gameDescriptor.isActive() == false){
                throw new Exception("the game isn't started yet!");
            }


            UserManager userManager = ServletUtils.getUserManager(getServletContext());
            if(userManager.getIsComputerType(username))
            {
                currGame.gameDescriptor.play();
            }
            else {
                String colValue = request.getParameter("col");
                if(request.getParameter("type").equals("pushin")) {
                    currGame.gameDescriptor.executePlayerMove(Integer.parseInt(colValue), currGame.gameDescriptor.getGame().getBoard().getPushInOperation());
                }
                else{
                    currGame.gameDescriptor.executePlayerMove(Integer.parseInt(colValue), currGame.gameDescriptor.getGame().getBoard().getPopOutOperation());
                }
            }
        }
        catch (Exception e){
            PrintWriter out = response.getWriter();
            out.print(e.getMessage());
            out.flush();
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
