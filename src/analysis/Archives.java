package analysis;

import com.toedter.calendar.JDateChooser;
import dbconnector.DBConnector;
import sun.applet.Main;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.swing.text.JTextComponent;

public class Archives {

    //dimension setting
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    //getting images
    URL url1 = Main.class.getResource("/img/logo.png");
    URL url2 = Main.class.getResource("/img/records.png");
    URL url3 = Main.class.getResource("/img/pastrecords.png");
    URL url4 = Main.class.getResource("/img/delete.png");
    URL url5 = Main.class.getResource("/img/search.png");
    URL url6 = Main.class.getResource("/img/view1.png");

    ImageIcon imagerecords = new ImageIcon(url2);
    ImageIcon imagedelete = new ImageIcon(url4);
    ImageIcon imagesearch = new ImageIcon(url5);
    ImageIcon imagepastrecords = new ImageIcon(url3);
    ImageIcon imageview = new ImageIcon(url6);

    //setting ma default image icon to my frames
    final ImageIcon icon = new ImageIcon(url4);
    Image iconimage = new ImageIcon(url1).getImage();

    JLabel llogo1, llogo2, ltitle, ltitle2, lsection, ladvanced, ladvanced2, llogo3, llogo4, llogo5, lrecord, lpast, ltotalamount, ltotalpatyments, ltotalbalance;
    JTextField tadvanced, tadvanced2, tsearch, tsearch2, ttotalamount, ttotalpatyments, ttotalbalance;
    JTable table, table2;
    JButton bload, bload2, bsearch, bsearch2, bdelete, bdelete2, brecord, bpastrecord, bback;

    JPanel panelmain = new JPanel(new BorderLayout(0, 0));
    JPanel panelmenu = new JPanel(new GridBagLayout());
    JPanel panelcard = new JPanel(new CardLayout(0, 0));
    JPanel panelrecord = new JPanel(new BorderLayout(0, 0));
    JPanel panelpastrecord = new JPanel(new BorderLayout(0, 0));
    JPanel paneltable = new JPanel(new GridBagLayout());
    JPanel paneltable2 = new JPanel(new GridBagLayout());
    JPanel paneldelete = new JPanel(new GridBagLayout());
    JPanel paneldelete2 = new JPanel(new GridBagLayout());

    JDateChooser dateexpry;

    JFrame Farchive = new JFrame();

    //set variables
    String[] values = new String[]{"Product Name", "Serial Number", "Stock In", "Stock In Cost", "Stock Out", "Stock Out Cost", "Total Profit", "Total Deficit", "Saved On"};
    String[] values2 = new String[]{"Company Name", "Identity", "Total Amount", "Payed Amount", "Balance", "Payed On"};

    //database connectors
    Statement stmt = null;
    ResultSet rs, rs1, rs2, rs3, rs4, rs5, rs6 = null;
    PreparedStatement prs = null;

    //date
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy, hh:mm:ss.SSS a");
    LocalDateTime now = LocalDateTime.now();
    String fileeditedlast = dtf.format(now);

    //calling class dbconnect
    DBConnector xamppfailure = new DBConnector();

