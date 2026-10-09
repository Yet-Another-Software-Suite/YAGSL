# YAGSL Example Projects

This directory contains complete, working example robot projects demonstrating how to integrate **YAGSL (Yet Another Generic Swerve Library)** into FRC robot codebases.

## Available Examples

| Example | Framework | Description | Path Planning / Auto |
| :--- | :--- | :--- | :--- |
| **[commands2](./commands2)** | WPILib Commands v2 (`edu.wpi.first.wpilibj2.command`) | Standard command-based robot using subsystems, triggers, and PathPlanner integration. | PathPlanner + Named Commands |
| **[commands3](./commands3)** | WPILib Commands v3 (`org.wpilib.command3`) & OpMode framework | Modern coroutine-driven mechanisms with OpMode-based autonomous and teleop routines. | Relative waypoints / `driveToPose` |

---

## Which Example Should I Use?

- **Use `commands2`** if your team uses standard WPILib Command-Based programming and PathPlanner for autonomous path generation and following.
- **Use `commands3`** if your team is exploring or using the next-generation Commands v3 coroutine and OpMode paradigm.

Each example directory contains its own `README.md` with detailed instructions on setup, driver controls, autonomous routines, simulation, and live tuning.
