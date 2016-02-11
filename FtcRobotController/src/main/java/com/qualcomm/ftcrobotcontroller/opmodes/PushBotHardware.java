package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

//------------------------------------------------------------------------------
//
// PushBotHardware
//
/**
 * Provides a single hardware access point between custom op-modes and the
 * OpMode class for the Push Bot.
 *
 * This class prevents the custom op-mode from throwing an exception at runtime.
 * If any hardware fails to map, a warning will be shown via telemetry data,
 * calls to methods will fail, but will not cause the application to crash.
 *
 * @author SSI Robotics
 * @version 2015-08-13-20-04
 */
public class PushBotHardware extends OpMode

{
    //--------------------------------------------------------------------------
    //
    // PushBotHardware
    //
    /**
     * Construct the class.
     *
     * The system calls this member when the class is instantiated.
     */
    public PushBotHardware()

    {
        //
        // Initialize base classes.
        //
        // All via self-construction.

        //
        // Initialize class members.
        //
        // All via self-construction.

        this.v_servo_left_arm = v_servo_left_arm;
    } // PushBotHardware

    //--------------------------------------------------------------------------
    //
    // init
    //
    /**
     * Perform any actions that are necessary when the OpMode is enabled.
     *
     * The system calls this member once when the OpMode is enabled.
     */
    @Override public void init ()

