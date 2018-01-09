package component;

import static org.junit.jupiter.api.Assertions.assertEquals;

import combinatorial.CTModel;
import common.*;
import handler.CH_Solver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class TestCombinatorial {

  @Test
  void model_basic() {
    int p = 5;
    int[] v = new int[p];
    for (int k = 0; k < p; k++)
      v[k] = 3;
    int t = 2;
    // 1  4  7  10  13
    // 2  5  8  11  14
    // 3  6  9  12  15
    ArrayList<int[]> c = new ArrayList<>();
    c.add(new int[]{-1, -7});       // (0, -, 0, -, -)
    c.add(new int[]{-8, -10, -15}); // (-, -, 1, 0, 2)

    CTModel model = new CTModel(p, v, t, c, new CH_Solver());
    model.initialization();
    assertEquals(89, model.getCombUncovered());
  }

}
