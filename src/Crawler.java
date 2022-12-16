import java.net.*; // Для использования сокетов
import java.io.*; // Для использования потоков
import java.util.*; // Для использования списков


public class Crawler {

    public static void main(String[] args) {
        int depth = 0; // текущая глубина

        // если при вводе параметров указаны некорректные аргументы, выводим сообщение об ошибке
        if (args.length != 2) {
            // число аргументов больше/меньше двух ИЛИ веверный ввод url
            System.out.println("usage: java Crawler <URL><depth>");
            System.exit(1); // выход с указанием на неудачное завершение
        } else {
            // является ли глубина цифрой
            try {
                depth = Integer.parseInt(args[1]);
            } catch (NumberFormatException notNumber) {
                System.out.println("usage: java Crawler <URL><depth>");
                System.exit(1); // выход с указанием на неудачное завершение
            }

        }
        // пары (URL, depth) для обработанных сайтов
        LinkedList<URLDepthPair> checkedURL = new LinkedList<>();
        // пары (URL, depth) для НЕобработанных сайтов
        LinkedList<URLDepthPair> uncheckedURL = new LinkedList<>();

        // создаём объект с глубиной 0 и с ссылкой, переданной пользователем
        URLDepthPair currentURLDepthPair = new URLDepthPair(args[0], 0);

        // добавляем введённый пользователем сайт в список необработанных ссылок
        uncheckedURL.add(currentURLDepthPair);
        // добавляем введённый пользователем сайт в список обработанных ссылок

        // список для проверенных url
        ArrayList<String> seenURL = new ArrayList<>();
        seenURL.add(currentURLDepthPair.getURL());

        // пока список необработанных сайтов НЕ пустой
        while (uncheckedURL.size() != 0) {
            // берём первую необработанную ссылку
            URLDepthPair depthPair = uncheckedURL.pop();
            // перемещаем ссылку в список с обработанными ссылками
            checkedURL.add(depthPair);
            // запоминаем её глубину
            int currentDepth = depthPair.getDepth();

            // создаём временный список для хранения всех ссылкох, расположеных на текущей странице
            LinkedList<String> currentLinksList;
            // добавляем в ранне созданный список ссылки, расположенные на текущей странице
            currentLinksList = Crawler.getAllLinks(depthPair);

            // если мы ещё не достигли максимальной глубины
            if (currentDepth < depth) {
                // перебираем все элементы в полученном списке ссылок
                for (String myURL : currentLinksList) {
                    // если такой ссылки нет
                    if (!seenURL.contains(myURL)) {
                        // добавляем в список необработанных ссылок
                        URLDepthPair newPair = new URLDepthPair(myURL, currentDepth + 1);
                        uncheckedURL.add(newPair);
                        seenURL.add(myURL);
                    }
                }
            }
        }
        getSites(checkedURL);
    }

    // метод для поиска всех ссылок расположенных на странице
    private static LinkedList<String> getAllLinks(URLDepthPair currentDepthPair) {
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

    // возвращаем список всех пар URL-глубины, которые были посещены
    private static void getSites(LinkedList<URLDepthPair> linkedList) {
        for (URLDepthPair urlDepthPair : linkedList) {
            System.out.println(urlDepthPair.toString());
        }
    }

}
