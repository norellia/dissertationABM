//This is the overall controlling object that contains all of the adjustable parameters
//The model is initialized and all fish are initialized
//All fish preferences and which movement rules are activated are in this object
package schooling;


import java.io.FileNotFoundException;

import ec.util.MersenneTwisterFast;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Stoppable;
import sim.field.continuous.Continuous2D;
import sim.field.grid.DoubleGrid2D;
import sim.util.Double2D;

@SuppressWarnings("serial")
class Ocean extends SimState 
{
	//Needed for start
	private static MersenneTwisterFast  rand;
	static Schedule             		sched;
	
	//Start time for environment files
	static int i = 2108;
	
	//Separate ints and doubles of the grid size for different movement rules
	//size is based off lat/lon in 0.08x0.08 grid
	public static double width          = 1449; 
	public static double height         = 1063; 
	public static int gridWidth 		= 1449;
	public static int gridHeight 		= 1063;
	
	//Starting coordinates for release event
	public static double startx			= 945;
	public static double starty			= 488;
	
	//Radius*1.5 of senses
	private static double neighborhood  = 5;
	
	//Environment Controls
	public static int heat 				= 0;
    public static final double MAX_HEAT = 60;
	public static int faddirection 		= 0;

    //Numbers by species
	public static int numYFT       		= 0;
	public static int numBET      		= 0;	
	public static int numSKJ      		= 0;
	public static int initYFT      		= 2130;
	public static int initBET     		= 1520;
	public static int initSKJ     		= 230;
	private static int maxYFT       	= 30000;
	private static int maxBET      		= 30000;
	private static int maxSKJ      		= 30000;
	
	//movement rules
	public static boolean tempOn		= true;
	public static boolean fadPri		= true;
	public static boolean fadSec		= false;
	
	//randomness level
	//1 = 100%, 2 = 50%, 4 = 25%, 100 = 1%
	public static int randomLevel		= 100;
	
//	//saving temperature means +/- CI  - Varied
//	private static double maxTempBET	= 25.2;
//	private static double minTempBET	= 20.8;
//	private static double maxTempYFT	= 28.6;
//	private static double minTempYFT	= 23.6;
//	private static double maxTempSKJ	= 28.3;
//	private static double minTempSKJ	= 23;
	
	//saving temperature same means +/- CI - Same
	private static double maxTempBET	= 27.7;
	private static double minTempBET	= 20.3;
	private static double maxTempYFT	= 27.7;
	private static double minTempYFT	= 20.3;
	private static double maxTempSKJ	= 27.7;
	private static double minTempSKJ	= 20.3;

	
	//FAD starting values
	public static int numFAD     		= 0;
	public static int numIdFAD     		= 0;
	public static int initFAD      		= 192;
	public static int tsFAD     		= 4;
	public static int maxFAD       		= 1000; //the highest density of FADs in the area at 1 time, previously 768 = 192*4

	
	//Neighborhood/1.5 trial and error for making the code efficient.  
	//continuous spaces are see-through so you can stack them, grid2D spaces are not and need to be layered properly.  
	//makes coloring the fish easier later.  
	static Continuous2D spaceBET  = new Continuous2D( neighborhood/1.5, width, height );
	static Continuous2D spaceYFT = new Continuous2D( neighborhood/1.5, width, height );
	static Continuous2D spaceSKJ = new Continuous2D( neighborhood/1.5, width, height );
	static Continuous2D spaceFAD = new Continuous2D( neighborhood/1.5, width, height );
	public static DoubleGrid2D valgrid;
	public static DoubleGrid2D fadgrid;

	//Generate seed and open app
	Ocean( long seed)
	{
		super( seed );
		rand = this.random;
		sched = this.schedule;
		createGrids();
	}
	
