package me.alabor.jdbccopier.ui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import me.alabor.jdbccopier.copier.CopierListener;
import me.alabor.jdbccopier.database.meta.Table;

public class StatusPanel extends JPanel implements CopierListener{
	
	private final JProgressBar barProgress = new JProgressBar();
	private final JTextArea txtLog = new JTextArea();

	public StatusPanel() {
		buildGui();
	}
	
	private void buildGui() {
		setLayout(new BorderLayout());
		
		txtLog.setEditable(false);
		
		add(barProgress, BorderLayout.NORTH);
		add(new JScrollPane(txtLog));
	}
	
	private void addLog(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				txtLog.setText(txtLog.getText() + text + "\n");
				txtLog.setCaretPosition(txtLog.getText().length());
			}
		});
	}
	
	// CopierListener ----------------------------------------------------------
	@Override
	public void startCopyTable(Table table, final long totalRows) {
		addLog("[" + table + "] Begin to copy " + totalRows + " rows");
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				barProgress.setMaximum((int)totalRows);
			}
		});
	}
	
	@Override
	public void copyTableStatus(Table table, final long currentPos, long totalRows) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				barProgress.setValue((int)currentPos);
			}
		});
	}
	
	@Override
	public void endCopyTable(Table table) {
		addLog("[" + table + "] Finished");
	}
	
	@Override
	public void error(Table table, Exception exception) {
		addLog("[" + table + "] Error: " + exception.getMessage());
	}
	
	@Override
	public void startCopy(List<Table> tables) {
		StringBuffer text = new StringBuffer("Copy following tables:");
		for (Table table : tables) {
			text.append("\n- ");
			text.append(table);
		}
		text.append("\n");
		addLog(text.toString());
	}
	
	@Override
	public void endCopy(int totalSuccess, int totalError) {
		addLog("Finished!");
	}
	
	
}