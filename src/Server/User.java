package Server;

import java.util.ArrayList;

/**
 * User class
 * 
 * Cada utilizador tem um unsername e uma password
 * 
 * 
 */

public class User {

	protected String username;
	protected String pass;
	protected ArrayList<String> result;
	protected ArrayList<Integer> numberOfPlays;
	protected String status;

	/**
	 * User construtor
	 * 
	 * @param username      O nome de utilizador do User
	 * @param pass          A palavra-pass do User
	 * @param result        Os resultados de cada jogo
	 * @param numberOfPlays O numero de jogadas de cada jogo
	 * @param status        O estado de atividade do User (ON/OFF)
	 * @return uma instancia da classe User
	 */
	public User(String username, String pass) {
		this.username = username;
		this.pass = pass;
		this.result = new ArrayList<String>();
		this.numberOfPlays = new ArrayList<Integer>();
		this.status = "OFF";
	}

	/**
	 * Obter o nome de utilizador
	 * 
	 * @return O nome de utilizador do User
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Mudar o nome de utilizador
	 * 
	 * @return O novo nome de utilizador do User
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Obter a palavra-pass
	 * 
	 * @return A palavra-pass do User
	 */
	public String getPass() {
		return pass;
	}

	/**
	 * Mudar a palavra-pass
	 * 
	 * @return A nova palavra-pass do User
	 */
	public void setPass(String pass) {
		this.pass = pass;
	}

	/**
	 * Obter a lista com os resultados de cada jogo
	 * 
	 * @return A lista com os resultados de cada jogo do User
	 */
	public ArrayList<String> getResult() {
		return result;
	}

	/**
	 * Mudar a lista com os resultados de cada jogo
	 * 
	 * @return A nova lista com os resultados de cada jogo do User
	 */
	public void setResult(ArrayList<String> result) {
		this.result = result;
	}

	/**
	 * Obter a lista com o numero de jogadas de cada jogo
	 * 
	 * @return A lista com o numero de jogadas de cada jogo do User
	 */
	public ArrayList<Integer> getNumberOfPlays() {
		return numberOfPlays;
	}

	/**
	 * Mudar a lista com o numero de jogadas de cada jogo
	 * 
	 * @return A nova lista com o numero de jogadas de cada jogo do User
	 */
	public void setNumberOfPlays(ArrayList<Integer> numberOfPlays) {
		this.numberOfPlays = numberOfPlays;
	}

	/**
	 * Obter o estado de atividade
	 * 
	 * @return O estado de atividade do User
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Mudar o estado de atividade
	 * 
	 * @return O novo estado de atividade do User
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	public String resultsToString() {
		// Copiar os resultados de cada jogo do User para uma String

		String resultsStri = "";

		for (String result : this.getResult()) {
			resultsStri = resultsStri + result + ";";
		}

		// Remover o ultimo ";" para evitar erros de formatacao
		UtilsServer.replaceLast(resultsStri, ";", "");

		return resultsStri;
	}

	public String playsToString() {
		// Copiar o numero de jogadas de cada jogo do User para uma String

		String playsStri = "";

		for (int plays1 : this.getNumberOfPlays()) {
			String plays2 = Integer.toString(plays1);
			playsStri = playsStri + plays2 + ";";
		}

		// Remover o ultimo ";" para evitar erros de formatacao
		UtilsServer.replaceLast(playsStri, ";", "");

		return playsStri;
	}
}
