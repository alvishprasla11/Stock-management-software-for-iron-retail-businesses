import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Financials {
    private static final String DATABASE_URL = IronInventoryManagementApp.DATABASE_URL;
    private static String selectedMonth;
    public static void createFinancialsFrame() {
        JFrame financialsFrame = createFrame("Financials", 500, 500);
        JPanel panel = createMainPanel();
        financialsFrame.add(panel);
        financialsFrame.setVisible(true);
    }

    private static JFrame createFrame(String title, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        return frame;
    }

    private static JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2));
        addComponents(panel);
        return panel;
    }

    private static void addComponents(JPanel panel) {
        List<String> months = generateMonths(2023, 5);
        JComboBox<String> monthComboBox = new JComboBox<>(new DefaultComboBoxModel<>(months.toArray(new String[0])));
        List<String> years = generateYear(2023, 5);
        JComboBox<String> yearComboBox = new JComboBox<>(new DefaultComboBoxModel<>(years.toArray(new String[0])));

        panel.add(new JLabel("User ID:"));
        JTextField userID = new JTextField();
        panel.add(userID);
        panel.add(new JLabel("PASSWORD:"));
        JTextField password = new JTextField();
        panel.add(password);
        panel.add(new JLabel("Select Month:"));
        panel.add(monthComboBox);
        panel.add(new JLabel("Select Year:"));
        panel.add(yearComboBox);
        JButton continueButton = new JButton("Continue");
        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleContinueButtonClick(monthComboBox,yearComboBox, userID, password);
            }
        });

        panel.add(continueButton);
    }

    private static void handleContinueButtonClick(JComboBox<String> monthComboBox,JComboBox<String> yearComboBox, JTextField userID, JTextField password) {
        String UserID1 = "NEW STEEL SYNDICATE";
        String Password1 = "TAJDIN";

        String enteredUserID = userID.getText();
        String enteredPassword = password.getText();

        if (enteredUserID.equals(UserID1) && enteredPassword.equals(Password1)) {
            createPrintProfitLossStatementFrame(monthComboBox,yearComboBox);
        } else if (enteredUserID.equals(UserID1) && !enteredPassword.equals(Password1)) {
            showMessage("Wrong password entered!");
        } else {
            showMessage("Wrong User ID and password entered!");
        }
    }
    public static void createPrintProfitLossStatementFrame(JComboBox<String> monthComboBox,JComboBox<String> yearComboBox) {
        JFrame createPrintInvoiceFrame = new JFrame("Month or year");
        createPrintInvoiceFrame.setSize(500, 300);
        createPrintInvoiceFrame.setLocationRelativeTo(null);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2));
        String chosenMonth = (String) monthComboBox.getSelectedItem();
        selectedMonth = convertToDatabaseFormat(chosenMonth);
        String selectedYear = (String) yearComboBox.getSelectedItem();
        JButton profitLossByYear = new JButton("Profit Loss Statement by Year");
        JButton profitLossByMonth = new JButton("Profit Loss Statement by month");
        
        profitLossByMonth.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BalanceSheet.createBalanceSheet(selectedMonth);
            }
        });

        profitLossByYear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BalanceSheetYear.createBalanceSheet(selectedYear);
            }
        });
        panel.add(profitLossByMonth);
        panel.add(profitLossByYear);
        createPrintInvoiceFrame.add(panel);
        createPrintInvoiceFrame.setVisible(true);
    }
    private static String convertToDatabaseFormat(String oldFormatMonth) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM");

        YearMonth yearMonth = YearMonth.parse(oldFormatMonth, inputFormatter);
        return yearMonth.format(outputFormatter);
    }
    private static void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Confirmation", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private static List<String> generateMonths(int startYear, int numberOfYears) {
        List<String> monthList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");

        for (int year = startYear; year < startYear + numberOfYears; year++) {
            for (int month = 1; month <= 12; month++) {
                String formattedMonth = YearMonth.of(year, month).format(formatter);
                monthList.add(formattedMonth);
            }
        }

        return monthList;
    }
    
    private static List<String> generateYear(int startYear, int numberOfYears) {
        List<String> yearList = new ArrayList<>();

        for (int year = startYear; year < startYear + numberOfYears; year++) {
            yearList.add(String.valueOf(year));
        }

        return yearList;
    }
}
