package selling;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.net.URL;
import java.security.SecureRandom;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import analysis.AnalysisType;
import dbconnector.DBConnector;

import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;

import login.Login;
import login.Sections;
import sun.applet.Main;

public class SellingDrug {

    //dimension setting
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    //getting images

    URL url1 = Main.class.getResource("/img/logo.png");
    URL url2 = Main.class.getResource("/img/selllogo.png");
    URL url3 = Main.class.getResource("/img/search.png");
    URL url4 = Main.class.getResource("/img/sellbutton.png");
    URL url5 = Main.class.getResource("/img/delete.png");
    URL url6 = Main.class.getResource("/img/print.png");
    URL url7 = Main.class.getResource("/img/cancel5.png");
    //setting images
    ImageIcon imageselllogo = new ImageIcon(url2);
    ImageIcon imagesearch = new ImageIcon(url3);
    ImageIcon imagesellbtn = new ImageIcon(url4);
    ImageIcon imageprint = new ImageIcon(url6);
    ImageIcon imagecancel = new ImageIcon(url7);

    //setting ma default image icon to my frames
    Image iconimage = new ImageIcon(url1).getImage();

    //images for joptionpanes
    final ImageIcon icon = new ImageIcon(url2);
    final ImageIcon icon2 = new ImageIcon(url5);

    //setting components
    JLabel lname, lquantity, ltotalcost, lprescription, lselllogo, largment, lcost, labout, ltitle, lcashpaid, lchange, lreceipt, linvoice, ltotal, lcash, lchangeprint, line1, line2, line3, line4, lwelcome, line5, lprint, lsellpoint;
    JTextField tname, tquantity, ttotalcost, tprescription, tsearch, targmant, tcost, tcashpaid, tchange, ttotal, tcash, tchangeprint, tnameprint, tdateprint, titems, tcprint, tserialprint;
    JButton bsearch;
    public JButton bsell;
    public JButton bselluser;
    JButton bcancel;
    public JButton bback;
    public JButton bbacksell;
    public JButton brefresh;
    public JButton brefreshadmin;
    JComboBox<String> boxdrugname;
    JTextArea treport;
    JTable table;

    //panel
    JPanel panelmain = new JPanel(new BorderLayout(0, 0));
    JPanel paneltable = new JPanel(new GridBagLayout());
    JPanel sellpanel = new JPanel(new BorderLayout(0, 0));
    JPanel panelprint = new JPanel(new GridBagLayout());
    JPanel panelproduct = new JPanel(new GridBagLayout());
    JPanel panelsell = new JPanel(new GridBagLayout());
    JPanel panelcost = new JPanel(new GridBagLayout());

    //frame
    JFrame Fsell = new JFrame();
    JFrame Fprint = new JFrame();

    //database connectors
    Statement stmt = null;
    ResultSet rs = null;
    PreparedStatement prs = null;

    //variables
    String[] values = new String[]{"Product Name", "Serial Number", "Quantity Available", "Cost [KSH]", "Section"};
    String cost, quantity, drugname, namefound;
    double cost2, quaty, total, costfound, change, dcash;
    StringBuilder sb;

    //date
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy, hh:mm:ss.SSS a");
    private static final DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("EEE,dd MMM yyyy,hh:mm a");
    LocalDateTime now = LocalDateTime.now();
    String fileeditedlast = dtf.format(now);
    String fileeditedlast2 = dtf2.format(now);
    Date today = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
    String format = formatter.format(today);
    double costage;

    //unique serial number
    Random randserial = new Random();
    int numserial = randserial.nextInt();

    //calling class dbconnect
    DBConnector xamppfailure = new DBConnector();

    //methods for refreshing
    private void refreshadmin() {
        Fsell.setVisible(false);
        SellingDrug dsell = new SellingDrug();
        dsell.DrugNameFound();
        dsell.SellDrug();
        dsell.bbacksell.setVisible(false);
        dsell.bselluser.setVisible(false);
        dsell.bsell.setVisible(true);
        dsell.bback.setVisible(true);
    }

    private void refreshuser() {
        Fsell.setVisible(false);
        SellingDrug dsell = new SellingDrug();
        dsell.DrugNameFound();
        dsell.SellDrug();
        dsell.bbacksell.setVisible(true);
        dsell.bselluser.setVisible(true);
        dsell.bsell.setVisible(false);
        dsell.bback.setVisible(false);
    }

    //method for cancelling
    private void canacelaction() {
        tname.setText("");
        targmant.setText("");
        tprescription.setText("");
        treport.setText("");
        tcost.setText("");
        tquantity.setText("");
        ttotalcost.setText("");
        tcashpaid.setText("");
        tchange.setText("");

        uplaod();

    }

    //method of calculating change
    private void getChange() {
        String cashpaid = tcashpaid.getText();
        dcash = Double.parseDouble(cashpaid);
        String tamount = ttotalcost.getText();
        double damount = Double.parseDouble(tamount);
        change = dcash - damount;

        tchange.setText(String.format("%.2f", change));
    }

