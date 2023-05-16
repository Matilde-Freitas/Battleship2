package Client;

import java.text.DecimalFormat;
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
	
	public String resultsToString () {
		
		String resultsStri = "";
		
		for (String result: this.getResult()) {
			resultsStri = resultsStri + result + ";";
		}
		
		UtilsServer.replaceLast(resultsStri, ";", "");
		
		return resultsStri;
	}
	
	public String playsToString () {
		
		String playsStri = "";
		
		for (int plays1: this.getNumberOfPlays()) {
			String plays2 = Integer.toString(plays1);
			playsStri = playsStri + plays2 + ";";
		}
		
		UtilsServer.replaceLast(playsStri, ";", "");
		
		return playsStri;
	}

	
	private static final DecimalFormat df = new DecimalFormat("0.00");
	
	public void printStats()
	{
		System.out.println("------------ STATS ------------");
		
		// no caso de ainda não ter havido partidas
		if (this.getResult().size() == 0)
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
			for (int i = 1; i <= this.getResult().size(); i++)
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
			for (String r: this.getResult())
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
			for (int p: this.getNumberOfPlays())
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
			
			double V_perc = (victories / this.getResult().size()) * 100;
			double D_perc = (defeats / this.getResult().size()) * 100;
			double A_perc = (quits / this.getResult().size()) * 100;
	
			System.out.println("Percentagem de vitorias: " + df.format(V_perc) + "%");
			System.out.println("Percentagem de derrotas: " + df.format(D_perc) + "%");
			System.out.println("Percentagem de abandonos: " + df.format(A_perc) + "%");
			
			System.out.println();
			
			double averagePlays = playsSum / this.getResult().size(); // A media das jogadas e dada pelo total das jogadas a dividir pelo
			// numero de jogos jogados
	
			System.out.println("Media de jogadas por partida: " + df.format(averagePlays));
		}
		
	}

}