    {
        //
        // Use the hardwareMap to associate class members to hardware ports.
        //
        // Note that the names of the devices (i.e. arguments to the get method)
        // must match the names specified in the configuration file created by
        // the FTC Robot Controller (Settings-->Configure Robot).
        //
        // The variable below is used to provide telemetry data to a class user.
        //

        v_warning_generated = false;
        v_warning_message = "Can't map; ";

        //
        // Connect the drive wheel motors.
        //
        // The direction of the right motor is reversed, so joystick inputs can
        // be more generically applied.
        //
        try
        {
            v_motor_churro_motor = hardwareMap.dcMotor.get ("churro_motor");
        }
        catch (Exception p_exeception)
        {
            m_warning_message ("churro_motor");
            DbgLog.msg (p_exeception.getLocalizedMessage ());

            v_motor_churro_motor = null;
        }
        try
        {
            v_motor_left_drive = hardwareMap.dcMotor.get ("left_drive");
        }
        catch (Exception p_exeception)
        {
            m_warning_message ("left_drive");
            DbgLog.msg (p_exeception.getLocalizedMessage ());

            v_motor_left_drive = null;
        }

        try
        {
            v_motor_right_drive = hardwareMap.dcMotor.get ("right_drive");
            v_motor_right_drive.setDirection (DcMotor.Direction.REVERSE);
        }
        catch (Exception p_exeception)
        {
            m_warning_message ("right_drive");
            DbgLog.msg (p_exeception.getLocalizedMessage ());

            v_motor_right_drive = null;
        }

        //
        // Connect the arm motor.
        //
     //   try
     //   {
     //       v_motor_left_arm = hardwareMap.dcMotor.get ("left_arm");
     //   }
     //   catch (Exception p_exeception)
     //   {
     //       m_warning_message ("left_arm");
     //       DbgLog.msg (p_exeception.getLocalizedMessage ());
     //       v_motor_left_arm = null;
     //   }

        //
        // Connect the servo motors.
        //
        // Indicate the initial position of both the left and right servos.  The
        // hand should be halfway opened/closed.
        //

        double l_left_flip_position=.05;
        double l_scrub_position=1;
        double l_left_hand_position = 0.4; // was 0.6
        double l_right_hand_position = 0.6;  // was 0.4
     //   double l_hand_position = 0.5;
        double l_right_bucket_rotate_position = .4;
        double l_left_bucket_rotate_position = .9;
        double l_right_flip_position=.95;
        double l_holder_position = 0.05;
        double l_right_dump_position=.43;
        double l_left_dump_position=.57;
        sensorGyro = hardwareMap.gyroSensor.get("gyro");
        sensorRGBBeacon = hardwareMap.colorSensor.get("beacon_color");
        sensorRGBLeft = hardwareMap.colorSensor.get("left_color");
        sensorRGBRight = hardwareMap.colorSensor.get("right_color");
        sensorRGBLeft.setI2cAddress(0x42);
        sensorRGBRight.setI2cAddress(0x44);
    //    sensorRGBBeacon.setI2cAddress(0x38);
        sensorGyro.calibrate();// Reset the gyro
        sensorRGBBeacon.enableLed(false);
        sensorRGBLeft.enableLed(true);
        sensorRGBRight.enableLed(true);

        while (sensorGyro.isCalibrating())  {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try
        {
            v_servo_left_hand = hardwareMap.servo.get ("left_hand");
            v_servo_left_hand.setPosition (l_left_hand_position);
        }
        catch (Exception p_exeception)
        {
            m_warning_message ("left_hand");
            DbgLog.msg (p_exeception.getLocalizedMessage ());

            v_servo_left_hand = null;
        }
        try {
            v_servo_scrub=hardwareMap.servo.get("beacon");
            v_servo_scrub.setPosition(l_scrub_position);
        }
        catch (Exception p_exeception){
            m_warning_message("scrub");
            DbgLog.msg(p_exeception.getLocalizedMessage());
            v_servo_scrub=null;
        }
        try
        {
            v_servo_holder = hardwareMap.servo.get ("holder");
            v_servo_holder.setPosition (l_holder_position);
        }
        catch (Exception p_exeception)
        {
            m_warning_message ("holder");
            DbgLog.msg (p_exeception.getLocalizedMessage ());

            v_servo_holder = null;
        }

        try
        {
            v_servo_left_dump = hardwareMap.servo.get ("left_dump");
            v_servo_left_dump.setPosition (l_left_dump_position);
        }
        catch (Exception p_exeception)
        {
            m_warning_message ("left_dump");
            DbgLog.msg (p_exeception.getLocalizedMessage ());

            v_servo_left_dump = null;
        }
        try
        {
            v_servo_right_dump = hardwareMap.servo.get ("right_dump");
            v_servo_right_dump.setPosition (l_right_dump_position);
        }
        catch (Exception p_exeception)
        {
            m_warning_message ("right_dump");
            DbgLog.msg (p_exeception.getLocalizedMessage ());

            v_servo_right_dump = null;
        }
        try
        {
            v_servo_right_flip = hardwareMap.servo.get ("right_flip");
            v_servo_right_flip.setPosition (l_right_flip_position);
        }
        catch (Exception p_exeception)
        {
            m_warning_message ("right_flip");
            DbgLog.msg (p_exeception.getLocalizedMessage ());

            v_servo_right_flip = null;
        }
        try
        {
            v_servo_left_flip = hardwareMap.servo.get ("left_flip");
            v_servo_left_flip.setPosition (l_left_flip_position);
        }
        catch (Exception p_exeception)
        {
            m_warning_message ("left_flip");
            DbgLog.msg (p_exeception.getLocalizedMessage ());

            v_servo_left_flip = null;
        }
        try
        {
            v_servo_right_hand = hardwareMap.servo.get ("right_hand");
            v_servo_right_hand.setPosition (l_right_hand_position);
        }
        catch (Exception p_exeception)
        {
            m_warning_message ("right_hand");
            DbgLog.msg (p_exeception.getLocalizedMessage ());

            v_servo_right_hand = null;
        }
        try
        {
            v_servo_right_bucket_rotate = hardwareMap.servo.get ("right_bucket_rotate");
            v_servo_right_bucket_rotate.setPosition (l_right_bucket_rotate_position);
        }
        catch (Exception p_exeception)
        {
            m_warning_message ("right_bucket_rotate");
            DbgLog.msg (p_exeception.getLocalizedMessage ());

            v_servo_right_bucket_rotate = null;
        }

        try
        {
            v_servo_left_bucket_rotate = hardwareMap.servo.get ("left_bucket_rotate");
            v_servo_left_bucket_rotate.setPosition (l_left_bucket_rotate_position);
        }
        catch (Exception p_exeception)
        {
            m_warning_message ("left_bucket_rotate");
            DbgLog.msg (p_exeception.getLocalizedMessage ());

            v_servo_left_bucket_rotate = null;
        }


    } // init

    //--------------------------------------------------------------------------
    //
    // a_warning_generated
    //
    /**
     * Access whether a warning has been generated.
     */
    boolean a_warning_generated ()

    {
        return v_warning_generated;

    } // a_warning_generated

    //-------------------------------------------------------------------------
    int a_gyro_heading(){
        return sensorGyro.getHeading();
    }
    double a_gyro_z()
    {return sensorGyro.rawZ();}
    int a_left_blue() {return sensorRGBLeft.blue();}
    int a_left_red() {return sensorRGBLeft.red();}
    double a_right_blue() {return sensorRGBRight.blue();}
    void right_led_on(){sensorRGBRight.enableLed(true);}
    int a_right_red() {return sensorRGBRight.red();}
    //--------------------------------------------------------------------------
    //
    // a_warning_message
    //
    /**
     * Access the warning message.
     */
    String a_warning_message ()

    {
        return v_warning_message;

    } // a_warning_message

    //--------------------------------------------------------------------------
    //
    // m_warning_message
    //
    /**
     * Mutate the warning message by ADDING the specified message to the current
     * message; set the warning indicator to true.
     *
     * A comma will be added before the specified message if the message isn't
     * empty.
     */
    void m_warning_message (String p_exception_message)

    {
        if (v_warning_generated) {
            v_warning_message += ", ";
        }
        v_warning_generated = true;
        v_warning_message += p_exception_message;

    }// m_warning_message

    //--------------------------------------------------------------------------
    //
    //--------------------------------------------------------------------------
    //
    double beacon_cleaniess(){
        double l_return=0.0;
        if (v_servo_scrub!=null){l_return=v_servo_scrub.getPosition();}
        return l_return;
        }
    void clean_beacon(double p_position){
        double l_position= Range.clip(p_position,0,1);
        if (v_servo_scrub!=null){v_servo_scrub.setPosition(l_position);}
    }
    double a_left_dump_position ()
    {
        double l_return = 0.0;

        if (v_servo_left_dump != null)
        {
            l_return = v_servo_left_dump.getPosition ();
        }

        return l_return;

    } // a_left_dump_position
    //--------------------------------------------------------------------------
    //
    // m_left_dump_position
    //
    /**
     * Mutate the left dump servo's position.
     */
    void m_left_dump_position (double p_position)
    {
        //
        // Ensure the specific value is legal.
        //
        double l_position = Range.clip
                ( p_position

                        , 0
                        , 1
                );


        if (v_servo_left_dump != null)
        {
            v_servo_left_dump.setPosition (l_position);
        }



    }

    double a_right_dump_position ()
    {
        double l_return = 0.0;

        if (v_servo_right_dump != null)
        {
            l_return = v_servo_right_dump.getPosition ();
        }

        return l_return;

    } // a_right_dump_position

    //--------------------------------------------------------------------------
    //
    // m_right_dump_position
    //
    /**
     * Mutate the right dump servo's position.
     */
    void m_right_dump_position (double p_position)
    {
        //
        // Ensure the specific value is legal.
        //
        double l_position = Range.clip
                ( p_position

                        , 0
                        , 1
                );


        if (v_servo_right_dump != null)
        {
            v_servo_right_dump.setPosition (l_position);
        }



    }



    //--------------------------------------------------------------------------
    //
    // a_right_bucket_rotate_position
    //
    double a_right_bucket_rotate_position ()
    {
        double l_return = 0.0;

        if (v_servo_right_bucket_rotate != null)
        {
            l_return = v_servo_right_bucket_rotate.getPosition ();
        }

        return l_return;

    } // a_right_bucket_rotate_position



    /**
     * Mutate the right bucket servo's position.
     */
    void m_right_bucket_rotate_position (double p_position)
    {
        //
        // Ensure the specific value is legal.
        //
        double l_position = Range.clip
                ( p_position

                        , .4
                        , .9
                );


        if (v_servo_right_bucket_rotate != null)
        {
            v_servo_right_bucket_rotate.setPosition (l_position);
        }



    } // m_right_bucket_rotate_position

    // a_left_bucket_rotate_position
    //
    /**
     * Access the left bucket servo's position.
     */
    double a_left_bucket_rotate_position ()
    {
        double l_return = 0.0;

        if (v_servo_left_bucket_rotate != null)
        {
            l_return = v_servo_left_bucket_rotate.getPosition ();
        }

        return l_return;

    } // a_left_bucket_rotate_position

    //--------------------------------------------------------------------------
    //
    // m_left_bucket_rotate_position
    //
    /**
     * Mutate the left bucket rotate servo's position.
     */
    void m_left_bucket_rotate_position (double p_position)
    {
        //
        // Ensure the specific value is legal.
        //
        double l_position = Range.clip
                ( p_position

                        , 0.3
                        , 0.9
                );


        if (v_servo_left_bucket_rotate != null)
        {
            v_servo_left_bucket_rotate.setPosition (l_position);
        }



    } // m_left_bucket_rotate_position

    // start
     //--------------------------------------------------------------------------
    //
    // a_holder_position
    //
    /**
     * Access the holder servo's position.
     */
    double a_holder_position ()
    {
        double l_return = 0.0;

        if (v_servo_holder != null)
        {
            l_return = v_servo_holder.getPosition ();
        }

        return l_return;

    } // a_holder_position

    //--------------------------------------------------------------------------
    //
    // m_holder_position
    //
    /**
     * Mutate the holder servo's position.
     */
    void m_holder_position (double p_position)
    {
        //
        // Ensure the specific value is legal.
        //
        double l_position = Range.clip
                ( p_position

                          , Servo.MIN_POSITION
                         , Servo.MAX_POSITION
                );


        if (v_servo_holder != null) {v_servo_holder.setPosition(l_position);}
    }


        //--------------------------------------------------------------------------
        //
        // a_right_flip_position
        //
        /**
         * Access the right flip servo's position.
         */

    double a_right_flip_position ()
    {
        double l_return = 0.0;

        if (v_servo_right_flip != null)
        {
            l_return = v_servo_right_flip.getPosition ();
        }

        return l_return;

    } // a_right_flip_position

    //--------------------------------------------------------------------------
    //
    // m_right_flip_position
    //
    /**
     * Mutate the right flip servo's position.
     */
    void m_right_flip_position (double p_position)
    {
        //
        // Ensure the specific value is legal.
        //
        double n_pos=1;
        if (a_right_hand_position()>=.3){n_pos=.95;}
        else {n_pos=1;}
        double l_position = Range.clip
                ( p_position

                        , 0
                        , n_pos
                );


        if (v_servo_right_flip != null)
        {
            v_servo_right_flip.setPosition (l_position);
        }

    } //m_right_flip_position

    double a_left_flip_position ()
    {
        double l_return = 0.0;

        if (v_servo_left_flip != null)
        {
            l_return = v_servo_left_flip.getPosition ();
        }

        return l_return;

    } // a_left_flip_position

    //--------------------------------------------------------------------------
    //
    // m_left_flip_position
    //
    /**
     * Mutate the left flip servo's position.
     */
    void m_left_flip_position (double p_position)
    {
        //
        // Ensure the specific value is legal.
        //
        double n_pos=1;
        if (a_left_hand_position()<=.7){
            n_pos=.05;
        }
        else{
            n_pos=0;
        }
        double l_position = Range.clip
                ( p_position

                        , n_pos
                        , 1
                );


        if (v_servo_left_flip != null)
        {
            v_servo_left_flip.setPosition (l_position);
        }




    }// m_left_flip_position

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
        // Only actions that are common to all Op-Modes (i.e. both automatic and
        // manual) should be implemented here.
        //
        // This method is designed to be overridden.
        //

    } // start

