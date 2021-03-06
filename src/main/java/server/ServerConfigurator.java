package server;

import VFS.VFS;
import database.AccountServicePack;
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
import resources.ResourceFactory;
import resources.Url;

import java.util.Iterator;

/*
 * Created by maxim on 11.03.14.
 */

public class ServerConfigurator {
    static public Server ConfigureServer(Integer port) {
        MessageSystem messageSystem = new MessageSystem(new AddressService());
        Frontend frontend = new Frontend(messageSystem);
        AccountServicePack accountServicePack = new AccountServicePack(port, messageSystem, 2);

        new Thread(frontend).start();
        accountServicePack.startAccountServices();

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
        rule.setReplacement(((Url) ResourceFactory.instance().get("url.xml")).getIndex());
        rewriteHandler.addRule(rule);

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{rewriteHandler , resource_handler, context });
        server.setHandler(handlers);
        return server;
    }

    static public void loadResources(String path) {
        VFS vfs = new VFS("");
        Iterator<String> iterator = vfs.getIterator(path);
        while(iterator.hasNext()) {
            String nextIter = iterator.next();
            if(!vfs.isDirectory(nextIter))
                ResourceFactory.instance().putResource(nextIter,
                        ResourceFactory.instance().getResource(vfs.getAbsolutePath(nextIter)));
        }
    }
}
