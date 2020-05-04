package Graphics;

import javax.swing.*;

import Models.Edge;
import Models.Graph;
import Models.Node;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;


public class GraphPanel extends JPanel implements MouseListener, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private DrawUtils drawUtils;

	private Graph graph;

	private Node selectedNode = null;
	private Node hoveredNode = null;
	private Edge hoveredEdge = null;

	private java.util.List<Node> path = null;

	private Point cursor;

	public GraphPanel(Graph graph){
		this.graph = graph;

		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void setPath(List<Node> path) {
		this.path = path;
		hoveredEdge = null;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D graphics2d = (Graphics2D) g;
		graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		drawUtils = new DrawUtils(graphics2d);

		if(graph.isSolved()){
			drawUtils.drawPath(path);
		}

		if(selectedNode != null && cursor != null){
			Edge e = new Edge(selectedNode, new Node(cursor));
			drawUtils.drawEdge(e);
		}

		for(Edge edge : graph.getEdges()){
			if(edge == hoveredEdge)
				drawUtils.drawHoveredEdge(edge);
			drawUtils.drawEdge(edge);
		}

		for(Node node : graph.getNodes()){
			if(node == selectedNode || node == hoveredNode)
				drawUtils.drawHalo(node);
			if(graph.isSource(node))
				drawUtils.drawSourceNode(node);
			else if(graph.isDestination(node))
				drawUtils.drawDestinationNode(node);
			else if(graph.isRedZoneNode(node))
				drawUtils.drawRedZoneNode(node);
			else if(graph.isHospitalNode(node))
				drawUtils.drawHospitalNode(node);
			else
				drawUtils.drawNode(node);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		MainWindow.setTopPanel();
		Node selected = null;
		for(Node node : graph.getNodes()) {
			if(DrawUtils.isWithinBounds(e, node.getCoord())){
				selected = node;
				break;
			}
		}

		if(selected!=null) {
			if(e.isControlDown() && e.isShiftDown()){
				graph.deleteNode(selected);
				graph.setSolved(false);
				repaint();
				MainWindow.setTopPanel();
				return;
			} else if(e.isControlDown() && graph.isSolved()){
				path = selected.getPath();
				repaint();
				return;
			} else if(e.isShiftDown()){
				if(SwingUtilities.isLeftMouseButton(e)){
					if(graph.isRedZoneNode(selected))
						JOptionPane.showMessageDialog(null,"A red zone area can't be marked as the source","Warning",JOptionPane.WARNING_MESSAGE);
					else {
						if(!graph.isDestination(selected))
							graph.setSource(selected);
						else
							JOptionPane.showMessageDialog(null, "Destination can't be set as the Source","Warning",JOptionPane.WARNING_MESSAGE);
					}
					MainWindow.setTopPanel();
				} else if(SwingUtilities.isRightMouseButton(e)) {
					if(graph.isRedZoneNode(selected))
						JOptionPane.showMessageDialog(null,"A red zone area can't be marked as the destination","Warning",JOptionPane.WARNING_MESSAGE);
					else if(!graph.isHospitalNode(selected))
						JOptionPane.showMessageDialog(null,"Only a hospital can be marked as a destination","Warning",JOptionPane.WARNING_MESSAGE);
					else {
						if(!graph.isSource(selected))
							graph.setDestination(selected);
						else
							JOptionPane.showMessageDialog(null, "Source can't be set as Destination","Warning",JOptionPane.WARNING_MESSAGE);
						MainWindow.setTopPanel();
					}
				} 				else
					return;

				graph.setSolved(false);
				repaint();
				return;
			}
			else if(e.isAltDown()) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					if(graph.isRedZoneNode(selected))
						JOptionPane.showMessageDialog(null,"A red zone can't be marked as a hospital","Warning",JOptionPane.WARNING_MESSAGE);
					if(!graph.isHospitalNode(selected))
						graph.setHospitalNode(selected);
					else
						graph.removeHospitalNode(selected);
					MainWindow.setTopPanel();
				}
				else if(SwingUtilities.isRightMouseButton(e)) {
					if(!graph.isRedZoneNode(selected)) {
						if(!graph.isDestination(selected) && !graph.isSource(selected) && !graph.isHospitalNode(selected))
							graph.setRedZone(selected);
						else
							JOptionPane.showMessageDialog(null, "Source, Destination or Hospitals can't be set as Red Zone Areas","Warning",JOptionPane.WARNING_MESSAGE);
					}
					else
						graph.removeRedZone(selected);
					MainWindow.setTopPanel();
				}

				graph.setSolved(false);
				repaint();
				return;
			}
		}

		if(hoveredEdge!=null){
			if(e.isControlDown() && e.isShiftDown()){
				graph.getEdges().remove(hoveredEdge);
				hoveredEdge = null;
				graph.setSolved(false);
				repaint();
				return;
			}

			String input = JOptionPane.showInputDialog("Enter weight for " + hoveredEdge.toString()
			+ " : ");
			try {
				int weight = Integer.parseInt(input);
				if (weight > 0) {
					hoveredEdge.setWeight(weight);
					graph.setSolved(false);
					repaint();
				} else {
					JOptionPane.showMessageDialog(null, "Weight should be positive");
				}
			} catch (NumberFormatException nfe) {}
			return;
		}

		for(Node node : graph.getNodes()) {
			if(DrawUtils.isOverlapping(e, node.getCoord())){
				JOptionPane.showMessageDialog(null, "Overlapping Node can't be created");
				return;
			}
		}

		graph.addNode(e.getPoint());
		graph.setSolved(false);
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		for (Node node : graph.getNodes()) {
			if(selectedNode !=null && node!= selectedNode && DrawUtils.isWithinBounds(e, node.getCoord())){
				Edge new_edge = new Edge(selectedNode, node);
				graph.addEdge(new_edge);
				graph.setSolved(false);
			}
		}
		selectedNode = null;
		hoveredNode = null;
		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		hoveredNode = null;

		for (Node node : graph.getNodes()) {
			if(selectedNode ==null && DrawUtils.isWithinBounds(e, node.getCoord())){
				selectedNode = node;
			} else if(DrawUtils.isWithinBounds(e, node.getCoord())) {
				hoveredNode = node;
			}
		}

		if(selectedNode !=null){
			if(e.isControlDown()){
				selectedNode.setCoord(e.getX(), e.getY());
				cursor = null;
				repaint();
				return;
			}

			cursor = new Point(e.getX(), e.getY());
			repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {

		if(e.isControlDown()){
			hoveredNode = null;
			for (Node node : graph.getNodes()) {
				if(DrawUtils.isWithinBounds(e, node.getCoord())) {
					hoveredNode = node;
				}
			}
		}

		hoveredEdge = null;

		for (Edge edge : graph.getEdges()) {
			if(DrawUtils.isOnEdge(e, edge)) {
				hoveredEdge = edge;
			}
		}

		repaint();
	}

	public void reset(){
		graph.clear();
		selectedNode = null;
		hoveredNode = null;
		hoveredEdge = null;                                                                                                                                       
		MainWindow.setTopPanel();
		repaint();
	}
}
