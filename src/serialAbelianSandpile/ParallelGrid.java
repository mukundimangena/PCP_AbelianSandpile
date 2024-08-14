package serialAbelianSandpile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.RecursiveTask;
import javax.imageio.ImageIO;

public class ParallelGrid extends RecursiveTask<Boolean> {
    private int rows, columns;
    private int[][] grid; // grid
    private int[][] updateGrid; // grid for next time step
    public static final int THRESHOLD = 400;
    // int lo, hi;

    public ParallelGrid(int w, int h) {
        rows = w + 2; // for the "sink" border
        columns = h + 2; // for the "sink" border
        grid = new int[this.rows][this.columns];
        updateGrid = new int[this.rows][this.columns];
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                grid[i][j] = 0;
                updateGrid[i][j] = 0;
            }
        }
    }

    public ParallelGrid(int[][] newGrid) {
        this(newGrid.length, newGrid[0].length); // call constructor above
        for (int i = 1; i < rows - 1; i++) {
            for (int j = 1; j < columns - 1; j++) {
                this.grid[i][j] = newGrid[i - 1][j - 1];
            }
        }
    }

    public ParallelGrid(ParallelGrid copyGrid) {
        this(copyGrid.rows, copyGrid.columns); // call constructor above
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                this.grid[i][j] = copyGrid.get(i, j);
            }
        }
    }

    public ParallelGrid(int[][] oldGrid, int startRow, int endRow, int startCol, int endCol) {
        rows = endRow - startRow + 2; // Include the borders
        columns = endCol - startCol + 2;
        grid = new int[rows][columns];
        updateGrid = new int[rows][columns];

        for (int i = 0; i < rows - 2; i++) {
            for (int j = 0; j < columns - 2; j++) {
                grid[i + 1][j + 1] = oldGrid[startRow + i][startCol + j];
            }
        }
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
        for (int i = 1; i < rows - 1; i++) {
            for (int j = 1; j < columns - 1; j++)
                grid[i][j] = value;
        }
    }

    public void nextTimeStep() {
        for (int i = 1; i < rows - 1; i++) {
            for (int j = 1; j < columns - 1; j++) {
                this.grid[i][j] = updateGrid[i][j];
            }
        }
    }

    boolean update() {
		boolean change = false;

		for (int i = 1; i < rows - 1; i++) {
			for (int j = 1; j < columns - 1; j++) {

				updateGrid[i][j] = (grid[i][j] % 4) +
						(grid[i - 1][j] / 4) +
						grid[i + 1][j] / 4 +
						grid[i][j - 1] / 4 +
						grid[i][j + 1] / 4;

				if (grid[i][j] != updateGrid[i][j]) {
					change = true;
				}
			}
		}
		if (change) {
			nextTimeStep();
		}
		return change;
	}

    @Override
    protected Boolean compute() {
        
        if (getRows() * getColumns() < THRESHOLD) {
            return update();
        } else {
            int midRow = (rows - 2) / 2 + 1;
            int midCol = (columns - 2) / 2 + 1;

            ParallelGrid tpL = new ParallelGrid(grid, 1, midRow, 1, midCol);
            ParallelGrid tpR = new ParallelGrid(grid, 1, midRow, midCol, columns - 1);
            ParallelGrid bmL = new ParallelGrid(grid, midRow, rows - 1, 1, midCol);
            ParallelGrid bmR = new ParallelGrid(grid, midRow, rows - 1, midCol, columns - 1);

            tpL.fork();
            tpR.fork();
            bmL.fork();

            boolean bl = bmR.compute();
            boolean tl = tpL.join();
            boolean tr = tpR.join();
            boolean br = bmL.join();

            return bl || tl || tr || br;
        }
    }

	// display the grid in text format
	void printGrid() {
		int i, j;
		// not border is not printed
		//System.out.printf("Grid:\n");
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

	// write grid out as an image
	void gridToImage(String fileName) throws IOException {
		System.out.printf("Rows: %d, Columns: %d\n", rows, columns);
		BufferedImage dstImage = new BufferedImage(rows, columns, BufferedImage.TYPE_INT_ARGB);
		// integer values from 0 to 255.
		int a = 0;
		int g = 0;// green
		int b = 0;// blue
		int r = 0;// red
		

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				g = 0;// green
				b = 0;// blue
				r = 0;// red

				switch (grid[i][j]) {
					case 0:
						break;
					case 1:
						g = 255;
						break;
					case 2:
						b = 255;
						break;
					case 3:
						r = 255;
						break;
					default:
						break;

				}
				// Set destination pixel to mean
				// Re-assemble destination pixel.
				int dpixel = (0xff000000)
						| (a << 24)
						| (r << 16)
						| (g << 8)
						| b;
				dstImage.setRGB(i, j, dpixel); // write it out

			}
		}

		File dstFile = new File(fileName);
		ImageIO.write(dstImage, "png", dstFile);
	}

}