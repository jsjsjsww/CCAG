package component;

import combinatorial.CTModel;
import combinatorial.FileReader;
import handler.CH_Solver;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

public class TestHandler {

  private static String mp = "benchmarks/benchmark_#_2way.model";
  private static String cp = "benchmarks/benchmark_#.constraints";

  @Test
  void basic_solver_init() {
    String name = "spinv";
    FileReader f = new FileReader(mp.replace("#", name), cp.replace("#", name));
    CH_Solver solver = new CH_Solver();
    CTModel m = new CTModel(f.parameter, f.value, 2, f.constraint, solver);

    Instant start = Instant.now();
    m.initialization();
    Instant end = Instant.now();

    m.show();
    System.out.println("COST = " + Duration.between(start, end));

    System.out.println("-------- solver statistic --------");
    solver.showStatistic();
  }


}
