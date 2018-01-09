package handler;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.util.Vector;

public class SAT4J implements Cloneable {

  private int MAXVAR;    // maximum number of variable
  private int NBCLAUSES; // number of clauses
  private ISolver solver;

  public SAT4J(int MAXVAR, int NBCLAUSES) {
    this.MAXVAR = MAXVAR;
    this.NBCLAUSES = NBCLAUSES;

    // set default solver
    solver = SolverFactory.newDefault();
    solver.newVar(MAXVAR);
    solver.setExpectedNumberOfClauses(NBCLAUSES);
  }

  /**
   * Feed the solver using DIMACS format
   * @param constraint a list of permanent constraints
   */
  public void addClauses(final Vector<Constraint> constraint) throws ContradictionException {
    for (Constraint clause : constraint) {
      solver.addClause(new VecInt(clause.disjunction));
    }
  }

  /**
   * Determine whether a given clause is satisfiable or not.
   * @param clause the candidate clause
   */
  public boolean isSatisfiable(final int[] clause) throws TimeoutException {
    VecInt c = new VecInt(clause);
    IProblem problem = solver;
    return problem.isSatisfiable(c);
  }
}