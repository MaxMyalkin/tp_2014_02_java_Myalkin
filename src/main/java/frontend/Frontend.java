package frontend;

import database.AccountService;
import database.UsersDataSet;
import helpers.CommonHelper;
import message.MsgGetUser;
import message.MsgRegistrate;
import messageSystem.Abonent;
import messageSystem.Address;
import messageSystem.MessageSystem;
import resources.Message;
import resources.Page;
import resources.ResourceFactory;
import resources.Url;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static helpers.CommonHelper.sleep;


public class Frontend extends HttpServlet implements Runnable, Abonent {

    private final MessageSystem messageSystem;
    private final Address address;
    private final Map<String, UserSession> sessions = new HashMap<>();

    public Frontend(MessageSystem messageSystem) {
        this.messageSystem = messageSystem;
        this.address = new Address();
        this.messageSystem.addAbonent(this);
    }

    public MsgGetUser makeMsgGetUser(Address from, Address to, String login, String password, String sessionID) {
        return new MsgGetUser(from, to, login, password, sessionID);
    }

    public MsgRegistrate makeMsgRegistrate(Address from, Address to, String login, String password, String sessionID) {
        return new MsgRegistrate(from, to, login, password, sessionID);
    }


    public UserSession addUserSession(String login , String sessionID){
        UserSession userSession = new UserSession(login,sessionID, messageSystem.getAddressService() , "");
        sessions.put(sessionID,userSession);
        return userSession;
    }

    private void removeUserSession(String sessionID) {
        sessions.remove(sessionID);
    }

    public UserSession getUserSession(String sessionID) {
        return sessions.get(sessionID);
    }


    public void setUser(String sessionID , UsersDataSet user){
        UserSession session = getUserSession(sessionID);
        if(session != null) {
            session.setUserID(user.getId());
            session.setUserName(user.getLogin());
        }
    }

    public void setMessage(String sessionID , String message){
        UserSession session = getUserSession(sessionID);
        if(session != null)
            session.setMessage(message);
    }



    private void setInfo(HttpSession session , HttpServletResponse response) throws IOException{
        UserSession userSession=getUserSession(session.getId());
        if( userSession!= null) {
            response.getWriter().print(userSession.getMessage());
            userSession.setMessage("");
        }
    }

    private void getPage (String page , HttpServletResponse response, Map<String, Object> pageVariables) throws IOException{
        response.getWriter().println(PageGenerator.getPage(page, pageVariables));
    }

    private void getSessionPage( HttpServletRequest request , HttpServletResponse response, Page page) throws ServletException, IOException
    {
        Message message = (Message) ResourceFactory.instance().get("message.xml");
        HttpSession session = request.getSession();
        Map<String,Object> pageVariables = new HashMap<>();
        UserSession userSession = getUserSession(session.getId());
        if(userSession == null) {
            pageVariables.put("info", message.getNeedAuth() );
        }
        else {
            pageVariables.put("info", userSession.getMessage());
            if(userSession.getMessage().equals(message.getAuthSuccessful())) {
                pageVariables.put("userId", userSession.getUserID());
                pageVariables.put("userName", userSession.getName());
            }
        }
        pageVariables.put("refreshPeriod", page.getRefreshTime());
        pageVariables.put("serverTime", CommonHelper.getTime());
        response.getWriter().println(PageGenerator.getPage(page.getSession(), pageVariables));
    }

    private void postAuthForm( HttpServletRequest request , HttpServletResponse response, Url url) throws ServletException, IOException
    {
        Message message = (Message) ResourceFactory.instance().get("message.xml");
        HttpSession session = request.getSession();
        final String login = request.getParameter("login");
        final String password = request.getParameter("password");
        final String sessionID = session.getId();

        UserSession userSession = addUserSession(login, sessionID);
        if(!login.equals("") && !password.equals("")) {
            messageSystem.sendMessage(makeMsgGetUser(address, messageSystem.getAddressService().getService(AccountService.class),
                    login, password, sessionID));
            userSession.setMessage(message.getWaiting());
        }
        else
            userSession.setMessage(message.getEmptyFields());
        response.sendRedirect(url.getSession());
    }

    private void postRegisterForm( HttpServletRequest request , HttpServletResponse response, Url url) throws ServletException, IOException
    {
        Message message = (Message) ResourceFactory.instance().get("message.xml");
        HttpSession session = request.getSession();
        final String login = request.getParameter("login");
        final String password = request.getParameter("password");
        final String sessionID = session.getId();

        UserSession userSession = addUserSession(login, sessionID);
        if(!login.equals("") && !password.equals("")) {
            messageSystem.sendMessage(makeMsgRegistrate(address, messageSystem.getAddressService().getService(AccountService.class),
                    login, password, sessionID));
            userSession.setMessage(message.getWaiting());
        }
        else
            userSession.setMessage(message.getEmptyFields());
        response.sendRedirect(url.getRegisterform());
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        HttpSession session= request.getSession();

        Url url = (Url) ResourceFactory.instance().get("url.xml");
        Page page = (Page) ResourceFactory.instance().get("page.xml");
        if(request.getPathInfo().equals(url.getAuthform())) {
            getPage(page.getAuthorization(), response, null);
            return;
        }

        if(request.getPathInfo().equals(url.getRegisterform())) {
            Map<String,Object> pageVariables = new HashMap<>();
            UserSession userSession = getUserSession(session.getId());
            if(userSession != null)
                pageVariables.put("info", userSession.getMessage());
            getPage(page.getRegistration(), response, pageVariables);
            return;
        }

        if(request.getPathInfo().equals(url.getLogout())) {
            removeUserSession(request.getSession().getId());
            response.sendRedirect(url.getIndex());
            return;
        }

        if(request.getPathInfo().equals(url.getIndex())) {
            getPage(page.getIndex(), response, null);
            return;
        }

        if(request.getPathInfo().equals(url.getAjaxChecking())) {
            setInfo(session,response);
            return;
        }

        if(request.getPathInfo().equals(url.getSession())) {
            getSessionPage(request, response, page);
            return;
        }
        response.sendRedirect(url.getIndex());
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {

        Url url = (Url) ResourceFactory.instance().get("url.xml");
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        if(request.getPathInfo().equals(url.getAuthform())) {
            postAuthForm(request , response, url);
            return;
        }

        if(request.getPathInfo().equals(url.getRegisterform())) {
            postRegisterForm(request , response, url);
        }
    }


    @SuppressWarnings("InfiniteLoopStatement")
    public void run(){
        while (true){
            messageSystem.execForAbonent(this);
            sleep(300);
        }
    }

    public Address getAddress(){
        return address;
    }

}
