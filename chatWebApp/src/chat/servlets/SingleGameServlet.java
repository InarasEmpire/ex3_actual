package chat.servlets;


import Logic.Player;
import chat.constants.Constants;
import chat.utils.ServletUtils;
import chat.utils.SessionUtils;
import engine.singlegame.GamesManager;
import engine.table.TableManager;
import engine.users.UserManager;

import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//@WebServlet(name = "SingleGameServlet", urlPatterns = {"/singlegameroom"})
//@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class SingleGameServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        TableManager tableManager = ServletUtils.getTableManager(getServletContext());
        String username = SessionUtils.getUsername(request);
        if (username == null) {
            response.sendRedirect(request.getContextPath() + "/index.html");
        }

        /*
        verify chat version given from the user is a valid number. if not it is considered an error and nothing is returned back
        Obviousley the UI should be ready for such a case and handle it properly
         */
        int tableindex = ServletUtils.getIntParameter(request, Constants.GAME_INDEX_PARAMETER);
        if (tableindex  == Constants.INT_PARAMETER_ERROR) {
            return;
        }

        //TODO: update game moves
        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
        try {
            out.print(gamesManager.getGameJsonStatus(tableindex));
            out.flush();
        } catch (Exception e) {
            response.setStatus(400);
            out.print(e.getMessage());
            out.flush();
        }
    }
}


