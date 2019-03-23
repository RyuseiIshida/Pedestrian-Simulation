package com.simulation.pedestrian;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ParameterWindow extends JFrame implements ActionListener {
    JTextField text1;
    JTextField text2;
    JLabel label;

    public static void main(String args[]) {
        ParameterWindow frame = new ParameterWindow("タイトル");
        frame.setVisible(true);

    }

    ParameterWindow(String title) {
        setTitle(title);
        setBounds(100, 100, 300, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel p = new JPanel();

        text1 = new JTextField("住所1", 20);
        text2 = new JTextField("住所2", 20);
        JButton button = new JButton("取得");
        button.addActionListener(this);
        label = new JLabel();

        p.add(text1);
        p.add(text2);
        p.add(button);

        Container contentPane = getContentPane();
        contentPane.add(p, BorderLayout.CENTER);
        contentPane.add(label, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e) {
        label.setText(text1.getText() + text2.getText());
    }
}

