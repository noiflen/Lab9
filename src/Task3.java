import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
record OrderEx(int id, String type, int quantity) {}
class ShoeWarehouseEx {
    private final Queue<OrderEx> orders = new LinkedList<>();
    private final int capacity;
    private final ExecutorService fulfillExecutor;
    public ShoeWarehouseEx(int capacity, int consumers) {
        this.capacity = capacity;
        this.fulfillExecutor = Executors.newFixedThreadPool(consumers);
    }
    public synchronized void receiveOrder(OrderEx order) throws InterruptedException {
        while (orders.size() >= capacity) wait();
        orders.add(order);
        notifyAll();
    }
    public synchronized OrderEx fulfillOrder() throws InterruptedException {
        while (orders.isEmpty()) wait();
        OrderEx order = orders.poll();
        notifyAll();
        return order;
    }
    public void startConsumers(int numConsumers) {
        for (int i = 0; i < numConsumers; i++) {
            final int id = i;
            fulfillExecutor.submit(() -> {
                for (int j = 0; j < 5; j++) {
                    try {
                        OrderEx o = fulfillOrder();
                        System.out.println("Пул потребитель " + id + " обработал: " + o);
                    } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }
            });
        }
    }
    public void shutdown() throws InterruptedException {
        fulfillExecutor.shutdown();
        fulfillExecutor.awaitTermination(1, TimeUnit.MINUTES);
    }
}
public class Task3 {
    public static void main(String[] args) throws Exception {
        ShoeWarehouseEx warehouse = new ShoeWarehouseEx(5, 2);
        warehouse.startConsumers(2);
        ExecutorService producerExecutor = Executors.newFixedThreadPool(2);
        for (int i = 1; i <= 10; i++) {
            final int id = i;
            producerExecutor.submit(() -> {
                try {
                    OrderEx o = new OrderEx(id, "Adidas", 5);
                    warehouse.receiveOrder(o);
                    System.out.println("Пул производитель добавил: " + o);
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
        }
        producerExecutor.shutdown();
        producerExecutor.awaitTermination(1, TimeUnit.MINUTES);
        warehouse.shutdown();
        System.out.println("Готово");
    }
}