# Module Auto-synchronization

YAGSL will automatically synchronize the absolute encoders and internal encoders of the angle/steering/azimuth motor controllers everytime the module is at rest for half-a-second if the delta between the internal encoder and absolute encoder is greater than the deadband.

This is configurable via `SwerveDrive.setModuleEncoderAutoSynchronize(bool, double)` where the first parameter is the enabled state and second is the deadband in degrees.
