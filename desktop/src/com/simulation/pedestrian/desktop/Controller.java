package com.simulation.pedestrian.desktop;
import com.simulation.pedestrian.Main;

import java.net.URL;
import java.util.ResourceBundle;

import com.simulation.pedestrian.Parameter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

public class Controller implements Initializable {
    @FXML
    private Button startButton;
    @FXML
    void startButton(ActionEvent event) {
        Main.setPLAY(true);
    }

    @FXML
    private Button stopButton;
    @FXML
    void stopButton(ActionEvent event) {
        Main.setPLAY(false);
    }

    @FXML
    private Button recordButton;
    @FXML
    void recordButton(ActionEvent event) {
        System.out.println("record");
    }

    @FXML
    private Button InitButton;
    @FXML
    void InitButton(ActionEvent event) {
        Main.getEnvironment().spawnInitAgents();
        Main.getEnvironment().setStep(0);
    }

    @FXML
    private Button deleteButton;
    @FXML
    void deleteButton(ActionEvent event) {
        Main.getEnvironment().clearAgent();
    }

    @FXML
    private RadioButton selectAgentView;
    @FXML
    void selectAgentView(ActionEvent event) {
        Main.setDrawView();
    }
    @FXML
    private RadioButton selectFollowLine;
    @FXML
    void selectFollowLine(ActionEvent event) {
        Main.setDrawFollowLine();
    }

    @FXML
    private RadioButton selectGoalLine;
    @FXML
    void selectGoalLine(ActionEvent event) {
        Main.setDrawGoalLine();
    }

    @FXML private Button ENDSTEP_Button;
    @FXML private TextField ENDSTEP_text;
    @FXML void ENDSTEP_Button(ActionEvent event) {
        Parameter.ENDSTEP = Float.valueOf(ENDSTEP_text.getText());
    }
    @FXML private Button ATTEMPTSNUM_Button;
    @FXML private TextField ATTEMPTSNUM_text;
    @FXML void ATTEMPTSNUM_Button(ActionEvent event)  {
        Parameter.ATTEMPTSNUM = Integer.valueOf(ATTEMPTSNUM_text.getText());
    }
    @FXML private Button CELL_INTERVAL_Button;
    @FXML private TextField CELL_INTERVAL_text;
    @FXML void CELL_INTERVAL_Button(ActionEvent event) {
        Parameter.CELL_INTERVAL = Float.valueOf(CELL_INTERVAL_text.getText());
    }
    @FXML private Button MAX_POTENTIAL_Button;
    @FXML private TextField MAX_POTENTIAL_text;
    @FXML void MAX_POTENTIAL_Button(ActionEvent event) {
        Parameter.MAX_POTENTIAL = Float.valueOf(MAX_POTENTIAL_text.getText());
    }
    @FXML private Button AGENT_POTENTIAL_WEIGHT_Button;
    @FXML private TextField AGENT_POTENTIAL_WEIGHT_text;
    @FXML void AGENT_POTENTIAL_WEIGHT_Button(ActionEvent event) {
        Parameter.AGENT_KIM_POTENTIAL_WEIGHT = Float.valueOf(AGENT_POTENTIAL_WEIGHT_text.getText());

    }
    @FXML private Button AGENT_POTENTIAL_RANGE_Button;
    @FXML private TextField AGENT_POTENTIAL_RANGE_text;
    @FXML void AGENT_POTENTIAL_RANGE_Button(ActionEvent event) {
        Parameter.AGENT_KIM_POTENTIAL_RANGE = Float.valueOf(AGENT_POTENTIAL_RANGE_text.getText());
    }

