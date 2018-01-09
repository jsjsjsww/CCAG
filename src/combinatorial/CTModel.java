package combinatorial;

import common.ALG;
import common.BArray;
import common.Position;
import handler.ValidityChecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static java.lang.Math.abs;

/**
 * Combinatorial test model, i.e. Model = < P, V, R, C >
 */
public class CTModel {

  public int parameter;
  public int[] value;
  public int t_way;
  // a list of constraints, each is represented by a conjunction of literals
  public ArrayList<int[]> constraint;

  // relation indicates the index of a given parameter value (starts from 1)
  // For example, the mapping for CA(N;t,5,3) is as follows:
  //  p1  p2  p3  p4  p5
  //   1   4   7  10  13
  //   2   5   8  11  14
  //   3   6   9  12  15
  public int[][] relation;

  // the list of parameter combinations
  public ArrayList<int[]> allPc;

  // constraint validity checker
  private ValidityChecker checker;

  // the set of parameters that are involved in constraints
  // note that the index of parameter starts from 0
  private HashSet<Integer> constrainedParameters;

  // combinations to be covered
  private BArray combination;       // the combinations to be covered
  private long combRaw;             // the total number of possible combinations
  private long combAll;             // the total number of valid combinations to be covered
  private long combUncovered;       // the number of uncovered combinations
  private int uniformRow;           // the number of uniform strength rows in combination, i.e. C(parameter, t_way)
  private int testCaseCoverMax;     // the maximum number of combinations that can be covered by a test case

  public CTModel(FileReader file, ValidityChecker checker) {
    this(file.parameter, file.value, file.t_way, file.constraint, checker);
  }

  public CTModel(int parameter, int[] value, int t_way, ArrayList<int[]> constraint,
                 ValidityChecker checker) {
    this.parameter = parameter;
    this.value = value;
    this.t_way = t_way;
    this.constraint = constraint;
    uniformRow = ALG.combine(parameter, t_way);
    testCaseCoverMax = this.uniformRow;

    // determine constrained parameters
    constrainedParameters = new HashSet<>();
    HashSet<Integer> temp = new HashSet<>();
    for (int[] c : constraint) {
      for (int v : c) {
        temp.add(abs(v));
      }
    }

    // set mapping relationship
    relation = new int[parameter][];
    int start = 1;
    for (int i = 0; i < parameter; i++) {
      relation[i] = new int[value[i]];
      for (int j = 0; j < value[i]; j++, start++) {
        relation[i][j] = start;
        if (temp.contains(start))
          constrainedParameters.add(i);
      }
    }

    // set constraint checker
    this.checker = checker;
   // System.out.println("init");
    this.checker.init(this);
    //System.out.println("init ok");
  }

