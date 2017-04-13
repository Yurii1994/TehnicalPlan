package com.muv.technicalplan;


import com.muv.technicalplan.data.DataLinking;
import com.muv.technicalplan.data.DataMaps;
import com.muv.technicalplan.data.DataPosition;
import com.muv.technicalplan.data.DataSearch;
import com.muv.technicalplan.data.DataUser;
import com.muv.technicalplan.data.DataUsers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class JsonParser
{
    public boolean parseRecovery(String dateUrl) throws Exception
    {
        URL url = new URL(dateUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        InputStream inputStream = urlConnection.getInputStream();
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String resultJson = buffer.toString();
        return Boolean.valueOf(resultJson);
    }

    public DataUser parseUser(String dateUrl) throws Exception
    {
        DataUser dataUser = new DataUser();

        URL url = new URL(dateUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        InputStream inputStream = urlConnection.getInputStream();
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String resultJson = buffer.toString();
        resultJson = resultJson.replace("true[", "[");

        if (!resultJson.equals("false"))
        {
            JSONArray jsonObj = new JSONArray(resultJson);

            for (int i = 0; i < jsonObj.length(); i++)
            {
                JSONObject object = jsonObj.getJSONObject(i);

                int id = object.getInt("id");
                String name = object.getString("name");
                String surname = object.getString("surname");
                String surname_father = object.getString("surname_father");
                String enterprise = object.getString("enterprise");
                String position = object.getString("position");
                String login = object.getString("login");
                String password = object.getString("password");
                String email = object.getString("email");
                int type_account = object.getInt("type_account");
                String image = object.getString("images");
                String name_table = object.getString("name_table");

                dataUser.setUser_id(id);
                dataUser.setName(name);
                dataUser.setSurname(surname);
                dataUser.setSurname_father(surname_father);
                dataUser.setEnterprise(enterprise);
                dataUser.setPosition(position);
                dataUser.setLogin(login);
                dataUser.setPassword(password);
                dataUser.setEmail(email);
                dataUser.setType_account(type_account);
                dataUser.setImage(image);
                dataUser.setName_table(name_table);
            }
        }
        return dataUser;
    }

    public DataUsers parseUsers(String dateUrl) throws Exception
    {
        DataUsers dataUsers = new DataUsers();

        URL url = new URL(dateUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        InputStream inputStream = urlConnection.getInputStream();
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String resultJson = buffer.toString();
        resultJson = resultJson.replace("true[", "[");

        if (!resultJson.equals("false"))
        {
            JSONArray jsonObj = new JSONArray(resultJson);

            for (int i = 0; i < jsonObj.length(); i++)
            {
                JSONObject object = jsonObj.getJSONObject(i);

                String name = object.getString("name");
                String surname = object.getString("surname");
                String surname_father = object.getString("surname_father");
                String enterprise = object.getString("enterprise");
                String position = object.getString("position");
                String login = object.getString("login");
                String email = object.getString("email");
                String image = object.getString("images");

                dataUsers.setName(name);
                dataUsers.setSurname(surname);
                dataUsers.setSurname_father(surname_father);
                dataUsers.setEnterprise(enterprise);
                dataUsers.setPosition(position);
                dataUsers.setLogin(login);
                dataUsers.setEmail(email);
                dataUsers.setImage(image);
            }
        }
        return dataUsers;
    }

    public String parseCode(String dateUrl) throws Exception
    {
        URL url = new URL(dateUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        InputStream inputStream = urlConnection.getInputStream();
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }

    public String parseRegistration(String dateUrl) throws Exception
    {
        URL url = new URL(dateUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.connect();
        InputStream inputStream = urlConnection.getInputStream();
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }

    public boolean parseRemoveRefresh(String dateUrl) throws Exception
    {
        URL url = new URL(dateUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.connect();
        InputStream inputStream = urlConnection.getInputStream();
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String resultJson = buffer.toString();
        return Boolean.valueOf(resultJson);
    }

    public List<DataSearch> parseSearch(String dateUrl) throws Exception
    {

        List<DataSearch> list = new ArrayList<>();

        URL url = new URL(dateUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        InputStream inputStream = urlConnection.getInputStream();
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String resultJson = buffer.toString();
        resultJson = resultJson.replace("true[", "[");
        if (!resultJson.equals("false"))
        {
            JSONArray jsonObj = new JSONArray(resultJson);

            for (int i = 0; i < jsonObj.length(); i++)
            {
                DataSearch data = new DataSearch();
                JSONObject object = jsonObj.getJSONObject(i);
                String login = object.getString("login");
                String name = object.getString("name");
                String surname = object.getString("surname");
                String surname_father = object.getString("surname_father");
                String enterprise = object.getString("enterprise");
                String image = object.getString("images");

                data.setImage(image);
                data.setLogin(login);
                data.setName(name);
                data.setSurname(surname);
                data.setSurname_father(surname_father);
                data.setEnterprise(enterprise);
                list.add(data);
            }
        }
        return list;
    }

    public List<DataPosition> parseGetPosition(String dateUrl) throws Exception
    {

        List<DataPosition> list = new ArrayList<>();

        URL url = new URL(dateUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        InputStream inputStream = urlConnection.getInputStream();
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String resultJson = buffer.toString();
        resultJson = resultJson.replace("true[", "[");
        if (!resultJson.equals("false"))
        {
            JSONArray jsonObj = new JSONArray(resultJson);

            for (int i = 0; i < jsonObj.length(); i++)
            {
                DataPosition data = new DataPosition();
                JSONObject object = jsonObj.getJSONObject(i);
                String login = object.getString("login");
                String position = object.getString("position");
                String code = object.getString("code");
                String name_table = object.getString("name_table");

                data.setLogin(login);
                data.setPosition(position);
                data.setCode(code);
                data.setName_table(name_table);
                list.add(data);
            }
        }
        return list;
    }

    public boolean parseSetLinking(String dateUrl) throws Exception
    {
        URL url = new URL(dateUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.connect();
        InputStream inputStream = urlConnection.getInputStream();
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String resultJson = buffer.toString();
        return Boolean.valueOf(resultJson);
    }

    public List<DataLinking> parseGetLinking(String dateUrl) throws Exception
    {

        List<DataLinking> list = new ArrayList<>();

        URL url = new URL(dateUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        InputStream inputStream = urlConnection.getInputStream();
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String resultJson = buffer.toString();
        resultJson = resultJson.replace("true[", "[");
        if (!resultJson.equals("false"))
        {
            JSONArray jsonObj = new JSONArray(resultJson);

            for (int i = 0; i < jsonObj.length(); i++)
            {
                DataLinking data = new DataLinking();
                JSONObject object = jsonObj.getJSONObject(i);
                String where_user = object.getString("where_user");
                String from_user = object.getString("from_user");
                String enterprise = object.getString("enterprise");
                String position = object.getString("position");
                String code = object.getString("code");
                String state = object.getString("state");
                String name_table = object.getString("name_table");

                data.setWhere_user(where_user);
                data.setFrom_user(from_user);
                data.setEnterprise(enterprise);
                data.setPosition(position);
                data.setCode(code);
                data.setState(state);
                data.setName_table(name_table);
                list.add(data);
            }
        }
        return list;
    }

    public boolean getUpdateEnterprise(String dateUrl) throws Exception
    {
        URL url = new URL(dateUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.connect();
        InputStream inputStream = urlConnection.getInputStream();
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String resultJson = buffer.toString();
        return Boolean.valueOf(resultJson);
    }

    public List<DataMaps> parseMaps(String dateUrl, String position, String name_table) throws Exception
    {

        List<DataMaps> list = new ArrayList<>();

        URL url = new URL(dateUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        InputStream inputStream = urlConnection.getInputStream();
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String resultJson = buffer.toString();
        resultJson = resultJson.replace("true[", "[");
        if (!resultJson.equals("false"))
        {
            JSONArray jsonObj = new JSONArray(resultJson);

            for (int i = 0; i < jsonObj.length(); i++)
            {
                DataMaps data = new DataMaps();
                JSONObject object = jsonObj.getJSONObject(i);
                int id = Integer.parseInt(object.getString("id"));
                String code = object.getString("code");
                String general = object.getString("general");
                String relative = object.getString("relative");
                String description = object.getString("description");
                String normal = object.getString("normal");
                String lightweight = object.getString("lightweight");
                String light = object.getString("light");
                String date = object.getString("date");
                String comment_manager = object.getString("comment_manager");
                String comment_performer = object.getString("comment_performer");
                String stitched = object.getString("stitched");
                String login = object.getString("login");
                String name = object.getString("name");
                String surname = object.getString("surname");
                String surname_father = object.getString("surname_father");

                data.setIdMap(id);
                data.setCode(code);
                data.setGeneral(general);
                data.setRelative(relative);
                data.setDescription(description);
                data.setNormal(normal);
                data.setLightweight(lightweight);
                data.setLight(light);
                data.setDate(date);
                data.setComment_manager(comment_manager);
                data.setComment_performer(comment_performer);
                data.setPosition(position);
                data.setName_table(name_table);
                data.setStitched(stitched);
                data.setLogin(login);
                data.setName(name);
                data.setSurname(surname);
                data.setSurname_father(surname_father);
                list.add(data);
            }
        }
        return list;
    }

    public boolean getCompletedStitchedComment(String dateUrl) throws Exception
    {
        URL url = new URL(dateUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.connect();
        InputStream inputStream = urlConnection.getInputStream();
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String resultJson = buffer.toString();
        return Boolean.valueOf(resultJson);
    }

    public String getCreateReport(String dateUrl, String params) throws Exception
    {
        URL url = new URL(dateUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);

        urlConnection.setRequestProperty("Content-Length", "" + Integer.toString(params.getBytes().length));
        OutputStream os = urlConnection.getOutputStream();

        byte[] data = params.getBytes("UTF-8");

        os.write(data);

        urlConnection.connect();
        int responseCode = urlConnection.getResponseCode();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        String resultString = "";
        if (responseCode == 200)
        {
            InputStream inputStream = urlConnection.getInputStream();

            byte[] buffer = new byte[8192];

            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1)
            {
                baos.write(buffer, 0, bytesRead);
            }
            data = baos.toByteArray();
            resultString = new String(data, "UTF-8");
        }
        return resultString.replace("\"", "");
    }

    public boolean getDeleteReport(String dateUrl) throws Exception
    {
        URL url = new URL(dateUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.connect();
        InputStream inputStream = urlConnection.getInputStream();
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String resultJson = buffer.toString();
        return Boolean.valueOf(resultJson);
    }
}
