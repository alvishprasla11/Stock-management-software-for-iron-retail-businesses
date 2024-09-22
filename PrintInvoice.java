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
public class PrintInvoice extends PrintInvoiceFrame {
    private static final String DATABASE_URL = IronInventoryManagementApp.DATABASE_URL;
    public static int fullquantity;
    public static void PrintInvoice() {
        JFrame printInvoiceFrame = new JFrame("Print Invoice");
        printInvoiceFrame.setSize(500, 500);
        printInvoiceFrame.setLocationRelativeTo(null);
        JPanel panel = new JPanel(new GridLayout(7, 2));

        String[] materials = {"MS", "Polished Mild Steel", "OHNS", "WPS", "EN-31"};
        JComboBox<String> materialComboBox = AddStock.createMaterialComboBox();
        JComboBox<String> shapeComboBox = AddStock.createShapeComboBox();
        JComboBox<Integer> quantityComboBox = AddStock.createComboBox(100);
        JComboBox<Integer> lengthComboBox = AddStock.createComboBox(100);
        JComboBox<Integer> widthComboBox = AddStock.createComboBox(100);
        JComboBox<Integer> thicknessComboBox = AddStock.createComboBox(100);
        JComboBox<Integer> diameterComboBox = AddStock.createComboBox(100);

        panel.add(new JLabel("Material:"));
        panel.add(materialComboBox);
        panel.add(new JLabel("Shape:"));
        panel.add(shapeComboBox);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityComboBox);
        JButton addButton = new JButton("Add Product");
        JButton printInvoiceButton = new JButton("Print Invoice");
        panel.add(new JLabel("Length(feet):"));
        panel.add(lengthComboBox);
        JLabel thick = new JLabel("Thickness(mm):");
        JLabel wid = new JLabel("Width(mm):");
        JLabel dia = new JLabel("Diameter(mm):");
        panel.add(thick);
        panel.add(thicknessComboBox);
        panel.add(addButton);
        panel.add(printInvoiceButton);

        shapeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedShape = (String) shapeComboBox.getSelectedItem();
                if ("Square".equals(selectedShape)) {
                    panel.remove(thick);
                    panel.remove(widthComboBox);
                    panel.remove(diameterComboBox);
                    panel.remove(thicknessComboBox);
                    panel.remove(wid);
                    panel.remove(dia);
                    panel.add(thick);
                    panel.add(thicknessComboBox);
                    panel.add(addButton);
                    panel.add(printInvoiceButton);

                } else if ("Rectangle".equals(selectedShape)) {
                    panel.remove(thick);
                    panel.remove(wid);
                    panel.remove(widthComboBox);
                    panel.remove(diameterComboBox);
                    panel.remove(thicknessComboBox);
                    panel.remove(dia);
                    panel.add(wid);
                    panel.add(widthComboBox);
                    panel.add(thick);
                    panel.add(thicknessComboBox);
                    panel.add(addButton);
                    panel.add(printInvoiceButton);
                } else {
                    panel.remove(thick);
                    panel.remove(wid);
                    panel.remove(widthComboBox);
                    panel.remove(diameterComboBox);
                    panel.remove(thicknessComboBox);
                    panel.remove(dia);
                    panel.add(dia);
                    panel.add(diameterComboBox);
                    panel.add(addButton);
                    panel.add(printInvoiceButton);
                } 
                panel.revalidate();
                panel.repaint();
            }
        });

        String material;
        String shape;
        int quantity;
        int length;
        int width;
        int thickness;
        int diameter;

        addButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String material = (String) materialComboBox.getSelectedItem();
            String shape = (String) shapeComboBox.getSelectedItem();
            int quantity = (Integer) quantityComboBox.getSelectedItem();
            int length = (Integer) lengthComboBox.getSelectedItem();
            int width = (Integer) widthComboBox.getSelectedItem();
            int thickness = (Integer) thicknessComboBox.getSelectedItem();
            int diameter = (Integer) diameterComboBox.getSelectedItem();
            int confirm = 0;
    
                if (isStockAvailable(material, shape, quantity, width, thickness, diameter, length)) {
            for (int i = 0; i < quantity; i++) {
                double costPerUnit = calculateCostForOneRod(material, shape, width, thickness, diameter, length, i);
        
                // Note: Pass the correct values to addProduct based on your requirements
               
                removeFromInventory(material, shape, 1, width, thickness, diameter, length);
            }
            JOptionPane.showMessageDialog(null, "Product added successfully!", "Confirmation", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Stock not available for the selected quantity.", "Error", JOptionPane.ERROR_MESSAGE);
        }

            if(confirm>0){
            JOptionPane.showMessageDialog(null, "Product added successfully!", "Confirmation", JOptionPane.INFORMATION_MESSAGE);}
        }
    });

        printInvoiceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PrintConfirmation.showPrintConfirmation();
            }
        });

        printInvoiceFrame.add(panel);
        printInvoiceFrame.setVisible(true);
    }

    public static void removeFromInventory(String material, String shape, int quantity, int width, int thickness, int diameter, int length) {
        String sanitizedMaterial = material.toLowerCase().replace(" ", "_");
        String tableName = sanitizedMaterial+ "_" + shape.toLowerCase();
        String selectQuery;
        if (shape.equalsIgnoreCase("square")) {
            selectQuery = "SELECT * FROM " + tableName + " WHERE thickness = " + thickness + " AND length = " + length + " LIMIT " + quantity;
        } else if (shape.equalsIgnoreCase("rectangle")) {
            selectQuery = "SELECT * FROM " + tableName + " WHERE width = " + width + " AND thickness = " + thickness + " AND length = " + length + " LIMIT " + quantity;
        } else if (shape.equalsIgnoreCase("round")) {
            selectQuery = "SELECT * FROM " + tableName + " WHERE diameter = " + diameter + " AND length = " + length + " LIMIT " + quantity;
        } else {
            return;
        }

        System.out.println("Generated SQL Query: " + selectQuery); // Print the generated SQL query

        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            conn.setAutoCommit(false); // Start transaction
            try (PreparedStatement pstmt = conn.prepareStatement(selectQuery);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    // Fetch the rows
                    int fetchedQuantity = rs.getInt("quantity");
                    int fetchedLength = rs.getInt("length");
                    int fetchedWidth = rs.getInt("width");
                    int fetchedThickness = rs.getInt("thickness");
                    int fetchedDiameter = rs.getInt("diameter");
                    double fetchedCostForOneRod = rs.getDouble("cost_price_per_inch");
                    String stockAddedDate = rs.getString("stock_added_date");
                    // Transfer the fetched rows to the sold table
                    Product.addProduct(material, shape, width, fetchedWidth, fetchedThickness, fetchedDiameter, fetchedCostForOneRod, stockAddedDate, quantity);
                    transferToSoldTable(conn, material, shape, fetchedQuantity, fetchedLength, fetchedWidth, fetchedThickness, fetchedDiameter, fetchedCostForOneRod, stockAddedDate);
                }
                conn.commit(); // Commit the transaction
            } catch (SQLException ex) {
                conn.rollback(); // Rollback if there's an exception
                throw ex; // Rethrow the exception after rollback
            } finally {
                conn.setAutoCommit(true); // Restore auto-commit mode
            }
            deleteRowsFromInventory(conn, material, shape, quantity, width, thickness, diameter, length);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error removing product from inventory or transferring to sold table. \nSQL Exception: " + ex.getMessage());
        }
    }

    public static void transferToSoldTable(Connection conn, String material, String shape, int quantity, int length, int width, int thickness, int diameter, double costForOneRod, String stockAddedDate) {
        String soldTableName = "stockSoldTable";
        String soldQuery = "INSERT INTO " + soldTableName + " (material, shape, quantity, length, width, thickness, diameter, cost_price_per_inch, selling_price_per_inch, stock_added_date, sold_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(soldQuery)) {
            pstmt.setString(1, material);
            pstmt.setString(2, shape);
            pstmt.setInt(3, quantity);
            pstmt.setInt(4, length);
            pstmt.setInt(5, width);
            pstmt.setInt(6, thickness);
            pstmt.setInt(7, diameter);
            pstmt.setDouble(8, costForOneRod);
            pstmt.setDouble(9, costForOneRod * 1.30);
            pstmt.setString(10, stockAddedDate);
            pstmt.setString(11, java.time.LocalDate.now().toString());

            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error transferring stock to sold table: " + ex.getMessage());
        }
    }

    public static double calculateCostForOneRod(String material, String shape, int width, int thickness, int diameter, int length, int rowNumber) {
        String tableName = material.toLowerCase() + "_" + shape.toLowerCase();
        String query;
        double costForOneRod = 0.0;

        if (shape.equalsIgnoreCase("square")) {
            query = "SELECT cost_price_per_inch FROM " + tableName + " WHERE thickness = ? AND length = ? ORDER BY rowid LIMIT 1 OFFSET ?";
        } else if (shape.equalsIgnoreCase("rectangle")) {
            query = "SELECT cost_price_per_inch FROM " + tableName + " WHERE width = ? AND thickness = ? AND length = ? ORDER BY rowid LIMIT 1 OFFSET ?";
        } else if (shape.equalsIgnoreCase("round")) {
            query = "SELECT cost_price_per_inch FROM " + tableName + " WHERE diameter = ? AND length = ? ORDER BY rowid LIMIT 1 OFFSET ?";
        } else {
            return costForOneRod;
        }

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            switch (shape.toLowerCase()) {
                case "square":
                    pstmt.setInt(1, thickness);
                    pstmt.setInt(2, length);
                    pstmt.setInt(3, rowNumber - 1); // Adjusting for 0-based indexing
                    break;
                case "rectangle":
                    pstmt.setInt(1, width);
                    pstmt.setInt(2, thickness);
                    pstmt.setInt(3, length);
                    pstmt.setInt(4, rowNumber - 1); // Adjusting for 0-based indexing
                    break;
                case "round":
                    pstmt.setInt(1, diameter);
                    pstmt.setInt(2, length);
                    pstmt.setInt(3, rowNumber - 1); // Adjusting for 0-based indexing
                    break;
            }

            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                costForOneRod = resultSet.getDouble("cost_price_per_inch");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return costForOneRod;
    }

    public static double getSellingPrice(String material, String shape, int width, int thickness, int diameter, int length) {
        String soldTableName = "stockSoldTable";
        String selectQuery = "SELECT selling_price_per_inch FROM " + soldTableName +
                " WHERE material = ? AND shape = ? AND width = ? AND thickness = ? AND diameter = ? AND length = ?" +
                " ORDER BY SUBSTR(sold_date, 1, 10) DESC LIMIT 1";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(selectQuery)) {
            pstmt.setString(1, material);
            pstmt.setString(2, shape);
            pstmt.setInt(3, width);
            pstmt.setInt(4, thickness);
            pstmt.setInt(5, diameter);
            pstmt.setInt(6, length);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("selling_price_per_inch");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the exception as needed
        }

        return 0.0; // Default value or indicator of failure
    }
}
