package frontend;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * Created by maxim on 09.03.14.
 */
public class Constants {
    public static class Url {
        final static public String AUTHFORM = "/authform";
        final static public String REGISTERFORM = "/registerform";
        final static public String SESSION = "/userid";
        final static public String INDEX = "/index";
        final static public String LOGOUT = "/logout";
        final static public String AJAX_CHECKING = "/ajaxcheking";
    }

    public static class Page {
        final static public String INDEX = "/index.html";
        final static public String REGISTRATION = "/registerform.html";
        final static public String AUTHORIZATION = "/authform.html";
        final static public String SESSION = "/userId.html";
    }

    public static class Message {
        final static public String EMPTY_FIELDS = "Заполните все поля";
        final static public String USER_EXISTS = "Пользователь уже существует";
        final static public String SUCCESSFUL_REGISTRATION = "Пользователь добавлен";
        final static public String AUTH_FAILED = "Неправильные логин/пароль";
        final static public String AUTH_SUCCESSFUL = "Вы успешно авторизовались";
        final static public String WAITING = "Подождите...";
        final static public String DATABASE_ERROR = "База данных недоступна";
    }

    final static public String REFRESH_TIME = "2000";
    final static public int MAIN_PORT = 8800;
    final static public int TEST_PORT = 8885;

    static public String getRandomString( int length) {
        StringBuilder string = new StringBuilder();
        for(int i = 0; i < length ; ++i )
        {
            string.append((char)(Math.random()*(128 - 32) + 32));
        }
        return string.toString();
    }

    final static private DateFormat FORMATTER = new SimpleDateFormat("HH.mm.ss");

    public static String getTime() {
        return FORMATTER.format(new Date());
    }

    public static void sleep(int ms){
        try {
            Thread.sleep(ms);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }


}
