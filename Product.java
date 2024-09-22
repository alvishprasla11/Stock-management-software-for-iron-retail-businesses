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

public class Product {
    public static List<List<Object>> productsList = new ArrayList<>();
    public static List<Integer> quan = new ArrayList<>();
        public static void addProduct(String material, String shape, int width, int length, int thickness, int diameter, double costPerUnit, String date, int quantity) {
        quan.add(quantity-1);
            for (int i = 0; i < quantity; i++) {
            List<Object> details = new ArrayList<>();
            details.add(material);
            details.add(shape);
            details.add(width);
            details.add(length);
            details.add(thickness);
            details.add(diameter);
            details.add(costPerUnit);
            details.add(date);
            details.add(quantity); // Set the quantity in the loop to i + 1
            productsList.add(details);
        }
    }
    public static void clearLists() {
        productsList.clear();
        quan.clear();
    }
}

