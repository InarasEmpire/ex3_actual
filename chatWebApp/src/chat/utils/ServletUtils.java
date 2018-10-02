package chat.utils;

import engine.table.TableManager;
import engine.users.UserManager;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import static chat.constants.Constants.INT_PARAMETER_ERROR;

public class ServletUtils {

	private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
	private static final String TABLE_MANAGER_ATTRIBUTE_NAME = "tableManager";

	/*
	Note how the synchronization is done only on the question and\or creation of the relevant managers and once they exists -
	the actual fetch of them is remained unchronicled for performance POV
	 */
	private static final Object userManagerLock = new Object();
	private static final Object tableManagerLock = new Object();

	public static UserManager getUserManager(ServletContext servletContext) {

		synchronized (userManagerLock) {
			if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
			}
		}
		return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
	}

	public static TableManager getTableManager(ServletContext servletContext) {
		synchronized (tableManagerLock) {
			if (servletContext.getAttribute(TABLE_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(TABLE_MANAGER_ATTRIBUTE_NAME, new TableManager ());
			}
		}
		return (TableManager ) servletContext.getAttribute(TABLE_MANAGER_ATTRIBUTE_NAME);
	}

	public static int getIntParameter(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException numberFormatException) {
			}
		}
		return INT_PARAMETER_ERROR;
	}
}
