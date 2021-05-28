import java.util.Scanner;
import java.util.Random;

public class Minesweeper { 

    private static final int GAME_WON = 1;
    private static final int GAME_LOST = -1;
    private static final int GAME_NOTOVER = 0;

    private static boolean gameOver = false;
    
    // Add other static variables and constants you might need
    private static Cell[][] grid;
    private static int width;
    private static int height;
    private static int area;
    private static int numMines;
    
    //Spaces between 2 cells
    private static int spacing = 3;
    
    //Scanner
    private static Scanner scanner = new Scanner(System.in);
    
    //Special case for Invalid Coordinates
    private static int[] INVALID = {-1,-1};
    //Make sure you don't randomly die on the first pick
    private static boolean firstTry = true;
    
    //Check if (x,y) is in the grid
    public static boolean outOfBounds (int x, int y) {
    	if(0<=x && x<width) {
    		if (0<=y && y<height) {
    			return false;
    		}
    	}
    	return true;
    }
    /* 
     * Create the grid and place mines in random locations.
     *
     * @param rows     The number of rows in the Minesweeper grid
     * @param columns  The number of columns in the Minesweeper grid
     *
     * Tip: Create Minesweeper grid with 2 extra rows and 2 extra columns
     *      This will make it easy to go around the grid eliminating
     *      the need for ArrayOutOfBounds checking at the edges.
     */
    public static void initGrid() {
    	//Create Grid
    	grid = new Cell[width][height];
    	for(int x=0; x<width; x++) {
    		for(int y=0; y<height; y++) {
    			grid[x][y]=new Cell();
    		}
    	}
    }
    
    private static void initMines(int[] initTry) {
    	//Place down mines
    	disperseMines(numMines, initTry);
    	//Set Values of non-mines
    	for(int x=0; x<width; x++) {
    		for(int y=0; y<height; y++) {
    			if(!grid[x][y].isMine()) {
    				adjacentMines(x,y);
    			}
    		}
    	}
    }
    
    /*
     * Places mines in random locations in the grid.
     *
     * @param amountMines   The number of mines to be set in the grid.
     * @param x, y The square that should have 0 neighboring mines
     */
    private static void disperseMines(int amountMines, int[] initTry) {
    	Random rand = new Random();
    	int initTryX = initTry[0];
    	int initTryY = initTry[1];
    	//Use a for loop to create # of mines
    	for(int i=0; i<amountMines; i++) {
    		boolean foundMine=false;
    		//Make sure the mines don't overlap
    		while(!foundMine) {
    			int x=rand.nextInt(width);
    			int y=rand.nextInt(height);
    			//Relative X and Y
    			//Check if the mine is right next to or on the first try
    			//d = distance
    			int dX = initTryX-x;
    			int dY = initTryY-y;
    			//If the distance is -1<=d<=1 then it is neighboring 
    			if((dX<=1 && dX>=-1) && (dY<=1 && dY>=-1)) {
    				continue;
    			}
    			if(!grid[x][y].isMine()) {
    				grid[x][y].setMine();
    				foundMine=true;
    			}
    		}
    	}
    	
    }

    /*
     * Updates each cell with the number of adjacent cells with mines
     */
    public static void adjacentMines(int row, int column) {
    	int numMines = 0;
		for(int x=-1; x<=1; x++) {
			for(int y=-1; y<=1; y++) {
				//Checking if neighbor is outside of the grid
				if (outOfBounds(row+x,column+y)) {
				}
				//Checking all neighbors
				else if (grid[row+x][column+y].isMine()) {
					numMines += 1;
				}
			}
		}
		grid[row][column].setAdjacentMines(numMines);
		
    }
    //Repeating spaces to make formatting the printing easier
    private static String repeat(int count, String with) {
        return new String(new char[count]).replace("\0", with);
    }

    private static String repeat(int count) {
        return repeat(count, " ");
    }
    