    //method for searching
    private void searchengine2() {
        String medate = ((JTextComponent) dateexpry.getDateEditor().getUiComponent()).getText();
        String valuesearch = tsearch2.getText();
        if (valuesearch.equalsIgnoreCase("") || valuesearch.equalsIgnoreCase(null) || medate == null) {
            JOptionPane.showMessageDialog(null, "Please Enter The Record Name/Identity/Select Date or Use Both For Search", "Archive Message", JOptionPane.INFORMATION_MESSAGE);
        } else {
            DefaultTableModel pharmacymodel = new DefaultTableModel();
            //dm.addColumn("Medicine Name");
            pharmacymodel.setColumnIdentifiers(values2);
            String fetchrecord = "SELECT * FROM paymentrecords WHERE Cname = '" + tsearch2.getText() + "' || CIdentity = '" + tsearch2.getText() + "' || Dpayed = '" + medate + "'";
            try {
                Connection con = DBConnector.getConnection();
                stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                rs = stmt.executeQuery(fetchrecord);
                if (rs.next()) {
                    do {
                        String cname = rs.getString("Cname");
                        String cidentity = rs.getString("CIdentity");
                        double tcost = rs.getDouble("Tcost");
                        String ccost = String.format("%.2f", tcost);
                        double cpay = rs.getDouble("Payed");
                        String spay = String.format("%.2f", cpay);
                        double balance = rs.getDouble("Balance");
                        String sbalance = String.format("%.2f", balance);
                        String cedited = rs.getString("LastEdited");

                        pharmacymodel.addRow(new String[]{cname, cidentity, ccost, spay, sbalance, cedited});

                    } while (rs.next());
                    table2.setModel(pharmacymodel);
                    table2.setAutoCreateRowSorter(true);
                    table2.setFillsViewportHeight(true);
                    table2.revalidate();

                    rs.close();
                    stmt.close();
                    con.close();

                } else {
                    table2.setModel(pharmacymodel);
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(null, "Sorry No Data Found For" + " " + tsearch2.getText(), "Notification", JOptionPane.INFORMATION_MESSAGE);
                    uplaod2();
                    rs.close();
                    stmt.close();
                    con.close();
                }
            } catch (SQLException x) {
                xamppfailure.getCon();
            }

            //code for fetching all the sum of stock in paymentrecords using the OR DECISION MAKING
            try {
                Connection con = DBConnector.getConnection();
                String sqlqsum1 = "SELECT SUM(Tcost) STOREQ FROM paymentrecords WHERE Cname = '" + tsearch2.getText() + "' || CIdentity = '" + tsearch2.getText() + "' || Dpayed = '" + medate + "'";
                prs = con.prepareStatement(sqlqsum1);
                rs1 = prs.executeQuery();
                if (rs1.next()) {
                    double costfound = rs1.getDouble("STOREQ");
                    String sstock = String.format("%.2f", costfound);

                    ttotalamount.setText(sstock);
                    rs1.close();
                    stmt.close();
                    con.close();
                }
            } catch (SQLException x) {
                xamppfailure.getCon();
            }

            //code for fetching all the sum of cost in paymentrecords
            try {
                Connection con = DBConnector.getConnection();
                String sqlcsum2 = "SELECT SUM(Payed) STORETC FROM paymentrecords WHERE Cname = '" + tsearch2.getText() + "' || CIdentity = '" + tsearch2.getText() + "' || Dpayed = '" + medate + "'";
                prs = con.prepareStatement(sqlcsum2);
                rs2 = prs.executeQuery();
                if (rs2.next()) {
                    double paymentsfound = rs2.getDouble("STORETC");
                    String sspayments = String.format("%.2f", paymentsfound);

                    ttotalpatyments.setText(sspayments);
                    rs2.close();
                    prs.close();
                    stmt.close();
                    con.close();
                }
            } catch (SQLException x) {
//                JOptionPane.showMessageDialog(null, "Connection Failure" + "\n" + x, "Error Message", JOptionPane.ERROR_MESSAGE);
//                //System.exit(0);
            }

            //code for fetching all the sum of cost in paymentrecords
            try {
                Connection con = DBConnector.getConnection();
                String sqlcsum3 = "SELECT SUM(Balance) STORETC FROM paymentrecords WHERE Cname = '" + tsearch2.getText() + "' || CIdentity = '" + tsearch2.getText() + "' || Dpayed = '" + medate + "'";
                prs = con.prepareStatement(sqlcsum3);
                rs3 = prs.executeQuery();
                if (rs3.next()) {
                    double balancefound = rs3.getDouble("STORETC");
                    String ssbalance = String.format("%.2f", balancefound);

                    ttotalbalance.setText(ssbalance);
                    rs3.close();
                    prs.close();
                    stmt.close();
                    con.close();
                }
            } catch (SQLException x) {
//                JOptionPane.showMessageDialog(null, "Connection Failure" + "\n" + x, "Error Message", JOptionPane.ERROR_MESSAGE);
//                //System.exit(0);
            }

            //code for fetching all the sum of stock in paymentrecords using the AND DECISION MAKING
            try {
                Connection con = DBConnector.getConnection();
                String sqlqsum1d = "SELECT SUM(Tcost) STOREQR FROM paymentrecords WHERE (Cname = '" + tsearch2.getText() + "' || CIdentity = '" + tsearch2.getText() + "') && Dpayed = '" + medate + "'";
                prs = con.prepareStatement(sqlqsum1d);
                rs4 = prs.executeQuery();
                if (rs4.next()) {
                    double cfound = rs4.getDouble("STOREQR");
                    String sstock = String.format("%.2f", cfound);

                    ttotalamount.setText(sstock);
                    rs4.close();
                    stmt.close();
                    con.close();
                }
            } catch (SQLException x) {
//                JOptionPane.showMessageDialog(null, "Connection Failure" + "\n" + x, "Error Message", JOptionPane.ERROR_MESSAGE);
//                //System.exit(0);
            }

            //code for fetching all the sum of cost in paymentrecords
            try {
                Connection con = DBConnector.getConnection();
                String sqlcsum2d = "SELECT SUM(Payed) STORETCR FROM paymentrecords WHERE (Cname = '" + tsearch2.getText() + "' || CIdentity = '" + tsearch2.getText() + "') && Dpayed = '" + medate + "'";
                prs = con.prepareStatement(sqlcsum2d);
                rs5 = prs.executeQuery();
                if (rs5.next()) {
                    double pfound = rs5.getDouble("STORETCR");
                    String sspayments = String.format("%.2f", pfound);

                    ttotalpatyments.setText(sspayments);
                    rs5.close();
                    stmt.close();
                    con.close();
                }
            } catch (SQLException x) {
//                JOptionPane.showMessageDialog(null, "Connection Failure" + "\n" + x, "Error Message", JOptionPane.ERROR_MESSAGE);
//                //System.exit(0);
            }

            //code for fetching all the sum of cost in paymentrecords
            try {
                Connection con = DBConnector.getConnection();
                String sqlcsum3d = "SELECT SUM(Balance) STORETCK FROM paymentrecords WHERE (Cname = '" + tsearch2.getText() + "' || CIdentity = '" + tsearch2.getText() + "') && Dpayed = '" + medate + "'";
                prs = con.prepareStatement(sqlcsum3d);
                rs6 = prs.executeQuery();
                if (rs6.next()) {
                    double bfound = rs6.getDouble("STORETCK");
                    String ssbalance = String.format("%.2f", bfound);

                    ttotalbalance.setText(ssbalance);
                    rs6.close();
                    stmt.close();
                    con.close();
                }
            } catch (SQLException x) {
//                JOptionPane.showMessageDialog(null, "Connection Failure" + "\n" + x, "Error Message", JOptionPane.ERROR_MESSAGE);
//                //System.exit(0);
            }
        }
    }
    //method for uploadin data in system

