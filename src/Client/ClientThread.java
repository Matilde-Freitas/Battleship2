package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientThread {
	public static void main(String[] args) throws IOException {

		Scanner scan = new Scanner(System.in);
		// Criar ligação socket ao servidor
		Socket serverCon = new Socket("localhost", 12346);

		// Criar canais de comunicação
		DataInputStream in = new DataInputStream(serverCon.getInputStream());
		DataOutputStream out = new DataOutputStream(serverCon.getOutputStream());

		// Definicao das variaveis
		boolean val_login = true;
		boolean val_login_menu = true;
		boolean val_menu = true;
		boolean val_game = true;
		boolean val_validBoard = false;
		boolean val_newBoard = false;

		boolean val_row = true;
		boolean val_col = true;
		boolean val_ships = true;
		boolean val_shot = true;

		String resultAlreadyHit = "ALREADY_HIT";

		while (val_login_menu) { // Inicio do ciclo do menu login

			val_login = true;

			System.out.println("\n-----------------------------\nInsira [1] para fazer login."
					+ "\nInsira [2] para sair do programa.");

			String sndLoginOrExit = scan.nextLine();
			out.writeUTF(sndLoginOrExit); // Envia a escolha do cliente
			out.flush();

			if (sndLoginOrExit.equals("2")) { // Se o cliente quiser sair

				// Fechar canais de comunicação e socket
				in.close();
				out.close();
				serverCon.close();
				scan.close(); // Fechar o scan
				val_login_menu = false;
				val_menu = false;
			}

			else if (sndLoginOrExit.equals("1")) { // Entra no login

				while (val_login) { // Inicio do ciclo de login
					val_menu = true;

					System.out.print("\n------------ MAIN MENU ------------\nUsername: ");
					String username = scan.nextLine();
					System.out.print("Password: ");
					String password = scan.nextLine();

					String sndUsernamePass = username + ";" + password;
					out.writeUTF(sndUsernamePass); // Envia ao servidor o username e password inseridos
					out.flush();

					// Cliente e informado se a conta ja existe na base de dados
					String rcvClientValidation = in.readUTF();
					String CliUsername = rcvClientValidation.split(":")[1];

					if (rcvClientValidation.startsWith("CLI_OLD")) { // Utilizador ja existe na base de dados
						System.out.println("\nBenvindo/a de volta " + CliUsername);
						val_login = false;
					}

					else if (rcvClientValidation.startsWith("CLI_ON")) { // Utilizador ja tem a sessao iniciada
						System.out.println(
								"\nSessão iniciada noutro dispositivo. Por favor encerrar sessão antes de proceder com o login");
						val_login = true;
					}

					else if (rcvClientValidation.startsWith("WRONG_PASS")) { // Utilizador inseriu a pass errada
						System.out.println("\nPassword errada.");
						val_login = true;
					}

					else { // Utilizador criou uma conta nova
						System.out.println("\nBenvindo/a " + CliUsername);
						val_login = false;
					}

				}
			}

			else { // Valor inserido no menu de login e invalido
				System.out.println("Formato invalido");
				val_menu = false;
			}

			while (val_menu) { // Entra no ciclo do menu principal

				System.out.println("\n------------ MAIN MENU ------------\nInsira [1] para jogar uma nova partida."
						+ "\nInsira [2] para ver o historico de partidas." + "\nInsira [3] para sair.");
				String sndMENU = scan.nextLine();
				out.writeUTF(sndMENU); // Envia a escolha do menu principal
				out.flush();

				String rcvMENU = in.readUTF(); // Recebe a confirmacao do menu enviado

				if (rcvMENU.startsWith("MENU_VALID")) { // Se a opcao do menu principal for valida

					String strCliMenu = rcvMENU.split(":")[1];

					if (strCliMenu.equals("2")) { // Estatisticas
						String rcvStats = in.readUTF();

						UtilsClient.printStats(rcvStats); // Recebe as estatisticas do cliente atual

						val_menu = true;
					}

					else if (strCliMenu.equals("3")) { // Sair do menu principal
						System.out.println("A sair do menu");
						val_menu = false;
					}

					else if (strCliMenu.equals("1")) { // Iniciar um novo jogo
						System.out.println("------------ GAME START ------------");

						val_game = true;

						while (val_game) { // Inicia o ciclo do jogo

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
							// Recebe os valores maximos e minimos das dimensoes do tabuleiro
							String rcvRowCol_minMax = in.readUTF();
							String rowCol_minMax = rcvRowCol_minMax.split(":")[1];
							String rowCol_min = rowCol_minMax.split(";")[0];
							String rowCol_max = rowCol_minMax.split(";")[1];

							while (val_row) { // Ciclo da definicao do numero de linhas do tabuleiro
								System.out.print("Numero de linhas (entre " + rowCol_min + " e " + rowCol_max + "): ");
								String sndRowValue = scan.nextLine();
								out.writeUTF(sndRowValue); // Envia o valor inserido de linhas do tabuleiro
								out.flush();

								rcvRowValue = in.readUTF(); // Recebe a confirmacao do numero de linhas

								val_row = UtilsClient.checkValue(rcvRowValue); // Se o valor for valido, avanca para o
																				// proximo ciclo
							}

							int row = Integer.parseInt(rcvRowValue.split(":")[1]);
							System.out.println();

							while (val_col) { // Ciclo da definicao do numero de colunas do tabuleiro
								System.out.print("Numero de colunas (entre " + rowCol_min + " e " + rowCol_max + "): ");
								String sndcolValue = scan.nextLine(); // Envia o valor inserido de colunas do tabuleiro
								out.writeUTF(sndcolValue);
								out.flush();

								rcvColValue = in.readUTF(); // Recebe a confirmacao do numero de colunas

								val_col = UtilsClient.checkValue(rcvColValue); // Se o valor for valido, avanca para o
																				// proximo ciclo
							}

							int col = Integer.parseInt(rcvColValue.split(":")[1]);
							System.out.println();

							// Recebe os valores maximos e minimos do numero de navios
							String rcvShips_minMax = in.readUTF();
							String ships_minMax = rcvShips_minMax.split(":")[1];
							String ships_min = ships_minMax.split(";")[0];
							String ships_max = ships_minMax.split(";")[1];

							while (val_ships) { // Ciclo de definicao do numero de navios, semelhante aos anteriores
								System.out.print("Numero de navios (entre " + ships_min + " e " + ships_max + "): ");
								String sndShipsValue = scan.nextLine();
								out.writeUTF(sndShipsValue);
								out.flush();

								rcvShipsValue = in.readUTF();

								val_ships = UtilsClient.checkValue(rcvShipsValue);
							}

							int numShips = Integer.parseInt(rcvShipsValue.split(":")[1]);
							System.out.println();

							// Criacao do tabuleiro que vai ser mostrado ao cliente
							ClientBoard clientBoard = new ClientBoard(row, col, numShips);

							while (val_newBoard) {

								clientBoard.createClientBoard(); // Enche o tabuleiro de agua

								String rcvValidBoard = in.readUTF(); // Recebe a confirmacao da formacao do tabuleiro

								// Se nao foi possivel criar um tabuleiro em menos de 100 tentativas
								if (rcvValidBoard.equals("INVALID_BOARD")) {
									val_validBoard = false;
									val_newBoard = false;
									System.out.println(
											"Excedeu o numero maximo de tentativas de colocacao. \nPor favor inserir novos valores iniciais");
								}

								// Se o tabuleiro foi criado com sucesso
								if (val_validBoard) {

									// Recebe uma string com as coordenadas das celulas correspondentes a navios
									String rcvShipLocations = in.readUTF();
									String shipLocations = rcvShipLocations.split("=")[1];

									val_shot = true;

									// Recebe o numero maximo e minimo de tiros
									String rcvShots_minMax = in.readUTF();
									String shots_minMax = rcvShots_minMax.split(":")[1];
									String shots_min = shots_minMax.split(";")[0];
									String shots_max = shots_minMax.split(";")[1];

									while (val_shot) { // Ciclo de definicao do numero de tiros, semelhante aos
														// anteriores
										System.out.print(
												"Numero de tiros (entre " + shots_min + " e " + shots_max + "): ");
										String sndShotValue = scan.nextLine();
										out.writeUTF(sndShotValue);
										out.flush();

										rcvShotValue = in.readUTF();

										val_shot = UtilsClient.checkValue(rcvShotValue);
									}

									int max_shot = Integer.parseInt(rcvShotValue.split(":")[1]);
									System.out.println();

									clientBoard.printBoard();
									System.out.println(
											"Escreva 'quit' ou 'exit' a qualquer momento para voltar ao menu principal.");

									int plays = 0;
									boolean quitGame = false;
									String rcvGameResult = "";

									for (int i = 0; i < max_shot; i++) { // Ciclo de cada jogada

										val_col = true;
										val_row = true;
										String rcvShotRow = "";

										while (val_row) { // Ciclo do valor da linha para atingir com um tiro
											System.out.print("Linha: ");
											String sndShotRow = scan.nextLine();
											out.writeUTF(sndShotRow);
											out.flush();

											rcvShotRow = in.readUTF();

											val_row = UtilsClient.checkValue(rcvShotRow);

											// Se o valor recebido foi "quit" ou "exit"
											if (rcvShotRow.startsWith("EXIT_GAME")) {

												rcvGameResult = in.readUTF(); // Recebe o resultado do jogo
												System.out.println(rcvGameResult.split(":")[1]);

												// Revela a localizacao dos navios
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

										while (val_col) { // Ciclo do valor da coluna para atingir com um tiro
											System.out.print("Coluna: ");
											String sndShotCol = scan.nextLine();
											out.writeUTF(sndShotCol);
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

										if (!quitGame) { // Se o cliente nao abandonar o jogo
											String rcvShotResult = in.readUTF(); // Recebe o resultado da jogada
											System.out.println(rcvShotResult.split(";")[1]);
											String shotResult = rcvShotResult.split("=")[1];

											// Se a celula ja tiver sido atingida, nao conta para o numero de jogadas
											if (!shotResult.split(";")[0].equals(resultAlreadyHit)) {
												plays++;
											}

											else { // Se for uma celula nao atingida, o numero de jogadas diminui
												i--;
											}

											String rcvPlayerCoordinates = in.readUTF(); // Recebe as coordenadas do tiro
											String playerCoordinates = rcvPlayerCoordinates.split(":")[1];

											// Atualiza o tabuleiro conforme o resultado do tiro
											clientBoard.updateBoard(playerCoordinates, shotResult.split(";")[0]);

											clientBoard.printBoard(); // Imprime o tabuleiro com o resultado do tiro
											System.out.println(
													"Escreva 'quit' ou 'exit' a qualquer momento para voltar ao menu principal.\n");

											System.out.println("Numero de tiros restantes: " + (max_shot - plays));
											System.out.println();

											// Recebe o resultado da jogo no final da jogada: vitoria, derrota ou continuacao do jogo
											rcvGameResult = in.readUTF(); 

											if (!rcvGameResult.startsWith("GAME_RESULT=C")) { // Se houver vitoria ou derrota

												System.out.println(rcvGameResult.split(":")[1]);

												// Imprime o tabuleiro final com a localizacao dos navios restantes
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
				} else { // Formato inserido no menu principal foi invalido
					System.out.println("Formato invalido");
					val_menu = true;
				}
			}
		}
	}
}