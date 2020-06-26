import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.*;

/**
 * The main class that parses command line parameters and initializes the n-queens problem.
 *
 * @author Mai Ajspur
 * @version 16.2.2018
 */
public class Queens {

	/**
     * Valid arguments: Logic size 
     * Standard values for size (length of square board) is 8. Should be greater than 4
     */
	public static void main(String[] args) {
		boolean err = args.length < 1;
        String errMsg = "You have to atleast one argument (an IQueensLogic-implementation)";

		int size = 8;
        IQueensLogic logic = null;
        
		if (args.length >= 1 ){
        	try {
                logic = parseLogicParam(args[0]);
            } catch(ClassNotFoundException cnf) {
                errMsg = cnf.toString();
                err = true;
            } catch(NoSuchMethodException nsme) {
                errMsg = "Your Logic had no constructor.";
                err = true;
            } catch(InstantiationException ie) {
                errMsg = "Your Logic could not be instantiated.";
                err = true;
            } catch(IllegalAccessException iae) {
                errMsg = "Your Logic caused an illegal access exception.";
                err = true;
            } catch(InvocationTargetException ite) {
                errMsg = "Your Logic constructor threw an exception: " + ite.toString();
                err = true;
            }
        
        	if ( args.length >= 2 ){
        		try {
        			size = Integer.parseInt(args[1]);
        	
        			if ( size <= 4 ){
        				errMsg = "Board size should be greater than 4";
        				err = true;
        			}
        	
        		}
        		catch(NumberFormatException nfe) {
        			errMsg = "Could not parse size value: " + args[1];
        			err = true;
        		}
        	}
		}

		if(err) {
        	printHelp(errMsg);
           	System.exit(1);
        }

		//Initializing the logic
		logic.initializeBoard(size);

		try {
			// Setup of the frame containing the game
			QueensGUI g = new QueensGUI(logic);
			JFrame f = new JFrame();
			f.setSize(200 + size*100, 200 + size*100);
			f.setTitle("n-queens Puzzle");
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.getContentPane().add(g);
			f.setVisible(true);
		}
	    catch (IOException e){
	      	errMsg = "Images not found at " + System.getProperty("user.dir") + "\\imgs";
	       	err = true;
	    }
	}
	
    /**
     * Printing error and help-message
     */
    public static void printHelp(String errMsg) {
    	System.err.println(errMsg);
    	System.err.println("Usage: java QueensLogic [size]");
    	System.err.println("\tQueensLogic\t\t- specifies a class implementing IQueensLogic");
    	System.err.println("\tsize\t\t - Must be an integer greater or equal to 5. Defaults to 8.");
    }
    
    /**
     * Returns an instance of the specified class implementing IQueensLogic
     * @param cmdParam String from the command line that should be a path to a java class implementing IQueensLogic
     */
    public static IQueensLogic parseLogicParam(String cmdParam) 
            throws ClassNotFoundException, NoSuchMethodException, 
                   InstantiationException, IllegalAccessException,
                   InvocationTargetException {    	
    	return (IQueensLogic)Class.forName(cmdParam).getConstructor().newInstance();
    }
}