    private void uplaod2() {
        DefaultTableModel pharmacymodel = new DefaultTableModel();
        //dm.addColumn("Medicine Name");
        pharmacymodel.setColumnIdentifiers(values2);
        String fetchrecord = "SELECT * FROM paymentrecords";
        try {
            Connection con = DBConnector.getConnection();
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(fetchrecord);
            if (rs.next()) {
                do {
                    String cname = rs.getString("Cname");
                    String cidentity = rs.getString("CIdentity");
                    double tcost = rs.getDouble("Tcost");
                    String ccost = String.format("%.2f", tcost);
                    double cpay = rs.getDouble("Payed");
                    String spay = String.format("%.2f", cpay);
                    double balance = rs.getDouble("Balance");
                    String sbalance = String.format("%.2f", balance);
                    String cedited = rs.getString("LastEdited");

                    pharmacymodel.addRow(new String[]{cname, cidentity, ccost, spay, sbalance, cedited});

                } while (rs.next());
                table2.setModel(pharmacymodel);
                table2.setAutoCreateRowSorter(true);
                table2.setFillsViewportHeight(true);
                table2.revalidate();

                rs.close();
                stmt.close();
                con.close();
            } else {
                table2.setModel(pharmacymodel);
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "No Payment Records In The Store For Analysis", "Notification", JOptionPane.INFORMATION_MESSAGE);
                rs.close();
                stmt.close();
                con.close();
            }
        } catch (SQLException x) {
            JOptionPane.showMessageDialog(null, "Database Connection Failure\nPlease Rerun the application", "Error Message", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        //code for fetching all the sum of stock in paymentrecords
        try {
            Connection con = DBConnector.getConnection();
            String sqlqsumall = "SELECT SUM(Tcost) STOREQ FROM paymentrecords";
            prs = con.prepareStatement(sqlqsumall);
            rs = prs.executeQuery();
            if (rs.next()) {
                double costfound = rs.getDouble("STOREQ");
                String sstock = String.format("%.2f", costfound);

                ttotalamount.setText(sstock);
                rs.close();
                prs.close();
                stmt.close();
                con.close();
            }
        } catch (SQLException x) {
            JOptionPane.showMessageDialog(null, "Database Connection Failure", "Error Message", JOptionPane.ERROR_MESSAGE);
            //System.exit(0);
        }

        //code for fetching all the sum of cost in paymentrecords
        try {
            Connection con = DBConnector.getConnection();
            String sqlcsumall2 = "SELECT SUM(Payed) STORETC FROM paymentrecords";
            prs = con.prepareStatement(sqlcsumall2);
            rs2 = prs.executeQuery();
            if (rs2.next()) {
                double paymentsfound = rs2.getDouble("STORETC");
                String sspayments = String.format("%.2f", paymentsfound);

                ttotalpatyments.setText(sspayments);
                rs2.close();
                prs.close();
                stmt.close();
                con.close();
            }
        } catch (SQLException x) {
            JOptionPane.showMessageDialog(null, "Connection Failure" + "\n" + x, "Error Message", JOptionPane.ERROR_MESSAGE);
            //System.exit(0);
        }

        //code for fetching all the sum of cost in paymentrecords
        try {
            Connection con = DBConnector.getConnection();
            String sqlcsumall3 = "SELECT SUM(Balance) STORETCX FROM paymentrecords";
            prs = con.prepareStatement(sqlcsumall3);
            rs3 = prs.executeQuery();
            if (rs3.next()) {
                double balancefound = rs3.getDouble("STORETCX");
                String ssbalance = String.format("%.2f", balancefound);

                ttotalbalance.setText(ssbalance);
                rs3.close();
                prs.close();
                stmt.close();
                con.close();
            }
        } catch (SQLException x) {
            JOptionPane.showMessageDialog(null, "Connection Failure" + "\n" + x, "Error Message", JOptionPane.ERROR_MESSAGE);
            //System.exit(0);
        }
    }

    //method for searching
    private void searchengine() {
        String valuesearch = tsearch.getText();
        if (valuesearch.equalsIgnoreCase("") || valuesearch.equalsIgnoreCase(null)) {
            JOptionPane.showMessageDialog(null, "Please Enter The Record Name/Serial Number", "Search Message", JOptionPane.INFORMATION_MESSAGE);
        } else {
            DefaultTableModel pharmacymodel = new DefaultTableModel();
            //dm.addColumn("Medicine Name");
            pharmacymodel.setColumnIdentifiers(values);
            String fetchrecord = "SELECT * FROM pastrecords WHERE Mserial = '" + tsearch.getText() + "' || Mname = '" + tsearch.getText() + "'";
            try {
                Connection con = DBConnector.getConnection();
                stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                rs = stmt.executeQuery(fetchrecord);
                if (rs.next()) {
                    do {
                        String drugname = rs.getString("Mname");
                        String drugserial = rs.getString("Mserial");
                        double druginstock = rs.getDouble("StockIn");
                        String sinstock = String.format("%.2f", druginstock);
                        double drugincost = rs.getDouble("StockInCost");
                        String sincost = String.format("%.2f", drugincost);
                        double drugoutstock = rs.getDouble("StockOut");
                        String soutstock = String.format("%.2f", drugoutstock);
                        double drugoutcost = rs.getDouble("StockOutCost");
                        String soutcost = String.format("%.2f", drugoutcost);
                        double drugprofit = rs.getDouble("Profit");
                        String sprofit = String.format("%.2f", drugprofit);
                        double drugloss = rs.getDouble("Loss");
                        String sloss = String.format("%.2f", drugloss);
                        String drugeditedon = rs.getString("LastEdited");

                        pharmacymodel.addRow(new String[]{drugname, drugserial, sinstock, sincost, soutstock, soutcost, sprofit, sloss, drugeditedon});

                    } while (rs.next());
                    table.setModel(pharmacymodel);
                    table.setAutoCreateRowSorter(true);
                    table.setFillsViewportHeight(true);
                    table.revalidate();

                    rs.close();
                    stmt.close();
                    con.close();
                } else {
                    table.setModel(pharmacymodel);
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(null, "Sorry No Data Found For" + " " + tsearch.getText(), "Notification", JOptionPane.INFORMATION_MESSAGE);
                    uplaod();
                    rs.close();
                    stmt.close();
                    con.close();
                }
            } catch (SQLException x) {
                xamppfailure.getCon();
            }
        }
    }

    //method for uploadin data in system
    private void uplaod() {
        DefaultTableModel pharmacymodel = new DefaultTableModel();
        //dm.addColumn("Medicine Name");
        pharmacymodel.setColumnIdentifiers(values);
        String fetchrecord = "SELECT * FROM pastrecords";
        try {
            Connection con = DBConnector.getConnection();
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(fetchrecord);
            if (rs.next()) {
                do {
                    String drugname = rs.getString("Mname");
                    String drugserial = rs.getString("Mserial");
                    double druginstock = rs.getDouble("StockIn");
                    String sinstock = String.format("%.2f", druginstock);
                    double drugincost = rs.getDouble("StockInCost");
                    String sincost = String.format("%.2f", drugincost);
                    double drugoutstock = rs.getDouble("StockOut");
                    String soutstock = String.format("%.2f", drugoutstock);
                    double drugoutcost = rs.getDouble("StockOutCost");
                    String soutcost = String.format("%.2f", drugoutcost);
                    double drugprofit = rs.getDouble("Profit");
                    String sprofit = String.format("%.2f", drugprofit);
                    double drugloss = rs.getDouble("Loss");
                    String sloss = String.format("%.2f", drugloss);
                    String drugeditedon = rs.getString("LastEdited");

                    pharmacymodel.addRow(new String[]{drugname, drugserial, sinstock, sincost, soutstock, soutcost, sprofit, sloss, drugeditedon});

                } while (rs.next());
                table.setModel(pharmacymodel);
                table.setAutoCreateRowSorter(true);
                table.setFillsViewportHeight(true);
                table.revalidate();

                rs.close();
                stmt.close();
                con.close();
            } else {
                Toolkit.getDefaultToolkit().beep();
                table.setModel(pharmacymodel);
                JOptionPane.showMessageDialog(null, "No Past Records In The Store For Analysis", "Notification", JOptionPane.INFORMATION_MESSAGE);
                rs.close();
                stmt.close();
                con.close();
            }
        } catch (SQLException x) {
            JOptionPane.showMessageDialog(null, "Database Connection Failure\nPlease Rerun the application", "Error Message", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    public void Archives() {
        //date code
        dateexpry = new JDateChooser();
        dateexpry.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dateexpry.setPreferredSize(new Dimension(200, 35));
        dateexpry.setDateFormatString("yyyy/MM/dd");
        dateexpry.setFont(new Font("Tahoma", Font.PLAIN, 12));

        llogo1 = new JLabel(imageview);
        llogo2 = new JLabel(imageview);
        llogo3 = new JLabel(imageview);
        llogo4 = new JLabel(imageview);
        llogo5 = new JLabel(imageview);
        lpast = new JLabel("PAST STOCK RECORDS");
        lpast.setFont(new Font("Tahoma", Font.PLAIN, 10));
        lrecord = new JLabel("PAYMENT RECORDS");
        lrecord.setFont(new Font("Tahoma", Font.PLAIN, 10));
        lsection = new JLabel("THE ARCHIVES");
        lsection.setFont(new Font("Tahoma", Font.BOLD, 13));

        //tables
        table = new JTable();
        // this enables horizontal scroll barz
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.setIntercellSpacing(new Dimension(20, 20));
        // table.setPreferredScrollableViewportSize(new Dimension(960, 570));
        table.setPreferredScrollableViewportSize(new Dimension((int) (screenSize.width / 1.43), (int) (screenSize.height / 1.40)));
        table.revalidate();
        table.setFont(new Font("Tahoma", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        table2 = new JTable();
        // this enables horizontal scroll bar
        table2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table2.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table2.setFillsViewportHeight(true);
        table2.setIntercellSpacing(new Dimension(20, 20));
        // table2.setPreferredScrollableViewportSize(new Dimension(935, 570));
        table2.setPreferredScrollableViewportSize(new Dimension((int) (screenSize.width / 1.47), (int) (screenSize.height / 1.40)));
        table2.revalidate();
        table2.setFont(new Font("Tahoma", Font.PLAIN, 13));
        JScrollPane scrollPane2 = new JScrollPane(table2, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        //for center
        ltitle = new JLabel("PAST STOCK RECORDS");
        ltitle.setFont(new Font("Tahoma", Font.BOLD, 20));
        tsearch = new JTextField(25);
        tsearch.setFont(new Font("Tahoma", Font.PLAIN, 15));
        tsearch.setToolTipText("Search Record By Name/Serial Number");
        ltitle2 = new JLabel("PAST PAYMENT RECORDS");
        ltitle2.setFont(new Font("Tahoma", Font.BOLD, 20));
        tsearch2 = new JTextField(20);
        tsearch2.setFont(new Font("Tahoma", Font.PLAIN, 15));
        tsearch2.setToolTipText("Search Record By Name/Identity");
        ladvanced = new JLabel("Deletion[STOCK]");
        ladvanced.setForeground(Color.BLUE.darker());
        ladvanced.setFont(new Font("Tahoma", Font.BOLD, 15));
        tadvanced = new JTextField(13);
        tadvanced.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tadvanced.setToolTipText("Enter Record Name/Serial Number");
        ladvanced2 = new JLabel("Deletion[Payments]");
        ladvanced2.setForeground(Color.BLUE.darker());
        ladvanced2.setFont(new Font("Tahoma", Font.BOLD, 15));
        tadvanced2 = new JTextField(13);
        tadvanced2.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tadvanced2.setToolTipText("Enter Record Name/Identity");

        ltotalamount = new JLabel("Total Amount[KSH]");
        ltotalamount.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ttotalamount = new JTextField(15);
        ttotalamount.setBackground(Color.LIGHT_GRAY);
        //ttotalamount.setEditable(false);
        ttotalamount.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttotalamount.setToolTipText("Amount In Past Payments");
        ltotalpatyments = new JLabel("Total Payments[KSH]");
        ltotalpatyments.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ttotalpatyments = new JTextField(15);
        ttotalpatyments.setBackground(Color.LIGHT_GRAY);
        //ttotalpatyments.setEditable(false);
        ttotalpatyments.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttotalpatyments.setToolTipText("Total Payments In Past");

        ltotalbalance = new JLabel("Total Balance[KSH]");
        ltotalbalance.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ttotalbalance = new JTextField(15);
        ttotalbalance.setBackground(Color.LIGHT_GRAY);
        //ttotalbalance.setEditable(false);
        ttotalbalance.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttotalbalance.setToolTipText("Total Payments In Past");

        bsearch = new JButton(imagesearch);
        bsearch.setBackground(Color.LIGHT_GRAY);
        bsearch.setToolTipText("Search Past Record");
        bsearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bsearch2 = new JButton(imagesearch);
        bsearch2.setBackground(Color.LIGHT_GRAY);
        bsearch2.setToolTipText("Search Past Payment");
        bsearch2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bload = new JButton("RELOAD PAST RECORDS");
        bload.setBackground(Color.GREEN.darker());
        bload.setToolTipText("reload past records");
        bload.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bload.setFont(new Font("Tahoma", Font.BOLD, 15));
        bload2 = new JButton("RELOAD PAYMENT RECORDS");
        bload2.setBackground(Color.GREEN.darker());
        bload2.setToolTipText("reload past records");
        bload2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bload2.setFont(new Font("Tahoma", Font.BOLD, 15));
        bdelete = new JButton(imagedelete);
        bdelete.setBackground(Color.RED.brighter());
        bdelete.setToolTipText("Please Dalete Only The Finished Stock(Enter Serial Number To Delete/ALL)");
        bdelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bdelete2 = new JButton(imagedelete);
        bdelete2.setBackground(Color.RED.brighter());
        bdelete2.setToolTipText("Please Dalete Only The Finished Stock(Enter Name/Identity To Delete/ALL)");
        bdelete2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bpastrecord = new JButton(imagepastrecords);
        //bpastrecord.setBackground(Color.lightGray);
        bpastrecord.setToolTipText("Section Past Records");
        bpastrecord.setCursor(new Cursor(Cursor.HAND_CURSOR));
        brecord = new JButton(imagerecords);
        //brecord.setBackground(Color.lightGray);
        brecord.setToolTipText("Section Payment Records");
        brecord.setCursor(new Cursor(Cursor.HAND_CURSOR));

        bback = new JButton("BACK");
        bback.setFont(new Font("Tahoma", Font.BOLD, 15));
        bback.setBackground(Color.LIGHT_GRAY);
        bback.setCursor(new Cursor(Cursor.HAND_CURSOR));

        //adding to panel menu
        GridBagConstraints v = new GridBagConstraints();
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 30, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        panelmenu.add(bback, v);
        v.gridy++;
        panelmenu.add(llogo3, v);
        v.gridy++;
        panelmenu.add(lsection, v);
        v.insets = new Insets(0, 0, 0, 0);
        v.gridy++;
        panelmenu.add(bpastrecord, v);
        v.insets = new Insets(0, 0, 70, 0);
        v.gridy++;
        panelmenu.add(lpast, v);
        v.insets = new Insets(0, 0, 0, 0);
        v.gridy++;
        panelmenu.add(brecord, v);
        v.gridy++;
        panelmenu.add(lrecord, v);
        panelmenu.revalidate();
        panelmenu.setBorder(new TitledBorder(""));

        //adding components to paneltable
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        paneltable.add(ltitle, v);
        v.gridy++;
        v.anchor = GridBagConstraints.WEST;
        v.insets = new Insets(0, 0, 0, 0);
        paneltable.add(bload, v);
        v.anchor = GridBagConstraints.EAST;
        v.insets = new Insets(0, 0, 0, 53);
        paneltable.add(tsearch, v);
        v.insets = new Insets(0, 0, 0, 0);
        paneltable.add(bsearch, v);
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(15, 0, 0, 0);
        v.gridy++;
        paneltable.add(scrollPane, v);
        paneltable.setBorder(new TitledBorder(""));
        paneltable.revalidate();

        //paneldelete
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 50, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        paneldelete.add(llogo1, v);
        v.insets = new Insets(0, 0, 0, 0);
        v.gridy++;
        paneldelete.add(ladvanced, v);
        v.gridy++;
        paneldelete.add(tadvanced, v);
        v.insets = new Insets(15, 0, 50, 0);
        v.gridy++;
        paneldelete.add(bdelete, v);
        v.gridy++;
        paneldelete.add(llogo4, v);
        paneldelete.setBorder(new TitledBorder(""));
        paneldelete.revalidate();

        //panelpastrecord
        panelpastrecord.add("Center", paneltable);
        panelpastrecord.add("East", paneldelete);
        panelpastrecord.revalidate();

        //adding components to paneltable2
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        paneltable2.add(ltitle2, v);
        v.gridy++;
        v.anchor = GridBagConstraints.WEST;
        v.insets = new Insets(0, 0, 0, 0);
        paneltable2.add(bload2, v);
        v.anchor = GridBagConstraints.EAST;
        v.insets = new Insets(0, 0, 0, 326);
        paneltable2.add(dateexpry, v);
        v.insets = new Insets(0, 0, 0, 53);
        paneltable2.add(tsearch2, v);
        v.insets = new Insets(0, 0, 0, 0);
        paneltable2.add(bsearch2, v);
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(15, 0, 0, 0);
        v.gridy++;
        paneltable2.add(scrollPane2, v);
        paneltable2.setBorder(new TitledBorder(""));
        paneltable2.revalidate();

        //paneldelete2
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 50, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        paneldelete2.add(llogo2, v);
        v.insets = new Insets(0, 0, 0, 0);
        v.gridy++;
        paneldelete2.add(ltotalamount, v);
        v.gridy++;
        paneldelete2.add(ttotalamount, v);
        v.gridy++;
        paneldelete2.add(ltotalpatyments, v);
        v.gridy++;
        paneldelete2.add(ttotalpatyments, v);
        v.gridy++;
        paneldelete2.add(ltotalbalance, v);
        v.insets = new Insets(0, 0, 100, 0);
        v.gridy++;
        paneldelete2.add(ttotalbalance, v);
        v.insets = new Insets(0, 0, 0, 0);
        v.gridy++;
        paneldelete2.add(ladvanced2, v);
        v.gridy++;
        paneldelete2.add(tadvanced2, v);
        v.insets = new Insets(15, 0, 30, 0);
        v.gridy++;
        paneldelete2.add(bdelete2, v);
        v.gridy++;
        paneldelete2.add(llogo5, v);
        paneldelete2.setBorder(new TitledBorder(""));
        paneldelete2.revalidate();

        //panelrecord
        panelrecord.add("Center", paneltable2);
        panelrecord.add("East", paneldelete2);
        panelrecord.revalidate();

        //panelcard
        panelcard.add(panelpastrecord);
        panelcard.add(panelrecord);
        java.awt.CardLayout mcardLayout = (java.awt.CardLayout) (panelcard.getLayout());
        bpastrecord.addActionListener(e -> mcardLayout.first(panelcard));
        brecord.addActionListener(e -> mcardLayout.last(panelcard));
        panelcard.revalidate();

        //panelmain
        panelmain.add("West", panelmenu);
        panelmain.add("Center", panelcard);
        panelmain.setBorder(new TitledBorder(""));
        panelmain.setBackground(Color.blue.brighter());
        panelmain.revalidate();

        //actions and methods
        bdelete.addActionListener(e -> {
            String serialgot = tadvanced.getText();
            try {
                if (serialgot.equalsIgnoreCase("")) {
                    JOptionPane.showMessageDialog(null, "Please Enter The Serial Number To Delete Record", "Deletion Message", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    if (serialgot.equalsIgnoreCase("all")) {

                        Connection con = DBConnector.getConnection();
                        String[] option = {"Yes", "No"};
                        int selloption = JOptionPane.showOptionDialog(null, "Proceed in Deleting Will Delete The Entire Stock" + " \n" + "This should only be done if the the record is no of use", "Deletion Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
                        if (selloption == 0) {
                            String sqldelete = "DELETE FROM pastrecords";
                            prs = con.prepareStatement(sqldelete);
                            prs.execute();
                            if (prs != null) {
                                JOptionPane.showMessageDialog(null, "Entire Record Deleted Successful", "Deletion Confirmation", JOptionPane.INFORMATION_MESSAGE, icon);
                                rs.close();
                                prs.close();
                                con.close();

                                //setting to null
                                tadvanced.setText(null);

                                //method for reloading details
                                uplaod();
                            } else {
                                JOptionPane.showMessageDialog(null, tadvanced.getText() + " " + "Deletion Failed", "Error Message", JOptionPane.ERROR_MESSAGE);
                                rs.close();
                                prs.close();
                                con.close();
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Deletion Cancelled", "Error Message", JOptionPane.ERROR_MESSAGE);
                            rs.close();
                            prs.close();
                            con.close();
                        }
                    } else {

                        Connection con = DBConnector.getConnection();
                        String[] option = {"Yes", "No"};
                        int selloption = JOptionPane.showOptionDialog(null, "Proceeding in Deleting Will Delete The Entire Stock Of The Item" + " " + tadvanced.getText() + " " + "Details with Serial_Number" + "\n" + "This should only be done if the payment record is no of use", "Deletion Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
                        if (selloption == 0) {
                            String sqldelete = "DELETE FROM pastrecords WHERE Mserial = '" + tadvanced.getText() + "'";
                            prs = con.prepareStatement(sqldelete);
                            prs.execute();
                            if (prs != null) {
                                JOptionPane.showMessageDialog(null, "Entire Record Deleted Successful", "Deletion Confirmation", JOptionPane.INFORMATION_MESSAGE, icon);
                                rs.close();
                                prs.close();
                                con.close();

                                //setting to null
                                tadvanced.setText(null);

                                //method for reloading details
                                uplaod();
                            } else {
                                JOptionPane.showMessageDialog(null, tadvanced.getText() + " " + "Deletion Failed", "Error Message", JOptionPane.ERROR_MESSAGE);
                                rs.close();
                                prs.close();
                                con.close();
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Deletion Cancelled", "Error Message", JOptionPane.ERROR_MESSAGE);
                            rs.close();
                            prs.close();
                            con.close();
                        }
                    }
                }
            } catch (SQLException x) {
                xamppfailure.getCon();
            }

        });
        bdelete2.addActionListener(e -> {
            String serialgot = tadvanced2.getText();
            try {
                if (serialgot.equalsIgnoreCase("")) {
                    JOptionPane.showMessageDialog(null, "Please Enter The Name/Identity To Delete Record", "Deletion Message", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    if (serialgot.equalsIgnoreCase("all")) {

                        Connection con = DBConnector.getConnection();
                        String[] option = {"Yes", "No"};
                        int selloption = JOptionPane.showOptionDialog(null, "Proceeding in Deleting Will Delete The Entire Payment Records" + " \n" + "This should only be done if the Records are no of use", "Deletion Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
                        if (selloption == 0) {
                            String sqldelete = "DELETE FROM paymentrecords";
                            prs = con.prepareStatement(sqldelete);
                            prs.execute();
                            if (prs != null) {
                                JOptionPane.showMessageDialog(null, "Entire Record Deleted Successful", "Deletion Confirmation", JOptionPane.INFORMATION_MESSAGE, icon);
                                rs.close();
                                prs.close();
                                con.close();

                                //setting to null
                                tadvanced2.setText(null);

                                //method for reloading details
                                uplaod2();
                            } else {
                                JOptionPane.showMessageDialog(null, tadvanced2.getText() + " " + "Deletion Failed", "Error Message", JOptionPane.ERROR_MESSAGE);
                                rs.close();
                                prs.close();
                                con.close();
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Deletion Cancelled", "Error Message", JOptionPane.ERROR_MESSAGE);
                            rs.close();
                            prs.close();
                            con.close();
                        }
                    } else {

                        Connection con = DBConnector.getConnection();
                        String[] option = {"Yes", "No"};
                        int selloption = JOptionPane.showOptionDialog(null, "Proceed in Deleting Will Delete The Entire Payment Of The Item" + " " + tadvanced.getText() + "\n" + "This should only be done if the record is no of use", "Deletion Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
                        if (selloption == 0) {
                            String sqldelete = "DELETE FROM paymentrecords WHERE CIdentity = '" + tadvanced2.getText() + "' || Cname = '" + tadvanced2.getText() + "'";
                            prs = con.prepareStatement(sqldelete);
                            prs.execute();
                            if (prs != null) {
                                JOptionPane.showMessageDialog(null, "Entire Record Deleted Successful", "Deletion Confirmation", JOptionPane.INFORMATION_MESSAGE, icon);
                                rs.close();
                                prs.close();
                                con.close();

                                //setting to null
                                tadvanced.setText(null);

                                //method for reloading details
                                uplaod2();
                            } else {
                                JOptionPane.showMessageDialog(null, tadvanced2.getText() + " " + "Deletion Failed", "Error Message", JOptionPane.ERROR_MESSAGE);
                                rs.close();
                                prs.close();
                                con.close();
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Deletion Cancelled", "Error Message", JOptionPane.ERROR_MESSAGE);
                            rs.close();
                            prs.close();
                            con.close();
                        }
                    }
                }
            } catch (SQLException x) {
                xamppfailure.getCon();
            }

        });
        bload.addActionListener(e -> {
            uplaod();

        });
        bsearch.addActionListener(e -> {
            searchengine();

        });
        bload2.addActionListener(e -> {
            uplaod2();
        });
        bsearch2.addActionListener(e -> {
            searchengine2();
        });

        uplaod();
        uplaod2();
        //end of actions

        //frame code
        //setting frame
        Farchive = new JFrame("Pharmacy System");
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("nimbusBase", Color.blue);
        } catch (Exception c) {
        }
        Farchive.setUndecorated(true);
        Farchive.setIconImage(iconimage);
        Farchive.add(panelmain);
        Farchive.setVisible(true);
        //Fstore.setSize(1400, 780);
        Farchive.setSize(screenSize.width, screenSize.height);
        Farchive.revalidate();
        Farchive.pack();
        Farchive.revalidate();
        Farchive.setLocationRelativeTo(null);
        //Fstore.setResizable(false);
        Farchive.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        //frame state to make components responsive
        Farchive.addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                // minimized
                if ((e.getNewState() & Frame.ICONIFIED) == Frame.ICONIFIED) {
                    Farchive.revalidate();
                    Farchive.pack();
                    Farchive.revalidate();
                    Farchive.setLocationRelativeTo(null);
                } // maximized
                else if ((e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                    Farchive.revalidate();
                    Farchive.pack();
                    Farchive.revalidate();
                    Farchive.setLocationRelativeTo(null);
                }
            }
        });

        bback.addActionListener(e -> {
            Farchive.setVisible(false);
            AnalysisType antype = new AnalysisType();
            antype.ChooseSection();
        });
    }
}
