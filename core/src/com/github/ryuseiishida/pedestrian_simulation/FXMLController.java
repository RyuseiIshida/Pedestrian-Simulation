package com.github.ryuseiishida.pedestrian_simulation;

import com.github.ryuseiishida.pedestrian_simulation.environment.Environment;
import com.github.ryuseiishida.pedestrian_simulation.render.RenderAgent;
import com.github.ryuseiishida.pedestrian_simulation.util.Inputs;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLController implements Initializable {

    @FXML private void menuEventOpenSimulationLog(ActionEvent event) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            GDXController.setEnvironment(new Environment(String.valueOf(selectedDirectory)));
        }
    }
    @FXML private void menuEventSave(ActionEvent event) { }
    @FXML private void menuEventClose(ActionEvent event) {
        System.exit(0);
    }

    // Tool Bar
    @FXML void onStartButton(ActionEvent event) {
        if (GDXController.startFlag) {
            Environment.setUpdateFlag(true);
        }
    }
    @FXML void onStopButton(ActionEvent event) {
        Environment.setUpdateFlag(false);
    }
    @FXML void eventNewButton(ActionEvent event) {
        GDXController.startFlag = true;
        GDXController.setBackgroundTexture(null);
        GDXController.resetEnvironment();
    }

    // TabPane
    // Parameter Tab
    // Environment Title Pane
    @FXML void onBackgroundButton(ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            GDXController.setBackgroundTexture(String.valueOf(file));
        }
    }
    @FXML private TextField scaleXText, scaleYText;
    @FXML private TextField cellIntervalText;
    @FXML private TextField actionIntervalTextBox;
    @FXML private TextField radiusTextBox;
    @FXML private TextField speedTextBox;
    @FXML private TextField viewLengthTextBox;
    @FXML private TextField viewAngleTextBox;
    @FXML private TextField obstacleWeightTextBox, obstacleRangeTextBox;
    @FXML private TextField agentPotentialWeightTextBox;
    @FXML private TextField agentPotentialRangeTextBox;
    @FXML private TextField obstaclePotentialWeightTextBox;
    @FXML private TextField obstaclePotentialRangeTextBox;

    // View Tab
    @FXML void eventFontRadioButton(ActionEvent event) {
    }
    @FXML void eventAgentViewRadioButton(ActionEvent event) {
        RenderAgent.switchViewFlag();
    }
    @FXML void eventAgentGoalLineRadioButton(ActionEvent event) {
        RenderAgent.switchMoveGoalLineFlag();
    }
    @FXML void eventObstacleRadioButton(ActionEvent event) {
//        Main.setDrawView();
    }

    // create
    // Agent Title Pane
    @FXML private void eventNonGoalAgentButton(ActionEvent event) {
        Inputs.setFxCreateProperty("agentN");
    }
    @FXML private TextField goalAgentSpeedTextField;
    @FXML private TextField goalAgentGoalIdTextField;
    @FXML private void eventGoalAgentButton(ActionEvent event) {
        if (!goalAgentGoalIdTextField.getText().isEmpty()) {
            Inputs.setFxCreateProperty("agentG" + "-" + goalAgentGoalIdTextField.getText());
        }
    }
    @FXML private TextField randomSpawnAgentNumTextField;
    @FXML private ChoiceBox<String> randomAgentChoiceBox;
    @FXML private void eventRandomSpawnButton(ActionEvent event) {
        if (!randomSpawnAgentNumTextField.getText().isEmpty()) {
            Parameter.INIT_AGENT_NUM = Integer.parseInt(randomSpawnAgentNumTextField.getText());
            Environment.spawnAgentsFlag = true;
        }
    }
    @FXML private void eventDeleteAgentButton(ActionEvent event) {
        Environment.deleteAllAgentFlag = true;
    }

    // Obstacle Title Pane
    @FXML private void eventObstacleLineButton(ActionEvent event) {
        Inputs.setFxCreateProperty("ObstacleLine");
    }
    @FXML private void eventDeleteLineButton(ActionEvent event) {
         Environment.deleteAllObstacleFlag = true;
    }
    // Goal Title Pane
    @FXML private Button GoalButton;
    @FXML private void GoalButton(ActionEvent event) {
        if (!goalIdTextField.getText().isEmpty() && !goalWidthTextField.getText().isEmpty() && !goalHeightField.getText().isEmpty()) {
            String fxProperty = String.format("Goal-%s-%sf-%sf", goalIdTextField.getText(), goalWidthTextField.getText(), goalHeightField.getText());
            Inputs.setFxCreateProperty(fxProperty);
        }
    }
    @FXML private TextField goalIdTextField;
    @FXML private TextField goalWidthTextField;
    @FXML private TextField goalHeightField;
    @FXML private void eventDeleteGoalButton(ActionEvent event) {
         Environment.deleteAllGoalFlag = true;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        randomAgentChoiceBox.getItems().addAll("select Agent(未実装)", "non_goal","goal");
        randomAgentChoiceBox.getSelectionModel().select(0);
    }


}
