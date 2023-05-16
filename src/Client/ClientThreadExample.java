package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import Server.GameBoard;

public class ClientThreadExample {
	public static void main(String[] args) throws IOException {

		Scanner scan=new Scanner(System.in);
		//Criar ligação socket ao servidor
		Socket serverCon = new Socket("localhost",12346);

		//Criar canais de comunicação
		DataInputStream in = new DataInputStream(serverCon.getInputStream());
		DataOutputStream out = new DataOutputStream(serverCon.getOutputStream());
		
		boolean val_login_menu = true;
		boolean val_menu = true;
		boolean val_game = true;
		boolean val_validBoard = false;
		boolean val_newBoard = false;
		

		boolean val_row = true;
		boolean val_col = true;
		boolean val_ships = true;
		boolean val_shot = true;
		
		String resultAlreadyHit = "AlreadyHit";


		while (val_login_menu) {
				
			System.out.println("\n-----------------------------\nInsira [1] para fazer login."
					+ "\nInsira [2] para sair do programa.");
			
			String sndLoginOrExit = scan.nextLine();
			out.writeUTF(sndLoginOrExit);
			out.flush();
			
			if (sndLoginOrExit.equals("2")) {

				//Fechar canais de comunicação e socket
				in.close();
				out.close();
				serverCon.close();
				scan.close();
			}
			
			else if (sndLoginOrExit.equals("1")) {
				val_menu = true;
				
				System.out.print("\n------------ MAIN MENU ------------\nUsername: ");
				String username =scan.nextLine();
				System.out.print("Password: ");
				String password = scan.nextLine();
				
				String sndUsernamePass = username + ";" + password;
				//Enviar string para o servidor
				out.writeUTF(sndUsernamePass);
				out.flush();
				
				String rcvClientValidation = in.readUTF();
				String CliUsername = rcvClientValidation.split(":")[1];
				
				if (rcvClientValidation.startsWith("CLI_OLD")) {
					System.out.println("\nBenvindo/a de volta " + CliUsername);
				}
				
				else {
					System.out.println("\nBenvindo/a " + CliUsername);
				}
			}
			
			else {
				System.out.println("Formato invalido");
				val_menu = false;
			}
			
			while (val_menu)
			{
			
				System.out.println("\n------------ MAIN MENU ------------\nInsira [1] para jogar uma nova partida."+ "\nInsira [2] para ver o historico de partidas." + "\nInsira [3] para sair.");
				String sndMENU = scan.nextLine();
				out.writeUTF(sndMENU);
				out.flush();
				
				String rcvMENU = in.readUTF();
				
				if(rcvMENU.startsWith("MenuVALID"))
				{
					//Informar o utilizador a respeito do comando recebido
					String strCliMenu=rcvMENU.split(":")[1];
					
					if (strCliMenu.equals("3"))
					{
						System.out.println("A sair do menu");
						val_menu = false;
					}
					
					else if (strCliMenu.equals("2"))
					{
						String rcvStats = in.readUTF();
						
						System.out.println(rcvStats);
						
						UtilsClient.printStats(rcvStats);
						
						val_menu = true;
					}
					
					else if (strCliMenu.equals("1"))
					{
						System.out.println("------------ GAME START ------------");
						
						val_game = true;
						
						while (val_game) {
							val_validBoard = true;
							val_newBoard = true;
							val_row = true;
							val_col = true;
							val_ships = true;
							val_shot = true;
							
							String rcvRowValue = "";
							String rcvColValue = "";
							String rcvShipsValue = "";
							String rcvShotValue = "";
							
							System.out.println("\nDefina as dimensoes do tabuleiro.\n");
							String rowCol_minMax = in.readUTF();
							String rowCol_min = rowCol_minMax.split(";")[0];
							String rowCol_max = rowCol_minMax.split(";")[1];
							
							while (val_row) {
								System.out.print("Numero de linhas (entre "+rowCol_min+" e "+rowCol_max+"): ");
								String rowValue = scan.nextLine();
								out.writeUTF(rowValue);
								out.flush();
								
								rcvRowValue = in.readUTF();
								
								val_row = UtilsClient.checkValue(rcvRowValue);
							}
							
							int row = Integer.parseInt(rcvRowValue.split(":")[1]);
							System.out.println();
							
							while (val_col) {
								System.out.print("Numero de colunas (entre "+rowCol_min+" e "+rowCol_max+"): ");
								String colValue = scan.nextLine();
								out.writeUTF(colValue);
								out.flush();
								
								rcvColValue = in.readUTF();
								
								val_col = UtilsClient.checkValue(rcvColValue);
							}
							
							int col = Integer.parseInt(rcvColValue.split(":")[1]);
							System.out.println();
							
							String ships_minMax = in.readUTF();
							String ships_min = ships_minMax.split(";")[0];
							String ships_max = ships_minMax.split(";")[1];
							
							while (val_ships) {
								System.out.print("Numero de navios (entre "+ships_min+" e "+ships_max+"): ");
								String shipsValue = scan.nextLine();
								out.writeUTF(shipsValue);
								out.flush();
								
								rcvShipsValue = in.readUTF();
								
								val_ships = UtilsClient.checkValue(rcvShipsValue);
							}
							
							int numShips = Integer.parseInt(rcvShipsValue.split(":")[1]);
							System.out.println();
							
							GameBoard clientBoard = new GameBoard(row,col,numShips);
							
							while (val_newBoard)
							{
								clientBoard.creatClientBoard();
								
								String rcvValidBoard = in.readUTF();
								
								if (rcvValidBoard.equals("INVALID_BOARD"))
								{
									val_validBoard = false;
									val_newBoard = false;
									System.out.println(
											"Excedeu o numero maximo de tentativas de colocacao. Por favor inserir novos valores iniciais");
								}
								
								if (val_validBoard)
								{
									String shipLocations = in.readUTF();
									
									val_shot = true;
									
									String shots_minMax = in.readUTF();
									String shots_min = shots_minMax.split(";")[0];
									String shots_max = shots_minMax.split(";")[1];
									
									while (val_shot) {
										System.out.print("Numero de tiros (entre "+shots_min+" e "+shots_max+"): ");
										String shotValue = scan.nextLine();
										out.writeUTF(shotValue);
										out.flush();
										
										rcvShotValue = in.readUTF();
										
										val_shot = UtilsClient.checkValue(rcvShotValue);
									}
									
									int max_shot = Integer.parseInt(rcvShotValue.split(":")[1]);
									System.out.println();

									clientBoard.printBoard();
									
									int plays = 0;
									
									boolean quitGame = false;
									
									String rcvGameResult = "";
									
									for (int i = 0; i < max_shot; i++) {

										val_col = true;
										val_row = true;
										String rcvShotRow = "";
										
										while (val_row) {
											System.out.print("Linha: ");
											String shotRow = scan.nextLine();
											out.writeUTF(shotRow);
											out.flush();
											
											rcvShotRow = in.readUTF();
											
											val_row = UtilsClient.checkValue(rcvShotRow);
											
											if (rcvShotRow.startsWith("EXIT_GAME")) {
												
												rcvGameResult = in.readUTF();
												System.out.println(rcvGameResult.split(":")[1]);
												
												clientBoard.setFinalBoard(shipLocations);
												clientBoard.printBoard();
												
												val_row = false;
												val_col = false;
												val_game = false;
												quitGame = true;
												val_newBoard = false;
												i = max_shot;
											}
										}

										System.out.println();
										String rcvShotCol = "";
										
										while (val_col) {
											System.out.print("Coluna: ");
											String shotCol = scan.nextLine();
											out.writeUTF(shotCol);
											out.flush();
											
											rcvShotCol = in.readUTF();
											
											val_col = UtilsClient.checkValue(rcvShotCol);
											
											if (rcvShotCol.startsWith("EXIT_GAME")) {

												rcvGameResult = in.readUTF();
												System.out.println(rcvGameResult.split(":")[1]);
												
												clientBoard.setFinalBoard(shipLocations);
												clientBoard.printBoard();
												
												val_row = false;
												val_col = false;
												val_game = false;
												quitGame = true;
												val_newBoard = false;
												i = max_shot;
											}
										}
			
										System.out.println();
										
										if (!quitGame) {
											String rcvShotResult = in.readUTF();
											System.out.println(rcvShotResult.split(";")[1]);
											
											if (!rcvShotResult.split(";")[0].equals(resultAlreadyHit)) {
												plays++;
											}
											
											else {
												i--;
											}
											
											String playerCoordinates = in.readUTF();
											
											clientBoard.updateBoard(playerCoordinates, rcvShotResult.split(";")[0]);
											

											clientBoard.printBoard();; // Imprime o tabuleiro com o resultado do tiro

											System.out.println("Numero de tiros restantes: " + (max_shot - plays));
											System.out.println();
											
											rcvGameResult = in.readUTF();
											
											if (!rcvGameResult.startsWith("C")) {
												
												System.out.println(rcvGameResult.split(":")[1]);
												
												clientBoard.setFinalBoard(shipLocations);
												clientBoard.printBoard();
												
												val_newBoard = false;
												i = max_shot;
												val_game = false;
											}
											
										}
									}
								}
							}
						}
					}
				}
				else
				{
					System.out.println("Formato invalido");//Faser até que a string introduzida seja diferente de "exit"
					val_menu = true;
				}
			}
		}

	}

}