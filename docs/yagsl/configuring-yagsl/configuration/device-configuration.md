# Device Configuration

## Device Configuration

All devices in a swerve drive come down to a basic set of fields. The device configuration is used to store and create those devices during parsing with a 1:1 mapping to [`DeviceJson`](https://docs.yagsl.com/configuring-yagsl/configuration/device-configuration).

## Fields

<table data-full-width="true"><thead><tr><th>Name</th><th>Units</th><th>Required</th><th>Description</th></tr></thead><tbody><tr><td><code>type</code></td><td><a href="../../devices/absolute-encoders.md#possible-absolute-encoder-types">Encoder <code>type</code></a><br><a href="../../devices/gyroscope.md#possible-gyroscope-types">IMU <code>type</code></a><br><a href="../../devices/motor-controllers/#possible-motor-controller-types">Motor <code>type</code></a></td><td>Y</td><td>The device type which is used for creation of the Swerve type.</td></tr><tr><td><code>id</code></td><td>Integer</td><td>Y</td><td>The ID of the device on the CANBus, or the pin ID on the roboRIO for certain devices.</td></tr><tr><td><code>canbus</code></td><td>String</td><td>N</td><td>The canbus to instantiate the device on. Only works on devices compatible with alternate CAN buses.<br><br>When the <code>type</code> is <code>sparkmax_brushed</code> this defines the encoder attached to the motor which is required for drive motors, angle motors should also use them if there is no attached absolute encoder. The valid values in this case are <code>greyhill_63r256</code>, <code>srx_mag_encoder</code>, <code>throughbore</code>, <code>throughbore_dataport</code>, <code>greyhill_63r256_dataport</code>, <code>srx_mag_encoder_dataport</code>.<br>DO NOT SET THIS IF THE ATTACHED ENCODER IS AN ABSOLUTE ENCODER</td></tr></tbody></table>

## Useful tips

1. Optimize your IMU if it isn't as accurate as you hope, NavX's have this nice guide [here](https://pdocs.kauailabs.com/navx-mxp/guidance/best-practices/)
2. Read the code! If you can't understand something all of the code is documented and easy to read.
