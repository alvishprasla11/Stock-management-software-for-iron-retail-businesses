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
public class PrintConfirmation extends PrintInvoice {

    private static JFrame printConfirmationFrame;
    private static final String DATABASE_URL = IronInventoryManagementApp.DATABASE_URL;

    public static void showPrintConfirmation() {
        printConfirmationFrame = new JFrame("Print Confirmation");
        printConfirmationFrame.setSize(500, 500);
        printConfirmationFrame.setLocationRelativeTo(null);
        JPanel panel = new JPanel(new GridLayout(1, 1));

        JTextArea confirmationTextArea = new JTextArea();
        confirmationTextArea.setEditable(false);

        StringBuilder confirmationText = new StringBuilder("New Steel Syndicate Invoice\n");
        confirmationText.append("Date: ").append(java.time.LocalDate.now().toString()).append("\n\n");
        int selling = 0;

        for (List<Object> productDetails : Product.productsList) {
            String material = (String) productDetails.get(0);
            String shape = (String) productDetails.get(1);
            double costPrice = (double) productDetails.get(6); // cost price is at index 6
            int quantity = (int) productDetails.get(8); // quantity is common for all products
    
            for (int i = 0; i < quantity; i++) {
                confirmationText.append(material).append(" ").append(shape);
                confirmationText.append(" - Price: ").append(String.valueOf(costPrice * 1.30)).append("\n");
                selling += costPrice * 1.30; // Increment based on the cost price and not on the quantity
            }
        }
        // Calculate and display total
        double total = selling;
        confirmationText.append("\nTotal: ").append(total);
        confirmationTextArea.setText(confirmationText.toString());

        JButton confirmPrintButton = new JButton("Confirm and Print");
        confirmPrintButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Proceed with printing
                Print.printInvoice();
                printConfirmationFrame.dispose();
            }
        });

        panel.add(new JScrollPane(confirmationTextArea));
        panel.add(confirmPrintButton);

        printConfirmationFrame.add(panel);
        printConfirmationFrame.setVisible(true);
    }
}