package nb.scode.bukumigas.constants;

public interface Constants {


    String WEB_SITE = "http://bukumigas.ourgoodhealth.us/";  //web site url address

    String CLIENT_ID = "1";  //Client ID | For identify the application | Example: 12567

    String API_DOMAIN = "http://bukumigas.ourgoodhealth.us/";  //url address to which the application sends requests

    String API_FILE_EXTENSION = ".inc.php";
    String API_VERSION = "v1";

    String METHOD_ACCOUNT_LOGIN = API_DOMAIN + "api/" + API_VERSION + "/method/account.signIn" + API_FILE_EXTENSION;
    String METHOD_ACCOUNT_SIGNUP = API_DOMAIN + "api/" + API_VERSION + "/method/account.signUp" + API_FILE_EXTENSION;
    String METHOD_ACCOUNT_AUTHORIZE = API_DOMAIN + "api/" + API_VERSION + "/method/account.authorize" + API_FILE_EXTENSION;
    String METHOD_ACCOUNT_SET_GCM_TOKEN = API_DOMAIN + "api/" + API_VERSION + "/method/account.setGcmToken" + API_FILE_EXTENSION;

    String METHOD_ACCOUNT_LOGOUT = API_DOMAIN + "api/" + API_VERSION + "/method/account.logOut" + API_FILE_EXTENSION;



    String METHOD_CATEGORIES_GET = API_DOMAIN + "api/" + API_VERSION + "/method/categories.get" + API_FILE_EXTENSION;
    String METHOD_CATEGORY_GET = API_DOMAIN + "api/" + API_VERSION + "/method/category.get" + API_FILE_EXTENSION;

    String METHOD_SUPPORT_SEND_TICKET = API_DOMAIN + "api/" + API_VERSION + "/method/support.sendTicket" + API_FILE_EXTENSION;

    String METHOD_PROFILE_UPLOADPHOTO = API_DOMAIN + "api/" + API_VERSION + "/method/profile.uploadPhoto" + API_FILE_EXTENSION;
    String METHOD_PROFILE_UPLOADCOVER = API_DOMAIN + "api/" + API_VERSION + "/method/profile.uploadCover" + API_FILE_EXTENSION;


    String METHOD_STREAM_GET = API_DOMAIN + "api/" + API_VERSION + "/method/stream.get" + API_FILE_EXTENSION;
    String METHOD_POPULAR_GET = API_DOMAIN + "api/" + API_VERSION + "/method/popular.get" + API_FILE_EXTENSION;


    String METHOD_APP_TERMS = API_DOMAIN + "api/" + API_VERSION + "/method/app.terms" + API_FILE_EXTENSION;
    String METHOD_APP_THANKS = API_DOMAIN + "api/" + API_VERSION + "/method/app.thanks" + API_FILE_EXTENSION;
    String METHOD_APP_SEARCH = API_DOMAIN + "api/" + API_VERSION + "/method/app.search" + API_FILE_EXTENSION;

    String METHOD_ITEMS_LIKE = API_DOMAIN + "api/" + API_VERSION + "/method/items.like" + API_FILE_EXTENSION;

    String METHOD_FAVORITES_GET = API_DOMAIN + "api/" + API_VERSION + "/method/favorites.get" + API_FILE_EXTENSION;



    int POPULAR_CONST_1 = 0; // All Time
    int POPULAR_CONST_2 = 1; // Day
    int POPULAR_CONST_3 = 2; // Week
    int POPULAR_CONST_4 = 3; // Month

    int LIST_ITEMS = 20;

    public static final int ENABLED = 1;
    public static final int DISABLED = 0;

    public static final int GCM_ENABLED = 1;
    public static final int GCM_DISABLED = 0;

    int ADMOB_ENABLED = 1;
    public static final int ADMOB_DISABLED = 0;

    int GCM_NOTIFY_CONFIG = 0;
    int GCM_NOTIFY_SYSTEM = 1;
    int GCM_NOTIFY_CUSTOM = 2;
    int GCM_NOTIFY_LIKE = 3;
    int GCM_NOTIFY_ANSWER = 4;
    int GCM_NOTIFY_QUESTION = 5;
    int GCM_NOTIFY_COMMENT = 6;
    int GCM_NOTIFY_FOLLOWER = 7;
    int GCM_NOTIFY_PERSONAL = 8;

    int ACCOUNT_STATE_ENABLED = 0;
    int ACCOUNT_STATE_DISABLED = 1;
    int ACCOUNT_STATE_BLOCKED = 2;
    int ACCOUNT_STATE_DEACTIVATED = 3;

    public static final int ITEM_EDIT = 10;
    public static final int STREAM_NEW_POST = 11;

    public static final String TAG = "TAG";
}