package pack;

import java.util.Random;

import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Keys;
import lejos.hardware.Sound;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;
import lejos.utility.Delay;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorMode;

public class BumperCar {

	static MovePilot pilot;
	static Random ran;
	static TouchSensor touch;
	static SampleProvider ColorProvider;
	static float[] ColorSample;
	static Brick b = BrickFinder.getDefault();
	static Port s3 = b.getPort("S3");
	static EV3UltrasonicSensor us = new EV3UltrasonicSensor(s3);
	static Ultrasonic ultra = new Ultrasonic(us.getMode("Distance"));

	static EV3 ev3 = (EV3) BrickFinder.getLocal();
	static TextLCD lcd = ev3.getTextLCD();
	static Keys keys = ev3.getKeys();
	static EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S4);
	static SensorMode color = colorSensor.getColorIDMode();
	static float[] sample = new float[color.sampleSize()];

	public static void main(String[] args) {

		BumperCarRunner();

	}

	public static void BumperCarRunner() {
		Wheel wheel1 = WheeledChassis.modelWheel(Motor.B, 1.5).offset(-72);
		Wheel wheel2 = WheeledChassis.modelWheel(Motor.C, 1.5).offset(72);
		Chassis chassis = new WheeledChassis(new Wheel[] { wheel1, wheel2 }, WheeledChassis.TYPE_DIFFERENTIAL);
		pilot = new MovePilot(chassis);
		ran = new Random();
		Brick brick = BrickFinder.getDefault();
		Port s1 = brick.getPort("S1");
		EV3TouchSensor sensor = new EV3TouchSensor(s1);
		touch = new TouchSensor(sensor);

		pilot.setLinearSpeed(5);
		pilot.forward();

		while (Button.ESCAPE.isUp()) {
			// Delay.msDelay(5);
			float distance = ultra.distance();
			if (distance > 0.1) {
				pilot.setLinearSpeed(6);
			} // else if(distance < 0.2) {
				// pilot.setLinearSpeed(9);

			// }
			else {
				pilot.stop();
				pilot.rotate(5);
				pilot.setLinearSpeed(5);
				pilot.forward();

			}

			if (touch.pressed()) {
				Sound.buzz();
				Button.LEDPattern(5);
				pilot.stop();
				Delay.msDelay(2);
				Sound.beepSequenceUp();
				pilot.travel(-6);

				if (ran.nextBoolean()) {
					Button.LEDPattern(4);
					pilot.rotate(5);
				} else {
					Button.LEDPattern(4);
					pilot.rotate(-5);
				}
				pilot.setLinearSpeed(5);
				pilot.forward();
			}
			
			//We can use this code if we use threads
/*
			color.fetchSample(sample, 0);
			int colorId = (int) sample[0];
			String colorName = "";
			switch (colorId) {
			case Color.NONE:
				colorName = "NONE";
				break;

			case Color.BLUE:
				colorName = "BLUE";
				colorSensor.setFloodlight(1);
				break;
			case Color.GREEN:
				colorName = "GREEN";
				colorSensor.setFloodlight(2);
				break;

			case Color.RED:
				colorName = "RED";
				colorSensor.setFloodlight(3);
				break;

			}
			lcd.drawString(colorId + " - " + colorName, 0, 0);
			keys.waitForAnyPress();
*/
		}
		pilot.stop();
		us.close();
	}
}
