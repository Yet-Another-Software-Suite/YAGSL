---
description: A swerve drive can be built wrong in more places than you'd think...
---

# Common build issues

Many software problems are really build problems. This is a lookup of common build issues.

It's important to keep in mind that SEVERAL of these issues could be present on your robot at once.

## Swerve module

**Drive**

* [ ] Wrong pinion installed; some kits come with both 12T and 14T.
* [ ] One module has a different gear ratio than the others.
* [ ] Wheel tread is worn or replaced.
* [ ] Gears need grease.

**Azimuth**

* [ ] Module doesn't turn freely.
* [ ] Bearing is missing, cracked, or crooked.
* [ ] Module doesn't match the manufacturer's guide.

**Absolute encoder**

* [ ] Magnet is not secured and slips; loctite/glue it in.
* [ ] Encoder is too far from the magnet. Check the vendor's documentation.
* [ ] Encoder is not secured to the module.

**Motor**

* [ ] Motor doesn't have a firm connection to the module shaft.
* [ ] Motor mounting screws are loose.
* [ ] Motor is dying.

## Wiring

* [ ] CAN wire is loose or broken.
* [ ] CAN bus isn't terminated.
* [ ] Device is wired to a different CAN bus than configured.
* [ ] Encoder cable is loose or in the wrong port.
* [ ] Power leads are loose or poorly crimped.

## Gyroscope

* [ ] Gyro isn't mounted firmly.
* [ ] Gyro isn't mounted flat.

## Robot

* [ ] Battery is worn out.
* [ ] Frame isn't square.
* [ ] Robot center of gravity is not ideal.
* [ ] SystemCore is nearly out of storage.
