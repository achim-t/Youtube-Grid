
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

/**
 * Servlet implementation class OAuth2CallbackServlet
 */
@WebServlet(asyncSupported = true, urlPatterns = { "/oauth2callback" })
public class OAuth2CallbackServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String error = req.getParameter("error");
		if ((error != null) && (error.trim().equals("access_denied"))) {
			HttpSession session = req.getSession();
			session.invalidate();
			resp.sendRedirect(req.getContextPath());
			return;

		}
		HttpSession session = req.getSession();
		OAuthService service = (OAuthService) session
				.getAttribute("oauth2Service");
		String code = req.getParameter("code");
		Token token = service.getAccessToken(null, new Verifier(code));
		session.setAttribute("token", token);
		resp.sendRedirect("./index");
//		req.getRequestDispatcher("/Index").forward(req, resp);
//		OAuthRequest oReq = new OAuthRequest(Verb.GET,
//				"https://www.googleapis.com/youtube/v3/subscriptions?part=snippet&mine=true");
//		service.signRequest(token, oReq);
//		Response oResp = oReq.send();
//		String string = oResp.getBody();
//		resp.getWriter().println("Success?");
//		resp.getWriter().println(string);
	}
}
