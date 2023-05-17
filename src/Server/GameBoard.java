package Server;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class GameBoard {
	
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

	public GameBoard(int row, int col, int numShips) {
        this.col = col;
        this.row = row;
        this.numShips = numShips;
        board = new char[row][col];
    }
	
    public char[][] getBoard() {
		return board;
	}

	public void setBoard(char[][] board) {
		this.board = board;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
		
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getNumShips() {
		return numShips;
	}

	public void setNumShips(int numShips) {
		this.numShips = numShips;
	}
	
	public static int checkBoardSettings(int numS, int row, int col, String command)
	{
		int min = 0;
		int max = 0;
		
		if (command.equals("linhas") || command.equals("colunas"))
		{
			min = 15;
			max = 30;
		}
		
		else if (command.equals("navios"))
		{
			max = (row * col) / 4; // o numero maximo de navios que poderao ser colocados e dada pela express
			// (N*M)/4, de forma a garantir que as celulas dos navios nao excedam o tamanho
			// do tabuleiro

			min = 3;
		}
		
		else
		{
			max = row*col; // o objeto inteiro max_shots e igual ao numero de elementos da matriz tabuleiro
			min = numS; // e criado o objeto inteiro min_shots que e igual ao numare de celulas ocupadas pelos
			// navios
		}
		
		//System.out.println();
		int value = 0; // criacao do objeto inteiro row (linhas) que se inicia em 0
		boolean val = true;

		while (val) // enquanto val_row for verdadeiro o sistema ira correr as instrucoes dentro do ciclo while
		{
			//System.out.print("Numero de "+command+" (entre "+min+" e "+max+"): ");

			if (scan.hasNextInt()) // verifica se o jogador inseriu um numero inteiro
			{
				value = scan.nextInt(); // regista o numero inteiro inserido pelo jogador
				//scan.nextLine();

				if (value >= min && value <= max) // verifica que o numero inteiro inserido esta entre os limites definidos
				{
					val = false; // val_row passa a ser falso e o ciclo while termina
				}

				else // caso o jogador inserir um numero inteiro fora dos limites definidos
				{
					System.out.println("Dimensoes fora dos limites");
					System.out.println();
					scan.nextLine(); // limpeza do scanner
					// val_row continua igual a true, ciclo repete-se até jogador inserir um numero inteiro valido
				}
			}

			else // caso o jogador introduza um carater ou outro formato alem de numero inteiro
			{
				System.out.println("Formato invalido");
				System.out.println();
				scan.nextLine(); // limpeza do scanner
				// val_row continua igual a true, ciclo repete-se até jogador inserir um numero inteiro valido
			}

		}
		return value; // e devolvido row , o numero de linhas validado
	}
	
	public void creatClientBoard()
	{
		for (char[] row : this.board) // percorrendo todas as linhas(rows) do this.board
		{
			Arrays.fill(row, water); // Preencher as linhas do this.board com agua, representado por '.'
		}
	}
	
	public void createServerBoard()
	{
		int ship_type; // Criacao do objeto inteiro tipo_navios
		int orientation; // criacao do objeto inteiro orientacao
		int[] spot = new int[2]; // criacao de um vetor de numeros inteiro do tipo 1*2
		int attempt = 0; // criacao do objeto inteiro tentativa
		int placedShips = 0; // criacao do objeto inteiro navios_colocados

		char possibleSpot; // criacao do objeto tipo carater possivel_loca ( para a celula 1 do navio)
		char possibleSpot1;
		char possibleSpot2;
		char possibleSpot3;
		char possibleSpot4;

		// 0 - tipo S ; 1 - tipo SS ; 2 - tipo SSSS
		// 0 - norte ; 1 - sul ; 2 - este ; 3 - oeste

		for (char[] row : this.board) // percorrendo todas as linhas(rows) do this.board
		{
			Arrays.fill(row, water); // Preencher as linhas do this.board com agua, representado por '.'
		}

		while (placedShips < this.getNumShips() && attempt <= 100) // enquanto a contagem de navios for inferior ao numero de
																// navios colocados pelo utilizador e inferior ou igual
																// a 100 tentativas
		{
			attempt++; // somar 1 a contagem de tentativas

			if (placedShips == 0) // o primeiro navio colocado vai ser do tipo 0
			{
				ship_type = 0;
			}

			else if (placedShips == 1) // o segundo navio colocado vai ser do tipo 1
			{
				ship_type = 1;
			}

			else if (placedShips == 2) // o terceiro navio colocado vai ser do tipo 2
			{
				ship_type = 2;
			}

			// Desta forma asseguramos que ha um navio de cada tipo

			else // o resto dos navios vao ser de tipo aleatorio
			{
				ship_type = new Random().nextInt(3);
			}

			if (ship_type == 0) // Caso o navio seja do tipo 0 (S)
			{
				orientation = 0; // A orientacao nao tera influencia uma vez que este tipo de barco e constituido apenas
								// por um elemento
				spot = generateCoordinates(ship_type, orientation, this.getRow(), this.getCol()); // atraves da funcao gerarCoordenadas2, obtemos
																			// um vetor [c1 c2] que nos indicara a
																			// localizacao do nosso possivel_lugar
				possibleSpot = this.board[spot[0]][spot[1]]; // o possivel_local e colocado sobre a matriz this.board

				if (possibleSpot == water) // caso este possivel_local na matriz esteja ocupado por agua
				{
					this.board[spot[0]][spot[1]] = ship; // passa a ser substituido na matriz por um objeto navio
					placedShips++; // aumenta em 1 a contagem dos navios_colocados
				}
			}

			else if (ship_type == 1) // Caso o navio seja do tipo 1 (SS)
			{
				orientation = new Random().nextInt(4); // Escolhemos aleatoriamente 1 das 4 possibilidades de orientacao
				spot = generateCoordinates(ship_type, orientation, this.getRow(), this.getCol()); // Selecionado o tipo de navio e orientacao foi
																			// gerada a primeira das suas coordenadas,
																			// recorrendo a funcao gerarCoordenadas2
																			// iremos obter rum vetor [c1 c2] que nos
																			// dara a posicao do possivel_local para a
																			// uma celula do navio

				if (orientation == 0) // se a orientacao for 0 (norte)
				{
					possibleSpot1 = this.board[spot[0]][spot[1]]; // A primeira celula e colocada no local
																		// correspondente as coordenadas
					possibleSpot2 = this.board[spot[0] - 1][spot[1]]; // a segunda celula do navio e colocada na
																			// mesma coluna e na linha acima

					if (possibleSpot1 == water && possibleSpot2 == water) // se ambas as possicoes forem ocupadas
																				// por agua
					{
						this.board[spot[0]][spot[1]] = ship; // entao passam ambas a ser ocupadas por navios
						this.board[spot[0] - 1][spot[1]] = ship;
						placedShips++;
					}
				}

				else if (orientation == 1) // se a orientacao for a orientacao 2 (sul)
				{
					possibleSpot1 = this.board[spot[0]][spot[1]]; // a primeira celula e colocada nas coordenas
																		// geradas
					possibleSpot2 = this.board[spot[0] + 1][spot[1]]; // a segunda celula e colocada na mesma coluna
																			// na linha abaixo

					if (possibleSpot1 == water && possibleSpot2 == water)
					{
						this.board[spot[0]][spot[1]] = ship;
						this.board[spot[0] + 1][spot[1]] = ship;
						placedShips++;
					}
				}

				else if (orientation == 2) // quando e escolhida a orientacao 2 (este)
				{
					possibleSpot1 = this.board[spot[0]][spot[1]]; // a primeira celula e colocada no this.board
																		// atraves das coordenadas geradas
					possibleSpot2 = this.board[spot[0]][spot[1] + 1]; // a segunda e colocada na mesma linha na
																			// primeira coluna a direita da primeira
																			// celula

					if (possibleSpot1 == water && possibleSpot2 == water)
					{
						this.board[spot[0]][spot[1]] = ship;
						this.board[spot[0]][spot[1] + 1] = ship;
						placedShips++;
					}
				}

				else // se nenhuma das outras posicoes for escolhida entao passamos para a posicao 4 (oeste)
				{
					possibleSpot1 = this.board[spot[0]][spot[1]]; // a primeira celula e colocada segunda as
																		// coordenadas do vetor [c1 c2]
					possibleSpot2 = this.board[spot[0]][spot[1] - 1]; // a seguinte e colocada na mesma linha e na
																			// primeira coluna a esquerda de primeira
																			// celula

					if (possibleSpot1 == water && possibleSpot2 == water)
					{
						this.board[spot[0]][spot[1]] = ship;
						this.board[spot[0]][spot[1] - 1] = ship;
						placedShips++;
					}
				}
			}

			else if (ship_type == 2) // se o navio escolhido for o navio tipo 2 (SSSS)
			{
				orientation = new Random().nextInt(4); // escolhemos de modo aleatorio 1 das 4 orientacoes
				spot = generateCoordinates(ship_type, orientation, this.getRow(), this.getCol());// atraves do gerarCoordenadas geramos a posicao
																		// da primeira celula

				if (orientation == 0) // norte
				{
					possibleSpot1 = this.board[spot[0]][spot[1]];// a primeira celula e colocda nas coordenadas
																	// geradas pelo gerarCoordenadas
					possibleSpot2 = this.board[spot[0] - 1][spot[1]];// a segunda e colocada nana mesma coluna mas na
																		// linha acima da primeira
					possibleSpot3 = this.board[spot[0] - 2][spot[1]];// a terceira e colocada na mesma linha mas na
																		// segunda linha acima da primeira celula
					possibleSpot4 = this.board[spot[0] - 3][spot[1]];// a quarta e colocada novamente na mesma coluna
																		// mas na terceira coluna acima da primeira

					if (possibleSpot1 == water && possibleSpot2 == water && possibleSpot3 == water
							&& possibleSpot4 == water)
					{
						this.board[spot[0]][spot[1]] = ship;
						this.board[spot[0] - 1][spot[1]] = ship;
						this.board[spot[0] - 2][spot[1]] = ship;
						this.board[spot[0] - 3][spot[1]] = ship;
						placedShips++;
					}
				}

				else if (orientation == 1) // sul
				{
					possibleSpot1 = this.board[spot[0]][spot[1]];// a primeira celula do navio e colocada nas
																	// coordenadas que ja foram geradas
					possibleSpot2 = this.board[spot[0] + 1][spot[1]];// a segunda e colocada na mesma coluna e na
																		// primeira linha abaixo da primeira celula
					possibleSpot3 = this.board[spot[0] + 2][spot[1]];// a terceira e colocada na mesma coluna e na
																		// segunda linha a baixo da primeira celula
					possibleSpot4 = this.board[spot[0] + 3][spot[1]];// a quarta e colocada na mesma coluna e na
																		// terceira linha a baixo da primeira celula

					if (possibleSpot1 == water && possibleSpot2 == water && possibleSpot3 == water
							&& possibleSpot4 == water)
					{
						this.board[spot[0]][spot[1]] = ship;
						this.board[spot[0] + 1][spot[1]] = ship;
						this.board[spot[0] + 2][spot[1]] = ship;
						this.board[spot[0] + 3][spot[1]] = ship;
						placedShips++;
					}
				}

				else if (orientation == 2) // este
				{
					possibleSpot1 = this.board[spot[0]][spot[1]];// a primeira celula e colocada na matriz segundo as
																	// coordenadas ja geradas
					possibleSpot2 = this.board[spot[0]][spot[1] + 1];// a segunda e colocada na mesma linha e na
																		// primeira coluna a direita da primeira celula
					possibleSpot3 = this.board[spot[0]][spot[1] + 2];// a terceira e colocada na mesma linha e na
																		// segunda coluna a direia da primeira celula
					possibleSpot4 = this.board[spot[0]][spot[1] + 3];// a quarta e colocada na mesma linha e na
																		// terceira coluna a direita da primeira celula

					if (possibleSpot1 == water && possibleSpot2 == water && possibleSpot3 == water
							&& possibleSpot4 == water)
					{
						this.board[spot[0]][spot[1]] = ship;
						this.board[spot[0]][spot[1] + 1] = ship;
						this.board[spot[0]][spot[1] + 2] = ship;
						this.board[spot[0]][spot[1] + 3] = ship;
						placedShips++;
					}
				}

				else // oeste
				{
					possibleSpot1 = this.board[spot[0]][spot[1]];// a primeira celula e colocada na posicao das
																	// coordenadas geradas
					possibleSpot2 = this.board[spot[0]][spot[1] - 1];// a segunda celula e colocada na mesma linha e
																		// na primeira coluna a esquerda da primeira
																		// celula
					possibleSpot3 = this.board[spot[0]][spot[1] - 2];// a segunda e colocada na mesma linha e na
																		// segunda coluna a esquerda da primeira celula
					possibleSpot4 = this.board[spot[0]][spot[1] - 3];// a terceira e colocada n mesma linha e na
																		// terceira coluna a esquerda da primeira celula

					if (possibleSpot1 == water && possibleSpot2 == water && possibleSpot3 == water
							&& possibleSpot4 == water)
					{
						this.board[spot[0]][spot[1]] = ship;
						this.board[spot[0]][spot[1] - 1] = ship;
						this.board[spot[0]][spot[1] - 2] = ship;
						this.board[spot[0]][spot[1] - 3] = ship;
						placedShips++;
					}
				}
			}
		}

		if (attempt > 100) // se as tentativas de colocar os navios todos exceder as 100 tentativas
		{
			this.board = new char[2][2]; // gera uma nova matriz this.board 2x2
		}

	}
	
	public static int[] generateCoordinates(int ship_type, int orientation, int row, int col)
	{
		// Atraves desta funcao o utilizador ira gerar um vetor de duas coordenadas onde serao colocados os navios de
		// acordo com o tipo e a orientacao

		// 0 - tipo S ; 1 - tipo SS ; 2 - tipo SSSS
		// 0 - norte ; 1 - sul ; 2 - este ; 3 - oeste

		int[] ship_coordinates = new int[2]; // As coordenadas do navio serao apresentadas em forma de um vetor 1*2
		int c1 = 0; // c1 vai ser o primeiro elemento do vetor coordenadas_navio, correspondente à linha
		int c2 = 0; // c2 vai ser o segundo elemento do vetor coordenadas_navio, correspondente à coluna

		if (ship_type == 0) // Caso o navio seja do tipo 0 ( ou seja apenas ocupa uma celula da matriz tabuleiro)
		{
			c1 = new Random().nextInt(row);// c1 e gerado de modo aleatorio dentro do intervalo de 0 a N
			c2 = new Random().nextInt(col);// c2 e gerado de modo aleatorio dentro do intervalo de 0 a M
			// Uma vez que o navio tipo 0 ocupa somente uma celula nao e necessario definir a sua orientacao

		}

		else if (ship_type == 1) // No caso do navio tipo 1 ( que ocupa duas celulas seguidas na tabela)
		{
			c1 = new Random().nextInt(row);
			c2 = new Random().nextInt(col);

			// Neste caso, e uma vez que o navio e composto por mais do que uma celula temos que avaliar a sua direcao
			// (norte(0),sul(1),este(3) e oeste(4))

			if (orientation == 0) // Caso a sua orientacao seja para norte / para cima)
			{
				while (c1 < 1) // Se c1 'calhar' na primeira linha, o navio ficara fora do limite superio da matriz
				{
					c1 = new Random().nextInt(row); // ora iremos continuar a gerar valores aleatorios para c1 ate este
													// ser diferente de 1 e assim asseguramos que este se encontra
													// dentro dos limites da matriz
				}
			}

			else if (orientation == 1) // Se a orientacao for para sul( para baixo)
			{
				while (c1 >= (row - 1)) // se c1 for colocado na ultima linha de matriz, o navio estara fora do limite
										// inferior da matriz
				{
					c1 = new Random().nextInt(row); // continuaremos a gerar valores aleatorios para c1 ate que este seja
													// colocado numa linha que nao seja a ultima da matriz
				}

			}

			else if (orientation == 2) // caso a orientacao seja para este, ou seja, para a direita
			{
				while (c2 >= (col - 1)) // Enquanto c2 nao for diferente da ultima coluna da matriz
				{
					c2 = new Random().nextInt(col); // continuaremos a gerar valor aleatorio entre 0 e M-1
				}
			}

			else // caso a orientacao seja para oeste ( esquerda)
			{
				while (c2 < 1) // se c2 se encontrar na primeira coluna da matriz
				{
					c2 = new Random().nextInt(col); // continuaremos a gerar aleatoriamente valores entre 0 e M-1
				}
			}
		}

		else // No caso do tipo de barco ser o tipo 2, ou seja, ocupando 4 celulas seguidas da matriz
		{
			c1 = new Random().nextInt(row); // Iremos gerar um numero aleatorio para c1 entre o e N
			c2 = new Random().nextInt(col);// Iremos gerar um numero aleatorio para c2 entre 0 e M

			if (orientation == 0) // No caso da orientacao ser para norte
			{
				while (c1 < 3) // e se c1 'calhar' em alguma das primeiras 3 linhas, o navio encontrar-se-a fora do
								// limite superior da matriz
				{
					c1 = new Random().nextInt(row); // iremos gerar valores para c1 ate este deixar de se encontrar numa
													// das primeiras 3 linhas
				}
			}

			else if (orientation == 1) // caso a orientacao seja para sul
			{
				while (c1 >= (row - 3)) // caso c1 se encontre em alguma das ultimas 3 linhas
				{
					c1 = new Random().nextInt(row); // Iremos gerar valores aleatorios para c1 ate este se encontrar em
													// algum ponto do resto da matriz
				}
			}

			else if (orientation == 2) // No caso da orientacao ser para este (direita)
			{
				while (c2 >= (col - 3)) // c2 nao podera ser colocado em nenhuma das ultimas 3 colunas pois caso isto
										// aconteca ira ficar de fora dos limites laterais da matriz tabuleiro
				{
					c2 = new Random().nextInt(col); // enquanto c2 for colocado em alguma das 3 ultimas colunas, serao
													// gerados valores aleatorios para c2 ate tal deixar de acontecer
				}
			}

			else // caso a orientacao seja para oeste (esquerda)
			{
				while (c2 < 3) // e se c2 se encontrar em alguma das primeiras 3 colunas
				{
					c2 = new Random().nextInt(col); // iremos gerar um valor aleatorio ate isso deixar de acontecer
				}
			}
		}

		ship_coordinates[0] = c1; // No final o valor valido de c1 sera colocado no elemento 1*1 do vetor linha
		ship_coordinates[1] = c2; // c2 valido sera colocado no elmento 1*2 do vetor

		return ship_coordinates; // sera devolvido o vetor com as coordenadas do navio [c1 c2] com os respectivos
									// valores gerados c1 e c2 aleatoriamente que se encontram dentro das condicoes
									// impostas
	}
	
	public int countS()
	{
		// conta o numero de caracteres 'S' presentes no tabuleiro
		int numS = 0;

		for (int row = 0; row < this.getRow(); row++)
		{
			for (int col = 0; col < this.getCol(); col++) // percorre cada elemento da matriz tabuleiro
			{
				if (this.board[row][col] == ship)
				{
					numS++; // faz a contagem dos elementos 'S'
				}
			}
		}

		return numS;
	}
	
	public void printBoard()
	{
		System.out.print("    ");
		// Calibracao dos espacos da matriz para o alinhamento das colunas(N) e linhas (N)

		for (int i = 0; i < this.getCol(); i++) // Impressao dos indices das colunas
		{

			if (i < 9)
			{
				System.out.print(i + 1 + "  "); // coloca um espaco adicional entre as linhas com indice menor que 9
			}
			else
			{
				System.out.print(i + 1 + " "); // coloca um espaco entre as colunas com indice maior que 9
			}
		}

		System.out.println();

		for (int row = 0; row < this.getRow(); row++) // percorre as linhas da matriz
		{
			// impressao dos indices das linhas
			if (row < 9)
			{
				System.out.print(row + 1 + "   ");
			}

			else
			{
				System.out.print(row + 1 + "  ");
			}

			for (int col = 0; col < this.getCol(); col++)
			{
				// impressao dos elementos da matriz tabuleiro
				char posicao = this.board[row][col];

//				if (posicao == ship) // esconde os navios, susbtituindo os elementos 'S' por agua
//				{
//					System.out.print(water + "  ");
//				}
//
//				else
//				{
					System.out.print(posicao + "  ");
//				}
			}

			System.out.println();
		}
	}
	
	public String evaluateShot(String playerCoordinates)

	// avalia o resultado do tiro do utilizador

	{
		int shotRow = Integer.parseInt(playerCoordinates.split(";")[0]);
		int shotCol = Integer.parseInt(playerCoordinates.split(";")[1]);
		
		shotRow--;
		shotCol--;
		
		char alvo = this.board[shotRow][shotCol]; // criamos um novo objeto do tipo carater que na matriz tabuleiro tem
											// coordenadas iguais às inseridas pelo utilizador
		String result = "";
		String finalResult ="";

		String message;

		if (alvo == ship) // se o tiro atingir um navio
		{
			message = "Acertou!";
			result = resultHit; // o tiro é considerado um 'hit'
		}

		else if (alvo == water) // se o tiro atingir agua
		{
			message = "Agua!";
			result = resultMiss;
		}

		else // se o tiro atingir um espaco ja atingido
		{
			message = "Ja atingido";
			result = resultAlreadyHit;
		}

		finalResult = result + ";" + message;
		return finalResult; // devolve o resultado final

	}
	
	public void updateBoard(String playerCoordinates, String shotResult)
	{

		// o tabuleiro e atualizado com o resultado do tiro do jogador
		
		int row = Integer.parseInt(playerCoordinates.split(";")[0]);
		int col = Integer.parseInt(playerCoordinates.split(";")[1]);
		
		row--;
		col--;

		if (shotResult.equals(resultHit))
		{
			this.board[row][col] = hit; // se o jogador atingir um navio, o carater é substituido por um X
		}

		else if (shotResult.equals(resultMiss) || shotResult.equals(resultAlreadyHit))
		{
			if (this.board[row][col] == hit)
			{
				this.board[row][col] = hit; // se o jogador atingir um navio ja atingido, o carater mantem-se
			}

			else
			{
				this.board[row][col] = miss; // se o jogador atingir agua ou um alvo ja atingido, o carater e subsituido
											// por um o
			}

		}
	}

	public void setFinalBoard(String shipLocations) {
		
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

	public String findShipLocations() {
		
		String shipCoordinates = "";
		
		for (int row = 0; row < this.getRow(); row++) {
			
			for (int col = 0; col < this.getCol(); col++) {
				
				char position = this.board[row][col];
				
				if (position == ship) {
					shipCoordinates = shipCoordinates + Integer.toString(row) + ":" + Integer.toString(col) + ";";
				}
			}
		}
		
		shipCoordinates = UtilsServer.replaceLast(shipCoordinates, ";", "");
		
		return shipCoordinates;
	}
    
}