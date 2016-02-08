    public class Team10363AutoLinearBlue extends LinearOpMode{
    
    public Team10363AutoLinearBlue(){}
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



v_warning_generated = false;
v_warning_message = "Can't map; ";

        //
        // Connect the drive wheel motors.
        //
        // The direction of the right motor is reversed, so joystick inputs can
        // be more generically applied.
        //
try{
    v_motor_churro_motor = hardwareMap.dcMotor.get ("churro_motor");
}
catch (Exception p_exeception)
{
    m_warning_message ("churro_motor");
    DbgLog.msg (p_exeception.getLocalizedMessage ());

    v_motor_churro_motor = null;
}
try{
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
double l_scrub_position=.92;
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
waitOneFullHardwareCycle();
sensorRGBRight.setI2cAddress(0x44);
waitOneFullHardwareCycle();
sensorRGBBeacon.setI2cAddress(0x38);
waitOneFullHardwareCycle();
sensorGyro.calibrate();// Reset the gyro

while (sensorGyro.isCalibrating())  {
    try {
        Thread.sleep(50);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}
sensorRGBRight.enableLED(false);
waitOneFullHardwareCycle();
sensorRGBLeft.enableLED(false);
waitOneFullHardwareCycle();
sensorRGBBeacon.enableLED(false);
waitOneFullHardwareCycle();
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
catch (Exception p_exeception){
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
catch (Exception p_exeception){
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
waitForStart();
}
