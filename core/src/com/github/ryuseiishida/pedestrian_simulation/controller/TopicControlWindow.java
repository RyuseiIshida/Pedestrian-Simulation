package com.github.ryuseiishida.pedestrian_simulation.controller;

import com.github.ryuseiishida.pedestrian_simulation.render.RenderTopic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class TopicControlWindow implements Initializable {
    @FXML
    private TextField numTopicField;

    private int getNumTopic() {
        return Integer.parseInt(numTopicField.getText());
    }

    private void setNumTopic(int num) {
        numTopicField.setText(String.valueOf(num));
        RenderTopic.setSelectTopicNumber(num);
    }

    @FXML
    private void eventNumTopicField(ActionEvent event) {
        if (getNumTopic() > 1 && getNumTopic() <= RenderTopic.getTopicK()) {
            setNumTopic(getNumTopic());
        }
    }

    @FXML
    private void onPlusButton(ActionEvent event) {
        if (!numTopicField.getText().isEmpty() && getNumTopic() < RenderTopic.getTopicK()) {
            setNumTopic(getNumTopic() + 1);
        }
    }

    @FXML
    private void onMinusButton(ActionEvent event) {
        if (!numTopicField.getText().isEmpty() && getNumTopic() > 1) {
            setNumTopic(getNumTopic() - 1);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
