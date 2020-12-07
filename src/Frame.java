import javax.swing.*;
import java.awt.*;
import java.nio.file.*;
import javax.imageio.ImageIO;
import java.io.*;

class Frame extends JFrame {
    private final static String bannerPath = "/res/danmemo.jpg";

    public Frame() {
        super("ダンメモ News Crawler");
        this.setLayout(new FlowLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(900, 1000);
        this.setResizable(false);

        //Path imgPath = Paths.get(System.getProperty("user.dir")).getParent();
        ImageIcon icon = new ImageIcon("./danmemo.jpg");
        JLabel imgLabel = new JLabel(icon);

        JPanel topPanel = new JPanel();
        JPanel middlePanel = new JPanel();

        topPanel.add(imgLabel);
        middlePanel.add(new JButton("Bakana"));

        // add panels to frame
        this.add(topPanel);
        this.add(middlePanel);

        this.setVisible(true);
    }
}
