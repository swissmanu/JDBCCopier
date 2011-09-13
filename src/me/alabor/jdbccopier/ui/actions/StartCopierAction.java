package me.alabor.jdbccopier.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import me.alabor.jdbccopier.copier.Copier;

public class StartCopierAction extends AbstractAction {

	private Copier copier;
	
	public StartCopierAction(Copier copier) {
		super("Start");
		this.copier = copier;
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		//copier.copy();
	}

}
