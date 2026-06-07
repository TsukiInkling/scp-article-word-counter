package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

public class SCPWordCounter {

    public static void main(String[] args) throws IOException {

        File counts = new File("counts.txt");
        FileWriter writer = new FileWriter(counts);

        String articleNumber;
        for (Integer i = 2; i < 10000; i++) {

                // Prepends zeros to articles under 100
            articleNumber = i.toString();
            switch (articleNumber.length()) {
                case 1:
                    articleNumber = "00" + articleNumber;
                    break;
                case 2:
                    articleNumber = "0" + articleNumber;
                    break;
            }


                // Connects to article and grabs the page-content element (element containing article text)
            try {
                Jsoup.connect("https://scp-wiki.wikidot.com/scp-" + articleNumber).get();
            } catch (HttpStatusException e) {
                writer.write("0 \n");
                System.out.println(articleNumber + " does not exist");
                continue;
            }

            Document page = Jsoup.connect("https://scp-wiki.wikidot.com/scp-" + articleNumber).get();
            Element content = page.getElementById("page-content");

                // Defines classes within page-content that are not part of the article
            String[] irrelevantClasses = new String[]
                    {"preview",
                            "blockquote contributors-only",
                            "page-rate-widget-box",
                            "info-container",
                            "authorlink-wrapper",
                            "danger-diamond",
                            "creditRate",
                            "footer-wikiwalk-nav",
                            "collection",
                            "licensebox",
                            "code"};

                // Removes child classes in list above from page-content
            for (String className : irrelevantClasses)
                content.getElementsByClass(className).remove();

                // Adds page to page text
            String pageText = content.text();

                // Defines articles that use ListPages in a unique way
            String[] exceptionalArticles = new String[]
                    {"1173",
                    "1496",
                    "1893",
                    "2237",
                    "3059",
                    "3306",
                    "4000",
                    "4004",
                    "4127",
                    "4417",
                    "4672",
                    "4921",
                    "5117",
                    "5147",
                    "5251",
                    "5301",
                    "5410",
                    "5664",
                    "5723"};
            ArrayList<String> exceptionalList = new ArrayList<>(Arrays.asList(exceptionalArticles));

                // Deals with articles that use ListPages
            if (content.children().hasClass("list-pages-box") && !exceptionalList.contains(articleNumber)) {

                Element pageSource = page.getElementById("page-content");
                if (!pageSource.html().contains("odate") && content.getElementsByClass("list-pages-box").first().hasText()) {

                    short offset = 1;
                    Element offsetContent;

                    // Defines parts of the article not unique to any child page
                    Element parentContent = content.clone();
                    parentContent.getElementsByClass("list-pages-item").remove();

                    do {
                        // Connects to the next offset page
                        offsetContent = Jsoup.connect("https://scp-wiki.wikidot.com/scp-" + articleNumber + "/offset/" + offset).get().getElementById("page-content");

                        // Removes non-article text again, this time from the offset page
                        for (String className : irrelevantClasses)
                            offsetContent.getElementsByClass(className).remove();

                        // Adds offset text to page text if offset has unique text
                        if (offsetIsNotEmpty(offsetContent, parentContent))
                            pageText += " \n" + offsetContent.text();

                        offset++;
                    } while (offsetIsNotEmpty(offsetContent, parentContent));
                }
            }


                // Procedures for aforementioned 'exceptional' articles
            else if (exceptionalList.contains(articleNumber)) {
                Element offsetContent;

                switch (articleNumber) {
                    case "1173":
                        offsetContent = Jsoup.connect("https://scp-wiki.wikidot.com/scp-1173/order/name").get().getElementById("page-content");

                        for (String className : irrelevantClasses)
                            offsetContent.getElementsByClass(className).remove();

                        pageText += " \n" + offsetContent.text();
                        break;
                    case "1893":
                        pageText = "";
                        String iterationNames = "abcde";
                        Element iterationContent;

                        for (int it = 0; it < 5; it++) {
                            iterationContent =
                                    Jsoup.connect("https://scp-wiki.wikidot.com/fragment:scp-1893-iteration-" + iterationNames.charAt(it)).get().getElementById("page-content");
                            iterationContent.getElementsByClass("warning-top-box").remove();
                            pageText += iterationContent.text() + " \n";
                        }
                        break;
                    case "2237":
                        pageText = "";

                        offsetContent = Jsoup.connect("https://scp-wiki.wikidot.com/fragment:scp-2237-a").get().getElementById("page-content");
                        offsetContent.getElementsByClass("warning-top-box").remove();
                        pageText += offsetContent.text() + " \n";

                        offsetContent = Jsoup.connect("https://scp-wiki.wikidot.com/fragment:scp-2237-b").get().getElementById("page-content");
                        offsetContent.getElementsByClass("warning-top-box").remove();
                        pageText += offsetContent.text();
                        break;
                    case "3059":
                        pageText = "";

                        for (int it = 1; it <= 5; it++) {
                            offsetContent = Jsoup.connect("https://scp-wiki.wikidot.com/fragment:3059-" + it).get().getElementById("page-content");
                            offsetContent.getElementsByClass("warning-top-box").remove();
                            pageText += offsetContent.text() + " \n";
                        }
                        break;
                    case "3306":
                        pageText = "";
                        String endings3306 = "abc";

                        offsetContent = Jsoup.connect("https://scp-wiki.wikidot.com/scp-3306").get().getElementById("page-content");
                        offsetContent.getElementsByClass("list-pages-box").remove();
                        pageText += offsetContent.text() + " \n";

                        for (int e = 0; e < 3; e++) {
                            offsetContent = Jsoup.connect("https://scp-wiki.wikidot.com/fragment:scp3306-ending" + endings3306.charAt(e)).get().getElementById("page-content");
                            offsetContent.getElementsByClass("warning-top-box").remove();
                            pageText += offsetContent.text() + " \n";
                        }
                        break;
                    case "4127":
                        pageText = "";

                        offsetContent = Jsoup.connect("https://scp-wiki.wikidot.com/fragment:scp-4127-1").get().getElementById("page-content");
                        offsetContent.getElementsByClass("warning-top-box").remove();
                        pageText += offsetContent.text() + " \n";

                        offsetContent = Jsoup.connect("https://scp-wiki.wikidot.com/fragment:scp-4127-2").get().getElementById("page-content");
                        offsetContent.getElementsByClass("warning-top-box").remove();
                        pageText += offsetContent.text();
                        break;
                    case "4417":
                        pageText = "";

                        offsetContent = Jsoup.connect("https://scp-wiki.wikidot.com/fragment:scp-4417-break").get().getElementById("page-content");
                        offsetContent.getElementsByClass("warning-top-box").remove();
                        pageText += offsetContent.text() + " \n";

                        offsetContent = Jsoup.connect("https://scp-wiki.wikidot.com/fragment:scp-4417-loop").get().getElementById("page-content");
                        offsetContent.getElementsByClass("warning-top-box").remove();
                        pageText += offsetContent.text() + " \n";

                        offsetContent = Jsoup.connect("https://scp-wiki.wikidot.com/fragment:scp-4417-jump").get().getElementById("page-content");
                        offsetContent.getElementsByClass("warning-top-box").remove();
                        pageText += offsetContent.text();
                        break;
                }
            }


            // System.out.println(pageText);

            Scanner text = new Scanner(pageText);
            int wordCount = 0;
            while (text.hasNext()) {
                text.next();
                wordCount++;
            }
            text.close();

            writer.write(wordCount + "\n");
            System.out.println(articleNumber + " done");
        }

        writer.close();
    }

    public static boolean offsetIsNotEmpty(Element offset, Element parent) {

        return
                // TRUE:
                // Has a list-pages-box module
            offset.children().hasClass("list-pages-box")
                // FALSE:
                // Text is empty
                // Text only contains text from the parent page
            && !(
                offset.text().isEmpty()
                || offset.text().equals(parent.text()));
    }

}