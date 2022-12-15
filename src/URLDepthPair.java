import java.net.*;

public class URLDepthPair {
    public static final String URL_PREFIX = "http: //";
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
}
