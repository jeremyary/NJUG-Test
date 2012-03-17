import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.MeetupApi;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Scanner;

public class HelloWorld extends HttpServlet {

    //private static final String PROTECTED_RESOURCE_URL = "http://api.meetup.com/2/member/self";
    private static final String PROTECTED_RESOURCE_URL = "http://api.meetup.com/events?group_urlname=nashvillejug";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        OAuthService service = new ServiceBuilder()
                .provider(MeetupApi.class)
                .apiKey("90efnc66mnm4v36nalqf22drtj")
                .apiSecret("uogbkpbgq78ig49piarb35rl3t")
                .build();
        Scanner in = new Scanner(System.in);

        Token requestToken = service.getRequestToken();
        Verifier verifier = new Verifier(in.nextLine());
        Token accessToken = service.getAccessToken(requestToken, verifier);

        OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, request);
        Response response = request.send();

        resp.getWriter().print(response.getBody());
    }

    public static void main(String[] args) throws Exception{
        Server server = new Server(Integer.valueOf(System.getenv("PORT")));
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        context.addServlet(new ServletHolder(new HelloWorld()),"/*");
        server.start();
        server.join();   
    }
}