package com.muv.technicalplan;


import java.net.URLEncoder;

public class ConstantUrl
{
    private String url_php_user = "http://www.technicalplan.ho.ua/user.php?";

    private String action_message = "action=message";
    private String action_select = "action=select";
    private String action_code = "action=code";
    private String action_insert = "action=insert";
    private String action_remove = "action=remove";
    private String action_refresh = "action=refresh";

    private String email = "&email=";
    private String login = "&login=";
    private String new_login = "&new_login=";
    private String name = "&name=";
    private String surname = "&surname=";
    private String surname_father = "&surname_father=";
    private String password = "&password=";
    private String type_account = "&type_account=";
    private String enterprise = "&enterprise=";
    private String position = "&position=";
    private String refresh = "&refresh=true";


    public String getUrlRecovery(String email)
    {
        return url_php_user + action_message + this.email + email;
    }

    public String getUrlSingInLogin(String login)
    {
        return url_php_user + action_select + this.login + login;
    }

    public String getUrlSingInEmail(String email)
    {
        return url_php_user + action_select + this.email + email;
    }

    public  String getUrlCode(String email)
    {
        return url_php_user + action_code + this.email + email;
    }

    public String getUrlRegistrationOrUpdate(String surname, String name, String surname_father,
            String login, String password, String email, int type_account, String enterprise, String position, String name_table,
                                             boolean update, String new_login) throws Exception
    {
        StringBuilder stringBuilder = new StringBuilder(url_php_user);
        stringBuilder.append(action_insert);
        stringBuilder.append(this.name);
        stringBuilder.append(URLEncoder.encode(name, "UTF-8"));
        stringBuilder.append(this.surname);
        stringBuilder.append(URLEncoder.encode(surname, "UTF-8"));
        stringBuilder.append(this.surname_father);
        stringBuilder.append(URLEncoder.encode(surname_father, "UTF-8"));
        stringBuilder.append(this.enterprise);
        stringBuilder.append(URLEncoder.encode(enterprise, "UTF-8"));
        stringBuilder.append(this.position);
        stringBuilder.append(URLEncoder.encode(position, "UTF-8"));
        stringBuilder.append(this.name_table);
        stringBuilder.append(URLEncoder.encode(name_table, "UTF-8"));
        stringBuilder.append(this.login);
        stringBuilder.append(URLEncoder.encode(login, "UTF-8"));
        stringBuilder.append(this.email);
        stringBuilder.append(URLEncoder.encode(email, "UTF-8"));
        stringBuilder.append(this.password);
        stringBuilder.append(URLEncoder.encode(password, "UTF-8"));
        stringBuilder.append(this.type_account);
        stringBuilder.append(type_account);
        if (update)
        {
            stringBuilder.append(this.refresh);
            stringBuilder.append(this.new_login);
            stringBuilder.append(URLEncoder.encode(new_login, "UTF-8"));
        }
        return stringBuilder.toString();
    }

    public String getUrlUpdateEnterprise(String login, String enterprise) throws Exception
    {
        StringBuilder stringBuilder = new StringBuilder(url_php_user);
        stringBuilder.append(action_refresh);
        stringBuilder.append(this.login);
        stringBuilder.append(URLEncoder.encode(login, "UTF-8"));
        stringBuilder.append(this.enterprise);
        stringBuilder.append(URLEncoder.encode(enterprise, "UTF-8"));
        return stringBuilder.toString();
    }

    private String UPLOAD_URL = "http://www.technicalplan.ho.ua/image.php?";
    private String DOWNLOAN_URL = "http://technicalplan.ho.ua/images_account/";

    public String getUrlUploadImage(String login, String new_login)
    {
        return UPLOAD_URL + action_insert + this.login + login + this.new_login + new_login;
    }

    public String getUrlDownloadImage(String name)
    {
        return DOWNLOAN_URL + name;
    }

    public String getUrlDeleteAccount(String login)
    {
        return url_php_user + action_remove + this.login + login;
    }

    public String getUrlDeleteImage(String login)
    {
        return UPLOAD_URL + action_remove + this.login + login;
    }

