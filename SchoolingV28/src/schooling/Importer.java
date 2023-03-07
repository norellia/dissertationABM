//Imports the map layer files 

package schooling;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;


public class Importer {
   public static double[][] myArray;

	static double[][] getMap( String file ) throws FileNotFoundException
	{
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(new BufferedReader(new FileReader( file )));
	      int rows = Ocean.gridWidth;
	      int columns = Ocean.gridHeight;  //swapping these fixed everything. 
	      double [][] myArray = new double[rows][columns];
	      while(sc.hasNextLine()) {
	         for (int i=0; i<myArray.length; i++) {
	            String[] line = sc.nextLine().trim().split(" ");
	            for (int j=0; j<line.length; j++) {
	               myArray[i][j] = Double.parseDouble(line[j]);
	            }
	         }
	      }
		return myArray;
	}
}