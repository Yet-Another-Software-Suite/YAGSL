package swervelib.telemetry;

import edu.wpi.first.wpilibj.Alert.AlertType;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * Thread-safe psuedo-Alert class
 */
public class Alert
{

  /**
   * Group of the alert
   */
  public final String    group;
  /**
   * Text of the alert
   */
  private String text;
  /**
   * Type of the alert
   */
  public final AlertType type;
  private      boolean   toggle = false;

  /**
   * Create a new Alert
   *
   * @param group Group of the alert
   * @param text  Text of the alert
   * @param type  Type of the alert
   */
  public Alert(String group, String text, AlertType type)
  {
    this.group = group;
    this.text = text;
    this.type = type;
  }

  /**
   * Create a new Alert
   *
   * @param text Text of the alert
   * @param type Type of the alert
   */
  public Alert(String text, AlertType type)
  {
    this("", text, type);
  }


  /**
   * Toggle the alert
   *
   * @param toggle
   */
  public void set(boolean toggle)
  {
    this.toggle = toggle;
    if (toggle)
    {
      String msg = "[" + group + "] " + text;
      switch (type)
      {
        case kError:
          DriverStation.reportError(msg, toggle);
          break;
        case kInfo:
        case kWarning:
          DriverStation.reportWarning(msg, toggle);
          break;
      }
    }
  }

  /***
   * Set the text of the alert
   * @param text Text of the alert
   */
  public void setText(String text)
  {
    this.text = text;
  }

  /// Does nothing
  public void close()
  {
    set(false);
  }
}
