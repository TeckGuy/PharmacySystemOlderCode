package membership;

import dbconnector.DBConnector;
import login.Sections;
import sun.applet.Main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class Worker {

    //dimension setting
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    //getting images
    URL url1 = Main.class.getResource("/img/logo.png");
    URL url2 = Main.class.getResource("/img/view.png");
    URL url3 = Main.class.getResource("/img/addsection.png");
    URL url4 = Main.class.getResource("/img/search.png");
    URL url5 = Main.class.getResource("/img/addp.png");
    URL url6 = Main.class.getResource("/img/cancel5.png");
    URL url7 = Main.class.getResource("/img/delete.png");
    URL url8 = Main.class.getResource("/img/payments.png");
    URL url9 = Main.class.getResource("/img/updatepay.png");
    URL url10 = Main.class.getResource("/img/payments.png");

    ImageIcon imageviewsection = new ImageIcon(url2);
    ImageIcon imagedelete = new ImageIcon(url7);
    ImageIcon imagesearch = new ImageIcon(url4);
    ImageIcon imageadd = new ImageIcon(url5);
    ImageIcon imageaddsection = new ImageIcon(url3);
    ImageIcon imagecancel = new ImageIcon(url6);
    ImageIcon imagepay = new ImageIcon(url9);
    ImageIcon imagepayments = new ImageIcon(url10);

    //setting ma default image icon to my frames
    final ImageIcon icon2 = new ImageIcon(url8);
    final ImageIcon icon = new ImageIcon(url7);
    Image iconimage = new ImageIcon(url1).getImage();

    JLabel ldeletemember, lremove, lnid, lusername, lpnumber, ltpayment, lpayed, lbalance, lpay, lnidadd,
            lusernameadd, lpnumberadd, lview, ladd, lviewtitle, laddtitle, ltitle, ltotalbalance, llogo1, llogo2, ltotalcost, ltotalpayments, ltpaymentdd;
    JTextField tdeletemember, tnid, tusername, tpnumber, ttpayment, tpayed, tbalance, tpay, tnidadd, tusernameadd, tpnumberadd, tsearch,
            ttotalcost, ttotalpayments, ttotalbalance, ttpaymentdd;
    ;
    JTable table;
    JComboBox<String> choose, boxdeletemember;
    JButton bsearch, bload, bdelete, bpay, badd, bcancel, badd1, bdeletemember, bback;

    JPanel panelmain = new JPanel(new BorderLayout(0, 0));
    JPanel panelcard = new JPanel(new CardLayout(0, 0));
    JPanel paneladd = new JPanel(new GridBagLayout());
    JPanel panelview = new JPanel(new GridBagLayout());
    JPanel paneltable = new JPanel(new GridBagLayout());
    JPanel panelall = new JPanel(new GridBagLayout());

    JFrame Fworker = new JFrame();

    //tabele colums
    String[] values = new String[]{"National ID", "Worker Name", "Personal Number", "Total Salary", "Amount Paid", "Balance", "Last Edited On"};
    double cost2, total, payed, total2;
    String cost, payedamount, workerfound;
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

    //String addtype
    String addtype, workergot;

    //calling class dbconnect
    DBConnector xamppfailure = new DBConnector();

    //method for fetching and updating data on writing on textfield
    private void updatecontent() {
        cost = ttpayment.getText();
        payedamount = tpay.getText();
        cost2 = Double.parseDouble(cost);
        payed = Double.parseDouble(payedamount);
        if (cost.equalsIgnoreCase("") && payedamount.equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(null, "No Payment Of The Worker given", "Error Message", JOptionPane.ERROR_MESSAGE, icon2);
        } else {
            try {
                Connection con = DBConnector.getConnection();
                String serialgot = String.valueOf(tnid.getText());
                String sqlqsumsuch = "SELECT * FROM userlogin WHERE NationalID  = '" + serialgot + "' UNION SELECT * FROM adminlogin WHERE NationalID  = '" + serialgot + "'";
                prs = con.prepareStatement(sqlqsumsuch);
                rs = prs.executeQuery();
                while (rs.next()) {
                    double payedfound = rs.getDouble("Payed");
                    double salaryfound = rs.getDouble("Salary");
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

    //method for getting values from table
    private void tablecontents() {
        int wid = Integer.parseInt(table.getValueAt(table.getSelectedRow(), 0).toString());
        String wname = table.getValueAt(table.getSelectedRow(), 1).toString();
        String wpn = table.getValueAt(table.getSelectedRow(), 2).toString();
        double wsalary = Double.parseDouble(String.valueOf(table.getValueAt(table.getSelectedRow(), 3)));
        double wpayed = Double.parseDouble(String.valueOf(table.getValueAt(table.getSelectedRow(), 4)));
        double wbalance = Double.parseDouble(String.valueOf(table.getValueAt(table.getSelectedRow(), 5)));

        //setting values got from table
        tnid.setText(String.valueOf(wid));
        tusername.setText(wname);
        tpnumber.setText(wpn);
        ttpayment.setText(String.format("%.2f", wsalary));
        tpayed.setText(String.format("%.2f", wpayed));
        tbalance.setText(String.format("%.2f", wbalance));
        tpay.setText(null);
    }

    //method for getting all the payments,balance
    private void all() {
        try {
            Connection con = DBConnector.getConnection();
            //adminlogin
            //getting totalcost
            String stockcostadmin = "SELECT SUM(Salary) TCOST FROM  adminlogin";
            prs = con.prepareStatement(stockcostadmin);
            rs1 = prs.executeQuery();
            rs1.next();
            double storeincostadmin = rs1.getDouble("TCOST");
            //ttotalcost.setText(String.format("%.2f", storeincost));

            //getting totalpayments
            String sqloutadmin = "SELECT SUM(Payed) PSUM FROM adminlogin";
            prs = con.prepareStatement(sqloutadmin);
            rs2 = prs.executeQuery();
            rs2.next();
            double storeoutadmin = rs2.getDouble("PSUM");
            //ttotalpayments.setText(String.format("%.2f", storeout));

            //getting totalbance
            String sqloutcostadmin = "SELECT SUM(Balance) BSUM FROM adminlogin";
            prs = con.prepareStatement(sqloutcostadmin);
            rs3 = prs.executeQuery();
            rs3.next();
            double storecostoutadmin = rs3.getDouble("BSUM");
            //ttotalbalance.setText(String.format("%.2f", storecostout));

            //admin end
            //getting totalcost
            String stockcost = "SELECT SUM(Salary) TCOST FROM  userlogin";
            prs = con.prepareStatement(stockcost);
            rs1 = prs.executeQuery();
            rs1.next();
            double storeincost = rs1.getDouble("TCOST");
            //ttotalcost.setText(String.format("%.2f", storeincost));

            //getting totalpayments
            String sqlout = "SELECT SUM(Payed) PSUM FROM userlogin";
            prs = con.prepareStatement(sqlout);
            rs2 = prs.executeQuery();
            rs2.next();
            double storeout = rs2.getDouble("PSUM");
            //ttotalpayments.setText(String.format("%.2f", storeout));

            //getting totalbance
            String sqloutcost = "SELECT SUM(Balance) BSUM FROM userlogin";
            prs = con.prepareStatement(sqloutcost);
            rs3 = prs.executeQuery();
            rs3.next();
            double storecostout = rs3.getDouble("BSUM");
            // ttotalbalance.setText(String.format("%.2f", storecostout));

            //setting in texts
            ttotalcost.setText(String.format("%.2f", storeincost + storeincostadmin));
            ttotalpayments.setText(String.format("%.2f", storeout + storeoutadmin));
            ttotalbalance.setText(String.format("%.2f", storecostout + storecostoutadmin));

            prs.close();
            rs1.close();
            rs2.close();
            rs3.close();
            stmt.close();
            con.close();
        } catch (SQLException x) {
            JOptionPane.showMessageDialog(null, "Database Connection Failure\nPlease Rerun the application", "Error Message", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    //method for getting data names from store
    public void WorkersFound() {
        try {
            Connection con = DBConnector.getConnection();
            String sqldrugname = "SELECT username FROM adminlogin\n"
                    + "UNION\n"
                    + "SELECT username FROM userlogin\n"
                    + "ORDER BY username; ";
            prs = con.prepareStatement(sqldrugname);
            rs = prs.executeQuery();
            Vector vnames = new Vector();
            vnames.add("Choose Worker");
            while (rs.next()) {
                workerfound = rs.getString("username");
                vnames.add(workerfound);
            }
            //String[] optionsnames = {""};
            boxdeletemember = new JComboBox<String>(vnames);
            rs.close();
            prs.close();
            con.close();
        } catch (SQLException x) {
            JOptionPane.showMessageDialog(null, "Database Connection Failure\nPlease Rerun the application", "Error Message", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    //method for uploading values
    private void uplaod() {
        DefaultTableModel pharmacymodel = new DefaultTableModel();
        //dm.addColumn("Medicine Name");
        pharmacymodel.setColumnIdentifiers(values);
        String fetchrecord2 = "SELECT * FROM adminlogin UNION SELECT * FROM userlogin ORDER BY NationalID";
        try {
            Connection con = DBConnector.getConnection();
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(fetchrecord2);
            if (rs.next()) {
                do {
                    int workerid = rs.getInt("NationalID");
                    String id = String.valueOf(workerid);
                    String workername = rs.getString("username");
                    String workerpn = rs.getString("personalnumber");
                    double workersalary = rs.getDouble("Salary");
                    String wsalary = String.format("%.2f", workersalary);
                    double workerpayed = rs.getDouble("Payed");
                    String wpayed = String.format("%.2f", workerpayed);
                    double workerbalance = rs.getDouble("Balance");
                    String wbalance = String.format("%.2f", workerbalance);
                    String wedited = rs.getString("LastEdited");

                    pharmacymodel.addRow(new String[]{id, workername, workerpn, wsalary, wpayed, wbalance, wedited});

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
                JOptionPane.showMessageDialog(null, "No Workers In The Store", "Notification", JOptionPane.INFORMATION_MESSAGE);
                rs.close();
                stmt.close();
                con.close();
            }
        } catch (SQLException x) {
            JOptionPane.showMessageDialog(null, "Database Connection Failure\nPlease Rerun the application", "Error Message", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    //method for adding new client
    private void addworker() {
        String admin = "Admin";
        String user = "User";
        if (tnidadd.getText().equalsIgnoreCase("") && tpnumberadd.getText().equalsIgnoreCase("") && tusernameadd.getText().equalsIgnoreCase("") || ttpaymentdd.getText().equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(null, "Please Enter Details Of New Worker", "Membership Message", JOptionPane.INFORMATION_MESSAGE);
        } else {
            try {
                String[] option = {"Yes", "No"};
                int selloption = JOptionPane.showOptionDialog(null, "Are you sure you want add" + " " + tusernameadd.getText() + " " + "as" + " " + addtype, "New Worker Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
                if (selloption == 0) {
                    if (admin.equalsIgnoreCase(addtype)) {
                        Connection con = DBConnector.getConnection();
                        //adding to userlogin
                        String sqladd10 = "INSERT INTO adminlogin(NationalID,username,personalnumber,Salary,Balance,LastEdited) VALUES (?,?,?,?,?,?)";
                        prs2 = con.prepareStatement(sqladd10);

                        //setting to database
                        prs2.setInt(1, Integer.parseInt(String.valueOf(tnidadd.getText())));
                        prs2.setString(2, tusernameadd.getText());
                        prs2.setString(3, tpnumberadd.getText());
                        prs2.setDouble(4, Double.parseDouble(ttpaymentdd.getText()));
                        prs2.setDouble(5, Double.parseDouble(ttpaymentdd.getText()));
                        prs2.setString(6, fileeditedlast);

                        prs2.execute();
                        if (prs2 != null) {
                            tusernameadd.setText("");
                            tpnumberadd.setText("");
                            tnidadd.setText("");
                            ttpaymentdd.setText("");

                            JOptionPane.showMessageDialog(null, tusernameadd.getText() + " " + "Added Successful", "New Worker Confirmation", JOptionPane.INFORMATION_MESSAGE);
                            rs.close();
                            prs.close();
                            prs2.close();
                            con.close();

                            //loading table
                            all();
                            WorkersFound();
                            uplaod();
                        } else {
                            tusernameadd.setText("");
                            tpnumberadd.setText("");
                            tnidadd.setText("");
                            ttpaymentdd.setText("");

                            uplaod();
                            JOptionPane.showMessageDialog(null, tusernameadd.getText() + " " + "Not Added Successfully", "New Worker Confirmation", JOptionPane.ERROR_MESSAGE);
                            rs.close();
                            prs.close();
                            prs2.close();
                            con.close();
                        }

                    } else if (user.equalsIgnoreCase(addtype)) {
                        Connection con = DBConnector.getConnection();
                        //adding to userlogin
                        String sqladd50 = "INSERT INTO userlogin(NationalID,username,personalnumber,Salary,Balance,LastEdited) VALUES (?,?,?,?,?,?)";
                        prs2 = con.prepareStatement(sqladd50);

                        //setting to database
                        prs2.setInt(1, Integer.parseInt(String.valueOf(tnidadd.getText())));
                        prs2.setString(2, tusernameadd.getText());
                        prs2.setString(3, tpnumberadd.getText());
                        prs2.setDouble(4, Double.parseDouble(ttpaymentdd.getText()));
                        prs2.setDouble(5, Double.parseDouble(ttpaymentdd.getText()));
                        prs2.setString(6, fileeditedlast);

                        prs2.execute();
                        if (prs2 != null) {
                            tusernameadd.setText("");
                            tpnumberadd.setText("");
                            tnidadd.setText("");
                            ttpaymentdd.setText("");

                            uplaod();
                            JOptionPane.showMessageDialog(null, tusernameadd.getText() + " " + "Added Successful", "New Worker Confirmation", JOptionPane.INFORMATION_MESSAGE);
                            rs.close();
                            prs.close();
                            prs2.close();
                            con.close();
                        } else {
                            tusernameadd.setText("");
                            tpnumberadd.setText("");
                            tnidadd.setText("");
                            ttpaymentdd.setText("");

                            uplaod();
                            JOptionPane.showMessageDialog(null, tusernameadd.getText() + " " + "Not Added Successfully", "New Worker Confirmation", JOptionPane.ERROR_MESSAGE);
                            rs.close();
                            prs.close();
                            prs2.close();
                            con.close();
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "You Cancelled", "Error Message", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException x) {
                xamppfailure.getCon();
            }
        }
    }

    //method for searching
    private void searchengine() {
        String valuesearch = tsearch.getText();
        String serialgot = String.valueOf(tsearch.getText());
        if (valuesearch.equalsIgnoreCase("") || valuesearch.equalsIgnoreCase(null)) {
            JOptionPane.showMessageDialog(null, "Please Enter The Worker Name/National_ID/Personal Number For Search", "Membership Message", JOptionPane.INFORMATION_MESSAGE);
        } else {
            DefaultTableModel pharmacymodel = new DefaultTableModel();
            //dm.addColumn("Medicine Name");
            pharmacymodel.setColumnIdentifiers(values);
            String fetchrecord = "SELECT * FROM userlogin WHERE NationalID = '" + serialgot + "' || username  = '" + tsearch.getText() + "' || personalnumber = '" + tsearch.getText() + "' UNION SELECT * FROM adminlogin WHERE NationalID = '" + serialgot + "' || username  = '" + tsearch.getText() + "' || personalnumber = '" + tsearch.getText() + "'";
            try {
                Connection con = DBConnector.getConnection();
                stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                rs = stmt.executeQuery(fetchrecord);
                if (rs.next()) {
                    do {
                        int workerid = rs.getInt("NationalID");
                        String id = String.valueOf(workerid);
                        String workername = rs.getString("username");
                        String workerpn = rs.getString("personalnumber");
                        double workersalary = rs.getDouble("Salary");
                        String wsalary = String.format("%.2f", workersalary);
                        double workerpayed = rs.getDouble("Payed");
                        String wpayed = String.format("%.2f", workerpayed);
                        double workerbalance = rs.getDouble("Balance");
                        String wbalance = String.format("%.2f", workerbalance);
                        String wedited = rs.getString("LastEdited");

                        pharmacymodel.addRow(new String[]{id, workername, workerpn, wsalary, wpayed, wbalance, wedited});

                        //setting values got from table
                        tnid.setText(String.valueOf(workerid));
                        tusername.setText(workername);
                        tpnumber.setText(workerpn);
                        ttpayment.setText(String.format("%.2f", workersalary));
                        tpayed.setText(String.format("%.2f", workerpayed));
                        tbalance.setText(String.format("%.2f", workerbalance));
                        tpay.setText(null);

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

    //method for payments
    private void workerpayments() {
        String valuebalance = tbalance.getText();
        double balancegot = Double.parseDouble(valuebalance);
        String valuesalry = ttpayment.getText();
        double salarygot = Double.parseDouble(valuesalry);
        String valuesearch = tnid.getText();
        String serialgot = String.valueOf(tnid.getText());
        try {
            if (valuesearch.equalsIgnoreCase("") || valuesearch.equalsIgnoreCase(null) || valuesalry.equalsIgnoreCase("") || valuesalry.equalsIgnoreCase(null)) {
                JOptionPane.showMessageDialog(null, "Please Select The Worker From The Table To Pay", "Payment Message", JOptionPane.INFORMATION_MESSAGE);
            } else {
                if (valuesalry.equalsIgnoreCase(null) && tpay.getText().equalsIgnoreCase(null)) {
                    JOptionPane.showMessageDialog(null, "Please Enter Salary And Amount To Pay" + " " + tusername.getText(), "Payment Message", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    Connection con = DBConnector.getConnection();
                    String[] option = {"Yes", "No"};
                    int selloption = JOptionPane.showOptionDialog(null, "Proceed in Paying" + " " + tusername.getText() + " " + "Details with National_ID" + " " + serialgot, "Payment Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
                    if (selloption == 0) {
                        String updatepayments8 = "UPDATE adminlogin set Salary = '" + salarygot + "', Payed = '" + total2 + "',Balance = '" + balancegot + "',LastEdited = '" + fileeditedlast + "' WHERE NationalID = '" + serialgot + "'";
                        prs = con.prepareStatement(updatepayments8);
                        prs.execute();

                        String updatepayments10 = "UPDATE userlogin set Salary = '" + salarygot + "', Payed = '" + total2 + "',Balance = '" + balancegot + "',LastEdited = '" + fileeditedlast + "' WHERE NationalID = '" + serialgot + "'";
                        prs2 = con.prepareStatement(updatepayments10);
                        prs2.execute();
                        if (prs != null || prs2 != null) {
                            //adding to table payment records
                            String sqladd9 = "INSERT INTO paymentrecords(Cname,CIdentity,Tcost,Payed,Balance,Dpayed,LastEdited) VALUES (?,?,?,?,?,?,?)";
                            prs2 = con.prepareStatement(sqladd9);

                            //setting to database
                            prs2.setString(1, tusername.getText());
                            prs2.setString(2, tnid.getText());
                            prs2.setDouble(3, Double.parseDouble(ttpayment.getText()));
                            prs2.setDouble(4, total2);
                            prs2.setDouble(5, Double.parseDouble(tbalance.getText()));
                            prs2.setString(6, format);
                            prs2.setString(7, fileeditedlast);

                            prs2.execute();

                            JOptionPane.showMessageDialog(null, tusername.getText() + " " + "Payed Successful", "Payment Confirmation", JOptionPane.INFORMATION_MESSAGE, icon2);
                            rs.close();
                            prs.close();
                            prs2.close();
                            con.close();

                            //setting null values
                            tnid.setText(null);
                            tpnumber.setText(null);
                            tusername.setText(null);
                            tbalance.setText(null);
                            ttpayment.setText(null);
                            tpayed.setText(null);
                            tpay.setText(null);

                            //method for reloading details
                            all();
                            uplaod();
                        } else {
                            JOptionPane.showMessageDialog(null, tusername.getText() + " " + "Payment Failed", "Error Message", JOptionPane.ERROR_MESSAGE);
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
            }
        } catch (SQLException x) {
            xamppfailure.getCon();
        }

    }

    public void workers() {
        llogo1 = new JLabel(imagepayments);
        llogo2 = new JLabel(imagepayments);
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

        ///comboboxes actions
        String[] options = {"Add_Type", "Admin", "User"};
        choose = new JComboBox<String>(options);
        choose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        choose.setFont(new Font("Tahoma", Font.PLAIN, 12));
        choose.addActionListener(event -> {
            JComboBox<String> ltype = (JComboBox<String>) event.getSource();
            addtype = (String) ltype.getSelectedItem();
            if (addtype.equalsIgnoreCase("Admin")) {
                //JOptionPane.showMessageDialog(null, "", "", JOptionPane.INFORMATION_MESSAGE);
            } else if (addtype.equalsIgnoreCase("User")) {
                //JOptionPane.showMessageDialog(null, type2, "", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        //combobox for names
        boxdeletemember.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boxdeletemember.setToolTipText("Search worker By NationalID");
        boxdeletemember.setFont(new Font("Tahoma", Font.PLAIN, 12));
        //method
        boxdeletemember.addActionListener(event -> {
            JComboBox<String> ltype = (JComboBox<String>) event.getSource();
            workergot = (String) ltype.getSelectedItem();
            Connection con = null;
            try {
                con = DBConnector.getConnection();
                String workername = "SELECT * FROM adminlogin WHERE username = '" + workergot + "'";
                prs = con.prepareStatement(workername);
                rs = prs.executeQuery();
                rs.next();
                int workerid = rs.getInt("NationalID");
                String id = String.valueOf(workerid);
                //JOptionPane.showMessageDialog(null, id, "", JOptionPane.INFORMATION_MESSAGE);
                tdeletemember.setText(id);
            } catch (SQLException x) {
                //JOptionPane.showMessageDialog(null, "Connection Failure" + "\n" + x, "Error Message", JOptionPane.ERROR_MESSAGE);
                //System.exit(0);
            }
            try {
                con = DBConnector.getConnection();
                String workername2 = "SELECT * FROM userlogin WHERE username = '" + workergot + "'";
                prs = con.prepareStatement(workername2);
                rs = prs.executeQuery();
                rs.next();
                int workerid = rs.getInt("NationalID");
                String id = String.valueOf(workerid);
                //JOptionPane.showMessageDialog(null, id, "", JOptionPane.INFORMATION_MESSAGE);
                tdeletemember.setText(id);
            } catch (SQLException x) {
                //JOptionPane.showMessageDialog(null, "Connection Failure" + "\n" + x, "Error Message", JOptionPane.ERROR_MESSAGE);
                //System.exit(0);
            }
        });
        //end of combobox

        lview = new JLabel(imageviewsection);
        ladd = new JLabel(imageaddsection);
        lviewtitle = new JLabel("WORKER DETAILS");
        lviewtitle.setFont(new Font("Tahoma", Font.BOLD, 15));
        lremove = new JLabel("REMOVE WORKER");
        lremove.setFont(new Font("Tahoma", Font.BOLD, 15));
        laddtitle = new JLabel("ADD NEW WORKER");
        laddtitle.setFont(new Font("Tahoma", Font.BOLD, 15));
        ltitle = new JLabel("USERS RECORDS");
        ltitle.setFont(new Font("Tahoma", Font.BOLD, 20));
        tsearch = new JTextField(25);
        tsearch.setFont(new Font("Tahoma", Font.PLAIN, 15));
        tsearch.setToolTipText("Search Worker By Name/ID Number");
        ltotalpayments = new JLabel("Total Payments[KSH]");
        ltotalpayments.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ltotalcost = new JLabel("Total Salaries[KSH]");
        ltotalcost.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ltotalbalance = new JLabel("Total Balance[KSH]");
        ltotalbalance.setFont(new Font("Tahoma", Font.PLAIN, 13));

        ldeletemember = new JLabel("Enter/Confirm National ID");
        ldeletemember.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lnid = new JLabel("National ID");
        lnid.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lusername = new JLabel("Worker Name");
        lusername.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lpnumber = new JLabel("Personal Number/PIN");
        lpnumber.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ltpayment = new JLabel("Salary[KSH]");
        ltpayment.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lpayed = new JLabel("Amount Paid[KSH]");
        lpayed.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lpay = new JLabel("Pay[KSH]");
        lpay.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lbalance = new JLabel("Balance[KSH]");
        lbalance.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lnidadd = new JLabel("National ID");
        lnidadd.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lusernameadd = new JLabel("Worker Name");
        lusernameadd.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lpnumberadd = new JLabel("Personal Number/PIN");
        lpnumberadd.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ltpaymentdd = new JLabel("Enter Salary");
        ltpaymentdd.setFont(new Font("Tahoma", Font.PLAIN, 15));

        tnid = new JTextField(15);
        tnid.setBackground(Color.lightGray);
        tnid.setEditable(false);
        tnid.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tusername = new JTextField(15);
        tusername.setBackground(Color.lightGray);
        tusername.setEditable(false);
        tusername.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tpnumber = new JTextField(15);
        tpnumber.setBackground(Color.lightGray);
        tpnumber.setEditable(false);
        tpnumber.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttpayment = new JTextField(15);
        ttpayment.setEditable(false);
        ttpayment.setBackground(Color.LIGHT_GRAY);
        ttpayment.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tpayed = new JTextField(15);
        tpayed.setBackground(Color.lightGray);
        tpayed.setEditable(false);
        tpayed.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tpay = new JTextField(15);
        tpay.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tbalance = new JTextField(15);
        tbalance.setBackground(Color.lightGray);
        tbalance.setEditable(false);
        tbalance.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tnidadd = new JTextField(15);
        tnidadd.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tusernameadd = new JTextField(15);
        tusernameadd.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttpaymentdd = new JTextField(15);
        ttpaymentdd.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tpnumberadd = new JTextField(15);
        tpnumberadd.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tdeletemember = new JTextField(15);
        tdeletemember.setBackground(Color.lightGray);
        tdeletemember.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttotalcost = new JTextField(12);
        ttotalcost.setBackground(Color.LIGHT_GRAY);
        // ttotalcost.setEditable(false);
        ttotalcost.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttotalpayments = new JTextField(12);
        ttotalpayments.setBackground(Color.LIGHT_GRAY);
        // ttotalpayments.setEditable(false);
        ttotalpayments.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttotalbalance = new JTextField(12);
        ttotalbalance.setBackground(Color.LIGHT_GRAY);
        // ttotalbalance.setEditable(false);
        ttotalbalance.setFont(new Font("Tahoma", Font.PLAIN, 13));

        //buttons
        bsearch = new JButton(imagesearch);
        bsearch.setBackground(Color.LIGHT_GRAY);
        bsearch.setToolTipText("Search Worker");
        bsearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bload = new JButton("RELOAD WORKERS");
        bload.setBackground(Color.GREEN.darker());
        bload.setToolTipText("reload members data/information");
        bload.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bload.setFont(new Font("Tahoma", Font.BOLD, 15));
        bdelete = new JButton(imagedelete);
        bdelete.setBackground(Color.RED.brighter());
        bdelete.setToolTipText("Please delete only fully paid Worker");
        bdelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bdeletemember = new JButton(imagedelete);
        bdeletemember.setBackground(Color.RED.brighter());
        bdeletemember.setToolTipText("Please delete only fully paid Worker");
        bdeletemember.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bpay = new JButton(imagepay);
        bpay.setBackground(Color.GREEN.brighter());
        bpay.setToolTipText("Pay Worker");
        bpay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        badd = new JButton(imageadd);
        badd.setBackground(Color.GREEN.brighter());
        badd.setToolTipText("Add New Worker");
        badd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bcancel = new JButton(imagecancel);
        bcancel.setBackground(Color.RED.brighter());
        bcancel.setToolTipText("Cancel/Back");
        bcancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        badd1 = new JButton(imageadd);
        badd1.setBackground(Color.lightGray);
        badd1.setToolTipText("Add New Worker");
        badd1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bback = new JButton("BACK");
        bback.setFont(new Font("Serif", Font.BOLD + Font.ITALIC, 15));
        bback.setBackground(Color.LIGHT_GRAY);
        bback.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bback.setFont(new Font("Tahoma", Font.BOLD, 15));

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
        paneltable.setBorder(new TitledBorder("Members Details"));
        paneltable.revalidate();

        //panel all
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 80, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        panelall.add(bback, v);
        v.insets = new Insets(0, 0, 10, 0);
        v.gridy++;
        panelall.add(badd1, v);
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

        //panel view
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 30, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        panelview.add(lview, v);
        v.gridy++;
        panelview.add(lviewtitle, v);
        v.insets = new Insets(0, 0, 0, 0);
        v.gridy++;
        panelview.add(lnid, v);
        v.gridy++;
        panelview.add(tnid, v);
        v.gridy++;
        panelview.add(lusername, v);
        v.gridy++;
        panelview.add(tusername, v);
        v.gridy++;
        panelview.add(lpnumber, v);
        v.gridy++;
        panelview.add(tpnumber, v);
        v.gridy++;
        panelview.add(ltpayment, v);
        v.gridy++;
        panelview.add(ttpayment, v);
        v.gridy++;
        panelview.add(lpayed, v);
        v.gridy++;
        panelview.add(tpayed, v);
        v.gridy++;
        panelview.add(lpay, v);
        v.gridy++;
        panelview.add(tpay, v);
        v.gridy++;
        panelview.add(lbalance, v);
        v.gridy++;
        panelview.add(tbalance, v);
        v.gridy++;
        v.anchor = GridBagConstraints.WEST;
        v.insets = new Insets(15, 0, 0, 0);
        panelview.add(bpay, v);
        v.anchor = GridBagConstraints.EAST;
        panelview.add(bdelete, v);
        v.gridy++;
        panelview.setBorder(new TitledBorder(""));

        //panel add
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 30, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        paneladd.add(ladd, v);
        v.gridy++;
        paneladd.add(laddtitle, v);
        v.insets = new Insets(0, 0, 0, 0);
        v.gridy++;
        paneladd.add(lnidadd, v);
        v.gridy++;
        paneladd.add(tnidadd, v);
        v.gridy++;
        paneladd.add(lusernameadd, v);
        v.gridy++;
        paneladd.add(tusernameadd, v);
        v.gridy++;
        paneladd.add(lpnumberadd, v);
        v.gridy++;
        paneladd.add(tpnumberadd, v);
        v.gridy++;
        paneladd.add(ltpaymentdd, v);
        v.gridy++;
        paneladd.add(ttpaymentdd, v);
        v.insets = new Insets(15, 0, 0, 0);
        v.gridy++;
        paneladd.add(choose, v);
        v.gridy++;
        v.anchor = GridBagConstraints.WEST;
        v.insets = new Insets(20, 0, 10, 0);
        paneladd.add(badd, v);
        v.anchor = GridBagConstraints.EAST;
        paneladd.add(bcancel, v);
        v.anchor = GridBagConstraints.CENTER;
        v.gridy++;
        paneladd.add(lremove, v);
        v.insets = new Insets(0, 0, 10, 0);
        v.gridy++;
        paneladd.add(boxdeletemember, v);
        v.insets = new Insets(0, 0, 0, 0);
        v.gridy++;
        paneladd.add(ldeletemember, v);
        v.gridy++;
        paneladd.add(tdeletemember, v);
        v.insets = new Insets(10, 0, 0, 0);
        v.gridy++;
        paneladd.add(bdeletemember, v);
        paneladd.setBorder(new TitledBorder(""));

        //panel card
        panelcard.add(panelview);
        panelcard.add(paneladd);
        java.awt.CardLayout mcardLayout = (java.awt.CardLayout) (panelcard.getLayout());
        bcancel.addActionListener(e -> mcardLayout.first(panelcard));
        badd1.addActionListener(e -> mcardLayout.last(panelcard));

        //panelmain
        panelmain.add("West", panelall);
        panelmain.add("Center", paneltable);
        panelmain.add("East", panelcard);
        panelmain.setBorder(new TitledBorder(""));
        panelmain.setBackground(Color.blue.brighter());
        panelmain.revalidate();

        //actions
        //methods for calculating payment aand balance
        tpay.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updatecontent();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updatecontent();
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
                tablecontents();

            }

            @Override
            public void mousePressed(MouseEvent e) {
                //getting values from table
                tablecontents();
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
        //actions end
        //setting frame
        Fworker = new JFrame("Pharmacy System");
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("nimbusBase", Color.blue);
        } catch (Exception c) {
        }
        Fworker.setUndecorated(true);
        Fworker.setIconImage(iconimage);
        Fworker.add(panelmain);
        Fworker.setVisible(true);
        //Fstore.setSize(1400, 780);
        Fworker.setSize(screenSize.width, screenSize.height);
        Fworker.revalidate();
        Fworker.pack();
        Fworker.revalidate();
        Fworker.setLocationRelativeTo(null);
        //Fsuppliers.setResizable(false);
        Fworker.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        //frame state to make components responsive
        Fworker.addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                // minimized
                if ((e.getNewState() & Frame.ICONIFIED) == Frame.ICONIFIED) {
                    Fworker.revalidate();
                    Fworker.pack();
                    Fworker.revalidate();
                    Fworker.setLocationRelativeTo(null);
                } // maximized
                else if ((e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                    Fworker.revalidate();
                    Fworker.pack();
                    Fworker.revalidate();
                    Fworker.setLocationRelativeTo(null);
                }
            }
        });

        //actions
        bdeletemember.addActionListener(e -> {
            String valuesearch = tdeletemember.getText();
            String serialgot = String.valueOf(tdeletemember.getText());
            try {
                if (valuesearch.equalsIgnoreCase("") || valuesearch.equalsIgnoreCase(null)) {
                    JOptionPane.showMessageDialog(null, "Please Enter ID For Admin/User", "Deletion Message", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    Connection con = DBConnector.getConnection();
                    String[] option = {"Yes", "No"};
                    int selloption = JOptionPane.showOptionDialog(null, "Proceed in Deleting " + workergot + " Details with National_ID " + " " + serialgot, "Deletion Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
                    if (selloption == 0) {
                        String sqldelete = "DELETE FROM userlogin WHERE NationalID = '" + serialgot + "'";
                        String sqldelete2 = "DELETE FROM adminlogin WHERE NationalID = '" + serialgot + "'";
                        prs = con.prepareStatement(sqldelete);
                        prs2 = con.prepareStatement(sqldelete2);
                        prs.execute();
                        prs2.execute();
                        if (prs != null || prs2 != null) {
                            JOptionPane.showMessageDialog(null, serialgot + " " + "Deleted Successful", "Deletion Confirmation", JOptionPane.INFORMATION_MESSAGE, icon);
                            rs.close();
                            prs2.close();
                            con.close();

                            tdeletemember.setText("");

                            //loading table
                            all();
                            WorkersFound();
                            uplaod();
                        } else {
                            JOptionPane.showMessageDialog(null, serialgot + " " + "Deletion Failed", "Error Message", JOptionPane.ERROR_MESSAGE);
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
            } catch (SQLException x) {
                xamppfailure.getCon();
            }

        });
        badd.addActionListener(e -> {
            addworker();
        });
        bload.addActionListener(e -> {
            uplaod();
            all();
        });
        bdelete.addActionListener(e -> {
            String valuesearch = tnid.getText();
            String serialgot = String.valueOf(tnid.getText());
            try {
                if (valuesearch.equalsIgnoreCase("") || valuesearch.equalsIgnoreCase(null)) {
                    JOptionPane.showMessageDialog(null, "Please Select The Worker From The Table To Delete", "Deletion Message", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    Connection con = DBConnector.getConnection();
                    String[] option = {"Yes", "No"};
                    int selloption = JOptionPane.showOptionDialog(null, "Proceed in Deleting" + " " + tusername.getText() + " " + "Details with National_ID" + " " + serialgot, "Deletion Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
                    if (selloption == 0) {
                        String sqldelete = "DELETE FROM userlogin WHERE NationalID = '" + serialgot + "'";
                        prs = con.prepareStatement(sqldelete);
                        prs.execute();
                        if (prs != null || prs2 != null) {
                            JOptionPane.showMessageDialog(null, tusername.getText() + " " + "Deleted Successful", "Deletion Confirmation", JOptionPane.INFORMATION_MESSAGE, icon);
                            rs.close();
                            prs.close();
                            con.close();

                            //setting null values
                            tnid.setText(null);
                            tpnumber.setText(null);
                            tusername.setText(null);
                            tbalance.setText(null);
                            ttpayment.setText(null);
                            tpayed.setText(null);
                            tpay.setText(null);

                            //loading table
                            all();
                            WorkersFound();
                            uplaod();
                        } else {
                            JOptionPane.showMessageDialog(null, tusername.getText() + " " + "Deletion Failed", "Error Message", JOptionPane.ERROR_MESSAGE);
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
            } catch (SQLException x) {
                xamppfailure.getCon();
            }

        });
        bsearch.addActionListener(e -> {
            searchengine();
        });
        bpay.addActionListener(e -> {
            workerpayments();
        });
        bback.addActionListener(e -> {
            Fworker.setVisible(false);
            Sections ds = new Sections();
            ds.Operations();
        });
        //end of actions
    }
}
