package me.alabor.jdbccopier;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
	
	public static void main(String[] args) throws InterruptedException {
		/* Config: */
		Properties properties = loadProperties();
		String sourceType = properties.getProperty("source.type", "");
		String sourceConnectionString = properties.getProperty("source.connectionString", "");
		String targetType = properties.getProperty("source.type", "");
		String targetConnectionString = properties.getProperty("target.connectionString", "");
		int maxWorkers = new Integer(properties.getProperty("maxworkers","-1")).intValue();
		
		// Check config:
		if(sourceType.length() == 0 || sourceConnectionString.length() == 0
			|| targetType.length() == 0 || targetConnectionString.length() == 0
			|| maxWorkers == -1) {
			
			JOptionPane.showMessageDialog(
					new JFrame(),
					"Error with config.properties!",
					"config.properties",
					JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		
		
		/* Run: */
		try {
			Database it = new MSSQLDatabase(sourceConnectionString);
			it.connect();
			
			List<String> nameFilter = getNameFilter();
			Queue<Table> pool = new ConcurrentLinkedQueue<Table>(it.getTables(nameFilter));
			final List<Thread> workers = new ArrayList<Thread>(maxWorkers+1);
			List<WorkerStatusPanel> statusPanels = new ArrayList<WorkerStatusPanel>(maxWorkers+1);
			ConsoleCopierListener console = new ConsoleCopierListener(false);
			
			/* Create Workers & Statuspanels: */
			for(int i = 0; i < maxWorkers; i++) {
				Database sourceDatabase = new MSSQLDatabase(sourceConnectionString);
				Database targetDatabase = new MSSQLDatabase(targetConnectionString);
				Copier pooledCopier = new PooledCopier(sourceDatabase, targetDatabase, pool);
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
	
	private static Properties loadProperties() {
		return loadProperties("config.properties");
	}
	
	private static Properties loadProperties(String configFile) {
		Properties props = new Properties();
		
		try {
			props.load(new FileInputStream(configFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return props;
	}
	

}
