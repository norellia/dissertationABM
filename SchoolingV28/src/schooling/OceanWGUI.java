//This is the GUI and the file you press "RUN" on.  


package schooling;

import java.awt.Color;

import javax.swing.JFrame;

import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.continuous.ContinuousPortrayal2D;
import sim.portrayal.grid.FastValueGridPortrayal2D;


public class OceanWGUI extends GUIState 
{

	static int y;
	public Display2D display;
	public JFrame displayFrame; 
	static Color myColor = Color.decode("#43B7BA");

    static FastValueGridPortrayal2D heatPortrayal = new FastValueGridPortrayal2D("Heat");
	ContinuousPortrayal2D YFTPortrayal  = new ContinuousPortrayal2D();
	ContinuousPortrayal2D BETPortrayal = new ContinuousPortrayal2D();
	ContinuousPortrayal2D SKJPortrayal = new ContinuousPortrayal2D();
	ContinuousPortrayal2D FADPortrayal = new ContinuousPortrayal2D();

	
	public static void main( String [] args)
	{
		OceanWGUI simGUI = new OceanWGUI();
		Console c = new Console( simGUI );
		c.setVisible( true );
	}
	
	public OceanWGUI ()
	{
		super( new Ocean( (long) System.currentTimeMillis() ) );
	}
	
	public static String getName ()
	{
		return "Fish Schooling";
	}

	public void start()
	{
		super.start();
		setupPortrayals();	
		y++;
	}
	
	public void load( SimState state )
	{
		super.load( state );
			
		// We now have new grids.  Set up portrayals to reflect that. 
		setupPortrayals();
	}
	
	public void setupPortrayals()
	{
		YFTPortrayal.setField( Ocean.spaceYFT );
		YFTPortrayal.setPortrayalForAll( new sim.portrayal.simple.RectanglePortrayal2D( Color.yellow, 2.0 ) );
		
		BETPortrayal.setField( Ocean.spaceBET );
		BETPortrayal.setPortrayalForAll( new sim.portrayal.simple.RectanglePortrayal2D( Color.cyan, 2.0 ) );
		
		SKJPortrayal.setField( Ocean.spaceSKJ );
		SKJPortrayal.setPortrayalForAll( new sim.portrayal.simple.RectanglePortrayal2D( Color.gray, 2.0 ) );
	
		FADPortrayal.setField( Ocean.spaceFAD );
		FADPortrayal.setPortrayalForAll( new sim.portrayal.simple.RectanglePortrayal2D( Color.white, 2.0 ) );
		
		heatPortrayal.setField(Ocean.valgrid);
	    heatPortrayal.setMap(new sim.util.gui.SimpleColorMap(0,Ocean.MAX_HEAT,Color.black,myColor));
	
	    
		//Reschedule the displayer
		display.reset();
	}
	
	public void init( Controller c )
	{
		super.init( c );
		
		//Build the portrayals 
		YFTPortrayal.setField( Ocean.spaceYFT );
		BETPortrayal.setField( Ocean.spaceBET );
		SKJPortrayal.setField( Ocean.spaceSKJ );
		FADPortrayal.setField( Ocean.spaceFAD );

		
		//Make the Display2D. We'll have it display stuff later
		display = new Display2D( 1000, 1000, this );
		displayFrame = display.createFrame();
		c.registerFrame( displayFrame );
		displayFrame.setVisible( true );
		
		//Attach the portrayals to the displayer, from bottom to top
        display.attach( heatPortrayal, "Heat" );
		display.attach( YFTPortrayal , "YFT" );
		display.attach( BETPortrayal , "BET" );
		display.attach( SKJPortrayal , "SKJ" );
		display.attach( FADPortrayal , "FAD" );



	}
	
	public void quit()
	{
		super.quit();
		
		if( displayFrame != null )  displayFrame.dispose();
		displayFrame = null;
		display = null;
	}

}
