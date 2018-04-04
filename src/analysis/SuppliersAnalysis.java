package analysis;

import dbconnector.DBConnector;
import sun.applet.Main;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SuppliersAnalysis {

    //dimension setting
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    //getting images
    URL url1 = Main.class.getResource("/img/logo.png");
    URL url2 = Main.class.getResource("/img/payments.png");
    URL url3 = Main.class.getResource("/img/delete.png");
    URL url4 = Main.class.getResource("/img/search.png");
    URL url5 = Main.class.getResource("/img/updatepay.png");

    ImageIcon imagepayments = new ImageIcon(url2);
    ImageIcon imagedelete = new ImageIcon(url3);
    ImageIcon imagesearch = new ImageIcon(url4);
    ImageIcon imagepay = new ImageIcon(url5);

    //setting ma default image icon to my frames
    final ImageIcon icon2 = new ImageIcon(url2);
    final ImageIcon icon = new ImageIcon(url3);
    Image iconimage = new ImageIcon(url1).getImage();

    //setiing components
    JLabel lserial, ldname, lname, lquantity, lcost, lpay, lbalance, ltitle, lcompany, llogo, lpayed, ltotalcost, ltotalpayments, ltotalbalance, llogo1, llogo2;
    JTextField tserial, tdname, tname, tquantity, tcost, tpay, tbalance, tsearch, tpayed, ttotalcost, ttotalpayments, ttotalbalance;
    JTable table;
    JButton bload, bsearch, bpay, bdelete, bback;

    //panels
    JPanel panelcompany = new JPanel(new GridBagLayout());
    JPanel paneltable = new JPanel(new GridBagLayout());
    JPanel panelall = new JPanel(new GridBagLayout());
    JPanel panelmain = new JPanel(new BorderLayout(0, 0));

    //frame
    JFrame Fsuppliers = new JFrame();

    //tabele colums
    String[] values = new String[]{"Serial Number", "Company Name", "Product Name", "Total Stock", "Cost", "Amount Paid", "Balance", "Last Edited On"};
    double cost2, total, payed, total2;
    String cost, payedamount;
    //database connectors
    Statement stmt = null;
    ResultSet rs, rs1, rs2, rs3, rs4 = null;
    PreparedStatement prs, prs2 = null;

    //date
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy, hh:mm:ss.SSS a");
    LocalDateTime now = LocalDateTime.now();
    String fileeditedlast = dtf.format(now);

    java.util.Date today = new java.util.Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
    String format = formatter.format(today);

    //calling class dbconnect
    DBConnector xamppfailure = new DBConnector();

    //method for getting tablecontents
    private void tablecont() {
        String productserial = table.getValueAt(table.getSelectedRow(), 0).toString();
        String companyname = table.getValueAt(table.getSelectedRow(), 1).toString();
        String productname = table.getValueAt(table.getSelectedRow(), 2).toString();
        double storein = Double.parseDouble(String.valueOf(table.getValueAt(table.getSelectedRow(), 3)));
        double storeincost = Double.parseDouble(String.valueOf(table.getValueAt(table.getSelectedRow(), 4)));
        double storepay = Double.parseDouble(String.valueOf(table.getValueAt(table.getSelectedRow(), 5)));
        double storebalance = Double.parseDouble(String.valueOf(table.getValueAt(table.getSelectedRow(), 6)));

        //setting values got from table
        tserial.setText(productserial);
        tdname.setText(companyname);
        tname.setText(productname);
        tquantity.setText(String.format("%.2f", storein));
        tcost.setText(String.format("%.2f", storeincost));
        tpayed.setText(String.format("%.2f", storepay));
        tpay.setText(null);
        tbalance.setText(String.format("%.2f", storebalance));
    }

    //updating data on textfield
    private void updatefield() {
        cost = tcost.getText();
        payedamount = tpay.getText();
        cost2 = Double.parseDouble(cost);
        payed = Double.parseDouble(payedamount);
        if (cost.equalsIgnoreCase("") && payedamount.equalsIgnoreCase("")) {
            //JOptionPane.showMessageDialog(null, "No Cost or Quantity given", "Error Message", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                Connection con = DBConnector.getConnection();
                String sqlqsum = "SELECT * FROM tabledistributers WHERE Mserial  = '" + tserial.getText() + "'";
                prs = con.prepareStatement(sqlqsum);
                rs = prs.executeQuery();
                while (rs.next()) {
                    double payedfound = rs.getDouble("Payed");
                    double balancefound = rs.getDouble("Balance");
                    double verifybalance = balancefound - payed;
                    double limit = 0;
                    if (verifybalance < limit) {
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(null, "The Amount Your Paying Is Extra" + "\n" + "Please Recheck Balance", "Payment Check", JOptionPane.INFORMATION_MESSAGE);
                        tbalance.setText(null);
                        tpayed.setText(null);
                    } else {
                        total2 = payedfound + payed;
                        total = balancefound - payed;
                        tpayed.setText(String.format("%.2f", total2));
                        tbalance.setText(String.format("%.2f", total));
                    }
                }
            } catch (SQLException x) {
                xamppfailure.getCon();
            }
        }
    }

    //method for getting all the payments,balance
    private void all() {
        try {
            Connection con = DBConnector.getConnection();

            //getting totalcost
            String stockcost = "SELECT SUM(Mtotalcost) TCOST FROM  tabledistributers";
            prs = con.prepareStatement(stockcost);
            rs1 = prs.executeQuery();
            rs1.next();
            double storeincost = rs1.getDouble("TCOST");
            ttotalcost.setText(String.format("%.2f", storeincost));

            //getting totalpayments
            String sqlout = "SELECT SUM(Payed) PSUM FROM tabledistributers";
            prs = con.prepareStatement(sqlout);
            rs2 = prs.executeQuery();
            rs2.next();
            double storeout = rs2.getDouble("PSUM");
            ttotalpayments.setText(String.format("%.2f", storeout));

            //getting totalbance
            String sqloutcost = "SELECT SUM(Balance) BSUM FROM tabledistributers";
            prs = con.prepareStatement(sqloutcost);
            rs3 = prs.executeQuery();
            rs3.next();
            double storecostout = rs3.getDouble("BSUM");
            ttotalbalance.setText(String.format("%.2f", storecostout));

            rs1.close();
            rs2.close();
            rs3.close();
            stmt.close();
            con.close();
        } catch (SQLException x) {
            xamppfailure.getCon();
        }
    }

    //method for searching
    private void searchengine() {
        String valuesearch = tsearch.getText();
        if (valuesearch.equalsIgnoreCase("") || valuesearch.equalsIgnoreCase(null)) {
            JOptionPane.showMessageDialog(null, "Please Enter The Product Name/Serial Number/Company Name For Search", "Search Message", JOptionPane.INFORMATION_MESSAGE);
        } else {
            DefaultTableModel pharmacymodel = new DefaultTableModel();
            //dm.addColumn("Medicine Name");
            pharmacymodel.setColumnIdentifiers(values);
            String fetchrecord = "SELECT * FROM tabledistributers WHERE Mserial = '" + tsearch.getText() + "' || Mname = '" + tsearch.getText() + "' || Dname = '" + tsearch.getText() + "'";
            try {
                Connection con = DBConnector.getConnection();
                stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                rs = stmt.executeQuery(fetchrecord);
                if (rs.next()) {
                    do {
                        String productserial = rs.getString("Mserial");
                        String comapnyname = rs.getString("Dname");
                        String productname = rs.getString("Mname");
                        double drugstock = rs.getDouble("Mquantity");
                        String sstock = String.format("%.2f", drugstock);
                        double drugcost = rs.getDouble("Mtotalcost");
                        String scost = String.format("%.2f", drugcost);
                        double drugpayed = rs.getDouble("Payed");
                        String stpayed = String.format("%.2f", drugpayed);
                        double drugbalance = rs.getDouble("Balance");
                        String stbalance = String.format("%.2f", drugbalance);
                        String drugredited = rs.getString("LastEdited");

                        pharmacymodel.addRow(new String[]{productserial, comapnyname, productname, sstock, scost, stpayed, stbalance, drugredited});

                        //setting values got from table
                        tserial.setText(productserial);
                        tdname.setText(comapnyname);
                        tname.setText(productname);
                        tquantity.setText(String.format("%.2f", drugstock));
                        tcost.setText(String.format("%.2f", drugcost));
                        tpayed.setText(String.format("%.2f", drugpayed));
                        tpay.setText(null);
                        tbalance.setText(String.format("%.2f", drugbalance));

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

    //method for uploading values
    public void uplaod() {
        DefaultTableModel pharmacymodel = new DefaultTableModel();
        //dm.addColumn("Medicine Name");
        pharmacymodel.setColumnIdentifiers(values);
        String fetchrecord = "SELECT * FROM tabledistributers";
        try {
            Connection con = DBConnector.getConnection();
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(fetchrecord);
            if (rs.next()) {
                do {
                    String productserial = rs.getString("Mserial");
                    String comapnyname = rs.getString("Dname");
                    String productname = rs.getString("Mname");
                    double drugstock = rs.getDouble("Mquantity");
                    String sstock = String.format("%.2f", drugstock);
                    double drugcost = rs.getDouble("Mtotalcost");
                    String scost = String.format("%.2f", drugcost);
                    double drugpayed = rs.getDouble("Payed");
                    String stpayed = String.format("%.2f", drugpayed);
                    double drugbalance = rs.getDouble("Balance");
                    String stbalance = String.format("%.2f", drugbalance);
                    String drugredited = rs.getString("LastEdited");

                    pharmacymodel.addRow(new String[]{productserial, comapnyname, productname, sstock, scost, stpayed, stbalance, drugredited});

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
                JOptionPane.showMessageDialog(null, "No Suppliers In The Store", "Notification", JOptionPane.INFORMATION_MESSAGE);
                rs.close();
                stmt.close();
                con.close();
            }
        } catch (SQLException x) {
            JOptionPane.showMessageDialog(null, "Database Connection Failure\nPlease Rerun the application", "Error Message", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    //method for payments
    private void supplierpayments() {
        try {
            Connection con = DBConnector.getConnection();
            String serialgot = tserial.getText();
            if (serialgot.equalsIgnoreCase("") || serialgot.equalsIgnoreCase(null)) {
                JOptionPane.showMessageDialog(null, "Please Select The Supplier From The Table To Pay", "Payment Message", JOptionPane.INFORMATION_MESSAGE);
            } else {
                String[] option = {"Yes", "No"};
                int selloption = JOptionPane.showOptionDialog(null, "Proceed in Deleting" + " " + tdname.getText() + " " + "Details with Serial_Number" + " " + serialgot, "Payment Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
                if (selloption == 0) {
                    String updatepayments = "UPDATE tabledistributers set Payed = '" + total2 + "',Balance = '" + total + "',LastEdited = '" + fileeditedlast + "' WHERE Mserial = '" + tserial.getText() + "'";
                    prs = con.prepareStatement(updatepayments);
                    prs.execute();
                    if (prs != null) {
                        //adding to table payment records
                        String sqladd = "INSERT INTO paymentrecords(Cname,CIdentity,Tcost,Payed,Balance,Dpayed,LastEdited) VALUES (?,?,?,?,?,?,?)";
                        prs2 = con.prepareStatement(sqladd);

                        //setting to database
                        prs2.setString(1, tdname.getText());
                        prs2.setString(2, tname.getText());
                        prs2.setDouble(3, Double.parseDouble(tcost.getText()));
                        prs2.setDouble(4, total2);
                        prs2.setDouble(5, Double.parseDouble(tbalance.getText()));
                        prs2.setString(6, format);
                        prs2.setString(7, fileeditedlast);

                        prs2.execute();

                        JOptionPane.showMessageDialog(null, tdname.getText() + " " + "Payed Successful", "Payment Confirmation", JOptionPane.INFORMATION_MESSAGE, icon2);
                        rs.close();
                        prs.close();
                        prs2.close();
                        con.close();

                        //setting null values
                        tname.setText(null);
                        tserial.setText(null);
                        tdname.setText(null);
                        tcost.setText(null);
                        tquantity.setText(null);
                        tpayed.setText(null);
                        tpay.setText(null);
                        tbalance.setText(null);

                        //method for reloading details
                        all();
                        uplaod();
                    } else {
                        JOptionPane.showMessageDialog(null, tdname.getText() + " " + "Payment Failed", "Payment Message", JOptionPane.ERROR_MESSAGE);
                        rs.close();
                        prs.close();
                        con.close();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Payment Cancelled", "Error Message", JOptionPane.ERROR_MESSAGE);
                    rs.close();
                    prs.close();
                    con.close();
                }
            }
        } catch (SQLException x) {
            xamppfailure.getCon();
        }
    }

    public void Suppliers() {
        llogo = new JLabel(imagepayments);
        llogo1 = new JLabel(imagepayments);
        llogo2 = new JLabel(imagepayments);
        lcompany = new JLabel("PAYMENTS");
        lcompany.setFont(new Font("Tahoma", Font.BOLD, 15));
        ltitle = new JLabel("PRODUCT'S SUPPLIERS DETAILS");
        ltitle.setFont(new Font("Tahoma", Font.BOLD, 20));
        tsearch = new JTextField(25);
        tsearch.setFont(new Font("Tahoma", Font.PLAIN, 15));
        tsearch.setToolTipText("Search Supplier By Product/Name/Serial Number");

        table = new JTable();
        // this enables horizontal scroll bar
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(20, 20));
        // table.setPreferredScrollableViewportSize(new Dimension(935, 570));
        table.setPreferredScrollableViewportSize(new Dimension((int) (screenSize.width / 1.47), (int) (screenSize.height / 1.40)));
        table.revalidate();
        table.setFont(new Font("Tahoma", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        ltotalpayments = new JLabel("Total Payments[KSH]");
        ltotalpayments.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ltotalcost = new JLabel("Total Amount[KSH]");
        ltotalcost.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ltotalbalance = new JLabel("Total Balance[KSH]");
        ltotalbalance.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lserial = new JLabel("Serial Number");
        lserial.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ldname = new JLabel("Supplier Name");
        ldname.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lname = new JLabel("Product Name");
        lname.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lquantity = new JLabel("Total Stock");
        lquantity.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lcost = new JLabel("Cost[KSH]");
        lcost.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lpayed = new JLabel("Amount Paid[KSH]");
        lpayed.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lpay = new JLabel("Pay[KSH]");
        lpay.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lbalance = new JLabel("Balance[KSH]");
        lbalance.setFont(new Font("Tahoma", Font.PLAIN, 15));

        ttotalcost = new JTextField(12);
        ttotalcost.setBackground(Color.LIGHT_GRAY);
        //ttotalcost.setEditable(false);
        ttotalcost.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttotalpayments = new JTextField(12);
        ttotalpayments.setBackground(Color.LIGHT_GRAY);
        // ttotalpayments.setEditable(false);
        ttotalpayments.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttotalbalance = new JTextField(12);
        ttotalbalance.setBackground(Color.LIGHT_GRAY);
        // ttotalbalance.setEditable(false);
        ttotalbalance.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tserial = new JTextField(15);
        tserial.setBackground(Color.LIGHT_GRAY);
        tserial.setEditable(false);
        tserial.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tdname = new JTextField(15);
        tdname.setBackground(Color.LIGHT_GRAY);
        tdname.setEditable(false);
        tdname.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tname = new JTextField(15);
        tname.setBackground(Color.LIGHT_GRAY);
        tname.setEditable(false);
        tname.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tquantity = new JTextField(15);
        tquantity.setBackground(Color.LIGHT_GRAY);
        tquantity.setEditable(false);
        tquantity.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tcost = new JTextField(15);
        tcost.setEditable(false);
        tcost.setBackground(Color.LIGHT_GRAY);
        tcost.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tpayed = new JTextField(15);
        tpayed.setEditable(false);
        tpayed.setBackground(Color.LIGHT_GRAY);
        tpayed.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tpay = new JTextField(15);
        tpay.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tbalance = new JTextField(15);
        tbalance.setBackground(Color.LIGHT_GRAY);
        tbalance.setEditable(false);
        tbalance.setFont(new Font("Tahoma", Font.PLAIN, 13));

        //buttons
        bsearch = new JButton(imagesearch);
        bsearch.setBackground(Color.LIGHT_GRAY);
        bsearch.setToolTipText("Search Supplier");
        bsearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bload = new JButton("RELOAD SUPPLIERS");
        bload.setBackground(Color.GREEN.darker());
        bload.setToolTipText("reload suppliers data/information");
        bload.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bload.setFont(new Font("Tahoma", Font.BOLD, 15));
        bdelete = new JButton(imagedelete);
        bdelete.setBackground(Color.RED.brighter());
        bdelete.setToolTipText("Please delete only fully paid supplier");
        bdelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bpay = new JButton(imagepay);
        bpay.setBackground(Color.GREEN.brighter());
        bpay.setToolTipText("Pay Supplier");
        bpay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bback = new JButton("BACK");
        bback.setFont(new Font("Tahoma", Font.BOLD, 15));
        bback.setBackground(Color.LIGHT_GRAY);
        bback.setCursor(new Cursor(Cursor.HAND_CURSOR));

        //adding components to paneltable
        GridBagConstraints v = new GridBagConstraints();
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

        //panel all
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 10, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        panelall.add(bback, v);
        v.gridy++;
        panelall.add(llogo1, v);
        v.gridy++;
        panelall.add(ltotalcost, v);
        v.gridy++;
        panelall.add(ttotalcost, v);
        v.gridy++;
        panelall.add(ltotalpayments, v);
        v.gridy++;
        panelall.add(ttotalpayments, v);
        v.gridy++;
        panelall.add(ltotalbalance, v);
        v.gridy++;
        panelall.add(ttotalbalance, v);
        v.gridy++;
        panelall.add(llogo2, v);
        panelall.setBorder(new TitledBorder(""));

        //panel company
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 10, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        panelcompany.add(llogo, v);
        v.gridy++;
        v.insets = new Insets(0, 0, 30, 0);
        panelcompany.add(lcompany, v);
        v.gridy++;
        v.insets = new Insets(0, 0, 0, 0);
        panelcompany.add(lserial, v);
        v.gridy++;
        panelcompany.add(tserial, v);
        v.gridy++;
        panelcompany.add(ldname, v);
        v.gridy++;
        panelcompany.add(tdname, v);
        v.gridy++;
        panelcompany.add(lname, v);
        v.gridy++;
        panelcompany.add(tname, v);
        v.gridy++;
        panelcompany.add(lquantity, v);
        v.gridy++;
        panelcompany.add(tquantity, v);
        v.gridy++;
        panelcompany.add(lcost, v);
        v.gridy++;
        panelcompany.add(tcost, v);
        v.gridy++;
        panelcompany.add(lpayed, v);
        v.gridy++;
        panelcompany.add(tpayed, v);
        v.gridy++;
        panelcompany.add(lpay, v);
        v.gridy++;
        panelcompany.add(tpay, v);
        v.gridy++;
        panelcompany.add(lbalance, v);
        v.gridy++;
        panelcompany.add(tbalance, v);
        v.insets = new Insets(30, 0, 0, 0);
        v.anchor = GridBagConstraints.WEST;
        v.gridy++;
        panelcompany.add(bpay, v);
        v.anchor = GridBagConstraints.EAST;
        panelcompany.add(bdelete, v);
        panelcompany.setBorder(new TitledBorder(""));

        //seeting to main panelmain
        panelmain.add("West", panelall);
        panelmain.add("Center", paneltable);
        panelmain.add("East", panelcompany);
        panelmain.setBorder(new TitledBorder(""));
        panelmain.setBackground(Color.blue.brighter());
        panelmain.revalidate();

        //methods for calculating payment aand balance
        tpay.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updatefield();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updatefield();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        //action for table to select a specific column
        table.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //getting values from table
                tablecont();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                //getting values from table
                tablecont();
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        uplaod();
        all();
        //setting frame
        Fsuppliers = new JFrame("Pharmacy System");
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("nimbusBase", Color.blue);
        } catch (Exception c) {
        }
        Fsuppliers.setUndecorated(true);
        Fsuppliers.setIconImage(iconimage);
        Fsuppliers.add(panelmain);
        Fsuppliers.setVisible(true);
        //Fstore.setSize(1400, 780);
        Fsuppliers.setSize(screenSize.width, screenSize.height);
        Fsuppliers.revalidate();
        Fsuppliers.pack();
        Fsuppliers.revalidate();
        Fsuppliers.setLocationRelativeTo(null);
        //Fsuppliers.setResizable(false);
        Fsuppliers.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        //frame state to make components responsive
        Fsuppliers.addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                // minimized
                if ((e.getNewState() & Frame.ICONIFIED) == Frame.ICONIFIED) {
                    Fsuppliers.revalidate();
                    Fsuppliers.pack();
                    Fsuppliers.revalidate();
                    Fsuppliers.setLocationRelativeTo(null);
                } // maximized
                else if ((e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                    Fsuppliers.revalidate();
                    Fsuppliers.pack();
                    Fsuppliers.revalidate();
                    Fsuppliers.setLocationRelativeTo(null);
                }
            }
        });

        //actions
        bload.addActionListener(e -> {
            uplaod();
            all();
        });
        bdelete.addActionListener(e -> {
            String serialgot = tserial.getText();
            try {
                if (serialgot.equalsIgnoreCase("")) {
                    JOptionPane.showMessageDialog(null, "Please Select The Supplier From The Table To Delete", "Deletion Message", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    Connection con = DBConnector.getConnection();
                    String[] option = {"Yes", "No"};
                    int selloption = JOptionPane.showOptionDialog(null, "Proceed in Deleting" + " " + tdname.getText() + " " + "Details with Serial_Number" + " " + serialgot, "Deletion Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
                    if (selloption == 0) {
                        String sqldelete = "DELETE FROM tabledistributers WHERE Mserial = '" + tserial.getText() + "'";
                        prs = con.prepareStatement(sqldelete);
                        prs.execute();
                        if (prs != null) {
                            JOptionPane.showMessageDialog(null, tdname.getText() + " " + "Deleted Successful", "Deletion Confirmation", JOptionPane.INFORMATION_MESSAGE, icon);
                            rs.close();
                            prs.close();
                            con.close();

                            //setting null values
                            tname.setText(null);
                            tserial.setText(null);
                            tdname.setText(null);
                            tcost.setText(null);
                            tquantity.setText(null);
                            tpayed.setText(null);
                            tpay.setText(null);
                            tbalance.setText(null);

                            //method for reloading details
                            all();
                            uplaod();
                        } else {
                            JOptionPane.showMessageDialog(null, tdname.getText() + " " + "Deletion Failed", "Deletion Message", JOptionPane.ERROR_MESSAGE);
                            rs.close();
                            prs.close();
                            con.close();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Deletion Cancelled", "Deletion Message", JOptionPane.ERROR_MESSAGE);
                        rs.close();
                        prs.close();
                        con.close();
                    }
                }
            } catch (SQLException x) {
                xamppfailure.getCon();
            }

        });
        bsearch.addActionListener(e -> {
            searchengine();
        });
        bpay.addActionListener(e -> {
            supplierpayments();
        });
        bback.addActionListener(e -> {
            Fsuppliers.setVisible(false);
            AnalysisType antype = new AnalysisType();
            antype.ChooseSection();
        });
        //end of actions

    }
}
