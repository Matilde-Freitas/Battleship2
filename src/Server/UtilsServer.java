package Server;

public class UtilsServer {

	public static String[] readLOGIN(String sndLOGIN) {
		// Transforma a string contendo o username e password numa String[]
		
		sndLOGIN.replaceAll(" ", "");
		String[] emailPass = sndLOGIN.split(";");

		return emailPass;

	}

	public static String checkBoardMaxMin(int numS, int row, int col, String command) {
		// Define os valores maximos e minimos das dimensoes do tabuleiro, do numero de navios e do numero de tiros
		
		int min = 0;
		int max = 0;

		if (command.equals("ROWCOL")) {
			min = 15;
			max = 30;
		}

		else if (command.equals("SHIPS")) {
			max = (row * col) / 4; // o numero maximo de navios que poderao ser colocados e dada pela expressao
			// (N*M)/4, de forma a garantir que as celulas dos navios nao excedam o tamanho
			// do tabuleiro

			min = 3;
		}

		else {
			max = row * col; // o objeto inteiro max e igual ao numero de elementos da matriz tabuleiro
			min = numS; // o objeto inteiro min e igual ao numare de celulas ocupadas pelos navios
		}

		String minMax = command + "_MINMAX:" + Integer.toString(min) + ";" + Integer.toString(max);

		return minMax;
	}

	public static String checkBoardSettings(String OGvalue, String minMax, String command) {
		// Verifica se o valor inserido de numero de linhas, colunas, navios e tiros e valido e devolve a confirmacao
		
		String StriValue;
		String minMaxStri = minMax.split(":")[1];
		String minStri = minMaxStri.split(";")[0];
		String maxStri = minMaxStri.split(";")[1];
		int min = Integer.parseInt(minStri);
		int max = Integer.parseInt(maxStri);

		try {
			int value = Integer.parseInt(OGvalue);
			if (value >= min && value <= max) {	
				StriValue = "VALID_" + command + ":" + OGvalue;
			} 
			else {	// Se o valor inserido excede os limites
				StriValue = "INVALID_SIZE_" + command + ":" + OGvalue;
			}
		} 
		catch (Exception e) { // Se o valor inserido nao tem o formato correto
			StriValue = "INVALID_FORMAT_" + command + ":" + OGvalue;
		}

		return StriValue;
	}

	public static String insertServerCoordinates(String OGvalue, int max, String command) {
		// Recebe e analisa o valor inserido para a linha ou coluna a atingir com um tiro

		String StriValue;
		OGvalue = OGvalue.toLowerCase();
		OGvalue = OGvalue.replaceAll("[']", "");

		try {
			int value = Integer.parseInt(OGvalue);
			if (value >= 1 && value <= max) {
				StriValue = "VALID_" + command + ":" + OGvalue;
			} else {
				StriValue = "INVALID_SIZE_" + command + ":" + OGvalue;
			}
		} catch (Exception e) { // Se o valor inserido nao for um numero inteiro
			if (OGvalue.equals("quit") || OGvalue.equals("exit")) {
				StriValue = "EXIT_GAME:" + OGvalue;
			} else {
				StriValue = "INVALID_FORMAT_" + command + ":" + OGvalue;
			}
		}

		return StriValue;
	}

	public static String replaceLast(String text, String regex, String replacement) {
		// Remove o ultimo elemento de uma string
		// Retirado de https://stackoverflow.com/questions/2282728/java-replacelast
		return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
	}

}
