package agency.shitcoding.arena.gamestate.tutorial;

import agency.shitcoding.arena.ArenaShooter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class ScriptRunner implements AutoCloseable {
  private BukkitTask task;
  private final ScriptBuilder scriptBuilder;
  AtomicInteger atomPointer = new AtomicInteger(0);
  Set<ScriptRunner> children = new HashSet<>();

  public void start() {
    task = Bukkit.getScheduler().runTaskTimer(ArenaShooter.getInstance(), this::task, 1L, 1L);
  }

  public void task() {
    if (atomPointer.get() >= scriptBuilder.getScriptNodes().size()) {
      task.cancel();
      return;
    }
    var atom = scriptBuilder.getScriptNodes().get(atomPointer.get());
    switch (atom) {
      case ScriptAtom.RunningScriptAtom run -> {
        run.runnable().run();
        atomPointer.incrementAndGet();
      }
      case ScriptAtom.WaitingScriptAtom wait -> {
        if (wait.condition().get()) {
          atomPointer.incrementAndGet();
        }
      }
      case ScriptAtom.ForkingScriptAtom fork -> {
        var runner = new ScriptRunner(fork.scriptBuilder());
        children.add(runner);
        runner.start();
        atomPointer.incrementAndGet();
      }
    }
  }

  @Override
  public void close() {
    for (ScriptRunner child : children) {
      child.close();
    }
    task.cancel();
  }
}
