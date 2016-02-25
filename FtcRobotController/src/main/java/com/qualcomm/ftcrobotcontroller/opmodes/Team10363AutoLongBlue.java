package com.qualcomm.ftcrobotcontroller.opmodes;

//------------------------------------------------------------------------------
//
// PushBotAuto
//

import com.qualcomm.robotcore.hardware.GyroSensor;

import static java.lang.Thread.sleep;

/**
 * Provide a basic autonomous operational mode that uses the left and right
 * drive motors and associated encoders implemented using a state machine for
 * the Push Bot.
 *
 * @author SSI Robotics
 * @version 2015-08-01-06-01
 */
public class Team10363AutoLongBlue extends PushBotTelemetry

{

    //--------------------------------------------------------------------------
    //
    // PushBotAuto
    //
    /**
     * Construct the class.
     *
     * The system calls this member when the class is instantiated.
     */
    public Team10363AutoLongBlue ()

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


        // e drive wheels.
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


            //
            // Start the drive wheel motors at half power.
            //
            //
            //Corrected by Gyro
            //

            /*straightDrive: sets individual motor speeds based on clockwise gyro heading and some
            calculations I came up with during a match after I was asked to add correction. Note that
            it converts degrees to radians, subtracts tempGyro (the zero heading set in case 0) from the total,
            and will go in circles if you input the counter clockwise heading (or how it works in trig).
             */
            adjspeed=.5*Math.sin(((2*Math.PI)/360)*(a_gyro_heading()-tempGyro));
            //move climber holders
            m_holder_position(.6);
            //set adjusted drive power
            set_drive_power (.25f-adjspeed, .25f+adjspeed);
    //        right_led_on();


            //
            // Have the motor shafts turned the required amount or has Robert seen blue?
            //
            // If they haven't, then the op-mode remains in this state (i.e this
            // block will be executed the next time this method is called).
            //
            if (have_drive_encoders_reached(12300,12300)|| a_right_blue()>=2)

            {
                //minimal distance (to avoid accidental triggering)
                if (have_drive_encoders_reached(10363,10363)){
                //.
                //
                    //If Robert did not see blue, trigger the toofar flag.
                    if (have_drive_encoders_reached(12300,12300)){toofar=true;}
                //
                // Stop the motors.
                //
                    set_drive_power(0.0f, 0.0f);
                    /*
                    left_encoder_pos and right_encoder_pos: these are used
                    in later states to find the change in encoder_count
                    */
                    left_encoder_pos=a_left_encoder_count();
                    right_encoder_pos=a_right_encoder_count();
                //
                // Transition to the next state when this method is called
                // again.

                    v_state++;
            }}
            //If color sensors have problems (as I discussed in state 0), run based on encoders
            if (colorproblems&&have_drive_encoders_reached(11000,11000)){
                set_drive_power(0.0f,0.0f);
                left_encoder_pos=a_left_encoder_count();
                right_encoder_pos=a_right_encoder_count();
                v_state++;
            }
            break;
        //
            //these states should only work if toofar is triggered. If it isn't, then these states should be skipped.
            case 2:
                if (toofar){
                    set_drive_power(-0.2f,0.2f);
                    if (a_gyro_heading()>=135+tempGyro&&a_gyro_heading()<180+tempGyro){
                        set_drive_power(0.0f,0.0f);
                        v_state++;}
                }
                else {v_state++;}
                break;
            case 3:
                if (toofar){
                    set_drive_power(0.2f,0.2f);
                    if (sensorRGBRight.alpha()>8||have_drive_encoders_reached(1440,1440)){
                        set_drive_power(0.0f,0.0f);
                        v_state++;
                    }
                }
                else {v_state++;}
                break;
            case 4:
                if (toofar){
                    set_drive_power(-0.2f,0.2f);
                    if (a_gyro_heading()<10){
                        set_drive_power(0.0f,0.0f);
                        v_state++;
                    }
                }
        case 5:
             // Update common telemetry
            update_telemetry();
            telemetry.addData("19", "LeftEncoderPos: " + left_encoder_pos);
            telemetry.addData("20", "RightEncoderPos: " + right_encoder_pos);
            //Set the right wheel backwards
            //
    //        set_drive_power(0.0f, -0.2f);
            set_drive_power(0.1f, -0.1f);

            //Same as before, but with the right wheel backwards and a little bit of extra goodness to prevent any bugs
            if (sensorRGBRight.alpha()>8) {
                set_drive_power(0.0f, 0.0f);

                left_encoder_pos=a_left_encoder_count();
                right_encoder_pos=a_right_encoder_count();

                v_state++;
                }
                //avoiding color sensor i2c problems, like in case 1.
            if (colorproblems&&a_gyro_heading()>=45+tempGyro){
                set_drive_power(0.0f,0.0f);
                left_encoder_pos=a_left_encoder_count();
                right_encoder_pos=a_right_encoder_count();
                v_state++;
            }

            break;
            case 6://Go towards the bin
                update_telemetry();
                telemetry.addData("19", "LeftEncoderPos: " + left_encoder_pos);
                telemetry.addData("20", "RightEncoderPos: " + right_encoder_pos);


                //Avoids motors going faster than 1.
                if (adjspeed>.8){adjspeed=.8;}
                //Robert's Jukes. Also called LineFollower because it follows the white line based on gyro and color sensors.
                if (sensorRGBRight.alpha()>8){
                set_drive_power(0.2,0.2);}
                else if (a_gyro_heading()>45+tempGyro){set_drive_power(0,.2);}
                else if (a_gyro_heading()<45+tempGyro){set_drive_power(.2,0);}
                else {set_drive_power(.2,.2);}
                m_holder_position(.8);
                
