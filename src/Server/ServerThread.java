package Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread {
	public static void main(String[] args) {
		int id = 1;

		// Criacao da base de dados com o perfil de cada utilizador
		ArrayList<User> database = new ArrayList<User>();

		try {
			// Criar ServerSocket para escutar na porta 12346
			ServerSocket listen = new ServerSocket(12346);
			System.out.println("Escutando na porta:" + listen.getLocalPort());

			while (true) {
				// Esperar pela ligação de um cliente
				Socket clientSocket = listen.accept();
				System.out.println("Recebi uma nova ligação...");
				// Delegar a comunicação com o cliente num thread e voltar a esperar por outro
				// cliente
				Thread c = new Thread(new ConnectionHandler(clientSocket, id, database));
				c.start();
				id++;
			}
		} catch (Exception e) {
			System.out.println("Server terminated. Error:");
			e.printStackTrace();
		}
	}

}