    //TODO:変数宣言
    @FXML private Button OBSTACLE_POTENTIAL_WEIGHT_Button;
    @FXML private TextField OBSTACLE_POTENTIAL_WEIGHT_text;
    @FXML void OBSTACLE_POTENTIAL_WEIGHT_Button(ActionEvent event) {
        Parameter.OBSTACLE_KIM_POTENTIAL_WEIGHT = Float.valueOf(OBSTACLE_POTENTIAL_WEIGHT_text.getText());
    }
    @FXML private Button OBSTACLE_POTENTIAL_RANGE_Button;
    @FXML private TextField OBSTACLE_POTENTIAL_RANGE_text;
    @FXML void OBSTACLE_POTENTIAL_RANGE_Button(ActionEvent event) {
        Parameter.OBSTACLE_KIM_POTENTIAL_RANGE = Float.valueOf(OBSTACLE_POTENTIAL_RANGE_text.getText());
    }
    @FXML private Button INIT_AGENT_NUM_Button;
    @FXML private TextField INIT_AGENT_NUM_text;
    @FXML void INIT_AGENT_NUM_Button(ActionEvent event) {
        Parameter.INIT_AGENT_NUM = Integer.valueOf(INIT_AGENT_NUM_text.getText());
    }
    @FXML private Button GOAL_AGENT_NUM_Button;
    @FXML private TextField GOAL_AGENT_NUM_text;
    @FXML void GOAL_AGENT_NUM_Button(ActionEvent event) {
        Parameter.GOAL_AGENT_NUM = Integer.valueOf(GOAL_AGENT_NUM_text.getText());
    }
    @FXML private Button INIT_RANDOM_X1_Button;
    @FXML private TextField INIT_RANDOM_X1_text;
    @FXML void INIT_RANDOM_X1_Button(ActionEvent event) {
        Parameter.INIT_RANDOM_X1 = Float.valueOf(INIT_RANDOM_X1_text.getText());
    }
    @FXML private Button INIT_RANDOM_X2_Button;
    @FXML private TextField INIT_RANDOM_X2_text;
    @FXML void INIT_RANDOM_X2_Button(ActionEvent event) {
        Parameter.INIT_RANDOM_X2 = Float.valueOf(INIT_RANDOM_X2_text.getText());
    }
    @FXML private Button INIT_RANDOM_Y1_Button;
    @FXML private TextField INIT_RANDOM_Y1_text;
    @FXML void INIT_RANDOM_Y1_Button(ActionEvent event) {
        Parameter.INIT_RANDOM_Y1 = Float.valueOf(INIT_RANDOM_Y1_text.getText());
    }
    @FXML private Button INIT_RANDOM_Y2_Button;
    @FXML private TextField INIT_RANDOM_Y2_text;
    @FXML void INIT_RANDOM_Y2_Button(ActionEvent event) {
        Parameter.INIT_RANDOM_Y2 = Float.valueOf(INIT_RANDOM_Y2_text.getText());
    }
    @FXML private Button GOALAGENT_DESTINATION_Button;
    @FXML private TextField GOALAGENT_DESTINATION_text;
    @FXML void GOALAGENT_DESTINATION_Button(ActionEvent event) {
        Parameter.GOALAGENT_DESTINATION = GOALAGENT_DESTINATION_text.getText();
    }
    @FXML private Button AGENT_SPEED_Button;
    @FXML private TextField AGENT_SPEED_text;
    @FXML void AGENT_SPEED_Button(ActionEvent event) {
        Parameter.AGENT_SPEED = Float.valueOf(AGENT_SPEED_text.getText());
    }
    @FXML private Button AGENT_RADIUS_Button;
    @FXML private TextField AGENT_RADIUS_text;
    @FXML void AGENT_RADIUS_Button(ActionEvent event) {
        Parameter.AGENT_RADIUS = Float.valueOf(AGENT_RADIUS_text.getText());
    }
    @FXML private Button VIEW_RADIUS_Button;
    @FXML private TextField VIEW_RADIUS_text;
    @FXML void VIEW_RADIUS_Button(ActionEvent event) {
        Parameter.VIEW_RADIUS = Float.valueOf(VIEW_RADIUS_text.getText());
    }
    @FXML private Button VIEW_DEGREE_Button;
    @FXML private TextField VIEW_DEGREE_text;
    @FXML void VIEW_DEGREE_Button(ActionEvent event) {
        Parameter.VIEW_DEGREE = Float.valueOf(VIEW_DEGREE_text.getText());
    }
    @FXML private Button U_RANDOM_WALK_Button;
    @FXML private TextField U_RANDOM_WALK_text;
    @FXML void U_RANDOM_WALK_Button(ActionEvent event) {
        Parameter.U_RANDOM_WALK = Float.valueOf(U_RANDOM_WALK_text.getText());
    }
    @FXML private Button U_FOLLOW_AGENT_Button;
    @FXML private TextField U_FOLLOW_AGENT_text;
    @FXML void U_FOLLOW_AGENT_Button(ActionEvent event) {
        Parameter.U_FOLLOW_AGENT = Float.valueOf(U_FOLLOW_AGENT_text.getText());
    }
    @FXML private Button U_MOVE_GOAL_Button;
    @FXML private TextField U_MOVE_GOAL_text;
    @FXML void U_MOVE_GOAL_Button(ActionEvent event) {
        Parameter.U_MOVE_GOAL = Float.valueOf(U_MOVE_GOAL_text.getText());
    }
    @FXML private Button ALPHA_Button;
    @FXML private TextField ALPHA_text;
    @FXML void ALPHA_Button(ActionEvent event) {
        Parameter.ALPHA = Float.valueOf(ALPHA_text.getText());
    }
    @FXML private Button BETA_Button;
    @FXML private TextField BETA_text;
    @FXML void BETA_Button(ActionEvent event) {
        Parameter.BETA = Float.valueOf(BETA_text.getText());
    }
    @FXML private Button GAMMA_Button;
    @FXML private TextField GAMMA_text;
    @FXML void GAMMA_Button(ActionEvent event) {
        Parameter.GAMMA = Float.valueOf(GAMMA_text.getText());
    }
    @FXML private Button DELTA_Button;
    @FXML private TextField DELTA_text;
    @FXML void DELTA_Button(ActionEvent event) {
        Parameter.DELTA = Float.valueOf(DELTA_text.getText());
    }
    @FXML private Button EPSILON_Button;
    @FXML private TextField EPSILON_text;
    @FXML void EPSILON_Button(ActionEvent event) {
        Parameter.EPSILON = Float.valueOf(EPSILON_text.getText());
    }
    //@FXML void (ActionEvent event) {}

