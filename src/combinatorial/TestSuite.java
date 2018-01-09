package combinatorial;

import java.util.ArrayList;

/**
 * Basic data structure and evaluations for classic test suite
 */
public class TestSuite {

  public ArrayList<TestCase> suite;

  public TestSuite() {
    this.suite = new ArrayList<>();
  }

  /**
   * Return size of test suite.
   */
  public int getTestSuiteSize() {
    return suite.size() ;
  }

}