    //--------------------------------------------------------------------------
    //
    // loop
    //
    /**
     * Perform any actions that are necessary while the OpMode is running.
     *
     * The system calls this member repeatedly while the OpMode is running.
     */
    @Override public void loop ()

    {
        //
        // Only actions that are common to all OpModes (i.e. both auto and\
        // manual) should be implemented here.
        //
        // This method is designed to be overridden.
        //

    } // loop

    //--------------------------------------------------------------------------
    //
    // stop
    //
    /**
     * Perform any actions that are necessary when the OpMode is disabled.
     *
     * The system calls this member once when the OpMode is disabled.
     */
    @Override public void stop ()
    {
        //
        // Nothing needs to be done for this method.
        //

    } // stop

    //--------------------------------------------------------------------------
    //
    // scale_motor_power
    //
    /**
     * Scale the joystick input using a nonlinear algorithm.
     */
    float scale_motor_power (float p_power)
    {
        //
        // Assume no scaling.
        //
        float l_scale = 0.0f;

        //
        // Ensure the values are legal.
        //
        float l_power = Range.clip (p_power, -1, 1);

        float[] l_array =
            { 0.00f, 0.05f, 0.09f, 0.10f, 0.12f
            , 0.15f, 0.18f, 0.24f, 0.30f, 0.36f
            , 0.43f, 0.50f, 0.60f, 0.72f, 0.85f
            , 1.00f, 1.00f
            };

        //
        // Get the corresponding index for the specified argument/parameter.
        //
        int l_index = (int)(l_power * 16.0);
        if (l_index < 0)
        {
            l_index = -l_index;
        }
        else if (l_index > 16)
        {
            l_index = 16;
        }

        if (l_power < 0)
        {
            l_scale = -l_array[l_index];
        }
        else
        {
            l_scale = l_array[l_index];
        }

        return l_scale;

    } // scale_motor_power


