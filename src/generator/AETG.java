package generator;

import combinatorial.CTModel;
import combinatorial.TestCase;
import combinatorial.TestSuite;
import common.ALG;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A greedy based (AETG-like) covering array generator with one-test-at-a-time framework.
 *
 * Myra B. Cohen et al., Constructing Interaction Test Suites for Highly-Configurable
 * Systems in the Presence of Constraints: A Greedy Approach, TSE, 34(5): 633-650, 2008
 */
public class AETG implements CAGenerator {

  /**
   * index-number pair
   * index represents a candidate parameter or value, and number represents the
   * number of uncovered combinations that pertain to this parameter or value
   */
  private class Pair implements Comparable<Pair> {
    int index;
    int number;

    private Pair(int i, int n) {
      index = i;
      number = n;
    }

    /*
     * compareTo should return < 0 if this is supposed to be
     * less than other, > 0 if this is supposed to be greater than
     * other and 0 if they are supposed to be equal
     *
     * do a descending solution via collection.sort (collection.sort is
     * ascending), and sorting is only based on the value of number
     */
    @Override
    public int compareTo(Pair B) {
      return - Integer.compare(this.number, B.number);
    }

    @Override
    public String toString() {
      return String.valueOf(index) + " (" + String.valueOf(number) + ")";
    }
  }

  public CTModel model;        // CT test model
  private int repeated = 10 ;  // number of candidates in each iteration
  private Random random;

  public AETG() {
    random = new Random();
  }

  public void setRepeated(int repeated) {
    this.repeated = repeated;
  }

  /**
   * The method to construct a t-way constrained covering
   * array for a given test model.
   * @param model a combinatorial test model
   * @param ts    the generated test suite
   */
  public void generation(CTModel model, TestSuite ts) {
    this.model = model;
    model.initialization();
    while (model.getCombUncovered() != 0) {
      int[] best = nextBestTestCase(repeated);
      ts.suite.add(new TestCase(best));
      model.updateCombination(best);
    }
  }

  /**
   * Return the next test case that covers the most uncovered combinations.
   * @param N number of candidates
   */
  private int[] nextBestTestCase( int N ) {
    int[] best = new int[model.parameter];
    long covBest = 0;
    int rep = 0;
    while (rep < N) {
      int[] tc = nextTestCase();
      long cov = model.fitnessValue(tc);
      if (cov == model.getTestCaseCoverMax()) {
        System.arraycopy(tc, 0, best, 0, model.parameter);
        break;
      }
      if (cov > covBest) {
        System.arraycopy(tc, 0, best, 0, model.parameter);
        covBest = cov;
      }
      rep += 1 ;
    }
    return best ;
  }

  /**
   * Return a new test case by greedy construction.
   */
  private int[] nextTestCase() {
    // get a random uncovered t-way combination
    // this combination has been assigned in tc[]
    int[] tc = model.getRandomUncoveredTuple().test ;

    // randomize a permutation of other parameters
    List<Integer> permutation = new ArrayList<>();
    for (int k = 0; k < model.parameter; k++) {
      if ( tc[k] == -1 )
        permutation.add(k);
    }
    Collections.shuffle(permutation);

    // for each of the remaining parameters
    for (int par : permutation) {
      tc[par] = selectBestValue(tc, par);
    }

    return tc;
  }

  /**
   * Determine whether a new parameter-value assignment is constraint satisfiable.
   *
   * @param test a partial test case
   * @param par index of parameter to be assigned
   * @param val value to be assigned
   */
  private boolean isSatisfied(final int[] test, int par, int val) {
    int[] test_temp = test.clone();
    test_temp[par] = val;
    return model.isValid(test_temp);
  }

  /**
   * Given a partial test case and a free parameter, return the value assignment
   * that is the best in terms of covering ability and at the same time constraint
   * satisfied.
   *
   * @param test a partial test case
   * @param par  the index of a free parameter
   */
  private int selectBestValue(final int[] test, int par) {
    // iterate all possible values
    ArrayList<Pair> vs = new ArrayList<>();
    for (int i = 0; i < model.value[par]; i++) {
      if (isSatisfied(test, par, i)) {
        int num = coveredSchemaNumberFast(test, par, i);
        vs.add(new Pair(i, num));
      }
    }
    Collections.sort(vs);

    // apply tie-breaking
    int max = vs.get(0).number;
    List<Pair> filtered = vs.stream()
        .filter(p -> p.number == max)
        .collect(Collectors.toList());

    int r = random.nextInt(filtered.size());
    return filtered.get(r).index;
  }

