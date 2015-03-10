

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Google2Api;
import org.scribe.oauth.OAuthService;


@WebServlet("/googleplus")
public class GooglePlusServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String CLIENT_ID = "331645637746-72r1bogrn14ud43p02cqhoq53vopi9ft.apps.googleusercontent.com"; 
	   private static final String CLIENT_SECRET = "qq14xBzl0IjKmzKVI2HWUrT0";
    

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServiceBuilder builder= new ServiceBuilder();
		OAuthService service = builder.provider(Google2Api.class) 
		         .apiKey(CLIENT_ID) 
		         .apiSecret(CLIENT_SECRET) 
		         .callback("http://localhost:8080/Youtube/oauth2callback") 
		         .scope("https://www.googleapis.com/auth/youtube.readonly")  
		         .debugStream(System.out) 
		         .build(); //Now build the call
		HttpSession session = request.getSession();
		session.setAttribute("oauth2Service", service);
		response.sendRedirect(service.getAuthorizationUrl(null));
	}
}