    //--------------------------------------------------------------------------
    //
    // a_left_arm_power
    //
    /**
     * Access the left churro motor's power level.
     */
    double a_churro_motor_power ()
    {
        double l_return = 0.0;

        if (v_motor_churro_motor != null)
        {
            l_return = v_motor_churro_motor.getPower ();
        }

        return l_return;

    } // a_churro_motor_power

    //--------------------------------------------------------------------------
    //
    // m_left_arm_power
    //
    /**
     * Access the left arm motor's power level.
     */
    void m_churro_motor_power (double p_level)
    {
        if (v_motor_churro_motor != null)
        {
            v_motor_churro_motor.setPower (p_level);
        }

    } // m_left_arm_power

    //--------------------------------------------------------------------------
    //
    // a_left_drive_power
    //
    /**
     * Access the left drive motor's power level.
     */
    double a_left_drive_power ()
    {
        double l_return = 0.0;

        if (v_motor_left_drive != null)
        {
            l_return = v_motor_left_drive.getPower ();
        }

        return l_return;

    } // a_left_drive_power

    //--------------------------------------------------------------------------
    //
    // a_right_drive_power
    //
    /**
     * Access the right drive motor's power level.
     */
    double a_right_drive_power ()
    {
        double l_return = 0.0;

        if (v_motor_right_drive != null)
        {
            l_return = v_motor_right_drive.getPower ();
        }

        return l_return;

    } // a_right_drive_power

