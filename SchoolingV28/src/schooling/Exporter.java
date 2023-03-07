//Exports .txt files with the information for each species and FADs for each timestep requested
//REQUIRES OUTPUT FILE

package schooling;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import sim.engine.SimState;
import sim.engine.Steppable;

@SuppressWarnings("serial")
public class Exporter implements Steppable {
	
	public static void write (String filename, String[]x) throws IOException{
		  BufferedWriter outputWriter = null;
		  outputWriter = new BufferedWriter(new FileWriter(filename));
		  for (int i = 0; i < x.length; i++) {
		    // Maybe:
		    outputWriter.write(x[i]+"");
		    outputWriter.newLine();
		  }
		  outputWriter.flush();  
		  outputWriter.close();  
		}
	
	 public int time = 1;

		public void step(SimState state) 
		{
		
		String file = "D:/alexn/Documents/Big Data/model_output/run"+ OceanWGUI.y +"BET.txt";
		String fileyft = "D:/alexn/Documents/Big Data/model_output/run"+ OceanWGUI.y +"YFT.txt";
		String fileskj = "D:/alexn/Documents/Big Data/model_output/run"+ OceanWGUI.y +"SKJ.txt";
		String filefad = "D:/alexn/Documents/Big Data/model_output/run"+ OceanWGUI.y +"FAD.txt";

		time++;	
		
		if(time > BET.timeCheck) {
		String[] simpleArray = new String[ BET.BETlist.size() ];
		BET.BETlist.toArray( simpleArray );
		String[] simpleArrayyft = new String[ YFT.YFTlist.size() ];
		YFT.YFTlist.toArray( simpleArrayyft );
		String[] simpleArrayskj = new String[ SKJ.SKJlist.size() ];
		SKJ.SKJlist.toArray( simpleArrayskj );
		String[] simpleArrayfad = new String[ FAD.FADlist.size() ];
		FAD.FADlist.toArray( simpleArrayfad );
		
		try {
				write( file, simpleArray );
			} catch (IOException e) {
				e.printStackTrace();
			}
		try {
				write( fileyft, simpleArrayyft );
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		try {
				write( fileskj, simpleArrayskj );
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		try {
			write( filefad, simpleArrayfad );
		} catch (IOException e) {
			e.printStackTrace();
		}

		}
		}
}
