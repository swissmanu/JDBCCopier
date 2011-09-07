package me.alabor.jdbccopier.copier;

import java.util.ArrayList;
import java.util.List;

import me.alabor.jdbccopier.database.meta.Table;

public class AbstractCopier {

	private List<CopierListener> listeners = new ArrayList<CopierListener>();

	public AbstractCopier() {
		super();
	}

	public void addCopierListener(CopierListener copierListener) {
		if(!listeners.contains(copierListener)) {
			listeners.add(copierListener);
		}
	}

	public void removeCopierListener(CopierListener copierListener) {
		listeners.remove(copierListener);
	}
	
	protected void fireStartCopyTable(Table table) {
		for (CopierListener listener : listeners) {
			listener.startCopyTable(table);
		}
	}

	protected void fireCopyTableStatus(Table table, long currentPos, long totalRows) {
		for (CopierListener listener : listeners) {
			listener.copyTableStatus(table, currentPos, totalRows);
		}
	}

	protected void fireError(Table table, Exception error) {
		for (CopierListener listener : listeners) {
			listener.error(table, error);
		}
	}
	
	protected void fireEndCopyTable(Table table) {
		for (CopierListener listener : listeners) {
			listener.endCopyTable(table);
		}
	}
	
	protected void fireEndCopy(int totalSuccess, int totalError) {
		for (CopierListener listener : listeners) {
			listener.endCopy(totalSuccess, totalError);
		}
	}

}