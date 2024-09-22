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
public class Print {
    private static final String DATABASE_URL = IronInventoryManagementApp.DATABASE_URL;

    public static void printInvoice() {
        JTextArea invoiceTextArea = new JTextArea();
        StringBuilder invoiceText = new StringBuilder();
        int maxSize = 1000;

        Integer[] dimensions = new Integer[maxSize];

        for (int i = 0; i < maxSize; i++) {
            dimensions[i] = i + 1;
        }

        invoiceText.append(java.time.LocalDate.now().toString()).append("\n\n\n");
        double total = 0;
        double spTotal = 0;
        int quantit;
        for (List<Object> productDetails : Product.productsList) {
                String material="";
                String shape="";
                int length=0;
                int thickness=0;
                int width=0;
                int diameter=0;
                double costPrice;
                
                material = (String) productDetails.get(0);
                shape = (String) productDetails.get(1);
                width = (int) productDetails.get(2);
                thickness = (int) productDetails.get(4);
                diameter = (int) productDetails.get(5);
                length = (int) productDetails.get(3);
                costPrice = (double) productDetails.get(6);
                int quantity = (int) productDetails.get(8); // quantity is common for all products
    
                
                double sp = costPrice * 1.30;
               spTotal = spTotal + sp;
               invoiceText.append("Material: ").append(material).append("\n");
            invoiceText.append("Shape: ").append(shape).append("\n");
            invoiceText.append("Length: ").append(length).append("\n");
            if (shape=="square"){
                invoiceText.append("Thickness: ").append(thickness).append("\n");
            }
            else if(shape=="rectangle"){
                invoiceText.append("Thickness: ").append(thickness).append("\n");
                invoiceText.append("Width: ").append(width).append("\n");
            }
            else{
                invoiceText.append("Diameter: ").append(diameter).append("\n");
            }
            invoiceText.append("Quantity: ").append(quantity).append("\n");
            invoiceText.append("Price:\t\t\t\t\t").append("₹" + spTotal*quantity).append("\n\n");
            invoiceText.append("\n------------------------------------------------------------------------------------------------------------\n");
                
            }
            Product.clearLists();
            total = total + spTotal;
        

        double tax = 0.095 * total;
        double tax2 = (0.18 * total) + total;

        invoiceText.append("Total before tax:\t\t\t\t₹" + total).append("\n");
        invoiceText.append("CGST(9.5%):\t\t\t\t\t₹" + tax).append("\n");
        invoiceText.append("SGST(9.5%):\t\t\t\t\t₹" + tax).append("\n\n");
        invoiceText.append("Final Total: \t\t\t\t\t ₹" + tax2).append("\n");
        invoiceTextArea.setText(invoiceText.toString());

        // Create a message format for printing
        MessageFormat header = new MessageFormat("NEW STEEL SYNDICATE INVOICE");
        MessageFormat footer = new MessageFormat("Page {0}");

        try {
            // Print the invoice
            invoiceTextArea.print(header, footer);
        } catch (PrinterException ex) {
            ex.printStackTrace();
        }
    }
}
