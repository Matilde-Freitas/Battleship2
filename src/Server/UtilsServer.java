package Server;

public class UtilsServer {
	
	public static String[] readLOGIN(String sndLOGIN)
	{
		sndLOGIN.replaceAll(" ","");
		String[] emailPass = sndLOGIN.split(";");
		
		return emailPass;
		
	}
	
	public static String checkBoardMaxMin(int numS, int row, int col, String command)
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
		
		Integer.toString(min);
		Integer.toString(max);
		
		String minMax= Integer.toString(min) + ";"+Integer.toString(max);
		
		return minMax;
	}
	
	public static String checkBoardSettings(String OGvalue, String minMax, String command)
	{
		String StriValue;
		String minStri = minMax.split(";")[0];
		String maxStri = minMax.split(";")[1];
		int min = Integer.parseInt(minStri);
		int max = Integer.parseInt(maxStri);
		
		try 
		{
			int value = Integer.parseInt(OGvalue);
			if (value >= min && value <= max)
			{
				StriValue = "VALID_" + command + ":" + OGvalue;
			}
			else
			{
				StriValue = "INVALID_SIZE_" + command + ":" + OGvalue;
			}
		}
		catch (Exception e)
		{
			StriValue = "INVALID_FORMAT_" + command + ":" + OGvalue;
		}
		
		return StriValue;
	}
	
	public static String insertServerCoordinates(String OGvalue, int max, String command) {
		
		String StriValue;
		OGvalue = OGvalue.toLowerCase();
		OGvalue = OGvalue.replaceAll("[']", "");
		
		try 
		{
			int value = Integer.parseInt(OGvalue);
			if (value >= 1 && value <= max)
			{
				StriValue = "VALID_" + command + ":" + OGvalue;
			}
			else
			{
				StriValue = "INVALID_SIZE_" + command + ":" + OGvalue;
			}
		}
		catch (Exception e)
		{
			if (OGvalue.equals("quit")) {
				StriValue = "EXIT_GAME:" + OGvalue;
			}
			else {
				StriValue = "INVALID_FORMAT_" + command + ":" + OGvalue;
			}
		}
		
		return StriValue;
	}
	
    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }

}
