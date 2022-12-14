public class URLDepthPair {
    public static final String URL_PREFIX = "http: //";
    private String urlAddress; // URL-адрес
    private int searchDepth; // глубина поиска

    public URLDepthPair(String url, int i) {
        urlAddress = url;
        searchDepth = i;
    }

    public int getDepth(){
        return searchDepth;
    }
    // выводим url и грубину поиска
    @Override
    public String toString(){
        return "URL - " + urlAddress + ". Depth - " + searchDepth;
    }
}
