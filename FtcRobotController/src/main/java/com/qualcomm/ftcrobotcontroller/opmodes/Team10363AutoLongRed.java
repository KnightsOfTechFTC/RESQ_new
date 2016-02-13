package com.qualcomm.ftcrobotcontroller.opmodes;

/**
 * Created by Lego5 on 12/6/2015.
 */
public class Team10363AutoLongRed extends PushBotTelemetry {
    public Team10363AutoLongRed ()

    {
        //
        // Initialize base classes.
        //
        // All via self-construction.

        //
        // Initialize class members.
        //
        // All via self-construction.

    } // PushBotAuto

    //--------------------------------------------------------------------------
    //
    // start
    //
    /**
     * Perform any actions that are necessary when the OpMode is enabled.
     *
     * The system calls this member once when the OpMode is enabled.
     */
    @Override public void start ()

    {
        //
        // Call the PushBotHardware (super/base class) start method.
        //
        super.start ();

        // Reset the motor encoders on the drive wheels.
        //
        reset_drive_encoders ();
        //
        // Reset the motor encoders on the drive wheels.
        //
        reset_drive_encoders ();

    } // start

    //--------------------------------------------------------------------------
    //
    // loop
    //
    /**
     * Implement a state machine that controls the robot during auto-operation.
     * The state machine uses a class member and encoder input to transition
     * between states.
     *
     * The system calls this member repeatedly while the OpMode is running.
     */
    @Override public void loop ()

