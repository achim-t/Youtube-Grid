
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

/**
 * Servlet implementation class Index
 */
@WebServlet("/")
public class Index extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		OAuthService service = (OAuthService) session
				.getAttribute("oauth2Service");
		OAuthRequest oReq = new OAuthRequest(Verb.GET,
				"https://www.googleapis.com/youtube/v3/subscriptions?part=snippet&mine=true");
		Token token = (Token)session.getAttribute("token");
		if (service == null){
			System.out.println("token is null");
			request.getRequestDispatcher("googleplus").forward(request, response);
			return;
		}
		System.out.println("token is not null");
		service.signRequest(token, oReq);
		Response oResp = oReq.send();
		String string = oResp.getBody();
		response.getWriter().println("Success?");
		response.getWriter().println(string);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
