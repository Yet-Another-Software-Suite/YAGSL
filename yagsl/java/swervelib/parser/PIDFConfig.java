package swervelib.parser;


/**
 * Hold the PIDF and Integral Zone values for a PID.
 */
public class PIDFConfig {

    /**
     * Proportional Gain for PID.
     */
    public double p;
    /**
     * Integral Gain for PID.
     */
    public double i;
    /**
     * Derivative Gain for PID.
     */
    public double d;

    /**
     * Feed forward kS property.
     */
    public double s = 0;
    /**
     * Feedforward kV property
     */
    public double v = 0;
    /**
     * Feedforward kA property.
     */
    public double a = 0;

}
