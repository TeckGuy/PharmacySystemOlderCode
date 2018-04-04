package store;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import dbconnector.DBConnector;

import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import javax.swing.text.DefaultCaret;

import login.Sections;
import sun.applet.Main;

public class MedicineStore {

    //getting images
    URL url1 = Main.class.getResource("/img/logo.png");
    URL url2 = Main.class.getResource("/img/store1.png");
    URL url3 = Main.class.getResource("/img/search.png");
    URL url4 = Main.class.getResource("/img/store.png");
    URL url5 = Main.class.getResource("/img/updatepay.png");
    URL url6 = Main.class.getResource("/img/delete.png");
    //setting images
    ImageIcon imagestorelogo = new ImageIcon(url2);
    ImageIcon imagestoresearch = new ImageIcon(url3);
    ImageIcon imagestore = new ImageIcon(url4);
    ImageIcon imagestorelogo1 = new ImageIcon(url1);
    ImageIcon imageupdate = new ImageIcon(url5);
    ImageIcon imagedelete = new ImageIcon(url6);

    //setting ma default image icon to my frames
    Image iconimage = new ImageIcon(url1).getImage();

    //images for joptionpanes
    final ImageIcon icon = new ImageIcon(url6);
    final ImageIcon icon2 = new ImageIcon(url5);

    //components
    JLabel llogo, llogo1, llogo2, llogo3, lstock, lcost, ltotalcost, lname, lserialnumber, ledate, lsection, lprescription, ldistributer, lcategory, ltcost, ltstock;
    JTable table;
    JTextField tstoresearch, tstock, tcost, ttotalcost, tname, tserialnumber, tedate, tsection, tprescription, tdistributer, tcategory, ttcost, ttstock;
    JButton sbsearch, sbload, sbedit, supadete, sdelete, bback;

    JTextArea treport;

    //panels
    JPanel panelstoremenu = new JPanel(new GridBagLayout());
    JPanel panelstoretable = new JPanel(new GridBagLayout());
    JPanel panelstorecard = new JPanel(new CardLayout(0, 0));
    JPanel panelstorelogo = new JPanel(new GridBagLayout());
    JPanel panelstoremain = new JPanel(new BorderLayout(0, 0));

    //frame
    JFrame Fstore = new JFrame();

    //database connectors
    Statement stmt = null;
    ResultSet rs = null;
    PreparedStatement prs = null;

    //set variables
    String[] values = new String[]{"Product_Name", "Serial Number", "Available Stock", "Cost/Item", "Total Cost", "Expiry Date", "Under/Section", "Prescription", "Distributer", "Category", "Report", "Last Edited On"};
    String cost, quantity, mname, medate, mserialnumber, mpres, mdistributer, mcategory, mreport, mtotal;
    double cost2, total, quaty, total2;

    //dimension setting
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    //date
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy, hh:mm:ss.SSS a");
    private static final DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    LocalDateTime now = LocalDateTime.now();
    String fileeditedlast = dtf.format(now);
    String datereached = dtf2.format(now);
    java.util.Date today = new java.util.Date();
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    String format = formatter.format(today);

    //calling class dbconnect
    DBConnector xamppfailure = new DBConnector();

