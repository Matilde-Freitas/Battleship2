package Client;

import java.util.Arrays;
import java.util.Scanner;

/**
 * ClientBoard class
 * 
 * A classe ClientBoard e utilizada para construir e atualizar o tabuleiro do
 * cliente, consoante as informacoes enviadas pelo servidor. Cada ClientBoard
 * tem um numero de linhas, colunas e navios
 * 
 * 
 */
public class ClientBoard {

	static Scanner scan = new Scanner(System.in);
	private char[][] board;
	private int col;
	private int row;
	private int numShips;
	char water = '.';
	char ship = 'S';
	char hit = 'X';
	char miss = 'o';
	String resultMiss = "MISS";
	String resultHit = "HIT";
	String resultAlreadyHit = "ALREADY_HIT";

	/**
	 * ClientBoard construtor
	 * 
	 * @param row      O numero de linhas do ClientBoard
	 * @param col      O numero de colunas do ClientBoard
	 * @param numShips O numero de navios do ClientBoard
	 * @return uma instancia da classe ClientBoard
	 */
	public ClientBoard(int row, int col, int numShips) {
		this.col = col;
		this.row = row;
		this.numShips = numShips;
		board = new char[row][col];
	}

	/**
	 * Obter o tabuleiro
	 * 
	 * @return O tabuleiro do ClientBoard
	 */
	public char[][] getBoard() {
		return board;
	}

	/**
	 * Muda o tabuleiro
	 * 
	 * @return O novo tabuleiro do ClientBoard
	 */
	public void setBoard(char[][] board) {
		this.board = board;
	}

	/**
	 * Obter o numero de colunas do ClientBoard
	 * 
	 * @return O numero de colunas do ClientBoard
	 */
	public int getCol() {
		return col;
	}

	/**
	 * Muda o numero de colunas do ClientBoard
	 * 
	 * @return O novo numero de colunas do ClientBoard
	 */
	public void setCol(int col) {
		this.col = col;
	}

	/**
	 * Obter o numero de linhas do ClientBoard
	 * 
	 * @return O numero de linhas do ClientBoard
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Muda o numero de linhas do ClientBoard
	 * 
	 * @return O novo numero de linhas do ClientBoard
	 */
	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * Obter o numero de navios do ClientBoard
	 * 
	 * @return O numero de navios do ClientBoard
	 */
	public int getNumShips() {
		return numShips;
	}

	/**
	 * Muda o numero de navios do ClientBoard
	 * 
	 * @return O novo numero de navios do ClientBoard
	 */
	public void setNumShips(int numShips) {
		this.numShips = numShips;
	}

	public void createClientBoard() {
		for (char[] row : this.board) // percorrendo todas as linhas(rows) do this.board
		{
			Arrays.fill(row, water); // Preencher as linhas do this.board com agua, representado por '.'
		}
	}

	public void printBoard() {
		System.out.print("    ");
		// Calibracao dos espacos da matriz para o alinhamento das colunas(N) e linhas
		// (N)

		for (int i = 0; i < this.getCol(); i++) // Impressao dos indices das colunas
		{

			if (i < 9) {
				System.out.print(i + 1 + "  "); // coloca um espaco adicional entre as linhas com indice menor que 9
			} else {
				System.out.print(i + 1 + " "); // coloca um espaco entre as colunas com indice maior que 9
			}
		}

		System.out.println();

		for (int row = 0; row < this.getRow(); row++) // percorre as linhas da matriz
		{
			// impressao dos indices das linhas
			if (row < 9) {
				System.out.print(row + 1 + "   ");
			}

			else {
				System.out.print(row + 1 + "  ");
			}

			for (int col = 0; col < this.getCol(); col++) {
				// impressao dos elementos da matriz tabuleiro
				char posicao = this.board[row][col];
				System.out.print(posicao + "  ");
			}

			System.out.println();
		}
	}

	public void updateBoard(String playerCoordinates, String shotResult) {

		// o tabuleiro e atualizado com o resultado do tiro do jogador

		int row = Integer.parseInt(playerCoordinates.split(";")[0]);
		int col = Integer.parseInt(playerCoordinates.split(";")[1]);

		row--;
		col--;

		if (shotResult.equals(resultHit)) {
			this.board[row][col] = hit; // se o jogador atingir um navio, o carater é substituido por um X
		}

		else if (shotResult.equals(resultMiss) || shotResult.equals(resultAlreadyHit)) {
			if (this.board[row][col] == hit) {
				this.board[row][col] = hit; // se o jogador atingir um navio ja atingido, o carater mantem-se
			}

			else {
				this.board[row][col] = miss; // se o jogador atingir agua ou um alvo ja atingido, o carater e subsituido
												// por um o
			}

		}
	}

	public void setFinalBoard(String shipLocations) {
		// Recebe as coordenadas das celulas correspondentes a navios e substitui-as
		// pelo respetivo carater

		for (String pair : shipLocations.split(";")) {

			int shipRow = Integer.parseInt(pair.split(":")[0]);
			int shipCol = Integer.parseInt(pair.split(":")[1]);

			for (int row = 0; row < this.getRow(); row++) {

				for (int col = 0; col < this.getCol(); col++) {

					if (row == shipRow && col == shipCol && this.board[row][col] != hit) {
						this.board[row][col] = ship;
					}
				}
			}
		}
	}

}