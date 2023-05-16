package Client;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class UtilsClient {
	
	static Scanner scan = new Scanner(System.in);
	
	public static boolean checkValue (String rcvValue)
	{
		boolean val = true;
		
		if (rcvValue.split(":")[0].startsWith("INVALID_SIZE"))
		{
			System.out.println("Valor fora dos limites\n");
			val = true;
		}
		
		else if (rcvValue.split(":")[0].startsWith("INVALID_FORMAT"))
		{
			System.out.println("Formato invalido\n");
			val = true;
		}
		
		else {
			val = false;
		}
		
		return val;
	}
	
	public static boolean startNewGame()
	{
		boolean val_choice = true; // validacao do input
		boolean val_game = false;
		String i;

		System.out.println("\nInsira [1] para jogar novamente e [2] para voltar ao menu principal.");

		while (val_choice) // e enquanto val_escolha for igual a verdadeiro o programa prossegue no sentido de executar
							// as seguintes instrucoes
		{
			i = scan.nextLine();
			i = i.replaceAll("\\[", "").replaceAll("\\]", ""); // permite que programa aceite inputs do tipo [1] e [2],
																// removendo os parenteses retos

			if (i.equals("1"))
			{
				val_choice = false; // validacao do input para
				val_game = true; // inicia um novo jogo
			}

			else if (i.equals("2"))
			{
				val_choice = false; // validacao do input para
				val_game = false; // volta ao menu principal
			}

			else // input diferente de 1, 2, [1], [2]
			{
				System.out.println("Valor invalido");
				System.out.println("Insira [1] para jogar novamente e [2] para voltar ao menu principal.");
				val_choice = true; // volta a ler o novo input
			}
		}
		return val_game;
	}
	
	private static final DecimalFormat df = new DecimalFormat("0.00");
	
	public static void printStats(String stats)
	{
		
		
		ArrayList<String> resultsArray = new ArrayList<String>();
		ArrayList<Integer> playsArray = new ArrayList<Integer>();
		
		if (stats.length() != 0) {
			
			String resultsStri = stats.split(":")[0];
			String playsStri = stats.split(":")[1];
			
			for (String result: resultsStri.split(";"))
			{
				resultsArray.add(result);
			}
			
			for (String plays: playsStri.split(";")) {
				int playsInt = Integer.parseInt(plays);
				playsArray.add(playsInt);
			}
		}
		
		System.out.println("------------ STATS ------------");
		
		// no caso de ainda não ter havido partidas
		if (resultsArray.size() == 0)
		{
			System.out.println("\nAinda não há partidas");
			System.out.println();
		}
		
		else
		{
			double victories = 0;
			double defeats = 0;
			double quits = 0;
			double playsSum = 0;
			
			System.out.print("\nNumero do jogo:     ");
			for (int i = 1; i <= resultsArray.size(); i++)
			{
				if (i < 10)	// ajuste dos espacos para alinhamento da tabela
				{
					System.out.print(i + "   ");
				}
				else if (i > 9 && i < 100)
				{
					System.out.print(i + "  ");
				}
				
				else
				{
					System.out.print(i + " ");
				}
			}
			
			System.out.print("\nResultado:          ");
			for (String r: resultsArray)
			{
				System.out.print(r + "   ");
				
				if (r.equals("V"))
				{
					victories++;	// contagem das vitorias
				}
				else if (r.equals("D"))
				{
					defeats++;	// contagem das derrotas
				}
				else 
				{
					quits++;	// contagem dos abandonos
				}
			}
			
			System.out.print("\nNumero de jogadas:  ");
			for (int p: playsArray)
			{
				playsSum += p;	// contagem do total de jogadas
				
				if (p < 10)	// ajuste dos espacos para alinhamento da tabela
				{
					System.out.print(p + "   ");
				}
				else if (p > 9 && p < 100)
				{
					System.out.print(p + "  ");
				}
				
				else
				{
					System.out.print(p + " ");
				}
			}
			
			System.out.println();
			System.out.println("\nLegenda: 'A' - Abandonado , 'V' - Vitoria , 'D' - Derrota"); // impressao da legenda
			System.out.println();
			
			// calculo das percentagens
			
			double V_perc = (victories / resultsArray.size()) * 100;
			double D_perc = (defeats / resultsArray.size()) * 100;
			double A_perc = (quits / resultsArray.size()) * 100;
	
			System.out.println("Percentagem de vitorias: " + df.format(V_perc) + "%");
			System.out.println("Percentagem de derrotas: " + df.format(D_perc) + "%");
			System.out.println("Percentagem de abandonos: " + df.format(A_perc) + "%");
			
			System.out.println();
			
			double averagePlays = playsSum / resultsArray.size(); // A media das jogadas e dada pelo total das jogadas a dividir pelo
			// numero de jogos jogados
	
			System.out.println("Media de jogadas por partida: " + df.format(averagePlays));
		}
		
	}
	
}
