package generator;

import combinatorial.CTModel;
import combinatorial.TestCase;
import combinatorial.TestSuite;

import java.util.Arrays;
import java.util.Random;

/**
 * Example
 * Randomly sample test cases until the required coverage is achieved.
 */
public class RT implements CAGenerator {

  private Random random ;

  public RT() {
    random = new Random();
  }

  /**
   * The method to construct a t-way constrained covering
   * array for a given test model.
   * @param model a combinatorial test model
   * @param ts    the generated test suite
   */
  public void generation(CTModel model, TestSuite ts) {
    // initialize the set of combinations to be covered
    model.initialization();

    // one-test-at-a-time framework
    while (model.getCombUncovered() > 0) {
      // sample a test case and calculate its fitness (# newly covered combinations)
      int[] test = sample(model);
      long cov = model.fitnessValue(test);

      // if it is invalid or fitness is zero, try again
      while (!model.isValid(test) || cov == 0) {
        test = sample(model);
        cov = model.fitnessValue(test);
      }

      System.out.println(Arrays.toString(test) + " cov = " + cov);

      // update the set of combinations to be covered
      model.updateCombination(test);

      // add it into the test suite
      TestCase tc = new TestCase(test);
      ts.suite.add(tc);
    }
  }

  /**
   * Randomly sample a test case. It might be an invalid one.
   * @param model a combinatorial test model
   */
  private int[] sample(CTModel model) {
    int[] tc = new int[model.parameter];
    for ( int i=0 ; i<model.parameter ; i++ )
      tc[i] = random.nextInt(model.value[i]);
    return tc;
  }

}
