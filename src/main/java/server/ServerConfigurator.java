package server;

import database.AccountService;
import database.DBService;
import database.TestDBService;
import frontend.Constants;
import frontend.Frontend;
import messageSystem.AddressService;
import messageSystem.MessageSystem;
import org.eclipse.jetty.rewrite.handler.RedirectRegexRule;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.Servlet;

/*
 * Created by maxim on 11.03.14.
 */
public class ServerConfigurator {
    static public Server ConfigureServer(Integer port) {
        MessageSystem messageSystem = new MessageSystem(new AddressService());
        Frontend frontend = new Frontend(messageSystem);
        AccountService accountService;
        if(port == Constants.TEST_PORT)
            accountService = new AccountService(new TestDBService(), messageSystem);
        else {
            accountService = new AccountService(new DBService(), messageSystem);
        }

        new Thread(frontend).start();
        new Thread(accountService).start();

        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(frontend), "/*");

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(false);
        resource_handler.setResourceBase("static");

        RewriteHandler rewriteHandler = new RewriteHandler();
        rewriteHandler.setRewriteRequestURI(true);
        rewriteHandler.setRewritePathInfo(true);
        rewriteHandler.setOriginalPathAttribute("requestedPath");
        RedirectRegexRule rule = new RedirectRegexRule();
        rule.setRegex("/");
        rule.setReplacement(Constants.Url.INDEX);
        rewriteHandler.addRule(rule);

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{rewriteHandler , resource_handler, context });
        server.setHandler(handlers);
        return server;
    }
}
