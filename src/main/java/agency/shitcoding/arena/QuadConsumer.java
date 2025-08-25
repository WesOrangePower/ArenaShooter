package agency.shitcoding.arena;

import org.jspecify.annotations.NullUnmarked;

@FunctionalInterface
@NullUnmarked
public interface QuadConsumer<T, T1, T2, T3> {

  void accept(T t, T1 t1, T2 t2, T3 t3);
}
