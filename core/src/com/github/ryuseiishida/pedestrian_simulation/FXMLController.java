package com.github.ryuseiishida.pedestrian_simulation;

import com.github.ryuseiishida.pedestrian_simulation.environment.Environment;
import com.github.ryuseiishida.pedestrian_simulation.render.RenderAgent;
import com.github.ryuseiishida.pedestrian_simulation.util.Inputs;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
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
            simulationLogText.setText(String.valueOf(selectedDirectory));
        }
    }
    @FXML private void menuEventBackground(ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            GDXController.setBackgroundTexture(String.valueOf(file));
            backgroundPathText.setText(String.valueOf(file));
        }
    }
    @FXML private void menuEventClose(ActionEvent event) {
        System.exit(0);
    }
    @FXML private void menuEventHowToUse(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("help");
        alert.setHeaderText("how to use Pedestrian Simulation");
        alert.setContentText("また今度書きます。m(_ _)m");
        alert.showAndWait();
    }
    @FXML private void menuEventAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
//        alert.setHeaderText("Pedestrian Simulation Version " + Parameter.version);
        alert.setHeaderText(Parameter.message);
        alert.setContentText("Pedestrian Simulation Version " + Parameter.version + "\n\n" +Parameter.versionMessage);
//        alert.setContentText(Parameter.message + "\n" +Parameter.versionMessage);
        alert.showAndWait();
    }

    // Tool Bar
    @FXML private Button startButton;
    @FXML void onStartButton(ActionEvent event) {
        if (GDXController.startFlag) {
            if(Environment.getUpdateFlag()) {
                startButton.setText("▶");
                Environment.setUpdateFlag(false);
            } else {
                startButton.setText("||");
                Environment.setUpdateFlag(true);
            }
        }
    }
    @FXML void eventNewButton(ActionEvent event) {
        GDXController.startFlag = true;
        GDXController.setBackgroundTexture(null);
        GDXController.resetEnvironment();
    }

    // TabPane
    // Parameter Tab
    // Environment Title Pane
    @FXML Text simulationLogText;
    @FXML void onSimulationLogButton(ActionEvent event) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            String path = String.valueOf(selectedDirectory);
            GDXController.setEnvironment(new Environment(path));
            simulationLogText.setText(path);
        }
    }
    @FXML Text backgroundPathText;
    @FXML void onBackgroundButton(ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            GDXController.setBackgroundTexture(String.valueOf(file));
            backgroundPathText.setText(String.valueOf(file));
        }
    }
    @FXML private TextField backgroundSizeXTextField, backgroundSizeYTextField;
    @FXML private void eventBackgroundSizeXButton(ActionEvent event) {
        if (!backgroundSizeXTextField.getText().isEmpty()) {
            Parameter.BACKGROUND_TEXTURE_SIZE.x = Float.parseFloat(backgroundSizeXTextField.getText());
            GDXController.setBackgroundTexture(null);
            GDXController.resetEnvironment();
        }
    }
    @FXML private void eventBackgroundSizeYButton(ActionEvent event) {
        if(!backgroundSizeYTextField.getText().isEmpty()) {
            Parameter.BACKGROUND_TEXTURE_SIZE.y = Float.parseFloat(backgroundSizeYTextField.getText());
            GDXController.resetEnvironment();
        }
    }
    @FXML private TextField scaleXText, scaleYText;
    @FXML private void eventScaleXButton(ActionEvent event) {
        if(!scaleXText.getText().isEmpty()) {
            Parameter.SCALE.x = Float.parseFloat(scaleXText.getText());
            GDXController.setBackgroundTexture(null);
            GDXController.resetEnvironment();
        }
    }
    @FXML private void eventScaleYButton(ActionEvent event) {
        if(!scaleYText.getText().isEmpty()) {
            Parameter.SCALE.y = Float.parseFloat(scaleYText.getText());
            GDXController.setBackgroundTexture(null);
            GDXController.resetEnvironment();
        }
    }

    @FXML private TextField cellIntervalText;
    @FXML private void eventCellIntervalButton(ActionEvent event) {
        if(!cellIntervalText.getText().isEmpty()) {
            Parameter.CELL_INTERVAL = Integer.parseInt(cellIntervalText.getText());
            GDXController.resetEnvironment();
        }
    }

    @FXML private TextField actionIntervalTextBox;
    @FXML private void eventActionIntervalButton(ActionEvent event) {
        if(!actionIntervalTextBox.getText().isEmpty()) {
            Parameter.AGENT_ACTION_INTERVAL = Integer.parseInt(actionIntervalTextBox.getText());
            GDXController.resetEnvironment();
        }
    }
    @FXML private TextField radiusTextBox;
    @FXML private void eventRadiusButton(ActionEvent event) {
        if(!radiusTextBox.getText().isEmpty()) {
            Parameter.AGENT_RADIUS = Float.parseFloat(radiusTextBox.getText());
            GDXController.resetEnvironment();
        }
    }
    @FXML private TextField speedTextBox;
    @FXML private void eventSpeedButton(ActionEvent event) {
        if(!speedTextBox.getText().isEmpty()) {
            Parameter.AGENT_SPEED = Float.parseFloat(speedTextBox.getText());
            GDXController.resetEnvironment();
        }
    }
    @FXML private TextField viewLengthTextBox;
    @FXML private void eventViewLengthButton(ActionEvent event) {
        if(!viewLengthTextBox.getText().isEmpty()) {
            Parameter.VIEW_RADIUS_LENGTH = Float.parseFloat(viewLengthTextBox.getText());
            GDXController.resetEnvironment();
        }
    }
    @FXML private TextField viewAngleTextBox;
    @FXML private void eventAngleButton(ActionEvent event) {
        if(!viewAngleTextBox.getText().isEmpty()) {
            Parameter.VIEW_DEGREE = Float.parseFloat(viewAngleTextBox.getText());
            GDXController.resetEnvironment();
        }
    }
    @FXML private TextField agentPotentialWeightTextBox;
    @FXML private void eventAgentPotentialWeightButton(ActionEvent event) {
        if(!agentPotentialWeightTextBox.getText().isEmpty()) {
            Parameter.AGENT_KIM_POTENTIAL_WEIGHT = Float.parseFloat(agentPotentialWeightTextBox.getText());
            GDXController.resetEnvironment();
        }
    }
    @FXML private TextField agentPotentialRangeTextBox;
    @FXML private void eventAgentPotentialRangeButton(ActionEvent event) {
        if(!agentPotentialRangeTextBox.getText().isEmpty()) {
            Parameter.AGENT_KIM_POTENTIAL_RANGE = Float.parseFloat(agentPotentialRangeTextBox.getText());
            GDXController.resetEnvironment();
        }
    }
    @FXML private TextField obstaclePotentialWeightTextBox;
    @FXML private void eventPotentialObstacleWeight(ActionEvent event) {
        if(!obstaclePotentialWeightTextBox.getText().isEmpty()) {
            Parameter.OBSTACLE_KIM_POTENTIAL_WEIGHT = Float.parseFloat(obstaclePotentialWeightTextBox.getText());
            GDXController.resetEnvironment();
        }
    }
    @FXML private TextField obstaclePotentialRangeTextBox;
    @FXML private void eventPotentialObstacleRangeButton(ActionEvent event) {
        if(!obstaclePotentialRangeTextBox.getText().isEmpty()) {
            Parameter.OBSTACLE_KIM_POTENTIAL_RANGE = Float.parseFloat(obstaclePotentialRangeTextBox.getText());
            GDXController.resetEnvironment();
        }
    }

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
    @FXML private TextField nonGoalAgentSpeedTextField;
    @FXML private void eventNonGoalAgentButton(ActionEvent event) {
        if(nonGoalAgentSpeedTextField.getText().isEmpty()) {
            Inputs.setFxCreateProperty("agentN");
        } else {
            Inputs.setFxCreateProperty("agentN" + "-" + nonGoalAgentSpeedTextField.getText());
        }
    }
    @FXML private TextField goalAgentSpeedTextField;
    @FXML private TextField goalAgentGoalIdTextField;
    @FXML private void eventGoalAgentButton(ActionEvent event) {
        if (!goalAgentGoalIdTextField.getText().isEmpty()) {
            if(goalAgentSpeedTextField.getText().isEmpty()) {
                Inputs.setFxCreateProperty("agentG" + "-" + goalAgentGoalIdTextField.getText());
            } else {
                Inputs.setFxCreateProperty("agentG" + "-" + goalAgentGoalIdTextField.getText() + "-" + goalAgentSpeedTextField.getText());
            }
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
    @FXML private TextField goalIdTextField;
    @FXML private TextField goalWidthTextField;
    @FXML private TextField goalHeightField;
    @FXML private Button GoalButton;
    @FXML private void GoalButton(ActionEvent event) {
        if (!goalIdTextField.getText().isEmpty() && !goalWidthTextField.getText().isEmpty() && !goalHeightField.getText().isEmpty()) {
            String fxProperty = String.format("Goal-%s-%sf-%sf", goalIdTextField.getText(), goalWidthTextField.getText(), goalHeightField.getText());
            Inputs.setFxCreateProperty(fxProperty);
        }
    }
    @FXML private void eventDeleteGoalButton(ActionEvent event) {
         Environment.deleteAllGoalFlag = true;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        randomAgentChoiceBox.getItems().addAll("select Agent(未実装)", "non_goal","goal");
        randomAgentChoiceBox.getSelectionModel().select(0);
    }


}