  public void IPOsortValue(){

    int[] value1 = new int[parameter];
    int[] value2 = new int[parameter];
    for(int i = 0; i < parameter; i++) {
      value1[i] = value[i];
      value2[i] = value[i];
    }
    int[] orderedParameters = new int[parameter];
    int[] reversePar = new int[parameter];
    for(int i = 0; i < parameter; i++){
      int max = value1[0];
      int maxI = 0;
      for(int j = 1; j < parameter; j++)
        if(value1[j] > max){
          max = value1[j];
          maxI = j;
        }
      orderedParameters[i] = maxI;
      reversePar[maxI] = i;
      value1[maxI] = -1;
    }
    for(int i = 0; i < parameter; i++)
      value[i] = value2[orderedParameters[i]];

    ArrayList<int[]> constraint1 = new ArrayList<>();
    for(int i = 0; i < constraint.size(); i++){
      int[] transferConstraint = new int[constraint.get(i).length];
      for(int j = 0; j < constraint.get(i).length; j++){
        int temp = Math.abs(constraint.get(i)[j]);
        int originalPar = -1;
        int sumPar = 0;
        while(temp > sumPar){
          originalPar++;
          sumPar += value2[originalPar];
        }
        int baseNum = 0;
        for(int k = 0; k < reversePar[originalPar] ; k++)
          baseNum += value[k];
        transferConstraint[j] = -(baseNum + temp - sumPar + value2[originalPar]);


      }
      constraint1.add(transferConstraint);



    }
   // for(int i = 0;i<parameter;i++)
      //System.out.print(value[i]+" ");
    //System.out.println();
    //for(int i = 0; i < constraint1.size(); i++){
     // for(int j = 0; j< constraint1.get(i).length;j++)
        //System.out.print(constraint1.get(i)[j]+" ");
     // System.out.println();
    //}
    constraint = constraint1;

    /*constrainedParameters = new HashSet<>();
    HashSet<Integer> temp = new HashSet<>();
    for (int[] c : constraint) {
      for (int v : c) {
        temp.add(abs(v));
      }
    }
    relation = new int[parameter][];
    int start = 1;
    for (int i = 0; i < parameter; i++) {
      relation[i] = new int[value[i]];
      for (int j = 0; j < value[i]; j++, start++) {
        relation[i][j] = start;
        if (temp.contains(start))
          constrainedParameters.add(i);
      }

    }
*/

    uniformRow = ALG.combine(parameter, t_way);
    testCaseCoverMax = this.uniformRow;

    // determine constrained parameters
    constrainedParameters = new HashSet<>();
    HashSet<Integer> temp = new HashSet<>();
    for (int[] c : constraint) {
      for (int v : c) {
        temp.add(abs(v));
      }
    }

    // set mapping relationship
    relation = new int[parameter][];
    int start = 1;
    for (int i = 0; i < parameter; i++) {
      relation[i] = new int[value[i]];
      for (int j = 0; j < value[i]; j++, start++) {
        relation[i][j] = start;
        if (temp.contains(start))
          constrainedParameters.add(i);
      }
    }


  }

  public BArray getComba(){
    return combination;
  }
  public long getCombRaw() {
    return combRaw;
  }

  public long getCombAll() {
    return combAll;
  }

  public long getCombUncovered() {
    return combUncovered;
  }

  public long getTestCaseCoverMax() {
    return testCaseCoverMax;
  }

  /**
   * Determine whether a complete or partial test case is
   * constraint satisfiable. The result is obtained from
   * the specified ValidityChecker.
   *
   * @param test a complete or partial test case
   */
  public boolean isValid(final int[] test) {
    // only call ValidityChecker when there has a fixed parameter
    // in test that is involved in constraints
    for (int i = 0 ; i < parameter ; i++) {
     // System.out.println(test[i]);
      if ( test[i] != -1 && constrainedParameters.contains(i) ) {
        return checker.isValid(test);
      }
    }
    return true;
  }

  public boolean isValid(final Tuple tuple) {
    for (int p : tuple.position) {
      if (constrainedParameters.contains(p)) {
        return checker.isValid(tuple.test);
      }
    }
    return true;
  }

  /**
   * Initialize all combinations to be covered. Generally, this should be
   * the first step before invoking any particular generation algorithms.
   */
  public void initialization() {
    combination = null;
    combRaw = combAll = combUncovered = 0;
    allPc = ALG.allCombination(parameter, t_way);

    // assign uniformRow rows
    combination = new BArray(uniformRow);

    // enumerate all t-way combinations to calculate the number of combinations
    // to be covered and remove invalid combinations
    int i = 0;
    int count = 0;
    for (int[] pos : allPc) {
      // calculate the number of t-way combinations
      int cc = ALG.combineValue(pos, value);
      combination.initializeRow(i++, cc);

      // update variables
      combRaw += cc;
      combAll += cc;
      combUncovered += cc;

      // handle constraints

      if (constraint.size() > 0) {
        for (int[] sch : ALG.allV(pos, t_way, value)) {
          Tuple tuple = new Tuple(pos, sch, parameter);
          if (!isValid(tuple)) {
            covered(pos, sch, 1);
            combAll--;
            count++;
          }
        }
      }
    }
    System.out.println("ini "+count);
    // end each parameter combination
  }

  /**
   * Return a random uncovered t-way combination. If there does not
   * exist such a combination, return null.
   */
  public Tuple getRandomUncoveredTuple() {
    if ( combUncovered == 0 )
      return null ;

    Position e = combination.gerRandomPosition();
    //    row <- combination of parameters
    // column <- combination of parameter values
    int[] pos = allPc.get(e.row);
    int[] sch = ALG.num2val(e.column, pos, t_way, value);
    return new Tuple(pos, sch, parameter);
  }

