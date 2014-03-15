package database;

import frontend.CreatedBy;

@CreatedBy(name = "max" , date = "22.02.14")
public class AccountService {

    private UsersDataSetDAO dao;

    public AccountService(DataService dataService) {
        this.dao = new UsersDataSetDAO(dataService);
    }

    public boolean addUser(String login , String password) {
        if(!checkLogin(login))
        {
            dao.add( new UsersDataSet(login, password));
            return true;
        }
        else
            return false;
    }

    public boolean checkUser(String login , String password)
    {
        try {
           return dao.getByLogin(login).getPassword().equals(password);
        }
        catch (NullPointerException e) {
            return false;
        }
    }

    public boolean checkLogin(String login)
    {
        return dao.getByLogin(login) != null;
    }

    public boolean deleteUser(String login)
    {
        return dao.delete(login);
    }
}
