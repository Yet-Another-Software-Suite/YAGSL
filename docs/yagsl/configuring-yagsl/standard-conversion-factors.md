# Standard Conversion Factors

This page serves a standard set of conversion factors for a few typical modules.

## How I found these?

```java
    // Angle conversion factor is 360 / (GEAR RATIO)
    //  In this case the gear ratio is 12.8 motor revolutions per wheel rotation.
    //  The encoder resolution per motor revolution is 1 per motor revolution.
    double angleConversionFactor = SwerveMath.calculateDegreesPerSteeringRotation(12.8);
    // Motor conversion factor is (PI * WHEEL DIAMETER IN METERS) / (GEAR RATIO).
    //  In this case the wheel diameter is 4 inches, which must be converted to meters to get meters/second.
    //  The gear ratio is 6.75 motor revolutions per wheel rotation.
    //  The encoder resolution per motor revolution is 1 per motor revolution.
    double driveConversionFactor = SwerveMath.calculateMetersPerRotation(Units.inchesToMeters(4), 6.75);
    System.out.println("\"conversionFactors\": {");
    System.out.println("\t\"angle\": {\"factor\": " + angleConversionFactor + "},");
    System.out.println("\t\"drive\": {\"factor\": " + driveConversionFactor + "}");
    System.out.println("}");
```

## MAX Swerve

{% tabs %}
{% tab title="12T" %}
```json
"conversionFactors": {
    "angle": {"gearRatio": 46.42},
    "drive": {"gearRatio": 5.50, "diameter": 3}
}
```
{% endtab %}

{% tab title="13T" %}
```json
"conversionFactors": {
    "angle": {"gearRatio": 46.42},
    "drive": {"gearRatio": 5.08, "diameter": 3}
}
```
{% endtab %}

{% tab title="14T" %}
```json
"conversionFactors": {
    "angle": {"gearRatio": 46.42},
    "drive": {"gearRatio": 4.71, "diameter": 3}
}
```
{% endtab %}
{% endtabs %}

## Swerve Drive Specialties (SDS)

{% tabs %}
{% tab title="MK4 L1" %}
```json
"conversionFactors": {
	"angle": {"gearRatio": 12.8},
	"drive": {"gearRatio": 8.14, "diameter": 4}
}
```
{% endtab %}

{% tab title="MK4 L2" %}
```json
"conversionFactors": {
	"angle": {"gearRatio": 12.8},
	"drive": {"gearRatio": 6.75, "diameter": 4}
}
```
{% endtab %}

{% tab title="MK4 L3" %}
```json
"conversionFactors": {
	"angle": {"gearRatio": 12.8},
	"drive": {"gearRatio": 6.12, "diameter": 4}
}
```
{% endtab %}

{% tab title="MK4 L4" %}
```json
"conversionFactors": {
	"angle": {"gearRatio": 12.8},
	"drive": {"gearRatio": 5.14, "diameter": 4}
}
```
{% endtab %}
{% endtabs %}

{% tabs %}
{% tab title="MK4i L1 " %}
```json
"conversionFactors": {
	"angle": {"gearRatio": 21.4285714286},
	"drive": {"gearRatio": 8.14, "diameter": 4}
}
```
{% endtab %}

{% tab title="MK4i L2" %}
```json
"conversionFactors": {
	"angle": {"gearRatio": 21.4285714286},
	"drive": {"gearRatio": 6.75, "diameter": 4}
}
```
{% endtab %}

{% tab title="MK4i L3" %}
```json
"conversionFactors": {
	"angle": {"gearRatio": 21.4285714286},
	"drive": {"gearRatio": 6.12, "diameter": 4}
}
```
{% endtab %}
{% endtabs %}

{% tabs %}
{% tab title="MK5i R1" %}
```json
"conversionFactors": {
	"angle": {"gearRatio": 26},
	"drive": {"gearRatio": 7.03, "diameter": 4}
}
```
{% endtab %}

