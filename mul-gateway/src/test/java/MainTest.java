import java.util.concurrent.atomic.AtomicInteger;

public class MainTest
{
    public static void main(String[] args)
    {
        AtomicInteger integer = new AtomicInteger(0);
        int current = integer.get();
        int next = (current + 1) % 10;
    }
}
