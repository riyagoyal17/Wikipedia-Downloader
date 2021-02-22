package tech.codingclub;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import tech.codingclub.utility.TaskManager;

import java.util.Date;
import java.util.Scanner;

public class WikipediaDownloader implements Runnable{



    private String keyword;

    public WikipediaDownloader()
    {

    }

    public WikipediaDownloader(String keyword)
    {
        this.keyword = keyword;
    }

    @Override
    public void run() {

        //Step1: Get the useful keyword

        if(this.keyword == null || this.keyword.length() == 0)
            return;

        // [ ] means replace all continuous character space with "_"
        this.keyword = this.keyword.trim().replaceAll("[ ]+","_");

        //Step2: Get the wikipedia url

        String wikiUrl =  getWikipediaUrlForQuery(this.keyword);

        //Step3: Make a get request to wikipedia

        String response = "";
        String image_url = "";

        try
        {
            String wikipediaResponseHTML = HttpUrlConnection.sendGet(wikiUrl);
            //System.out.println(wikipediaResponseHTML);

            //Step4 : parsing and getting useful result

            Document document = Jsoup.parse(wikipediaResponseHTML,"https://en.wikipedia.org");

            Elements childElements = document.body().select(".mw-parser-output > *");

            int state = 0;

            for(Element childElement : childElements)
            {
                //System.out.println(childElement.tagName());

                if(state == 0)
                {
                    if(childElement.tagName().equals("table"))
                    {
                        state = 1;
                    }
                }

                else if(state == 1)
                {
                    if(childElement.tagName().equals("p"))
                    {
                        state = 2;
                        response = childElement.text();
                        break;
                    }
                }

                try
                {
                    image_url = document.body().select(".infobox img").get(0).attr("src");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        WikiResult wikiresult = new WikiResult(this.keyword,response,image_url);

        //Push result into database
        //printing json

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(wikiresult);
        System.out.println(json);
    }

    private String getWikipediaUrlForQuery(String cleanKeyword) {

        return "https://en.wikipedia.org/wiki/" + cleanKeyword;
    }

    public static void main(String[] args) {

        System.out.println("This is Riya Goyal.");
        System.out.println("Wikipedia Downloader is running at " + new Date().toString() + " sharp.");

        System.out.println("Enter the keyword : ");
        Scanner scan = new Scanner(System.in);
        String word = scan.nextLine();

        String[] arr = {"India" , "United States"};

        TaskManager taskManager = new TaskManager(20);

        for(String words: arr)
        taskManager.waitTillQueueIsEmptyAndAddTask(new WikipediaDownloader(words));
    }
}