  /**
   * Return all uncovered t-way combinations.
   */
  public ArrayList<Tuple> getAllUncoveredTuple() {
    ArrayList<Tuple> tuple = new ArrayList<>();
    if ( combUncovered == 0 )
      return tuple;

    HashSet<Position> ele = combination.getZeroPosition();
    ele.forEach( x -> {
      int[] pos = allPc.get(x.row);
      int[] sch = ALG.num2val(x.column, pos, t_way, value);
      tuple.add(new Tuple(pos, sch, parameter));
    });
    return tuple ;
  }

  /**
   * Get the number of uncovered combinations that can be
   * covered by a given test case.
   *
   * @param test a test case
   */
  public long fitnessValue(final int[] test) {
    return fitness(test, 0);
  }

  /**
   * Get the number of uncovered combinations that can be
   * covered by a given test suite.
   *
   * @param suite a test suite
   */
  public long fitnessValue(ArrayList<int[]> suite) {
    if ( suite == null || suite.size() == 0 )
      return -1;

    // iterate each parameter combination
    long total_covered = 0;
    for (int[] pos : allPc) {
      // all possible value combinations
      int len = ALG.combineValue(pos, value);
      int[] cover = new int[len];

      int covered = 0;
      // for each row in tests
      int[] sch = new int[t_way];
      for ( int[] tc : suite ) {
        for (int k = 0; k < t_way; k++)
          sch[k] = tc[pos[k]];
        int index = ALG.val2num(pos, sch, t_way, value);
        if (cover[index] == 0) {
          cover[index] = 1;
          covered++;
        }
      }
      total_covered += covered;
    }
    combUncovered = combAll - total_covered;
    return combUncovered;
  }

  /**
   * Update uncovered combinations according to a given
   * test case.
   *
   * @param test a test case
   */
  public void updateCombination(final int[] test) {
    fitness(test, 1);
  }

  /**
   * The method to calculate the fitness value of a given test case.
   *
   * If FLAG = 0, only a number is returned (just for evaluation).
   * If FLAG = 1, combination and combUncovered will be updated accordingly.
   */
  private long fitness(final int[] test, int FLAG) {
    long num = 0;
    // get each combination of C(parameter, t_way)
    for( int[] position : allPc ) {
      int[] schema = new int[t_way];
      for (int k = 0; k < t_way; k++)
        schema[k] = test[position[k]];
      // if it is covered
      if (!covered(position, schema, FLAG))
        num++;
    }
    return num;
  }

  /**
   * Determine whether a particular k-way combination is covered or not,
   * where position[] indicates the indexes of parameters, and schema[]
   * indicates the corresponding parameter values.
   *
   * If FLAG = 0, combination and combUncovered will not be updated.
   * If FLAG = 1, combination and combUncovered will be updated accordingly.
   */
  public boolean covered(int[] position, int[] schema, int FLAG) {
    // check the value of combination[row][column] to determine cover or not
    // the row and column is computed based on position and schema, respectively
    int row = ALG.combine2num(position, parameter, t_way);
    int column = ALG.val2num(position, schema, t_way, value);

    // determiner whether combination is covered or not
    boolean cov = combination.getElement(row, column);
    if ( !cov & FLAG == 1) {
      // if uncovered and flag = 1, set this combination as covered
      combination.setElement(row, column, true);
      combUncovered--;
    }
    return cov;
  }

  /**
   * Display basic information.
   */
  public void show() {
    System.out.println("parameter = " + parameter);
    System.out.println("value = " + Arrays.toString(value));
    System.out.println("t-way = " + t_way);
    System.out.println("size of original constraints = " + constraint.size());
    constraint.forEach(x -> System.out.println(Arrays.toString(x)));
    System.out.println("constrained parameters = " + constrainedParameters);
    System.out.println("raw space = " + combRaw + ", valid combinations = " + combAll);
    System.out.println("currently uncovered valid combinations = " + combUncovered);
  }

}