package me.alabor.jdbccopier.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.HashMap;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import me.alabor.jdbccopier.copier.Copier;
import me.alabor.jdbccopier.ui.actions.StartCopierAction;

public class JDBCCopierFrame extends JFrame {
	
	private final static String ACTION_STARTCOPIER = "action.startCopier";
	
	private HashMap<String, Action> actions = new HashMap<String, Action>();
	private Copier copier;
	
	public JDBCCopierFrame(Copier copier) {
		super("JDBCCopier");
		this.copier = copier;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(800,600));
		GUIHelper.centerOnScreen(this);
		
		initActions();
		setContentPane(buildGui());
	}
	
	private void initActions() {
		actions.put(ACTION_STARTCOPIER, new StartCopierAction(copier));
	}
	
	private JComponent buildGui() {
		JPanel gui = new JPanel(new BorderLayout());
		
		gui.add(buildButtonBar(), BorderLayout.SOUTH);
		
		return gui;
	}
	
	private JComponent buildButtonBar() {
		JPanel bar = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		
		bar.add(new JButton(actions.get(ACTION_STARTCOPIER)));
		
		return bar;
	}
	
	public static void main(String[] args) {
		GUIHelper.useJGoodiesLooks();
		new JDBCCopierFrame(null).setVisible(true);
	}
	
}
