package me.alabor.jdbccopier.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;

public class GUIHelper {

    public static void centerOnOwner(Component toCenter, Component owner) {
		Dimension ownerSize = owner.getSize();
		Point ownerLocation = owner.getLocation();
		Dimension toCenterSize = toCenter.getSize();
		
		int posX = new Double(((ownerSize.getWidth() - toCenterSize.getWidth()) / 2) + ownerLocation.getX()).intValue();
		int posY = new Double(((ownerSize.getHeight() - toCenterSize.getHeight()) / 2) + ownerLocation.getY()).intValue();
		
		toCenter.setLocation(posX, posY);
    }
    
    public static void centerOnScreen(Component toCenter) {
        Dimension paneSize = toCenter.getSize();
        Dimension screenSize = toCenter.getToolkit().getScreenSize();
        
        int posX = (screenSize.width - paneSize.width) / 2;
        int posY = (screenSize.height - paneSize.height) / 2; 
        
        toCenter.setLocation(posX, posY);
    }
    
    public static BufferedImage toCompatibleImage(BufferedImage image) {
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice d = e.getDefaultScreenDevice();
        GraphicsConfiguration c = d.getDefaultConfiguration();
        
        BufferedImage compatibleImage = c.createCompatibleImage(
                image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics g = compatibleImage.getGraphics();
        
        g.drawImage(image, 0, 0, null);
        g.dispose();
        
        return compatibleImage;
    }
    
    public static void useJGoodiesLooks() {
        try {
            UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
            UIManager.put("jgoodies.popupDropShadowEnabled", Boolean.TRUE);
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }
    
}
