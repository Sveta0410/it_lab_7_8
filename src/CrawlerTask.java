import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;

public class CrawlerTask implements Runnable {
    private URLPool pool;
    public URLDepthPair pair;

    public CrawlerTask(URLPool p) {
        pool = p;
    }

    @Override
    public void run() {
        // получение пары url-depth из пула, ожидая в случае, если пара не будет сразу допущена
//        pair = pool.getPair();

        URLDepthPair newPair;
        int depth;

        // пока в пуле не останется пар url-depth
        while (true){
            pair = pool.getPair();
            depth = pair.getDepth();
            // создаём временный список для хранения всех ссылок, расположенных на текущей странице
            LinkedList<String> currentLinksList;
            // добавляем в ранне созданный список ссылки, расположенные на текущей странице
            currentLinksList = CrawlerTask.getAllLinks(pair);

            // перебираем все элементы в полученном списке ссылок
            for (String myURL : currentLinksList) {
                // добавляем в список необработанных ссылок
                newPair = new URLDepthPair(myURL, depth + 1);
                pool.addPair(newPair);
            }
        }
    }

    // метод для поиска всех ссылок расположенных на странице
    public static LinkedList<String> getAllLinks(URLDepthPair currentDepthPair) {
        int port = 80; // порт по которому мы будем подключаться (для http)
        // создаём список для зранения найденных ссылок
        LinkedList<String> foundURLs = new LinkedList<>();
        // создаём сокет
        Socket socket;

        // инициализируем сокет
        try {
            socket = new Socket(currentDepthPair.getHost(), port);
        } catch (UnknownHostException e) {
            // исключение "неизвестный хост"
            System.err.println("UnknownHostException " + e.getMessage());
            return foundURLs;
        } catch (IOException e) {
            // исключение ввода/вывода
            System.err.println("IOException  1" + e.getMessage());
            return foundURLs;
        }

        // устанавливаем время ожидания сокета (в миллисекундах)
        // это необходимо для того, чтобы сокет знал, сколько нужно ждать передачи данных с другой стороны
        // SocketException = возникновение ошибки на сокете
        try {
            socket.setSoTimeout(1000); // 1 секунда
        } catch (SocketException e) {
            System.err.println("SocketException " + e.getMessage());
            return foundURLs;
        }

        // создаём outputStream (метод getOutputStream позволяет сокету отправлять данные на другую сторону соединения)
        OutputStream outputStream;
        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            // исключение ввода/вывода
            System.err.println("IOException " + e.getMessage());
            return foundURLs;
        }

        // создаём printWriter
        // autoFlush - true = буфер будет очищаться после каждого вызова метода println
        PrintWriter printWriter = new PrintWriter(outputStream, true);
        // отпрввляем на сервер запрос
        printWriter.println("GET " + currentDepthPair.getPath() + " HTTP/1.1"); // запрашиваем страницу
        printWriter.println("Host: " + currentDepthPair.getHost()); // запрашиваем страницу
        printWriter.println("Connection: close");
        printWriter.println();

        // создаём inputStreamReader (для получения информации с другого конца соединения)
        InputStreamReader in;
        try {
            in = new InputStreamReader(socket.getInputStream());
        } catch (IOException e) {
            // исключение ввода/вывода
            System.err.println("IOException " + e.getMessage());
            return foundURLs;
        }
        // создаём BufferedReader для чтения целых строк
        BufferedReader reader = new BufferedReader(in);
        // перебираем строки
        while (true) {
            String line;
            try {
                line = reader.readLine();
            } catch (IOException e) {
                // исключение ввода/вывода
                System.err.println("IOException " + e.getMessage());
                return foundURLs;
            }
            // если строка не считалась (больше строк нет), выходим из цикла
            if (line == null) {
                break;
            }
            // находим url
            String newURL = URLDepthPair.isLink(line, currentDepthPair.getHost());
            // если url успешно найдено, добавляем пару
            // если формат не подходит, продолжаем перебирать строки
            if (!"".equals(newURL)) {

                foundURLs.add(newURL);
            }
        }
        // закрываем сокет
        try {
            socket.close();
        } catch (IOException e) {
            // исключение ввода/вывода
            System.err.println("IOException " + e.getMessage());
            return foundURLs;
        }
        return foundURLs;
    }

}