    //--------------------------------------------------------------------------
    //
    // set_drive_power
    //
    /**
     * Scale the joystick input using a nonlinear algorithm.
     */
    void set_drive_power (double p_left_power, double p_right_power)

    {
        if (v_motor_left_drive != null)
        {
            v_motor_left_drive.setPower (p_left_power);
        }
        if (v_motor_right_drive != null)
        {
            v_motor_right_drive.setPower (p_right_power);
        }

    } // set_drive_power

    //--------------------------------------------------------------------------
    //
    // run_using_left_drive_encoder
    //
    /**
     * Set the left drive wheel encoder to run, if the mode is appropriate.
     */
    public void run_using_left_drive_encoder ()

    {
        if (v_motor_left_drive != null)
        {
            v_motor_left_drive.setMode
                ( DcMotorController.RunMode.RUN_USING_ENCODERS
                );
        }

    } // run_using_left_drive_encoder

    //--------------------------------------------------------------------------
    //
    // run_using_right_drive_encoder
    //
    /**
     * Set the right drive wheel encoder to run, if the mode is appropriate.
     */
    public void run_using_right_drive_encoder ()

    {
        if (v_motor_right_drive != null)
        {
            v_motor_right_drive.setMode
                ( DcMotorController.RunMode.RUN_USING_ENCODERS
                );
        }

    } // run_using_right_drive_encoder

    //--------------------------------------------------------------------------
    //
    // run_using_encoders
    //
    /**
     * Set both drive wheel encoders to run, if the mode is appropriate.
     */
    public void run_using_encoders ()

    {
        //
        // Call other members to perform the action on both motors.
        //
        run_using_left_drive_encoder ();
        run_using_right_drive_encoder ();

    } // run_using_encoders

    //--------------------------------------------------------------------------
    //
    // run_without_left_drive_encoder
    //
    /**
     * Set the left drive wheel encoder to run, if the mode is appropriate.
     */
    public void run_without_left_drive_encoder ()

    {
        if (v_motor_left_drive != null)
        {
            if (v_motor_left_drive.getMode () ==
                DcMotorController.RunMode.RESET_ENCODERS)
            {
                v_motor_left_drive.setMode
                    ( DcMotorController.RunMode.RUN_WITHOUT_ENCODERS
                    );
            }
        }

    } // run_without_left_drive_encoder

    //--------------------------------------------------------------------------
    //
    // run_without_right_drive_encoder
    //
    /**
     * Set the right drive wheel encoder to run, if the mode is appropriate.
     */
    public void run_without_right_drive_encoder ()

    {
        if (v_motor_right_drive != null)
        {
            if (v_motor_right_drive.getMode () ==
                DcMotorController.RunMode.RESET_ENCODERS)
            {
                v_motor_right_drive.setMode
                    ( DcMotorController.RunMode.RUN_WITHOUT_ENCODERS
                    );
            }
        }

    } // run_without_right_drive_encoder

    //--------------------------------------------------------------------------
    //
    // run_without_drive_encoders
    //
    /**
     * Set both drive wheel encoders to run, if the mode is appropriate.
     */
    public void run_without_drive_encoders ()

