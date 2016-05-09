/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias;

import org.chocosolver.solver.ResolutionPolicy;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.ICF;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import static org.chocosolver.solver.search.strategy.ISF.*;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VF;
import org.chocosolver.solver.variables.VariableFactory;



/**
 *
 * @author Mert
 */
public class ChocaNonLinear {
    
    
    public void Choca(){
    Solver solver = new Solver("my first problem");

    IntVar X = VariableFactory.bounded("X", -999, 999, solver);
  //  IntVar Y = VariableFactory.bounded("Y", 6, 6, solver);
    
    IntVar Y = VariableFactory.scale(X, -3*X.getValue()+2);
    IntVar Z=VariableFactory.offset(Y, 6);
    
    IntVar OBJ = VF.bounded("objective",-999, 999, solver);
    solver.post(ICF.scalar(new IntVar[]{Z}, new int[]{1}, OBJ));
    solver.findOptimalSolution(ResolutionPolicy.MAXIMIZE, OBJ);
   
    System.out.println( Z.getValue()+"      Z Value");
    System.out.println( X.getValue()+"      X Value");
    }
    
}
