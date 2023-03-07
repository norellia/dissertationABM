//Generating the FAD directional movement layer
//REQUIRES FAD LAYER INPUT FILE

package schooling;

import java.io.FileNotFoundException;

import sim.engine.SimState;
import sim.engine.Steppable;

@SuppressWarnings("serial")
public class Environment_fad implements Steppable 
{

	@Override
	public void step(SimState state) 
	{
	
    
	String file = "C:/Users/alexn/Desktop/fadbearing.txt";


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
		double faddirection = myArray[x][y];
		Ocean.fadgrid.field[x][y] = faddirection;
	}
	}
	}

}
