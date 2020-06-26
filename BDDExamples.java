import net.sf.javabdd.*;

/**
 * Class to give some examples of the usage of the BDD package
 */
public class BDDExamples {

	public static void main(String[] args){
		// A BDDFactory is used to produce the variables and constants (True and False) that we are going to use.
		// It needs to be initialized with the number of variables to be used.
		BDDFactory fact = JFactory.init(20,20); // The two numbers represents the node number and the cache size.
												// Not so important - see project description for a useful size.
		int nVars = 3; // The number of variables to be used.fact.setVarNum(nVars);
		fact.setVarNum(nVars);
		
		// We can print out the node table (T in your nodes) using the factory
		System.out.println("The node table: "); // A row contains nodeID, variable number, low_nodeID, high_nodeID
												// The nodeID is the first number in the square brackets
		fact.printAll(); // Here, BDDs (nodes) for the requested number of variables (unnegated and negated) are already included  
		
		// Getting the constant BDDs True and False by use of the factory.
		BDD True = fact.one();
		BDD False = fact.zero();
		
		// Using the factory, we can produce BDDs corresponding to the i'th variable for i from 0 to nVars-1 
		BDD x0 = fact.ithVar(0); // A BDD representing the 0'th variable. 
		// The factory can also produce BDDs corresponding to the negation of the i'th variable
		BDD nx1 = fact.nithVar(1); // The representation of the negation of the 1'st variable
		BDD x2 = fact.ithVar(2); 
		// The negations of variables can also be made using the method 'not' (and the already produced "variable-BDDs"
		BDD nx2 = x2.not();
		
		// BDDs can be combined using the 'apply'-method which you know from the lectures ...
		BDD b1 = x2.apply(x0, BDDFactory.or); // Making the BDD corresponding to (x2 or x0)
		// ... or - more conveniently - by using the methods, 'or', 'and', 'not', 'imp', 'biimp' etc.
		BDD b2 = x2.or(x0); // Produces the same BDD as b1
		
		// We can check that b1 and b2 are indeed the same using 'equals'
		System.out.println("b1 equals b2: " + b1.equals(b2)); 
		
		// More complicated BDDs can of course also be produced
		BDD b3 = nx1.or(x0).and(x2.or(False)); // The expression (not x1 or x0) and (x2 or False)
		// Notice the relationship between the parenthesis and the structure of the calls 
		
		System.out.println("The node-entry for b3: ");
		fact.printTable(b3);
		
		// Satisfiability checks and tautology-checks can be done by comparing to True or False, or using the following 
		System.out.println("b3 is unsat? : " + b3.isZero()); 		
		System.out.println("b3 is tautology? : " + b3.isOne()); 
		
		// In order to restrict or quantify the expression to a given assignment,
		// we give the assignment as a conjunction where positive variables
		// indicate that the variable should be restricted to true, and vice versa.
		BDD restriction_x2True_x0False = fact.ithVar(2).and(fact.nithVar(0)); //e.g. x2 should be True and x0 should be False
		BDD restricted_b3 = b3.restrict(restriction_x2True_x0False); // Result of restricting b3 with the above restriction
																     // Note: result is returned in new BDD (b3 is unchanged)
		
		System.out.println("The node-entry for restricted_b3: ");
		fact.printTable(restricted_b3);
		System.out.println("restricted_b3 is the same as b3?: " + restricted_b3.equals(b3) );
		
		// Lastly notice, that there are version of many of the above mentioned methods (e.g. 'or')
		// that ends with "With". The latter methods transforms/consumes the calling BDD 
		// instead of returning a new BDD
	}
}
