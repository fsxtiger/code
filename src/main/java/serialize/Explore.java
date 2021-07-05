package serialize;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

public class Explore {
    private static ForkJoinPool forkJoinPool = new ForkJoinPool(7);
    private static ExecutorService ex = Executors.newFixedThreadPool(7);


    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
//
        long sum = 0;
        for (int i = 0; i <= 10000000; i ++) {
            TimeUnit.MICROSECONDS.sleep(20);
            sum += i;
        }
        System.out.println("serial cost time:" + (System.currentTimeMillis() - start) + ", sum = " + sum);

        start = System.currentTimeMillis();

        sum = forkJoinPool.invoke(new SumTask(1, 10000000));
        System.out.println("parallel cost time" + (System.currentTimeMillis() - start) + ", sum = " + sum);

        start = System.currentTimeMillis();
        List<Future<Long>> futures = new ArrayList<>();
        for (int x = 0; x < 10000000; x = x + 11){
            final int y = x;
            futures.add(ex.submit(() -> {
                long stepSum = 0;
                for (int i = y; i <= y + 10; i++) {
                    stepSum += i;
                }

                return stepSum;
            }));
        }

        for (Future<Long> future : futures) {
            sum += future.get();
        }
        System.out.println("parallel cost time" + (System.currentTimeMillis() - start) + ", sum = " + sum);
    }
}

class SumTask extends RecursiveTask<Long> {
    public long start;
    public long end;

    public SumTask(long start, long end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        if (end - start == 1) {
            try {
                TimeUnit.MICROSECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return start + end;
        }
        if (start == end) {
            try {
                TimeUnit.MICROSECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return start;
        }
        long mid = (start + end) >> 1;
        SumTask task1 = new SumTask(start, mid);
        ForkJoinTask<Long> task2 = new SumTask(mid + 1, end);

        invokeAll(task1, task2);

        return task1.join()
                + task2.join();
    }
}
