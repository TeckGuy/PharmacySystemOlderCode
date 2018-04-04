package analysis;

import dbconnector.DBConnector;
import sun.applet.Main;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

public class StockAnalysis {

    //dimension setting
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    //getting images
    URL url1 = Main.class.getResource("/img/logo.png");
    URL url2 = Main.class.getResource("/img/stock.png");
    URL url3 = Main.class.getResource("/img/delete.png");
    URL url4 = Main.class.getResource("/img/search.png");

    ImageIcon imagestock = new ImageIcon(url2);
    ImageIcon imagedelete = new ImageIcon(url3);
    ImageIcon imagesearch = new ImageIcon(url4);

    //setting ma default image icon to my frames
    final ImageIcon icon = new ImageIcon(url3);
    final ImageIcon iconstock = new ImageIcon(url2);
    Image iconimage = new ImageIcon(url1).getImage();

    //components
    JLabel ltstockall, ltstock, ltcostall, ltcost, ltsoldall, ltsold, ltscostall, lname, lserial, ltcostsold, ladvanced, ltitle, lprofitmain, llossmain, lprofit, lloss, ltpayments, llogo1, llogo2, lbrief, llogo3;
    JTextField ttstockall, ttstock, ttcostall, ttcost, ttsoldall, ttsold, ttscostall, ttcostsold, tname, tserial, tsearch, tadvanced, tprofitmain, tlossmain, tprofit, tloss, ttpayments;
    JTable table;
    JButton bdelete, bsearch, bload, bback;

    JPanel panelstcok = new JPanel(new GridBagLayout());
    JPanel panelstockitem = new JPanel(new GridBagLayout());
    JPanel paneltable = new JPanel(new GridBagLayout());
    JPanel panelmain = new JPanel(new BorderLayout(0, 0));

    JFrame Fstock = new JFrame();

    //set variables
    String[] values = new String[]{"Product Name", "Serial Number", "Stock In", "Stock In Cost", "Stock Out", "Stock Out Cost", "Total Profit", "Total Deficit", "Saved On"};
    String pname, psearial;
    double stockin, costin, stockout, costout, profit, loss;

    //database connectors
    Statement stmt = null;
    ResultSet rs, rs1, rs2, rs3, rs4, rs5, rs6 = null;
    PreparedStatement prs, prs2, prs3, prs4, prs5 = null;

    //date
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy, hh:mm:ss.SSS a");
    LocalDateTime now = LocalDateTime.now();
    String fileeditedlast = dtf.format(now);

    //calling class dbconnect
    DBConnector xamppfailure = new DBConnector();

    //getting table contents
    private void tablecont() {
        String storename = table.getValueAt(table.getSelectedRow(), 0).toString();
        String storeserial = table.getValueAt(table.getSelectedRow(), 1).toString();
        double storein = Double.parseDouble(String.valueOf(table.getValueAt(table.getSelectedRow(), 2)));
        double storeincost = Double.parseDouble(String.valueOf(table.getValueAt(table.getSelectedRow(), 3)));
        double storeout = Double.parseDouble(String.valueOf(table.getValueAt(table.getSelectedRow(), 4)));
        double storeoutcost = Double.parseDouble(String.valueOf(table.getValueAt(table.getSelectedRow(), 5)));
        double storeprofit = Double.parseDouble(String.valueOf(table.getValueAt(table.getSelectedRow(), 6)));
        double storeloss = Double.parseDouble(String.valueOf(table.getValueAt(table.getSelectedRow(), 7)));

        //setting values got from table
        tname.setText(storename);
        tserial.setText(storeserial);
        ttstock.setText(String.format("%.2f", storein));
        ttcost.setText(String.format("%.2f", storeincost));
        ttsold.setText(String.format("%.2f", storeout));
        ttcostsold.setText(String.format("%.2f", storeoutcost));
        tprofit.setText(String.format("%.2f", storeprofit));
        tloss.setText(String.format("%.2f", storeloss));
        tadvanced.setText(storeserial);
    }

