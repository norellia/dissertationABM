//action that converts angles to x+y coordinates

package schooling;

import sim.util.Double2D;

class AngleToCoords 
{
	private static boolean arrayFilled = false;
	
	//calculate to a degree (0-359)
	private static int numToMake = 360; 
	
	private static double [] xArray = new double[ numToMake ];
	private static double [] yArray = new double[ numToMake ];
	
	static void fillArrays()
	{		
		for( int i = 0; i < numToMake; i++ )
		{
			double angle = i;
			xArray[ i ] = Math.cos( Math.toRadians( angle ) );
			yArray[ i ] = Math.sin( Math.toRadians( angle ) );
		}
		
		arrayFilled = true;
	}
			
	/**
	 * Returns a Double2D of coordinates from an angle read in
	 * If fillArrays not yet called, it calls it
	 * 
	 * @param angle
	 * @return Double2D
	 */
	
	static Double2D getLocation( int angle )
	{
		if( !arrayFilled ) fillArrays();
		Double2D loc = new Double2D( xArray[ angle ], yArray[ angle ] );
		
		return loc;
	}
}
