package generator;

import combinatorial.CTModel;
import combinatorial.TestSuite;

public interface CAGenerator {

  /**
   * The method to construct a t-way constrained covering
   * array for a given test model.
   * @param model a combinatorial test model
   * @param ts    the generated test suite
   */
  void generation(CTModel model, TestSuite ts);
}