    //method for getting tablecont
    private void tablecont() {
        String storename = table.getValueAt(table.getSelectedRow(), 0).toString();
        String storeserial = table.getValueAt(table.getSelectedRow(), 1).toString();
        double storestock = Double.parseDouble(String.valueOf(table.getValueAt(table.getSelectedRow(), 2)));
        double storecost = Double.parseDouble(String.valueOf(table.getValueAt(table.getSelectedRow(), 3)));
        double storetotalcost = Double.parseDouble(String.valueOf(table.getValueAt(table.getSelectedRow(), 4)));
        String storeedate = table.getValueAt(table.getSelectedRow(), 5).toString();
        String storesection = table.getValueAt(table.getSelectedRow(), 6).toString();
        String storeprescription = table.getValueAt(table.getSelectedRow(), 7).toString();
        String storedistributer = table.getValueAt(table.getSelectedRow(), 8).toString();
        String storecategory = table.getValueAt(table.getSelectedRow(), 9).toString();
        String storeport = table.getValueAt(table.getSelectedRow(), 10).toString();

        //setting values got from table
        tname.setText(storename);
        tserialnumber.setText(storeserial);
        tstock.setText(String.format("%.2f", storestock));
        tcost.setText(String.format("%.2f", storecost));
        ttotalcost.setText(String.format("%.2f", storetotalcost));
        tedate.setText(storeedate);
        tsection.setText(storesection);
        tprescription.setText(storeprescription);
        tdistributer.setText(storedistributer);
        tcategory.setText(storecategory);
        treport.setText(storeport);

        //notification for expiry
        tedate.setBackground(Color.lightGray);
        tedate.setForeground(Color.black);
        tstock.setBackground(Color.white);
        tstock.setForeground(Color.black);
        try {
            Connection con = DBConnector.getConnection();
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            //String sql = "SELECT Mname,Msection,Mprescription,Mcost FROM store WHERE Mname = '" + tsearch.getText() + "' ";
            String sqldatereached = "SELECT Medate,Mquantity FROM store WHERE Mserial = '" + storeserial + "' ";
            rs = stmt.executeQuery(sqldatereached);
            rs.next();
            //store date in variable
            java.util.Date fetcheddate = rs.getDate("Medate");
            double quantityfound = rs.getDouble("Mquantity");
            if (today.after(fetcheddate)) {
                tedate.setBackground(Color.red.darker());
                tedate.setForeground(Color.white);
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "The Product has Expired" + "\n" + "Expiry Date" + " " + fetcheddate, "Expiring Date", JOptionPane.INFORMATION_MESSAGE);
                rs.close();
                stmt.close();
                con.close();
            } else {
                rs.close();
                stmt.close();
                con.close();
            }
            if (quantityfound <= 10) {
                tstock.setBackground(Color.red.darker());
                tstock.setForeground(Color.white);
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "The Stock Is Getting Finished Re-Order" + "\n" + quantityfound + " " + "Items Remaining Only", "Stock Notification", JOptionPane.INFORMATION_MESSAGE);
                rs.close();
                stmt.close();
                con.close();
            } else {
                rs.close();
                stmt.close();
                con.close();
            }
        } catch (SQLException x) {
            xamppfailure.getCon();
        }
        //end of notication
    }

    //method for getting sum of the cost ontyping
    private void sumStore() {
        cost = tcost.getText();
        quantity = tstock.getText();
        if (cost.equalsIgnoreCase("") && quantity.equalsIgnoreCase("")) {
            //JOptionPane.showMessageDialog(null, "No Cost or Quantity given", "Error Message", JOptionPane.ERROR_MESSAGE);
        } else {
            cost2 = Double.parseDouble(cost);
            quaty = Double.parseDouble(quantity);
            total = quaty * cost2;
            //String costresult = Double.toString(total);
            ttotalcost.setText(String.format("%.2f", total));
        }
    }

    //method to reupload details after update or deletion
    private void reupload() {
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
                    double drugstock = rs.getDouble("Mquantity");
                    String sstock = String.format("%.2f", drugstock);
                    double drugcost = rs.getDouble("Mcost");
                    String scost = String.format("%.2f", drugcost);
                    double drugtotalcost = rs.getDouble("Mtotalcost");
                    String stcost = String.format("%.2f", drugtotalcost);
                    String drugdate = rs.getString("Medate");
                    String drugsection = rs.getString("Msection");
                    String drugp = rs.getString("Mprescription");
                    String drugdistributer = rs.getString("Mdistributer");
                    String drugcategory = rs.getString("Mcategory");
                    String drugreport = rs.getString("Mreport");
                    String drugredited = rs.getString("LastEdited");

                    pharmacymodel.addRow(new String[]{drugname, drugserial, sstock, scost, stcost, drugdate, drugsection, drugp, drugdistributer, drugcategory, drugreport, drugredited});

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
                JOptionPane.showMessageDialog(null, "No Stock In The Store", "Notification", JOptionPane.INFORMATION_MESSAGE);
                rs.close();
                stmt.close();
                con.close();
            }
        } catch (SQLException x) {
            JOptionPane.showMessageDialog(null, "Database Connection Failure\nPlease Rerun the application", "Error Message", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        //code for fetching all the sum of stock in store
        try {
            Connection con = DBConnector.getConnection();
            String sqlqsum = "SELECT SUM(Mquantity) STOREQ FROM store";
            prs = con.prepareStatement(sqlqsum);
            rs = prs.executeQuery();
            if (rs.next()) {
                double quantityfound = rs.getDouble("STOREQ");
                String sstock = String.format("%.2f", quantityfound);

                ttstock.setText(sstock);
                rs.close();
                stmt.close();
                con.close();
            }
        } catch (SQLException x) {
            JOptionPane.showMessageDialog(null, "Connection Failure" + "\n" + x, "Error Message", JOptionPane.ERROR_MESSAGE);
            //System.exit(0);
        }

        //code for fetching all the sum of cost in store
        try {
            Connection con = DBConnector.getConnection();
            String sqlcsum = "SELECT SUM(Mtotalcost) STORETC FROM store";
            prs = con.prepareStatement(sqlcsum);
            rs = prs.executeQuery();
            if (rs.next()) {
                double costfound = rs.getDouble("STORETC");
                String sscost = String.format("%.2f", costfound);

                ttcost.setText(sscost);
                rs.close();
                stmt.close();
                con.close();
            }
        } catch (SQLException x) {
            JOptionPane.showMessageDialog(null, "Connection Failure" + "\n" + x, "Error Message", JOptionPane.ERROR_MESSAGE);
            //System.exit(0);
        }
    }

    public void MStore() {

        table = new JTable();
        // this enables horizontal scroll bar
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(20, 20));
        // table.setPreferredScrollableViewportSize(new Dimension(930, 570));
        table.setPreferredScrollableViewportSize(new Dimension((int) (screenSize.width / 1.47), (int) (screenSize.height / 1.38)));
        table.revalidate();
        table.setFont(new Font("Tahoma", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        llogo = new JLabel(imagestorelogo);
        llogo1 = new JLabel(imagestorelogo1);
        llogo2 = new JLabel(imagestore);
        llogo3 = new JLabel(imagestore);

        llogo = new JLabel("PHARMACY STORE");
        llogo.setFont(new Font("Tahoma", Font.BOLD, 20));

        lname = new JLabel("Product Name");
        lname.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lserialnumber = new JLabel("Serial Number");
        lserialnumber.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lstock = new JLabel("Total stock Of Item");
        lstock.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lcost = new JLabel("Cost/Item[KSH]");
        lcost.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lsection = new JLabel("Under Section");
        lsection.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ledate = new JLabel("Expiry Date[yyyy/MM/dd]");
        ledate.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ltotalcost = new JLabel("Total Cost Of Item (KSH)");
        ltotalcost.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lprescription = new JLabel("Prescription");
        lprescription.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ldistributer = new JLabel("Distributed By");
        ldistributer.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lcategory = new JLabel("Product Category");
        lcategory.setFont(new Font("Tahoma", Font.PLAIN, 15));

        ltstock = new JLabel("Total Stock");
        ltstock.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ltcost = new JLabel("Total Cost (KSH)");
        ltcost.setFont(new Font("Tahoma", Font.PLAIN, 15));

        treport = new JTextArea(5, 16);
        treport.setLineWrap(true);
        DefaultCaret caret = (DefaultCaret) treport.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scrollpane = new JScrollPane();
        scrollpane.add(treport);
        scrollpane.setViewportView(treport);
        //treport.append("");
        //treport.setCaretPosition(treport.getDocument().getLength());
        treport.setWrapStyleWord(true);
        treport.setFont(new Font("Tahoma", Font.PLAIN, 13));
        treport.setToolTipText("Write on the little usage of the Product");

        tstoresearch = new JTextField(25);
        tstoresearch.setFont(new Font("Tahoma", Font.PLAIN, 15));
        tstoresearch.setToolTipText("Search Product By Name/Arragement/Distributers/Category");

        tname = new JTextField(15);
        tname.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tname.setBackground(Color.LIGHT_GRAY);
        tname.setEditable(false);
        tserialnumber = new JTextField(15);
        tserialnumber.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tserialnumber.setBackground(Color.LIGHT_GRAY);
        tserialnumber.setEditable(false);
        tstock = new JTextField(15);
        tstock.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tstock.setBackground(Color.LIGHT_GRAY);
        tstock.setEditable(false);
        tcost = new JTextField(15);
        tcost.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tcost.setEditable(false);
        tcost.setBackground(Color.LIGHT_GRAY);
        tsection = new JTextField(15);
        tsection.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tsection.setEditable(false);
        tsection.setBackground(Color.LIGHT_GRAY);
        tedate = new JTextField(15);
        tedate.setBackground(Color.LIGHT_GRAY);
        tedate.setEditable(false);
        tedate.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttotalcost = new JTextField(15);
        ttotalcost.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttotalcost.setEditable(false);
        ttotalcost.setBackground(Color.LIGHT_GRAY);
        tprescription = new JTextField(15);
        tprescription.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tdistributer = new JTextField(15);
        tdistributer.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tdistributer.setEditable(false);
        tdistributer.setBackground(Color.LIGHT_GRAY);
        tcategory = new JTextField(15);
        tcategory.setFont(new Font("Tahoma", Font.PLAIN, 13));

        ttstock = new JTextField(10);
        // ttstock.setEditable(false);
        ttstock.setBackground(Color.LIGHT_GRAY);
        ttstock.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttcost = new JTextField(10);
        // ttcost.setEditable(false);
        ttcost.setBackground(Color.LIGHT_GRAY);
        ttcost.setFont(new Font("Tahoma", Font.PLAIN, 13));

        //setting buttons
        supadete = new JButton("Update Details");
        supadete.setBackground(Color.lightGray);
        supadete.setForeground(Color.GREEN.darker());
        supadete.setToolTipText("Update Product Details");
        supadete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        supadete.setFont(new Font("Tahoma", Font.BOLD, 18));
        sdelete = new JButton(imagedelete);
        sdelete.setBackground(Color.red.brighter());
        sdelete.setToolTipText("Delete Product Details");
        sdelete.setCursor(new Cursor(Cursor.HAND_CURSOR));

        sbsearch = new JButton(imagestoresearch);
        sbsearch.setBackground(Color.LIGHT_GRAY);
        sbsearch.setToolTipText("Search Product");
        sbsearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sbload = new JButton("RELOAD STOCK");
        sbload.setFont(new Font("Tahoma", Font.BOLD, 15));
        sbload.setBackground(Color.green.darker());
        sbload.setToolTipText("Click To Load Available Stock");
        sbload.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sbedit = new JButton("Edit Data");
        sbedit.setFont(new Font("Serif", Font.BOLD + Font.ITALIC, 15));
        sbedit.setBackground(Color.LIGHT_GRAY);
        sbedit.setToolTipText("Select an item in the table and edit its contents");
        sbedit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bback = new JButton("BACK");
        bback.setFont(new Font("Tahoma", Font.BOLD, 15));
        bback.setBackground(Color.LIGHT_GRAY);
        bback.setCursor(new Cursor(Cursor.HAND_CURSOR));

        //setting panel table
        GridBagConstraints v = new GridBagConstraints();
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        panelstoretable.add(llogo, v);
        v.gridy++;
        v.anchor = GridBagConstraints.WEST;
        v.insets = new Insets(0, 0, 0, 0);
        panelstoretable.add(sbload, v);
        v.anchor = GridBagConstraints.EAST;
        v.insets = new Insets(0, 0, 0, 53);
        panelstoretable.add(tstoresearch, v);
        v.insets = new Insets(0, 0, 0, 0);
        panelstoretable.add(sbsearch, v);
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(15, 0, 0, 0);
        v.gridy++;
        panelstoretable.add(scrollPane, v);
        panelstoretable.setBorder(new TitledBorder(""));
        panelstoretable.revalidate();

        //panel menu
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        panelstoremenu.add(lname, v);
        v.gridy++;
        panelstoremenu.add(tname, v);
        v.gridy++;
        panelstoremenu.add(lserialnumber, v);
        v.gridy++;
        panelstoremenu.add(tserialnumber, v);
        v.gridy++;
        panelstoremenu.add(lstock, v);
        v.gridy++;
        panelstoremenu.add(tstock, v);
        v.gridy++;
        panelstoremenu.add(lcost, v);
        v.gridy++;
        panelstoremenu.add(tcost, v);
        v.gridy++;
        panelstoremenu.add(lsection, v);
        v.gridy++;
        panelstoremenu.add(tsection, v);
        v.gridy++;
        panelstoremenu.add(ledate, v);
        v.gridy++;
        panelstoremenu.add(tedate, v);
        v.gridy++;
        panelstoremenu.add(lprescription, v);
        v.gridy++;
        panelstoremenu.add(tprescription, v);
        v.gridy++;
        panelstoremenu.add(ldistributer, v);
        v.gridy++;
        panelstoremenu.add(tdistributer, v);
        v.gridy++;
        panelstoremenu.add(lcategory, v);
        v.gridy++;
        panelstoremenu.add(tcategory, v);
        v.gridy++;
        panelstoremenu.add(ltotalcost, v);
        v.gridy++;
        panelstoremenu.add(ttotalcost, v);
        v.insets = new Insets(8, 0, 0, 0);
        v.gridy++;
        panelstoremenu.add(scrollpane, v);
//        v.anchor = GridBagConstraints.WEST;
        v.insets = new Insets(10, 0, 0, 0);
        v.gridy++;
        panelstoremenu.add(supadete, v);
//        v.anchor = GridBagConstraints.EAST;
//        panelstoremenu.add(sdelete, v);
        panelstoremenu.setBorder(new TitledBorder(""));

        //panel store logo
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 100, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        panelstorelogo.add(bback, v);
        v.gridy++;
        v.insets = new Insets(0, 0, 0, 0);
        panelstorelogo.add(llogo2, v);
        v.gridy++;
        panelstorelogo.add(ltstock, v);
        v.gridy++;
        panelstorelogo.add(ttstock, v);
        v.gridy++;
        panelstorelogo.add(ltcost, v);
        v.gridy++;
        panelstorelogo.add(ttcost, v);
        v.gridy++;
        panelstorelogo.add(llogo3, v);
        panelstorelogo.setBorder(new TitledBorder(""));

        panelstoremain.add("West", panelstorelogo);
        panelstoremain.add("Center", panelstoretable);
        panelstoremain.add("East", panelstoremenu);
        panelstoremain.setBorder(new TitledBorder(""));
        panelstoremain.setBackground(Color.blue.brighter());
        panelstoremain.revalidate();

        //events and action start
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

        //methods for calculating total cost
        tcost.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                sumStore();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                sumStore();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        //loading data from database
        sbload.addActionListener((ActionEvent e) -> {
            //method for loading details
            reupload();
        });

        //serach code
        sbsearch.addActionListener(e -> {
            String storesearch = tstoresearch.getText();
            if (storesearch.equalsIgnoreCase("")) {
                JOptionPane.showMessageDialog(null, "Please Enter Either Of this:" + "\n" + "Product Serial Number" + "\n"
                        + "Product Name" + "\n" + "Product Arrangement " + "\n" + "Product Category " + "\n" + "Product Distributer" + "\n\n" + "\t\t"
                        + "For Search", "Store Message", JOptionPane.INFORMATION_MESSAGE);
            } else {
                DefaultTableModel pharmacymodel = new DefaultTableModel();
                //dm.addColumn("Medicine Name");
                pharmacymodel.setColumnIdentifiers(values);
                String fetchrecord = "SELECT * FROM store WHERE Mserial = '" + storesearch + "' || Mname = '" + storesearch + "' || Msection = '" + storesearch + "' || Mdistributer = '" + storesearch + "' || Mcategory = '" + storesearch + "'";
                try {
                    Connection con = DBConnector.getConnection();
                    stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    rs = stmt.executeQuery(fetchrecord);
                    if (rs.next()) {
                        do {
                            String drugname = rs.getString("Mname");
                            String drugserial = rs.getString("Mserial");
                            double drugstock = rs.getDouble("Mquantity");
                            String sstock = String.format("%.2f", drugstock);
                            double drugcost = rs.getDouble("Mcost");
                            String scost = String.format("%.2f", drugcost);
                            double drugtotalcost = rs.getDouble("Mtotalcost");
                            String stcost = String.format("%.2f", drugtotalcost);
                            String drugdate = rs.getString("Medate");
                            String drugsection = rs.getString("Msection");
                            String drugp = rs.getString("Mprescription");
                            String drugdistributer = rs.getString("Mdistributer");
                            String drugcategory = rs.getString("Mcategory");
                            String drugreport = rs.getString("Mreport");
                            String drugredited = rs.getString("LastEdited");

                            pharmacymodel.addRow(new String[]{drugname, drugserial, sstock, scost, stcost, drugdate, drugsection, drugp, drugdistributer, drugcategory, drugreport, drugredited});

                            //setting values got from table
                            tname.setText(drugname);
                            tserialnumber.setText(drugserial);
                            tstock.setText(String.format("%.2f", drugstock));
                            tcost.setText(String.format("%.2f", drugcost));
                            ttotalcost.setText(String.format("%.2f", drugtotalcost));
                            tedate.setText(drugdate);
                            tsection.setText(drugsection);
                            tprescription.setText(drugp);
                            tdistributer.setText(drugdistributer);
                            tcategory.setText(drugcategory);
                            treport.setText(drugreport);

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
                        JOptionPane.showMessageDialog(null, tstoresearch.getText() + " " + "Missing In The Store", "Notification", JOptionPane.INFORMATION_MESSAGE);
                        reupload();
                        rs.close();
                        stmt.close();
                        con.close();
                    }

                } catch (SQLException x) {
                    xamppfailure.getCon();
                }

                //code for fetching all the sum of stock in store
                try {
                    Connection con = DBConnector.getConnection();
                    String sqlqsum = "SELECT SUM(Mquantity) STOREQ FROM store WHERE  Mserial = '" + storesearch + "' || Mname = '" + storesearch + "' || Msection = '" + storesearch + "' || Mdistributer = '" + storesearch + "' || Mcategory = '" + storesearch + "'";
                    prs = con.prepareStatement(sqlqsum);
                    rs = prs.executeQuery();
                    if (rs.next()) {
                        double quantityfound = rs.getDouble("STOREQ");
                        String sstock = String.format("%.2f", quantityfound);

                        ttstock.setText(sstock);
                        rs.close();
                        stmt.close();
                        con.close();
                    }
                } catch (SQLException x) {
                    xamppfailure.getCon();
                }

                //code for fetching all the sum of cost in store
                try {
                    Connection con = DBConnector.getConnection();
                    String sqlcsum = "SELECT SUM(Mtotalcost) STORETC FROM store WHERE  Mserial = '" + storesearch + "' || Mname = '" + storesearch + "' || Msection = '" + storesearch + "' || Mdistributer = '" + storesearch + "' || Mcategory = '" + storesearch + "'";
                    prs = con.prepareStatement(sqlcsum);
                    rs = prs.executeQuery();
                    if (rs.next()) {
                        double costfound = rs.getDouble("STORETC");
                        String sscost = String.format("%.2f", costfound);

                        ttcost.setText(sscost);
                        rs.close();
                        stmt.close();
                        con.close();
                    }
                } catch (SQLException x) {
                    JOptionPane.showMessageDialog(null, "Connection Failure", "Error Message", JOptionPane.ERROR_MESSAGE);
                    //System.exit(0);
                }

            }
        });

        //updating code
        supadete.addActionListener(event -> {
            String storename = tname.getText();
            String storeserial = tserialnumber.getText();
            String storetcost = ttotalcost.getText();
            String storesection = tsection.getText();
            String storedate = tedate.getText();
            String storeprescription = tprescription.getText();
            String storedistributer = tdistributer.getText();
            String storecategory = tcategory.getText();
            String storereport = treport.getText();
            if (storename.equalsIgnoreCase("") && storeserial.equalsIgnoreCase("") && storetcost.equalsIgnoreCase("")
                    && storecategory.equalsIgnoreCase("") && storedate.equalsIgnoreCase("") && storedistributer.equalsIgnoreCase("")
                    && storeprescription.equalsIgnoreCase("") && storesection.equalsIgnoreCase("") && storereport.equalsIgnoreCase("")) {
                JOptionPane.showMessageDialog(null, "Please Select The Product From The Table To Update", "Notification", JOptionPane.INFORMATION_MESSAGE);
            } else {
                try {
                    Connection con = DBConnector.getConnection();
                    String sqlupdate = "UPDATE store set Mname = '" + storename + "', Mquantity = '" + quaty + "'"
                            + ",Mcost = '" + cost2 + "',Mtotalcost = '" + total + "',Medate = '" + storedate + "' "
                            + ",Msection = '" + storesection + "',Mprescription = '" + storeprescription + "',Mdistributer = '" + storedistributer + "' "
                            + ",Mcategory = '" + storecategory + "',Mreport = '" + storereport + "',LastEdited = '" + fileeditedlast + "' WHERE Mserial ='" + storeserial + "'";
                    String[] option = {"Yes", "No"};
                    int selloption = JOptionPane.showOptionDialog(null, "Proceed in Updating" + " " + storename + " " + "Details", "Update Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
                    if (selloption == 0) {
                        prs = con.prepareStatement(sqlupdate);
                        prs.execute();
                        if (prs != null) {
                            JOptionPane.showMessageDialog(null, storename + " " + "Updates Were Successful", "Update Confirmation", JOptionPane.INFORMATION_MESSAGE, icon2);
                            rs.close();
                            prs.close();
                            con.close();

                            //setting values to null
                            tname.setText(null);
                            tserialnumber.setText(null);
                            tstock.setText(null);
                            tcost.setText(null);
                            ttotalcost.setText(null);
                            tsection.setText(null);
                            tedate.setText(null);
                            tprescription.setText(null);
                            tdistributer.setText(null);
                            tcategory.setText(null);
                            treport.setText(null);

                            //method for reloading details
                            reupload();
                        } else {
                            JOptionPane.showMessageDialog(null, storename + " " + "Updates Failed", "Error Message", JOptionPane.ERROR_MESSAGE);
                            rs.close();
                            prs.close();
                            con.close();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Updates Cancelled", "Error Message", JOptionPane.ERROR_MESSAGE);
                        rs.close();
                        prs.close();
                        con.close();
                    }
                } catch (SQLException x) {
                    xamppfailure.getCon();
                }
            }
        });

        //deleting code
        sdelete.addActionListener(event -> {
            String storename = tname.getText();
            String storeserial = tserialnumber.getText();
            String storetcost = ttotalcost.getText();
            String storesection = tsection.getText();
            String storedate = tedate.getText();
            String storeprescription = tprescription.getText();
            String storedistributer = tdistributer.getText();
            String storecategory = tcategory.getText();
            String storereport = treport.getText();
            try {
                if (storename.equalsIgnoreCase("") && storeserial.equalsIgnoreCase("") && storetcost.equalsIgnoreCase("")
                        && storecategory.equalsIgnoreCase("") && storedate.equalsIgnoreCase("") && storedistributer.equalsIgnoreCase("")
                        && storeprescription.equalsIgnoreCase("") && storesection.equalsIgnoreCase("") && storereport.equalsIgnoreCase("")) {
                    JOptionPane.showMessageDialog(null, "Please Select The Product From The Table To Delete", "Error Message", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    Connection con = DBConnector.getConnection();
                    String[] option = {"Yes", "No"};
                    int selloption = JOptionPane.showOptionDialog(null, "Proceed in Deleting" + " " + storename + " " + "Details with Serial_Number" + " " + storeserial, "Deletion Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
                    if (selloption == 0) {
                        String sqldelete = "DELETE FROM store WHERE Mserial = '" + storeserial + "'";
                        prs = con.prepareStatement(sqldelete);
                        prs.execute();
                        if (prs != null) {
                            JOptionPane.showMessageDialog(null, storename + " " + "Deleted Successful", "Deletion Confirmation", JOptionPane.INFORMATION_MESSAGE, icon);
                            rs.close();
                            prs.close();
                            con.close();

                            //setting null values
                            tname.setText(null);
                            tserialnumber.setText(null);
                            tstock.setText(null);
                            tcost.setText(null);
                            ttotalcost.setText(null);
                            tsection.setText(null);
                            tedate.setText(null);
                            tprescription.setText(null);
                            tdistributer.setText(null);
                            tcategory.setText(null);
                            treport.setText(null);

                            //method for reloading details
                            reupload();
                        } else {
                            JOptionPane.showMessageDialog(null, storename + " " + "Deletion Failed", "Error Message", JOptionPane.ERROR_MESSAGE);
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
        reupload();

        //setting frame
        Fstore = new JFrame("Pharmacy System");
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("nimbusBase", Color.blue);
        } catch (Exception c) {
        }
        Fstore.setUndecorated(true);
        Fstore.setIconImage(iconimage);
        Fstore.add(panelstoremain);
        Fstore.setVisible(true);
        //Fstore.setSize(1400, 780);
        Fstore.setSize(screenSize.width, screenSize.height);
        Fstore.revalidate();
        Fstore.pack();
        Fstore.revalidate();
        Fstore.setLocationRelativeTo(null);
        //Fstore.setResizable(false);
        Fstore.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        //frame state to make components responsive
        Fstore.addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                // minimized
                if ((e.getNewState() & Frame.ICONIFIED) == Frame.ICONIFIED) {
                    Fstore.revalidate();
                    Fstore.pack();
                    Fstore.revalidate();
                    Fstore.setLocationRelativeTo(null);
                } // maximized
                else if ((e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                    Fstore.revalidate();
                    Fstore.pack();
                    Fstore.revalidate();
                    Fstore.setLocationRelativeTo(null);
                }
            }
        });

        bback.addActionListener(e -> {
            Fstore.setVisible(false);
            Sections ds = new Sections();
            ds.Operations();
        });
    }

//    public static void main(String[] args) {
//        MedicineStore mstore = new MedicineStore();
//        mstore.MStore();
//    }
}
