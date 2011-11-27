package com.unibe.happyemu;
import com.unibe.happyemu.VierGewinnt.Token;
/*	Exercise 4.1
 * 	authors:
 * 		Urs Gerber  	09-921-156
 * 		Lukas Keller  	10-113-736
 */

public class ComputerPlayer implements IPlayer
{
	// ¦1¦2¦3¦4¦3¦2¦1¦
	// ¦2¦3¦4¦5¦4¦3¦2¦
	// ¦3¦4¦5¦6¦5¦4¦3¦
	// ¦2¦3¦4¦5¦4¦3¦2¦
	// ¦1¦2¦3¦4¦3¦2¦1¦
	// ¦0¦1¦2¦3¦2¦1¦0¦
	
	// ¦0¦1¦2¦3¦2¦1¦0¦
	// ¦1¦2¦3¦4¦3¦2¦1¦
	// ¦2¦3¦4¦5¦4¦3¦2¦
	// ¦3¦4¦5¦6¦5¦4¦3¦
	// ¦2¦3¦4¦5¦4¦3¦2¦
	// ¦1¦2¦3¦4¦3¦2¦1¦
	
	// ¦1¦2¦3¦3¦3¦2¦1¦
	// ¦1¦2¦3¦4¦3¦2¦1¦
	// ¦3¦4¦5¦5¦5¦4¦3¦
	// ¦2¦3¦4¦5¦4¦3¦2¦
	// ¦2¦3¦4¦6¦4¦3¦2¦
	// ¦0¦2¦4¦8¦4¦1¦0¦

//	private static final int[][] quantifierMap = { { 0, 1, 2, 3, 2, 1 }, { 1, 2, 3, 4, 3, 2 }, { 2, 3, 4, 5, 4, 3 },
//			{ 3, 4, 5, 6, 5, 4 }, { 2, 3, 4, 5, 4, 3 }, { 1, 2, 3, 4, 3, 2 }, { 0, 1, 2, 3, 2, 1 } };
	private static final int[][] quantifierMap = { { 1, 2, 3, 2, 1,0 }, { 2, 3, 4, 3, 2,1 }, { 3, 4, 5, 4, 3,2 },
		{ 4, 5, 6, 5, 4,3 }, { 3, 4, 5, 4, 3,2 }, { 2, 3, 4, 3, 2,1 }, { 1, 2, 3, 2, 1,0 } };
//	private static final int[][] quantifierMap = { { 0, 2, 2, 3, 1,1 }, { 2, 3, 3, 4, 2,2 }, { 4, 4, 4, 5, 3,3 },
//		{ 8, 6, 5, 5, 4,3 },{ 4, 4, 4, 5, 3,3 }, { 2, 3, 3, 4, 2,2 }, { 0, 2, 2, 3, 1,1 } };
	private static final int[] sortedColumns = { 3, 2, 4, 1, 5, 0, 6};
	
	private static final int MAX_DEPTH = 42;
	private static final int INITIAL_DEPTH = 10;
	private static final int SAFETY_TIME = 20;
	private static final int TIMEOUT_TIME = 10000;
	
	private static final int WINNING_SCORE = 1000;
	private static final int UNDEF_SCORE = 1000000;
	
	private int evals, betacuts = 0;
	private boolean timeout = false;

	private Token token;

	public String getProgrammers()
	{
		return "Urs Gerber & Lukas Keller";
	}

	public Token getToken()
	{
		return this.token;
	}

	public int getNextColumn(Token[][] board)
	{		
		int tmpMaxDepth = INITIAL_DEPTH;
		int tmpBestMove = -1;
		this.timeout = false;
		System.out.println("\n"+VierGewinnt.displayBoard(board));
		Board boardCopy = new Board(board);
		long startTime = System.currentTimeMillis();
		
		try
		{
			while (!timeout && tmpMaxDepth < ComputerPlayer.MAX_DEPTH)
			{
				tmpBestMove = getBestMove(boardCopy, this.token, tmpMaxDepth, startTime);
				tmpMaxDepth++;
			}
		} 
		catch (TimeoutException e)
		{
			System.out.print(e.getMessage());
		}
		
		return tmpBestMove;
	}