    public String getUrlUpdateImageLogin(String login, String new_login)
    {
        return UPLOAD_URL + action_refresh+ this.login + login + this.new_login + new_login;
    }

    private String url_php_search = "http://www.technicalplan.ho.ua/search.php?";
    private String action_search = "action=search";
    private String name_one = "&name_one=";
    private String name_two = "&name_two=";
    private String name_three = "&name_three=";

    public String getUrlSearchOne(String name_one, int type_account) throws Exception
    {
        StringBuilder stringBuilder = new StringBuilder(url_php_search);
        stringBuilder.append(action_search);
        stringBuilder.append(this.type_account);
        stringBuilder.append(type_account);
        stringBuilder.append(this.name_one);
        stringBuilder.append(URLEncoder.encode(name_one, "UTF-8"));
        return stringBuilder.toString();
    }

    public String getUrlSearchTwo(String name_one, String name_two, int type_account) throws Exception
    {
        StringBuilder stringBuilder = new StringBuilder(url_php_search);
        stringBuilder.append(action_search);
        stringBuilder.append(this.type_account);
        stringBuilder.append(type_account);
        stringBuilder.append(this.name_one);
        stringBuilder.append(URLEncoder.encode(name_one, "UTF-8"));
        stringBuilder.append(this.name_two);
        stringBuilder.append(URLEncoder.encode(name_two, "UTF-8"));
        return stringBuilder.toString();
    }

    public String getUrlSearchThree(String name_one, String name_two, String name_three, int type_account) throws Exception
    {
        StringBuilder stringBuilder = new StringBuilder(url_php_search);
        stringBuilder.append(action_search);
        stringBuilder.append(this.type_account);
        stringBuilder.append(type_account);
        stringBuilder.append(this.name_one);
        stringBuilder.append(URLEncoder.encode(name_one, "UTF-8"));
        stringBuilder.append(this.name_two);
        stringBuilder.append(URLEncoder.encode(name_two, "UTF-8"));
        stringBuilder.append(this.name_three);
        stringBuilder.append(URLEncoder.encode(name_three, "UTF-8"));
        return stringBuilder.toString();
    }

    private String url_php_position = "http://www.technicalplan.ho.ua/position.php?";
    private String delete_table = "&delete_table=";

    public String getUrlGetPosition(String login)
    {
        return url_php_position + action_select + this.login + login;
    }

    public String setUrlPositionMap(String login, String enterprise, String position) throws Exception
    {
        StringBuilder stringBuilder = new StringBuilder(url_php_position);
        stringBuilder.append(action_insert);
        stringBuilder.append(this.login);
        stringBuilder.append(URLEncoder.encode(login, "UTF-8"));
        stringBuilder.append(this.enterprise);
        stringBuilder.append(URLEncoder.encode(enterprise, "UTF-8"));
        stringBuilder.append(this.position);
        stringBuilder.append(URLEncoder.encode(position, "UTF-8"));
        return stringBuilder.toString();
    }

    public String setUrlPositionMapRefresh(String login, String enterprise, String position, String code,
                                           String name_table, boolean delete_table) throws Exception
    {
        StringBuilder stringBuilder = new StringBuilder(url_php_position);
        stringBuilder.append(action_insert);
        stringBuilder.append(this.login);
        stringBuilder.append(URLEncoder.encode(login, "UTF-8"));
        stringBuilder.append(this.enterprise);
        stringBuilder.append(URLEncoder.encode(enterprise, "UTF-8"));
        stringBuilder.append(this.position);
        stringBuilder.append(URLEncoder.encode(position, "UTF-8"));
        stringBuilder.append(this.refresh);
        stringBuilder.append(this.code);
        stringBuilder.append(URLEncoder.encode(code, "UTF-8"));
        stringBuilder.append(this.name_table);
        stringBuilder.append(URLEncoder.encode(name_table, "UTF-8"));
        stringBuilder.append(this.delete_table);
        stringBuilder.append(delete_table);
        return stringBuilder.toString();
    }