    @FXML
    private Button Default;
    @FXML
    void Default(ActionEvent event) {
    }

    @FXML
    private Button Potential_1;
    void Potential_1(ActionEvent event) {
        System.out.println("event = " + event);
    }

    @FXML
    private Button Potential_2;
    void Potential_2(ActionEvent event) {
        System.out.println("event = " + event);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //textfield
        ENDSTEP_text.setPromptText(String.valueOf(Parameter.ENDSTEP));
        ATTEMPTSNUM_text.setPromptText(String.valueOf(Parameter.ATTEMPTSNUM));
        CELL_INTERVAL_text.setPromptText(String.valueOf(Parameter.CELL_INTERVAL));
        MAX_POTENTIAL_text.setPromptText(String.valueOf(Parameter.MAX_POTENTIAL));
        AGENT_POTENTIAL_WEIGHT_text.setPromptText(String.valueOf(Parameter.AGENT_KIM_POTENTIAL_RANGE));
        AGENT_POTENTIAL_RANGE_text.setPromptText(String.valueOf(Parameter.AGENT_KIM_POTENTIAL_RANGE));
        OBSTACLE_POTENTIAL_WEIGHT_text.setPromptText(String.valueOf(Parameter.OBSTACLE_KIM_POTENTIAL_WEIGHT));
        OBSTACLE_POTENTIAL_RANGE_text.setPromptText(String.valueOf(Parameter.OBSTACLE_KIM_POTENTIAL_RANGE));
        INIT_AGENT_NUM_text.setPromptText(String.valueOf(Parameter.INIT_AGENT_NUM));
        GOAL_AGENT_NUM_text.setPromptText(String.valueOf(Parameter.GOAL_AGENT_NUM));
        INIT_RANDOM_X1_text.setPromptText(String.valueOf(Parameter.INIT_RANDOM_X1));
        INIT_RANDOM_X2_text.setPromptText(String.valueOf(Parameter.INIT_RANDOM_X2));
        INIT_RANDOM_Y1_text.setPromptText(String.valueOf(Parameter.INIT_RANDOM_Y1));
        INIT_RANDOM_Y2_text.setPromptText(String.valueOf(Parameter.INIT_RANDOM_Y2));
        GOALAGENT_DESTINATION_text.setPromptText(String.valueOf(Parameter.GOALAGENT_DESTINATION));
        AGENT_SPEED_text.setPromptText(String.valueOf(Parameter.AGENT_SPEED));
        AGENT_RADIUS_text.setPromptText(String.valueOf(Parameter.AGENT_RADIUS));
        VIEW_RADIUS_text.setPromptText(String.valueOf(Parameter.VIEW_RADIUS));
        VIEW_DEGREE_text.setPromptText(String.valueOf(Parameter.VIEW_DEGREE));
        U_RANDOM_WALK_text.setPromptText(String.valueOf(Parameter.U_RANDOM_WALK));
        U_FOLLOW_AGENT_text.setPromptText(String.valueOf(Parameter.U_FOLLOW_AGENT));
        U_MOVE_GOAL_text.setPromptText(String.valueOf(Parameter.U_MOVE_GOAL));
        ALPHA_text.setPromptText(String.valueOf(Parameter.ALPHA));
        BETA_text.setPromptText(String.valueOf(Parameter.BETA));
        GAMMA_text.setPromptText(String.valueOf(Parameter.GAMMA));
        DELTA_text.setPromptText(String.valueOf(Parameter.DELTA));
        EPSILON_text.setPromptText(String.valueOf(Parameter.EPSILON));
    }
}