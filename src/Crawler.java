public class Crawler {

    public static void main(String[] args) {
        int depth = 0; // текущая глубина
        int numOfTreads = 4; // число задач (потоков)

        // если при вводе параметров указаны некорректные аргументы, выводим сообщение об ошибке
        if (args.length != 3) {
            // число аргументов больше/меньше трёх
            System.out.println("usage: java Crawler <URL><depth><number of threads>");
            System.exit(1); // выход с указанием на неудачное завершение
        } else {
            // является ли глубина и число задач (потоков) цифрой
            try {
                depth = Integer.parseInt(args[1]);
                numOfTreads = Integer.parseInt(args[2]);
            } catch (NumberFormatException notNumber) {
                System.out.println("usage: java Crawler <URL><depth>");
                System.exit(1); // выход с указанием на неудачное завершение
            }
        }

        // создаём объект с глубиной 0 и с ссылкой, переданной пользователем
        URLDepthPair currentURLDepthPair = new URLDepthPair(args[0], 0);
        // создаём экземпляр пула
        URLPool pool = new URLPool(depth);
        // помещаем введённый пользователем url адрес в пул с глубиной 0
        pool.addPair(currentURLDepthPair);
        // создаём указанное пользователем количество задач
        for (int i = 0; i < numOfTreads; i++){
            CrawlerTask task = new CrawlerTask(pool);
            Thread thread = new Thread(task);
            thread.start();
        }
        while (pool.threadsCount() != numOfTreads) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.err.println("InterruptedException " + e.getMessage());
            }
        }
        pool.getSites();
        System.exit(0);
    }
}
