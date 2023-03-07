//Generating the Sea Surface Temperature layer
//REQUIRES SST LAYER INPUT FILE

package schooling;

import java.io.FileNotFoundException;

import sim.engine.SimState;
import sim.engine.Steppable;

@SuppressWarnings("serial")
public class Environment implements Steppable 
{
    public static int i = 2108;

	@Override
	public void step(SimState state) 
	{
	
    
	String file = "D:/alexn/Documents/Big Data/input/layer"+i+".txt";
	i++;


	double[][] myArray = null;
	try {
		myArray = Importer.getMap( file );
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}

	for(int x = 0; x < Ocean.gridWidth; x++)
	{
	for(int y = 0; y < Ocean.gridHeight; y++)
	{		
		double heat = myArray[x][y];
		Ocean.valgrid.field[x][y] = heat;
	}
	}
	}

}