  /**
   * Given a parameter and its corresponding value, return the number of uncovered
   * combinations that can be covered by assigning this parameter value (fast version).
   *
   * @param test current test case before assigning
   * @param par  index of parameter to be assigned
   * @param val  value to be assigned to the parameter
   * @return number of uncovered combinations
   */
  private int coveredSchemaNumberFast(final int[] test, int par, int val) {

    // Only consider the combination between X and the assigned values:
    // iterate all (t-1)-way value combinations among all assigned values
    // to compute the number of uncovered combinations that can be covered
    // by assigning X.
    //
    // 1 1 1 0   X        - - - - -
    // --------  -        ---------
    // assigned  par-val  unassigned

    int fit = 0;
    int count = 0;
    int[] new_test = new int[model.parameter];
    for (int i = 0; i < model.parameter; i++) {
      new_test[i] = test[i];
      if (test[i] != -1)
        count++;
    }
    new_test[par] = val;

    int assigned = count;             // number of fixed parameters
    int required = model.t_way - 1;   // number of required parameters to form a t-way combination

    // get fixed part, not including newly assigned one
    int[] fp = new int[assigned];
    int[] fv = new int[assigned];
    for (int i = 0, j = 0; i < model.parameter; i++) {
      if (new_test[i] != -1 && i != par) {
        fp[j] = i;
        fv[j++] = new_test[i];
      }
    }

    // newly assigned one
    int[] pp = {par};
    int[] vv = {val};

    // for each possible r-way parameter combinations among fp[]
    for (int[] each : ALG.allCombination(assigned, required)) {
      int[] pos = new int[required];
      int[] sch = new int[required];
      for (int k = 0; k < required; k++) {
        pos[k] = fp[each[k]];
        sch[k] = fv[each[k]];
      }

      // construct a temp t-way combination
      int[] position = new int[model.t_way];
      int[] schema = new int[model.t_way];
      mergeArray(pos, sch, pp, vv, position, schema);

      // determine whether this t-way combination is covered or not
      if (!model.covered(position, schema, 0))
        fit++;
    }
    return fit;
  }

  /*
   * Merge two sorted arrays into a new sorted array. The ordering is
   * conducted on primary arrays (parameter array), while values in
   * additional arrays (value array) will be exchanged at the same time.
   * The new arrays will be kept in pos[] and sch[].
   */
  private void mergeArray(int[] p1, int[] v1, int[] p2, int[] v2, int[] pos, int[] sch) {
    int i, j, k ;
    for (i = 0, j = 0, k = 0; i < p1.length && j < p2.length; ) {
      if (p1[i] < p2[j]) {
        pos[k] = p1[i];
        sch[k++] = v1[i++];
      } else {
        pos[k] = p2[j];
        sch[k++] = v2[j++];
      }
    }
    if (i < p1.length) {
      for (; i < p1.length; i++, k++) {
        pos[k] = p1[i];
        sch[k] = v1[i];
      }
    }
    if (j < p2.length) {
      for (; j < p2.length; j++, k++) {
        pos[k] = p2[j];
        sch[k] = v2[j];
      }
    }
  }

  /**
   * Given a parameter and its corresponding value, return the number of uncovered
   * combinations that can be covered by assigning this parameter value.
   *
   * @param test current test case before assigning
   * @param par  index of parameter to be assigned
   * @param val  value to be assigned to the parameter
   * @return number of uncovered combinations
   */
  private int coveredSchemaNumber(final int[] test, int par, int val) {

    // To compute the number of uncovered combination that can be covered by
    // assigning X, we consider all possible t-way combinations between X and
    // both assigned (backward) and unassigned (forward) values.
    //
    // 1 1 1 0   X        - - - - -
    // --------  -        ---------
    // assigned  par-val  unassigned
    //
    // Firstly select tau1 elements from assigned values, tau1 in [0, max(t-1, |assigned|)].
    // For each tau1, select tau2 = t - tau1 - 1 elements from unassigned values. Then by
    // combining values in tau1, X and tau2, we get a new t-way combination.

    int fit = 0;
    ArrayList<Integer> fixedPara = new ArrayList<>();
    ArrayList<Integer> unfixedPara = new ArrayList<>();
    int[] tc = new int[model.parameter];
    for (int i = 0; i < model.parameter; i++) {
      tc[i] = test[i];
      if (test[i] != -1)
        fixedPara.add(i);
      else if( i != par )
        unfixedPara.add(i);
    }
    tc[par] = val;

    // the number of parameters that are fixed or not fixed
    int assigned = fixedPara.size();
    int unassigned = unfixedPara.size();

    ArrayList<Pair> sel1, sel2;
    for(int tau1 = 0; tau1 <= model.t_way - 1 && tau1 <= assigned ; tau1++ ) {
      // for each tau1 (assigned part)
      for( int[] p1 : ALG.allCombination(assigned, tau1) ) {
        sel1 = new ArrayList<>();
        // Pair<index, number>, default sort is based on the value of number
        for (int l : p1)
          sel1.add(new Pair(tc[fixedPara.get(l)], fixedPara.get(l)));

        // for each tau_2 (unassigned part)
        int tau2 = model.t_way - tau1 - 1 ;
        for( int[] p2 : ALG.allCombination(unassigned, tau2) ) {
          int[] pos2 = new int[tau2];
          for( int k2=0 ; k2<tau2 ; k2++ )
            pos2[k2] = unfixedPara.get(p2[k2]);

          int[][] vComb = ALG.allV(pos2, tau2, model.value);
          for( int[] sch2 : vComb ) {
            sel2 = new ArrayList<>();
            for( int k=0 ; k<tau2 ; k++ )
              sel2.add(new Pair(sch2[k], pos2[k]));

            // now we can combine sel1, X and sel2 together
            sel2.addAll(sel1);
            sel2.add(new Pair(val, par));

            int[] position = new int[model.t_way];
            int[] schema = new int[model.t_way];
            combineTriple(sel2, position, schema);

            // determine whether the t-way combination is covered or not
            if (!model.covered(position, schema, 0))
              fit++;

          } // end for each sch2
        } // end for each pos2
      } // end for each pos1 + sch1
    }
    return fit ;
  }

  private void combineTriple(ArrayList<Pair> ss, int[] pos, int[] sch) {
    if( pos.length != ss.size() ) {
      System.err.println("combineTriple error");
      return;
    }

    Collections.sort(ss);
    for( int x=0, k=ss.size()-1 ; k>=0 ; k-- ) {
      pos[x] = ss.get(k).number;
      sch[x++] = ss.get(k).index;
    }
  }



}