    {
        //
        // Call other members to perform the action on both motors.
        //
        run_without_left_drive_encoder ();
        run_without_right_drive_encoder ();

    } // run_without_drive_encoders

    //--------------------------------------------------------------------------
    //
    // reset_left_drive_encoder
    //
    /**
     * Reset the left drive wheel encoder.
     */
    public void reset_left_drive_encoder ()

    {
        if (v_motor_left_drive != null)
        {
            v_motor_left_drive.setMode
                ( DcMotorController.RunMode.RESET_ENCODERS
                );
        }

    } // reset_left_drive_encoder

    //--------------------------------------------------------------------------
    //
    // reset_right_drive_encoder
    //
    /**
     * Reset the right drive wheel encoder.
     */
    public void reset_right_drive_encoder ()

    {
        if (v_motor_right_drive != null)
        {
            v_motor_right_drive.setMode
                ( DcMotorController.RunMode.RESET_ENCODERS
                );
        }

    } // reset_right_drive_encoder

    //--------------------------------------------------------------------------
    //
    // reset_drive_encoders
    //
    /**
     * Reset both drive wheel encoders.
     */
    public void reset_drive_encoders ()

    {
        //
        // Reset the motor encoders on the drive wheels.
        //
        reset_left_drive_encoder ();
        reset_right_drive_encoder ();

    } // reset_drive_encoders

    //--------------------------------------------------------------------------
    //
    // a_left_encoder_count
    //
    /**
     * Access the left encoder's count.
     */
    int a_left_encoder_count ()
    {
        int l_return = 0;

        if (v_motor_left_drive != null)
        {
            l_return = v_motor_left_drive.getCurrentPosition ();
        }

        return l_return;

    } // a_left_encoder_count

    //--------------------------------------------------------------------------
    //
    // a_right_encoder_count
    //
    /**
     * Access the right encoder's count.
     */
    int a_right_encoder_count ()

    {
        int l_return = 0;

        if (v_motor_right_drive != null)
        {
            l_return = v_motor_right_drive.getCurrentPosition ();
        }

        return l_return;

    } // a_right_encoder_count

    //--------------------------------------------------------------------------
    //
    
    // has_left_drive_encoder_reached
    //
    /**
     * Indicate whether the left drive motor's encoder has reached a value.
     */
    boolean has_left_drive_encoder_reached (double p_count)

    {
        //
        // Assume failure.
        //
        boolean l_return = false;

        if (v_motor_left_drive != null)
        {
            //
            // Has the encoder reached the specified values?
            //
            // TODO Implement stall code using these variables.
            //
            if (Math.abs (v_motor_left_drive.getCurrentPosition ()) > p_count)
            {
                //
                // Set the status to a positive indication.
                //
                l_return = true;
            }
        }

        //
        // Return the status.
        //
        return l_return;

    } // has_left_drive_encoder_reached

    //--------------------------------------------------------------------------
    //
    // has_right_drive_encoder_reached
    //
    /**
     * Indicate whether the right drive motor's encoder has reached a value.
     */
    boolean has_right_drive_encoder_reached (double p_count)

    {
        //
        // Assume failure.
        //
        boolean l_return = false;

        if (v_motor_right_drive != null)
        {
            //
            // Have the encoders reached the specified values?
            //
            // TODO Implement stall code using these variables.
            //
            if (Math.abs (v_motor_right_drive.getCurrentPosition ()) > p_count)
            {
                //
                // Set the status to a positive indication.
                //
                l_return = true;
            }
        }

        //
        // Return the status.
        //
        return l_return;

    } // has_right_drive_encoder_reached

    //--------------------------------------------------------------------------
    //
    // have_drive_encoders_reached
    //
    /**
     * Indicate whether the drive motors' encoders have reached a value.
     */
    boolean have_drive_encoders_reached
        ( double p_left_count
        , double p_right_count
        )

    {
        //
        // Assume failure.
        //
        boolean l_return = false;

        //
        // Have the encoders reached the specified values?
        //
        if (has_left_drive_encoder_reached (p_left_count) &&
            has_right_drive_encoder_reached (p_right_count))
        {
            //
            // Set the status to a positive indication.
            //
            l_return = true;
        }

        //
        // Return the status.
        //
        return l_return;

    } // have_encoders_reached
boolean anti_have_drive_encoders_reached
        ( double p_left_count
        , double p_right_count
        )

