import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLDepthPair {
    public static final String URL_PREFIX = "http://";
    public static final String URL_HREF = "<a href=\"" + URL_PREFIX;
    public static final int URL_HREF_LENGTH = URL_HREF.length();


    private String urlAddress; // URL-адрес
    private int searchDepth; // глубина поиска

    public URLDepthPair(String url, int i) {
        urlAddress = url;
        searchDepth = i;
    }

    // возвращаем глубину
    public int getDepth() {
        return searchDepth;
    }

    // возвращаем url
    public String getURL() {
        return urlAddress;
    }

    //возвращаем хост
    // MalformedURLException = адрес указан неверно или заданный в нём ресурс отсутствует
    public String getHost() {
        try {
            URL url = new URL(urlAddress);
            return url.getHost();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    //возвращаем путь
    // MalformedURLException = адрес указан неверно или заданный в нём ресурс отсутствует
    public String getPath() {
        try {
            URL url = new URL(urlAddress);
            return url.getPath();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    // выводим url и грубину поиска
    @Override
    public String toString() {
        return "URL - " + urlAddress + ". Depth - " + searchDepth;
    }

    // проверяем, является ли найденная строка ссылкой и возвращаем найденный url
    public static String isLink(String link) {
        int start = link.indexOf(URL_HREF);
        // если URL_HREF не найдено
        if (start == -1) {
            return "";
        }
        start += URL_HREF_LENGTH;
        String result = link.substring(start, link.indexOf("\"", start));
        return "http://" + result;

    }
}
