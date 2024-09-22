import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.*;
import java.sql.*;
import java.util.Calendar;
import java.util.Locale;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class BalanceSheet {
    private static JTextArea balanceSheetTextArea;
    private static final String DATABASE_URL = IronInventoryManagementApp.DATABASE_URL;
    private static String selectedMonth;

    public static void createBalanceSheet(String initialSelectedMonth) {
        selectedMonth = initialSelectedMonth;

        JFrame balanceSheetFrame = createBalanceSheetFrame();

        JPanel panel = createBalanceSheetPanel();

        JButton printButton = createPrintButton();
        addPrintButtonListener(printButton);

        panel.add(new JScrollPane(balanceSheetTextArea));
        panel.add(printButton);

        balanceSheetFrame.add(panel);
        balanceSheetFrame.setVisible(true);

        // Update the balance sheet when creating it
        calculateBalanceSheet(); 
    }

    private static JFrame createBalanceSheetFrame() {
        JFrame balanceSheetFrame = new JFrame("Balance Sheet");
        balanceSheetFrame.setSize(500, 500);
        balanceSheetFrame.setLocationRelativeTo(null);
        return balanceSheetFrame;
    }

    private static JPanel createBalanceSheetPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        balanceSheetTextArea = new JTextArea();
        balanceSheetTextArea.setEditable(false);
        return panel;
    }

    private static JButton createPrintButton() {
        return new JButton("Print Balance Sheet");
    }

    private static void addPrintButtonListener(JButton printButton) {
        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printBalanceSheet();
            }
        });
    }

    private static void calculateBalanceSheet() {
        // Existing calculation logic...
        double stockBroughtValue = getStockBroughtValue(selectedMonth);
        double stockSoldValue = getStockSoldValue(selectedMonth);
        double excessStock = stockBroughtValue - (stockSoldValue/1.30);
        double profitBeforeTax = (stockSoldValue - stockBroughtValue)+ excessStock;
        double taxRate = 0.18;
        double taxAmount = profitBeforeTax * taxRate;
        double netProfitAfterTax = profitBeforeTax - taxAmount;

        StringBuilder balanceSheetText = new StringBuilder("Balance Sheet for " + selectedMonth + ":\n\n");
        balanceSheetText.append("Stock Brought: $").append(stockBroughtValue).append("\n");
        balanceSheetText.append("Stock Sold: $").append(stockSoldValue).append("\n");
        balanceSheetText.append("Excess Stock: $").append(excessStock).append("\n");
        balanceSheetText.append("Profit Before Tax: $").append(profitBeforeTax).append("\n");
        balanceSheetText.append("Tax (18%): $").append(taxAmount).append("\n");
        balanceSheetText.append("Net Profit After Tax: $").append(netProfitAfterTax).append("\n");

        balanceSheetTextArea.setText(balanceSheetText.toString());
    }

    private static void printBalanceSheet() {
        PrinterJob printerJob = PrinterJob.getPrinterJob();

        PageFormat pageFormat = printerJob.pageDialog(printerJob.defaultPage());

        printerJob.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE;
                }

                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                // You can customize the font, size, and other properties for printing
                Font font = new Font("Monospaced", Font.PLAIN, 12);
                g2d.setFont(font);

                String[] lines = balanceSheetTextArea.getText().split("\n");

                int lineHeight = g2d.getFontMetrics().getHeight();
                int y = 0;

                for (String line : lines) {
                    y += lineHeight;
                    g2d.drawString(line, 0, y);
                }

                return PAGE_EXISTS;
            }
        }, pageFormat);

        if (printerJob.printDialog()) {
            try {
                printerJob.print();
            } catch (PrinterException ex) {
                ex.printStackTrace();
            }
        }
    }

   
    
          private static double getTotalStockValue(String tableName, String selectedMonth) {
    double totalStockValue = 0.0;
    String url = DATABASE_URL;

    // Use strftime to compare the month and year from the date
    String query = "SELECT IFNULL(SUM(cost_price_per_inch), 0.0) FROM " + tableName +
            " WHERE strftime('%Y-%m', stock_added_date) = ?";

    try (Connection conn = DriverManager.getConnection(url);
         PreparedStatement pstmt = conn.prepareStatement(query)) {

        pstmt.setString(1, selectedMonth);

        // Print the generated SQL query
        System.out.println("Generated SQL Query: " + pstmt.toString());

        try (ResultSet resultSet = pstmt.executeQuery()) {
            if (resultSet.next()) {
                totalStockValue = resultSet.getDouble(1);
            }
        }

    } catch (SQLException e) {
        System.out.println("Error executing query: " + e.getMessage());
        e.printStackTrace();
    }

    System.out.println("Total Stock Value for " + selectedMonth + ": " + totalStockValue);

    return totalStockValue;
}


    public static double getStockBroughtValue(String selectedMonth) {
        double totalStockBroughtValue = 0.0;

        // Loop through all materials and shapes
        String[] materials = {"ms", "polished_mild_steel", "ohns", "wps", "en_31"};
        String[] shapes = {"square", "rectangle", "round"};

        for (String material : materials) {
            for (String shape : shapes) {
                totalStockBroughtValue += getTotalStockValue(material + "_" + shape, selectedMonth);
            }
        }
        String tableName="stockSoldTable";
        totalStockBroughtValue += getTotalStockValue(tableName, selectedMonth);
        
        
        return totalStockBroughtValue;
    }

    public static double getStockSoldValue(String selectedMonth) {
        String tableName="stockSoldTable";
        double cp=getTotalStockValue(tableName, selectedMonth);
        double sp=cp*1.30;
        return sp;
    }

}
