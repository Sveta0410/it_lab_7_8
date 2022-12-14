import java.net.*; // Для использования сокетов
import java.io.*; // Для использования потоков
import java.util.*; // Для использования списков


public class Crawler {

    public static void main(String[] args) {
        int depth = 0; // текущая глубина

        // если при вводе параметров указаны некорректные аргументы, выводим сообщение об ошибке
        if (args.length != 2 || !args[0].startsWith(URLDepthPair.URL_PREFIX)) {
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
        LinkedList<URLDepthPair> checkedURL = new LinkedList<URLDepthPair>();
        // пары (URL, depth) для НЕобработанных сайтов
        LinkedList<URLDepthPair> uncheckedURL = new LinkedList<URLDepthPair>();

        // создаём объект с глубиной 0 и с ссылкой, переданной пользователем
        URLDepthPair currentURLDepthPair = new URLDepthPair(args[0], 0);

        // добавляем введённый пользователем сайт в список необработанных ссылок
        uncheckedURL.add(currentURLDepthPair);
        // добавляем введённый пользователем сайт в список обработанных ссылок
        checkedURL.add(currentURLDepthPair);

        // пока список необработанных сайтов НЕ пустой
        while (!uncheckedURL.isEmpty()) {
            // берём первую необработанную ссылку
            URLDepthPair depthPair = uncheckedURL.pop();
            // перемещаем ссылку в список с обработанными ссылками
            checkedURL.add(depthPair);
            // запоминаем её глубину
            int currentDepth = depthPair.getDepth();

            // создаём временный список для хранения всех ссылкох, расположеных на текущей странице
            LinkedList<URLDepthPair> currentLinksList = new LinkedList<URLDepthPair>();
            // добавляем в ранне созданный список ссылки, расположенные на текущей странице
//            currentLinksList = Crawler.getAllLinks(depthPair);

        }

    }

    // метод для поиска всех ссылок расположенных на странице
//    private static LinkedList<URLDepthPair> getAllLinks(URLDepthPair currentDepthPair){
//        // создаём список для зранения найденных ссылок
//        LinkedList<URLDepthPair> foundURLs = new LinkedList<URLDepthPair>();
//        // создаём сокет
//        Socket socket;
//
//    }

    // возвращаем список всех пар URL-глубины, которые были посещены
    private static void getSites(LinkedList<URLDepthPair> linkedList) {
        for (URLDepthPair urlDepthPair : linkedList) {
            System.out.println(urlDepthPair.toString());
        }
    }

}
