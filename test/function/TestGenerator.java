package function;

import combinatorial.CTModel;
import combinatorial.FileReader;
import combinatorial.TestSuite;
import generator.AETG;
import generator.CAGenerator;
import generator.RT;
import generator.IPO1;
import handler.CH_MFTVerifier;
import handler.CH_Solver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

public class TestGenerator {

  private static String mp = "benchmarks/#_2way.model";
  private static String cp = "benchmarks/#.constraints";

  private CTModel model ;
  private TestSuite ts;
  private CAGenerator gen;
  private Instant start, end;

  @BeforeEach
  void before() {
    // read model file
    String name = "gcc";
    FileReader file = new FileReader(mp.replace("#", name), cp.replace("#", name));
    model = new CTModel(file.parameter, file.value, 2, file.constraint, new CH_MFTVerifier());
    //model = new CTModel(file.parameter, file.value, 2, file.constraint, new CH_Solver());
    // test suite object
    //System.out.println("model ok!");
    ts = new TestSuite();
  }

  @AfterEach()
  void runGeneration() {
    start = Instant.now();
    gen.generation(model, ts);
    end = Instant.now();
    System.out.println("CA Size = " + ts.getTestSuiteSize());
    System.out.println("Time Cost = " + Duration.between(start, end));
  }

  @Test
  void random_generator() {
    gen = new RT();
  }

  @Test
  void aetg_generator() {
    gen = new AETG();
    ((AETG)gen).setRepeated(1);
  }

  @Test
  void ipo_generator() {
    gen = new IPO1();

  }








}
