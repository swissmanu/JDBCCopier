package me.alabor.jdbccopier.copier.listener;

import me.alabor.jdbccopier.database.meta.Table;

public class ConsoleCopierListener implements CopierListener {

	private boolean outputPercentageOutput;
	private int percentageDone = 0;
	
	public ConsoleCopierListener() {
		this(true);
	}
	
	public ConsoleCopierListener(boolean outputPercentageOutput) {
		this.outputPercentageOutput = outputPercentageOutput;
	}
	
	
	@Override
	public void startCopyTable(Table table, long totalRows) {
		System.out.println("Copy table " + table + " (" + totalRows + " rows)");
		percentageDone = 0;
	}

	@Override
	public void copyTableStatus(Table table, long currentPos, long totalRows) {
		if(outputPercentageOutput) {
			int percentage = (int)(currentPos * 100 / totalRows);

			if(percentageDone+10 < percentage) {
				System.out.println("Progress: " + percentageDone + "%");
				percentageDone = percentage;
			}			
		}
	}
	
	@Override
	public void error(Table table, Exception exception) {
		System.out.println("Error during copy table " + table + "!");
	}

	@Override
	public void endCopyTable(Table table) {
		if(outputPercentageOutput) System.out.println("Done 100%");
		System.out.println("-------------------------------------------");
	}

	@Override
	public void startCopy() {
		System.out.println("Start copy");
		System.out.println("===========================================");
	}
	
	@Override
	public void endCopy(int totalSuccess, int totalError) {
		System.out.println("===========================================");
		System.out.println("Finished Process (" + totalSuccess + " suceeded, " + totalError + " errors)");
	}
	
}
