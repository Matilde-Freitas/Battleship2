
package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import Client.Client;

//Classe que define o thread que deverá tratar da comunicação com um cliente
public class ConnectionHandlerExample extends Thread {
	private Socket clientSocket;
	private DataInputStream in;
	private DataOutputStream out;
	private int id;

	public ConnectionHandlerExample(Socket sock, int id) {
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

	public void run()
	{

		try
		{
			String clientLOGIN;
			String[] usernamePass;
			String clientMENU;
			boolean val_menu = true;
			boolean val_login = true;
			boolean val_login_menu = true;
			
			boolean val_game = true;
			boolean val_validBoard = false;
			boolean val_newBoard = false;
			

			boolean val_row = true;
			boolean val_col = true;
			boolean val_ships = true;
			boolean val_shot = true;
			
			boolean quitGame = false;
			
			String resultAlreadyHit = "AlreadyHit";
			
			ArrayList<Client> database = new ArrayList<Client>();
			Client matilde = new Client("matilde","123");
			database.add(matilde);
			
			Client currentClient = null;
			
			while (val_login_menu) {
				
				String loginOrExit=this.in.readUTF();
				loginOrExit = loginOrExit.replaceAll("\\[", "").replaceAll("\\]", "");
				
				if (loginOrExit.equals("2")) {
					System.out.println("Cliente "+this.id+" saiu!!!");
					
					//Fechar canais de comunicação e socket com o cliente
					this.in.close();
					this.out.close();
					this.clientSocket.close();
				}
				
				else if (loginOrExit.equals("1")) {
					val_login = true;
					val_menu = true;
					
					String rcvUsernamePass = in.readUTF();
					usernamePass = UtilsServer.readLOGIN(rcvUsernamePass);
					System.out.println("Client "+this.id+" username and pass = "+ usernamePass[0]+ " ; " + usernamePass[1]);
					
					String sndClientValidation = "";
					
					for (Client c: database)
					{
						if (c.getUsername().equals(usernamePass[0]))
						{
							if (c.getPass().equals(usernamePass[1]))
							{

								currentClient = c;
								sndClientValidation = "CLI_OLD:"+currentClient.getUsername();
								val_menu = true;
							}
							else {
								currentClient = new Client(usernamePass[0],usernamePass[1]);
								sndClientValidation = "CLI_NEW:"+currentClient.getUsername();
								val_menu = true;
							}
						}
						else
						{
							currentClient = new Client(usernamePass[0],usernamePass[1]);
							sndClientValidation = "CLI_NEW:"+currentClient.getUsername();
							val_menu = true;
						}
					}
					
					this.out.writeUTF(sndClientValidation);
					this.out.flush();
					
					
				}
				
				else {
					System.out.println("Formato invalido");
					val_menu = false;
				}
					
					while (val_menu)
					{	
						clientMENU=this.in.readUTF();
						clientMENU = clientMENU.replaceAll("\\[", "").replaceAll("\\]", "");
						
						if (clientMENU.equals("1"))
						{
							val_menu = true;
							this.out.writeUTF("MenuVALID:" + clientMENU);
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
								
								String rowCol_minMax = UtilsServer.checkBoardMaxMin(0, 0, 0, "linhas");
								this.out.writeUTF(rowCol_minMax);
								this.out.flush();
								
								while (val_row) {
									
									String rowValue = this.in.readUTF();
									sndRowValue = UtilsServer.checkBoardSettings(rowValue, rowCol_minMax, "ROW_VALUE");
									System.out.println(sndRowValue);
									
									this.out.writeUTF(sndRowValue);
									this.out.flush();
									if (sndRowValue.startsWith("INVALID"))
									{
										val_row = true;
									}
									
									else {
										val_row = false;
									}
								}
								

								int row = Integer.parseInt(sndRowValue.split(":")[1]);
								
								while (val_col) {
									
									String colValue = this.in.readUTF();
									sndColValue = UtilsServer.checkBoardSettings(colValue, rowCol_minMax, "COL_VALUE");
									System.out.println(sndColValue);
									
									this.out.writeUTF(sndColValue);
									this.out.flush();
									if (sndColValue.startsWith("INVALID"))
									{
										val_col = true;
									}
									
									else {
										val_col = false;
									}
								}
								
								int col = Integer.parseInt(sndColValue.split(":")[1]);
								
								String ships_minMax = UtilsServer.checkBoardMaxMin(0, row, col, "navios");
								this.out.writeUTF(ships_minMax);
								this.out.flush();
								
								while (val_ships) {
									
									String shipsValue = this.in.readUTF();
									sndShipsValue = UtilsServer.checkBoardSettings(shipsValue, ships_minMax, "SHIPS_VALUE");
									System.out.println(sndShipsValue);
									
									this.out.writeUTF(sndShipsValue);
									this.out.flush();
									if (sndShipsValue.startsWith("INVALID"))
									{
										val_ships = true;
									}
									
									else {
										val_ships = false;
									}
								}
								
								int numShips = Integer.parseInt(sndShipsValue.split(":")[1]);
								
								GameBoard serverBoard = new GameBoard(row,col,numShips);
								
								while (val_newBoard)
								{
									serverBoard.createServerBoard();
									
									if (serverBoard.getBoard().length < 10)
									{
										val_validBoard = false;
										val_newBoard = false;
										sndValidBoard = "INVALID_BOARD";
									}
									
									System.out.println(sndValidBoard);
									this.out.writeUTF(sndValidBoard);
									this.out.flush();
									
									if (val_validBoard)
									{
										int numS = serverBoard.countS();
										
										String sndShipLocations = serverBoard.findShipLocations();
										
										this.out.writeUTF(sndShipLocations);
										this.out.flush();
										
										String shots_minMax = UtilsServer.checkBoardMaxMin(numS, row, col, "tiros");
										this.out.writeUTF(shots_minMax);
										this.out.flush();
										
										val_shot = true;
										
										while (val_shot) {
											
											String shotsValue = this.in.readUTF();
											System.out.println(shotsValue);
											sndShotsValue = UtilsServer.checkBoardSettings(shotsValue, shots_minMax, "SHOT_VALUE");
											System.out.println(sndShotsValue);
											
											this.out.writeUTF(sndShotsValue);
											this.out.flush();
											if (sndShotsValue.startsWith("INVALID"))
											{
												val_shot = true;
											}
											
											else {
												val_shot = false;
											}
										}
										
										int max_shot = Integer.parseInt(sndShotsValue.split(":")[1]);
										
										System.out.println("max_shot=" + max_shot);
										
										serverBoard.printBoard();
										
										int plays = 0;
										
										quitGame = false;
										
										String gameResult = "";
										
										for (int i = 0; i < max_shot; i++) {

											val_col = true;
											val_row = true;
											String sndShotRow = "";
											String shotRow = "";
											
											while (val_row) {
												shotRow = this.in.readUTF();
												sndShotRow = UtilsServer.insertServerCoordinates(shotRow, serverBoard.getRow(),"SHOT_ROW");
												System.out.println(sndShotRow);
												
												this.out.writeUTF(sndShotRow);
												this.out.flush();
												if (sndShotRow.startsWith("INVALID"))
												{
													val_row = true;
												}
												else if (sndShotRow.startsWith("EXIT_GAME")){
													gameResult = "Abandonou o jogo";
													this.out.writeUTF("A:"+gameResult);
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
											String shotCol = "";
											
											while (val_col) {
												shotCol = this.in.readUTF();
												sndShotCol = UtilsServer.insertServerCoordinates(shotCol, serverBoard.getCol(),"SHOT_COL");
												System.out.println(sndShotCol);
												
												this.out.writeUTF(sndShotCol);
												this.out.flush();
												
												if (sndShotCol.startsWith("INVALID"))
												{
													val_col = true;
												}
												else if (sndShotCol.startsWith("EXIT_GAME")){
													gameResult = "Abandonou o jogo";
													this.out.writeUTF("A:"+gameResult);
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
												String playerCoordinates = shotRow + ";" + shotCol;
												String shotResult = serverBoard.evaluateShot(playerCoordinates);
												
												System.out.println(shotResult.split(";")[0]);
												
												this.out.writeUTF(shotResult);
												this.out.flush();
												
												if (!shotResult.split(";")[0].equals(resultAlreadyHit)) {
													plays++;
												}
												
												else {
													i--;
												}
												
												this.out.writeUTF(playerCoordinates);
												this.out.flush();
												
												serverBoard.updateBoard(playerCoordinates, shotResult.split(";")[0]);
												
												serverBoard.printBoard();
												
												numS = serverBoard.countS();
												
												System.out.println("plays=" + plays + "; max_shot=" + max_shot);
												
												if (numS == 0) {
													gameResult = "V:Ganhou o jogo!";
													
													currentClient.getResult().add("V");
													currentClient.getNumberOfPlays().add(plays);
													
													val_newBoard = false;
													i = max_shot;
													val_game = false;
												}
												
												else if (plays == max_shot) {
													gameResult = "D:Perdeu o jogo!";
													
													currentClient.getResult().add("D");
													currentClient.getNumberOfPlays().add(plays);
													
													val_newBoard = false;
													i = max_shot;
													val_game = false;
												}
												
												else {
													gameResult = "C:CONTINUE";
												}
												
												System.out.println(gameResult);
												this.out.writeUTF(gameResult);
												this.out.flush();
											}
										}
									}
									
								}

							}
							
							
						}
						
						else if (clientMENU.equals("2"))
						{
							System.out.println("Menu " + clientMENU);
							val_menu = true;
							this.out.writeUTF("MenuVALID:" + clientMENU);
							this.out.flush();
							String sndStats = "";
							
							if (currentClient.getResult().size() != 0 ) {
								sndStats = currentClient.resultsToString() + ":" + currentClient.playsToString();
							}
							
							System.out.println(sndStats);
							
							this.out.writeUTF(sndStats);
							this.out.flush();
						}
						
						else if (clientMENU.equals("3"))
						{
							System.out.println("Menu " + clientMENU);
							val_menu = false;
							this.out.writeUTF("MenuVALID:" + clientMENU);
							this.out.flush();
						}
						
						else
						{
							System.out.println("Menu " + clientMENU);
							val_menu = true;
							this.out.writeUTF("INVALID:" + clientMENU);
							this.out.flush();
						}
					}
				}
		
		}
			

	catch(Exception e)
	{
			e.printStackTrace();
		}
}

}