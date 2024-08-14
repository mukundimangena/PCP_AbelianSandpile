package serialAbelianSandpile;

import java.util.concurrent.RecursiveTask;

public class Grid extends RecursiveTask<Boolean> {

          private static final int THRESHOLD = 50; // Threshold for splitting tasks
          private int[][] grid;
          private int[][] updateGrid;
          private int startRow, endRow, startCol, endCol;
          private int rows , columns;
      
          public Grid(int[][] grid, int startRow, int endRow, int startCol, int endCol) {
              this.grid = grid;
              this.startRow = startRow;
              this.endRow = endRow;
              this.startCol = startCol;
              this.endCol = endCol;
              this.updateGrid = new int[grid.length][grid[0].length];
          }
         

	

	public int getRows() {
		return rows - 2; // less the sink
	}

	public int getColumns() {
		return columns - 2;// less the sink
	}

	int get(int i, int j) {
		return this.grid[i][j];
	}

	void setAll(int value) {
		// borders are always 0
		for (int i = 1; i < rows - 1; i++) {
			for (int j = 1; j < columns - 1; j++)
				grid[i][j] = value;
		}
	}

	// for the next timestep - copy updateGrid into grid
	// this method changes the the grid to the next step in the simulation
	public void nextTimeStep() {
		for (int i = 1; i < rows - 1; i++) {
			for (int j = 1; j < columns - 1; j++) {
				this.grid[i][j] = updateGrid[i][j];
			}
		}
	}

          boolean update() {
                    boolean changed = false;

                    for (int i = startRow; i < endRow; i++) {
                        for (int j = startCol; j < endCol; j++) {
                            // Calculate new value for grid[i][j] and store it in updateGrid
                            updateGrid[i][j] = (grid[i][j] % 4) +
                                    (grid[i - 1][j] / 4) +
                                    grid[i + 1][j] / 4 +
                                    grid[i][j - 1] / 4 +
                                    grid[i][j + 1] / 4;

				if (grid[i][j] != updateGrid[i][j]) {
					changed = true;

				}
			}
		}
		if (changed) {
			nextTimeStep();
		}
		return changed;
	}

          public Boolean compute(){
                    if ((endRow - startRow) * (endCol - startCol) <= THRESHOLD) {
                              printGrid();
                              return update();
                          } else {
                              // Otherwise, split the task into two or more smaller tasks
                              int midRow = (startRow + endRow) / 2;
                              int midCol = (startCol + endCol) / 2;
                  
                              Grid topLeft     = new Grid(grid, startRow, midRow, startCol, midCol);
                              Grid topRight    = new Grid(grid, startRow, midRow, midCol, endCol);
                              Grid bottomLeft  = new Grid(grid, midRow, endRow, startCol, midCol);
                              Grid bottomRight = new Grid(grid, midRow, endRow, midCol, endCol);
                           
                              invokeAll(topLeft, topRight, bottomLeft, bottomRight);
                              

                              // Combine the results from all four quadrants
                              return topLeft.join() || topRight.join() || bottomLeft.join() || bottomRight.join();
                          }

          }

          void printGrid() {
		int i, j;
		// not border is not printed
		System.out.printf("Grid:\n");
		System.out.printf("+");
		for (j = 1; j < columns - 1; j++)
			System.out.printf("  --");
		System.out.printf("+\n");
		for (i = 1; i < rows - 1; i++) {
			System.out.printf("|");
			for (j = 1; j < columns - 1; j++) {
				if (grid[i][j] > 0)
					System.out.printf("%4d", grid[i][j]);
				else
					System.out.printf("    ");
			}
			System.out.printf("|\n");
		}
		System.out.printf("+");
		for (j = 1; j < columns - 1; j++)
			System.out.printf("  --");
		System.out.printf("+\n\n");
	}

      
          
}