                //leftEncoderProblems and rightEncoderProblems: if motors stall, skip to next state to avoid damage.
                //If encoder positions don't change, increase counter.
                if (leftEnconderProblems == a_left_encoder_count() && rightEnconderProblems == a_right_encoder_count()){
                    count = count + 1;
                }else {
                    count = 0;
                    leftEnconderProblems = a_left_encoder_count();
                    rightEnconderProblems = a_right_encoder_count();
                }
                // Is counter greater than 126 iters (3*42)? If so, skip to next state.
                if (count > 126){
                    set_drive_power(0.0f, 0.0f);
                    update_telemetry();
                    telemetry.addData("200", "Skipped case 6, we seemed to stay in one place");
                    left_encoder_pos = leftEnconderProblems;
                    right_encoder_pos = rightEnconderProblems;
                    BeaconBlue = sensorRGBBeacon.blue();
                    BeaconRed = sensorRGBBeacon.red();
                    v_state++;
                }

                if (a_left_encoder_count()-left_encoder_pos+a_right_encoder_count()-right_encoder_pos>=6000) {
                    set_drive_power(0.0f, 0.0f);
                    left_encoder_pos=a_left_encoder_count();
                    right_encoder_pos=a_right_encoder_count();
                    //Read the beacon light sensor
                    BeaconBlue = sensorRGBBeacon.blue();
                    BeaconRed = sensorRGBBeacon.red();
                    v_state++;

                    }
                break;
            case 7://Drop the servos holding the climbers

                m_holder_position(.95);

                //int x=0;
                //while (x<=10)
                //{
                //    m_holder_position(.1*x);
                //    x=x+1;
                //}
                left_encoder_pos=a_left_encoder_count();
                right_encoder_pos=a_right_encoder_count();
                //Since the normal wait code had weird problems, this essentially does the same thing except with sleep
                if (a_holder_position()>=.61 && a_holder_position()<= .8){v_state++;}
                //double timeToWaitFor = System.currentTimeMillis() + 3000;
                //while (timeToWaitFor > System.currentTimeMillis()) {}

            break;
            case 8://Go backwards, ensuring that the servos are in the bin

                set_drive_power(-0.2f,-0.2f);
                clean_beacon(.3);

                if (anti_have_drive_encoders_reached(left_encoder_pos-1440,right_encoder_pos-1440)) {

                    set_drive_power(0.0f, 0.0f);
                    //move holder back to original place
                    m_holder_position(.3);
                    left_encoder_pos=a_left_encoder_count();
                    right_encoder_pos=a_right_encoder_count();

                    v_state++;

                    //m_hand_position(1);
                    //m_hand_position(0);
                }

                break;

            case 9://Turning to press beacon button
                if (BeaconBlue >= 2 && BeaconRed<2){
            //    if (BeaconBlue > BeaconRed ){
                    set_drive_power(0.2,-0.2);
                    if (a_gyro_heading() >= 48+tempGyro){
                        left_encoder_pos=a_left_encoder_count();
                        right_encoder_pos=a_right_encoder_count();
                        clean_beacon(0);
                        stayinplace=false;
                        v_state++;
                    }
                } else if (BeaconRed>=2 && BeaconBlue<2) {
                    //    } else if (BeaconRed > BeaconBlue) {
                    set_drive_power(0.2, -0.2);
                    if (a_gyro_heading() >= 53 + tempGyro) {
                        left_encoder_pos = a_left_encoder_count();
                        right_encoder_pos = a_right_encoder_count();
                        clean_beacon(0);
                        stayinplace = false;
                        v_state++;
                    }
                } else {
                        clean_beacon(1);
                        //stayinplace: flag to skip case 10.
                        stayinplace=true;
                }

                break;

            case 10://Go forwards again to press beacon button



             //   update_telemetry ();
             //   telemetry.addData("21", "LeftEncoderPos: " + left_encoder_pos);
             //   telemetry.addData("22", "RightEncoderPos: " + right_encoder_pos);
                //m_hand_position(1);
                //m_hand_position(0);

                set_drive_power(0.2f,0.2f);

                if (have_drive_encoders_reached(left_encoder_pos+800,right_encoder_pos+800)|| stayinplace) {
                    set_drive_power(0.0f, 0.0f);
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
        telemetry.addData("81","right blue:"+a_right_blue());
        telemetry.addData("82","Adjspeed:"+adjspeed);
        telemetry.addData("83","tempGyro:"+tempGyro);
        telemetry.addData("84","Beacon Blue:"+sensorRGBBeacon.blue());
        telemetry.addData("85","Beacon Red:"+sensorRGBBeacon.red());
        telemetry.addData("86","Beacon Green:"+sensorRGBBeacon.green());
        telemetry.addData("87","Count:"+count);
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
    private double count = 0;
    private double rightEnconderProblems = 0;
    private double leftEnconderProblems = 0;
    private int v_state = 0;
    private double left_encoder_pos = 0;
    private double right_encoder_pos = 0;
    private double BeaconBlue = 0;
    private boolean toofar=false;
    private boolean stayinplace=false;
    private double BeaconRed=0;
    private double adjspeed = 0;
    private double tempGyro = 0;
    private boolean colorproblems=false;
} // PushBotAuto
