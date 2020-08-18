package com.github.ryuseiishida.pedestrian_simulation.controller;

import com.github.ryuseiishida.pedestrian_simulation.analysis.AnalyzeLogLDA;
import com.github.ryuseiishida.pedestrian_simulation.render.RenderTopic;
import com.github.ryuseiishida.pedestrian_simulation.util.LoadLog;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class CreateCorpusWindow implements Initializable {
    @FXML
    private VBox vbox;

    @FXML
    private TextField mapSplitSizeTextField;
    @FXML
    private TextField stepSplitSizeTextField;
    @FXML
    private Button cancelButton;
    @FXML
    private Button createButton;
    @FXML
    private ProgressBar progressBar;

    @FXML
    private void eventMapSplitSizeTextField(ActionEvent event) {
        if (mapSplitSizeTextField.getText().isEmpty()) return;
        AnalyzeLogLDA.setMapSplitSize(Integer.parseInt(mapSplitSizeTextField.getText()));
    }

    @FXML
    private void onCancelButton(ActionEvent event) {
        closeWindow();
    }

    @FXML
    private void onCreateButton(ActionEvent event) throws InterruptedException {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null && ifNotCorrectFolder(selectedDirectory)) {
            AnalyzeLogLDA analyzeLogLDA = new AnalyzeLogLDA(selectedDirectory.getPath());
            if (stepSplitSizeTextField.getText().isEmpty())
                analyzeLogLDA.recordStepSplit(60);
            else analyzeLogLDA.recordStepSplit(Integer.parseInt(stepSplitSizeTextField.getText()));
            analyzeLogLDA.recordGroupSizeSplit();
            closeWindow();
        }
    }

    private boolean ifNotCorrectFolder(File file) {
        if(LoadLog.searchAgentFileList(file.getPath()).size() == 0) {
            folderAlert(file);
            return false;
        } else {
            return true;
        }
    }

    private void folderAlert(File file) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText("It is not the correct folder");
        alert.setContentText("Select path : " + file.getPath() + "\nThe folder must contain \"agent.txt\"");
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
        RenderTopic.setRenderCellFlag(false);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