{% tab title="MK5i R2" %}
```json
"conversionFactors": {
	"angle": {"gearRatio": 26},
	"drive": {"gearRatio": 6.03, "diameter": 4}
}
```
{% endtab %}

{% tab title="MK5i R3" %}
```json
"conversionFactors": {
	"angle": {"gearRatio": 26},
	"drive": {"gearRatio": 5.27, "diameter": 4}
}
```
{% endtab %}
{% endtabs %}

{% tabs %}
{% tab title="MK4n L1" %}
```json
"conversionFactors": {
	"angle": {"gearRatio": 18.75},
	"drive": {"gearRatio": 7.13, "diameter": 4}
}
```
{% endtab %}

{% tab title="MK4n L2" %}
```json
"conversionFactors": {
	"angle": {"gearRatio": 18.75},
	"drive": {"gearRatio": 5.9, "diameter": 4}
}
```
{% endtab %}

{% tab title="MK4n L3" %}
```json
"conversionFactors": {
	"angle": {"gearRatio": 18.75},
	"drive": {"gearRatio": 5.36, "diameter": 4}
}
```
{% endtab %}
{% endtabs %}

{% tabs %}
{% tab title="MK5n R1" %}
```json
"conversionFactors": {
	"angle": {"gearRatio": 26.09},
	"drive": {"gearRatio": 7.03, "diameter": 4}
}
```
{% endtab %}

{% tab title="MK5n R2" %}
```json
"conversionFactors": {
	"angle": {"gearRatio": 26.09},
	"drive": {"gearRatio": 6.03, "diameter": 4}
}
```
{% endtab %}

{% tab title="MK5n R3" %}
```json
"conversionFactors": {
	"angle": {"gearRatio": 26.09},
	"drive": {"gearRatio": 5.27, "diameter": 4}
}
```
{% endtab %}
{% endtabs %}

## Thrifty Swerve

{% tabs %}
{% tab title="18T" %}
<figure><img src="../.gitbook/assets/thriftswerve.PNG" alt=""><figcaption><p>Thrifty Swerve Gear Ratio Table</p></figcaption></figure>

The following example is for 18T Output Gear, and 12T pinion gear with a 3in wheel controlled by NEOs. Please refer to the chart above for your configuration.

The steering motor gear ratio is **25:1**

```json
"conversionFactors": {
	"angle": {"gearRatio": 25},
	"drive": {"gearRatio": 15, "diameter": 3}
}
```
{% endtab %}

{% tab title="16T" %}


<figure><img src="../.gitbook/assets/thriftswerve.PNG" alt=""><figcaption><p>Thrifty Swerve Gear Ratio Table</p></figcaption></figure>

The following example is for 16T Output Gear, and 12T pinion gear with a 3in wheel controlled by NEOs. Please refer to the chart above for your configuration.&#x20;

The steering motor gear ratio is **25:1**

```json
"conversionFactors": {
	"angle": {"gearRatio": 25},
	"drive": {"gearRatio": 16.9, "diameter": 3}
}
```
{% endtab %}
{% endtabs %}



## Plummer Industries

{% tabs %}
{% tab title="Corner Mid Ratio" %}
```json
"conversionFactors": {
	"angle": {"gearRatio": 28},
	"drive": {"gearRatio": 4, "diameter": 2.5}
}
```
{% endtab %}

{% tab title="Corner High Ratio" %}
```json
"conversionFactors": {
	"angle": {"gearRatio": 28},
	"drive": {"gearRatio": 3.25, "diameter": 2.5}
}
```
{% endtab %}
{% endtabs %}

{% tabs %}
{% tab title="Corner Mid Ratio - Linear" %}
```json
"conversionFactors": {
	"angle": {"gearRatio": 28},
	"drive": {"gearRatio": 4, "diameter": 2.5}
}
```
{% endtab %}

{% tab title="Corner High Ratio - Linear" %}
```json
"conversionFactors": {
	"angle": {"gearRatio": 28},
	"drive": {"gearRatio": 3.25, "diameter": 2.5}
}
```
{% endtab %}
{% endtabs %}



