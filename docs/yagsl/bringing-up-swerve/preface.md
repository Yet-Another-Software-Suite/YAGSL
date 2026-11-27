# Preface

## Swerve Drives are complicated!

Swerve drives can have many things go wrong.  Debugging can be a long and arduous process  if you don't have all the right information.

## Configuring a swerve drive takes time!

Depending on your experience level with swerve; your experience with configuring a swerve drive will be very different. Don't forget to factor in that physically assembling swerve modules takes time. &#x20;

If you have already worked with swerve code before you might find the process of YAGSL configuration to be fairly easy,  and maybe take about 30 minutes + PID tuning. If you have never had a working swerve drive before it can take **weeks** to configure correctly and many many help requests. Everything will work but patience is a must! I try to document everything so be sure to check here for any questions you may have!

## As much as you hope, plead, pray, or offer sacrifices, a swerve drive will not always "Just work"

Some tools like the [CTRE Tuner X Swerve Generator](https://pro.docs.ctr-electronics.com/en/latest/docs/tuner/tuner-swerve/index.html) use desktop clients to configure the swerve drive constants file for you based off of actions and preset data from COTS modules. These go a long way and are completely different that what YAGSL offers (and is a lot more work than I can justify to create in my free time). You are going to need a decent understanding of WPILib and Java to consider programming a swerve drive correctly. Some examples of what you should know how to do are change controllers from [`XboxController`](https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/wpilibj/XboxController.html) to [`Joystick`](https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/wpilibj/Joystick.html).
