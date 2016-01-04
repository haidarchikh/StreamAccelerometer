package com.example.haidar.streamaccelerometer;

/**
 * Created by haidar on 2016-01-03.
 * StreamAccelerometer
 */
public class Consts {

    private Consts(){}

    // Atomic Activities A_
    public static final String A_LYING                     = "lying";
    public static final String A_RUNNING                   = "running";
    public static final String A_SITTING                   = "sitting";
    public static final String A_STANDING                  = "standing";
    public static final String A_WALKING                   = "walking";
    public static final String A_TOOTH_BRUSHING_HAND_MOVE  = "tooth_brushing_hand_move";
    public static final String A_OPEN_BATHROOM_CUPBOARD    = "open_bathroom_cupboard";
    public static final String A_TAKE_TOOTHBRUSH           = "take_toothbrush";
    public static final String A_TAKE_TOOTHPASTE           = "take_toothpaste";
    public static final String A_COFFEE_MUG                = "coffee_mug";
    public static final String A_COFFEE_MACHINE            = "coffee_machine";

    // Context Attributes C_
    public static final String C_AT_BATHROOM               = "at_bathroom";
    public static final String C_AT_KITCHEN                = "at_kitchen";
    public static final String C_BATHROOM_LIGHT_ON         = "bathroom_light_on";
    public static final String C_KITCHEN_LIGHT_ON          = "kitchen_light_on";

    // Complex Activity CA_
    public static final String CA_BRUSHING_TEETH 	       = "brushing_teeth";
    public static final String CA_PREPARING_COFFEE	       = "preparing_coffee";

    // Situation S_
    public static final String S_UNKNOWN                   = "unknown";
    public static final String S_AWAKE                     = "awake";
    public static final String S_AT_HOME                   = "at_home";
    public static final String S_AT_WORK                   = "at_work";
    // JSON Keys
    public static final String ACTIVITY                    = "activity";
    public static final String CONTEXT                     = "context";

    // Rabbit
    public static final String LOCALHOST                   = "localhost";
    public static final String EXCHANGE_NAME_EVENTS        = "events";
    public static final String EXCHANGE_NAME_ACCELEROMETER = "accelerometer";
    public static final String EXCHANGE_TYPE_DIRECT        = "direct";
    public static final String CLASSIFIER_PATH = "/home/haidar/Desktop/Features/featuresModel.model";
}