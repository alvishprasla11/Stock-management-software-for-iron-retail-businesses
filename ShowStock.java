import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.OrientationRequested;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.MessageFormat;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ShowStock {
    private static String DATABASE_URL = IronInventoryManagementApp.DATABASE_URL;
    public static void createShowStockFrame() {
        JFrame showStockFrame = new JFrame("Show Stock");
        showStockFrame.setSize(500, 500);
        showStockFrame.setLocationRelativeTo(null);

        JPanel panel = createMainPanel(showStockFrame);

        showStockFrame.add(panel);
        showStockFrame.setVisible(true);
    }

    private static JPanel createMainPanel(JFrame showStockFrame) {
        JPanel panel = new JPanel(new GridLayout(5, 2));

        JComboBox<String> materialComboBox = AddStock.createMaterialComboBox();
        JComboBox<String> shapeComboBox = AddStock.createShapeComboBox();
        JComboBox<Integer> widthComboBox = AddStock.createComboBox(100);
        JComboBox<Integer> thicknessComboBox = AddStock.createComboBox(100);
        JComboBox<Integer> diameterComboBox = AddStock.createComboBox(100);

        JButton checkAvailabilityButton = new JButton("Check Availability");

        addComponents(showStockFrame, panel, materialComboBox, shapeComboBox, widthComboBox, thicknessComboBox, diameterComboBox, checkAvailabilityButton);

        return panel;
    }

    private static void addComponents(JFrame showStockFrame, JPanel panel, JComboBox<String> materialComboBox, JComboBox<String> shapeComboBox,
                                      JComboBox<Integer> widthComboBox, JComboBox<Integer> thicknessComboBox,
                                      JComboBox<Integer> diameterComboBox, JButton checkAvailabilityButton) {
        JLabel thick = new JLabel("Thickness(mm):");
        JLabel wid = new JLabel("Width(mm):");
        JLabel dia = new JLabel("Diameter(mm):");

        panel.add(new JLabel("Material:"));
        panel.add(materialComboBox);
        panel.add(new JLabel("Shape:"));
        panel.add(shapeComboBox);
        panel.add(thick);
        panel.add(thicknessComboBox);
        panel.add(checkAvailabilityButton);
        
        AddStock.configureShapeComboBox(shapeComboBox, panel, thick, wid, dia, widthComboBox, thicknessComboBox, diameterComboBox, checkAvailabilityButton);
        
        checkAvailabilityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAvailability(showStockFrame, materialComboBox, shapeComboBox, widthComboBox, thicknessComboBox, diameterComboBox);
            }
        });
    }

          private static void checkAvailability(JFrame showStockFrame, JComboBox<String> materialComboBox,
                                          JComboBox<String> shapeComboBox, JComboBox<Integer> widthComboBox,
                                          JComboBox<Integer> thicknessComboBox, JComboBox<Integer> diameterComboBox) {
        String material = (String) materialComboBox.getSelectedItem();
        String shape = (String) shapeComboBox.getSelectedItem();
        int width = (Integer) widthComboBox.getSelectedItem();
        int thickness = (Integer) thicknessComboBox.getSelectedItem();
        int diameter = (Integer) diameterComboBox.getSelectedItem();
    
        // Ensure that DATABASE_URL is accessible from this method
    
        String sanitizedMaterial = sanitizeName(material);
        String sanitizedShape = sanitizeName(shape);
        String tableName = sanitizedMaterial + "_" + sanitizedShape;
    
        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            String query = "";
            if (sanitizedShape.equalsIgnoreCase("square")) {
                query = "SELECT length, SUM(quantity) as total_quantity, selling_price_per_inch FROM " + tableName +
                        " WHERE thickness = ? AND quantity > 0 GROUP BY length, selling_price_per_inch";
            } else if (sanitizedShape.equalsIgnoreCase("rectangle")) {
                query = "SELECT length, SUM(quantity) as total_quantity, selling_price_per_inch FROM " + tableName +
                        " WHERE width = ? AND thickness = ? AND quantity > 0 GROUP BY length, selling_price_per_inch";
            } else if (sanitizedShape.equalsIgnoreCase("round")) {
                query = "SELECT length, SUM(quantity) as total_quantity, selling_price_per_inch FROM " + tableName +
                        " WHERE diameter = ? AND quantity > 0 GROUP BY length, selling_price_per_inch";
            }
    
            PreparedStatement pstmt = conn.prepareStatement(query);
    
            if (sanitizedShape.equalsIgnoreCase("square")) {
                pstmt.setInt(1, thickness);
            } else if (sanitizedShape.equalsIgnoreCase("rectangle")) {
                pstmt.setInt(1, width);
                pstmt.setInt(2, thickness);
            } else if (sanitizedShape.equalsIgnoreCase("round")) {
                pstmt.setInt(1, diameter);
            }
    
            ResultSet resultSet = pstmt.executeQuery();
    
            StringBuilder availableStock = new StringBuilder("Available Stock:\n");
    
            while (resultSet.next()) {
                int length = resultSet.getInt("length");
                int totalQuantity = resultSet.getInt("total_quantity");
                double sellingPrice = resultSet.getDouble("selling_price_per_inch");
                availableStock.append("Length : ").append(length).append(",  Quantity : ").append(totalQuantity)
                                .append(",  SP of one : ").append(sellingPrice).append("\n");
            }
    
            if (availableStock.length() > 18) { // Check if available stock info exists
                JOptionPane.showMessageDialog(showStockFrame, availableStock.toString());
            } else {
                JOptionPane.showMessageDialog(showStockFrame, "No stock available");
            }
    
            pstmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(showStockFrame, "Error checking availability: " + ex.getMessage());
        }
    }


    // Sanitize the material and shape names
    private static String sanitizeName(String name) {
        return name.toLowerCase().replace(" ", "_");
    }


}
