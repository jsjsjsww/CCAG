package handler;

import common.ALG;
import combinatorial.CTModel;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Use the sat4j solver (http://www.sat4j.org) as the constraint handling technique.
 * Current version uses a basic SAT (boolean) encoding to model constraints.
 */
public class CH_Solver implements ValidityChecker {

  private int[][] relation;
  private Vector<Constraint> basicConstraint;  // at-least & at-most constraints
  private Vector<Constraint> hardConstraint;   // user specified constraints

  private SAT4J solver;   // the SAT solver
  private int numCall ;   // number of calls of the isValid() method
  private long time ;

  public CH_Solver() {
    basicConstraint = new Vector<>();
    hardConstraint = new Vector<>();
    numCall = 0;
  }

  /**
   * Initialize a validity checker.
   * @param model an object of CT model
   */
  public void init(CTModel model) {
    relation = model.relation;

    // set at-least constraint
    for (int i = 0; i < model.parameter; i++) {
      basicConstraint.add(new Constraint(relation[i]));
    }

    // set at-most constraint
    for (int i = 0; i < model.parameter; i++) {
      for (int[] row : ALG.allCombination(model.value[i], 2)) {
        int[] tp = {0 - relation[i][row[0]], 0 - relation[i][row[1]]};
        basicConstraint.add(new Constraint(tp));
      }
    }

    // set hard constraints
    for (int[] x : model.constraint) {
      hardConstraint.add(new Constraint(x));
    }

    // initialize solver
    int SS = basicConstraint.size() + hardConstraint.size();
    int MM = relation[model.parameter-1][model.value[model.parameter-1]-1];
    solver = new SAT4J(MM, SS);
    try {
      solver.addClauses(basicConstraint);
      solver.addClauses(hardConstraint);
    } catch (ContradictionException e) {
      System.err.println("CH_Solver Contradiction Error: " + e.getMessage());
    }
  }

  /**
   * Determine whether a given complete or partial test case is
   * constraints satisfiable. Any free parameters are assigned
   * to value -1.
   * @param test a complete or partial test case
   */
  public boolean isValid(final int[] test) {
    numCall += 1 ;
    if (hardConstraint.size() == 0)
      return true;

    Instant start = Instant.now();
    // transfer test to clause representation
    ArrayList<Integer> list = new ArrayList<>();
    for (int i = 0; i < test.length; i++) {
      if (test[i] != -1)
        list.add(relation[i][test[i]]);
    }
    int[] clause = list.stream().mapToInt(i -> i).toArray();

    // determine satisfiability
    boolean satisfiable = false;
    try {
      satisfiable = solver.isSatisfiable(clause);
    } catch (TimeoutException e) {
      System.err.println("CH_Solver Timeout Error: " + e.getMessage());
    }
    Instant end = Instant.now();
    time += Duration.between(start, end).getSeconds();
    return satisfiable;
  }


  public void showStatistic() {
    System.out.println("number of calls: " + numCall);
    System.out.println("total time:      " + time);
    System.out.println("average time:    " + (double)time / (double)numCall);
  }

}