    //method for fetching results on writing on textfield
    private void fetchData() {
        cost = tcost.getText();
        quantity = tquantity.getText();
        cost2 = Double.parseDouble(cost);
        quaty = Double.parseDouble(quantity);
        if (cost.equalsIgnoreCase("") && quantity.equalsIgnoreCase("")) {
            //JOptionPane.showMessageDialog(null, "No Cost or Quantity given", "Error Message", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                Connection con = DBConnector.getConnection();
                String sqlqsum = "SELECT Mquantity FROM store WHERE Mname = '" + drugname + "' || Mname = '" + tsearch.getText() + "'|| Mserial = '" + tsearch.getText() + "' ";
                prs = con.prepareStatement(sqlqsum);
                rs = prs.executeQuery();
                while (rs.next()) {
                    double quantityfound = rs.getDouble("Mquantity");
                    double verifyquantity = quantityfound - quaty;
                    double limit = 0;
                    if (verifyquantity < limit) {
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(null, "Sorry Only " + " " + quantityfound + " " + "Items Remaining In Stock Is Almost Finished Please Re-order", "Stock Notification", JOptionPane.INFORMATION_MESSAGE);
                        tquantity.setText(null);
                        ttotalcost.setText(null);
                    } else {
                        total = quaty * cost2;
                        //String costresult = Double.toString(total);
                        ttotalcost.setText(String.format("%.2f", total));
                    }
                }
            } catch (SQLException x) {
                xamppfailure.getCon();
            }

        }
    }

    //printing module
    private void printingreceiet() {
        //lprint = new JLabel(imageprint);
        lprint = new JLabel("");
        lprint.setFont(new Font("Tahoma", Font.PLAIN, 9));
        line1 = new JLabel("--------------------------------------------");
        line1.setFont(new Font("Tahoma", Font.PLAIN, 11));
        line2 = new JLabel("--------------------------------------------");
        line2.setFont(new Font("Tahoma", Font.PLAIN, 1));
        line3 = new JLabel("--------------------------------------------");
        line3.setFont(new Font("Tahoma", Font.PLAIN, 11));
        line4 = new JLabel("--------------------------------------------");
        line4.setFont(new Font("Tahoma", Font.PLAIN, 11));
        line5 = new JLabel("-");
        line5.setFont(new Font("Tahoma", Font.PLAIN, 10));
        lreceipt = new JLabel("CUSTOMER RECEIPT");
        lreceipt.setFont(new Font("Tahoma", Font.BOLD, 9));
        lcash = new JLabel("Cash");
        lcash.setFont(new Font("Tahoma", Font.PLAIN, 8));
        lchangeprint = new JLabel("Change");
        lchangeprint.setFont(new Font("Tahoma", Font.PLAIN, 8));
        linvoice = new JLabel("Tax Invoice");
        linvoice.setFont(new Font("Tahoma", Font.PLAIN, 9));
        ltotal = new JLabel("TOTAL");
        ltotal.setFont(new Font("Tahoma", Font.BOLD, 8));
        lwelcome = new JLabel("WELCOME AGAIN");
        lwelcome.setFont(new Font("Tahoma", Font.BOLD, 6));

        tdateprint = new JTextField(20);
        tdateprint.setFont(new Font("Tahoma", Font.PLAIN, 8));
        tdateprint.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        //tdateprint.setText(fileeditedlast2);
        tserialprint = new JTextField(20);
        tserialprint.setFont(new Font("Tahoma", Font.PLAIN, 8));
        tserialprint.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        //tserialprint.setText("REF NO: " + String.valueOf(numserial));
        tnameprint = new JTextField(20);
        tnameprint.setFont(new Font("Tahoma", Font.PLAIN, 8));
        tnameprint.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        //tnameprint.setText(tname.getText());
        titems = new JTextField(20);
        titems.setFont(new Font("Tahoma", Font.PLAIN, 8));
        titems.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        //titems.setText(tquantity.getText() + " item(s) ");
        ttotal = new JTextField(20);
        ttotal.setFont(new Font("Tahoma", Font.PLAIN, 8));
        ttotal.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        //ttotal.setText(ttotalcost.getText() + " KSH ");
        tcash = new JTextField(20);
        tcash.setFont(new Font("Tahoma", Font.PLAIN, 8));
        tcash.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        //tcash.setText(tcashpaid.getText() + " KSH ");
        tchangeprint = new JTextField(20);
        tchangeprint.setFont(new Font("Tahoma", Font.PLAIN, 8));
        tchangeprint.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        //tchangeprint.setText(tchange.getText() + " KSH ");
        tcprint = new JTextField(20);
        tcprint.setFont(new Font("Tahoma", Font.PLAIN, 8));
        tcprint.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        //tcprint.setText(tcost.getText() + " KSH ");

        //setting variables
        ttotal.setText(String.format("%.2f", total) + " KSH ");
        tcprint.setText(String.format("%.2f", cost2) + " KSH ");
        titems.setText(String.format("%.2f", quaty) + " item(s) ");
        tnameprint.setText(tname.getText());
        tserialprint.setText("REF NO: " + String.valueOf(numserial));
        tdateprint.setText(fileeditedlast2);
        tcash.setText(String.format("%.2f", dcash) + " KSH ");
        tchangeprint.setText(String.format("%.2f", change) + " KSH ");

        GridBagConstraints v = new GridBagConstraints();
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(10, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        panelprint.add(lprint, v);
        v.insets = new Insets(0, 0, 0, 0);
        v.gridy++;
        panelprint.add(lreceipt, v);
        v.gridy++;
        panelprint.add(linvoice, v);
        v.gridy++;
        panelprint.add(tdateprint, v);
        v.anchor = GridBagConstraints.CENTER;
        v.gridy++;
        panelprint.add(tserialprint, v);
        v.gridy++;
        panelprint.add(line1, v);

        v.anchor = GridBagConstraints.WEST;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 6;
        panelprint.add(tnameprint, v);
        v.anchor = GridBagConstraints.EAST;
        v.gridy++;
        panelprint.add(tcprint, v);
        v.anchor = GridBagConstraints.WEST;
        v.gridy++;
        panelprint.add(titems, v);
        v.gridy++;
        panelprint.add(ltotal, v);

        v.anchor = GridBagConstraints.EAST;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 7;
        //panelprint.add(tcprint, v);
        v.gridy++;
        panelprint.add(line5, v);
        v.gridy++;
        panelprint.add(ttotal, v);

        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 10;
        panelprint.add(line2, v);

        v.anchor = GridBagConstraints.WEST;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 12;
        panelprint.add(lcash, v);
        v.gridy++;
        panelprint.add(lchangeprint, v);

        v.anchor = GridBagConstraints.EAST;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 12;
        panelprint.add(tcash, v);
        v.gridy++;
        panelprint.add(tchangeprint, v);

        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 15;
        panelprint.add(line3, v);
        v.gridy++;
        panelprint.add(lwelcome, v);
        v.gridy++;
        panelprint.add(line4, v);
        panelprint.setBackground(Color.WHITE);

        Fprint = new JFrame();
        Fprint.setIconImage(iconimage);
        Fprint.add(panelprint);
        Fprint.setVisible(false);
        Fprint.pack();
        Fprint.setLocationRelativeTo(null);
        Fprint.setResizable(true);
        Fprint.revalidate();
        Fprint.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Fprint.setBackground(Color.white);

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Receipt");

        job.setPrintable(new Printable() {
            public int print(Graphics pg, PageFormat pf, int pageNum) {
                if (pageNum > 0) {
                    return Printable.NO_SUCH_PAGE;
                }
                Graphics2D g2 = (Graphics2D) pg;
                g2.translate(pf.getImageableX(), pf.getImageableY());
                panelprint.paint(g2);
                return Printable.PAGE_EXISTS;
            }
        });
        boolean ok = job.printDialog();
        if (ok) {
            try {
                job.print();
            } catch (PrinterException ex) {

            }
        }
    }

    //method for searching
    private void searchengineuploadtext() {
        DefaultTableModel pharmacymodel = new DefaultTableModel();
        //dm.addColumn("Medicine Name");
        pharmacymodel.setColumnIdentifiers(values);
        String fetchrecord = "SELECT * FROM store WHERE Mname = '" + tsearch.getText() + "' || Mserial = '" + tsearch.getText() + "' ";
        try {
            Connection con = DBConnector.getConnection();
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(fetchrecord);
            if (rs.next()) {
                do {
                    String drugname = rs.getString("Mname");
                    String drugserial = rs.getString("Mserial");
                    double druginstock = rs.getDouble("Mquantity");
                    String sinstock = String.format("%.2f", druginstock);
                    double drugincost = rs.getDouble("Mcost");
                    String sincost = String.format("%.2f", drugincost);
                    String drugeditedon = rs.getString("Msection");

                    pharmacymodel.addRow(new String[]{drugname, drugserial, sinstock, sincost, drugeditedon});

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
                JOptionPane.showMessageDialog(null, "No Stock In The Store For Sell", "Store Message", JOptionPane.INFORMATION_MESSAGE);
                rs.close();
                stmt.close();
                con.close();
            }
        } catch (SQLException x) {
            xamppfailure.getCon();
        }

    }

    //method for searching
    private void searchengineupload() {
        DefaultTableModel pharmacymodel = new DefaultTableModel();
        //dm.addColumn("Medicine Name");
        pharmacymodel.setColumnIdentifiers(values);
        String fetchrecord = "SELECT * FROM store WHERE Mname = '" + drugname + "'";
        try {
            Connection con = DBConnector.getConnection();
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(fetchrecord);
            if (rs.next()) {
                do {
                    String drugname = rs.getString("Mname");
                    String drugserial = rs.getString("Mserial");
                    double druginstock = rs.getDouble("Mquantity");
                    String sinstock = String.format("%.2f", druginstock);
                    double drugincost = rs.getDouble("Mcost");
                    String sincost = String.format("%.2f", drugincost);
                    String drugeditedon = rs.getString("Msection");

                    pharmacymodel.addRow(new String[]{drugname, drugserial, sinstock, sincost, drugeditedon});

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
                JOptionPane.showMessageDialog(null, "No Stock In The Store For Sell", "Store Message", JOptionPane.INFORMATION_MESSAGE);
                rs.close();
                stmt.close();
                con.close();
            }
        } catch (SQLException x) {
            xamppfailure.getCon();
        }

    }

    //method for getting data names from store
    public void DrugNameFound() {

        try {
            Connection con = DBConnector.getConnection();
            String sqldrugname = "SELECT * FROM store";
            prs = con.prepareStatement(sqldrugname);
            rs = prs.executeQuery();
            Vector vnames = new Vector();
            vnames.add("Choose Product From The Store To Sell");
            while (rs.next()) {
                namefound = rs.getString("Mname");
                vnames.add(namefound);
            }
            //String[] optionsnames = {""};
            boxdrugname = new JComboBox<String>(vnames);
            rs.close();
            prs.close();
            con.close();
        } catch (SQLException x) {
            JOptionPane.showMessageDialog(null, "Database Connection Failure\nPlease Rerun the application", "Error Message", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    //method for search
    private void searchstore2() {
        if (tsearch.getText().equalsIgnoreCase("") || tsearch.getText().equalsIgnoreCase(null)) {
            JOptionPane.showMessageDialog(null, "Please Give The Name/Serial_NO Of The Product For Search To Sell", "Sell Message", JOptionPane.INFORMATION_MESSAGE);
        } else {
            try {
                Connection con = DBConnector.getConnection();
                stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                //String sql = "SELECT Mname,Msection,Mprescription,Mcost FROM store WHERE Mname = '" + tsearch.getText() + "' ";
                String sqldatereached = "SELECT Medate FROM store WHERE Mname = '" + tsearch.getText() + "' || Mserial = '" + tsearch.getText() + "'";
                rs = stmt.executeQuery(sqldatereached);
                if (rs.next()) {
                    //store date in variable
                    Date fetcheddate = rs.getDate("Medate");
                    if (today.after(fetcheddate)) {
                        String[] option = {"Yes", "No"};
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(null, "Sorry Product Can't Be Sold" + "\n" + "Expiry Date:" + " " + fetcheddate, "Expiry Notification", JOptionPane.INFORMATION_MESSAGE);
//                        int selloption = JOptionPane.showOptionDialog(null, "Sorry Medicine Can't Be Sold" + "\n" + "Expiry Date" + " " + fetcheddate + "\n\n" + "Do you want to remove it from store", "Expiry Date", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
//                        if (selloption == 0) {
//                            String sqlremove = "DELETE FROM store WHERE Mname = '" + tsearch.getText() + "' ";
//                            prs = con.prepareStatement(sqlremove);
//                            prs.execute();
//                            if (prs != null) {
//                                JOptionPane.showMessageDialog(null, tsearch.getText() + " " + "Deleted Successful", "Deletion Confirmation", JOptionPane.INFORMATION_MESSAGE, icon2);
//
//                                rs.close();
//                                stmt.close();
//                                con.close();
//
//                                //rerun the method to refresh db
//                                DrugNameFound();
//                                uplaod();
//                            } else {
//                                JOptionPane.showMessageDialog(null, tsearch.getText() + " " + "Deletion Failed", "Error Message", JOptionPane.ERROR_MESSAGE);
//                                rs.close();
//                                prs.close();
//                                con.close();
//
//                                uplaod();
//                            }
//                        } else {
//                            JOptionPane.showMessageDialog(null, "Successfully Cancelled ", "Expiring Date", JOptionPane.ERROR_MESSAGE);
//                            rs.close();
//                            stmt.close();
//                            con.close();
//                        }
                    } else {
                        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        //String sql = "SELECT Mname,Msection,Mprescription,Mcost FROM store WHERE Mname = '" + tsearch.getText() + "' ";
                        String sqlcool = "SELECT Mname,Msection,Mquantity,Mprescription,Mcost,Mreport FROM store WHERE Mname = '" + tsearch.getText() + "' || Mserial = '" + tsearch.getText() + "'";
                        rs = stmt.executeQuery(sqlcool);
                        if (rs.next()) {
                            String mname = rs.getString("Mname");
                            String msection = rs.getString("Msection");
                            String mdescription = rs.getString("Mprescription");
                            costage = rs.getDouble("Mcost");
                            double remainingquantity = rs.getDouble("Mquantity");
                            String aboutdrug = rs.getString("Mreport");
                            if (remainingquantity <= 10) {
                                Toolkit.getDefaultToolkit().beep();
                                JOptionPane.showMessageDialog(null, "The Stock Is Getting Finished" + "\n" + "Only" + " " + remainingquantity + " " + "Items remaining In The Store", "Stock Notification", JOptionPane.INFORMATION_MESSAGE);

                                //setting values to fields
                                tname.setText(mname);
                                targmant.setText(msection);
                                tprescription.setText(mdescription);
                                tcost.setText(String.format("%.2f", costage));
                                treport.setText(aboutdrug);
                                tquantity.setText(null);
                                ttotalcost.setText(null);
                                tcashpaid.setText(null);

                                searchengineuploadtext();

                                rs.close();
                                stmt.close();
                                con.close();
                            } else {
                                //setting values to fields
                                tname.setText(mname);
                                targmant.setText(msection);
                                tprescription.setText(mdescription);
                                tcost.setText(String.format("%.2f", costage));
                                treport.setText(aboutdrug);
                                tquantity.setText(null);
                                ttotalcost.setText(null);
                                tcashpaid.setText(null);

                                searchengineuploadtext();

                                rs.close();
                                stmt.close();
                                con.close();
                            }
                        } else {
                            Toolkit.getDefaultToolkit().beep();
                            JOptionPane.showMessageDialog(null, "No such Product in the store", "Store Notification", JOptionPane.INFORMATION_MESSAGE);
                            rs.close();
                            stmt.close();
                            con.close();
                        }
                    }
                } else {
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(null, "No such Product in the store", "Store Notification", JOptionPane.INFORMATION_MESSAGE);
                    rs.close();
                    stmt.close();
                    con.close();
                }
            } catch (SQLException e) {
                xamppfailure.getCon();
            }
        }
    }

    private void searchstore() {
        //String searchingvalue = tsearch.getText();
        try {
            Connection con = DBConnector.getConnection();
            if (con != null) {
                if (drugname.equalsIgnoreCase("Choose Product From The Store To Sell")) {
                    JOptionPane.showMessageDialog(null, "Invalid Choice", "Notification", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    //String sql = "SELECT Mname,Msection,Mprescription,Mcost FROM store WHERE Mname = '" + tsearch.getText() + "' ";
                    String sqldatereached = "SELECT Medate FROM store WHERE Mname = '" + drugname + "' ";
                    rs = stmt.executeQuery(sqldatereached);
                    rs.next();
                    //store date in variable
                    Date fetcheddate = rs.getDate("Medate");
                    if (today.after(fetcheddate)) {
                        String[] option = {"Yes", "No"};
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(null, "Sorry Product Can't Be Sold" + "\n" + "Expiry Date:" + " " + fetcheddate, "Expiry Notification", JOptionPane.INFORMATION_MESSAGE);
//                        int selloption = JOptionPane.showOptionDialog(null, "Sorry Medicine Can't Be Sold" + "\n" + "Expiry Date" + " " + fetcheddate + "\n\n" + "Do you want to remove it from store", "Expiry Date", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
//                        if (selloption == 0) {
//                            String sqlremove = "DELETE FROM store WHERE Mname = '" + drugname + "' ";
//                            prs = con.prepareStatement(sqlremove);
//                            prs.execute();
//                            if (prs != null) {
//                                JOptionPane.showMessageDialog(null, drugname + " " + "Deleted Successful", "Deletion Confirmation", JOptionPane.INFORMATION_MESSAGE, icon2);
//
//                                rs.close();
//                                stmt.close();
//                                con.close();
//                                //rerun the method to refresh db
//                                DrugNameFound();
//                                uplaod();
//                            } else {
//                                JOptionPane.showMessageDialog(null, drugname + " " + "Deletion Failed", "Error Message", JOptionPane.ERROR_MESSAGE);
//                                rs.close();
//                                prs.close();
//                                con.close();
//                                uplaod();
//                            }
//                        } else {
//                            JOptionPane.showMessageDialog(null, "Successfully Cancelled ", "Expiring Date", JOptionPane.ERROR_MESSAGE);
//                            rs.close();
//                            stmt.close();
//                            con.close();
//                        }
                    } else {
                        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        //String sql = "SELECT Mname,Msection,Mprescription,Mcost FROM store WHERE Mname = '" + tsearch.getText() + "' ";
                        String sql = "SELECT Mname,Msection,Mquantity,Mprescription,Mcost,Mreport FROM store WHERE Mname = '" + drugname + "' ";
                        rs = stmt.executeQuery(sql);
                        if (rs.next()) {
                            String mname = rs.getString("Mname");
                            String msection = rs.getString("Msection");
                            String mdescription = rs.getString("Mprescription");
                            costage = rs.getDouble("Mcost");
                            double remainingquantity = rs.getDouble("Mquantity");
                            String aboutdrug = rs.getString("Mreport");
                            if (remainingquantity <= 10) {
                                Toolkit.getDefaultToolkit().beep();
                                JOptionPane.showMessageDialog(null, "The Stock Is Getting Finished" + "\n" + "Only" + " " + remainingquantity + " " + "Items remaining In The Store", "Stock Notification", JOptionPane.INFORMATION_MESSAGE);

                                //setting values to fields
                                tname.setText(mname);
                                targmant.setText(msection);
                                tprescription.setText(mdescription);
                                tcost.setText(String.format("%.2f", costage));
                                treport.setText(aboutdrug);
                                tquantity.setText(null);
                                ttotalcost.setText(null);
                                tcashpaid.setText(null);

                                searchengineupload();

                                rs.close();
                                stmt.close();
                                con.close();
                            } else {
                                //setting values to fields
                                tname.setText(mname);
                                targmant.setText(msection);
                                tprescription.setText(mdescription);
                                tcost.setText(String.format("%.2f", costage));
                                treport.setText(aboutdrug);
                                tquantity.setText(null);
                                ttotalcost.setText(null);
                                tcashpaid.setText(null);

                                searchengineupload();

                                rs.close();
                                stmt.close();
                                con.close();
                            }
                        } else {
                            Toolkit.getDefaultToolkit().beep();
                            JOptionPane.showMessageDialog(null, "No such Product in the store", "Store Notification", JOptionPane.INFORMATION_MESSAGE);
                            rs.close();
                            stmt.close();
                            con.close();
                        }
                    }
                }
            } else {
                xamppfailure.getCon();
            }
        } catch (SQLException x) {
            xamppfailure.getCon();
        }
    }

    //method for selling items for store
    private void sellingcart() {
        String finalcost = ttotalcost.getText();
        String getpayed = tcashpaid.getText();
        double dpayed = Double.parseDouble(getpayed);
        //double costfinal = Double.parseDouble(finalcost);
        if (dpayed < total) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "Please Enter Extra Cash", "Cash Message", JOptionPane.INFORMATION_MESSAGE);
        } else {
            if (finalcost.equalsIgnoreCase("")) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "No Total Cost Has Been Given Re-Enter quantity", "Cost Message", JOptionPane.INFORMATION_MESSAGE);
            } else {
                try {
                    Connection con = DBConnector.getConnection();
//                    stmt = (Statement) con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    String sqlqsum = "SELECT Mquantity FROM store WHERE Mname = '" + drugname + "' || Mname = '" + tsearch.getText() + "'|| Mserial = '" + tsearch.getText() + "' ";
                    prs = con.prepareStatement(sqlqsum);
                    rs = prs.executeQuery();
                    if (rs.next()) {
                        double quantityfound = rs.getDouble("Mquantity");
                        if (quantityfound <= 0) {
                            Toolkit.getDefaultToolkit().beep();
                            JOptionPane.showMessageDialog(null, "Sorry The " + drugname + tsearch.getText() + " " + "Stock Is Finished Please Re-order", "Error Message", JOptionPane.ERROR_MESSAGE);
                            double setcost = 0.0;
                            String updatecost = "UPDATE store set Mcost = '" + setcost + "' WHERE Mname = '" + drugname + "' || Mname = '" + tsearch.getText() + "' || Mserial = '" + tsearch.getText() + "'";
                            prs = con.prepareStatement(updatecost);
                            prs.execute();

                            rs.close();
                            prs.close();
                            con.close();
                        } else {
//do nothing
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Sorry System Unable To Fetch Try Again", "Error Message", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException x) {
                    xamppfailure.getCon();

                }

                try {
                    Connection con = DBConnector.getConnection();
                    String sqlcsum = "SELECT Mserial,Mquantity,Mtotalcost FROM store WHERE Mname = '" + drugname + "' || Mname = '" + tsearch.getText() + "'|| Mserial = '" + tsearch.getText() + "'";
                    prs = con.prepareStatement(sqlcsum);
                    rs = prs.executeQuery();
                    if (rs.next()) {
                        String globalserial = rs.getString("Mserial");
                        double tcostfound = rs.getDouble("Mtotalcost");
                        double quantityfound = rs.getDouble("Mquantity");
                        if (quantityfound <= 0) {
                            //say nothing
                        } else {
                            String[] option = {"Yes", "No"};
                            int selloption = JOptionPane.showOptionDialog(null, "Proceed in Selling" + " " + quaty + " " + drugname + tsearch.getText() + " " + "at" + " " + String.format("%.2f", total) + "/=", "Sell Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
                            if (selloption == 0) {
                                double soldcost = tcostfound - total;
                                double soldvalue = 0;
                                if (tcostfound >= 1 && quantityfound >= 1) {
                                    String updatecost = "UPDATE store set Mtotalcost = '" + soldcost + "' WHERE Mname = '" + drugname + "' || Mname = '" + tsearch.getText() + "'|| Mserial = '" + tsearch.getText() + "'";
                                    prs = con.prepareStatement(updatecost);
                                    prs.execute();

                                    rs.close();
                                    prs.close();
                                    con.close();

                                    con = DBConnector.getConnection();
                                    double soldquantity = quantityfound - quaty;
                                    String updatequantity = "UPDATE store set Mquantity = '" + soldquantity + "' WHERE Mname = '" + drugname + "' || Mname = '" + tsearch.getText() + "'|| Mserial = '" + tsearch.getText() + "'";
                                    prs = con.prepareStatement(updatequantity);
                                    prs.execute();
                                    JOptionPane.showMessageDialog(null, drugname + tsearch.getText() + " " + "Sold Successfully", "Sell Confirmation", JOptionPane.INFORMATION_MESSAGE, icon);

                                    rs.close();
                                    prs.close();
                                    con.close();

                                    printingreceiet();
                                    refreshadmin();

                                    tchangeprint.setText("");
                                    tcash.setText("");
                                    ttotal.setText("");
                                    tdateprint.setText("");
                                    tcprint.setText("");
                                    titems.setText("");
                                    tnameprint.setText("");
                                    tserialprint.setText("");
                                } else {
                                    String updatecost = "UPDATE store set Mtotalcost = '" + soldvalue + "' WHERE Mname = '" + drugname + "' || Mname = '" + tsearch.getText() + "'|| Mserial = '" + tsearch.getText() + "'";
                                    prs = con.prepareStatement(updatecost);
                                    prs.execute();

                                    rs.close();
                                    prs.close();
                                    con.close();

                                    con = DBConnector.getConnection();
                                    double soldquantity = quantityfound - quaty;
                                    String updatequantity = "UPDATE store set Mquantity = '" + soldvalue + "' WHERE Mname = '" + drugname + "' || Mname = '" + tsearch.getText() + "'|| Mserial = '" + tsearch.getText() + "'";
                                    prs = con.prepareStatement(updatequantity);
                                    prs.execute();
                                    Toolkit.getDefaultToolkit().beep();
                                    JOptionPane.showMessageDialog(null, "Sorry Only " + " " + quantityfound + " " + "Items Remaining In Stock You Can'T Sell Extra", "Stock Message", JOptionPane.INFORMATION_MESSAGE);

                                    rs.close();
                                    prs.close();
                                    con.close();
                                }

                                //code for adding sold items to tablesell
                                con = DBConnector.getConnection();
                                String sqladd = "INSERT INTO tablesell(Mname,Mserial,Bcost,Mcost,Mtotalquantity,Mtotalcost,CashPaid,ChangePaid,Msolddate,LastEdited,Invoice) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
                                prs = con.prepareStatement(sqladd);

                                //setting to database
                                prs.setString(1, tname.getText());
                                prs.setString(2, globalserial);
                                prs.setDouble(3, costage);
                                prs.setDouble(4, cost2);
                                prs.setDouble(5, quaty);
                                prs.setDouble(6, total);
                                prs.setDouble(7, Double.parseDouble(tcashpaid.getText()));
                                prs.setDouble(8, Double.parseDouble(tchange.getText()));
                                prs.setString(9, format);
                                prs.setString(10, fileeditedlast);
                                prs.setString(11, String.valueOf(numserial));

                                prs.execute();

                                rs.close();
                                prs.close();
                                con.close();

                                tname.setText("");
                                targmant.setText("");
                                tprescription.setText("");
                                treport.setText("");
                                tcost.setText("");
                                tquantity.setText("");
                                ttotalcost.setText("");
                                tcashpaid.setText("");
                                tchange.setText("");
//                            String sqlverify = "SELECT Msolddate,Mtotalquantity,Mtotalcost FROM tablesell WHERE Mname = '" + tname.getText() + "'";
//                            prs = con.prepareStatement(sqlverify);
//                            rs = prs.executeQuery();
//                            if (rs.next()) {
//                                Date solddate = rs.getDate("Msolddate");
//                                double tcostsold = rs.getDouble("Mtotalcost");
//                                double quantitysold = rs.getDouble("Mtotalquantity");
//                                if (today.equals(solddate)) {
//                                    String sqladd = "INSERT INTO tablesell(Mname,Mserial,Mcost,Mtotalquantity,Mtotalcost,Msolddate,LastEdited) VALUES (?,?,?,?,?,?,?)";
//                                    prs = con.prepareStatement(sqladd);
//
//                                    //setting to database
//                                    prs.setString(1, tname.getText());
//                                    prs.setString(2, globalserial);
//                                    prs.setDouble(3, cost2);
//                                    prs.setDouble(4, quaty);
//                                    prs.setDouble(4, total);
//                                    prs.setString(5, format);
//                                    prs.setString(6, fileeditedlast);
//
//                                    prs.execute();
//
//                                    rs.close();
//                                    prs.close();
//                                    con.close();
//
//                                    tname.setText("");
//                                    targmant.setText("");
//                                    tprescription.setText("");
//                                    treport.setText("");
//                                    tcost.setText("");
//                                    tquantity.setText("");
//                                    ttotalcost.setText("");
//                                } else {
//                                    double finalsoldcost = tcostsold + total;
//                                    double finalsoldquantity = quantitysold + quaty;
//                                    String updatetablesell = "UPDATE tablesell set Mtotalcost  = '" + finalsoldcost + "',Mtotalquantity  = '" + finalsoldquantity + "' WHERE Mserial = '" + globalserial + "'";
//                                    prs = con.prepareStatement(updatetablesell);
//
//                                    prs.execute();
//                                    rs.close();
//                                    prs.close();
//                                    con.close();
//
//                                    tname.setText("");
//                                    targmant.setText("");
//                                    tprescription.setText("");
//                                    treport.setText("");
//                                    tcost.setText("");
//                                    tquantity.setText("");
//                                    ttotalcost.setText("");
//                                }
//                            } else {
//                                String sqladd1 = "INSERT INTO tablesell(Mname,Mserial,Mcost,Mtotalquantity,Mtotalcost,Msolddate,LastEdited) VALUES (?,?,?,?,?,?,?)";
//                                prs = con.prepareStatement(sqladd1);
//
//                                //setting to database
//                                prs.setString(1, tname.getText());
//                                prs.setString(2, globalserial);
//                                prs.setDouble(3, cost2);
//                                prs.setDouble(4, quaty);
//                                prs.setDouble(4, total);
//                                prs.setString(5, format);
//                                prs.setString(6, fileeditedlast);
//
//                                prs.execute();
//                                rs.close();
//                                prs.close();
//                                con.close();
//
//                                tname.setText("");
//                                targmant.setText("");
//                                tprescription.setText("");
//                                treport.setText("");
//                                tcost.setText("");
//                                tquantity.setText("");
//                                ttotalcost.setText("");
//                            }
                                //end of code of adding
                            } else {
                                JOptionPane.showMessageDialog(null, "Order For" + " " + drugname + " " + "Cancelled.", "Order Message", JOptionPane.INFORMATION_MESSAGE);
                                JOptionPane.showMessageDialog(null, "Order For" + " " + tsearch.getText() + " " + "Cancelled.", "Order Message", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    } else {
                        //JOptionPane.showMessageDialog(null, "Sorry System Unable To Fetch Try Again", "Error Message", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException x) {
                    xamppfailure.getCon();
                }
            }
        }
    }

    //method for selling items for store
    private void sellingcartuser() {
        String finalcost = ttotalcost.getText();
        String getpayed = tcashpaid.getText();
        double dpayed = Double.parseDouble(getpayed);
        //double costfinal = Double.parseDouble(finalcost);
        if (dpayed < total) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "Please Enter Extra Cash", "Cash Message", JOptionPane.INFORMATION_MESSAGE);
        } else {
            if (finalcost.equalsIgnoreCase("")) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "No Total Cost Has Been Given Re-Enter quantity", "Cost Message", JOptionPane.INFORMATION_MESSAGE);
            } else {
                try {
                    Connection con = DBConnector.getConnection();
//                    stmt = (Statement) con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    String sqlqsum = "SELECT Mquantity FROM store WHERE Mname = '" + drugname + "' || Mname = '" + tsearch.getText() + "'|| Mserial = '" + tsearch.getText() + "' ";
                    prs = con.prepareStatement(sqlqsum);
                    rs = prs.executeQuery();
                    if (rs.next()) {
                        double quantityfound = rs.getDouble("Mquantity");
                        if (quantityfound <= 0) {
                            Toolkit.getDefaultToolkit().beep();
                            JOptionPane.showMessageDialog(null, "Sorry The " + drugname + tsearch.getText() + " " + "Stock Is Finished Please Re-order", "Error Message", JOptionPane.ERROR_MESSAGE);
                            double setcost = 0.0;
                            String updatecost = "UPDATE store set Mcost = '" + setcost + "' WHERE Mname = '" + drugname + "' || Mname = '" + tsearch.getText() + "' || Mserial = '" + tsearch.getText() + "'";
                            prs = con.prepareStatement(updatecost);
                            prs.execute();

                            rs.close();
                            prs.close();
                            con.close();
                        } else {
//do nothning
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Sorry System Unable To Fetch Try Again", "Error Message", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException x) {
                    xamppfailure.getCon();
                    //System.exit(0);
                }
                //for getting total cost
                try {
                    Connection con = DBConnector.getConnection();
//                    stmt = (Statement) con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    String sqlcsumk = "SELECT Mserial,Mquantity,Mtotalcost FROM store WHERE Mname = '" + drugname + "' || Mname = '" + tsearch.getText() + "'|| Mserial = '" + tsearch.getText() + "'";
                    prs = con.prepareStatement(sqlcsumk);
                    rs = prs.executeQuery();
                    if (rs.next()) {
                        String globalserial = rs.getString("Mserial");
                        double tcostfound = rs.getDouble("Mtotalcost");
                        double quantityfound = rs.getDouble("Mquantity");
                        if (quantityfound <= 0) {
                            //say nothing
                        } else {
                            String[] option = {"Yes", "No"};
                            int selloption = JOptionPane.showOptionDialog(null, "Proceed in Selling" + " " + quaty + " " + drugname + tsearch.getText() + " " + "at" + " " + String.format("%.2f", total) + "/=", "Sell Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
                            if (selloption == 0) {
                                double soldcost = tcostfound - total;
                                double soldvalue = 0;
                                if (tcostfound >= 1 && quantityfound >= 1) {
                                    String updatecost = "UPDATE store set Mtotalcost = '" + soldcost + "' WHERE Mname = '" + drugname + "' || Mname = '" + tsearch.getText() + "'|| Mserial = '" + tsearch.getText() + "'";
                                    prs = con.prepareStatement(updatecost);
                                    prs.execute();

                                    rs.close();
                                    prs.close();
                                    con.close();

                                    con = DBConnector.getConnection();
                                    double soldquantity = quantityfound - quaty;
                                    String updatequantity = "UPDATE store set Mquantity = '" + soldquantity + "' WHERE Mname = '" + drugname + "' || Mname = '" + tsearch.getText() + "'|| Mserial = '" + tsearch.getText() + "'";
                                    prs = con.prepareStatement(updatequantity);
                                    prs.execute();
                                    JOptionPane.showMessageDialog(null, drugname + tsearch.getText() + " " + "Sold Successfully", "Sell Confirmation", JOptionPane.INFORMATION_MESSAGE, icon);

                                    rs.close();
                                    prs.close();
                                    con.close();

                                    printingreceiet();
                                    refreshuser();

                                    tchangeprint.setText("");
                                    tcash.setText("");
                                    ttotal.setText("");
                                    tdateprint.setText("");
                                    tcprint.setText("");
                                    titems.setText("");
                                    tnameprint.setText("");
                                    tserialprint.setText("");
                                } else {
                                    String updatecost = "UPDATE store set Mtotalcost = '" + soldvalue + "' WHERE Mname = '" + drugname + "' || Mname = '" + tsearch.getText() + "'|| Mserial = '" + tsearch.getText() + "'";
                                    prs = con.prepareStatement(updatecost);
                                    prs.execute();

                                    rs.close();
                                    prs.close();
                                    con.close();

                                    con = DBConnector.getConnection();
                                    double soldquantity = quantityfound - quaty;
                                    String updatequantity = "UPDATE store set Mquantity = '" + soldvalue + "' WHERE Mname = '" + drugname + "' || Mname = '" + tsearch.getText() + "'|| Mserial = '" + tsearch.getText() + "'";
                                    prs = con.prepareStatement(updatequantity);
                                    prs.execute();
                                    Toolkit.getDefaultToolkit().beep();
                                    JOptionPane.showMessageDialog(null, "Sorry Only " + " " + quantityfound + " " + "Items Remaining In Stock You Can'T Sell Extra", "Stock Message", JOptionPane.INFORMATION_MESSAGE);

                                    rs.close();
                                    prs.close();
                                    con.close();
                                }

                                //code for adding sold items to tablesell
                                con = DBConnector.getConnection();
                                String sqladd = "INSERT INTO tablesell(Mname,Mserial,Bcost,Mcost,Mtotalquantity,Mtotalcost,CashPaid,ChangePaid,Msolddate,LastEdited,Invoice) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
                                prs = con.prepareStatement(sqladd);

                                //setting to database
                                prs.setString(1, tname.getText());
                                prs.setString(2, globalserial);
                                prs.setDouble(3, costage);
                                prs.setDouble(4, cost2);
                                prs.setDouble(5, quaty);
                                prs.setDouble(6, total);
                                prs.setDouble(7, Double.parseDouble(tcashpaid.getText()));
                                prs.setDouble(8, Double.parseDouble(tchange.getText()));
                                prs.setString(9, format);
                                prs.setString(10, fileeditedlast);
                                prs.setString(11, String.valueOf(numserial));

                                prs.execute();

                                rs.close();
                                prs.close();
                                con.close();

                                tname.setText("");
                                targmant.setText("");
                                tprescription.setText("");
                                treport.setText("");
                                tcost.setText("");
                                tquantity.setText("");
                                ttotalcost.setText("");
                                tcashpaid.setText("");
                                tchange.setText("");
//                            String sqlverify = "SELECT Msolddate,Mtotalquantity,Mtotalcost FROM tablesell WHERE Mname = '" + tname.getText() + "'";
//                            prs = con.prepareStatement(sqlverify);
//                            rs = prs.executeQuery();
//                            if (rs.next()) {
//                                Date solddate = rs.getDate("Msolddate");
//                                double tcostsold = rs.getDouble("Mtotalcost");
//                                double quantitysold = rs.getDouble("Mtotalquantity");
//                                if (today.equals(solddate)) {
//                                    String sqladd = "INSERT INTO tablesell(Mname,Mserial,Mcost,Mtotalquantity,Mtotalcost,Msolddate,LastEdited) VALUES (?,?,?,?,?,?,?)";
//                                    prs = con.prepareStatement(sqladd);
//
//                                    //setting to database
//                                    prs.setString(1, tname.getText());
//                                    prs.setString(2, globalserial);
//                                    prs.setDouble(3, cost2);
//                                    prs.setDouble(4, quaty);
//                                    prs.setDouble(4, total);
//                                    prs.setString(5, format);
//                                    prs.setString(6, fileeditedlast);
//
//                                    prs.execute();
//
//                                    rs.close();
//                                    prs.close();
//                                    con.close();
//
//                                    tname.setText("");
//                                    targmant.setText("");
//                                    tprescription.setText("");
//                                    treport.setText("");
//                                    tcost.setText("");
//                                    tquantity.setText("");
//                                    ttotalcost.setText("");
//                                } else {
//                                    double finalsoldcost = tcostsold + total;
//                                    double finalsoldquantity = quantitysold + quaty;
//                                    String updatetablesell = "UPDATE tablesell set Mtotalcost  = '" + finalsoldcost + "',Mtotalquantity  = '" + finalsoldquantity + "' WHERE Mserial = '" + globalserial + "'";
//                                    prs = con.prepareStatement(updatetablesell);
//
//                                    prs.execute();
//                                    rs.close();
//                                    prs.close();
//                                    con.close();
//
//                                    tname.setText("");
//                                    targmant.setText("");
//                                    tprescription.setText("");
//                                    treport.setText("");
//                                    tcost.setText("");
//                                    tquantity.setText("");
//                                    ttotalcost.setText("");
//                                }
//                            } else {
//                                String sqladd1 = "INSERT INTO tablesell(Mname,Mserial,Mcost,Mtotalquantity,Mtotalcost,Msolddate,LastEdited) VALUES (?,?,?,?,?,?,?)";
//                                prs = con.prepareStatement(sqladd1);
//
//                                //setting to database
//                                prs.setString(1, tname.getText());
//                                prs.setString(2, globalserial);
//                                prs.setDouble(3, cost2);
//                                prs.setDouble(4, quaty);
//                                prs.setDouble(4, total);
//                                prs.setString(5, format);
//                                prs.setString(6, fileeditedlast);
//
//                                prs.execute();
//                                rs.close();
//                                prs.close();
//                                con.close();
//
//                                tname.setText("");
//                                targmant.setText("");
//                                tprescription.setText("");
//                                treport.setText("");
//                                tcost.setText("");
//                                tquantity.setText("");
//                                ttotalcost.setText("");
//                            }
                                //end of code of adding
                            } else {
                                JOptionPane.showMessageDialog(null, "Order For" + " " + drugname + " " + "Cancelled.", "Order Message", JOptionPane.INFORMATION_MESSAGE);
                                JOptionPane.showMessageDialog(null, "Order For" + " " + tsearch.getText() + " " + "Cancelled.", "Order Message", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    } else {
                        //JOptionPane.showMessageDialog(null, "Sorry System Unable To Fetch Try Again", "Error Message", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException x) {
                    xamppfailure.getCon();
                }
            }
        }
    }

    //method for uploadin data in system
    private void uplaod() {
        DefaultTableModel pharmacymodel = new DefaultTableModel();
        //dm.addColumn("Medicine Name");
        pharmacymodel.setColumnIdentifiers(values);
        String fetchrecord = "SELECT * FROM store";
        try {
            Connection con = DBConnector.getConnection();
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(fetchrecord);
            if (rs.next()) {
                do {
                    String drugname = rs.getString("Mname");
                    String drugserial = rs.getString("Mserial");
                    double druginstock = rs.getDouble("Mquantity");
                    String sinstock = String.format("%.2f", druginstock);
                    double drugincost = rs.getDouble("Mcost");
                    String sincost = String.format("%.2f", drugincost);
                    String drugeditedon = rs.getString("Msection");

                    pharmacymodel.addRow(new String[]{drugname, drugserial, sinstock, sincost, drugeditedon});

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
                JOptionPane.showMessageDialog(null, "No Stock In The Store For Sell", "Store Message", JOptionPane.ERROR_MESSAGE);
                rs.close();
                stmt.close();
                con.close();
            }
        } catch (SQLException x) {
            xamppfailure.getCon();
        }
    }

    public void SellDrug() {
        table = new JTable();
        // this enables horizontal scroll bar
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFillsViewportHeight(true);
        //table.setAutoCreateRowSorter(true);
        table.setIntercellSpacing(new Dimension(20, 20));
        // table.setPreferredScrollableViewportSize(new Dimension(935, 570));
        table.setPreferredScrollableViewportSize(new Dimension((int) (screenSize.width / 1.25), (int) (screenSize.height / 2.70)));
        table.revalidate();
        table.setFont(new Font("Tahoma", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.revalidate();

        lselllogo = new JLabel(imageselllogo);
        ltitle = new JLabel("THE SELLING POINT");
        ltitle.setFont(new Font("Tahoma", Font.BOLD, 22));
        lname = new JLabel("Product Name");
        lname.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lquantity = new JLabel("Quantity[Numbers]");
        lquantity.setFont(new Font("Tahoma", Font.PLAIN, 12));
        ltotalcost = new JLabel("Total Cost [KSH]");
        ltotalcost.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lprescription = new JLabel("Prescription");
        lprescription.setFont(new Font("Tahoma", Font.PLAIN, 12));
        largment = new JLabel("Place Of Arrangement");
        largment.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lcost = new JLabel("Cost Per Item[KSH]");
        lcost.setFont(new Font("Tahoma", Font.PLAIN, 12));
        labout = new JLabel("About The Product");
        labout.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lcashpaid = new JLabel("Cash Paid [KSH]");
        lcashpaid.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lchange = new JLabel("Change [KSH]");
        lchange.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lsellpoint = new JLabel("SELLING POINT");
        lsellpoint.setFont(new Font("Tahoma", Font.BOLD, 13));

        treport = new JTextArea(4, 23);
        treport.setLineWrap(true);
        DefaultCaret caret = (DefaultCaret) treport.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scrollpane1 = new JScrollPane();
        scrollpane1.add(treport);
        scrollpane1.setViewportView(treport);
        treport.setWrapStyleWord(true);
        treport.setEditable(false);
        treport.setBackground(Color.LIGHT_GRAY);
        treport.setFont(new Font("Tahoma", Font.PLAIN, 13));

        tcashpaid = new JTextField(20);
        tcashpaid.setFont(new Font("Tahoma", Font.PLAIN, 15));
        tchange = new JTextField(20);
        tchange.setBackground(Color.LIGHT_GRAY);
        tchange.setFont(new Font("Tahoma", Font.PLAIN, 15));
        tchange.setEditable(false);
        tcost = new JTextField(20);
        tcost.setFont(new Font("Tahoma", Font.PLAIN, 15));
        //tcost.setEditable(false);
        // tcost.setBackground(Color.LIGHT_GRAY);
        ttotalcost = new JTextField(20);
        ttotalcost.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ttotalcost.setEditable(false);
        ttotalcost.setBackground(Color.LIGHT_GRAY);
        tname = new JTextField(20);
        tname.setFont(new Font("Tahoma", Font.PLAIN, 15));
        tname.setEditable(false);
        tname.setBackground(Color.LIGHT_GRAY);
        tquantity = new JTextField(20);
        tquantity.setFont(new Font("Tahoma", Font.PLAIN, 15));
        tprescription = new JTextField(20);
        tprescription.setFont(new Font("Tahoma", Font.PLAIN, 15));
        tprescription.setEditable(false);
        tprescription.setBackground(Color.LIGHT_GRAY);
        targmant = new JTextField(20);
        targmant.setFont(new Font("Tahoma", Font.PLAIN, 15));
        targmant.setEditable(false);
        targmant.setBackground(Color.LIGHT_GRAY);
        tsearch = new JTextField(21);
        tsearch.setFont(new Font("Tahoma", Font.PLAIN, 15));
        tsearch.setToolTipText("Search Product By Name For Sell");

        //combobox for names
        boxdrugname.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boxdrugname.setToolTipText("Search Product By Name");
        boxdrugname.setFont(new Font("Tahoma", Font.PLAIN, 12));
        //method
        boxdrugname.addActionListener(event -> {
            JComboBox<String> boxdrugname = (JComboBox<String>) event.getSource();
            drugname = (String) boxdrugname.getSelectedItem();
            searchstore();
        });
        //end of combobox

        bsearch = new JButton(imagesearch);
        bsearch.setBackground(Color.LIGHT_GRAY);
        bsearch.setToolTipText("Search Product");
        bsearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bsell = new JButton(imagesellbtn);
        bsell.setBackground(Color.GREEN);
        bsell.setToolTipText("Sell Medicine");
        bsell.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bselluser = new JButton(imagesellbtn);
        bselluser.setBackground(Color.GREEN);
        bselluser.setToolTipText("Sell Medicine");
        bselluser.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bcancel = new JButton(imagecancel);
        bcancel.setBackground(Color.red);
        bcancel.setToolTipText("Cancel/Reload");
        bcancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bback = new JButton("BACK");
        bback.setFont(new Font("Tahoma", Font.BOLD, 15));
        bback.setBackground(Color.LIGHT_GRAY);
        bback.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bbacksell = new JButton("LOGOUT");
        bbacksell.setFont(new Font("Tahoma", Font.BOLD, 15));
        bbacksell.setBackground(Color.LIGHT_GRAY);
        bbacksell.setCursor(new Cursor(Cursor.HAND_CURSOR));
        brefresh = new JButton("REFRESH");
        brefresh.setFont(new Font("Serif", Font.BOLD + Font.ITALIC, 15));
        brefresh.setBackground(Color.GREEN);
        brefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        brefreshadmin = new JButton("REFRESH");
        brefreshadmin.setFont(new Font("Serif", Font.BOLD + Font.ITALIC, 15));
        brefreshadmin.setBackground(Color.GREEN);
        brefreshadmin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        //adding components to paneltable
        GridBagConstraints v = new GridBagConstraints();
        v.anchor = GridBagConstraints.WEST;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        paneltable.add(bbacksell, v);
        paneltable.add(bback, v);
//        v.insets = new Insets(0, 100, 0, 0);
//        paneltable.add(brefreshadmin, v);
//        paneltable.add(brefresh, v);
        v.anchor = GridBagConstraints.EAST;
        v.insets = new Insets(0, 0, 0, 340);
        paneltable.add(boxdrugname, v);
        v.insets = new Insets(0, 110, 0, 53);
        paneltable.add(tsearch, v);
        v.insets = new Insets(0, 0, 0, 0);
        paneltable.add(bsearch, v);
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 0, 0);
        v.gridy++;
        paneltable.add(scrollPane, v);
        paneltable.setBorder(new TitledBorder("Store Product(s) Details"));
        paneltable.revalidate();

        //sellpanel
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        panelproduct.add(lname, v);
        v.gridy++;
        panelproduct.add(tname, v);
        v.gridy++;
        panelproduct.add(largment, v);
        v.gridy++;
        panelproduct.add(targmant, v);
        v.gridy++;
        panelproduct.add(lprescription, v);
        v.gridy++;
        panelproduct.add(tprescription, v);
        v.gridy++;
        panelproduct.add(labout, v);
        v.gridy++;
        panelproduct.add(scrollpane1, v);
        panelproduct.setBorder(new TitledBorder("Product Details"));

        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 3;
        v.gridy = 0;
        panelcost.add(lcost, v);
        v.gridy++;
        panelcost.add(tcost, v);
        v.gridy++;
        panelcost.add(lquantity, v);
        v.gridy++;
        panelcost.add(tquantity, v);
        v.gridy++;
        panelcost.add(ltotalcost, v);
        v.gridy++;
        panelcost.add(ttotalcost, v);
        v.gridy++;
        panelcost.add(lcashpaid, v);
        v.gridy++;
        panelcost.add(tcashpaid, v);
        v.gridy++;
        panelcost.add(lchange, v);
        v.gridy++;
        panelcost.add(tchange, v);
        panelcost.setBorder(new TitledBorder("Costage"));

        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 50, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 5;
        v.gridy = 0;
        panelsell.add(lselllogo, v);
        v.insets = new Insets(0, 0, 10, 0);
        v.gridy++;
        panelsell.add(lsellpoint, v);
        v.insets = new Insets(0, 0, 0, 150);
        v.anchor = GridBagConstraints.WEST;
        v.gridy++;
        panelsell.add(bsell, v);
        panelsell.add(bselluser, v);
        v.anchor = GridBagConstraints.EAST;
        v.insets = new Insets(0, 150, 0, 0);
        panelsell.add(bcancel, v);
        panelsell.setBorder(new TitledBorder("Sell Item"));

        //panel sellpanel
        sellpanel.add("West", panelproduct);
        sellpanel.add("Center", panelcost);
        sellpanel.add("East", panelsell);
        sellpanel.setBorder(new TitledBorder(""));

        panelmain.add("Center", paneltable);
        panelmain.add("South", sellpanel);
        panelmain.setBorder(new TitledBorder(""));
        panelmain.setBackground(Color.blue.brighter());
        panelmain.revalidate();

        //action events start
        bsearch.addActionListener(e -> {
            //searchstore();
            searchstore2();
        });

        tquantity.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                fetchData();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                fetchData();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        tcashpaid.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                getChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                getChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        uplaod();
        //action event end

        //setting frame
        Fsell = new JFrame("Pharmacy System");
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("nimbusBase", Color.blue);
        } catch (Exception ignored) {
        }
        Fsell.setUndecorated(true);
        Fsell.setIconImage(iconimage);
        Fsell.add(panelmain);
        Fsell.setVisible(true);
        // Fsell.setSize(950, 600);
        Fsell.setSize(screenSize.width, screenSize.height);
        Fsell.revalidate();
        Fsell.pack();
        Fsell.revalidate();
        Fsell.setLocationRelativeTo(null);
        Fsell.setResizable(true);
        Fsell.revalidate();
        Fsell.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        Fsell.addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                // minimized
                if ((e.getNewState() & Frame.ICONIFIED) == Frame.ICONIFIED) {
                    Fsell.revalidate();
                    Fsell.pack();
                    Fsell.revalidate();
                    Fsell.setLocationRelativeTo(null);
                } // maximized
                else if ((e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                    Fsell.revalidate();
                    Fsell.pack();
                    Fsell.revalidate();
                    Fsell.setLocationRelativeTo(null);
                }
            }
        });

        bsell.addActionListener(e -> {
            sellingcart();
        });
        bselluser.addActionListener(e -> {
            sellingcartuser();
        });
        bcancel.addActionListener(e -> {
            canacelaction();
        });

        bbacksell.addActionListener(e -> {
            String[] option = {"Yes", "No"};
            Toolkit.getDefaultToolkit().beep();
            int selloption = JOptionPane.showOptionDialog(null, "Are you Sure you want to logout", "Logout Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
            if (selloption == 0) {
                Fsell.setVisible(false);
                Login r3 = new Login();
                r3.LoginSection();
            } else {
                JOptionPane.showMessageDialog(null, "Still Logged In Continue With your Work", "Logout Message", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        bback.addActionListener(e -> {
            Fsell.setVisible(false);
            Sections opsec = new Sections();
            opsec.Operations();
        });
//        brefresh.addActionListener(e -> {
//            Fsell.setVisible(false);
//            SellingDrug dsell = new SellingDrug();
//            dsell.DrugNameFound();
//            dsell.SellDrug();
//            dsell.bbacksell.setVisible(true);
//            dsell.brefresh.setVisible(true);
//            dsell.brefreshadmin.setVisible(false);
//            dsell.bback.setVisible(false);
//        });
//        brefreshadmin.addActionListener(e -> {
//            Fsell.setVisible(false);
//            SellingDrug dsell = new SellingDrug();
//            dsell.DrugNameFound();
//            dsell.SellDrug();
//            dsell.bbacksell.setVisible(false);
//            dsell.brefresh.setVisible(false);
//            dsell.brefreshadmin.setVisible(true);
//            dsell.bback.setVisible(true);
//        });
    }

//    public static void main(String[] args) {
//        SellingDrug msell = new SellingDrug();
//        msell.DrugNameFound();
//        msell.SellDrug();
//    }
}
