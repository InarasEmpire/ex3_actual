package chat.servlets;

import chat.constants.Constants;
import chat.utils.SessionUtils;
import chat.utils.ServletUtils;
import engine.users.UserManager;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static chat.constants.Constants.USERNAME;

public class LoginServlet extends HttpServlet {

    private final String CHAT_ROOM_URL = "../gamesroom/gamesroom.html";
    private final String SIGN_UP_URL = "../signup/signup.html";
    private final String LOGIN_ERROR_URL = "/pages/loginerror/login_attempt_after_error.jsp";  // must start with '/' since will be used in request dispatcher...
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
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        if (usernameFromSession == null) {
            String usernameFromParameter = request.getParameter(USERNAME);
            if (usernameFromParameter == null) {
     
                response.sendRedirect(SIGN_UP_URL);
            } else {
                usernameFromParameter = usernameFromParameter.trim();

                synchronized (this) {
                    if (userManager.isUserExists(usernameFromParameter)) {
                        String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";
                
                        request.setAttribute(Constants.USER_NAME_ERROR, errorMessage);
                        getServletContext().getRequestDispatcher(LOGIN_ERROR_URL).forward(request, response);
                    } else {
                        //check if human
                        boolean isComputer = false;
                        if(request.getParameter(Constants.IS_COMPUTER) == null)
                        {
                            //checkbox not checked
                            request.getSession(true).setAttribute(Constants.IS_COMPUTER, false);
                        }
                        else
                        {
                            request.getSession(true).setAttribute(Constants.IS_COMPUTER, true);
                            isComputer = true;
                        }

                        Boolean isComputerFromParameter = isComputer;

                        userManager.addUser(usernameFromParameter, isComputerFromParameter);
           
                        request.getSession(true).setAttribute(Constants.USERNAME, usernameFromParameter);

                        System.out.println("On login, request URI is: " + request.getRequestURI());
                        response.sendRedirect(CHAT_ROOM_URL);
                    }
                }
            }
        } else {
            //user is already logged in
            response.sendRedirect(CHAT_ROOM_URL);
        }
    }


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
