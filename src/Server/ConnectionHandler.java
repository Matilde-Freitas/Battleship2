
package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import Client.Client;

//Classe que define o thread que deverá tratar da comunicação com um cliente
public class ConnectionHandler extends Thread {
	private Socket clientSocket;
	private DataInputStream in;
	private DataOutputStream out;
	private int id;

	public ConnectionHandler(Socket sock, int id) {
		this.clientSocket = sock;
		this.id = id;
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
			String[] usernamePass;
			String rcvMENU;
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

			ArrayList<Client> database = new ArrayList<Client>();

			Client currentClient = null;

			while (val_login_menu) {

				String rcvLoginOrExit = this.in.readUTF();
				rcvLoginOrExit = rcvLoginOrExit.replaceAll("\\[", "").replaceAll("\\]", "");

				if (rcvLoginOrExit.equals("2")) {
					System.out.println("Cliente " + this.id + " saiu!!!");

					// Fechar canais de comunicação e socket com o cliente
					this.in.close();
					this.out.close();
					this.clientSocket.close();
				}

				else if (rcvLoginOrExit.equals("1")) {
					val_menu = true;

					String rcvUsernamePass = in.readUTF();
					usernamePass = UtilsServer.readLOGIN(rcvUsernamePass);
					System.out.println(
							"C" + this.id + " -- " +  " USERNAME = " + usernamePass[0] + " ; PASS = " + usernamePass[1]);

					String sndClientValidation = "CLI_NEW:";

					for (Client c : database) {
						if (c.getUsername().equals(usernamePass[0]) && c.getPass().equals(usernamePass[1])) {
							currentClient = c;
							sndClientValidation = "CLI_OLD:" + currentClient.getUsername();
							val_menu = true;
						}
					}

					if (sndClientValidation.startsWith("CLI_NEW")) {
						currentClient = new Client(usernamePass[0], usernamePass[1]);
						database.add(currentClient);
						sndClientValidation = "CLI_NEW:" + currentClient.getUsername();
						val_menu = true;
					}

					this.out.writeUTF(sndClientValidation);
					this.out.flush();
				}

				else {
					System.out.println("C" + this.id + " -- " + "INVALID_FORMAT_MENU");
					val_menu = false;
				}

				while (val_menu) {
					rcvMENU = this.in.readUTF();
					rcvMENU = rcvMENU.replaceAll("\\[", "").replaceAll("\\]", "");

					String sndMENU = "MenuVALID:" + rcvMENU;

					if (rcvMENU.equals("2")) {
						System.out.println("C" + this.id + " -- " + sndMENU);
						val_menu = true;
						this.out.writeUTF(sndMENU);
						this.out.flush();
						String sndStats = "";

						if (currentClient.getResult().size() != 0) {
							sndStats = currentClient.resultsToString() + ":" + currentClient.playsToString();
						}

						System.out.println("C" + this.id + " -- " + sndStats);

						this.out.writeUTF(sndStats);
						this.out.flush();
					}

					else if (rcvMENU.equals("3")) {
						System.out.println("C" + this.id + " -- " + sndMENU);
						val_menu = false;
						this.out.writeUTF(sndMENU);
						this.out.flush();
					}

					else if (rcvMENU.equals("1")) {
						System.out.println("C" + this.id + " -- " + sndMENU);
						val_menu = true;
						this.out.writeUTF(sndMENU);
						this.out.flush();

						val_game = true;

						while (val_game) {

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

							String sndRowCol_minMax = UtilsServer.checkBoardMaxMin(0, 0, 0, "linhas");
							this.out.writeUTF(sndRowCol_minMax);
							this.out.flush();

							while (val_row) {

								String rcvRowValue = this.in.readUTF();
								sndRowValue = UtilsServer.checkBoardSettings(rcvRowValue, sndRowCol_minMax,
										"ROW_VALUE");
								System.out.println("C" + this.id + " -- " + sndRowValue);

								this.out.writeUTF(sndRowValue);
								this.out.flush();

								val_row = sndRowValue.startsWith("INVALID");
							}

							int row = Integer.parseInt(sndRowValue.split(":")[1]);

							while (val_col) {

								String rcvColValue = this.in.readUTF();
								sndColValue = UtilsServer.checkBoardSettings(rcvColValue, sndRowCol_minMax,
										"COL_VALUE");
								System.out.println("C" + this.id + " -- " + sndColValue);

								this.out.writeUTF(sndColValue);
								this.out.flush();

								val_col = sndColValue.startsWith("INVALID");
							}

							int col = Integer.parseInt(sndColValue.split(":")[1]);

							String sndShips_minMax = UtilsServer.checkBoardMaxMin(0, row, col, "navios");
							this.out.writeUTF(sndShips_minMax);
							this.out.flush();

							while (val_ships) {

								String rcvShipsValue = this.in.readUTF();
								sndShipsValue = UtilsServer.checkBoardSettings(rcvShipsValue, sndShips_minMax,
										"SHIPS_VALUE");
								System.out.println("C" + this.id + " -- " + sndShipsValue);

								this.out.writeUTF(sndShipsValue);
								this.out.flush();

								val_ships = sndShipsValue.startsWith("INVALID");
							}

							int numShips = Integer.parseInt(sndShipsValue.split(":")[1]);

							GameBoard serverBoard = new GameBoard(row, col, numShips);

							while (val_newBoard) {
								serverBoard.createServerBoard();

								if (serverBoard.getBoard().length < 10) {
									val_validBoard = false;
									val_newBoard = false;
									sndValidBoard = "INVALID_BOARD";
								}

								System.out.println("C" + this.id + " -- " + sndValidBoard);
								this.out.writeUTF(sndValidBoard);
								this.out.flush();

								if (val_validBoard) {
									int numS = serverBoard.countS();

									String sndShipLocations = serverBoard.findShipLocations();

									this.out.writeUTF(sndShipLocations);
									this.out.flush();

									String sndShots_minMax = UtilsServer.checkBoardMaxMin(numS, row, col, "tiros");
									this.out.writeUTF(sndShots_minMax);
									this.out.flush();

									val_shot = true;

									while (val_shot) {

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
									serverBoard.printBoard();

									int plays = 0;

									quitGame = false;

									String sndGameResult = "";

									for (int i = 0; i < max_shot; i++) {

										val_col = true;
										val_row = true;
										String sndShotRow = "";
										String rcvShotRow = "";

										while (val_row) {
											rcvShotRow = this.in.readUTF();
											sndShotRow = UtilsServer.insertServerCoordinates(rcvShotRow,
													serverBoard.getRow(), "SHOT_ROW");
											System.out.println("C" + this.id + " -- " + sndShotRow);

											this.out.writeUTF(sndShotRow);
											this.out.flush();
											if (sndShotRow.startsWith("INVALID")) {

												val_row = true;
											}

											else if (sndShotRow.startsWith("EXIT_GAME")) {

												sndGameResult = "Abandonou o jogo";
												this.out.writeUTF("A:" + sndGameResult);
												this.out.flush();

												currentClient.getResult().add("A");
												currentClient.getNumberOfPlays().add(plays);

												val_row = false;
												val_col = false;
												val_game = false;
												val_newBoard = false;
												quitGame = true;
												i = max_shot;
											}

											else {
												val_row = false;
											}
										}

										String sndShotCol = "";
										String rcvShotCol = "";

										while (val_col) {
											rcvShotCol = this.in.readUTF();
											sndShotCol = UtilsServer.insertServerCoordinates(rcvShotCol,
													serverBoard.getCol(), "SHOT_COL");
											System.out.println("C" + this.id + " -- " + sndShotCol);

											this.out.writeUTF(sndShotCol);
											this.out.flush();

											if (sndShotCol.startsWith("INVALID")) {
												val_col = true;
											}

											else if (sndShotCol.startsWith("EXIT_GAME")) {

												sndGameResult = "Abandonou o jogo";
												this.out.writeUTF("A:" + sndGameResult);
												this.out.flush();

												currentClient.getResult().add("A");
												currentClient.getNumberOfPlays().add(plays);

												val_row = false;
												val_col = false;
												val_game = false;
												val_newBoard = false;
												quitGame = true;
												i = max_shot;
											}

											else {
												val_col = false;
											}
										}

										if (!quitGame) {
											String sndPlayerCoordinates = rcvShotRow + ";" + rcvShotCol;
											String sndShotResult = serverBoard.evaluateShot(sndPlayerCoordinates);

											System.out.println("C" + this.id + " -- " + sndShotResult.split(";")[0]);

											this.out.writeUTF(sndShotResult);
											this.out.flush();

											if (!sndShotResult.split(";")[0].equals(resultAlreadyHit)) {
												plays++;
											}

											else {
												i--;
											}

											this.out.writeUTF(sndPlayerCoordinates);
											this.out.flush();

											serverBoard.updateBoard(sndPlayerCoordinates, sndShotResult.split(";")[0]);

											System.out.println("C" + this.id + " -- ");
											serverBoard.printBoard();

											numS = serverBoard.countS();

											System.out.println("C" + this.id + " -- " + "PLAYS = " + plays + "; MAX_SHOT = " + max_shot);

											if (numS == 0) {
												sndGameResult = "V:Ganhou o jogo!";

												currentClient.getResult().add("V");
												currentClient.getNumberOfPlays().add(plays);

												val_newBoard = false;
												i = max_shot;
												val_game = false;
											}

											else if (plays == max_shot) {
												sndGameResult = "D:Perdeu o jogo!";

												currentClient.getResult().add("D");
												currentClient.getNumberOfPlays().add(plays);

												val_newBoard = false;
												i = max_shot;
												val_game = false;
											}

											else {
												sndGameResult = "CONTINUE";
											}

											System.out.println("C" + this.id + " -- " + sndGameResult);
											this.out.writeUTF(sndGameResult);
											this.out.flush();
										}
									}
								}
							}
						}
					}

					else {
						sndMENU = "INVALID:" + rcvMENU;
						System.out.println("C" + this.id + " -- " + sndMENU);
						val_menu = true;
						this.out.writeUTF(sndMENU);
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