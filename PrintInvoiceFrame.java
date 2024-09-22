import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
public class PrintInvoiceFrame
{   
    private static final String DATABASE_URL = IronInventoryManagementApp.DATABASE_URL;
    public static void createPrintInvoiceFrame() {
        JFrame createPrintInvoiceFrame = new JFrame("Print Invoice or Remove Stock");
        createPrintInvoiceFrame.setSize(500, 300);
        createPrintInvoiceFrame.setLocationRelativeTo(null);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2));

        JButton printInvoiceButton = new JButton("Print Invoice");
        JButton removeStockButton = new JButton("Remove stock");
        
        removeStockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RemoveStock.createRemoveStockFrame();
            }
        });

        printInvoiceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PrintInvoice.PrintInvoice();
            }
        });
        panel.add(removeStockButton);
        panel.add(printInvoiceButton);
        createPrintInvoiceFrame.add(panel);
        createPrintInvoiceFrame.setVisible(true);
    }
    public static void deleteRowsFromInventory(Connection conn,String material, String shape, int quantity, int width, int thickness, int diameter, int length) {
        String sanitizedMaterial = material.toLowerCase().replace(" ", "_");
        String tableName = sanitizedMaterial + "_" + shape.toLowerCase();
        String deleteQuery;
    
        if (shape.equalsIgnoreCase("square")) {
            deleteQuery = "DELETE FROM " + tableName + " WHERE rowid IN (SELECT rowid FROM " + tableName + " WHERE thickness = " + thickness + " AND length = " + length + " LIMIT " + quantity + ")";
        } else if (shape.equalsIgnoreCase("rectangle")) {
            deleteQuery = "DELETE FROM " + tableName + " WHERE rowid IN (SELECT rowid FROM " + tableName + " WHERE width = " + width + " AND thickness = " + thickness + " AND length = " + length + " LIMIT " + quantity + ")";
        } else if (shape.equalsIgnoreCase("round")) {
            deleteQuery = "DELETE FROM " + tableName + " WHERE rowid IN (SELECT rowid FROM " + tableName + " WHERE diameter = " + diameter + " AND length = " + length + " LIMIT " + quantity + ")";
        } else {
            return;
        }
    
        System.out.println("Generated SQL Query (Delete from Inventory): " + deleteQuery); // Print the generated SQL query
    
        try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
    
            pstmt.executeUpdate();
    
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error removing product from inventory. \nSQL Exception: " + ex.getMessage());
        }
    }
    public static boolean isStockAvailable(String material, String shape, int quantity, int width, int thickness, int diameter, int length) {
            String sanitizedMaterial = material.toLowerCase().replace(" ", "_");
            String tableName = sanitizedMaterial + "_" + shape.toLowerCase();
            String query;
        
            if (shape.equalsIgnoreCase("square")) {
                query = "SELECT COUNT(*) FROM " + tableName + " WHERE length=? AND thickness = ? AND quantity = ?";
            } else if (shape.equalsIgnoreCase("rectangle")) {
                query = "SELECT COUNT(*) FROM " + tableName + " WHERE length=? AND width = ? AND thickness = ? AND quantity = ?";
            } else if (shape.equalsIgnoreCase("round")) {
                query = "SELECT COUNT(*) FROM " + tableName + " WHERE length=? AND diameter = ? AND quantity = ?";
            } else {
                return false;
            }
        
            try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    if (shape.equalsIgnoreCase("square")) {
                        pstmt.setInt(1, length);
                        pstmt.setInt(2, thickness);
                        pstmt.setInt(3, 1); // Check if at least 1 item is available
                    } else if (shape.equalsIgnoreCase("rectangle")) {
                        pstmt.setInt(1, length);
                        pstmt.setInt(2, width);
                        pstmt.setInt(3, thickness);
                        pstmt.setInt(4, 1); // Check if at least 1 item is available
                    } else if (shape.equalsIgnoreCase("round")) {
                        pstmt.setInt(1, length);
                        pstmt.setInt(2, diameter);
                        pstmt.setInt(3, 1); // Check if at least 1 item is available
                    }
        
                    try (ResultSet resultSet = pstmt.executeQuery()) {
                        if (resultSet.next()) {
                            int availableCount = resultSet.getInt(1);
                            return availableCount >= quantity;
                        }
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        
            return false;
        }
}
