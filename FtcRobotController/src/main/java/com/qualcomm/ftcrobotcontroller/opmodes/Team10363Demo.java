package com.qualcomm.ftcrobotcontroller.opmodes;
public class Team10363Demo extends PushBotTelemetry {
    public Team10363Demo ()

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
        dirt = true;
        switch (v_state)
        {
            //
            // Synchronize the state machine and hardware.
            //
            case 0:
                // set i2c addresses.
                sensorRGBLeft.setI2cAddress(0x42);
                sensorRGBRight.setI2cAddress(0x44);
                sensorRGBBeacon.setI2cAddress(0x3C);
                //
                // Reset the encoders to ensure they are at a known good value.
                //
                reset_drive_encoders();
                //Turns on and off LEDs.
                sensorRGBBeacon.enableLed(false);
                sensorRGBLeft.enableLed(true);
                sensorRGBRight.enableLed(true);
                //Moves beacon cleaner out.
                clean_beacon(.7);
                toofar=false;
                // Detect problems with color sensors. If there are problems, they will give readings of 0, which triggers the flag
                if (sensorRGBRight.red()==0&&sensorRGBRight.blue()==0&&sensorRGBRight.green()==0){colorproblems=true;}
                else if (sensorRGBLeft.red()==0&&sensorRGBLeft.blue()==0&&sensorRGBLeft.green()==0){colorproblems=true;}
                else {colorproblems=false;}
                //
                // Transition to the next state when this method is called again.
                //
                if (a_gyro_heading()<180) {
                    tempGyro = a_gyro_heading();
                }
                else {tempGyro=a_gyro_heading()-360;}
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
                run_using_encoders ();
                // We all love straightDrive, don't we?
                adjspeed=.5*Math.sin(((2*Math.PI)/360)*(a_gyro_heading()-tempGyro));
                set_drive_power (.25f-adjspeed, .25f+adjspeed);

                //
                // Have the motor shafts turned the required amount?
                //
                // If they haven't, then the op-mode remains in this state (i.e this
                // block will be executed the next time this method is called).
                //
                if (sensorRGBLeft.alpha()>=8||have_drive_encoders_reached(10363,10363))
                {
                    //
                    // Reset the encoders to ensure they are at a known good value.
                    //

                    //
                    // Stop the motors.
                    //
                    set_drive_power (0.0f, 0.0f);

                    // Transition to the next state when this method is called
                    // again.

                    v_state++;
                }
                break;
            case 2:
                set_drive_power(0.25f,-0.25f);
                if (a_gyro_heading()>=90){
                    set_drive_power(0.0f,0.0f);
                    v_state++;
                    leftEncoderPos=a_left_encoder_count();
                    rightEncoderPos=a_right_encoder_count();
                }
                break;
            case 3:
                if (sensorRGBLeft.alpha()>8){
                    set_drive_power(0.2,0.2);}
                else if (a_gyro_heading()>90+tempGyro){set_drive_power(0,.2);}
                else if (a_gyro_heading()<90+tempGyro){set_drive_power(.2,0);}
                else {set_drive_power(.2,.2);}
                if (sensorRGBLeft.blue()>=2||have_drive_encoders_reached(5000+leftEncoderPos,5000+rightEncoderPos)){
                    set_drive_power(0.0f,0.0f);
                    v_state++;
                }
                break;

                //


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
                if (!have_drive_encoders_reset()) {
                    reset_drive_encoders();
                }
                break;
        }

        //
        // Send telemetry data to the driver station.
        //
        update_telemetry (); // Update common telemetry
        telemetry.addData ("18", "State: " + v_state);
        telemetry.addData("36", "adjspeed: "+adjspeed);
        telemetry.addData("88", "Left Alpha: "+ sensorRGBLeft.alpha());
        telemetry.addData("81", "Left Blue: "+sensorRGBLeft.blue());

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
    private double left_encoder_pos = 0;
    private double right_encoder_pos = 0;
    private boolean dirt = true;
    private boolean colorproblems=false;
    private boolean toofar=false;
    private double tempGyro=0;
    private int leftEncoderPos=0;
    private int rightEncoderPos=0;
    private double adjspeed=0;
} // PushBotAuto

