package me.alabor.jdbccopier;

import java.util.ArrayList;
import java.util.List;

import me.alabor.jdbccopier.copier.ConsoleCopierListener;
import me.alabor.jdbccopier.copier.Copier;
import me.alabor.jdbccopier.database.Database;
import me.alabor.jdbccopier.database.MSSQLDatabase;
import me.alabor.jdbccopier.database.meta.Field;
import me.alabor.jdbccopier.database.meta.Table;


public class JDBCCopier {

	public static void main(String[] args) throws InterruptedException {
		Database it = new MSSQLDatabase("jdbc:sqlserver://CHSA1639.eur.beluni.net\\TZUPBRRI01;database=RepRisk;integratedSecurity=true;");
		Database local = new MSSQLDatabase("jdbc:sqlserver://localhost;database=RepRiskLocal;integratedSecurity=true;");
		
		Copier copier = new Copier(it, local);
		copier.addCopierListener(new ConsoleCopierListener());
		
		try {
			it.connect();
			
			Table table = new Table("dbo","DefaultPreference");
			table.addField(new Field("ID",it.mapFieldType("bigint")));
			table.addField(new Field("Name",it.mapFieldType("varchar")));
			table.addField(new Field("Value",it.mapFieldType("text")));
			table.addField(new Field("GroupName",it.mapFieldType("varchar")));
			table.addField(new Field("PreferenceType",it.mapFieldType("varchar")));
			table.addField(new Field("DefaultValue",it.mapFieldType("bit")));
			List<Table> tables = new ArrayList<Table>();
			tables.add(table);
			copier.setTablesToCopy(tables);
			
			//copier.setTablesToCopy(it.getTables());
			copier.copy();			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
