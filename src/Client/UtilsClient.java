package Client;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class UtilsClient {

	static Scanner scan = new Scanner(System.in);

	public static boolean checkValue(String rcvValue) {
		// Interpreta a confirmacao recebida e devolve um booleano que permite terminar
		// o ciclo da variavel respetiva

		boolean val = true;

		if (rcvValue.split(":")[0].startsWith("INVALID_SIZE")) {
			System.out.println("Valor fora dos limites\n");
			val = true;
		}

		else if (rcvValue.split(":")[0].startsWith("INVALID_FORMAT")) {
			System.out.println("Formato invalido\n");
			val = true;
		}

		else {
			val = false;
		}

		return val;
	}

	private static final DecimalFormat df = new DecimalFormat("0.00");

	public static void printStats(String rcvStats) {
		// Recebe o resultado de cada partida e o respetivo numero de jogadas e imprime
		// as estatisticas totais

		// Criacao de duas arrayLists para formatacao dos resultados
		ArrayList<String> resultsArray = new ArrayList<String>();
		ArrayList<Integer> playsArray = new ArrayList<Integer>();

		try { // Copia as estatisticas recebidas para as ArrayLists criadas

			String stats = rcvStats.split("=")[1];
			String resultsStri = stats.split(":")[0];
			String playsStri = stats.split(":")[1];

			for (String result : resultsStri.split(";")) {
				resultsArray.add(result);
			}

			for (String plays : playsStri.split(";")) {
				int playsInt = Integer.parseInt(plays);
				playsArray.add(playsInt);
			}
		} catch (Exception e) { // Se ainda nao houver partidas devolve uma string vazia
			rcvStats = "";
		}

		System.out.println("------------ STATS ------------");

		// no caso de ainda não ter havido partidas
		if (resultsArray.size() == 0) {
			System.out.println("\nAinda não há partidas");
			System.out.println();
		}

		else {
			double victories = 0;
			double defeats = 0;
			double quits = 0;
			double playsSum = 0;

			System.out.print("\nNumero do jogo:     ");
			for (int i = 1; i <= resultsArray.size(); i++) {
				if (i < 10) // ajuste dos espacos para alinhamento da tabela
				{
					System.out.print(i + "   ");
				} else if (i > 9 && i < 100) {
					System.out.print(i + "  ");
				}

				else {
					System.out.print(i + " ");
				}
			}

			System.out.print("\nResultado:          ");
			for (String r : resultsArray) {
				System.out.print(r + "   ");

				if (r.equals("V")) {
					victories++; // contagem das vitorias
				} else if (r.equals("D")) {
					defeats++; // contagem das derrotas
				} else {
					quits++; // contagem dos abandonos
				}
			}

			System.out.print("\nNumero de jogadas:  ");
			for (int p : playsArray) {
				playsSum += p; // contagem do total de jogadas

				if (p < 10) // ajuste dos espacos para alinhamento da tabela
				{
					System.out.print(p + "   ");
				} else if (p > 9 && p < 100) {
					System.out.print(p + "  ");
				}

				else {
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

			double averagePlays = playsSum / resultsArray.size(); // A media das jogadas e dada pelo total das jogadas a
																	// dividir pelo
			// numero de jogos jogados

			System.out.println("Media de jogadas por partida: " + df.format(averagePlays));
		}

	}

}
