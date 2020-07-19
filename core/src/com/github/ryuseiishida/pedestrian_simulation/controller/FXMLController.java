package com.github.ryuseiishida.pedestrian_simulation.controller;

import com.badlogic.gdx.Gdx;
import com.github.ryuseiishida.pedestrian_simulation.util.LoadLog;
import com.github.ryuseiishida.pedestrian_simulation.util.WriteLog;
import com.github.ryuseiishida.pedestrian_simulation.environment.Environment;
import com.github.ryuseiishida.pedestrian_simulation.render.*;
import com.github.ryuseiishida.pedestrian_simulation.util.Inputs;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLController implements Initializable {
    private void reset() {
        GdxController.startFlag = true;
        GdxController.setBackgroundTexture(null);
        GdxController.resetEnvironment();
        startButton.setText("▶");
    }

    @FXML
    private Button saveSimulationButton;

    @FXML
    private void menuEventOpenWorkSpace(ActionEvent event) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);
        String dirPath = String.valueOf(selectedDirectory);
        if (selectedDirectory != null) {
            LoadLog.setObstacle(dirPath);
            LoadLog.setGoal(dirPath);
            LoadLog.setInitAgent(dirPath);
        }
    }

    @FXML
    private void menuEventOpenSimulationLog(ActionEvent event) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            GdxController.setEnvironment(new Environment(String.valueOf(selectedDirectory)));
            simulationLogText.setText(selectedDirectory.getName());
        }
    }

    @FXML
    private void menuEventOpenBackground(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            String path = String.valueOf(file);
            GdxController.setBackgroundTexture(path);
            backgroundPathText.setText(file.getName());
        }
    }

    @FXML
    private void menuEventClose(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void menuEventHowToUse(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("help");
        alert.setHeaderText("how to use Pedestrian Simulation");
        alert.setContentText("使い方\n詳細を表示してください。");
        TextArea area = new TextArea(help());
        area.setWrapText(true);
        area.setEditable(false);
        alert.getDialogPane().setExpandableContent(area);
        alert.showAndWait();
    }

    @FXML
    private void menuEventAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(Parameter.ABOUT_MESSAGE);
        alert.setContentText("Pedestrian Simulation Version " + Parameter.VERSION + "\n\n" + Parameter.VERSION_MESSAGE);
        alert.showAndWait();
    }

    // Tool Bar
    @FXML
    private Button startButton;

    @FXML
    void onStartButton(ActionEvent event) {
        if (GdxController.startFlag) {
            if (Environment.getUpdateFlag()) {
                startButton.setText("▶");
                Environment.setUpdateFlag(false);
            } else {
                startButton.setText("||");
                Environment.setUpdateFlag(true);
            }
        }
    }

    @FXML
    private void menuEventSaveWorkSpace(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Work Space");
        File selectedDirectory = fileChooser.showSaveDialog(null);
        String dirPath = String.valueOf(selectedDirectory);
        if (selectedDirectory != null) {
            selectedDirectory.mkdir();
            WriteLog.writeParameterLog(dirPath);
            WriteLog.writeAgentInitLog(dirPath);
            WriteLog.writeGoalLog(dirPath);
            WriteLog.writeObstacleLog(dirPath);
        }
    }

    @FXML
    void eventSaveSimulationButton(ActionEvent event) {
        if (saveSimulationButton.getTextFill() == Color.RED) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Simulation Log");
            File directory = fileChooser.showSaveDialog(null);
            String dirPath = String.valueOf(directory);
            if (directory != null) {
                saveSimulationButton.setTextFill(Color.BLACK);
                directory.mkdir();
                Parameter.WRITE_LOG_PATH = dirPath;
                WriteLog.writeParameterLog(dirPath);
                WriteLog.writeObstacleLog(dirPath);
                WriteLog.writeGoalLog(dirPath);
                WriteLog.writeAgentLog(dirPath);
            }
        } else {
            saveSimulationButton.setTextFill(Color.RED);
            Parameter.IS_WRITE_LOG = true;
            Environment.setStep(0);
        }
    }

    @FXML
    void eventEraserButton(ActionEvent event) {
        Inputs.setFxCreateProperty("eraser");
    }

    @FXML
    void eventNewButton(ActionEvent event) {
        reset();
    }

    // TabPane
    // Parameter Tab
    // Environment Title Pane
    @FXML
    Text simulationLogText;

    @FXML
    void onSimulationLogButton(ActionEvent event) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            String path = String.valueOf(selectedDirectory);
            GdxController.setEnvironment(new Environment(path));
            simulationLogText.setText(selectedDirectory.getName());
        }
    }

    @FXML
    Text backgroundPathText;

    @FXML
    void onBackgroundButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            String path = String.valueOf(file);
            GdxController.setBackgroundTexture(path);
            backgroundPathText.setText(file.getName());
        }
    }

    @FXML
    private TextField backgroundSizeXTextField, backgroundSizeYTextField;

    @FXML
    private void eventBackgroundSizeXButton(ActionEvent event) {
        if (!backgroundSizeXTextField.getText().isEmpty()) {
            Parameter.BACKGROUND_TEXTURE_SIZE.x = Float.parseFloat(backgroundSizeXTextField.getText());
            reset();
        }
    }

    @FXML
    private void eventBackgroundSizeYButton(ActionEvent event) {
        if (!backgroundSizeYTextField.getText().isEmpty()) {
            Parameter.BACKGROUND_TEXTURE_SIZE.y = Float.parseFloat(backgroundSizeYTextField.getText());
            reset();
        }
    }

    @FXML
    private TextField scaleXText, scaleYText;

    @FXML
    private void eventScaleXButton(ActionEvent event) {
        if (!scaleXText.getText().isEmpty()) {
            Parameter.SCALE.x = Float.parseFloat(scaleXText.getText());
            reset();
        }
    }

    @FXML
    private void eventScaleYButton(ActionEvent event) {
        if (!scaleYText.getText().isEmpty()) {
            Parameter.SCALE.y = Float.parseFloat(scaleYText.getText());
            GdxController.setBackgroundTexture(null);
            reset();
        }
    }

    @FXML
    private TextField cellIntervalText;

    @FXML
    private void eventCellIntervalButton(ActionEvent event) {
        if (!cellIntervalText.getText().isEmpty()) {
            Parameter.CELL_INTERVAL = Integer.parseInt(cellIntervalText.getText());
            reset();
        }
    }

    @FXML
    private TextField radiusTextBox;

    @FXML
    private void eventRadiusButton(ActionEvent event) {
        if (!radiusTextBox.getText().isEmpty()) {
            Parameter.AGENT_RADIUS = Float.parseFloat(radiusTextBox.getText());
            reset();
        }
    }

    @FXML
    private TextField speedTextBox;

    @FXML
    private void eventSpeedButton(ActionEvent event) {
        if (!speedTextBox.getText().isEmpty()) {
            Parameter.AGENT_SPEED = Float.parseFloat(speedTextBox.getText());
            reset();
        }
    }

    @FXML
    private TextField viewLengthTextBox;

    @FXML
    private void eventViewLengthButton(ActionEvent event) {
        if (!viewLengthTextBox.getText().isEmpty()) {
            Parameter.VIEW_RADIUS_LENGTH = Float.parseFloat(viewLengthTextBox.getText());
            reset();
        }
    }

    @FXML
    private TextField viewAngleTextBox;

    @FXML
    private void eventAngleButton(ActionEvent event) {
        if (!viewAngleTextBox.getText().isEmpty()) {
            Parameter.VIEW_DEGREE = Float.parseFloat(viewAngleTextBox.getText());
            reset();
        }
    }

    @FXML
    private TextField goalPotentialWeightTextBox;

    @FXML
    private void eventGoalPotentialWeightButton(ActionEvent event) {
        if (!goalPotentialWeightTextBox.getText().isEmpty()) {
            Parameter.GOAL_POTENTIAL_WEIGHT = Float.parseFloat(goalPotentialWeightTextBox.getText());
            reset();
        }
    }

    @FXML
    private TextField goalPotentialRangeTextBox;

    @FXML
    private void eventGoalPotentialRangeButton(ActionEvent event) {
        if (!goalPotentialRangeTextBox.getText().isEmpty()) {
            Parameter.GOAL_POTENTIAL_RANGE = Float.parseFloat(goalPotentialRangeTextBox.getText());
            reset();
        }
    }

    @FXML
    private TextField agentPotentialWeightTextBox;

    @FXML
    private void eventAgentPotentialWeightButton(ActionEvent event) {
        if (!agentPotentialWeightTextBox.getText().isEmpty()) {
            Parameter.AGENT_POTENTIAL_WEIGHT = Float.parseFloat(agentPotentialWeightTextBox.getText());
            reset();
        }
    }

    @FXML
    private TextField agentPotentialRangeTextBox;

    @FXML
    private void eventAgentPotentialRangeButton(ActionEvent event) {
        if (!agentPotentialRangeTextBox.getText().isEmpty()) {
            Parameter.AGENT_POTENTIAL_RANGE = Float.parseFloat(agentPotentialRangeTextBox.getText());
            reset();
        }
    }

    @FXML
    private TextField obstaclePotentialWeightTextBox;

    @FXML
    private void eventPotentialObstacleWeight(ActionEvent event) {
        if (!obstaclePotentialWeightTextBox.getText().isEmpty()) {
            Parameter.OBSTACLE_POTENTIAL_WEIGHT = Float.parseFloat(obstaclePotentialWeightTextBox.getText());
            reset();
        }
    }

    @FXML
    private TextField obstaclePotentialRangeTextBox;

    @FXML
    private void eventPotentialObstacleRangeButton(ActionEvent event) {
        if (!obstaclePotentialRangeTextBox.getText().isEmpty()) {
            Parameter.OBSTACLE_POTENTIAL_RANGE = Float.parseFloat(obstaclePotentialRangeTextBox.getText());
            reset();
        }
    }

    // View Tab
    @FXML
    void eventBackgroundRadioButton(ActionEvent event) {
        RenderBackground.switchRenderFlag();
    }

    @FXML
    void eventFontRadioButton(ActionEvent event) {
        RenderFont.switchRenderFlag();
    }

    @FXML
    void eventAgentViewRadioButton(ActionEvent event) {
        RenderAgent.switchViewFlag();
    }

    @FXML
    void eventAgentGoalLineRadioButton(ActionEvent event) {
        RenderAgent.switchMoveGoalLineFlag();
    }

    @FXML
    void eventObstacleRadioButton(ActionEvent event) {
        RenderObstacle.switchRenderFlag();
    }

    @FXML
    void eventGoalRadioButton(ActionEvent event) {
        RenderGoal.switchRenderFlag();
    }

    // create
    // Agent Title Pane
    @FXML
    private TextField nonGoalAgentSpeedTextField;

    @FXML
    private void eventNonGoalAgentButton(ActionEvent event) {
        if (nonGoalAgentSpeedTextField.getText().isEmpty()) {
            Inputs.setFxCreateProperty("agentN");
        } else {
            Inputs.setFxCreateProperty("agentN" + "-" + nonGoalAgentSpeedTextField.getText());
        }
    }

    @FXML
    private TextField goalAgentSpeedTextField;
    @FXML
    private TextField goalAgentGoalIdTextField;

    @FXML
    private void eventGoalAgentButton(ActionEvent event) {
        if (!goalAgentGoalIdTextField.getText().isEmpty()) {
            if (goalAgentSpeedTextField.getText().isEmpty()) {
                Inputs.setFxCreateProperty("agentG" + "-" + goalAgentGoalIdTextField.getText());
            } else {
                Inputs.setFxCreateProperty("agentG" + "-" + goalAgentGoalIdTextField.getText() + "-" + goalAgentSpeedTextField.getText());
            }
        }
    }

    @FXML
    private TextField randomSpawnRangeTextField;
    @FXML
    private TextField randomSpawnGoalIDText;
    @FXML
    private TextField randomSpawnGoalIDTextField;
    @FXML
    private TextField randomSpawnAgentNumTextField;
    @FXML
    private RadioButton randomSpawnNonGoalRadioButton;

    @FXML
    private void randomSpawnNonGoalRadioButton(ActionEvent event) {
        if (randomSpawnNonGoalRadioButton.isSelected()) {
            randomSpawnGoalRadioButton.setSelected(false);
            randomSpawnGoalIDTextField.setDisable(true);
        } else randomSpawnGoalRadioButton.setSelected(true);
    }

    @FXML
    private RadioButton randomSpawnGoalRadioButton;

    @FXML
    private void randomSpawnGoalRadioButton(ActionEvent event) {
        if (randomSpawnGoalRadioButton.isSelected()) {
            randomSpawnNonGoalRadioButton.setSelected(false);
            randomSpawnGoalIDTextField.setDisable(false);
        } else randomSpawnNonGoalRadioButton.setSelected(true);
    }

    @FXML
    private void eventRandomSpawnButton(ActionEvent event) {
        if (!randomSpawnRangeTextField.getText().isEmpty() && !randomSpawnAgentNumTextField.getText().isEmpty()) {
            if (randomSpawnNonGoalRadioButton.isSelected()) {
                Inputs.setFxCreateProperty("RandomNonGoalSpawn" + "-"
                        + randomSpawnRangeTextField.getText() + "-"
                        + randomSpawnAgentNumTextField.getText());
            } else if (!randomSpawnGoalIDTextField.getText().isEmpty()) { //randomSpawnBGoalRadioButton.isSelected()
                Inputs.setFxCreateProperty("RandomGoalSpawn" + "-"
                        + randomSpawnRangeTextField.getText() + "-"
                        + randomSpawnAgentNumTextField.getText() + "-"
                        + randomSpawnGoalIDTextField.getText());
            } else {
                randomSpawnNonGoalRadioButton.setSelected(true);
                randomSpawnGoalRadioButton.setSelected(false);
            }
        }
    }

    @FXML
    private void eventDeleteAgentButton(ActionEvent event) {
        Environment.removeAllAgent();
    }

    // Obstacle Title Pane
    @FXML
    private void eventObstacleLineButton(ActionEvent event) {
        Inputs.setFxCreateProperty("ObstacleLine");
    }

    @FXML
    private void eventDeleteLineButton(ActionEvent event) {
        Environment.removeAllObstacle();
    }

    // Goal Title Pane
    @FXML
    private TextField goalIdTextField;
    @FXML
    private TextField goalWidthTextField;
    @FXML
    private TextField goalHeightField;
    @FXML
    private Button GoalButton;

    @FXML
    private void GoalButton(ActionEvent event) {
        if (!goalIdTextField.getText().isEmpty() && !goalWidthTextField.getText().isEmpty() && !goalHeightField.getText().isEmpty()) {
            String fxProperty = String.format("Goal-%s-%sf-%sf", goalIdTextField.getText(), goalWidthTextField.getText(), goalHeightField.getText());
            Inputs.setFxCreateProperty(fxProperty);
        }
    }

    @FXML
    private void eventDeleteGoalButton(ActionEvent event) {
        Environment.removeAllGoal();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        backgroundSizeXTextField.setPromptText(String.valueOf(Parameter.BACKGROUND_TEXTURE_SIZE.x));
        backgroundSizeYTextField.setPromptText(String.valueOf(Parameter.BACKGROUND_TEXTURE_SIZE.y));
        scaleXText.setPromptText(String.valueOf(Parameter.SCALE.x));
        scaleYText.setPromptText(String.valueOf(Parameter.SCALE.y));
        cellIntervalText.setPromptText(String.valueOf(Parameter.CELL_INTERVAL));
        radiusTextBox.setPromptText(String.valueOf(Parameter.AGENT_RADIUS));
        speedTextBox.setPromptText(String.valueOf(Parameter.AGENT_SPEED));
        viewLengthTextBox.setPromptText(String.valueOf(Parameter.VIEW_RADIUS_LENGTH));
        viewAngleTextBox.setPromptText(String.valueOf(Parameter.VIEW_DEGREE));
        goalPotentialWeightTextBox.setPromptText(String.valueOf(Parameter.GOAL_POTENTIAL_WEIGHT));
        goalPotentialRangeTextBox.setPromptText(String.valueOf(Parameter.GOAL_POTENTIAL_RANGE));
        agentPotentialRangeTextBox.setPromptText(String.valueOf(Parameter.AGENT_POTENTIAL_RANGE));
        agentPotentialWeightTextBox.setPromptText(String.valueOf(Parameter.AGENT_POTENTIAL_WEIGHT));
        obstaclePotentialRangeTextBox.setPromptText(String.valueOf(Parameter.OBSTACLE_POTENTIAL_RANGE));
        obstaclePotentialWeightTextBox.setPromptText(String.valueOf(Parameter.OBSTACLE_POTENTIAL_WEIGHT));
    }

    public String help() {
        String message = "Pedestrian Simulation(ver 1.0x)の使い方\n" +
                "\n" +
                "ver 1.0xの説明\n" +
                "マウス操作による簡単なシミュレーションを行うことができる。\n" +
                "\n" +
                "はじめかた\n" +
                "Newボタンでスタート画面からシミュレーション画面へ移行する\n" +
                "またこのボタンはリセット機能も持っている\n" +
                "\n" +
                "- ▶/||ボタン シミュレーションの開始・一時停止\n" +
                "- ●ボタン シミュレーションの記録\n" +
                "\n" +
                "\n" +
                "↓↓Editの説明\n" +
                "\n" +
                "Editはマウス操作によってシミュレーション空間にオブジェクトの設置できる機能\n" +
                "\n" +
                "non_goal_agent\n" +
                "- speed 歩行速度(空の場合はパラメータ設定が適用される)\n" +
                "\n" +
                "goal_agent\n" +
                "- speed 歩行速度(空の場合はパラメータ設定が適用される)\n" +
                "- goal id 設置済みのGoalのIDと同じ値を入力する(idが正しくないとmouse setがアクティブにできない)\n" +
                "\n" +
                "random_spawn_agent\n" +
                "- agent_num 設置するエージェント数\n" +
                "- agent type 現在は全てnon_goal_agentが設置される\n" +
                "- spawn このボタンで空間にエージェントがランダムに設置される\n" +
                "\n" +
                "goal\n" +
                "- goal_id 出口に紐付ける任意のID\n" +
                "- width 横幅サイズ\n" +
                "- height 縦幅サイズ\n" +
                "\n" +
                "line\n" +
                "1回のクリックで始点を設定し、2回目のクリックで終点を設定する\n" +
                "\n" +
                "\n" +
                "↓↓避難エージェントモデルの説明\n" +
                "\n" +
                "出口を知らないエージェント(non_goal_agent)\n" +
                "行動ルール\n" +
                "- ランダム行動(ランダムな方向に移動し、壁が視野に入ると方向を変更)\n" +
                "- 追従行動(視野内にgoal_agentがいれば、追従する)\n" +
                "\n" +
                "出口を知っているエージェント(goal_agent)\n" +
                "- 出口移動(自分の知っている出口へ移動するエージェント)\n" +
                "\n";
        return message;
    }


}
