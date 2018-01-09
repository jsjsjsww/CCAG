package handler;

import combinatorial.CTModel;


public interface ValidityChecker {

  /**
   * Initialize a validity checker.
   * @param model an object of CT model
   */
  void init(CTModel model);

  /**
   * Determine whether a given complete or partial test case is
   * constraints satisfiable. Any free parameters are assigned
   * to value -1.
   * @param test a complete or partial test case
   */
  boolean isValid(final int[] test);

}
