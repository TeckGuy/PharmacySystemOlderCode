package sellingstore;

import com.toedter.calendar.JDateChooser;
import dbconnector.DBConnector;
import login.Sections;
import sun.applet.Main;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.net.URL;
import java.sql.*;
import javax.swing.text.JTextComponent;

public class SellAnalysis {

    //dimension setting
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    //getting images
    URL url1 = Main.class.getResource("/img/logo.png");
    URL url2 = Main.class.getResource("/img/search.png");
    URL url3 = Main.class.getResource("/img/solditems.png");

    //setting images
    ImageIcon imagesearch = new ImageIcon(url2);
    ImageIcon imageitems = new ImageIcon(url3);

    //setting ma default image icon to my frames
    Image iconimage = new ImageIcon(url1).getImage();

    JLabel llogo1, llogo2, llogo3, ltotalsale, ltitle, ltotalamount, ltitle2;
    JTextField tsearch, ttotalsale, ttotalamount;
    JTable table;
    JDateChooser dateexpry;
    JButton bsearch, bload, bback;

    JPanel panellogo = new JPanel(new GridBagLayout());
    JPanel panelshow = new JPanel(new GridBagLayout());
    JPanel paneltable = new JPanel(new GridBagLayout());
    JPanel panelmain = new JPanel(new BorderLayout(0, 0));

    JFrame Fsells = new JFrame();

    //set variables
    String[] values2 = new String[]{"Product_Name", "Serial_Number", "Total_Quantity", "Buying_Price", "Selling_Price", "Cash Paid", "Change Given", "Total_Cost Sell", "Invoice_NO", "Sold On"};

    //database connectors
    Statement stmt = null;
    ResultSet rs, rs1, rs2, rs3, rs4, rs5, rs6 = null;
    PreparedStatement prs = null;

    //calling class dbconnect
    DBConnector xamppfailure = new DBConnector();