    {
        //
        // Assume failure.
        //
        boolean l_return = true;

        //
        // Have the encoders reached the specified values?
        //
        if (has_left_drive_encoder_reached (p_left_count) &&
            has_right_drive_encoder_reached (p_right_count))
        {
            //
            // Set the status to a negative indication.
            //
            l_return = false;
        }

        //
        // Return the status.
        //
        return l_return;

    }
    //--------------------------------------------------------------------------
    //
    // drive_using_encoders
    //
    /**
     * Indicate whether the drive motors' encoders have reached a value.
     */
    boolean drive_using_encoders
        ( double p_left_power
        , double p_right_power
        , double p_left_count
        , double p_right_count
        )

    {
        //
        // Assume the encoders have not reached the limit.
        //
        boolean l_return = false;

        //
        // Tell the system that motor encoders will be used.
        //
        run_using_encoders ();

        //
        // Start the drive wheel motors at full power.
        //
        set_drive_power (p_left_power, p_right_power);

        //
        // Have the motor shafts turned the required amount?
        //
        // If they haven't, then the op-mode remains in this state (i.e this
        // block will be executed the next time this method is called).
        //
        if (have_drive_encoders_reached (p_left_count, p_right_count))
        {
            //
            // Reset the encoders to ensure they are at a known good value.
            //
            reset_drive_encoders ();

            //
            // Stop the motors.
            //
            set_drive_power (0.0f, 0.0f);

            //
            // Transition to the next state when this method is called
            // again.
            //
            l_return = true;
        }

        //
        // Return the status.
        //
        return l_return;

    } // drive_using_encoders

    //--------------------------------------------------------------------------
    //
    // has_left_drive_encoder_reset
    //
    /**
     * Indicate whether the left drive encoder has been completely reset.
     */
    boolean has_left_drive_encoder_reset ()
    {
        //
        // Assume failure.
        //
        boolean l_return = false;

        //
        // Has the left encoder reached zero?
        //
        if (a_left_encoder_count () == 0)
        {
            //
            // Set the status to a positive indication.
            //
            l_return = true;
        }

        //
        // Return the status.
        //
        return l_return;

    } // has_left_drive_encoder_reset

    //--------------------------------------------------------------------------
    //
    // has_right_drive_encoder_reset
    //
    /**
     * Indicate whether the left drive encoder has been completely reset.
     */
    boolean has_right_drive_encoder_reset ()
    {
        //
        // Assume failure.
        //
        boolean l_return = false;

        //
        // Has the right encoder reached zero?
        //
        if (a_right_encoder_count () == 0)
        {
            //
            // Set the status to a positive indication.
            //
            l_return = true;
        }

        //
        // Return the status.
        //
        return l_return;

    } // has_right_drive_encoder_reset

    //--------------------------------------------------------------------------
    //
    // have_drive_encoders_reset
    //
    /**
     * Indicate whether the encoders have been completely reset.
     */
    boolean have_drive_encoders_reset ()
    {
        //
        // Assume failure.
        //
        boolean l_return = false;

        //
        // Have the encoders reached zero?
        //
        if (has_left_drive_encoder_reset () && has_right_drive_encoder_reset ())
        {
            //
            // Set the status to a positive indication.
            //
            l_return = true;
        }

        //
        // Return the status.
        //
        return l_return;

    } // have_drive_encoders_reset

    //--------------------------------------------------------------------------
    //
    // a_left_arm_power
    //
    /**
     * Access the left arm motor's power level.
     */
    double a_left_arm_power ()
    {
        double l_return = 0.0;

        if (v_motor_left_arm != null)
        {
            l_return = v_motor_left_arm.getPower ();
        }

        return l_return;

    } // a_left_arm_power

    //--------------------------------------------------------------------------
    //
    // m_left_arm_power
    //
    /**
     * Access the left arm motor's power level.
     */
    void m_left_arm_power (double p_level)
    {
        if (v_motor_left_arm != null)
        {
            v_motor_left_arm.setPower (p_level);
        }

    } // m_left_arm_power

    //--------------------------------------------------------------------------
    //
    // a_hand_position
    //
    /**
     * Access the hand position.
     */
    double a_right_hand_position ()
    {
        double l_return = 0.0;

        if (v_servo_right_hand != null)
        {
            l_return = v_servo_right_hand.getPosition ();
        }

        return l_return;

    }//End of a_right_hand_position
    double a_left_hand_position ()
    {
        double l_return = 0.0;

        if (v_servo_left_hand != null)
        {
            l_return = v_servo_left_hand.getPosition ();
        }

        return l_return;

    } //End of a_left_hand_position
    double a_hand_position ()
    {
        double l_return = 0.0;

        if (v_servo_left_hand != null)
        {
            l_return = v_servo_left_hand.getPosition ();
        }

        return l_return;

    } // a_hand_position

