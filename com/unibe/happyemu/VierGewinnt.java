package com.unibe.happyemu;
/*	Exercise 4.1
 * 	authors:
 * 		Urs Gerber  	09-921-156
 * 		Lukas Keller  	10-113-736
 * 
 */

/* Programmierung 1 HS 2011 Aufgabe 4-1 */

import java.util.Arrays;
import java.util.Scanner;

public class VierGewinnt {
	
	public static final int COLS = 7;
	public static final int ROWS = 6;
	
	// Enum for the different tokens (empty, O, X)
	public enum Token { 
		empty(" "),
		player1("O"),
		player2("X");
		private String representation; // string representation of value
		Token(String s) { this.representation = s; }
		public String toString() { return this.representation; }
	}
	
	private Token[][] board = new Token[COLS][ROWS]; // 7 columns with 6 fields each
	private IPlayer[] players = new IPlayer[2]; // two players
	
	public void play() {
		// initialize the board
		for (Token[] column : this.board) {
			Arrays.fill(column, Token.empty);
		}
		// initialize players
		players[0] = new ComputerPlayer();
		System.out.print("Play against a human opponent? (y / n) ");
		String opponent = new Scanner(System.in).nextLine();
		if(opponent.toLowerCase().equals("y")){
			players[1] = new HumanPlayer();
		}else{
			players[1] = new ComputerPlayer();
		}
		players[0].setToken(Token.player1);
		players[1].setToken(Token.player2);
		// play...
		boolean solved = false;
		int currentPlayer = (new java.util.Random()).nextInt(2);  //choose randomly who begins
		System.out.println("current player: "+currentPlayer);
		int insertCol, insertRow; // starting from 0
		while (!solved && !this.isBoardFull()){
			// get player's next "move"
			insertCol = players[currentPlayer].getNextColumn(this.board);
			// insert the token and get the row where it landed
			insertRow = this.insertToken(insertCol, players[currentPlayer].getToken());
			// check if the game is over
			solved = this.checkVierGewinnt(insertCol, insertRow);
			//switch to other player
			if (!solved) currentPlayer = (currentPlayer+1) % 2;
		}
		System.out.println(displayBoard(this.board));
		if (solved) System.out.println("Player "+players[currentPlayer].getToken() + " wins!");
		else System.out.println("Draw! Game over.");
	}
	
	private int insertToken(int column, Token tok) {
		
		if (column < 0 || column > COLS - 1 || columnIsFull(column))
			System.exit(1);
		
		int finalRow = 0;
		for (int row = 0; row < ROWS; row++)
		{
			finalRow = row;
			if (this.board[column][row] == Token.empty)
			{
				this.board[column][row] = tok;
				break;
			}
		}
		return finalRow;
	}
	
	private boolean columnIsFull(int column)
	{
		
		if (this.board[column][VierGewinnt.ROWS - 1] == Token.empty)
			return false;
		return true;
	}
	
	private boolean isBoardFull() {
		for (int c = 0; c < COLS; c++)
		{
			if (this.board[c][VierGewinnt.ROWS - 1] == Token.empty)
				return false;
		}
		return true;
	}

	private boolean checkVierGewinnt(int col, int row)
	{
		int x1, y1, x2, y2 = 0;
	    Token player = board[col][row];
	    int lastCol = col;
	    int lastRow = row;
	    
	    // Horizontal
	    x1 = x2 = lastCol;
	    //Go right
	    while (x1 < Board.COLS && board[x1][lastRow] == player) x1++;
	    //Go left
	    while (x2 >= 0 && board[x2][lastRow] == player) x2--;
	 
	    if (x1 - x2 > 4)
	        return true;

	    // Vertical
	    y1 = y2 = lastRow;
	    //Go up
	    while (y1 < Board.ROWS && board[lastCol][y1] == player) y1++;
	    //Go down
	    while (y2 >= 0 && board[lastCol][y2]==player) y2--;
	    //Do we have a win here?
	    if (y1 - y2 > 4)
	        return true;

	    // Diagonal negative slope
	    x1 = x2 = lastCol;
	    y1 = y2 = lastRow;
	    //Go down and right
	    while (y1 >= 0 && x1 < Board.COLS && board[x1][y1] == player) { x1++; y1--; }
	    //Go up and left
	    while (y2 < Board.ROWS && x2 >= 0 && board[x2][y2] == player) { x2--; y2++; }
	    
	    if (x1 - x2 > 4)
	        return true;

	    // Diagonal positive slope
	    x1 = x2 = lastCol;
	    y1 = y2 = lastRow;
	    //Go down and left
	    while (y1 >= 0 && x1 >= 0 && board[x1][y1] == player) { x1--; y1--; }
	    //Go up and right
	    while (y2 < Board.ROWS && x2 < Board.COLS && board[x2][y2] == player) { x2++; y2++; }
	    
	    if (x2 - x1 > 4)
	        return true;

	    return false;
	}
	
	public static String displayBoard(Token[][] myBoard){
		String rowDelimiter = "+";
		String rowNumbering = " ";
		for(int col=0; col<myBoard.length; col++){
			rowDelimiter += "---+";
			rowNumbering += " "+(col+1)+"  ";
		}
		rowDelimiter += "\n";
		
		String rowStr;
		String presentation = rowDelimiter;
		for(int row=myBoard[0].length-1; row >= 0; row--){
			rowStr = "| ";
			for(int col=0; col < myBoard.length; col++){
				rowStr += myBoard[col][row].toString() + " | ";
			}
			presentation += rowStr + "\n" + rowDelimiter;
		}
		presentation += rowNumbering;
		return presentation;
	}

	public static void main(String args[]){
		VierGewinnt game = new VierGewinnt();
		game.play();
	}
}
