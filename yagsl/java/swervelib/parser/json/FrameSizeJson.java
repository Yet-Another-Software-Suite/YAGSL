package swervelib.parser.json;

/**
 * Used to store frame size.
 */
public class FrameSizeJson {

  /**
   * Frame length in inches.
   */
  public double length;
  /**
   * Frame width in inches.
   */
  public double width;
  /**
   * Bumper width in inches.
   */
  public double bumperThickness;

  /**
   * Default constructor.
   */
  public FrameSizeJson() 
  {
  }

  /**
   * Default Constructor.
   *
   * @param length Frame length in inches.
   * @param width  Frame width in inches.
   * @param bumperThickness Bumper thickness in inches.
   */
  public FrameSizeJson(double length, double width, double bumperThickness) 
  {
    this.length = length;
    this.width = width;
    this.bumperThickness = bumperThickness;
  }
}
