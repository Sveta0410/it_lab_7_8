import java.util.ArrayList;
import java.util.LinkedList;

public class URLPool {

    private int maxDepth;
    private LinkedList<URLDepthPair> checkedURL; // пары (URL, depth) для обработанных сайтов
    private LinkedList<URLDepthPair> uncheckedURL; // пары (URL, depth) для НЕобработанных сайтов
    ArrayList<String> seenURL; // список для проверенных url
    private int waiting = 0; // сколько потоков ожидает новый URL-адрес


    public URLPool(int depth) {
        checkedURL = new LinkedList<>();
        uncheckedURL = new LinkedList<>();
        seenURL = new ArrayList<>();
        maxDepth = depth;
    }

    // проверка размера
    public synchronized int size(){
        return uncheckedURL.size();
    }

    // проверка количества ожидающих потоков
    public synchronized int threadsCount(){
        return waiting;
    }


    // getPair для получения пары (URL, depth)
    // synchronized - чтобы заблокировать доступ к методу, если его использует другой поток
    public synchronized URLDepthPair getPair() {
        // когда нет доступных url адресов
        if (uncheckedURL.size() == 0){
            // увеличиваем счётчик потоков
            waiting ++;
            try {
                // режим ожидания
                wait();
            } catch (InterruptedException e) {
                System.err.println("InterruptedException" + e.getMessage());
            }
            waiting -= 1; // уменьшаем количество ожидающих потоков
        }

        return uncheckedURL.removeFirst(); // удаляем и возвращаем первый элемент из списка
    }

    // addPair для добавления пары (URL, depth)
    // synchronized - это ключевое слово позволяет заблокировать доступ к методу, если его использует другой поток
    public synchronized void addPair(URLDepthPair pair) {
        String pairURL = pair.getURL();
        // если URL в списке проверенных нет
        if (!seenURL.contains(pairURL)) {
            seenURL.add(pairURL); // добавляем url в список с просмотренными url

            // если глубина меньше максимальной
            if (pair.getDepth() < maxDepth) {
                uncheckedURL.add(pair); // добавляем пару в список НЕобработанных сайтов
                notify(); // продолжаем работу потока, у которого ранее был вызван метод wait()
            }
                checkedURL.add(pair); // добавляем пару в список обработанных сайтов
        }
    }
    public synchronized void getSites() {
        System.out.println("Result:");
        for (URLDepthPair urlDepthPair : checkedURL) {
            System.out.println(urlDepthPair.toString());
        }
    }
}