    //method for search
    private void searchsell() {
        String medate = ((JTextComponent) dateexpry.getDateEditor().getUiComponent()).getText();
        String valuesearch = tsearch.getText();
        if (valuesearch.equalsIgnoreCase("") || valuesearch.equalsIgnoreCase(null) || medate == null) {
            JOptionPane.showMessageDialog(null, "Please Enter The Record Tax Invoice Number/Name/Identity/Select Date or Use Both For Search", "Selling Message", JOptionPane.INFORMATION_MESSAGE);
        } else {
            DefaultTableModel pharmacymodel = new DefaultTableModel();
            //dm.addColumn("Medicine Name");
            pharmacymodel.setColumnIdentifiers(values2);
            String fetchrecord = "SELECT * FROM tablesell WHERE Mname  = '" + tsearch.getText() + "' || Mserial  = '" + tsearch.getText() + "' || Msolddate  = '" + medate + "' || Invoice ='" + tsearch.getText() + "'";
            try {
                Connection con = DBConnector.getConnection();
                stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                rs = stmt.executeQuery(fetchrecord);
                if (rs.next()) {
                    do {
                        String cname = rs.getString("Mname");
                        String cidentity = rs.getString("Mserial");
                        double tcost = rs.getDouble("Mtotalquantity");
                        String ccost = String.format("%.2f", tcost);
                        double cpay = rs.getDouble("Bcost");
                        String spay = String.format("%.2f", cpay);
                        double balance = rs.getDouble("Mcost");
                        String sbalance = String.format("%.2f", balance);
                        double balance1 = rs.getDouble("Mtotalcost");
                        String sbalance1 = String.format("%.2f", balance1);
                        double bpaid = rs.getDouble("CashPaid");
                        String sbpaid = String.format("%.2f", bpaid);
                        double change = rs.getDouble("ChangePaid");
                        String schange = String.format("%.2f", change);
                        String pinvoice = rs.getString("Invoice");
                        String cedited = rs.getString("LastEdited");

                        pharmacymodel.addRow(new String[]{cname, cidentity, ccost, spay, sbalance, sbpaid, schange, sbalance1, pinvoice, cedited});

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
                    uploadsells();
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
                String sqlqsum1 = "SELECT SUM(Mtotalquantity) STOREQ FROM tablesell WHERE Mname = '" + tsearch.getText() + "' || Mserial = '" + tsearch.getText() + "' || Msolddate  = '" + medate + "'|| Invoice ='" + tsearch.getText() + "'";
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
                JOptionPane.showMessageDialog(null, "Connection Failure" + "\n" + x, "Error Message", JOptionPane.ERROR_MESSAGE);
                //System.exit(0);
            }

            //code for fetching all the sum of cost in paymentrecords
            try {
                Connection con = DBConnector.getConnection();
                String sqlcsum2 = "SELECT SUM(Mtotalcost) STORETC FROM tablesell WHERE Mname = '" + tsearch.getText() + "' || Mserial = '" + tsearch.getText() + "' || Msolddate  = '" + medate + "' || Invoice ='" + tsearch.getText() + "'";
                prs = con.prepareStatement(sqlcsum2);
                rs2 = prs.executeQuery();
                if (rs2.next()) {
                    double paymentsfound = rs2.getDouble("STORETC");
                    String sspayments = String.format("%.2f", paymentsfound);

                    ttotalsale.setText(sspayments);
                    rs2.close();
                    prs.close();
                    stmt.close();
                    con.close();
                }
            } catch (SQLException x) {
                JOptionPane.showMessageDialog(null, "Connection Failure", "Error Message", JOptionPane.ERROR_MESSAGE);
                //System.exit(0);
            }

            //code for fetching all the sum of stock in paymentrecords using the AND DECISION MAKING
            try {
                Connection con = DBConnector.getConnection();
                String sqlqsum1 = "SELECT SUM(Mtotalquantity) STOREQ FROM tablesell WHERE (Mname = '" + tsearch.getText() + "' || Mserial = '" + tsearch.getText() + "' || Invoice ='" + tsearch.getText() + "') && Msolddate  = '" + medate + "'";
                prs = con.prepareStatement(sqlqsum1);
                rs4 = prs.executeQuery();
                if (rs4.next()) {
                    double costfound = rs4.getDouble("STOREQ");
                    String sstock = String.format("%.2f", costfound);

                    ttotalamount.setText(sstock);
                    rs4.close();
                    stmt.close();
                    con.close();
                }
            } catch (SQLException x) {
                JOptionPane.showMessageDialog(null, "Connection Failure", "Error Message", JOptionPane.ERROR_MESSAGE);
                //System.exit(0);
            }

            //code for fetching all the sum of cost in paymentrecords
            try {
                Connection con = DBConnector.getConnection();
                String sqlcsum2 = "SELECT SUM(Mtotalcost) STORETC FROM tablesell WHERE (Mname = '" + tsearch.getText() + "' || Mserial = '" + tsearch.getText() + "' || Invoice ='" + tsearch.getText() + "') && Msolddate  = '" + medate + "'";
                prs = con.prepareStatement(sqlcsum2);
                rs5 = prs.executeQuery();
                if (rs5.next()) {
                    double paymentsfound = rs5.getDouble("STORETC");
                    String sspayments = String.format("%.2f", paymentsfound);

                    ttotalsale.setText(sspayments);
                    rs5.close();
                    prs.close();
                    stmt.close();
                    con.close();
                }
            } catch (SQLException x) {
                JOptionPane.showMessageDialog(null, "Connection Failure", "Error Message", JOptionPane.ERROR_MESSAGE);
                //System.exit(0);
            }
        }
    }

    //method to upload all sold items
    private void uploadsells() {
        DefaultTableModel pharmacymodel = new DefaultTableModel();
        //dm.addColumn("Medicine Name");
        pharmacymodel.setColumnIdentifiers(values2);
        String fetchrecord = "SELECT * FROM tablesell";
        try {
            Connection con = DBConnector.getConnection();
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(fetchrecord);
            if (rs.next()) {
                do {
                    String cname = rs.getString("Mname");
                    String cidentity = rs.getString("Mserial");
                    double tcost = rs.getDouble("Mtotalquantity");
                    String ccost = String.format("%.2f", tcost);
                    double cpay = rs.getDouble("Bcost");
                    String spay = String.format("%.2f", cpay);
                    double balance = rs.getDouble("Mcost");
                    String sbalance = String.format("%.2f", balance);
                    double balance1 = rs.getDouble("Mtotalcost");
                    String sbalance1 = String.format("%.2f", balance1);
                    double bpaid = rs.getDouble("CashPaid");
                    String sbpaid = String.format("%.2f", bpaid);
                    double change = rs.getDouble("ChangePaid");
                    String schange = String.format("%.2f", change);
                    String pinvoice = rs.getString("Invoice");
                    String cedited = rs.getString("LastEdited");

                    pharmacymodel.addRow(new String[]{cname, cidentity, ccost, spay, sbalance, sbpaid, schange, sbalance1, pinvoice, cedited});

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
                JOptionPane.showMessageDialog(null, "No Sold Records In The Store For Analysis", "Notification", JOptionPane.INFORMATION_MESSAGE);
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
            String sqlqsumall = "SELECT SUM(Mtotalquantity) STOREQ FROM tablesell";
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
            String sqlcsumall2 = "SELECT SUM(Mtotalcost) STORETC FROM tablesell";
            prs = con.prepareStatement(sqlcsumall2);
            rs2 = prs.executeQuery();
            if (rs2.next()) {
                double paymentsfound = rs2.getDouble("STORETC");
                String sspayments = String.format("%.2f", paymentsfound);

                ttotalsale.setText(sspayments);
                rs2.close();
                prs.close();
                stmt.close();
                con.close();
            }
        } catch (SQLException x) {
            JOptionPane.showMessageDialog(null, "Database Connection Failure", "Error Message", JOptionPane.ERROR_MESSAGE);
            //System.exit(0);
        }
    }

    public void stocksell() {
        //date code
        dateexpry = new JDateChooser();
        dateexpry.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dateexpry.setPreferredSize(new Dimension(200, 35));
        dateexpry.setDateFormatString("yyyy/MM/dd");
        dateexpry.setFont(new Font("Tahoma", Font.PLAIN, 12));

        //tables
        table = new JTable();
        // this enables horizontal scroll barz
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.setIntercellSpacing(new Dimension(20, 20));
        //table.setPreferredScrollableViewportSize(new Dimension(960, 570));
        table.setPreferredScrollableViewportSize(new Dimension((int) (screenSize.width / 1.47), (int) (screenSize.height / 1.40)));
        table.revalidate();
        table.setFont(new Font("Tahoma", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        llogo1 = new JLabel(imageitems);
        llogo2 = new JLabel(imageitems);
        llogo3 = new JLabel(imageitems);
        ltitle2 = new JLabel("DAILY SALES");
        ltitle2.setFont(new Font("Tahoma", Font.BOLD, 20));
        ltitle = new JLabel("SALES ANALYSIS");
        ltitle.setFont(new Font("Tahoma", Font.BOLD, 15));
        ltotalamount = new JLabel("Quantity Sold[Numbers]");
        ltotalamount.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ttotalamount = new JTextField(15);
        ttotalamount.setBackground(Color.LIGHT_GRAY);
        ttotalamount.setFont(new Font("Tahoma", Font.PLAIN, 13));
        // ttotalamount.setEditable(false);
        ltotalsale = new JLabel("Total Sales[KSH]");
        ltotalsale.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ttotalsale = new JTextField(15);
        ttotalsale.setBackground(Color.LIGHT_GRAY);
        ttotalsale.setFont(new Font("Tahoma", Font.PLAIN, 13));
        // ttotalsale.setEditable(false);
        tsearch = new JTextField(20);
        tsearch.setFont(new Font("Tahoma", Font.PLAIN, 15));
        tsearch.setToolTipText("Search Record By Name/Serial Number/Tax Invoice Number");

        bsearch = new JButton(imagesearch);
        bsearch.setBackground(Color.LIGHT_GRAY);
        bsearch.setToolTipText("Search Past Record");
        bsearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bload = new JButton("RELOAD SOLD ITEMS");
        bload.setBackground(Color.GREEN.darker());
        bload.setToolTipText("reload sells");
        bload.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bload.setFont(new Font("Tahoma", Font.BOLD, 15));
        bback = new JButton("BACK");
        bback.setFont(new Font("Serif", Font.BOLD + Font.ITALIC, 15));
        bback.setBackground(Color.LIGHT_GRAY);
        bback.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bback.setFont(new Font("Tahoma", Font.BOLD, 15));

        //setting paneltable
        GridBagConstraints v = new GridBagConstraints();
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 50, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        panellogo.add(bback, v);
        v.gridy++;
        v.insets = new Insets(0, 0, 0, 0);
        panellogo.add(llogo1, v);
        v.gridy++;
        panellogo.add(ltitle, v);
        panellogo.setBorder(new TitledBorder(""));

        //paneltable
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        paneltable.add(ltitle2, v);
        v.gridy++;
        v.anchor = GridBagConstraints.WEST;
        v.insets = new Insets(0, 0, 0, 0);
        paneltable.add(bload, v);
        v.anchor = GridBagConstraints.EAST;
        v.insets = new Insets(0, 0, 0, 326);
        paneltable.add(dateexpry, v);
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

        //panel show
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        panelshow.add(llogo2, v);
        v.gridy++;
        panelshow.add(ltotalamount, v);
        v.gridy++;
        panelshow.add(ttotalamount, v);
        v.gridy++;
        panelshow.add(ltotalsale, v);
        v.gridy++;
        panelshow.add(ttotalsale, v);
        v.gridy++;
        panelshow.add(llogo3, v);
        panelshow.setBorder(new TitledBorder(""));
        panelshow.revalidate();

        //panelmain
        panelmain.add("West", panellogo);
        panelmain.add("Center", paneltable);
        panelmain.add("East", panelshow);
        panelmain.setBorder(new TitledBorder(""));
        panelmain.setBackground(Color.blue.brighter());
        panelmain.revalidate();

        //method for uploading
        uploadsells();

        //setting frame
        Fsells = new JFrame("Pharmacy System");
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("nimbusBase", Color.blue);
        } catch (Exception c) {
        }
        Fsells.setUndecorated(true);
        Fsells.setIconImage(iconimage);
        Fsells.add(panelmain);
        Fsells.setVisible(true);
        //Fstore.setSize(1400, 780);
        Fsells.setSize(screenSize.width, screenSize.height);
        Fsells.revalidate();
        Fsells.pack();
        Fsells.revalidate();
        Fsells.setLocationRelativeTo(null);
        //Fstore.setResizable(false);
        Fsells.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        //frame state to make components responsive
        Fsells.addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                // minimized
                if ((e.getNewState() & Frame.ICONIFIED) == Frame.ICONIFIED) {
                    Fsells.revalidate();
                    Fsells.pack();
                    Fsells.revalidate();
                    Fsells.setLocationRelativeTo(null);
                } // maximized
                else if ((e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                    Fsells.revalidate();
                    Fsells.pack();
                    Fsells.revalidate();
                    Fsells.setLocationRelativeTo(null);
                }
            }
        });

        //methods start
        bsearch.addActionListener(e -> {
            searchsell();

        });
        bload.addActionListener(e -> {
            uploadsells();

        });
        bback.addActionListener(e -> {
            Fsells.setVisible(false);
            Sections ds = new Sections();
            ds.Operations();
        });

    }
}
