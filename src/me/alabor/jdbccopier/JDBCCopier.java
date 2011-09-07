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

import me.alabor.jdbccopier.copier.Copier;
import me.alabor.jdbccopier.copier.CopierTask;
import me.alabor.jdbccopier.database.Database;
import me.alabor.jdbccopier.database.MSSQLDatabase;
import me.alabor.jdbccopier.database.meta.Field;
import me.alabor.jdbccopier.database.meta.Table;
import me.alabor.jdbccopier.ui.StatusPanel;


public class JDBCCopier {

	public static void main(String[] args) throws InterruptedException {
		Database it = new MSSQLDatabase("jdbc:sqlserver://CHSA1639.eur.beluni.net\\TZUPBRRI01;database=RepRisk;integratedSecurity=true;");
		Database local = new MSSQLDatabase("jdbc:sqlserver://localhost;database=RepRiskLocal;integratedSecurity=true;");
		
		final Copier copier = new Copier(it, local);
		
		try {
			it.connect();
			
			Table table = new Table("dbo","OracleAttachment");
			table.addField(new Field("AttachmentID",it.mapFieldType("uniqueidentifier")));
			table.addField(new Field("AttachmentPath",it.mapFieldType("varchar")));
			table.addField(new Field("AttachmentName",it.mapFieldType("varchar")));
			table.addField(new Field("MIMEType",it.mapFieldType("varchar")));
			table.addField(new Field("ImageData",it.mapFieldType("varbinary")));
			table.addField(new Field("DateUploaded",it.mapFieldType("datetime")));
			List<Table> tables = new ArrayList<Table>();
			tables.add(table);
			copier.setTablesToCopy(tables);
			CopierTask task = new CopierTask(copier);
			final Thread copierThread = new Thread(task);
			
			JFrame frame = new JFrame("JDBCCopier");
			frame.setMinimumSize(new Dimension(600,500));
			frame.setSize(frame.getMinimumSize());
			
			JTabbedPane tbpTabs = new JTabbedPane();
			JButton btnStart = new JButton("Start");
			btnStart.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					copierThread.start();
				}
			});
			StatusPanel status = new StatusPanel();
			copier.addCopierListener(status);
			
			JPanel contentPane = new JPanel(new BorderLayout());
			contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			tbpTabs.addTab("Copier", status);
			
			contentPane.add(tbpTabs, BorderLayout.CENTER);
			contentPane.add(btnStart, BorderLayout.SOUTH);
			
			frame.setContentPane(contentPane);
			frame.setVisible(true);
			
			//copier.setTablesToCopy(it.getTables());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
