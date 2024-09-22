import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddStock {
    private static JFrame addStockFrame;
    private static final String DATABASE_URL = IronInventoryManagementApp.DATABASE_URL;
    public static void createAddStockFrame() {
        addStockFrame = new JFrame("Add Stock");
        addStockFrame.setSize(500, 500);
        addStockFrame.setLocationRelativeTo(null);

        JPanel panel = createMainPanel();

        addStockFrame.add(panel);
        addStockFrame.setVisible(true);
    }

    private static JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridLayout(8, 2));

        JComboBox<String> materialComboBox = createMaterialComboBox();
        JComboBox<String> shapeComboBox = createShapeComboBox();
        JComboBox<Integer> quantityComboBox = createComboBox(100);
        JComboBox<Integer> lengthComboBox = createComboBox(30);
        JComboBox<Integer> widthComboBox = createComboBox(100);
        JComboBox<Integer> thicknessComboBox = createComboBox(100);
        JComboBox<Integer> diameterComboBox = createComboBox(100);
        JTextField costTextField = createCostTextField();

        JLabel thick = new JLabel("Thickness(mm):");
        JLabel wid = new JLabel("Width(mm):");
        JLabel dia = new JLabel("Diameter(mm):");
        JButton addButton = new JButton("Add");
        
        configureShapeComboBox(shapeComboBox, panel, thick, wid, dia, widthComboBox, thicknessComboBox, diameterComboBox, addButton);
        
        addButton.addActionListener(e -> addStock(materialComboBox, shapeComboBox, costTextField, quantityComboBox, lengthComboBox, widthComboBox, thicknessComboBox, diameterComboBox));

        panel.add(new JLabel("Material:"));
        panel.add(materialComboBox);
        panel.add(new JLabel("Shape:"));
        panel.add(shapeComboBox);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityComboBox);
        panel.add(new JLabel("Length(feet):"));
        panel.add(lengthComboBox);
        panel.add(new JLabel("Cost for the stock:"));
        panel.add(costTextField);
        panel.add(thick);
        panel.add(thicknessComboBox);
        panel.add(addButton);
       
        return panel;
    }

    public static JComboBox<String> createMaterialComboBox() {
        String[] materials = {"MS", "Polished Mild Steel", "OHNS", "WPS", "EN-31"};
        return new JComboBox<>(materials);
    }

    public static JComboBox<String> createShapeComboBox() {
        String[] shapes = {"Square", "Rectangle", "Round"};
        return new JComboBox<>(shapes);
    }

    public static JComboBox<Integer> createComboBox(int maxSize) {
        Integer[] dimensions = new Integer[maxSize];
        for (int i = 0; i < maxSize; i++) {
            dimensions[i] = i + 1;
        }
        return new JComboBox<>(dimensions);
    }

        private static JTextField createCostTextField() {
            return new JTextField();
        }
    public static void configureShapeComboBox(JComboBox<String> shapeComboBox, JPanel panel, JLabel thick, JLabel wid, JLabel dia,
                                               JComboBox<Integer> widthComboBox, JComboBox<Integer> thicknessComboBox, JComboBox<Integer> diameterComboBox,
                                               JButton addButton) {
        shapeComboBox.addActionListener(e -> {
            String selectedShape = (String) shapeComboBox.getSelectedItem();
            

            if ("Square".equals(selectedShape)) {
            updatePanelForSquare(panel, shapeComboBox,wid, widthComboBox,thick, thicknessComboBox,dia, diameterComboBox, addButton);
        } else if ("Rectangle".equals(selectedShape)) {
            updatePanelForRectangle(panel, shapeComboBox,wid, widthComboBox,thick, thicknessComboBox,dia, diameterComboBox, addButton);
        } else{
            updatePanelForRound(panel, shapeComboBox,wid, widthComboBox,thick, thicknessComboBox,dia, diameterComboBox, addButton);
        }

            panel.revalidate();
            panel.repaint();
        });
    }
    private static void updatePanelForSquare(JPanel panel, JComboBox<String> shapeComboBox, JLabel wid,
                                                JComboBox<Integer> widthComboBox,JLabel thick, JComboBox<Integer> thicknessComboBox, JLabel dia,
                                                JComboBox<Integer> diameterComboBox, JButton checkAvailabilityButton) {
        removeComponents(panel,wid, widthComboBox,thick, thicknessComboBox,dia, diameterComboBox);
        addComponents(panel, thick, thicknessComboBox, checkAvailabilityButton);
    }

    private static void updatePanelForRectangle(JPanel panel, JComboBox<String> shapeComboBox, JLabel wid,
                                                JComboBox<Integer> widthComboBox,JLabel thick, JComboBox<Integer> thicknessComboBox, JLabel dia,
                                                JComboBox<Integer> diameterComboBox, JButton checkAvailabilityButton) {
        removeComponents(panel,wid, widthComboBox,thick, thicknessComboBox,dia, diameterComboBox);
        addComponents(panel, wid, widthComboBox, thick, thicknessComboBox, checkAvailabilityButton);
    }

    private static void updatePanelForRound(JPanel panel, JComboBox<String> shapeComboBox, JLabel wid,
                                                JComboBox<Integer> widthComboBox,JLabel thick, JComboBox<Integer> thicknessComboBox, JLabel dia,
                                                JComboBox<Integer> diameterComboBox, JButton checkAvailabilityButton) {
        removeComponents(panel,wid, widthComboBox,thick, thicknessComboBox,dia, diameterComboBox);
        addComponents(panel, dia, diameterComboBox, checkAvailabilityButton);
    }
    private static void removeComponents(JPanel panel, Component... components) {
        for (Component component : components) {
            panel.remove(component);
        }
    }
    private static void addComponents(JPanel panel, Component... components) {
        for (Component component : components) {
            panel.add(component);
        }
    }
    private static void addStock(JComboBox<String> materialComboBox, JComboBox<String> shapeComboBox, JTextField costTextField,
                                 JComboBox<Integer> quantityComboBox, JComboBox<Integer> lengthComboBox, JComboBox<Integer> widthComboBox,
                                 JComboBox<Integer> thicknessComboBox, JComboBox<Integer> diameterComboBox) {
        String material = (String) materialComboBox.getSelectedItem();
        String shape = (String) shapeComboBox.getSelectedItem();
        int quantity = (Integer) quantityComboBox.getSelectedItem();
        int length = (Integer) lengthComboBox.getSelectedItem();
        int width = (Integer) widthComboBox.getSelectedItem();
        int thickness = (Integer) thicknessComboBox.getSelectedItem();
        int diameter = (Integer) diameterComboBox.getSelectedItem();

        String costForOneRodText = costTextField.getText();
        double costForOneRod;

        try {
            costForOneRod = Double.parseDouble(costForOneRodText);
            costForOneRod = costForOneRod / quantity;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(addStockFrame, "Invalid cost value. Please enter a valid number.");
            return;
        }

        String date = java.time.LocalDate.now().toString();
        insertDataIntoDatabase(material, shape, quantity, length, width, thickness, diameter, costForOneRod, date);
    }
                   private static void insertDataIntoDatabase(String material, String shape, int quantity, int length, int width, int thickness, int diameter, double costForOneRod, String date) {
        String sanitizedMaterial = material.toLowerCase().replace(" ", "_");
        String tableName = sanitizedMaterial+ "_" + shape.toLowerCase();
        String query = "INSERT INTO " + tableName + " (quantity, length, width, thickness, diameter, cost_price_per_inch, selling_price_per_inch, stock_added_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                for (int i = 0; i < quantity; i++) {
                    pstmt.setInt(1, 1);  // Quantity is set to 1 for each iteration
                    pstmt.setInt(2, length);
                    pstmt.setInt(3, width);
                    pstmt.setInt(4, thickness);
                    pstmt.setInt(5, diameter);
                    pstmt.setDouble(6, costForOneRod);
                    double sellingPrice = costForOneRod + (0.30 * costForOneRod);
                    pstmt.setDouble(7, sellingPrice);
                    pstmt.setString(8, date);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();  // Execute the batch for all iterations
    
                JOptionPane.showMessageDialog(addStockFrame, "Stock added successfully!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(addStockFrame, "Error adding stock: " + ex.getMessage());
        }
    }
}
