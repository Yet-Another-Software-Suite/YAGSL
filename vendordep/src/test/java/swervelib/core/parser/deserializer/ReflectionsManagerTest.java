package swervelib.core.parser.deserializer;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

/**
 * Every vendor class {@link ReflectionsManager} loads by name has to exist. They are checked against the source files,
 * because a class whose vendor library has no 2027 release, such as AndyMark's, is left out of the build.
 */
class ReflectionsManagerTest {

  private static final File REFLECTIONS_SOURCE =
      new File(
          System.getProperty("user.dir"),
          "../yagsl/java/swervelib/core/parser/deserializer/reflections");

  private static void assertSourceExists(Enum<?> constant) throws ReflectiveOperationException {
    Field field = constant.getDeclaringClass().getDeclaredField("className");
    field.setAccessible(true);
    String className = (String) field.get(constant);
    String simpleName = className.substring(className.lastIndexOf('.') + 1);
    if (simpleName.isEmpty()) {
      return;
    }
    assertTrue(
        new File(REFLECTIONS_SOURCE, simpleName + ".java").isFile(),
        constant + " loads " + className + ", which has no source file in " + REFLECTIONS_SOURCE);
  }

  @Test
  void everyAbsoluteEncoderClassExists() throws ReflectiveOperationException {
    assertTrue(REFLECTIONS_SOURCE.isDirectory(), REFLECTIONS_SOURCE + " not found");
    for (var encoder : ReflectionsManager.AbsoluteEncoder.values()) {
      assertSourceExists(encoder);
    }
  }

  @Test
  void everyMotorControllerClassExists() throws ReflectiveOperationException {
    for (var motorController : ReflectionsManager.VendorMotorController.values()) {
      assertSourceExists(motorController);
    }
  }

  @Test
  void everyGyroClassExists() throws ReflectiveOperationException {
    for (var gyro : ReflectionsManager.Gyro.values()) {
      assertSourceExists(gyro);
    }
  }
}
