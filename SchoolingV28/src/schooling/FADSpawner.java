//Helps spawn in FADs randomly, in a specific area, at different times.  

package schooling;


import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.util.Double2D;

@SuppressWarnings("serial")
public class FADSpawner implements Steppable {
	
	@Override
	public void step(SimState state) 
	{
		
	recruitFADnew( Ocean.tsFAD );
	
	}
	
	static void recruitFADnew( double num )
	{
		for(int i = 0; i < num; i++ )
		{
			if( Ocean.numFAD < Ocean.maxFAD )
			{
				makeOneFAD_new();
			}
		}
	}
	private static void makeOneFAD_new( ) 
	{			
		double x = Ocean.getRand().nextInt( 250 ) + 939;
		double y = 599 - Ocean.getRand().nextInt( 250 ) ;
		
		FAD fad = new FAD( new Double2D( x , y), 0 , Ocean.numIdFAD);
		
		Stoppable stop = Ocean.sched.scheduleRepeating( fad ); 
		
		fad.setStopper( stop );
		Ocean.numFAD++;
		Ocean.numIdFAD++;

	}
}
