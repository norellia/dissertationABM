//The Skipjack Tuna Object - contains all movement rules that Skipjack tuna will follow.  

package schooling;

import java.util.ArrayList;
import java.util.List;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.util.Bag;
import sim.util.Double2D;


@SuppressWarnings("serial")
class SKJ implements Steppable {

	private Stoppable stopper;
	
	//Relevant movement information
	private Double2D loc;
	private int pangle;
	public double ox = Ocean.startx;
	public double oy = Ocean.starty;
	public int randomMovementProbability;
	public int stopped;
	public double minTemp;
	public double maxTemp;
	public double currentTemp;
	public int timeFAD;
	public int idnum;
	
	//Output controls
	private int id; //output id number
	private int timestep;
	static List<String> SKJlist = new ArrayList<String>(); //full SKJ printout

	//Fish object with location, previous angle, random movement probability
	//distance from origin, time stopped, preferred temperatures, time on FADs and id number
	SKJ( Double2D loc , int pangle, int randomMovementProbability, double distOrigin, int stopped, double minTemp, double maxTemp, double currentTemp, int timeFAD, int idnum)
	{
		setLoc( loc );
		setPangle( pangle );
		setRandomMovementProbability( randomMovementProbability );		
		setMinTemp( minTemp );
		setMaxTemp( maxTemp );
		setTimeFAD( timeFAD );
		setIdnum( idnum );
		setStopped( stopped );
	}
	
	//Steps SKJ follow
	@Override
	public void step(SimState state) 
	{
		//actions
		move();
		
		//prints out the coordinates of SKJ at specified times
		timestep++;
		if (timestep == BET.timeCheck) {
			System.out.println("SKJ" + "," + BET.timeCheck + "," + loc.x + "," + loc.y + "," + currentTemp + "," + timeFAD + "," + idnum);
			SKJlist.add("SKJ" + "," + BET.timeCheck + "," + loc.x + "," + loc.y + "," + currentTemp + "," + timeFAD + "," + idnum);
		}
		
		if ( BET.timesList.contains(timestep) ) {
			id++;
			System.out.println("SKJ" + "," + id + "," + loc.x + "," + loc.y + "," + currentTemp + "," + timeFAD + "," + idnum);
			SKJlist.add("SKJ" + "," + id + "," + loc.x + "," + loc.y + "," + currentTemp + "," + timeFAD + "," + idnum);
			}
		
	}

