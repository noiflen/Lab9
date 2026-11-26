import java.util.LinkedList;
import java.util.Queue;
record Order(int id, String type, int quantity) {}
class ShoeWarehouse {
    private final Queue<Order> orders = new LinkedList<>();
    private final int capacity;
    public ShoeWarehouse(int capacity) { this.capacity = capacity; }
    public synchronized void receiveOrder(Order order) throws InterruptedException {
        while (orders.size() >= capacity) wait();
        orders.add(order);
        notifyAll();
    }
    public synchronized Order fulfillOrder() throws InterruptedException {
        while (orders.isEmpty()) wait();
        Order order = orders.poll();
        notifyAll();
        return order;
    }
}
public class Task2 {
    public static void main(String[] args) throws Exception {
        ShoeWarehouse warehouse = new ShoeWarehouse(5);
        Thread[] consumers = new Thread[2];
        for (int i = 0; i < 2; i++) {
            final int id = i;
            consumers[i] = new Thread(() -> {
                for (int j = 0; j < 5; j++) {
                    try {
                        Order o = warehouse.fulfillOrder();
                        System.out.println("Потребитель " + id + " обработал: " + o);
                    } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }
            });
            consumers[i].start();
        }
        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    Order o = new Order(i, "Nike", 10);
                    warehouse.receiveOrder(o);
                    System.out.println("Производитель добавил: " + o);
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        });
        producer.start();
        producer.join();
        for (Thread c : consumers) c.join();
        System.out.println("Готово");
    }
}