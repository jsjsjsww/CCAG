package common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

/**
 * A two dimensional array where each element is a boolean variable.
 */
public class BArray {

  private boolean[][] matrix;
  private HashSet<Position> zeros;  // the list of <row, column> of all false positions
  private Random random = new Random();

  public BArray(int row) {
    matrix = new boolean[row][];
    zeros = new HashSet<>();
    for (int i = 0; i < row; i++)
      matrix[i] = null;
  }

  public BArray(int row, int column) {
    matrix = new boolean[row][column];
    zeros = new HashSet<>();
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < column; j++) {
        matrix[i][j] = false;
        for (int k = 0; k < 8; k++)
          zeros.add(new Position(i, j));
      }
    }
  }

  public boolean[][] getMatrix(){
    return matrix;
  }

  /**
   * Initialize a single row by zero.
   *
   * @param index  index of row
   * @param column number of elements in that row
   */
  public void initializeRow(int index, int column) {
    if (matrix[index] == null)
      matrix[index] = new boolean[column];

    for (int j = 0; j < column; j++) {
      matrix[index][j] = false;
      for (int k = 0; k < 8; k++)
        zeros.add(new Position(index, j));
    }
  }

  /**
   * Get the element in row i and column j.
   *
   * @param i index of row
   * @param j index of column
   * @return false or true
   */
  public boolean getElement(int i, int j) {
    return matrix[i][j];
  }

  /**
   * Set the element in row i and column j to true or false.
   *
   * @param i     index of row
   * @param j     index of column
   * @param value new value
   */
  public void setElement(int i, int j, boolean value) {
    Position p = new Position(i, j);
    if (value)
      zeros.remove(p);
    else
      zeros.add(p);
    matrix[i][j] = value;
  }

  /**
   * Convert a specified row into a string representation.
   *
   * @param index  index of row
   * @return string representation
   */
  public String getRow(int index) {
    StringBuilder sb = new StringBuilder();
    for (boolean b : matrix[index]) {
      String str = b ? "1 " : "0 ";
      sb.append(str);
    }
    return sb.toString();
  }

  /**
   * Return the list of zero positions.
   */
  public HashSet<Position> getZeroPosition() {
    return zeros;
  }

  /**
   * Return a random zero position.
   */
  public Position gerRandomPosition() {
    int index = random.nextInt(zeros.size());
    Iterator<Position> it = zeros.iterator();
    for (int i = 0; i < index; i++)
      it.next();
    return it.next();
  }
}