    public String getUrlCompletedStitched(String name_table, String date, String stitched, String id)
    {
        return url_php_position + action_insert_stitched + this.name_table + name_table + this.date + date + this.stitched + stitched + this.id + id;
    }

    public String getUrlComment (String name_table, String comment_manager, String comment_performer, String id)
    {
        return url_php_position + action_insert_comment + this.name_table + name_table + this.id + id +
                this.comment_manager + comment_manager + this.comment_performer + comment_performer;
    }

    private String comment_manager = "&comment_manager=";
    private String comment_performer = "&comment_performer=";
    private String action_insert_comment = "action=insert_comment";
    private String date = "&date=";
    private String id = "&id=";
    private String stitched = "&stitched=";
    private String action_insert_stitched = "action=insert_stitched";
    private String name_table = "&name_table=";

    public String getUrlRemovePosition(String login, String code, String name_table)
    {
        return url_php_position + action_remove + this.login + login + this.code + code + this.name_table + name_table;
    }

    public String getUrlTablePosition(String name_table)
    {
        return url_php_position + action_select + this.name_table + name_table;
    }

    private String url_php_linking = "http://technicalplan.ho.ua/linking.php?";
    private String where_user = "&where_user=";
    private String from_user = "&from_user=";
    private String code = "&code=";
    private String state = "&state=";
    private String action_linked = "action=linked";

    public String getUrlSetLinking(String where, String from, String enterprise, String position, String code, String name_table) throws Exception
    {
        StringBuilder stringBuilder = new StringBuilder(url_php_linking);
        stringBuilder.append(action_insert);
        stringBuilder.append(this.where_user);
        stringBuilder.append(URLEncoder.encode(where, "UTF-8"));
        stringBuilder.append(this.from_user);
        stringBuilder.append(URLEncoder.encode(from, "UTF-8"));
        stringBuilder.append(this.enterprise);
        stringBuilder.append(URLEncoder.encode(enterprise, "UTF-8"));
        stringBuilder.append(this.position);
        stringBuilder.append(URLEncoder.encode(position, "UTF-8"));
        stringBuilder.append(this.code);
        stringBuilder.append(URLEncoder.encode(code, "UTF-8"));
        stringBuilder.append(this.name_table);
        stringBuilder.append(URLEncoder.encode(name_table, "UTF-8"));
        return stringBuilder.toString();
    }

    public String getUrlGetLinkingWhere(String login)
    {
        return url_php_linking + action_select + where_user + login;
    }

    public String getUrlGetLinkingFrom(String login)
    {
        return url_php_linking + action_select + from_user + login;
    }

    public String getUrlSetLinkingState(String where, String from, String linked, String name_table)
    {
        return url_php_linking + action_linked + where_user + where + from_user + from + state + linked + this.name_table + name_table;
    }

    public String getUrlRemoveLinkingFrom(String where, String from)
    {
        return url_php_linking + action_remove + where_user + where + from_user + from;
    }

    private String DOWNLOAN_CSV_URL = "http://technicalplan.ho.ua/csv/";
    private String DOWNLOAN_CSV_URL_CREATED = "http://technicalplan.ho.ua/position.php?";

    public String getUrlDownloadCsv(String name) throws Exception
    {
        StringBuilder stringBuilder = new StringBuilder(DOWNLOAN_CSV_URL);
        stringBuilder.append(URLEncoder.encode(name, "UTF-8"));
        return stringBuilder.toString();
    }

    private String action_download = "action=download";
    private String name_position = "&name_position=";

    private String comment_state = "&comment_state=";

    public String getUrlDownloadCsvCreated(String name_table, String name_position, boolean comment_state) throws Exception
    {
        StringBuilder stringBuilder = new StringBuilder(DOWNLOAN_CSV_URL_CREATED);
        stringBuilder.append(action_download);
        stringBuilder.append(this.name_table);
        stringBuilder.append(URLEncoder.encode(name_table, "UTF-8"));
        stringBuilder.append(this.name_position);
        stringBuilder.append(URLEncoder.encode(name_position, "UTF-8"));
        stringBuilder.append(this.comment_state);
        stringBuilder.append(comment_state);
        return stringBuilder.toString();
    }
}
