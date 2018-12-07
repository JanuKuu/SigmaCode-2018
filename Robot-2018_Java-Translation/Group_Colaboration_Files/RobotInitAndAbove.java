/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

Import iostream
Import IterativeRobot.h
Import Driverstation.h
Import LiveWindow/LiveWindow.h
Import SmartDashboard/SmartDashboard.h
Import SmartDashboard/SendableChooser.h
Import ctre/Phoenix.h
Import RobotDrive.h
Import XboxController.h
Import Compressor.h
Import DoubleSolenoid.h
Import ADXRS450_Gyro.h
//#include <NetworkTable.h> //try to fix later
Import "WPIlib.h" //replace with network-tables

#define TICKS_PER_INCH 1672
/*
#define STATE1 		1
#define STATE1_5 	2
#define STATE2 		3
#define STATE2_5 	4
#define STATE3 		5
*/
class Robot : public frc::IterativeRobot {
public:

	std::string gamedata;

//	std::string _sb;
//	limelight network table declarations

	std::shared_ptr<NetworkTable> table = NetworkTable::GetTable("limelight");

	float tv; //targets detected
	float ta; //target area in '%'
	float tx; //x axis values
	float ty; //y axis values

	//limelight vision logic declarations
	double speed;
	float Kp = -0.025f; //-0.037 casts 'tx' directly to a value of 0 to 1
	float min_command = 0.05f;
	int tx_Sign = 0;
	int tx_Val = 0;

	int pulseWidthPosLeft = 0;
	int pulseWidthPosRight = 0;

	Compressor compressor = new Compressor(0);
	frc::DoubleSolenoid sigmaShift{0, 1}; // Shifts to high gear - LT Press-and-Hold
	frc::DoubleSolenoid sigmaLift{2, 3}; // Elevate - ASCEND! - X Toggle
	frc::DoubleSolenoid sigmaIntake1{4, 5}; // Cube GRIPP! - RT Toggle

	//1,2,3
	WPI_TalonSRX leftDrive = new WPI_TalonSRX(1);
	WPI_VictorSPX left2 = new WPI_VictorSPX(2);
	WPI_VictorSPX left3 = new WPI_VictorSPX(3);

	//4,5,6
	WPI_TalonSRX rightDrive = new WPI_TalonSRX(4);
	WPI_VictorSPX right2 = new WPI_VictorSPX(5);
	WPI_VictorSPX right3 = new WPI_VictorSPX(6);

	WPI_TalonSRX intakePivot = new WPI_TalonSRX(8);

	WPI_TalonSRX firstElev1 = new WPI_TalonSRX(7);
	WPI_TalonSRX secondElev = new WPI_TalonSRX(9);
	WPI_VictorSPX firstElev2 = new WPI_VictorSPX(10);

	WPI_VictorSPX intake1 = new WPI_VictorSPX(11);
	WPI_VictorSPX intake2 = new WPI_VictorSPX(12); // follows intake1

	frc::ADXRS450_Gyro Gyro = new ADXRS450_Gyro();
	int resetGyro = 0;

	//XboxController
	XboxController * controller = new XboxController(0);
	boolean X;
	boolean A;
	boolean Y;
	boolean B;
	boolean RB;
	boolean LB;
	boolean LS;
	double LT;
	double RT;
	double LY;
	double RY;
	int DP;
	boolean stateX = false;
	boolean stateRT = false;


	XboxController controller2 = new XboxController(1);
	boolean Y2;
	boolean A2;

	boolean intakeState = 0;
	boolean intakeStateFlipped = 0;

	double liftEncoder1;
	double liftEncoder2;

	double liftEncoderMax1;
	double liftEncoderMin1;
	double liftEncoderMax2;
	double liftEncoderMin2;

	//int initEncoder = 0;

    double leftDriveValue;
	double rightDriveValue;

	double rightOffset = 0.95;

	int rInvert = 0;
	int cInvert = 0;
	double cAngle = 0;
	int cDistance = 0;
	int autoState = 0;
	int moveState = 0;
	int baseCurrent;
	int targetPulseWidthPosRight = 0;


	void RobotInit() {
		m_chooser.AddDefault(kAutoNameDefault, kAutoNameDefault);
		m_chooser.AddObject(autoLine, autoLine);
		m_chooser.AddObject(leftAuto, leftAuto);
		m_chooser.AddObject(centerAuto, centerAuto);
		m_chooser.AddObject(rightAuto, rightAuto);
		frc::SmartDashboard::PutData("Auto Modes", &m_chooser);

		Gyro->Calibrate();
	}
