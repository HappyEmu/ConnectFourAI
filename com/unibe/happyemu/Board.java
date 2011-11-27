package com.unibe.happyemu;
import com.unibe.happyemu.VierGewinnt.Token;
/*	Exercise 4.1
 * 	authors:
 * 		Urs Gerber  	09-921-156
 * 		Lukas Keller  	10-113-736
 * 
 */

public class Board
{
	public static final int COLS = VierGewinnt.COLS;
	public static final int ROWS = VierGewinnt.ROWS;
	
	private Token[][] board = new Token[COLS][ROWS]; // 7 columns with 6 fields each
	
	public int[] lastMove = new int[2];
	public Token lastToken;
	
	public Board(Token[][] board)
	{
		for (int i = 0; i < Board.COLS; i++)
		{
			 for (int j = 0; j < Board.ROWS; j++)
			 {
				 this.board[i][j] = board[i][j];
			 }
		}
	}
	
	public Board()
	{
		this.board = new Token[Board.COLS][Board.ROWS];
		this.lastMove = new int[2];
		this.lastToken = Token.empty;
	}
	
	public Board(Board board)
	{
		for (int i = 0; i < Board.COLS; i++)
		{
			 for (int j = 0; j < Board.ROWS; j++)
			 {
				 this.board[i][j] = board.board[i][j];
			 }
		}
		this.lastMove[0] = board.lastMove[0];
		this.lastMove[1] = board.lastMove[1];
		this.lastToken = board.lastToken;
	}
	
	public Board clone()
	{
		return new Board(this);
	}
	
	public Token getTokenAt(int col, int row)
	{
		return this.board[col][row];
	}
	
	public int makeMove(int column, Token player)
	{
		return insertToken(column, player);
	}
	
	public void undoMove(int col, int row)
	{
		this.board[col][row] = Token.empty;
		this.lastMove[0] = -1; this.lastMove[1] = -1;
	}
	
	private int insertToken(int column, Token tok) {
		
		if (columnIsFull(column))
			return -1;
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
		
		this.lastMove[0] = column;
		this.lastMove[1] = finalRow;
		this.lastToken = this.board[column][finalRow];
				
		return finalRow;
	}
	
	public boolean isLegalMove(int column)
	{
		return !this.columnIsFull(column);
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int r = Board.ROWS - 1; r >= 0; r--)
		{
			sb.append("|");
			for (int c = 0; c < Board.COLS; c++)
			{
				sb.append(this.board[c][r].toString() + "|");
			}
			sb.append("\n");
		}		
		
		return sb.toString();
	}
	
	private boolean columnIsFull(int column)
	{
		if (this.board[column][Board.ROWS - 1] == Token.empty)
			return false;
		return true;
	}
	
	public boolean isFull() {
		for (int i = 0; i < COLS; i++)
		{
			if (this.board[i][Board.ROWS - 1] == Token.empty)
				return false;
		}
		return true;
	}
	
	public Token getLastPlayer()
	{
		return this.lastToken;
	}
	
	public int[] getLastMove()
	{
		int[] lastMove = new int[2];
		lastMove = this.lastMove.clone();
		return lastMove;
	}
	
	public Token getWinner()
	{
	    int x1, y1, x2, y2 = 0;
	    Token player = this.lastToken;
	    int lastCol = this.lastMove[0];
	    int lastRow = this.lastMove[1];
	    
	    // Horizontal
	    x1 = x2 = lastCol;
	    //Go right
	    while (x1 < Board.COLS && board[x1][lastRow] == player) x1++;
	    //Go left
	    while (x2 >= 0 && board[x2][lastRow] == player) x2--;
	 
	    if (x1 - x2 > 4)
	        return player;

	    // Vertical
	    y1 = y2 = lastRow;
	    //Go up
	    while (y1 < Board.ROWS && board[lastCol][y1] == player) y1++;
	    //Go down
	    while (y2 >= 0 && board[lastCol][y2]==player) y2--;
	    //Do we have a win here?
	    if (y1 - y2 > 4)
	        return player;

	    // Diagonal negative slope
	    x1 = x2 = lastCol;
	    y1 = y2 = lastRow;
	    //Go down and right
	    while (y1 >= 0 && x1 < Board.COLS && board[x1][y1] == player) { x1++; y1--; }
	    //Go up and left
	    while (y2 < Board.ROWS && x2 >= 0 && board[x2][y2] == player) { x2--; y2++; }
	    
	    if (x1 - x2 > 4)
	        return player;

	    // Diagonal positive slope
	    x1 = x2 = lastCol;
	    y1 = y2 = lastRow;
	    //Go down and left
	    while (y1 >= 0 && x1 >= 0 && board[x1][y1] == player) { x1--; y1--; }
	    //Go up and right
	    while (y2 < Board.ROWS && x2 < Board.COLS && board[x2][y2] == player) {x2++; y2++; }
	    
	    if (x2 - x1 > 4)
	        return player;

	    return Token.empty;
	}
}