    //--------------------------------------------------------------------------
    //
    // m_hand_position
    //
    /**
     * Mutate the hand position.
     */
    void m_right_hand_position (double p_position)
    {
        //
        // Ensure the specific value is legal.
        //
        double l_position = Range.clip
                ( p_position
                        , .05
                        , .55
                );

        //
        // Set the value.  The right hand value must be opposite of the left
        // value.
        //
        //if (v_servo_left_hand != null)
        //{
        //    v_servo_left_hand.setPosition (l_position);
        //}
        if (v_servo_right_hand != null)
        {
            v_servo_right_hand.setPosition (l_position);
        }

    } //End of m_right_hand_position
    void m_left_hand_position (double p_position)
    {
        //
        // Ensure the specific value is legal.
        //
        double l_position = Range.clip
                ( p_position
                        , .45
                        , .95
                );

        //
        // Set the value.  The right hand value must be opposite of the left
        // value.
        //
        if (v_servo_left_hand != null)
        {
            v_servo_left_hand.setPosition (l_position);
        }
        //if (v_servo_right_hand != null)
        //{
        //    v_servo_right_hand.setPosition (1.0 - l_position);
        //}

    } //End of m_left_hand_position
    void m_hand_position (double p_position)
    {
        //
        // Ensure the specific value is legal.
        //
        double l_position = Range.clip
            ( p_position
            , .45
            , .95
            );

        //
        // Set the value.  The right hand value must be opposite of the left
        // value.
        //
        if (v_servo_left_hand != null)
        {
            v_servo_left_hand.setPosition (l_position);
        }
        if (v_servo_right_hand != null)
        {
            v_servo_right_hand.setPosition (1.0 - l_position);
        }

    } // m_hand_position

    //--------------------------------------------------------------------------
    //
    // open_hand
    //
    /**
     * Open the hand to its fullest.
     */
    void open_hand ()

    {
        //
        // Set the value.  The right hand value must be opposite of the left
        // value.
        //
        if (v_servo_left_hand != null)
        {
            v_servo_left_hand.setPosition (Servo.MAX_POSITION);
        }
        if (v_servo_right_hand != null)
        {
            v_servo_right_hand.setPosition (Servo.MIN_POSITION);
        }

    } // open_hand

    //--------------------------------------------------------------------------
    //
    // v_warning_generated
    //
    /**
     * Indicate whether a message is a available to the class user.
     */
    private boolean v_warning_generated = false;

    //--------------------------------------------------------------------------
    //
    // v_warning_message
    //
    /**
     * Store a message to the user if one has been generated.
     */
    private String v_warning_message;

    //--------------------------------------------------------------------------
    //
    // v_motor_left_drive
    //
    /**
     * Manage the aspects of the left drive motor.
     */
    private DcMotor v_motor_left_drive;

    //--------------------------------------------------------------------------
    //
    // v_motor_right_drive
    //
    /**
     * Manage the aspects of the right drive motor.
     */
    private DcMotor v_motor_right_drive;
    private DcMotor v_motor_churro_motor;

    //--------------------------------------------------------------------------
    //
    // v_motor_left_arm
    //
    /**
     * Manage the aspects of the left arm motor.
     */
    private DcMotor v_motor_left_arm;

    //--------------------------------------------------------------------------
    //
    // v_servo_left_hand
    //
    /**
     * Manage the aspects of the left hand servo.
     */
    private Servo v_servo_left_hand;
    private Servo v_servo_left_bucket_rotate;
    private Servo v_servo_right_bucket_rotate;

    //--------------------------------------------------------------------------
    //
    // v_servo_right_hand
    //
    /**
     * Manage the aspects of the right hand servo.
     */
    private Servo v_servo_right_hand;
    private Servo v_servo_scrub;
    private Servo v_servo_right_flip;
    private Servo v_servo_left_flip;
    private Servo v_servo_right_dump;
    private Servo v_servo_left_dump;
    private GyroSensor sensorGyro;
    ColorSensor sensorRGBBeacon;
    ColorSensor sensorRGBRight;
    ColorSensor sensorRGBLeft;

private  Servo v_servo_left_arm;
private  Servo v_servo_right_arm;
private  Servo v_servo_holder;

} // PushBotHardware
