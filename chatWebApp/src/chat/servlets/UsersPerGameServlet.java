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


public class UsersPerGameServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //returning JSON objects, not HTML
        response.setContentType("application/json");
        try{
            String username = SessionUtils.getUsername(request);
            if (username == null) {
                response.sendRedirect(request.getContextPath() + "/index.html");
            }

            // add current game index
            int gameId= ServletUtils.getIntParameter(request, Constants.GAME_INDEX_PARAMETER);
            if (gameId == Constants.INT_PARAMETER_ERROR) {
                return;
            }

            UserManager userManager = ServletUtils.getUserManager(getServletContext());
            Player player = new Player(username, userManager.getIsComputerType(username), userManager.getUserId(username));
            GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
            if(gamesManager.getGameByIndex(gameId).gameDescriptor.isActive() == false) {
                gamesManager.addPlayerToGame(player, gameId);
                SingleGame singleGame= gamesManager.getGameByIndex(gameId);
                if(singleGame.gameDescriptor.getDynamicPlayers().getPlayers().size() == singleGame.gameDescriptor.getDynamicPlayers().getTotalPlayers())
                {
                    // start game
                    singleGame.gameDescriptor.startGame();

                }
            }
            else{
                throw new Exception("this game already started!");
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
