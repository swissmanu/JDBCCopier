package me.alabor.jdbccopier.ui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import me.alabor.jdbccopier.copier.listener.CopierListener;
import me.alabor.jdbccopier.database.meta.Table;

public class WorkerStatusPanel extends JPanel implements CopierListener{
	
	private final JProgressBar barProgress = new JProgressBar();
	private final JLabel lblStatus = new JLabel("Worker ready");

	public WorkerStatusPanel() {
		buildGui();
	}
	
	private void buildGui() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, getForeground()),
				BorderFactory.createEmptyBorder(4,4,2,4)));
		
		add(barProgress, BorderLayout.NORTH);
		add(lblStatus, BorderLayout.SOUTH);
	}
	
	private void setStatusText(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				lblStatus.setText(text);
			}
		});
	}
	
	private void setProgess(final int progress, final int progressMax) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				barProgress.setValue(progress);
				barProgress.setMaximum(progressMax);
			}
		});		
	}
	
	// CopierListener ----------------------------------------------------------
	@Override
	public void startCopyTable(Table table, final long totalRows) {
		setStatusText("Copy table " + table + " (" + totalRows + " rows)");
		setProgess(0, (int)totalRows);
	}
	
	@Override
	public void copyTableStatus(Table table, final long currentPos, long totalRows) {
		setProgess((int)currentPos, (int)totalRows);
	}
	
	@Override
	public void endCopyTable(Table table) { }
	
	@Override
	public void error(Table table, Exception exception) {
		setStatusText("Error: " + exception.getMessage() + " (Table " + table + ")");
	}
	
	@Override
	public void startCopy() { }
	
	@Override
	public void endCopy(int totalSuccess, int totalError) {
		setStatusText("Finished");
	}
	
	
}