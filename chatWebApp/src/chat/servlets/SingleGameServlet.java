package chat.servlets;


import Logic.GameDescriptor;
import Logic.Player;
import chat.constants.Constants;
import chat.utils.ServletUtils;
import chat.utils.SessionUtils;
import engine.chat.ChatManager;
import engine.singlegame.GamesManager;
import engine.table.TableManager;
import engine.users.UserContainer;
import engine.users.UserManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "SingleGameServlet", urlPatterns = {"/games"})
//@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class SingleGameServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        response.setContentType("application/json");
        try{
            TableManager tableManager = ServletUtils.getTableManager(getServletContext());
            String username = SessionUtils.getUsername(request);
            if (username == null) {
                response.sendRedirect(request.getContextPath() + "/index.html");
            }
            // add current user to game description
            int gameId= ServletUtils.getIntParameter(request, Constants.TABLE_VERSION_PARAMETER);
            if (gameId == Constants.INT_PARAMETER_ERROR) {
                return;
            }

            UserManager userManager = ServletUtils.getUserManager(getServletContext());
            Player player = new Player(username, userManager.getUserType(username), userManager.getUserId(username));
            GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
            gamesManager.addPlayerToGame(player, gameId);
        }
        catch (Exception e){

        }

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
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
        if (tableVersion  == Constants.INT_PARAMETER_ERROR) {
            return;
        }

        //TODO: get game json
    }
}


