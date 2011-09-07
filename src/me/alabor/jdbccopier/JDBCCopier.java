package me.alabor.jdbccopier;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import me.alabor.jdbccopier.copier.AbstractCopier;
import me.alabor.jdbccopier.copier.Copier;
import me.alabor.jdbccopier.copier.CopierTask;
import me.alabor.jdbccopier.copier.DefaultCopier;
import me.alabor.jdbccopier.database.Database;
import me.alabor.jdbccopier.database.MSSQLDatabase;
import me.alabor.jdbccopier.database.meta.Table;
import me.alabor.jdbccopier.ui.StatusPanel;


public class JDBCCopier {

	private final static String SOURCE = "jdbc:sqlserver://CHSA1639.eur.beluni.net\\TZUPBRRI01;database=RepRisk;integratedSecurity=true;";
	private final static String TARGET = "jdbc:sqlserver://localhost;database=RepRiskLocal;integratedSecurity=true;";
		
	public static void main(String[] args) throws InterruptedException {
		
		try {
			Database it = new MSSQLDatabase(SOURCE);
			it.connect();
			List<Table> tables = it.getTables();
			
			List<Table> copierPackage = new ArrayList<Table>();
			final List<DefaultCopier> copiers = new ArrayList<DefaultCopier>();
			for(int i = 0, l = tables.size(); i < l; i++) {
				Table table = tables.get(i);
				copierPackage.add(table);
				
				if(copierPackage.size() >= 8) {
					Database source = new MSSQLDatabase(SOURCE);
					Database target = new MSSQLDatabase(TARGET);
					DefaultCopier copier = new DefaultCopier(source, target, copierPackage);
					copiers.add(copier);
					copierPackage = new ArrayList<Table>();
				}
			}
			
			if(copierPackage.size() > 0 && copierPackage.size() <= 8) {
				Database source = new MSSQLDatabase(SOURCE);
				Database target = new MSSQLDatabase(TARGET);
				DefaultCopier copier = new DefaultCopier(source, target, copierPackage);
				copiers.add(copier);
				copierPackage = new ArrayList<Table>();
			}
			
			
			JFrame frame = new JFrame("JDBCCopier");
			frame.setMinimumSize(new Dimension(600,500));
			frame.setSize(frame.getMinimumSize());
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			JTabbedPane tbpTabs = new JTabbedPane();
			final JButton btnStart = new JButton("Start");
			btnStart.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					btnStart.setEnabled(false);
					
					for(Copier copier : copiers) {
						Thread t = new Thread(new CopierTask(copier));
						t.start();
					}
				}
			});
			
			
			for (AbstractCopier copier : copiers) {
				StatusPanel status = new StatusPanel();
				copier.addCopierListener(status);
				tbpTabs.addTab("Copier",status);
			}

			JPanel contentPane = new JPanel(new BorderLayout());
			contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			contentPane.add(tbpTabs, BorderLayout.CENTER);
			contentPane.add(btnStart, BorderLayout.SOUTH);
			
			frame.setContentPane(contentPane);
			frame.setVisible(true);
			
		
//			Table table = new Table("dbo","OracleAttachment");
//			table.addField(new Field("AttachmentID",it.mapFieldType("uniqueidentifier")));
//			table.addField(new Field("AttachmentPath",it.mapFieldType("varchar")));
//			table.addField(new Field("AttachmentName",it.mapFieldType("varchar")));
//			table.addField(new Field("MIMEType",it.mapFieldType("varchar")));
//			table.addField(new Field("ImageData",it.mapFieldType("varbinary")));
//			table.addField(new Field("DateUploaded",it.mapFieldType("datetime")));
//			List<Table> tables = new ArrayList<Table>();
//			tables.add(table);
//			copier.setTablesToCopy(tables);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	

}