	private int getBestMove(Board board, Token player, int depth, long time) throws TimeoutException
	{
		// The starting move is illegal, so we will know when algorithm has failed
		// This is a wanted behaviour!
		int bestMove = -1;
		int bestEval = -UNDEF_SCORE;

		for (int col = 0; col < Board.COLS; col++)
		{
			if (board.isLegalMove(col))
			{
				int row = board.makeMove(col, player);
				int eval = -negamax(board, enemy(player), -UNDEF_SCORE, UNDEF_SCORE, depth);
				board.undoMove(col, row);
				
				if (eval > bestEval)
				{
					bestEval = eval;
					bestMove = col;
				}
			}
			if (System.currentTimeMillis() - time >= ComputerPlayer.TIMEOUT_TIME - ComputerPlayer.SAFETY_TIME)
				throw new TimeoutException();
		}
		
		System.out.println(String.format("@Depth %d: Evals: %d, Score: %d, Cuts: %d, BestMove: %d", depth, evals, bestEval, betacuts, bestMove+1));
		
		if (bestEval >= WINNING_SCORE)
			System.out.println("I'm going to win!");
		else if (bestEval <= -WINNING_SCORE)
			System.out.println("I'm going to lose!");
		
		return bestMove;
	}

	private int evaluateBoard(Board board, Token currentPlayer)
	{
		// Evaluation MUST be symmetrical aka eval(player1) = -eval(player2)
		// otherwise negamax will fail!
		int score = 0;
		
		for (int i = 0; i < Board.COLS; i++)
		{
			for (int j = 0; j < Board.ROWS; j++)
			{
				Token testToken = board.getTokenAt(i, j);
				if (testToken == currentPlayer)
					score += ComputerPlayer.quantifierMap[i][j];
				else if (testToken == enemy(currentPlayer))
					score -= ComputerPlayer.quantifierMap[i][j];
			}
		}
		
		return score;
	}

	private int negamax(Board board, Token currentPlayer, int alpha, int beta, int depth) 
	{
		evals++;
		
		int eval = -UNDEF_SCORE;
		
		Token winner = board.getWinner();
		if (winner != Token.empty)
		{
			// Someone won
			if (winner == currentPlayer)
				return WINNING_SCORE;
			else if (winner == enemy(currentPlayer))
				return -WINNING_SCORE;
		}
		
		// Remis, noone won
		if (board.isFull())
			return 0;
		// maximum depth was reached
		if (depth <= 0)
			return this.evaluateBoard(board, currentPlayer);

		// Iterate over all possible moves
		for (int col : sortedColumns)
		{
			if (board.isLegalMove(col))
			{
				// Make move
				int row = board.makeMove(col, currentPlayer);
				// Evaluate next move
				eval = -negamax(board, enemy(currentPlayer), -beta, -alpha, depth - 1);
				// Undo previous move
				board.undoMove(col, row);
				// Pruning
				if (eval >= beta)
				{
					betacuts++;
					return beta;
				}
				if (eval > alpha)
					alpha = eval;
			}
		}
		return alpha;
	}

	public void setToken(Token token)
	{
		this.token = token;
	}

	public Token enemy(Token player)
	{
		if (player == Token.player1)
			return Token.player2;
		if (player == Token.player2)
			return Token.player1;
		return Token.empty;
	}
	
	private class TimeoutException extends Exception
	{
		private static final long serialVersionUID = -5107743283582197083L;

		public TimeoutException()
		{
			super("Computation timeout occured!");
		}
	}
	
	private class BestMove
	{
		private int column = -1;
		public void set(int c)
		{
			this.column = c;
		}
	}
}