    //method for loading and analyzing all data for the whole stock
    private void storeanalyzer() {
        try {
            Connection con = DBConnector.getConnection();
            String stocksql = "SELECT SUM(Mtotalquantity) STORESTOCK FROM  tablestockin";
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(stocksql);
            if (rs.next()) {
                double storein = rs.getDouble("STORESTOCK");

                //getting sum BUYING of stock
                String stockcost = "SELECT SUM(Mtotalcost) STORESTOCKCOST FROM  tablestockin";
                prs = con.prepareStatement(stockcost);
                rs1 = prs.executeQuery();
                rs1.next();
                double storeincost = rs1.getDouble("STORESTOCKCOST");

                //getting sum sold of stock
                String sqlout = "SELECT SUM(Mtotalquantity) STOCKOUTSUM FROM tablesell";
                prs = con.prepareStatement(sqlout);
                rs2 = prs.executeQuery();
                rs2.next();
                double storeout = rs2.getDouble("STOCKOUTSUM");
                //JOptionPane.showMessageDialog(null, storeout);

                //getting sum sold for stock cost
                String sqloutcost = "SELECT SUM(Mtotalcost) COSTOUTSUM FROM tablesell";
                prs = con.prepareStatement(sqloutcost);
                rs3 = prs.executeQuery();
                rs3.next();
                double storecostout = rs3.getDouble("COSTOUTSUM");
                //JOptionPane.showMessageDialog(null, storecostout);

                //getting sum of payements from suppliers
                String sqlsumsupply = "SELECT SUM(Payed) SUMPAYED FROM tabledistributers";
                prs = con.prepareStatement(sqlsumsupply);
                rs4 = prs.executeQuery();
                rs4.next();
                double supplypay = rs4.getDouble("SUMPAYED");

                //getting sum of payements from users
                String sqlsumworkers = "SELECT SUM(Payed) SUMPAYED FROM userlogin";
                prs = con.prepareStatement(sqlsumworkers);
                rs5 = prs.executeQuery();
                rs5.next();
                double workerpay = rs5.getDouble("SUMPAYED");

                //getting sum of payements from admin
                String sqlsumworkers2 = "SELECT SUM(Payed) ADMINPAYED FROM adminlogin";
                prs = con.prepareStatement(sqlsumworkers2);
                rs6 = prs.executeQuery();
                rs6.next();
                double workerpay2 = rs6.getDouble("ADMINPAYED");

                //calculate the profit,payments and losses
                double totalpay = supplypay + workerpay + workerpay2;
                double storeprofit = storecostout - (storeincost + totalpay);
                double storeloss = storeincost - storecostout;
                double storevalue = 0;

                ttstockall.setText(String.format("%.2f", storein));
                ttcostall.setText(String.format("%.2f", storeincost));
                ttsoldall.setText(String.format("%.2f", storeout));
                ttscostall.setText(String.format("%.2f", storecostout));
                ttpayments.setText(String.format("%.2f", totalpay));
                if (storeprofit >= 1) {
                    tprofitmain.setText(String.format("%.2f", storeprofit));
                } else {
                    tprofitmain.setText(String.format("%.2f", storevalue));
                }
                if (storeloss >= 1) {
                    tlossmain.setText(String.format("%.2f", storeloss));
                } else {
                    tlossmain.setText(String.format("%.2f", storevalue));
                }

                rs4.close();
                rs2.close();
                rs3.close();
                rs1.close();
                rs.close();
                stmt.close();
                con.close();
            } else {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "No Stock In The Store For Analysis", "Notification", JOptionPane.INFORMATION_MESSAGE);
                rs.close();
                stmt.close();
                con.close();
            }
        } catch (SQLException x) {
            JOptionPane.showMessageDialog(null, "Database Connection Failure\nPlease Rerun the application", "Error Message", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    //method for searching
    private void searchengine() {
        String valuesearch = tsearch.getText();
        if (valuesearch.equalsIgnoreCase("") || valuesearch.equalsIgnoreCase(null)) {
            JOptionPane.showMessageDialog(null, "Please Enter The Medicine Name/Serial Number For Search", "Search Message", JOptionPane.INFORMATION_MESSAGE);
        } else {
            DefaultTableModel pharmacymodel = new DefaultTableModel();
            //dm.addColumn("Medicine Name");
            pharmacymodel.setColumnIdentifiers(values);
            String fetchrecord = "SELECT * FROM tablecritical WHERE Mserial = '" + tsearch.getText() + "' || Mname = '" + tsearch.getText() + "'";
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

                        //setting values got from table
                        tname.setText(drugname);
                        tserial.setText(drugserial);
                        ttstock.setText(String.format("%.2f", druginstock));
                        ttcost.setText(String.format("%.2f", drugincost));
                        ttsold.setText(String.format("%.2f", drugoutstock));
                        ttcostsold.setText(String.format("%.2f", drugoutcost));
                        tprofit.setText(String.format("%.2f", drugprofit));
                        tloss.setText(String.format("%.2f", drugloss));
                        tadvanced.setText(drugserial);

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

    //metod for analysing data and analyzing data for a single stock
    public void analyzingdata() {
        try {
            Connection con = DBConnector.getConnection();
            String fetchserial = "SELECT * FROM  tablestockin";
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(fetchserial);
            if (rs.next()) {
                do {
                    String storename = rs.getString("Mname");
                    String storeserial = rs.getString("Mserial");
                    double storein = rs.getDouble("Mtotalquantity");
                    double storeincost = rs.getDouble("Mtotalcost");
                    //JOptionPane.showMessageDialog(null, storename + " " + storeserial);
                    //JOptionPane.showMessageDialog(null, storein + " " + storeincost);

                    //getting sum sold of stock
                    String sqlout = "SELECT SUM(Mtotalquantity) STOCKOUTSUM FROM tablesell WHERE Mserial = '" + storeserial + "'";
                    prs = con.prepareStatement(sqlout);
                    rs2 = prs.executeQuery();
                    while (rs2.next()) {
                        double storeout = rs2.getDouble("STOCKOUTSUM");
                        //JOptionPane.showMessageDialog(null, storeout);

                        //getting sum sold for stock cost
                        String sqloutcost = "SELECT SUM(Mtotalcost) COSTOUTSUM FROM tablesell WHERE Mserial = '" + storeserial + "'";
                        prs = con.prepareStatement(sqloutcost);
                        rs3 = prs.executeQuery();
                        while (rs3.next()) {
                            double storecostout = rs3.getDouble("COSTOUTSUM");
                            //JOptionPane.showMessageDialog(null, storecostout);

                            //calculate the profit
                            double finalprofit = storecostout - storeincost;
                            double finalloss = storeincost - storecostout;
                            double finalvalue = 0;

                            String nameanalyzed = "SELECT Mserial FROM tablecritical WHERE Mserial = '" + storeserial + "'";
                            prs = con.prepareStatement(nameanalyzed);
                            rs4 = prs.executeQuery();
                            if (rs4.next()) {
                                if (finalprofit >= 1) {
                                    String updatetablecitical = "UPDATE tablecritical set StockOut   = '" + storeout + "',StockOutCost  = '" + storecostout + "',Profit  = '" + finalprofit + "' WHERE Mserial = '" + storeserial + "'";
                                    prs = con.prepareStatement(updatetablecitical);

                                    prs.execute();
                                } else {
                                    String updatetablecitical = "UPDATE tablecritical set StockOut   = '" + storeout + "',StockOutCost  = '" + storecostout + "',Profit  = '" + finalvalue + "' WHERE Mserial = '" + storeserial + "'";
                                    prs = con.prepareStatement(updatetablecitical);

                                    prs.execute();
                                }
                                if (finalloss >= 1) {
                                    String updatetablecitical = "UPDATE tablecritical set StockOut   = '" + storeout + "',StockOutCost  = '" + storecostout + "',Loss  = '" + finalloss + "' WHERE Mserial = '" + storeserial + "'";
                                    prs = con.prepareStatement(updatetablecitical);

                                    prs.execute();
                                } else {
                                    String updatetablecitical = "UPDATE tablecritical set StockOut   = '" + storeout + "',StockOutCost  = '" + storecostout + "',Loss  = '" + finalvalue + "' WHERE Mserial = '" + storeserial + "'";
                                    prs = con.prepareStatement(updatetablecitical);

                                    prs.execute();
                                }

                            } else {
                                String sqladd = "INSERT INTO tablecritical(Mname,Mserial,StockIn,StockInCost,StockOut,StockOutCost,Profit,Loss,LastEdited) VALUES (?,?,?,?,?,?,?,?,?)";
                                prs = con.prepareStatement(sqladd);

                                //setting to database
                                prs.setString(1, storename);
                                prs.setString(2, storeserial);
                                prs.setDouble(3, storein);
                                prs.setDouble(4, storeincost);
                                prs.setDouble(5, storeout);
                                prs.setDouble(6, storecostout);
                                if (finalprofit >= 1) {
                                    prs.setDouble(7, finalprofit);
                                } else {
                                    prs.setDouble(7, finalvalue);
                                }
                                if (finalloss >= 1) {
                                    prs.setDouble(8, finalloss);
                                } else {
                                    prs.setDouble(8, finalvalue);
                                }
                                prs.setString(9, fileeditedlast);

                                prs.execute();

                            }
                        }

                    }

                } while (rs.next());

                rs4.close();
                rs2.close();
                rs3.close();
                rs.close();
                stmt.close();
                con.close();
            } else {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "No Stock In The Store For Analysis", "Notification", JOptionPane.INFORMATION_MESSAGE);
                rs.close();
                stmt.close();
                con.close();
            }
        } catch (SQLException x) {
            JOptionPane.showMessageDialog(null, "Database Connection Failure\nPlease Rerun the application", "Error Message", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    //method for uploadin data in system
    private void uplaod() {
        DefaultTableModel pharmacymodel = new DefaultTableModel();
        //dm.addColumn("Medicine Name");
        pharmacymodel.setColumnIdentifiers(values);
        String fetchrecord = "SELECT * FROM tablecritical";
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
                JOptionPane.showMessageDialog(null, "No Stock In The Store For Analysis", "Notification", JOptionPane.INFORMATION_MESSAGE);
                rs.close();
                stmt.close();
                con.close();
            }
        } catch (SQLException x) {
            JOptionPane.showMessageDialog(null, "Database Connection Failure", "Error Message", JOptionPane.ERROR_MESSAGE);
            //System.exit(0);
        }
    }

    public void CriticalAnalysis() {
        llogo1 = new JLabel(imagestock);
        llogo2 = new JLabel(imagestock);
        llogo3 = new JLabel(imagestock);
        lbrief = new JLabel("ANALYSIS PER PRODUCT");
        lbrief.setFont(new Font("Tahoma", Font.BOLD, 13));

        table = new JTable();
        // this enables horizontal scroll bar
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFillsViewportHeight(true);
        table.setFont(new Font("Tahoma", Font.PLAIN, 13));
        //table.setAutoCreateRowSorter(true);
        table.setIntercellSpacing(new Dimension(20, 20));
        // table.setPreferredScrollableViewportSize(new Dimension(935, 570));
        table.setPreferredScrollableViewportSize(new Dimension((int) (screenSize.width / 1.47), (int) (screenSize.height / 1.40)));
        table.revalidate();
        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.revalidate();

        //for center
        ltitle = new JLabel("ANALYZED STOCK AND CASH FLOW");
        ltitle.setFont(new Font("Tahoma", Font.BOLD, 20));
        tsearch = new JTextField(25);
        tsearch.setFont(new Font("Tahoma", Font.PLAIN, 15));
        tsearch.setToolTipText("Search Product By Name/Serial Number");

        //for left side
        ltstockall = new JLabel("Total Stock In");
        ltstockall.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ltcostall = new JLabel("Total Buying Cost[KSH]");
        ltcostall.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ltsoldall = new JLabel("Total Stock Out");
        ltsoldall.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ltscostall = new JLabel("Total Selling Cost[KSH]");
        ltscostall.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ltpayments = new JLabel("Total Payments[KSH]");
        ltpayments.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lprofitmain = new JLabel("Total Profit[KSH]");
        lprofitmain.setFont(new Font("Tahoma", Font.PLAIN, 13));
        llossmain = new JLabel("Total Deficit[KSH]");
        llossmain.setFont(new Font("Tahoma", Font.PLAIN, 13));

        //for right side
        lname = new JLabel("Product Name");
        lname.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lserial = new JLabel("Serial Number");
        lserial.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ltstock = new JLabel("Stock In");
        ltstock.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ltcost = new JLabel("Total Buying Cost[KSH]");
        ltcost.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ltsold = new JLabel("Stock Out");
        ltsold.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ltcostsold = new JLabel("Total Selling Cost[KSH]");
        ltcostsold.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lprofit = new JLabel("Total Profit[KSH]");
        lprofit.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lloss = new JLabel("Total Deficit[KSH]");
        lloss.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ladvanced = new JLabel("Advanced Deletion");
        ladvanced.setForeground(Color.BLUE.darker());
        ladvanced.setFont(new Font("Tahoma", Font.BOLD, 15));

        //for left side
        ttstockall = new JTextField(10);
        // ttstockall.setEditable(false);
        ttstockall.setBackground(Color.LIGHT_GRAY);
        ttstockall.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttcostall = new JTextField(10);
        // ttcostall.setEditable(false);
        ttcostall.setBackground(Color.LIGHT_GRAY);
        ttcostall.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttsoldall = new JTextField(10);
        // ttsoldall.setEditable(false);
        ttsoldall.setBackground(Color.LIGHT_GRAY);
        ttsoldall.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttscostall = new JTextField(10);
        // ttscostall.setEditable(false);
        ttscostall.setBackground(Color.LIGHT_GRAY);
        ttscostall.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttpayments = new JTextField(10);
        // ttpayments.setEditable(false);
        ttpayments.setBackground(Color.LIGHT_GRAY);
        ttpayments.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tprofitmain = new JTextField(10);
        // tprofitmain.setEditable(false);
        tprofitmain.setBackground(Color.LIGHT_GRAY);
        tprofitmain.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tlossmain = new JTextField(10);
        // tlossmain.setEditable(false);
        tlossmain.setBackground(Color.LIGHT_GRAY);
        tlossmain.setFont(new Font("Tahoma", Font.PLAIN, 13));

        //for left side
        tname = new JTextField(15);
        tname.setBackground(Color.LIGHT_GRAY);
        tname.setEditable(false);
        tname.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tserial = new JTextField(15);
        tserial.setBackground(Color.LIGHT_GRAY);
        tserial.setEditable(false);
        tserial.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttstock = new JTextField(15);
        ttstock.setBackground(Color.LIGHT_GRAY);
        ttstock.setEditable(false);
        ttstock.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttcost = new JTextField(15);
        ttcost.setBackground(Color.LIGHT_GRAY);
        ttcost.setEditable(false);
        ttcost.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttsold = new JTextField(15);
        ttsold.setEditable(false);
        ttsold.setBackground(Color.LIGHT_GRAY);
        ttsold.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttcostsold = new JTextField(15);
        ttcostsold.setBackground(Color.LIGHT_GRAY);
        ttcostsold.setEditable(false);
        ttcostsold.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tprofit = new JTextField(15);
        tprofit.setBackground(Color.LIGHT_GRAY);
        tprofit.setEditable(false);
        tprofit.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tloss = new JTextField(15);
        tloss.setBackground(Color.LIGHT_GRAY);
        tloss.setEditable(false);
        tloss.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tadvanced = new JTextField(15);
        tadvanced.setFont(new Font("Tahoma", Font.PLAIN, 13));

        bsearch = new JButton(imagesearch);
        bsearch.setBackground(Color.LIGHT_GRAY);
        bsearch.setToolTipText("Search Product");
        bsearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bload = new JButton("START ANALYSIS/RELOAD");
        bload.setBackground(Color.GREEN.darker());
        bload.setToolTipText("load analyzed data");
        bload.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bload.setFont(new Font("Tahoma", Font.BOLD, 15));
        bdelete = new JButton(imagedelete);
        bdelete.setBackground(Color.RED.brighter());
        bdelete.setToolTipText("Please Dalete Only The Finished Stock(Enter Serial Number To Delete)");
        bdelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bback = new JButton("BACK");
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
        paneltable.setBorder(new TitledBorder(""));
        paneltable.revalidate();

        //adding to panel
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(5, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        panelstcok.add(bback, v);
        v.gridy++;
        panelstcok.add(llogo1, v);
        v.gridy++;
        panelstcok.add(ltstockall, v);
        v.gridy++;
        panelstcok.add(ttstockall, v);
        v.gridy++;
        panelstcok.add(ltcostall, v);
        v.gridy++;
        panelstcok.add(ttcostall, v);
        v.gridy++;
        panelstcok.add(ltsoldall, v);
        v.gridy++;
        panelstcok.add(ttsoldall, v);
        v.gridy++;
        panelstcok.add(ltscostall, v);
        v.gridy++;
        panelstcok.add(ttscostall, v);
        v.gridy++;
        panelstcok.add(lprofitmain, v);
        v.gridy++;
        panelstcok.add(tprofitmain, v);
        v.gridy++;
        panelstcok.add(llossmain, v);
        v.gridy++;
        panelstcok.add(tlossmain, v);
        v.gridy++;
        panelstcok.add(ltpayments, v);
        v.gridy++;
        panelstcok.add(ttpayments, v);
        v.gridy++;
        panelstcok.add(llogo2, v);
        panelstcok.setBorder(new TitledBorder(""));

        //adding to panelstockitem
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 10, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        panelstockitem.add(llogo3, v);
        v.gridy++;
        panelstockitem.add(lbrief, v);
        v.insets = new Insets(0, 0, 0, 0);
        v.gridy++;
        panelstockitem.add(lname, v);
        v.gridy++;
        panelstockitem.add(tname, v);
        v.gridy++;
        panelstockitem.add(lserial, v);
        v.gridy++;
        panelstockitem.add(tserial, v);
        v.gridy++;
        panelstockitem.add(ltstock, v);
        v.gridy++;
        panelstockitem.add(ttstock, v);
        v.gridy++;
        panelstockitem.add(ltcost, v);
        v.gridy++;
        panelstockitem.add(ttcost, v);
        v.gridy++;
        panelstockitem.add(ltsold, v);
        v.gridy++;
        panelstockitem.add(ttsold, v);
        v.gridy++;
        panelstockitem.add(ltcostsold, v);
        v.gridy++;
        panelstockitem.add(ttcostsold, v);
        v.gridy++;
        panelstockitem.add(lprofit, v);
        v.gridy++;
        panelstockitem.add(tprofit, v);
        v.gridy++;
        panelstockitem.add(lloss, v);
        v.gridy++;
        panelstockitem.add(tloss, v);
        v.insets = new Insets(50, 0, 0, 0);
        v.gridy++;
        panelstockitem.add(ladvanced, v);
        v.insets = new Insets(0, 0, 0, 0);
        v.gridy++;
        panelstockitem.add(tadvanced, v);
        v.insets = new Insets(10, 0, 0, 0);
        v.gridy++;
        panelstockitem.add(bdelete, v);
        panelstockitem.setBorder(new TitledBorder(""));

        //seeting to main panelmain
        panelmain.add("West", panelstcok);
        panelmain.add("Center", paneltable);
        panelmain.add("East", panelstockitem);
        panelmain.setBorder(new TitledBorder(""));
        panelmain.setBackground(Color.blue.brighter());
        panelmain.revalidate();

        //actions setion
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
        bload.addActionListener(e -> {
            analyzingdata();
            storeanalyzer();
            uplaod();
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "Stock Analysis Finished Successfully" + "\n" + "Your Stock Is up to Date", "Stock Analysis Notification", JOptionPane.INFORMATION_MESSAGE, iconstock);
        });
        bsearch.addActionListener(e -> {
            searchengine();
        });
        bdelete.addActionListener(e -> {
            String serialgot = tadvanced.getText();
            try {
                if (serialgot.equalsIgnoreCase("")) {
                    JOptionPane.showMessageDialog(null, "Please Enter The Serial Number", "Notification", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    if (serialgot.equalsIgnoreCase("all")) {

                        Connection con = DBConnector.getConnection();
                        String[] option = {"Yes", "No"};
                        int selloption = JOptionPane.showOptionDialog(null, "Proceeding in Deleting Will Delete The Entire Stock" + " \n" + "This should only be done if the stock is never needed" + "\n\n" + "If deleted no saving will be done to the Archives", "Deletion Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
                        if (selloption == 0) {
                            String sqldelete = "DELETE FROM store";
                            String sqldelete2 = "DELETE FROM tablecritical";
                            String sqldelete3 = "DELETE FROM tablesell";
                            String sqldelete4 = "DELETE FROM tablestockin";
                            prs = con.prepareStatement(sqldelete);
                            prs2 = con.prepareStatement(sqldelete2);
                            prs3 = con.prepareStatement(sqldelete3);
                            prs4 = con.prepareStatement(sqldelete4);
                            prs.execute();
                            prs2.execute();
                            prs3.execute();
                            prs4.execute();
                            if (prs != null && prs2 != null && prs3 != null && prs4 != null) {
                                JOptionPane.showMessageDialog(null, "Entire Stock Deleted Successful", "Deletion Confirmation", JOptionPane.INFORMATION_MESSAGE, icon);
                                rs.close();
                                prs.close();
                                prs2.close();
                                prs3.close();
                                prs4.close();
                                prs5.close();
                                con.close();

                                //setting to null
                                tname.setText(null);
                                tserial.setText(null);
                                ttstock.setText(null);
                                ttcost.setText(null);
                                ttsold.setText(null);
                                ttcostsold.setText(null);
                                tprofit.setText(null);
                                tloss.setText(null);
                                tadvanced.setText(null);

                                //method for reloading details
                                storeanalyzer();
                                analyzingdata();
                                uplaod();
                            } else {
                                JOptionPane.showMessageDialog(null, tname.getText() + " " + "Deletion Failed", "Error Message", JOptionPane.ERROR_MESSAGE);
                                rs.close();
                                prs.close();
                                con.close();
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Deletion Cancelled", "Notification", JOptionPane.INFORMATION_MESSAGE, icon);
                            rs.close();
                            prs.close();
                            con.close();
                        }
                    } else {

                        Connection con = DBConnector.getConnection();
                        String[] option = {"Yes", "No"};
                        int selloption = JOptionPane.showOptionDialog(null, "Proceeding in Deleting Will Delete The Entire Stock Of The Item" + " " + tname.getText() + " " + "Details with Serial_Number" + " " + serialgot + "\n" + "This should only be done if the product's stock is over" + "\n\n" + "If Deleted The Product Details Will Be Saved To The Archive For Future Reference", "Deletion Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
                        if (selloption == 0) {
                            String sqldelete = "DELETE FROM store WHERE Mserial = '" + tadvanced.getText() + "'";
                            String sqldelete2 = "DELETE FROM tablecritical WHERE Mserial = '" + tadvanced.getText() + "'";
                            String sqldelete3 = "DELETE FROM tablesell WHERE Mserial = '" + tadvanced.getText() + "'";
                            String sqldelete4 = "DELETE FROM tablestockin WHERE Mserial = '" + tadvanced.getText() + "'";
                            prs = con.prepareStatement(sqldelete);
                            prs2 = con.prepareStatement(sqldelete2);
                            prs3 = con.prepareStatement(sqldelete3);
                            prs4 = con.prepareStatement(sqldelete4);
                            prs.execute();
                            prs2.execute();
                            prs3.execute();
                            prs4.execute();
                            if (prs != null && prs2 != null && prs3 != null && prs4 != null) {
                                //insert into past records
                                String sqladd = "INSERT INTO pastrecords(Mname,Mserial,StockIn,StockInCost,StockOut,StockOutCost,Profit,Loss,LastEdited) VALUES (?,?,?,?,?,?,?,?,?)";
                                prs5 = con.prepareStatement(sqladd);

                                //setting to database
                                prs5.setString(1, tname.getText());
                                prs5.setString(2, tserial.getText());
                                prs5.setDouble(3, Double.parseDouble(ttstock.getText()));
                                prs5.setDouble(4, Double.parseDouble(ttcost.getText()));
                                prs5.setDouble(5, Double.parseDouble(ttsold.getText()));
                                prs5.setDouble(6, Double.parseDouble(ttcostsold.getText()));
                                prs5.setDouble(7, Double.parseDouble(tprofit.getText()));
                                prs5.setDouble(8, Double.parseDouble(tloss.getText()));
                                prs5.setString(9, fileeditedlast);

                                prs5.execute();
                                JOptionPane.showMessageDialog(null, "Entire Details of " + tname.getText() + " Deleted Successful", "Deletion Confirmation", JOptionPane.INFORMATION_MESSAGE, icon);
                                rs.close();
                                prs.close();
                                prs2.close();
                                prs3.close();
                                prs4.close();
                                prs5.close();
                                con.close();

                                //setting to null
                                tname.setText(null);
                                tserial.setText(null);
                                ttstock.setText(null);
                                ttcost.setText(null);
                                ttsold.setText(null);
                                ttcostsold.setText(null);
                                tprofit.setText(null);
                                tloss.setText(null);
                                tadvanced.setText(null);

                                //method for reloading details
                                storeanalyzer();
                                analyzingdata();
                                uplaod();
                            } else {
                                JOptionPane.showMessageDialog(null, tname.getText() + " " + "Deletion Failed", "Error Message", JOptionPane.ERROR_MESSAGE);
                                rs.close();
                                prs.close();
                                con.close();
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Deletion Cancelled", "Notification", JOptionPane.INFORMATION_MESSAGE, icon);
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

        storeanalyzer();
        uplaod();
        //end of actions
        //setting frame
        Fstock = new JFrame("Pharmacy System");
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("nimbusBase", Color.blue);
        } catch (Exception c) {
        }
        Fstock.setUndecorated(true);
        Fstock.setIconImage(iconimage);
        Fstock.add(panelmain);
        Fstock.setVisible(true);
        //Fstore.setSize(1400, 780);
        Fstock.setSize(screenSize.width, screenSize.height);
        Fstock.revalidate();
        Fstock.pack();
        Fstock.revalidate();
        Fstock.setLocationRelativeTo(null);
        //Fstore.setResizable(false);
        Fstock.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        //frame state to make components responsive
        Fstock.addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                // minimized
                if ((e.getNewState() & Frame.ICONIFIED) == Frame.ICONIFIED) {
                    Fstock.revalidate();
                    Fstock.pack();
                    Fstock.revalidate();
                    Fstock.setLocationRelativeTo(null);
                } // maximized
                else if ((e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                    Fstock.revalidate();
                    Fstock.pack();
                    Fstock.revalidate();
                    Fstock.setLocationRelativeTo(null);
                }
            }
        });

        bback.addActionListener(e -> {
            Fstock.setVisible(false);
            AnalysisType antype = new AnalysisType();
            antype.ChooseSection();
        });

    }
}