    /*
     * Method to print Minesweeper grid
     */
    private static void printGrid() {
    	//Column Numbers\
    	System.out.print(repeat(spacing));
    	for(int i=0; i<grid.length; i++) {
    		String num = Integer.toString(i + 1);
    		System.out.print(num + repeat(spacing - num.length()));
    	}
    	System.out.println();
    	//Minefield
    	for (int row=0; row<grid.length; row++) {
    		//Row numbers
    		String rowNum = Integer.toString(row + 1);
    		System.out.print(rowNum + repeat(spacing - rowNum.length()));
    		//Printing cells
    		for (Cell cell : grid[row]) {
    			System.out.print(cell.getVal() + repeat(spacing - 1));
    		}
    		System.out.println();
    	}
    }

    /*
     * Method to reveal all the hidden cells. Prints grid after all cells
     * have been revealed.
     */
    public static void revealGrid() {
    	//Column Numbers
    	System.out.print(repeat(spacing));
    	for(int i=0; i<grid.length; i++) {
    		String num = Integer.toString(i + 1);
    		System.out.print(num + repeat(spacing - num.length()));
    	}
    	System.out.println();
    	//Minefield
    	for (int row=0; row<grid.length; row++) {
    		//Row numbers
    		String rowNum = Integer.toString(row + 1);
    		System.out.print(rowNum + repeat(spacing - rowNum.length()));
    		//Printing cells
    		for (Cell cell : grid[row]) {
    			if(cell.isMine()) {
    				System.out.print(Cell.MINE + repeat(spacing - 1));
    			}
    			else {
    				System.out.print(cell.getNumMines() + repeat(spacing - 1));
    			}
    		}
    		System.out.println();
    	}
    }

    public static void revealZeros (int row, int column) {
    	Cell cell = grid[row][column];
    	cell.reveal();
    	if(cell.getVal() != Cell.NO_ADJ_MINE_CELL) {
    		return;
    	}
    	for(int x=-1; x<=1; x++) {
			for(int y=-1; y<=1; y++) {
				//Checking if neighbor is outside of the grid
				if (outOfBounds(row+x,column+y)) {
				}
				//Checking all neighbors
				else if (!grid[row+x][column+y].isRevealed()){
					revealZeros(row+x, column+y);
				}
			}
		}
    }
    // Reveals the cell
    public static void revealCell(int row, int column) {
        /*
         * Handle user's cell selection specified by row and column 
         * There are three different cases:
         * 1. user chooses already explored cell - do nothing
         * 2. user chooses cell which has a mine - game lost
         * 3. user chooses a mine-free cell - reveal the cell
         * Print Minesweeper grid after handling user input
         *
         */
    	Cell cell = grid[row][column];
    	cell.reveal();
    	char val = cell.getVal();
    	switch(val){
    		case Cell.MINE:
    			gameOver = true;
    			return;
    		case Cell.NO_ADJ_MINE_CELL:
    			revealZeros(row, column);
    			return;
    		default:
    			return;
    	}
    }
    
    //Flags a Cell
    public static void flagCell(int row, int column) {
    	Cell cell = grid[row][column];
    	if(cell.isRevealed()) {
    		System.out.println("You already revealed that cell");
    	}
    	else {
    		cell.setFlag();
    	}
    }

    /*
     * Check if the game is over
     */
    public static boolean checkGameOver() {
    	//Death by mine
    	if(gameOver) {
    		System.out.println("Game Over, you stepped on a mine");
    		return true;
    	}
    	//Winning
    	int numRevealed = 0;
    	for(Cell[] row : grid) {
    		for(Cell cell : row) {
    			if(cell.isRevealed()) {
    				numRevealed += 1;
    			}
    		}
    	}
    	if(numRevealed == (area - numMines)) {
    		System.out.println("You've sucessfully cleared the minefield!");
    		return true;
    	}
		return false;
    }

    /* Restart the game */
    private static void restart() {
    	main(null);
    }
    
