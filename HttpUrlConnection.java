package tech.codingclub;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;

public class HttpUrlConnection {

    public final static String USER_AGENT = "Mozilla/5.0";

    public static String sendGet(String urlstr) throws Exception
    {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlstr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String line;

        while((line = rd.readLine()) != null)
        {
            result.append(line);
        }

        rd.close();
        return(result.toString());
    }

    public static void main(String[] args) {

        try
        {
            System.out.println(sendGet("https://codingclub.tech/test-get-request?name=Riya"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}

