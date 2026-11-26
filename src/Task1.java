class EvenThread extends Thread {
    public void run() {
        for (int i = 2; i <= 10; i += 2) System.out.println("Чётное: " + i);
    }
}
class OddRunnable implements Runnable {
    public void run() {
        for (int i = 1; i <= 9; i += 2) System.out.println("Нечётное: " + i);
    }
}
public class Task1 {
    public static void main(String[] args) throws Exception {
        new EvenThread().start();
        new Thread(new OddRunnable()).start();
    }
}