    public static void main(String[] args) {
    	//Width and Height
    	System.out.println("Width of Minefield: ");
        width = Integer.parseInt(scanner.nextLine().strip());
        System.out.println("Height of Minefield: ");
   	    height = Integer.parseInt(scanner.nextLine().strip());
   	    //Area
	    area = width*height;
	    //Difficulty
	    System.out.println("Difficulty (1-10): ");
	    int difficulty = Integer.parseInt(scanner.nextLine());
	    //Difficulty to number of mines is a linear line from 1/20 of the area to 1/2
	    numMines = (int) (area/2*((float)difficulty/10));
	    //Set Up Game
   	    initGrid();
   	    //Play the game
   	    boolean showGrid = true;
   	    while(!checkGameOver()) {
   	    	//playAction tells itself whenever to show the grid next time
   	    	showGrid=playAction(showGrid);
   	    }
   	    //Lose or win, reveal the grid
   	    revealGrid();
   	    //Choice to play again
   	    System.out.println("Play Again?");
   	    String playAgain = scanner.nextLine().toLowerCase().strip();
   	    if(playAgain.equals("y") || playAgain.equals("yes")) {
   	    	restart();
		}
   	    
    }
    /*
     * Each cycle of picking a square to try or flag
     * Reveal is "r" or "reveal"
     * Returns a boolean to show the grid next time
     */
    private static boolean playAction(final boolean showGrid) {
    	//Show the grid
		if(showGrid) {
			printGrid();
		}
		//Get Input
		System.out.println("What do you want to do, try or flag? (t or f) ");
		String toDo = scanner.nextLine().strip().toLowerCase();
		//Reveal by "r" or "reveal"
		if(toDo.equals("r") || toDo.equals("reveal")) {
			if(firstTry) {
				System.out.println("The map has not been made yet");
				return false;
			}
	   		revealGrid();
		   	System.out.println("Press enter to continue");
		   	scanner.nextLine();
		   	return true;
		}
		//Try
		if (toDo.equals("t") || toDo.equals("try")) {
			System.out.println("What square to you want to try? (in the format of row,column)");
			//Check Coordinates are valid
			int[] coords= checkCoordinates();
			if(coords==INVALID) {
				return false;
			}
			//Make sure you don't step on a mine first time
			if(firstTry) {
				initMines(coords);
				firstTry = false;
			}
			revealCell(coords[0],coords[1]);
		}
		//Flag
		else if (toDo.equals("f") || toDo.equals("flag")) {
			System.out.println("What square to you want to flag? (in the format of row,column)");
			//Check Coordinates are valid
			int[] coords= checkCoordinates();
			if(coords==INVALID) {
				return false;
			}
			flagCell(coords[0],coords[1]);
		}
	  		
		//Incorrect action
		else {
			System.out.println("Try using a correct answer : f, flag, t, try");
			return false;
		}
	  	return true;
    }
    private static int[] checkCoordinates() {
    	String[] coords=scanner.nextLine().strip().replaceAll(",", " ").split(" ");
    	
		//Incorrect number of coordinates
		if(coords.length!=2) {
			System.out.println("Try using 2 values, the column and the height, separated by a space");
			return INVALID;
		}
		//Finding chosen coordinates
		int x;
		int y;
		try {
			x = Integer.parseInt(coords[0]) - 1;
			y = Integer.parseInt(coords[1]) - 1;
		}
		//Not Integers
		catch(Exception e){
			System.out.println("Please type integers for values");
			return INVALID;
   		}
		//Coordinates out of bounds
  		if(outOfBounds(x,y)) {
  			System.out.println("Coordinates are out of bounds, please type:");
  			System.out.println("A column number less than or equal to " + width);
  			System.out.println("A row number less than or equal to " + height);
  			return INVALID;
  		}
  		int[] intCoords = {x,y};
  		return intCoords;
    }
}
