package de.ferienakademie.smartquake.kernel2;
import org.ejml.data.DenseMatrix64F;


/**
 * Created by Felix Wechsler on 21/09/16.
 */
public interface TimeIntegrationSolver {


    /**
     * This is a interface for the time integration solver.
     *
     * @param t
     *        global time since start in seconds
     *
     * @param delta_t
     *        time step in seconds
     *
     */
    void nextStep( double t, double delta_t);

}
