package component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import common.ALG;
import common.BArray;
import common.Position;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class TestCommon {

  @Test
  void allCombination() {
    // C(5, 2)
    ArrayList<int[]> d = ALG.allCombination(5, 2);
    int[][] out = {
        {0, 1}, {0, 2}, {0, 3}, {0, 4}, {1, 2},
        {1, 3}, {1, 4}, {2, 3}, {2, 4}, {3, 4}
    };
    for (int x = 0; x < d.size(); x++ ) {
      assertTrue(Arrays.equals(d.get(x), out[x]));
    }

    // C(4, 0) = 1, return []
    // C(4, 1) = 4
    // C(4, 4) = 1
    int n = 4;
    for( int m : new int[]{0, 1, 4}) {
      assertEquals(ALG.combine(n, m), ALG.allCombination(n, m).size());
    }
  }

  @Test
  void allCombinationCost() {
    int n = 200, m = 3;
    Instant start, end ;

    start = Instant.now();
    ALG.allCombination(n, m);
    end = Instant.now();
    System.out.println("version new: " + Duration.between(start, end));

    //start = Instant.now();
    //ALG.allC(n, m);
    //end = Instant.now();
    //System.out.println("version old: " + Duration.between(start, end));
  }

  @Test
  void allV() {
    int[] v = {3, 4, 2, 3, 3};
    int[] pos = {1, 2};

    int[][] data = ALG.allV(pos, 2, v);
    for (int[] line : data)
      System.out.println(Arrays.toString(line));
    assertEquals(8, data.length);
  }

  @Test
  void permutation() {
    int t = 3;
    System.out.println("factorial(" + t + ") = " + ALG.cal_factorial(t));
    HashMap<ArrayList<Integer>, Integer> p = ALG.cal_permutation(t);
    p.forEach((k, v) -> System.out.println(v + " = " + k.toString()));

    Integer[] kk = {2, 0, 1};
    assertEquals(2, (int)p.get(new ArrayList<>(Arrays.asList(kk))));
  }

  @Test
  void BArray() {
    int row = 10, col = 25 ;
    BArray ba = new BArray(row, col);
    Random random = new Random();

    HashSet<Position> all = new HashSet<>();
    for( int k = 0 ; k < 200 ; k++ ) {
      int r = random.nextInt(row), c = random.nextInt(col);
      ba.setElement(r, c, true);
      all.add(new Position(r, c));
    }

    for( int k = 0 ; k < row ; k++ )
      System.out.println(ba.getRow(k));

    HashSet<Position> arr = ba.getZeroPosition();
    arr.forEach(System.out::println);
    System.out.println("# zero positions = " + arr.size());
    assertEquals(row * col - all.size(), arr.size());
  }
}
