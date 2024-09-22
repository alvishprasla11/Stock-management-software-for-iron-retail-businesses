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

public class RemoveStock extends PrintInvoiceFrame {
    private static final String DATABASE_URL = IronInventoryManagementApp.DATABASE_URL;

    public static void createRemoveStockFrame() {
        JFrame createRemoveStockFrame = new JFrame("Remove Stock");
        createRemoveStockFrame.setSize(500, 500);
        createRemoveStockFrame.setLocationRelativeTo(null);

        JPanel panel = createRemoveStockPanel();

        createRemoveStockFrame.add(panel);
        createRemoveStockFrame.setVisible(true);
    }

    private static JPanel createRemoveStockPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 2));

        JComboBox<String> materialComboBox = AddStock.createMaterialComboBox();
        JComboBox<String> shapeComboBox = AddStock.createShapeComboBox();
        JComboBox<Integer> widthComboBox = AddStock.createComboBox(100);
        JComboBox<Integer> thicknessComboBox = AddStock.createComboBox(100);
        JComboBox<Integer> diameterComboBox = AddStock.createComboBox(100);

        JComboBox<Integer> quantityComboBox = AddStock.createComboBox(100);
        JComboBox<Integer> lengthComboBox = AddStock.createComboBox(100);
        
        JLabel thick = new JLabel("Thickness(mm):");
        JLabel wid = new JLabel("Width(mm):");
        JLabel dia = new JLabel("Diameter(mm):");

        panel.add(new JLabel("Material:"));
        panel.add(materialComboBox);
        panel.add(new JLabel("Shape:"));
        panel.add(shapeComboBox);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityComboBox);
        JButton addButton = new JButton("Remove Product");
        panel.add(new JLabel("Length(feet):"));
        panel.add(lengthComboBox);
        panel.add(addButton);

        AddStock.configureShapeComboBox(shapeComboBox, panel, thick, wid, dia, widthComboBox, thicknessComboBox, diameterComboBox, addButton);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRemoveButtonClick(materialComboBox, shapeComboBox, quantityComboBox, lengthComboBox, widthComboBox, thicknessComboBox, diameterComboBox);
            }
        });

        return panel;
    }


    private static void handleRemoveButtonClick(JComboBox<String> materialComboBox, JComboBox<String> shapeComboBox,
                                                JComboBox<Integer> quantityComboBox, JComboBox<Integer> lengthComboBox,
                                                JComboBox<Integer> widthComboBox, JComboBox<Integer> thicknessComboBox,
                                                JComboBox<Integer> diameterComboBox) {
        String material = materialComboBox.getSelectedItem().toString();
        String shape = shapeComboBox.getSelectedItem().toString();
        int quantity = quantityComboBox.getItemAt(quantityComboBox.getSelectedIndex());
        int length = lengthComboBox.getItemAt(lengthComboBox.getSelectedIndex());
        int width = widthComboBox.getItemAt(widthComboBox.getSelectedIndex());
        int thickness = thicknessComboBox.getItemAt(thicknessComboBox.getSelectedIndex());
        int diameter = diameterComboBox.getItemAt(diameterComboBox.getSelectedIndex());

        if (isStockAvailable(material, shape, quantity, width, thickness, diameter, length)) {
            try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
                conn.setAutoCommit(false); // Start transaction
                deleteRowsFromInventory(conn, material, shape, quantity, width, thickness, diameter, length);
                JOptionPane.showMessageDialog(null, "Stock removed successfully");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error removing product from inventory or transferring to sold table. \nSQL Exception: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Stock not available for the selected quantity.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
}

