package message;

import database.UsersDataSet;
import frontend.Frontend;
import messageSystem.Address;

/*
 * Created by maxim on 29.03.14.
 */

public class MsgUpdateUser extends MsgToFrontend {

    private UsersDataSet user;

    public MsgUpdateUser(Address from, Address to, String sessionID, UsersDataSet user , String message){
        super(from,to, sessionID, message);
        this.user = user;
    }

    public void exec(Frontend frontend){
        frontend.setMessage(sessionID,message);
        if(user != null)
            frontend.setUser(sessionID, user);
        else
            frontend.setUser(sessionID, new UsersDataSet("",""));
    }
}
