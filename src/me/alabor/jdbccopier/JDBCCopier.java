package me.alabor.jdbccopier;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import me.alabor.jdbccopier.copier.Copier;
import me.alabor.jdbccopier.copier.CopierTask;
import me.alabor.jdbccopier.copier.PooledCopier;
import me.alabor.jdbccopier.copier.listener.ConsoleCopierListener;
import me.alabor.jdbccopier.database.Database;
import me.alabor.jdbccopier.database.MSSQLDatabase;
import me.alabor.jdbccopier.database.meta.Table;
import me.alabor.jdbccopier.ui.ListLayout;
import me.alabor.jdbccopier.ui.WorkerStatusPanel;


public class JDBCCopier {

	private final static int MAX_WORKERS = 5;
	private final static String SOURCE = "jdbc:sqlserver://CHSA1639.eur.beluni.net\\TZUPBRRI01;database=RepRisk;integratedSecurity=true;";
	private final static String TARGET = "jdbc:sqlserver://localhost;database=RepRiskLocal;integratedSecurity=true;";
	//private final static String SOURCE = "jdbc:sqlserver://localhost;database=RepRiskLocal;integratedSecurity=true;";
	//private final static String TARGET = "jdbc:sqlserver://localhost;database=Test;integratedSecurity=true;";
		
	public static void main(String[] args) throws InterruptedException {
		
		try {
			Database it = new MSSQLDatabase(SOURCE);
			it.connect();
			
			List<String> nameFilter = getNameFilter();
			Queue<Table> pool = new ConcurrentLinkedQueue<Table>(it.getTables(nameFilter));
			final List<Thread> workers = new ArrayList<Thread>(MAX_WORKERS+1);
			List<WorkerStatusPanel> statusPanels = new ArrayList<WorkerStatusPanel>(MAX_WORKERS+1);
			ConsoleCopierListener console = new ConsoleCopierListener(false);
			
			/* Create Workers & Statuspanels: */
			for(int i = 0; i < MAX_WORKERS; i++) {
				Database source = new MSSQLDatabase(SOURCE);
				Database target = new MSSQLDatabase(TARGET);
				Copier pooledCopier = new PooledCopier(source, target, pool);
				WorkerStatusPanel statusPanel = new WorkerStatusPanel();
				
				pooledCopier.addCopierListener(statusPanel);
				pooledCopier.addCopierListener(console);
				workers.add(new Thread(new CopierTask(pooledCopier)));
				statusPanels.add(statusPanel);
			}
			
			/* Create and show frame: */
			JFrame frame = new JFrame("JDBCCopier");
			frame.setMinimumSize(new Dimension(600,500));
			frame.setSize(frame.getMinimumSize());
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			final JButton btnStart = new JButton("Start");
			btnStart.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					btnStart.setEnabled(false);
					for(Thread t: workers) {
						t.start();
					}
				}
			});
			
			JPanel statusContainer = new JPanel(new ListLayout());
			for (WorkerStatusPanel workerStatusPanel : statusPanels) {
				statusContainer.add(workerStatusPanel);
			}

			JPanel contentPane = new JPanel(new BorderLayout());
			contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			contentPane.add(btnStart, BorderLayout.NORTH);
			contentPane.add(new JScrollPane(statusContainer), BorderLayout.CENTER);
			
			frame.setContentPane(contentPane);
			frame.setVisible(true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static List<String> getNameFilter() {
		List<String> filters = new ArrayList<String>();
		
		filters.add("AttachmentData");
		filters.add("AuditSubmission");
		filters.add("AuditUserDetails");
		filters.add("ConditionComment");
		filters.add("ConditionGroup");
		filters.add("Division");
		filters.add("MessageQueue");
		filters.add("OracleAttachment");
		filters.add("OracleUser");
		filters.add("OracleUserDetails");
		filters.add("Region");
		filters.add("SubmissionIDAllocation");
		filters.add("SubmissionStatusActionRoleBackup3");
		filters.add("SubmissionUserRole");
		filters.add("UserRole");
		
		return filters;
	}
	

}
