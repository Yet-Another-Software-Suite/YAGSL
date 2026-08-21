package swervelib.parser;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;

class SwerveParserTest {

  // -- Helper: resolve bundled test-resources directory --------------------

  /**
   * Returns the {@code swervedrive} directory bundled under {@code src/test/resources/swervedrive}
   * via the classpath.
   */
  private File testResourceDir() {
    URL url = getClass().getClassLoader().getResource("swervedrive");
    assertNotNull(url, "swervedrive test-resource directory not found on classpath");
    return new File(url.getFile());
  }

  // -- Helper: build a minimal config directory on the fly -----------------

  private File createTestConfigDir() throws IOException {
    File dir = Files.createTempDirectory("yagsl-test").toFile();
    File modulesDir = new File(dir, "modules");
    modulesDir.mkdirs();

    writeFile(
        new File(dir, "swervedrive.json"),
        "{\"gyro\":{\"type\":\"pigeon2_can\",\"id\":0,\"canbus\":\"\"},"
            + "\"gyroAxis\":\"yaw\",\"gyroInvert\":false,"
            + "\"modules\":[\"fl.json\",\"fr.json\",\"bl.json\",\"br.json\"]}");

    writeFile(
        new File(modulesDir, "pidfproperties.json"),
        "{\"drive\":{\"p\":0.5,\"i\":0.0,\"d\":0.01},"
            + "\"angle\":{\"p\":2.0,\"i\":0.0,\"d\":0.1}}");

    writeFile(
        new File(modulesDir, "physicalproperties.json"),
        "{\"gearing\":{\"drive\":{\"gearRatio\":6.75,\"diameter\":4.0},"
            + "\"angle\":{\"gearRatio\":12.8}},"
            + "\"statorCurrentLimit\":{\"drive\":40,\"angle\":20}}");

    String moduleTemplate =
        "{\"drive\":{\"type\":\"sparkmax_neo\",\"id\":%d,\"canbus\":\"\"},"
            + "\"angle\":{\"type\":\"sparkmax_neo\",\"id\":%d,\"canbus\":\"\"},"
            + "\"absoluteEncoder\":{\"type\":\"revthroughbore_attached\",\"id\":0,\"channel\":0,\"canbus\":\"\"},"
            + "\"inverted\":{\"drive\":false,\"angle\":false},"
            + "\"absoluteEncoderOffset\":0.0,"
            + "\"location\":{\"front\":%s,\"left\":%s}}";
    writeFile(new File(modulesDir, "fl.json"), String.format(moduleTemplate, 1, 2, "12.0", "12.0"));
    writeFile(
        new File(modulesDir, "fr.json"), String.format(moduleTemplate, 3, 4, "12.0", "-12.0"));
    writeFile(
        new File(modulesDir, "bl.json"), String.format(moduleTemplate, 5, 6, "-12.0", "12.0"));
    writeFile(
        new File(modulesDir, "br.json"), String.format(moduleTemplate, 7, 8, "-12.0", "-12.0"));

    return dir;
  }

  private File createTestConfigDirWithGyroAxis(String gyroAxis) throws IOException {
    File dir = Files.createTempDirectory("yagsl-gyroaxis-test").toFile();
    File modulesDir = new File(dir, "modules");
    modulesDir.mkdirs();

    writeFile(
        new File(dir, "swervedrive.json"),
        "{\"gyro\":{\"type\":\"pigeon2_can\",\"id\":0,\"canbus\":\"\"},"
            + "\"gyroAxis\":\""
            + gyroAxis
            + "\",\"gyroInvert\":false,"
            + "\"modules\":[\"fl.json\"]}");

    writeFile(
        new File(modulesDir, "pidfproperties.json"),
        "{\"drive\":{\"p\":0.5,\"i\":0.0,\"d\":0.01},"
            + "\"angle\":{\"p\":2.0,\"i\":0.0,\"d\":0.1}}");

    writeFile(
        new File(modulesDir, "physicalproperties.json"),
        "{\"gearing\":{\"drive\":{\"gearRatio\":6.75,\"diameter\":4.0},"
            + "\"angle\":{\"gearRatio\":12.8}},"
            + "\"statorCurrentLimit\":{\"drive\":40,\"angle\":20}}");

    writeFile(
        new File(modulesDir, "fl.json"),
        "{\"drive\":{\"type\":\"sparkmax_neo\",\"id\":1,\"canbus\":\"\"},"
            + "\"angle\":{\"type\":\"sparkmax_neo\",\"id\":2,\"canbus\":\"\"},"
            + "\"absoluteEncoder\":{\"type\":\"revthroughbore_attached\",\"id\":0,\"channel\":0,\"canbus\":\"\"},"
            + "\"inverted\":{\"drive\":false,\"angle\":false},"
            + "\"absoluteEncoderOffset\":0.0,"
            + "\"location\":{\"front\":12.0,\"left\":12.0}}");

    return dir;
  }