    {
        //----------------------------------------------------------------------
        //
        // State: Initialize (i.e. state_0).
        switch (v_state) {
            //
            // Synchronize the state machine and hardware.
            //
            case 0:
                //
                // Reset the encoders to ensure they are at a known good value.
                //
                reset_drive_encoders();
                if (a_gyro_heading() < 180) {
                    zeroheading = a_gyro_heading();
                } else {
                    zeroheading = a_gyro_heading() - 360;
                }
                sensorRGBLeft.setI2cAddress(0x42);
                sensorRGBRight.setI2cAddress(0x44);
                sensorRGBBeacon.setI2cAddress(0x3C);
                //
                // Reset the encoders to ensure they are at a known good value.
                //
                sensorRGBBeacon.enableLed(false);
                sensorRGBLeft.enableLed(true);
                sensorRGBRight.enableLed(true);
                clean_beacon(.7);


                //
                // Transition to the next state when this method is called again.
                //
                v_state++;

                break;
            //
            // Drive forward until the encoders exceed the specified values.
            //
            case 1://straight towards floor goal

                //
                // Tell the system that motor encoders will be used.  This call MUST
                // be in this state and NOT the previous or the encoders will not
                // work.  It doesn't need to be in subsequent states.
                //
                run_using_encoders();


                //
                // Start the drive wheel motors at half power.
                //
                adjspeed = .5 * Math.sin(((2 * Math.PI) / 360) * (a_gyro_heading() - zeroheading));
                set_drive_power(.25f - adjspeed, .25f + adjspeed);
                m_holder_position(.6);

                //
                // Have the motor shafts turned the required amount?
                //
                // If they haven't, then the op-mode remains in this state (i.e this
                // block will be executed the next time this method is called).
                //
                if (have_drive_encoders_reached(12500, 12500) || a_left_red() > 2) {
                    if (have_drive_encoders_reached(10363, 10363)) {
                        // Reset the encoders to ensure they are at a known good value.
                        //

                        //
                        // Stop the motors.
                        //
                        left_encoder_pos = a_left_encoder_count();
                        right_encoder_pos = a_right_encoder_count();
                        set_drive_power(-0.2f, -0.2f);
                        if (anti_have_drive_encoders_reached(left_encoder_pos - 100, right_encoder_pos - 100)) {
                            set_drive_power(0, 0);
                        }

                        left_encoder_pos = a_left_encoder_count();
                        right_encoder_pos = a_right_encoder_count();

                        //
                        // Transition to the next state when this method is called
                        // again.

                        v_state++;
                    }
                }
                break;
            //
            // Wait...
            //
            case 2:
                // Update common telemetry
                update_telemetry();
                telemetry.addData("19", "LeftEncoderPos: " + left_encoder_pos);
                telemetry.addData("20", "RightEncoderPos: " + right_encoder_pos);
                //Set the right wheel backwards

                set_drive_power(-0.1f, 0.1f);
                //Same as before, but with the left wheel backwards and a little bit of extra goodness to prevent any bugs
                //        if (a_gyro_heading()<=315+zeroheading || sensorRGBLeft.alpha()>8) {
                if (sensorRGBLeft.alpha() > 8) {
                    set_drive_power(0.0f, 0.0f);

                    left_encoder_pos = a_left_encoder_count();
                    right_encoder_pos = a_right_encoder_count();

                    v_state++;
                }

                break;

            case 3://Find line with right sensor
                update_telemetry();

                set_drive_power(0.0f, 0.2f);
                if (sensorRGBRight.alpha() > 8) {
                    set_drive_power(0.0f, 0.0f);

                    left_encoder_pos = a_left_encoder_count();
                    right_encoder_pos = a_right_encoder_count();

                    v_state++;
                }

                break;

            case 4://Move to left side
                update_telemetry();

                set_drive_power(0.2f, 0.2f);
                if (have_drive_encoders_reached(left_encoder_pos+600,right_encoder_pos+600)) {
                    set_drive_power(0.0f, 0.0f);

                    left_encoder_pos = a_left_encoder_count();
                    right_encoder_pos = a_right_encoder_count();

                    v_state++;
                }

                break;

            case 5://Go towards the bin
                update_telemetry();
                telemetry.addData("19", "LeftEncoderPos: " + left_encoder_pos);
                telemetry.addData("20", "RightEncoderPos: " + right_encoder_pos);

                //        adjspeed=Math.sin(((2*Math.PI)/360)*(a_gyro_heading()+(315+zeroheading)));
                //        set_drive_power(0.2f-adjspeed,0.2f+adjspeed);
                if (sensorRGBRight.alpha() > 8) {
                    set_drive_power(.15, .15);
                } else if (a_gyro_heading() > 315 + zeroheading) {
                    set_drive_power(0, .2);
                } else if (a_gyro_heading() < 315 + zeroheading) {
                    set_drive_power(.2, 0);
                } else {
                    set_drive_power(.2, .2);
                }
                m_holder_position(.8);
                if (a_right_encoder_count() - right_encoder_pos + a_left_encoder_count() - left_encoder_pos > 4000) {
                    set_drive_power(0.0f, 0.0f);
                    left_encoder_pos = a_left_encoder_count();
                    right_encoder_pos = a_right_encoder_count();
                    BeaconBlue = sensorRGBBeacon.blue();
                    BeaconRed = sensorRGBBeacon.red();

                    v_state++;
                }
                break;

            case 6://Drop the servos holding the climbers

                m_holder_position(.95);


                //int x=0;
                //while (x<=10)
                //{
                //    m_holder_position(.1*x);
                //    x=x+1;
                //}
                left_encoder_pos = a_left_encoder_count();
                right_encoder_pos = a_right_encoder_count();
                //Since the normal wait code had weird problems, this essentially does the same thing except with sleep
                if (a_holder_position() >= .61 && a_holder_position() <= .8) {
                    v_state++;
                }
                //double timeToWaitFor = System.currentTimeMillis() + 3000;
                //while (timeToWaitFor > System.currentTimeMillis()) {}

                break;

            case 7://Go backwards, ensuring that the servos are in the bin

                set_drive_power(-0.2f, -0.2f);
                clean_beacon(.3);

                if (anti_have_drive_encoders_reached(left_encoder_pos - 1440, right_encoder_pos - 1440)) {

                    set_drive_power(0.0f, 0.0f);
                    m_holder_position(.3);
                    left_encoder_pos = a_left_encoder_count();
                    right_encoder_pos = a_right_encoder_count();

                    v_state++;

                }

                break;

            case 8://Turning to press beacon button
                if (BeaconBlue >= 2 && BeaconRed < 2) {
                    set_drive_power(0.2, -0.2);
                    if (a_gyro_heading() >= 323 + zeroheading) {
                        left_encoder_pos = a_left_encoder_count();
                        right_encoder_pos = a_right_encoder_count();
                        clean_beacon(0);
                        stayinplace = false;
                        v_state++;
                    }
                } else if (BeaconRed >= 2 && BeaconBlue < 2) {
                    set_drive_power(0.2, -0.2);
                    if (a_gyro_heading() >= 318 + zeroheading) {
                        left_encoder_pos = a_left_encoder_count();
                        right_encoder_pos = a_right_encoder_count();
                        clean_beacon(0);
                        stayinplace = false;
                        v_state++;
                    }
                } else {
                        clean_beacon(1);
                        stayinplace = true;
                }


                break;

            case 9://Go forwards again, ensuring that the robot is in the square area
                
         //       update_telemetry ();
         //       telemetry.addData("21", "LeftEncoderPos: " + left_encoder_pos);
         //       telemetry.addData("22", "RightEncoderPos: " + right_encoder_pos);
                //m_hand_position(1);
                //m_hand_position(0);

                set_drive_power(0.2f,0.2f);

                if (have_drive_encoders_reached(left_encoder_pos+800,right_encoder_pos+800)|| stayinplace) {
                    set_drive_power(0.0f,0.0f);
                    clean_beacon(1);
                    v_state++;
                }
                break;




                // Turn left until the encoders exceed the specified values.
                //
       /* case 3:
            run_using_encoders ();
            set_drive_power (-1.0f, 1.0f);
            if (have_drive_encoders_reached (2880, 2880))
            {
                reset_drive_encoders ();
                set_drive_power (0.0f, 0.0f);
                v_state++;
            }
            break;
        //
        // Wait...
        //
        case 4:
            if (have_drive_encoders_reset ())
            {
                v_state++;
            }
            break;
        //
        // Turn right until the encoders exceed the specified values.
        //
        case 5:
            run_using_encoders ();
            set_drive_power (1.0f, -1.0f);
            if (have_drive_encoders_reached (2880, 2880))
            {
                reset_drive_encoders ();
                set_drive_power (0.0f, 0.0f);
                v_state++;
            }
            break;
        //
        // Wait...
        //
        case 6:
            if (have_drive_encoders_reset ())
            {
                v_state++;
            }
            break;
    */  ///
                // / Perform no action - stay in this case until the OpMode is stopped.
                // This method will still be called regardless of the state machine.
                //
            default:
                //
                // The autonomous actions have been accomplished (i.e. the state has
                // transitioned into its final state.)
                //
                clean_beacon(1);
                if (!have_drive_encoders_reset()) {
                    reset_drive_encoders();
                }
                break;
        }

        //
        // Send telemetry data to the driver station.
        //
        update_telemetry(); // Update common telemetry
        telemetry.addData("18", "State: " + v_state);
        telemetry.addData("80","gyro heading:"+a_gyro_heading());
        telemetry.addData("82","Adjspeed:"+adjspeed);
        telemetry.addData("83", "zero heading:" + zeroheading);
        telemetry.addData("84","Beacon Blue:"+sensorRGBBeacon.blue());
        telemetry.addData("85","Beacon Red:"+sensorRGBBeacon.red());
        telemetry.addData("86","Beacon Green:"+sensorRGBBeacon.green());
    } // loop

    //--------------------------------------------------------------------------
    //
    // v_state
    //
    /**
     * This class member remembers which state is currently active.  When the
     * start method is called, the state will be initialized (0).  When the loop
     * starts, the state will change from initialize to state_1.  When state_1
     * actions are complete, the state will change to state_2.  This implements
     * a state machine for the loop method.
     */
    private int v_state = 0;
    private boolean stayinplace=false;
    private double BeaconBlue =0;
    private double BeaconRed=0;
    private double adjspeed=0;
    private int zeroheading=0;
    private double left_encoder_pos = 0;
    private double right_encoder_pos = 0;
} // PushBotAuto

