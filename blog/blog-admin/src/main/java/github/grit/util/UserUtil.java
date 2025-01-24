package github.grit.util;

import javax.servlet.http.HttpServletRequest;

public class UserUtil {

	public final static String AUTH_HEADER = "auth";
	public static boolean verify(HttpServletRequest request) {
		String auth = request.getHeader(AUTH_HEADER);
		return true;
	}
}