package Graphics;

import javax.imageio.ImageIO;
import javax.swing.*;

import Models.Algorithm;
import Models.Graph;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class MainWindow extends JPanel implements Resources.Constants {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Graph graph;
    private GraphPanel graphPanel;

    public MainWindow() {
        super.setLayout(new BorderLayout());
        setGraphPanel();
    }

    private void setGraphPanel() {
        graph = new Graph();
        graphPanel = new GraphPanel(graph);
        graphPanel.setPreferredSize(new Dimension(1000, 596));
        JScrollPane scroll = new JScrollPane();
        scroll.setViewportView(graphPanel);
        scroll.getViewport().setViewPosition(new Point(0, 0));
        add(scroll, BorderLayout.CENTER);
        setTopPanel();
        setButtons();
    }

   public static void setTopPanel() {
	JLabel info=new JLabel("                                                 COVID-19 Hospital Locator");
	info.setFont(new Font("Serif", Font.BOLD, 34));
	info.setForeground(new Color(255, 0, 0));      
	info.setVerticalAlignment(SwingConstants.CENTER);
	JLabel status=new JLabel();
	status.setText("                                                                                                                                                 No.of Hospitals: "+Models.Graph.hospitalNode.size());
	status.setFont(new Font("Serif", Font.BOLD, 16));
	status.setForeground(new Color(255, 0, 0));      
	status.setVerticalAlignment(SwingConstants.CENTER);
	JLabel status1=new JLabel();
	status1.setFont(new Font("Serif", Font.BOLD, 16));
	status1.setForeground(new Color(255, 0, 0));      
	status1.setVerticalAlignment(SwingConstants.CENTER);
	JPanel panel=new JPanel();
	panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	panel.setPreferredSize(new Dimension(300, 100));
	panel.setBackground(new Color(7,0,0));
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	panel.add(info);
	panel.add(status);
	panel.add(status1);
	MainPackage.Main.j.add(panel,BorderLayout.NORTH);
	status.setText("                                                                                                                                                  No.of Hopitals: "+Models.Graph.hospitalNode.size());
	status1.setText("                                                                                                                                           No.of Red Zone Areas: "+Models.Graph.redZoneNode.size());
    }

    private void setButtons() {
        JButton run = new JButton();
        setupIcon(run, "run");
        JButton reset = new JButton();
        setupIcon(reset, "reset");
        final JButton info = new JButton();
        setupIcon(info, "info");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(DrawUtils.parseColor("#000000"));
        buttonPanel.add(reset);
        buttonPanel.add(run);
        buttonPanel.add(info);

        reset.addActionListener((ActionEvent e) -> {
            graphPanel.reset();
        });

        info.addActionListener((ActionEvent e) -> {
            JOptionPane.showMessageDialog(null,
                    Resources.Constants.read,
                    "Information Panel",
                    JOptionPane.QUESTION_MESSAGE);
        });

        run.addActionListener((ActionEvent e) -> {
            Algorithm dijkstraAlgorithm = new Algorithm(graph);
            try {
                dijkstraAlgorithm.run();
                graphPanel.setPath(dijkstraAlgorithm.getDestinationPath());
            } catch (IllegalStateException ise) {
                JOptionPane.showMessageDialog(null, ise.getMessage());
            }
        });

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupIcon(JButton button, String img) {
        try {
            Image icon = ImageIO.read(getClass().getResource(
                    "/resources/" + img + ".png"));
            ImageIcon imageIcon = new ImageIcon(icon);
            button.setIcon(imageIcon);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setContentAreaFilled(false);
        } catch (IOException e) {
            e.getMessage();
        }
    }

}
