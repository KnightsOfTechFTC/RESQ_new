package com.qualcomm.ftcrobotcontroller.opmodes;

//------------------------------------------------------------------------------
//
// PushBotManual
//

import com.qualcomm.robotcore.util.Range;

/**
 * Provide a basic manual operational mode that uses the left and right
 * drive motors, left arm motor, servo motors and gamepad input from two
 * gamepads for the Push Bot.
 *
 * @author SSI Robotics
 * @version 2015-08-01-06-01
 */
public class PushBotManual extends PushBotTelemetry

{
    //--------------------------------------------------------------------------
    //
    // PushBotManual
    //
    /**
     * Construct the class.
     *
     * The system calls this member when the class is instantiated.
     */
    public PushBotManual ()

    {
        //
        // Initialize base classes.
        //
        // All via self-construction.

        //
        // Initialize class members.
        //
        // All via self-construction.

    } // PushBotManual

    //--------------------------------------------------------------------------
    //
    // loop
    //
    /**
     * Implement a state machine that controls the robot during
     * manual-operation.  The state machine uses gamepad input to transition
     * between states.
     *
     * The system calls this member repeatedly while the OpMode is running.
     */
    private double current_heading = 0;
    @Override public void loop ()

    {
        //----------------------------------------------------------------------
        //
        // DC Motors
        //
        // Obtain the current values of the joystick controllers.
        //
        // Note that x and y equal -1 when the joystick is pushed all of the way
        // forward (i.e. away from the human holder's body).
        //
        // The clip method guarantees the value never exceeds the range +-1.
        //
        // The DC motors are scaled to make it easier to control them at slower
        // speeds.
        //
        // The setPower methods write the motor power values to the DcMotor
        // class, but the power levels aren't applied until this method ends.
        //

        //Clean_Beacon controls the vegetable cleaner servo
        clean_beacon(.8);

        //This is a slow button. It makes it go slow, by setting a lot of states later in code.
        boolean slow = false;
        if(gamepad1.left_bumper){slow=true;}

        //
        // Automatically Auto-Adjusts
        //

        //Adjustment to the speed
        double adjspeed = 0;
        double gyroscale= gamepad1.left_stick_y+gamepad1.right_stick_y;
        double gyroZ=a_gyro_z();
        //scale is how fast it goes. 1 is a *1 modifier, .4 is a *.4 modifier, etc.
        float scale = 1;
        if(slow){scale = 0.4f;}
        /*
        This is a manual implementation of straightDrive. First, if its going slow, record the gyro heading
        Otherwise, if both of the sticks are close to the center, record the gyro heading. Otherwise,
        if were not turning (If our sticks are around a tenth close together) then set adjspeed normally
        as we did in the autonomous straightDrive. Remember, if adjspeed is not set here it will default
        to 0 as it is set to zero before this. Otherwise, record the gyro heading. The gyro heading is
        stored in current_heading. Of note is that the only time current_heading is used is in iterations
        where it is not set, as the only time it is used is in setting adjspeed. This means that when
        in straightDrive current_heading will not change. When not in straightDrive, however, it will
        change to meet the current heading.
        */
        if (slow){
            current_heading = a_gyro_heading();
        } else if (Math.abs(gamepad1.left_stick_y) <= 0.1 && Math.abs(gamepad1.right_stick_y) <= 0.1){
            current_heading = a_gyro_heading();
        }
        else if (Math.abs(gamepad1.left_stick_y-gamepad1.right_stick_y)<.1) {
                adjspeed=gyroscale*Math.sin((Math.PI/180)*(a_gyro_heading() - current_heading));
        }else{
            current_heading = a_gyro_heading();
        }
        //Telemetry is debug states that are sent to the driver controller. Useful for debug
        update_telemetry();
        //a_gyro_z is the z variable the gyro gives.
        telemetry.addData("48: Gyro Z",a_gyro_z());
        //Send gyroscale to telemetry
        telemetry.addData("49: gyroscale", gyroscale);
        //Send the state of adjseed to telemetry
        telemetry.addData("50: adjspeed", adjspeed);
        //Send the gyro heading to telemetry
        telemetry.addData("51: gyro heading", a_gyro_heading());
        //Send current_heading to telemetry
        telemetry.addData("52: current heading", current_heading);
        //Send the left stick's y axis to telemetry
        telemetry.addData("53: Gamepad 1 Left Stick Y", gamepad1.left_stick_y);

        //Scales sticks so if they're almost equal, Robert won't turn.
        float l_left_drive_power = scale_motor_power (((-gamepad1.left_stick_y*scale)));
        float l_right_drive_power = scale_motor_power (((-gamepad1.right_stick_y*scale)));
        //Add in adjspeed corrections
        l_left_drive_power = Range.clip(((float) ((l_left_drive_power) - adjspeed)), -1, 1);
        l_right_drive_power = Range.clip(((float) ((l_right_drive_power) + adjspeed)), -1, 1);

        //Add some telemetry about that
        telemetry.addData("54: left_drive_power", l_left_drive_power);
        telemetry.addData("55: right_drive_power", l_right_drive_power);

        //Set the drive power
        set_drive_power(l_left_drive_power, l_right_drive_power);

        //
        // Servo controls
        //
        //Set the tread powers (the churro motor) sadly doesn't work that well
        if (gamepad1.a){m_churro_motor_power(.22);}
        if (gamepad1.b){m_churro_motor_power(-.22);}
        if (gamepad1.x){m_churro_motor_power(0);}
        // manual controls for left and right buckets
        m_left_bucket_rotate_position(a_left_bucket_rotate_position() - ((gamepad2.left_stick_y) * .005));
        m_right_bucket_rotate_position(a_right_bucket_rotate_position() + ((gamepad2.right_stick_y) * .005));
        m_left_dump_position(a_left_dump_position() - ((gamepad2.left_stick_x) * .005));
        m_right_dump_position(a_right_dump_position() - ((gamepad2.right_stick_x)*.005));
        // right and left debris flip servos
        if (gamepad2.right_trigger>=.5){m_right_flip_position(0);}
        if (gamepad2.right_bumper){m_right_flip_position(1);}
        if (gamepad2.left_trigger>=.5){m_left_flip_position(1);}
        if (gamepad2.left_bumper){m_left_flip_position(0);}
        // left bucket collect position
        if (gamepad2.dpad_down){m_left_bucket_rotate_position(.42); m_left_dump_position(.12);}
        // right bucket collect position
        if (gamepad2.a){m_right_bucket_rotate_position(.82); m_right_dump_position(.93);}
        // left bucket store position
        if (gamepad2.dpad_up){m_left_bucket_rotate_position(.89); m_left_dump_position(.56);}
        // right bucket store position
        if (gamepad2.y){m_right_bucket_rotate_position(.4); m_right_dump_position(.5);}
        // left bucket dump position
        if (gamepad2.guide){m_left_bucket_rotate_position(.57); m_left_dump_position(.43);}
        // right bucket dump position
        if (gamepad2.start){m_right_bucket_rotate_position(.67); m_right_dump_position(.5);}

        //Updates telemetry again. Are as labeled
        update_telemetry();
        telemetry.addData("18: right_bucket_rotate", a_right_bucket_rotate_position());
        telemetry.addData("81: left_dump_position", a_left_dump_position());
        telemetry.addData("82: left_bucket_rotate", a_left_bucket_rotate_position());
        telemetry.addData("88: right_dump_position", a_right_dump_position());
        telemetry.addData("91: right_hand_position",a_right_hand_position());
        telemetry.addData("92: left_hand_position",a_left_hand_position());

        // Manage the holder (climber) servo

        if (gamepad2.dpad_left) {
            m_holder_position(a_holder_position()+.01);
        }
        else if (gamepad2.dpad_right) {
            m_holder_position(a_holder_position()-.01);
        }
        //----------------------------------------------------------------------
        //
        // Servo Motors
        //
        // Obtain the current values of the gamepad 'x' and 'b' buttons.
        //
        // Note that x and b buttons have boolean values of true and false.
        //
        // The clip method guarantees the value never exceeds the allowable range of
        // [0,1].
        //
        // The setPosition methods write the motor power values to the Servo
        // class, but the positions aren't applied until this method ends.
        //
        if (gamepad1.dpad_down){m_tape_extend_pover(.8);}
        else if (gamepad1.dpad_up){m_tape_extend_pover(-.8);}
        else {m_tape_extend_pover(0);}

        if (gamepad1.dpad_right){m_tape_angle_position(0.7);}
        else if (gamepad1.dpad_left){m_tape_angle_position(0.3);}
        else {m_tape_angle_position(0.5);}

        if (gamepad2.x)
        {
            m_left_hand_position(a_left_hand_position() + 0.01);
            //m_right_flip_position(a_right_flip_position());
            m_left_flip_position(a_left_flip_position());
        } else if (gamepad2.left_stick_button){
            m_left_hand_position (a_left_hand_position () - 0.01);
            //m_right_flip_position(a_right_flip_position());
            m_left_flip_position(a_left_flip_position());
        }
        if (gamepad2.b)
        {
            m_right_hand_position (a_right_hand_position () - 0.01);
            //m_left_flip_position(a_left_flip_position());
            m_right_flip_position(a_right_flip_position());
        } else if (gamepad2.right_stick_button){
            m_right_hand_position (a_right_hand_position () + 0.01);
            m_right_flip_position(a_right_flip_position());
            //m_left_flip_position(a_left_flip_position());
        }


        //
        // Send telemetry data to the driver station.
        //
        update_telemetry (); // Update common telemetry
        update_gamepad_telemetry ();

    } // loop

} // PushBotManual
