//The FAD Object - contains all movement rules that FADs will follow.  


package schooling;

import java.util.ArrayList;
import java.util.List;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.util.Double2D;

@SuppressWarnings("serial")
class FAD implements Steppable {

	private Stoppable stopper;
	
	//Relevant movement information
	private Double2D loc;
	public double ox = Ocean.startx;
	public double oy = Ocean.starty;
	private int soaktime;
	private int numId;
	
	//Tracker for slower speed
	private int speedstep;
	
	//Output controls
	private int id;  //output id number
	private int timestep;
	static List<String> FADlist = new ArrayList<String>(); //full FAD printout
	
	
	//FAD object with location, soak time and id number
	public FAD( Double2D loc , int soaktime, int numId)
	{
		setLoc( loc );
		setSoaktime( soaktime );
		setNumId( numId );
	}
	
	
	//Steps FADs follow
	@Override
	public void step(SimState state)
	{
		
		speedstep++;
		soaktime++;
		if (soaktime >= 192 ) { //after 24 days, get removed and add a new random FAD
			beach();
			spawn();
		}else if ( Ocean.valgrid.field[(int) loc.x][(int) loc.y] == 0.0 ) {  //if temperature = 0 aka null, hit land
			beach();
			spawn();
        }else if ( speedstep > 3 ) { //move every 4 time steps
			move();
			speedstep = 0;
		}


		//prints out the coordinates of all the FAD at specified times to the same output text file.  
		timestep++;
		if (timestep == BET.timeCheck) {
			System.out.println("FAD" + "," + id + "," + loc.x + "," + loc.y + "," + numId + "," + Environment.i);
			FADlist.add("FAD" + "," + id + "," + loc.x + "," + loc.y + "," + numId + "," + Environment.i);
		}
		
		if ( BET.timesList.contains(timestep) ) {
			id++;
			System.out.println("FAD" + "," + id + "," + loc.x + "," + loc.y + "," + numId + "," + Environment.i);
			FADlist.add("FAD" + "," + id + "," + loc.x + "," + loc.y + "," + numId + "," + Environment.i);
			}
	} 

	
	//Movement Method
	private void move()
	{
		//initial bestx and besty
		double bestx = 0;
	    double besty = 0;
		
		int myx = (int) loc.x;
        int myy = (int) loc.y;
        
		int angle = (int) Ocean.fadgrid.field[myx][myy];  //go the same angle as the FAD field

    	Double2D locIncrements = AngleToCoords.getLocation( angle );
    	bestx = Ocean.spaceFAD.stx( loc.x + locIncrements.x );
		besty = Ocean.spaceFAD.sty( loc.y + locIncrements.y );
		 
		//create a new instance of Double2d and set all other variables
		Double2D locNew = new Double2D( bestx, besty ); 
		setLoc( locNew );
		
	}
	
	//beach
	void beach()
	{
		//let's remove a FAD
		int num = Ocean.getNumFAD();
		num--;
		Ocean.setNumFAD( num );
		
		stopper.stop();
		Ocean.spaceFAD.remove( this );
	}
	void spawn()
	{
		int num = Ocean.getNumFAD();
		num++;
		Ocean.setNumFAD( num );
		
		//let's spawn in new FADs;
		
		double x = Ocean.getRand().nextInt( 250 ) + 939;
		double y = 599 - Ocean.getRand().nextInt( 250 ) ;
		
		FAD fad = new FAD( new Double2D( x , y), 0 , Ocean.numIdFAD);
		
		Ocean.numIdFAD++;
		
		Stoppable stop = Ocean.sched.scheduleRepeating( fad ); 
		
		fad.setStopper( stop );
	}
	
	//setters and getters
	void setLoc( Double2D loc )
	{
		this.loc = loc;
		Ocean.spaceFAD.setObjectLocation( this, loc );
	}
	void setSoaktime( int soaktime )
	{
		this.soaktime = soaktime;
	}
	void setNumId( int numId )
	{
		this.numId = numId;
	}
	void setStopper( Stoppable stopper )
	{
		this.stopper = stopper;
	}
}
