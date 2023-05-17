package Client;

import java.util.ArrayList;

import Server.UtilsServer;

public class Client {

	protected String username;
	protected String pass;
	protected ArrayList<String> result;
	protected ArrayList<Integer> numberOfPlays;

	public Client(String username, String pass) {
		this.username = username;
		this.pass = pass;
		this.result = new ArrayList<String>();
		this.numberOfPlays = new ArrayList<Integer>();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public ArrayList<String> getResult() {
		return result;
	}

	public void setResult(ArrayList<String> result) {
		this.result = result;
	}

	public ArrayList<Integer> getNumberOfPlays() {
		return numberOfPlays;
	}

	public void setNumberOfPlays(ArrayList<Integer> numberOfPlays) {
		this.numberOfPlays = numberOfPlays;
	}

	public String resultsToString() {

		String resultsStri = "";

		for (String result : this.getResult()) {
			resultsStri = resultsStri + result + ";";
		}

		UtilsServer.replaceLast(resultsStri, ";", "");

		return resultsStri;
	}

	public String playsToString() {

		String playsStri = "";

		for (int plays1 : this.getNumberOfPlays()) {
			String plays2 = Integer.toString(plays1);
			playsStri = playsStri + plays2 + ";";
		}

		UtilsServer.replaceLast(playsStri, ";", "");

		return playsStri;
	}
}
