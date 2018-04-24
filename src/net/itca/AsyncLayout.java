package net.itca;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AsyncLayout {

    public static void main(String[] args) {
        System.out.println("starting async layout");
        var aslayout = new AsyncLayout();
    }

    public AsyncLayout(){
        var frame = new JFrame();
        var mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(createSouthPanel(), BorderLayout.SOUTH);
        frame.setContentPane(mainPanel);
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private int count = 0;
    private JPanel createCenterPanel(){
        var centerPanel = new JPanel(new GridLayout(10,10));
        centerPanel.add(new JLabel("center"));
        Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(10_000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        centerPanel.add(new JLabel(String.format("Center: %s", count++)));
                        System.out.println("added label");
                        centerPanel.repaint();
                        centerPanel.revalidate();


                    }
                }, 100, 1000, TimeUnit.MILLISECONDS);
        return centerPanel;
    }

    private JPanel createSouthPanel(){
        var southPanel = new JPanel();
        southPanel.add(new JTextField(10));
        return southPanel;
    }


}