	//Movement Method
	private void move()
	{
		//initial bestx and besty
		final int START=-1;
		double bestx = START;
	    double besty = 0;
		
		//initials for temperature
		int myx = (int) Math.round( loc.x ); //round to nearest int before converting to int
        int myy = (int) Math.round( loc.y );
	    
        //check if there is a FAD within 1 grid cell
	    Bag closeFADs = Ocean.spaceFAD.getNeighborsWithinDistance( loc, 0.5 );
		if ( closeFADs.size() >= 1 ) {        			
			int timeWfad = timeFAD + 1;
    		setTimeFAD( timeWfad );
		}
	    //chain of if statements describing decision process
	    if (loc.x <= 2 | loc.x >= Ocean.width-2 | loc.y <= 2 | loc.y >= Ocean.height-2) {  //if touching an edge of the map
			bestx = loc.x;  //stop movement
			besty = loc.y;
		} else if ( Ocean.valgrid.field[(int) loc.x][(int) loc.y] == 0.0 ) {  //if temperature = 0 aka on land
			bestx = loc.x-2;  //go west
			besty = loc.y;
        	int angle = 180;
    		setPangle( angle );
        } else if ( randomMovementProbability == 0 ) { //if fish predetermined to move randomly
        	int angle = Ocean.getRand().nextInt( 360 );
			
        	Double2D locIncrements = AngleToCoords.getLocation( angle );
        	bestx = Ocean.spaceSKJ.stx( loc.x + locIncrements.x );
    		besty = Ocean.spaceSKJ.sty( loc.y + locIncrements.y );    	
    		setPangle( angle );
        } else if ( Ocean.fadPri == true ) { //if fish is responding to FADs
        		if ( closeFADs.size() >= 1 ) {   //is there a FAD nearby?     			
        			if(stopped > 3) {
            			//reset stopped variable to 0
        				int stop = 0;
            			setStopped(stop);
            			
            			int angle = (int) Ocean.fadgrid.field[myx][myy]; //get angle from FAD grid
            			
            			//move with FAD field
                    	Double2D locIncrements = AngleToCoords.getLocation( angle );
                    	bestx = Ocean.spaceSKJ.stx( loc.x + locIncrements.x );
                		besty = Ocean.spaceSKJ.sty( loc.y + locIncrements.y );
                		setPangle( angle );
        			} else {
        				//stay still for 3 timesteps to move at FAD speed
            			int stop = stopped + 1;
        				bestx = loc.x;
            			besty = loc.y;
            			setStopped(stop);
            			
            			//still set angle to the FAD direction
            			int angle = (int) Ocean.fadgrid.field[myx][myy]; //get angle from FAD grid
                		setPangle( angle );
                		}
        		} else if ( Ocean.tempOn == true ) { //same direction
        			if( Ocean.valgrid.field[myx][myy] > maxTemp )  // go to coldest place
        	        {
        	        for(int x=-1;x<2;x++)
        	            for (int y=-1;y<2;y++)
        	                if (!(x==0 && y==0))
        	                    {
        	                    
        	                	int xx = (int) Ocean.spaceSKJ.stx(x + myx);    // toroidal
        	                    int yy = (int) Ocean.spaceSKJ.sty(y + myy);       // toroidal
        	                    
        	                    if (bestx==START ||
        	                        (Ocean.valgrid.field[xx][yy] < Ocean.valgrid.field[myx][myy]) ||
        	                        (Ocean.valgrid.field[xx][yy] == Ocean.valgrid.field[myx][myy] && Ocean.getRand().nextBoolean() ))  // not uniform, but enough to break up the go-up-and-to-the-left syndrome
        	                        { bestx = Ocean.spaceSKJ.stx(x + loc.x); besty = Ocean.spaceSKJ.sty(y + loc.y); }
        	                    
        	                    }
        	        }
        	        else if ( Ocean.valgrid.field[myx][myy] < minTemp )  // go to warmest place
        	        {
        	        for(int x=-1;x<2;x++)
        	            for (int y=-1;y<2;y++)
        	                if (!(x==0 && y==0))
        	                    {
        	                    int xx = (int) Ocean.spaceSKJ.stx(x + myx);    // toroidal
        	                    int yy = (int) Ocean.spaceSKJ.sty(y + myy);       // toroidal
        	                    if (bestx==START || 
        	                        (Ocean.valgrid.field[xx][yy] > Ocean.valgrid.field[myx][myy]) ||
        	                        (Ocean.valgrid.field[xx][yy] == Ocean.valgrid.field[myx][myy] && Ocean.getRand().nextBoolean() ))  // && Ocean.getRand().nextBoolean() not uniform, but enough to break up the go-up-and-to-the-left syndrome
        	                        { bestx = Ocean.spaceSKJ.stx(x + loc.x); besty = Ocean.spaceSKJ.sty(y + loc.y); }
        	                    }
        	        }else { //same direction
        				int angle = pangle;
        				
        	        	Double2D locIncrements = AngleToCoords.getLocation( angle );
        	        	bestx = Ocean.spaceSKJ.stx( loc.x + locIncrements.x );
        	    		besty = Ocean.spaceSKJ.sty( loc.y + locIncrements.y );
        	    		setPangle( angle );
        		    }
        	    } else { //same direction
    				int angle = pangle;
    				
    	        	Double2D locIncrements = AngleToCoords.getLocation( angle );
    	        	bestx = Ocean.spaceSKJ.stx( loc.x + locIncrements.x );
    	    		besty = Ocean.spaceSKJ.sty( loc.y + locIncrements.y );
    	    		setPangle( angle );
    		    }
        } else if ( Ocean.tempOn == true ) {
        	if( Ocean.valgrid.field[myx][myy] > maxTemp )  // go to coldest place
	        {
	        for(int x=-1;x<2;x++)
	            for (int y=-1;y<2;y++)
	                if (!(x==0 && y==0))
	                    {
	                    
	                	int xx = (int) Ocean.spaceSKJ.stx(x + myx);    // toroidal
	                    int yy = (int) Ocean.spaceSKJ.sty(y + myy);       // toroidal
	                    
	                    if (bestx==START ||
	                        (Ocean.valgrid.field[xx][yy] < Ocean.valgrid.field[myx][myy]) ||
	                        (Ocean.valgrid.field[xx][yy] == Ocean.valgrid.field[myx][myy] && Ocean.getRand().nextBoolean() ))  // not uniform, but enough to break up the go-up-and-to-the-left syndrome
	                        { bestx = Ocean.spaceSKJ.stx(x + loc.x); besty = Ocean.spaceSKJ.sty(y + loc.y); }
	                    
	                    }
	        }
	        else if ( Ocean.valgrid.field[myx][myy] < minTemp )  // go to warmest place
	        {
	        for(int x=-1;x<2;x++)
	            for (int y=-1;y<2;y++)
	                if (!(x==0 && y==0))
	                    {
	                    int xx = (int) Ocean.spaceSKJ.stx(x + myx);    // toroidal
	                    int yy = (int) Ocean.spaceSKJ.sty(y + myy);       // toroidal
	                    if (bestx==START || 
	                        (Ocean.valgrid.field[xx][yy] > Ocean.valgrid.field[myx][myy]) ||
	                        (Ocean.valgrid.field[xx][yy] == Ocean.valgrid.field[myx][myy] && Ocean.getRand().nextBoolean() ))  // && Ocean.getRand().nextBoolean() not uniform, but enough to break up the go-up-and-to-the-left syndrome
	                        { bestx = Ocean.spaceSKJ.stx(x + loc.x); besty = Ocean.spaceSKJ.sty(y + loc.y); }
	                    }
	        } else if ( Ocean.fadSec == true ) { //if fish is responding to FADs
	        	if ( closeFADs.size() >= 1 ) {   //is there a FAD nearby?     			
	    			if(stopped > 3) {
	        			//reset stopped variable to 0
	    				int stop = 0;
	        			setStopped(stop);
	        			
	        			int angle = (int) Ocean.fadgrid.field[myx][myy]; //get angle from FAD grid
	        			
	        			//move with FAD field
	                	Double2D locIncrements = AngleToCoords.getLocation( angle );
	                	bestx = Ocean.spaceSKJ.stx( loc.x + locIncrements.x );
	            		besty = Ocean.spaceSKJ.sty( loc.y + locIncrements.y );
	            		setPangle( angle );
	    			} else {
	    				//stay still for 3 timesteps to move at FAD speed
	        			int stop = stopped + 1;
	    				bestx = loc.x;
	        			besty = loc.y;
	        			setStopped(stop);
	        			
	        			//still set angle to the FAD direction
	        			int angle = (int) Ocean.fadgrid.field[myx][myy]; //get angle from FAD grid
	            		setPangle( angle );
	            		}
	    			}else { //same direction
	    				int angle = pangle;
	    				
	    	        	Double2D locIncrements = AngleToCoords.getLocation( angle );
	    	        	bestx = Ocean.spaceSKJ.stx( loc.x + locIncrements.x );
	    	    		besty = Ocean.spaceSKJ.sty( loc.y + locIncrements.y );
	    	    		setPangle( angle );
	    		    } 
	        } else { //same direction
				int angle = pangle;
				
	        	Double2D locIncrements = AngleToCoords.getLocation( angle );
	        	bestx = Ocean.spaceSKJ.stx( loc.x + locIncrements.x );
	    		besty = Ocean.spaceSKJ.sty( loc.y + locIncrements.y );
	    		setPangle( angle );
		    }
        	
        } else if ( Ocean.fadSec == true ) { //if fish is responding to FADs
        	if ( closeFADs.size() >= 1 ) {   //is there a FAD nearby?     			
    			if(stopped > 3) {
        			//reset stopped variable to 0
    				int stop = 0;
        			setStopped(stop);
        			
        			int angle = (int) Ocean.fadgrid.field[myx][myy]; //get angle from FAD grid
        			
        			//move with FAD field
                	Double2D locIncrements = AngleToCoords.getLocation( angle );
                	bestx = Ocean.spaceSKJ.stx( loc.x + locIncrements.x );
            		besty = Ocean.spaceSKJ.sty( loc.y + locIncrements.y );
            		setPangle( angle );
    			} else {
    				//stay still for 3 timesteps to move at FAD speed
        			int stop = stopped + 1;
    				bestx = loc.x;
        			besty = loc.y;
        			setStopped(stop);
        			
        			//still set angle to the FAD direction
        			int angle = (int) Ocean.fadgrid.field[myx][myy]; //get angle from FAD grid
            		setPangle( angle );
            		}
    			}else { //same direction
    				int angle = pangle;
    				
    	        	Double2D locIncrements = AngleToCoords.getLocation( angle );
    	        	bestx = Ocean.spaceSKJ.stx( loc.x + locIncrements.x );
    	    		besty = Ocean.spaceSKJ.sty( loc.y + locIncrements.y );
    	    		setPangle( angle );
    		    } 
        } else { //same direction
			int angle = pangle;
			
        	Double2D locIncrements = AngleToCoords.getLocation( angle );
        	bestx = Ocean.spaceSKJ.stx( loc.x + locIncrements.x );
    		besty = Ocean.spaceSKJ.sty( loc.y + locIncrements.y );
    		setPangle( angle );
	    }
		 
		 
		//create a new instance of Double2d and set all other variables
		Double2D locNew = new Double2D( bestx, besty ); 
		setLoc( locNew );
		setRandomMovementProbability( Ocean.getRand().nextInt( Ocean.randomLevel ) );
		setCurrentTemp( Ocean.valgrid.field[myx][myy] );

	}

	
	void die() //if fish need to be removed
	{
		int num = Ocean.getNumYFT();
		num--;
		Ocean.setNumYFT( num );
		
		stopper.stop();
		Ocean.spaceSKJ.remove( this );
	}
	
	//setters and getters
	void setLoc( Double2D loc )
	{
		this.loc = loc;
		Ocean.spaceSKJ.setObjectLocation( this, loc );
	}
	void setPangle( int pangle )
	{
		this.pangle = pangle;
	}
	void setRandomMovementProbability( int randomMovementProbability )
	{
		this.randomMovementProbability = randomMovementProbability;
	}
	void setStopper( Stoppable stopper )
	{
		this.stopper = stopper;
	}
	void setStopped( int stopped )
	{
		this.stopped = stopped;
	}
	void setMinTemp( double minTemp )
	{
		this.minTemp = minTemp;
	}
	void setMaxTemp( double maxTemp )
	{
		this.maxTemp = maxTemp;
	}
	void setCurrentTemp( double currentTemp )
	{
		this.currentTemp = currentTemp;
	}
	void setTimeFAD( int timeFAD )
	{
		this.timeFAD = timeFAD;
	}
	void setIdnum( int idnum )
	{
		this.idnum = idnum;
	}
}
