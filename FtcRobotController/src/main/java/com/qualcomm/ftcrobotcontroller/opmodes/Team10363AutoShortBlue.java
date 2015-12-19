package com.qualcomm.ftcrobotcontroller.opmodes;

//------------------------------------------------------------------------------
//
// PushBotAuto
//

/**
 * Provide a basic autonomous operational mode that uses the left and right
 * drive motors and associated encoders implemented using a state machine for
 * the Push Bot.
 *
 * @author SSI Robotics
 * @version 2015-08-01-06-01
 */
public class Team10363AutoShortBlue extends PushBotTelemetry

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
    public Team10363AutoShortBlue()

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
        switch (v_state)
        {
        //
        // Synchronize the state machine and hardware.
        //
        case 0:
            //
            // Reset the encoders to ensure they are at a known good value.
            //
            reset_drive_encoders ();

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
            run_using_encoders ();


            //
            // Start the drive wheel motors at half power.
            //
            m_holder_position(.6);
            set_drive_power (.25f, .25f);


            //
            // Have the motor shafts turned the required amount?
            //
            // If they haven't, then the op-mode remains in this state (i.e this
            // block will be executed the next time this method is called).
            //
            if (have_drive_encoders_reached (8640, 8640))
            {
                //
                // Reset the encoders to ensure they are at a known good value.
                //

                //
                // Stop the motors.
                //
                set_drive_power (0.0f, 0.0f);

                left_encoder_pos=a_left_encoder_count();
                right_encoder_pos=a_right_encoder_count();
                //
                // Transition to the next state when this method is called
                // again.

                v_state++;
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
            set_drive_power(0.0f, -0.25f);
            //Same as before, but with the right wheel backwards and a little bit of extra goodness to prevent any bugs
            if (anti_have_drive_encoders_reached(left_encoder_pos,right_encoder_pos-1500)) {
                set_drive_power(0.0f, 0.0f);

                left_encoder_pos=a_left_encoder_count();
                right_encoder_pos=a_right_encoder_count();

                v_state++;
                }

            break;

            case 3://Go towards the bin
                update_telemetry ();
                telemetry.addData("19", "LeftEncoderPos: " + left_encoder_pos);
                telemetry.addData ("20", "RightEncoderPos: " + right_encoder_pos);
                set_drive_power(0.2f,0.2f);
                m_holder_position(.8);

                if (have_drive_encoders_reached(left_encoder_pos+2880,right_encoder_pos+2880)) {
                    set_drive_power(0.0f, 0.0f);
                    left_encoder_pos=a_left_encoder_count();
                    right_encoder_pos=a_right_encoder_count();

                    v_state++;
                    }
                break;
            case 4://Drop the servos holding the climbers

                m_holder_position(.9);

            //    try {
            //        sleep(1000);
            //    } catch (InterruptedException e) {
            //        e.printStackTrace();
            //    }
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
            case 5://Go backwards, ensuring that the servos are in the bin

                set_drive_power(-0.2f,-0.2f);

                if (anti_have_drive_encoders_reached(left_encoder_pos-1440,right_encoder_pos-1440)) {

                    set_drive_power(0.0f,0.0f);
                    m_holder_position(.3);

                    left_encoder_pos=a_left_encoder_count();
                    right_encoder_pos=a_right_encoder_count();
                    //double timeToWaitFor2 = System.currentTimeMillis() + 1000;
                    //while (timeToWaitFor2 > System.currentTimeMillis()) {}
             //       try {
             //           sleep(1000);
             //       } catch (InterruptedException e) {
             //           e.printStackTrace();
             //       }
                    //v_state++;
             //       try {
             //           m_holder_position(0);
             //           sleep(1000);
             //       } catch (InterruptedException e) {
             //           e.printStackTrace();
             //       }
                    v_state++;
                    //m_hand_position(1);
                    //m_hand_position(0);
                }
                break;
            case 6://Go forwards again, ensuring that the robot is in the square area



             //   update_telemetry ();
             //   telemetry.addData("21", "LeftEncoderPos: " + left_encoder_pos);
             //   telemetry.addData("22", "RightEncoderPos: " + right_encoder_pos);
                //m_hand_position(1);
                //m_hand_position(0);

                set_drive_power(0.5f,0.5f);
                /*try {

                    sleep(1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                if (have_drive_encoders_reached(left_encoder_pos+720,right_encoder_pos+720)) {
                    set_drive_power(0.0f,0.0f);

                    v_state++;
                }




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
} // PushBotAuto