	//Method creating the environment
	protected void createGrids()
	{
		
		//imported environment
		String file = "D:/alexn/Documents/Big Data/input/layer"+i+".txt";

		double[][] tempArray = null;
		try {
			tempArray = Importer.getMap( file );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//Create the environmental grid
		valgrid = new DoubleGrid2D(gridWidth, gridHeight, 0);
		for(int x = 0; x < gridWidth; x++)
		{
		for(int y = 0; y < gridHeight; y++)
		{		
			double heat = tempArray[x][y];
			valgrid.field[x][y] = heat;
		}
		}
		
		//Create the FAD grid
		String fadfile = "C:/Users/alexn/Desktop/fadbearing.txt";

		double[][] fadArray = null;
		try {
			fadArray = Importer.getMap( fadfile );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		fadgrid = new DoubleGrid2D(gridWidth, gridHeight, 0);
		for(int x = 0; x < gridWidth; x++)
		{
		for(int y = 0; y < gridHeight; y++)
		{		
			double faddirection = fadArray[x][y];
			fadgrid.field[x][y] = faddirection;
		}
		}
		
	}
		
	//start a run of the model
	@Override
	public void start()
	{
		super.start();
		createGrids();  //create the environment
		recruitYFT ( initYFT ); //generate the fish to fill the environment
		recruitBET ( initBET );
		recruitSKJ ( initSKJ );
		recruitFAD ( initFAD );
		
		schedule.scheduleRepeating(Schedule.EPOCH,1,new Exporter(),1); //print out reports of the status
		schedule.scheduleRepeating(Schedule.EPOCH,1,new Environment(),1); //allow the environment to change
		schedule.scheduleRepeating(Schedule.EPOCH,1,new FADSpawner(),1); //allow FADs to spawn
	}
	
	//Tell the program how many fish and FADs need to be recruited
	static void recruitYFT( double num )
	{
		for(int i = 0; i < num; i++ )
		{
			if( numYFT < maxYFT )
			{
				makeOneYFT();
			}
		}
	}
	
	static void recruitBET( double num )
	{
		for(int i = 0; i < num; i++ )
		{
			if( numBET < maxBET )
			{
				makeOneBET();
			}
		}
	}

	static void recruitSKJ( double num )
	{
		for(int i = 0; i < num; i++ )
		{
			if( numSKJ < maxSKJ )
			{
				makeOneSKJ();
			}
		}
	}
	
	static void recruitFAD( double num )
	{
		for(int i = 0; i < num; i++ )
		{
			if( numFAD < maxFAD )
			{
				makeOneFAD();
			}
		}
	}
	
	
	//Create each recruited fish and FADs with randomized initial statuses
	private static void makeOneYFT( ) 
	{
		double x = startx;
		double y = starty;
				
		YFT fish = new YFT( new Double2D( x , y ), Ocean.getRand().nextInt( 360 ), 0, 0, 0, minTempYFT, maxTempYFT, 0, 0, numYFT);
		
		Stoppable stop = sched.scheduleRepeating( fish ); 
		
		fish.setStopper( stop );
		numYFT++;
	}
	
	private static void makeOneBET( ) 
	{
		double x = startx;
		double y = starty;
		
		BET fish = new BET( new Double2D( x , y ), Ocean.getRand().nextInt( 360 ), 0, 0, 0, minTempBET, maxTempBET, 0, 0, numBET);
		
		Stoppable stop = sched.scheduleRepeating( fish ); 
		
		fish.setStopper( stop );
		numBET++;
	}
	
	private static void makeOneSKJ( ) 
	{
		double x = startx;
		double y = starty;
				
		SKJ fish = new SKJ( new Double2D( x , y ), Ocean.getRand().nextInt( 360 ), 0, 0, 0, minTempSKJ, maxTempSKJ, 0, 0, numSKJ);
		
		Stoppable stop = sched.scheduleRepeating( fish ); 
		
		fish.setStopper( stop );
		numSKJ++;
	}

	private static void makeOneFAD( ) 
	{			
		double x = Ocean.getRand().nextInt( 250 ) + 939;
		double y = 599 - Ocean.getRand().nextInt( 250 ) ;
		
		FAD fad = new FAD( new Double2D( x , y), Ocean.getRand().nextInt(191), numIdFAD);
		
		Stoppable stop = sched.scheduleRepeating( fad ); 
		
		fad.setStopper( stop );
		numFAD++;
		numIdFAD++;
	}
	
	//getters and setters
	public static MersenneTwisterFast getRand() {
		return rand;
	}

	public static void setRand(MersenneTwisterFast rand) {
		Ocean.rand = rand;
	}

	public static Schedule getSched() {
		return sched;
	}

	public static void setSched(Schedule sched) {
		Ocean.sched = sched;
	}

	public static double getWidth() {
		return width;
	}

	public static void setWidth(double width) {
		Ocean.width = width;
	}

	public static double getHeight() {
		return height;
	}

	public static void setHeight(double height) {
		Ocean.height = height;
	}

	public static double getNeighborhood() {
		return neighborhood;
	}

	public static void setNeighborhood(double neighborhood) {
		Ocean.neighborhood = neighborhood;
	}

	public static int getNumYFT() {
		return numYFT;
	}

	public static void setNumYFT(int numYFT) {
		Ocean.numYFT = numYFT;
	}
	public static int getNumFAD() {
		return numFAD;
	}

	public static void setNumFAD(int numFAD) {
		Ocean.numFAD = numFAD;
	}
	public static int getNumBET() {
		return numBET;
	}

	public static void setNumBET(int numBET) {
		Ocean.numBET = numBET;
	}

	public static double getInitYFT() {
		return initYFT;
	}

	public static void setInitYFT(int initYFT) {
		Ocean.initYFT = initYFT;
	}

	public static double getInitBET() {
		return initBET;
	}

	public static double getInitFAD() {
		return initFAD;
	}
	
	public static void setInitBET(int initBET) {
		Ocean.initBET = initBET;
	}

	public static int getMaxYFT() {
		return maxYFT;
	}

	public static void setMaxYFT(int maxYFT) {
		Ocean.maxYFT = maxYFT;
	}

	public static int getMaxBET() {
		return maxBET;
	}

	public static void setMaxBET(int maxBET) {
		Ocean.maxBET = maxBET;
	}
	public static Continuous2D getSpaceBET() {
		return spaceBET;
	}

	public static void setSpaceBET(Continuous2D spaceBET) {
		Ocean.spaceBET = spaceBET;
	}

	public static Continuous2D getSpaceYFT() {
		return spaceYFT;
	}

	public static void setSpaceYFT(Continuous2D spaceYFT) {
		Ocean.spaceYFT = spaceYFT;
	}
	
	
}
