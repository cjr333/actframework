package benchmark;

import async.model.Member;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
public class AsyncTest {
  private Random random;
  private final int MEMBER_COUNT = 100000;

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Fork(2)
  @Measurement(iterations = 5)
  @Warmup(iterations = 5)
  public void syncInsert() {
    for (int i = 0; i < MEMBER_COUNT; i++) {
      Member member = new Member();
      member.setId(UUID.randomUUID().toString());
      member.setPasswd("test");
      member.setNickname(UUID.randomUUID().toString().substring(0, 8));
      System.out.println(member.getId());
    }
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Fork(2)
  @Measurement(iterations = 5)
  @Warmup(iterations = 5)
  public void asyncInsert() throws ExecutionException, InterruptedException {
    for (int i = 0; i < MEMBER_COUNT; i++) {
      Member member = new Member();
      member.setId(UUID.randomUUID().toString());
      member.setPasswd("test");
      CompletableFuture<String> future = CompletableFuture.supplyAsync(this::getRandomId);
      member.setNickname(UUID.randomUUID().toString().substring(0, 8));
      member.setId(future.get());
      CompletableFuture.runAsync(() -> System.out.println(member.getId()));
    }
  }

  private String getRandomId() {
    return UUID.randomUUID().toString();
  }
}
