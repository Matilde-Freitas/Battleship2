
package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

//Classe que define o thread que deverá tratar da comunicação com um cliente
public class ConnectionHandler extends Thread {
	private Socket clientSocket;
	private DataInputStream in;
	private DataOutputStream out;
	private int id;
	private ArrayList<User> database;

	public ConnectionHandler(Socket sock, int id, ArrayList<User> database) {
		this.clientSocket = sock;
		this.id = id;
		this.database = database;
		try {
			// Criar canais de comunicação para o socket do cliente
			this.in = new DataInputStream(clientSocket.getInputStream());
			this.out = new DataOutputStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void run() {

		try {

			// Definicao das variaveis
			String[] usernamePass;
			String rcvMENU;
			boolean val_login = true;
			boolean val_menu = true;
			boolean val_login_menu = true;

			boolean val_game = true;
			boolean val_validBoard = false;
			boolean val_newBoard = false;

			boolean val_row = true;
			boolean val_col = true;
			boolean val_ships = true;
			boolean val_shot = true;

			boolean quitGame = false;

			String resultAlreadyHit = "ALREADY_HIT";

			User currentUser = null;

			while (val_login_menu) { // Inicio do ciclo do menu login

				val_login = true;

				String rcvLoginOrExit = this.in.readUTF(); // Recebe a escolha do cliente
				rcvLoginOrExit = rcvLoginOrExit.replaceAll("\\[", "").replaceAll("\\]", "");

				if (rcvLoginOrExit.equals("2")) { // Se o cliente quiser sair
					System.out.println("Cliente " + this.id + " saiu!!!");

					// Fechar canais de comunicação e socket com o cliente
					this.in.close();
					this.out.close();
					this.clientSocket.close();
					val_menu = false;
					val_login_menu = false;
				}

				else if (rcvLoginOrExit.equals("1")) {

					while (val_login) {
						val_menu = true;

						String rcvUsernamePass = in.readUTF();

						// Transforma a string contendo o username e password numa String[]
						usernamePass = UtilsServer.readLOGIN(rcvUsernamePass);

						System.out.println("C" + this.id + " -- " + " USERNAME = " + usernamePass[0] + " ; PASS = "
								+ usernamePass[1]);

						String sndClientValidation = "CLI_NEW:";

						// Verfica se o cliente ja esta presente na base de dados
						for (User c : database) {
							if (c.getUsername().equals(usernamePass[0])) {

								// Username e pass existem na base de dados, a conta existe
								if (c.getPass().equals(usernamePass[1])) { 
									currentUser = c;
									sndClientValidation = "CLI_OLD:" + currentUser.getUsername();
									val_login = false;
								}

								else { // Username igual mas pass diferente
									sndClientValidation = "WRONG_PASS:" + currentUser.getUsername();
									val_login = true;
								}
							}
						}

						// Username e pass nao existem na base de dados, e criado uma conta nova
						if (sndClientValidation.startsWith("CLI_NEW")) { 
							currentUser = new User(usernamePass[0], usernamePass[1]);
							database.add(currentUser);
							sndClientValidation = "CLI_NEW:" + currentUser.getUsername();
							val_login = false;
						}

						// Se a conta foi criada ou ja existia, e verificado o status do utilizador
						if (!sndClientValidation.startsWith("WRONG_PASS")) { 
							if (currentUser.getStatus().equals("ON")) {
								sndClientValidation = "CLI_ON:" + currentUser.getUsername();
								val_login = true;
							}
						}

						System.out.println("C" + this.id + " -- " + sndClientValidation);

						// Informa o cliente se a conta ja existe na base de dados e envia o seu
						// username
						this.out.writeUTF(sndClientValidation);
						this.out.flush();
					}
				}

				else { // Valor inserido no menu de login e invalido
					System.out.println("C" + this.id + " -- " + "INVALID_FORMAT_MENU");
					val_menu = false;
				}

				while (val_menu) { // Entra no ciclo do menu principal

					currentUser.setStatus("ON");

					rcvMENU = this.in.readUTF(); // Recebe a escolha do menu principal
					rcvMENU = rcvMENU.replaceAll("\\[", "").replaceAll("\\]", "");

					String sndMenuValidation = "MENU_VALID:" + rcvMENU;

					if (rcvMENU.equals("2")) { // Estatisticas

						System.out.println("C" + this.id + " -- " + sndMenuValidation);
						val_menu = true;
						this.out.writeUTF(sndMenuValidation); // Envia confirmacao do menu recebido
						this.out.flush();
						String sndStats = "STATS=";

						if (currentUser.getResult().size() != 0) { // Se ja houver partidas jogadas
							sndStats = "STATS=" + currentUser.resultsToString() + ":" + currentUser.playsToString();
						}

						System.out.println("C" + this.id + " -- " + sndStats);

						this.out.writeUTF(sndStats); // Envia as estatisticas do cliente atual
						this.out.flush();
					}

					else if (rcvMENU.equals("3")) { // Sair do menu principal
						System.out.println("C" + this.id + " -- " + sndMenuValidation);
						currentUser.setStatus("OFF");
						val_menu = false;
						this.out.writeUTF(sndMenuValidation); // Envia confirmacao do menu recebido
						this.out.flush();
					}

					else if (rcvMENU.equals("1")) { // Iniciar um novo jogo
						System.out.println("C" + this.id + " -- " + sndMenuValidation);
						val_menu = true;
						this.out.writeUTF(sndMenuValidation); // Envia confirmacao do menu recebido
						this.out.flush();

						val_game = true;

						while (val_game) { // Inicia o ciclo do jogo

							String sndValidBoard = "VALID_BOARD";
							val_validBoard = true;
							val_newBoard = true;
							val_row = true;
							val_col = true;
							val_ships = true;
							val_shot = true;

							String sndRowValue = "";
							String sndColValue = "";
							String sndShipsValue = "";
							String sndShotsValue = "";

							// Definicao e envio dos valores maximos e minimos das dimensoes do tabuleiro
							String sndRowCol_minMax = UtilsServer.checkBoardMaxMin(0, 0, 0, "ROWCOL");
							System.out.println("C" + this.id + " -- " + sndRowCol_minMax);
							this.out.writeUTF(sndRowCol_minMax);
							this.out.flush();

							while (val_row) { // Ciclo da definicao do numero de linhas do tabuleiro

								String rcvRowValue = this.in.readUTF(); // Recebe o valor inserido de linhas do
																		// tabuleiro
								sndRowValue = UtilsServer.checkBoardSettings(rcvRowValue, sndRowCol_minMax,
										"ROW_VALUE");
								System.out.println("C" + this.id + " -- " + sndRowValue);

								this.out.writeUTF(sndRowValue); // Envia a confirmacao e respetivo valor de linhas
																// inserido
								this.out.flush();

								val_row = sndRowValue.startsWith("INVALID"); // Se o valor for valido, avanca para o
																				// proximo ciclo
							}

							int row = Integer.parseInt(sndRowValue.split(":")[1]);

							while (val_col) { // Ciclo da definicao do numero de colunas do tabuleiro

								String rcvColValue = this.in.readUTF(); // Recebe o valor inserido de colunas do
																		// tabuleiro
								sndColValue = UtilsServer.checkBoardSettings(rcvColValue, sndRowCol_minMax,
										"COL_VALUE");
								System.out.println("C" + this.id + " -- " + sndColValue);

								this.out.writeUTF(sndColValue); // Envia a confirmacao e respetivo valor de colunas
																// inserido
								this.out.flush();

								val_col = sndColValue.startsWith("INVALID"); // Se o valor for valido, avanca para o
																				// proximo ciclo
							}

							int col = Integer.parseInt(sndColValue.split(":")[1]);

							// Definicao e envio dos valores maximos e minimos do numero de navios
							String sndShips_minMax = UtilsServer.checkBoardMaxMin(0, row, col, "SHIPS");
							System.out.println("C" + this.id + " -- " + sndShips_minMax);
							this.out.writeUTF(sndShips_minMax);
							this.out.flush();

							while (val_ships) { // Ciclo de definicao do numero de navios, semelhante aos anteriores

								String rcvShipsValue = this.in.readUTF();
								sndShipsValue = UtilsServer.checkBoardSettings(rcvShipsValue, sndShips_minMax,
										"SHIPS_VALUE");
								System.out.println("C" + this.id + " -- " + sndShipsValue);

								this.out.writeUTF(sndShipsValue);
								this.out.flush();

								val_ships = sndShipsValue.startsWith("INVALID");
							}

							int numShips = Integer.parseInt(sndShipsValue.split(":")[1]);

							// Criacao do tabuleiro que vai ser mostrado ao servidor
							ServerBoard serverBoard = new ServerBoard(row, col, numShips);

							while (val_newBoard) {

								serverBoard.createServerBoard(); // Cria o tabuleiro com os navios posicionados

								// Se nao foi possivel criar um tabuleiro em menos de 100 tentativas
								if (serverBoard.getBoard().length < 10) {
									val_validBoard = false;
									val_newBoard = false;
									sndValidBoard = "INVALID_BOARD";
								}

								System.out.println("C" + this.id + " -- " + sndValidBoard);
								this.out.writeUTF(sndValidBoard); // Envia a confirmacao da formacao do tabuleiro
								this.out.flush();

								// Se o tabuleiro foi criado com sucesso
								if (val_validBoard) {

									// Conta o numero de celulas correspondentes a navios
									int numS = serverBoard.countS();

									// Cria e envia uma string com as coordenadas das celulas correspondentes a
									// navios
									String sndShipLocations = serverBoard.findShipLocations();
									System.out.println("C" + this.id + " -- " + "SHIP_LOCATIONS = " + sndShipLocations);
									this.out.writeUTF("SHIP_LOCATIONS=" + sndShipLocations);
									this.out.flush();

									// Definicao e envio do numero maximo e minimo de tiros
									String sndShots_minMax = UtilsServer.checkBoardMaxMin(numS, row, col, "SHOTS");
									System.out.println("C" + this.id + " -- " + sndShots_minMax);
									this.out.writeUTF(sndShots_minMax);
									this.out.flush();

									val_shot = true;

									while (val_shot) { // Ciclo de definicao do numero de tiros, semelhante aos
														// anteriores

										String rcvShotsValue = this.in.readUTF();
										sndShotsValue = UtilsServer.checkBoardSettings(rcvShotsValue, sndShots_minMax,
												"SHOT_VALUE");
										System.out.println("C" + this.id + " -- " + sndShotsValue);

										this.out.writeUTF(sndShotsValue);
										this.out.flush();

										val_shot = sndShotsValue.startsWith("INVALID");
									}

									int max_shot = Integer.parseInt(sndShotsValue.split(":")[1]);

									System.out.println("C" + this.id + " -- ");
									serverBoard.printBoard(); // Imprime o tabuleiro com os navios

									int plays = 0;
									quitGame = false;
									String sndGameResult = "";

									for (int i = 0; i < max_shot; i++) { // Cilco de cada jogada

										val_col = true;
										val_row = true;
										String sndShotRowVal = "";
										String rcvShotRowVal = "";

										while (val_row) { // Ciclo do valor da linha para atingir com um tiro
											rcvShotRowVal = this.in.readUTF();
											// Recebe e analisa o valor inserido para a linha
											sndShotRowVal = UtilsServer.insertServerCoordinates(rcvShotRowVal,
													serverBoard.getRow(), "SHOT_ROW");
											System.out.println("C" + this.id + " -- " + sndShotRowVal);

											this.out.writeUTF(sndShotRowVal);
											this.out.flush();
											if (sndShotRowVal.startsWith("INVALID")) { // Valor invalido, continua o ciclo

												val_row = true;
											}

											// Se o valor recebido foi "quit" ou "exit"
											else if (sndShotRowVal.startsWith("EXIT_GAME")) {

												sndGameResult = "Abandonou o jogo";
												System.out.println("");
												this.out.writeUTF("GAME_END=A:" + sndGameResult); // Envia o resutlado do jogo
												this.out.flush();

												// Adiciona o resultado do jogo e numero de jogadas ao atual cliente
												currentUser.getResult().add("A");
												currentUser.getNumberOfPlays().add(plays);

												val_row = false;
												val_col = false;
												val_game = false;
												val_newBoard = false;
												quitGame = true;
												i = max_shot;
											}

											else { // Valor valido, termina o ciclo
												val_row = false;
											}
										}

										String sndShotCol = "";
										String rcvShotCol = "";

										while (val_col) { // Ciclo do valor da coluna para atingir com um tiro
											// Recebe e analisa o valor inserido para a coluna
											rcvShotCol = this.in.readUTF();
											sndShotCol = UtilsServer.insertServerCoordinates(rcvShotCol,
													serverBoard.getCol(), "SHOT_COL");
											System.out.println("C" + this.id + " -- " + sndShotCol);

											this.out.writeUTF(sndShotCol);
											this.out.flush();

											if (sndShotCol.startsWith("INVALID")) { // Valor invalido, continua o ciclo
												val_col = true;
											}

											// Se o valor recebido foi "quit" ou "exit"
											else if (sndShotCol.startsWith("EXIT_GAME")) {

												sndGameResult = "Abandonou o jogo";
												this.out.writeUTF("GAME_END=A:" + sndGameResult); // Envia o resutlado do jogo
												this.out.flush();

												// Adiciona o resultado do jogo e numero de jogadas ao atual cliente
												currentUser.getResult().add("A");
												currentUser.getNumberOfPlays().add(plays);

												val_row = false;
												val_col = false;
												val_game = false;
												val_newBoard = false;
												quitGame = true;
												i = max_shot;
											}

											else { // Valor valido, termina o ciclo
												val_col = false;
											}
										}

										if (!quitGame) { // Se o cliente nao abandonar o jogo
											String sndPlayerCoordinates = rcvShotRowVal + ";" + rcvShotCol;
											// Avalia o tiro e devolve o resultado
											String sndShotResult = serverBoard.evaluateShot(sndPlayerCoordinates);

											System.out.println("C" + this.id + " -- " + sndShotResult.split(";")[0]);

											this.out.writeUTF(sndShotResult); // Envia o resultado da jogada
											this.out.flush();
											
											String shotResult = sndShotResult.split("=")[1];

											// Se a celula ja tiver sido atingida, nao conta para o numero de jogadas
											if (!shotResult.split(";")[0].equals(resultAlreadyHit)) {
												plays++;
											}

											else { // Se for uma celula nao atingida, o numero de jogadas diminui
												i--;
											}

											// Envia as coordenadas do tiro
											this.out.writeUTF("PLAYER_COORDINATES:" + sndPlayerCoordinates); 
											this.out.flush();

											// Atualiza o tabuleiro conforme o resultado do tiro
											serverBoard.updateBoard(sndPlayerCoordinates, shotResult.split(";")[0]);

											System.out.println("C" + this.id + " -- ");
											serverBoard.printBoard(); // Imprime o tabuleiro com o resultado do tiro

											numS = serverBoard.countS(); // Conta o numero de celulas de navios restantes

											// Imprime o numero de jogadas realizadas e maximas
											System.out.println("C" + this.id + " -- " + "PLAYS = " + plays
													+ "; MAX_SHOT = " + max_shot); 

											if (numS == 0) { // Se nao houver mais navios, o jogador ganha o jogo
												sndGameResult = "GAME_RESULT=V:Ganhou o jogo!";

												currentUser.getResult().add("V"); // Registo do jogo nas estatisticas
												currentUser.getNumberOfPlays().add(plays);

												val_newBoard = false; 
												i = max_shot;
												val_game = false;
											}

											else if (plays == max_shot) { // Se o numero maximo de jogadas for atingido, o jogador perde o jogo
												sndGameResult = "GAME_RESULT=D:Perdeu o jogo!";

												currentUser.getResult().add("D");
												currentUser.getNumberOfPlays().add(plays);

												val_newBoard = false;
												i = max_shot;
												val_game = false;
											}

											else { // Nao houve vitoria ou derrota, por isso o jogo continua
												sndGameResult = "GAME_RESULT=CONTINUE";
											}

											System.out.println("C" + this.id + " -- " + sndGameResult);
											this.out.writeUTF(sndGameResult); // Envia o resultado do jogo no final de cada partida
											this.out.flush();
										}
									}
								}
							}
						}
					}

					else { // Formato inserido no menu principal foi invalido
						sndMenuValidation = "MENU_INVALID:" + rcvMENU;
						System.out.println("C" + this.id + " -- " + sndMenuValidation);
						val_menu = true;
						this.out.writeUTF(sndMenuValidation);
						this.out.flush();
					}
				}
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}
}