  private void writeFile(File file, String content) throws IOException {
    try (java.io.FileWriter fw = new java.io.FileWriter(file)) {
      fw.write(content);
    }
  }

  // -- testParsesSwervedrive ------------------------------------------------

  @Test
  void testParsesSwervedriveNotNull() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir);
    assertNotNull(SwerveParser.swerveDriveJson);
  }

  @Test
  void testParsesSwervedriveGyroType() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir)
    assertEquals("pigeon2_can", SwerveParser.swerveDriveJson.gyro.type);
  }

  @Test
  void testParsesSwervedriveGyroId() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir)
    assertEquals(0, SwerveParser.swerveDriveJson.gyro.id);
  }

  @Test
  void testParsesSwervedriveGyroAxis() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir)
    assertEquals("yaw", SwerveParser.swerveDriveJson.gyroAxis);
  }

  @Test
  void testParsesSwervedriveGyroInvert() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir)
    assertFalse(SwerveParser.swerveDriveJson.gyroInvert);
  }

  @Test
  void testParsesSwervedriveModulesLength() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir)
    assertEquals(4, SwerveParser.swerveDriveJson.modules.length);
  }

  // -- testParsesPidfProperties ---------------------------------------------

  @Test
  void testParsesPidfPropertiesNotNull() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir)
    assertNotNull(SwerveParser.pidfPropertiesJson);
  }

  @Test
  void testParsesPidfDriveP() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir)
    assertEquals(0.5, SwerveParser.pidfPropertiesJson.drive.p, 0.001);
  }

  @Test
  void testParsesPidfDriveI() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir)
    assertEquals(0.0, SwerveParser.pidfPropertiesJson.drive.i, 0.001);
  }

  @Test
  void testParsesPidfDriveD() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir)
    assertEquals(0.01, SwerveParser.pidfPropertiesJson.drive.d, 0.001);
  }

  @Test
  void testParsesPidfAngleP() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir)
    assertEquals(2.0, SwerveParser.pidfPropertiesJson.angle.p, 0.001);
  }

  @Test
  void testParsesPidfAngleD() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir)
    assertEquals(0.1, SwerveParser.pidfPropertiesJson.angle.d, 0.001);
  }

  // -- testParsesPhysicalProperties ----------------------------------------

  @Test
  void testParsesPhysicalPropertiesNotNull() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir)
    assertNotNull(SwerveParser.physicalPropertiesJson);
  }

  @Test
  void testParsesPhysicalDriveGearRatio() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir)
    assertEquals(6.75, SwerveParser.physicalPropertiesJson.gearing.drive.gearRatio, 0.001);
  }

  @Test
  void testParsesPhysicalDriveDiameter() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir)
    assertEquals(4.0, SwerveParser.physicalPropertiesJson.gearing.drive.diameter, 0.001);
  }

  @Test
  void testParsesPhysicalAngleGearRatio() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir)
    assertEquals(12.8, SwerveParser.physicalPropertiesJson.gearing.angle.gearRatio, 0.001);
  }

  @Test
  void testParsesPhysicalStatorCurrentLimitDrive() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir)
    assertEquals(40, SwerveParser.physicalPropertiesJson.statorCurrentLimit.drive);
  }

  @Test
  void testParsesPhysicalStatorCurrentLimitAngle() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir)
    assertEquals(20, SwerveParser.physicalPropertiesJson.statorCurrentLimit.angle);
  }

  // -- testParsesAllModules -------------------------------------------------

  @Test
  void testParsesAllModulesNotNull() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir)
    assertNotNull(SwerveParser.moduleJsons);
  }

  @Test
  void testParsesAllModulesLength() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir)
    assertEquals(4, SwerveParser.moduleJsons.length);
  }

  @Test
  void testParsesFlModuleDriveType() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir)
    assertEquals("sparkmax_neo", SwerveParser.moduleJsons[0].drive.type);
  }

  @Test
  void testParsesFlModuleDriveId() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir)
    assertEquals(1, SwerveParser.moduleJsons[0].drive.id);
  }

  @Test
  void testParsesFlModuleLocationFront() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir)
    assertEquals(12.0, SwerveParser.moduleJsons[0].location.front, 0.001);
  }

  @Test
  void testParsesFlModuleLocationLeft() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir)
    assertEquals(12.0, SwerveParser.moduleJsons[0].location.left, 0.001);
  }

  @Test
  void testParsesBrModuleDriveId() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir)
    assertEquals(7, SwerveParser.moduleJsons[3].drive.id);
  }

  @Test
  void testParsesBrModuleLocationFront() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir)
    assertEquals(-12.0, SwerveParser.moduleJsons[3].location.front, 0.001);
  }

  @Test
  void testParsesBrModuleLocationLeft() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir)
    assertEquals(-12.0, SwerveParser.moduleJsons[3].location.left, 0.001);
  }

  // -- testModuleDefaultValues ----------------------------------------------

  @Test
  void testModuleDefaultAbsoluteEncoderInverted() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir)
    assertFalse(SwerveParser.moduleJsons[0].absoluteEncoderInverted);
  }

  @Test
  void testModuleDefaultAbsoluteEncoderGearRatio() throws IOException {
    File dir = createTestConfigDir();
    SwerveParser.parse(dir)
    assertEquals(1.0, SwerveParser.moduleJsons[0].absoluteEncoderGearRatio, 0.001);
  }

  // -- testMissingDirectoryThrowsException ---------------------------------

  @Test
  void testMissingDirectoryThrowsException() {
    File nonExistentDir =
        new File(System.getProperty("java.io.tmpdir"), "nonexistent-yagsl-test-dir");
    assertThrows(AssertionError.class, () -> SwerveParser.parse(nonExistentDir));
  }

  // -- testMissingSwervedriveJsonThrowsException ----------------------------

  @Test
  void testMissingSwervedriveJsonThrowsException() throws IOException {
    File dir = Files.createTempDirectory("yagsl-missing-test").toFile();
    // Intentionally omit swervedrive.json — only the modules dir present
    new File(dir, "modules").mkdirs();
    writeFile(
        new File(new File(dir, "modules"), "pidfproperties.json"),
        "{\"drive\":{\"p\":0.5,\"i\":0.0,\"d\":0.01},\"angle\":{\"p\":2.0,\"i\":0.0,\"d\":0.1}}");
    writeFile(
        new File(new File(dir, "modules"), "physicalproperties.json"),
        "{\"gearing\":{\"drive\":{\"gearRatio\":6.75,\"diameter\":4.0},"
            + "\"angle\":{\"gearRatio\":12.8}},"
            + "\"statorCurrentLimit\":{\"drive\":40,\"angle\":20}}");
    assertThrows(AssertionError.class, () -> SwerveParser.parse(dir));
  }

  // -- testGyroAxisValues ---------------------------------------------------

  @Test
  void testGyroAxisYawParsed() throws IOException {
    File dir = createTestConfigDirWithGyroAxis("yaw");
    SwerveParser.parse(dir)
    assertEquals("yaw", SwerveParser.swerveDriveJson.gyroAxis);
  }

  @Test
  void testGyroAxisPitchParsed() throws IOException {
    File dir = createTestConfigDirWithGyroAxis("pitch");
    SwerveParser.parse(dir)
    assertEquals("pitch", SwerveParser.swerveDriveJson.gyroAxis);
  }

  @Test
  void testGyroAxisRollParsed() throws IOException {
    File dir = createTestConfigDirWithGyroAxis("roll");
    SwerveParser.parse(dir)
    assertEquals("roll", SwerveParser.swerveDriveJson.gyroAxis);
  }

  // -- Resource-based sanity: parse the bundled test config -----------------

  @Test
  void testResourceDirSwerveDriveNotNull() throws IOException {
    SwerveParser.parse(testResourceDir());
    assertNotNull(SwerveParser.swerveDriveJson);
  }

  @Test
  void testResourceDirPidfNotNull() throws IOException {
    SwerveParser.parse(testResourceDir());
    assertNotNull(SwerveParser.pidfPropertiesJson);
  }

  @Test
  void testResourceDirPhysicalPropertiesNotNull() throws IOException {
    SwerveParser.parse(testResourceDir());
    assertNotNull(SwerveParser.physicalPropertiesJson);
  }

  @Test
  void testResourceDirModuleJsonsNotNull() throws IOException {
    SwerveParser.parse(testResourceDir());
    assertNotNull(SwerveParser.moduleJsons);
  }

  @Test
  void testResourceDirModuleCount() throws IOException {
    SwerveParser.parse(testResourceDir());
    assertEquals(4, SwerveParser.moduleJsons.length);
  }

  @Test
  void testResourceDirFlDriveId() throws IOException {
    SwerveParser.parse(testResourceDir());
    assertEquals(1, SwerveParser.moduleJsons[0].drive.id);
  }

  @Test
  void testResourceDirBrDriveId() throws IOException {
    SwerveParser.parse(testResourceDir());
    assertEquals(7, SwerveParser.moduleJsons[3].drive.id);
  }
}
