package ordering;

import com.sun.org.apache.bcel.internal.generic.FNEG;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.net.URL;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import dbconnector.DBConnector;

import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

import login.Sections;
import sun.applet.Main;

public class AddMedicine {

    //dimension setting
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    //getting images

    URL url1 = Main.class.getResource("/img/logo.png");
    URL url2 = Main.class.getResource("/img/additem.png");
    URL url3 = Main.class.getResource("/img/back2.png");
    URL url4 = Main.class.getResource("/img/addadd.png");
    URL url5 = Main.class.getResource("/img/reports.png");
    URL url6 = Main.class.getResource("/img/next3.png");
    URL url7 = Main.class.getResource("/img/cancel5.png");

    //setting images
    ImageIcon imageadd = new ImageIcon(url2);
    ImageIcon imageback = new ImageIcon(url3);
    ImageIcon imageadddrug = new ImageIcon(url4);
    ImageIcon imagereports = new ImageIcon(url5);
    ImageIcon imagenext = new ImageIcon(url6);
    ImageIcon imagecancel = new ImageIcon(url7);

    //setting ma default image icon to my frames
    Image iconimage = new ImageIcon(url1).getImage();

    //images for joptionpanes
    final ImageIcon icon = new ImageIcon(url4);

    //setting components
    JLabel lmname, lmcost, lmtotal, lmquantity, lmdateexpry, lmprescription, lmreport, lmcategory, lmwholesale, licon1, licon2, lserialnumber;
    JTextField tmname, tmcost, tmquantity, tmdateexpry, tmprescription, tmcategory, tmwholesale, tmtotal, tserialnumber;
    JTextArea tamreport;
    JButton bnext, bback, badd, bcancel;
    JDateChooser dateexpry;
    JComboBox<String> largment;

    //panel setting
    JPanel panelmenu = new JPanel(new GridBagLayout());
    JPanel panelcomp1 = new JPanel(new GridBagLayout());
    JPanel panelcomp2 = new JPanel(new GridBagLayout());
    JPanel mpanelsell = new JPanel(new GridBagLayout());
    JPanel mpanelmain = new JPanel(new BorderLayout(0, 0));

    //frame setting
    JFrame FNewDrug = new JFrame();

    //variables
    String type1, cost, quantity, mname, medate, mserialnumber, mpres, mdistributer, mcategory, mreport, mtotal;
    double cost2, total, quaty, total2;

    //database connectors
    Statement stmt = null;
    ResultSet rs = null;
    PreparedStatement prs = null;

    //date
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy, hh:mm:ss.SSS a");
    private static final DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    LocalDateTime now = LocalDateTime.now();
    String fileeditedlast = dtf.format(now);

    //calling class dbconnect
    DBConnector xamppfailure = new DBConnector();

    //code for getting sum ontyping
    private void sumCost() {
        cost = tmcost.getText();
        quantity = tmquantity.getText();
        if (cost.equalsIgnoreCase("") && quantity.equalsIgnoreCase("")) {
            //JOptionPane.showMessageDialog(null, "No Cost or Quantity given", "Error Message", JOptionPane.ERROR_MESSAGE);
        } else {
            cost2 = Double.parseDouble(cost);
            quaty = Double.parseDouble(quantity);
            total = quaty * cost2;
            //String costresult = Double.toString(total);
            tmtotal.setText(String.format("%.2f", total));
        }
    }

    //method cancel
    private void cancelProcess() {
        //set the fields to blank
        tmname.setText("");
        tserialnumber.setText("");
        tmquantity.setText("");
        tmcost.setText("");
        tmtotal.setText("");
        tmprescription.setText("");
        tmwholesale.setText("");
        tmcategory.setText("");
        tamreport.setText("");
    }

    //code for adding stockin
    private void stockin() {
        try {
            Connection con = DBConnector.getConnection();
            String sqladd = "INSERT INTO tablestockin(Mname,Mserial,Mtotalquantity,Mtotalcost) VALUES (?,?,?,?)";
            prs = con.prepareStatement(sqladd);

            //setting to database
            prs.setString(1, mname);
            prs.setString(2, mserialnumber);
            prs.setDouble(3, quaty);
            prs.setDouble(4, total2);

            prs.execute();
            prs.close();
            con.close();
        } catch (SQLException x) {
            xamppfailure.getCon();
        }

    }
    //end of code

    //code for adding distributers
    private void distributers() {
        try {
            Connection con = DBConnector.getConnection();
            String sqladd = "INSERT INTO tabledistributers(Mserial,Dname,Mname,Mquantity,Mtotalcost,Balance,LastEdited) VALUES (?,?,?,?,?,?,?)";
            prs = con.prepareStatement(sqladd);

            //setting to database
            prs.setString(1, mserialnumber);
            prs.setString(2, mdistributer);
            prs.setString(3, mname);
            prs.setDouble(4, quaty);
            prs.setDouble(5, total2);
            prs.setDouble(6, total2);
            prs.setString(7, fileeditedlast);

            prs.execute();
            prs.close();
            con.close();
        } catch (SQLException x) {
            xamppfailure.getCon();
        }

    }

    //method for adding medicine to the store contains both method distributers() and stockin()
    private void addingtosstore() {
        //get values from textfields
        mname = tmname.getText();
        mserialnumber = tserialnumber.getText();
        mtotal = tmtotal.getText();
        total2 = Double.parseDouble(mtotal);
        medate = ((JTextComponent) dateexpry.getDateEditor().getUiComponent()).getText();
        //System.out.println(medate);
        mpres = tmprescription.getText();
        mdistributer = tmwholesale.getText();
        mcategory = tmcategory.getText();
        mreport = tamreport.getText();
        //end getting
        String d1 = "Display";
        String d2 = "Dispensary";
        String d3 = "Control Drugs";
        String[] option = {"Yes", "No"};
        int selloption = JOptionPane.showOptionDialog(null, "Proceed in Adding" + " " + tmname.getText() + " " + "Details with Serial_Number" + " " + tserialnumber.getText(), "Notification", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
        if (selloption == 0) {
            if (type1.equalsIgnoreCase(d1)) {
                try {
                    Connection con = DBConnector.getConnection();
                    if (con != null) {
                        if (mname.equalsIgnoreCase("") && cost.equalsIgnoreCase("") && quantity.equalsIgnoreCase("") && medate.equalsIgnoreCase("")
                                && mpres.equalsIgnoreCase("") && mdistributer.equalsIgnoreCase("") && mcategory.equalsIgnoreCase("") && mreport.equalsIgnoreCase("") && mserialnumber.equalsIgnoreCase("")) {
                            JOptionPane.showMessageDialog(null, "Please Fill In The Fields", "Error Message", JOptionPane.ERROR_MESSAGE);
                        } else {
                            String sqladd = "INSERT INTO store(Mname,Mserial,Mquantity,Mcost,Mtotalcost,Medate,Msection,Mprescription,Mdistributer,Mcategory,Mreport,LastEdited) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
                            prs = con.prepareStatement(sqladd);

                            //setting to database
                            prs.setString(1, mname);
                            prs.setString(2, mserialnumber);
                            prs.setDouble(3, quaty);
                            prs.setDouble(4, cost2);
                            prs.setDouble(5, total2);
                            prs.setString(6, medate);
                            prs.setString(7, d1);
                            prs.setString(8, mpres);
                            prs.setString(9, mdistributer);
                            prs.setString(10, mcategory);
                            prs.setString(11, mreport);
                            prs.setString(12, fileeditedlast);

                            prs.execute();

                            if (prs != null) {
                                JOptionPane.showMessageDialog(null, "Product Details Saved" + "\n" + mname + " with Serial Number " + mserialnumber, "Store Message Notification", JOptionPane.INFORMATION_MESSAGE, icon);
                                con.close();
                                prs.close();

                                //set the fields to blank
                                tmname.setText("");
                                tserialnumber.setText("");
                                tmquantity.setText("");
                                tmcost.setText("");
                                tmtotal.setText("");
                                tmprescription.setText("");
                                tmwholesale.setText("");
                                tmcategory.setText("");
                                tamreport.setText("");

                                //method for adding new distributers
                                distributers();
                                //method for stockin
                                stockin();
                            } else {
                                JOptionPane.showMessageDialog(null, "Details Failed To Save", "Error Message", JOptionPane.ERROR_MESSAGE);
                                con.close();
                                prs.close();
                            }
                        }
                    }
                } catch (SQLException x) {
                    xamppfailure.getCon();
                }
            } else if (type1.equalsIgnoreCase(d2)) {
                try {
                    Connection con = DBConnector.getConnection();
                    if (con != null) {
                        if (mname.equalsIgnoreCase("") && cost.equalsIgnoreCase("") && quantity.equalsIgnoreCase("") && medate.equalsIgnoreCase("")
                                && mpres.equalsIgnoreCase("") && mdistributer.equalsIgnoreCase("") && mcategory.equalsIgnoreCase("") && mreport.equalsIgnoreCase("") && mserialnumber.equalsIgnoreCase("")) {
                            JOptionPane.showMessageDialog(null, "Please Fill In The Fields", "Error Message", JOptionPane.ERROR_MESSAGE);
                        } else {
                            String sqladd = "INSERT INTO store(Mname,Mserial,Mquantity,Mcost,Mtotalcost,Medate,Msection,Mprescription,Mdistributer,Mcategory,Mreport,LastEdited) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
                            prs = con.prepareStatement(sqladd);

                            //setting to database
                            prs.setString(1, mname);
                            prs.setString(2, mserialnumber);
                            prs.setDouble(3, quaty);
                            prs.setDouble(4, cost2);
                            prs.setDouble(5, total2);
                            prs.setString(6, medate);
                            prs.setString(7, d2);
                            prs.setString(8, mpres);
                            prs.setString(9, mdistributer);
                            prs.setString(10, mcategory);
                            prs.setString(11, mreport);
                            prs.setString(12, fileeditedlast);

                            prs.execute();

                            if (prs != null) {
                                JOptionPane.showMessageDialog(null, "Product Details Saved" + "\n" + mname + " with Serial Number " + mserialnumber, "Store Message Notification", JOptionPane.INFORMATION_MESSAGE, icon);
                                con.close();
                                prs.close();

                                //set the fields to blank
                                tmname.setText("");
                                tserialnumber.setText("");
                                tmquantity.setText("");
                                tmcost.setText("");
                                tmtotal.setText("");
                                tmprescription.setText("");
                                tmwholesale.setText("");
                                tmcategory.setText("");
                                tamreport.setText("");

                                //method for adding new distributers
                                distributers();
                                //method for stockin
                                stockin();
                            } else {
                                JOptionPane.showMessageDialog(null, "Details Failed To Save", "Error Message", JOptionPane.ERROR_MESSAGE);
                                con.close();
                                prs.close();
                            }
                        }
                    }
                } catch (SQLException x) {
                    xamppfailure.getCon();
                }
            } else if (type1.equalsIgnoreCase(d3)) {
                try {
                    Connection con = DBConnector.getConnection();
                    if (con != null) {
                        if (mname.equalsIgnoreCase("") && cost.equalsIgnoreCase("") && quantity.equalsIgnoreCase("") && medate.equalsIgnoreCase("")
                                && mpres.equalsIgnoreCase("") && mdistributer.equalsIgnoreCase("") && mcategory.equalsIgnoreCase("") && mreport.equalsIgnoreCase("") && mserialnumber.equalsIgnoreCase("")) {
                            JOptionPane.showMessageDialog(null, "Please Fill In The Fields", "Notification Message", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            String sqladd = "INSERT INTO store(Mname,Mserial,Mquantity,Mcost,Mtotalcost,Medate,Msection,Mprescription,Mdistributer,Mcategory,Mreport,LastEdited) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
                            prs = con.prepareStatement(sqladd);

                            //setting to database
                            prs.setString(1, mname);
                            prs.setString(2, mserialnumber);
                            prs.setDouble(3, quaty);
                            prs.setDouble(4, cost2);
                            prs.setDouble(5, total2);
                            prs.setString(6, medate);
                            prs.setString(7, d3);
                            prs.setString(8, mpres);
                            prs.setString(9, mdistributer);
                            prs.setString(10, mcategory);
                            prs.setString(11, mreport);
                            prs.setString(12, fileeditedlast);

                            prs.execute();

                            if (prs != null) {
                                JOptionPane.showMessageDialog(null, "Product Details Saved" + "\n" + mname + " with Serial Number " + mserialnumber, "Store Message Notification", JOptionPane.INFORMATION_MESSAGE, icon);
                                con.close();
                                prs.close();

                                //set the fields to blank
                                tmname.setText("");
                                tserialnumber.setText("");
                                tmquantity.setText("");
                                tmcost.setText("");
                                tmtotal.setText("");
                                tmprescription.setText("");
                                tmwholesale.setText("");
                                tmcategory.setText("");
                                tamreport.setText("");

                                //method for adding new distributers
                                distributers();
                                //method for stockin
                                stockin();
                            } else {
                                JOptionPane.showMessageDialog(null, "Details Failed To Save", "Error Message", JOptionPane.ERROR_MESSAGE);
                                con.close();
                                prs.close();
                            }
                        }
                    }
                } catch (SQLException x) {
                    xamppfailure.getCon();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "You Cancelled", "Notification", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    //end of code

    public void NewDrug() {
        licon1 = new JLabel(imageadd);
        licon2 = new JLabel("NEW PRODUCT");
        licon2.setFont(new Font("Tahoma", Font.BOLD, 15));
        lmname = new JLabel("Product Name");
        lmname.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lserialnumber = new JLabel("Serial Number");
        lserialnumber.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lmquantity = new JLabel("Quantity[Numbers]");
        lmquantity.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lmcost = new JLabel("Cost[KSH]");
        lmcost.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lmtotal = new JLabel("Total Cost (KSH)");
        lmtotal.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lmdateexpry = new JLabel("Expiry Date[yyyy/MM/dd]");
        lmdateexpry.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lmprescription = new JLabel("Prescription");
        lmprescription.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lmreport = new JLabel(imagereports);
        lmreport.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lmcategory = new JLabel("Category");
        lmcategory.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lmwholesale = new JLabel("Distributed By");
        lmwholesale.setFont(new Font("Tahoma", Font.PLAIN, 13));

        tmtotal = new JTextField(15);
        tmtotal.setFont(new Font("Tahoma", Font.PLAIN, 15));
        tmtotal.setEditable(false);
        tmtotal.setBackground(Color.LIGHT_GRAY);
        tmname = new JTextField(20);
        tmname.setFont(new Font("Tahoma", Font.PLAIN, 15));
        tserialnumber = new JTextField(20);
        tserialnumber.setFont(new Font("Tahoma", Font.PLAIN, 15));
        tmquantity = new JTextField(15);
        tmquantity.setFont(new Font("Tahoma", Font.PLAIN, 15));
        tmcost = new JTextField(15);
        tmcost.setFont(new Font("Tahoma", Font.PLAIN, 15));
        tmprescription = new JTextField(20);
        tmprescription.setFont(new Font("Tahoma", Font.PLAIN, 15));
        tmcategory = new JTextField(20);
        tmcategory.setFont(new Font("Tahoma", Font.PLAIN, 15));
        tmwholesale = new JTextField(20);
        tmwholesale.setFont(new Font("Tahoma", Font.PLAIN, 15));

        String[] options = {"Choose Product Arrangement", "Display", "Dispensary", "Control Drugs"};
        largment = new JComboBox<String>(options);
//        largment.addItem("Arragement Of Medicine");
//        largment.addItem("");
//        largment.addItem("");
//        largment.addItem("");
        largment.setCursor(new Cursor(Cursor.HAND_CURSOR));
        largment.setFont(new Font("Tahoma", Font.PLAIN, 12));
        //String type1 = largment.getItemAt(1);
        //String type1 = String.valueOf(largment.getSelectedItem());

        largment.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                JComboBox<String> largment = (JComboBox<String>) event.getSource();
                type1 = (String) largment.getSelectedItem();
                if (type1.equalsIgnoreCase("Display")) {
                    //JOptionPane.showMessageDialog(null, type1, "", JOptionPane.INFORMATION_MESSAGE);
                } else if (type1.equalsIgnoreCase("Dispensary")) {
                    //JOptionPane.showMessageDialog(null, type1, "", JOptionPane.INFORMATION_MESSAGE);
                } else if (type1.equalsIgnoreCase("Control Drugs")) {
                    //JOptionPane.showMessageDialog(null, type1, "", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        tamreport = new JTextArea(10, (int) 52.5);
        tamreport.setLineWrap(true);
        DefaultCaret caret = (DefaultCaret) tamreport.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scrollpane = new JScrollPane();
        scrollpane.add(tamreport);
        scrollpane.setViewportView(tamreport);
        tamreport.setWrapStyleWord(true);
        tamreport.setToolTipText("Write on the little usage of the Product");
        tamreport.setFont(new Font("Tahoma", Font.PLAIN, 13));
        //date code
        dateexpry = new JDateChooser();
        dateexpry.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dateexpry.setPreferredSize(new Dimension(251, 35));
        dateexpry.setDateFormatString("yyyy/MM/dd");
        dateexpry.setFont(new Font("Tahoma", Font.PLAIN, 12));
//        DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
//        Date newDate = new Date();
//        dateexpry.setDate(newDate);

        bcancel = new JButton(imagecancel);
        bcancel.setBackground(Color.red);
        bcancel.setToolTipText("Cancel/Reload");
        bcancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bnext = new JButton(imagenext);
        bnext.setToolTipText("Next");
        bnext.setBackground(Color.LIGHT_GRAY);
        bback = new JButton("BACK");
        bback.setToolTipText("BACK");
        bback.setBackground(Color.LIGHT_GRAY);
        bback.setFont(new Font("Tahoma", Font.BOLD, 15));
        badd = new JButton(imageadddrug);
        badd.setToolTipText("Store Product Details");
        badd.setBackground(Color.GREEN);
        badd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bnext.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bback.setCursor(new Cursor(Cursor.HAND_CURSOR));

        //panel panelcomp1
        GridBagConstraints v = new GridBagConstraints();
        v.anchor = GridBagConstraints.FIRST_LINE_START;
        v.insets = new Insets(0, 0, 0, 800);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        panelmenu.add(bback, v);
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 0, 0);
        panelmenu.add(licon2, v);
        panelmenu.setBorder(new TitledBorder(""));

        v.anchor = GridBagConstraints.WEST;
        v.insets = new Insets(0, 0, 0, 50);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        panelcomp1.add(lmname, v);
        v.gridy++;
        panelcomp1.add(tmname, v);
        v.gridy++;
        panelcomp1.add(lserialnumber, v);
        v.gridy++;
        panelcomp1.add(tserialnumber, v);
        v.gridy++;
        panelcomp1.add(lmprescription, v);
        v.insets = new Insets(0, 0, 10, 50);
        v.gridy++;
        panelcomp1.add(tmprescription, v);
        v.anchor = GridBagConstraints.CENTER;
        v.gridy++;
        v.insets = new Insets(0, 0, 20, 50);
        panelcomp1.add(largment, v);
        v.anchor = GridBagConstraints.WEST;
        v.insets = new Insets(0, 0, 0, 0);
        v.gridy++;
        v.gridwidth = 10;
        panelcomp1.add(lmreport, v);
        v.gridy++;
        panelcomp1.add(scrollpane, v);

        //panel panelcomp2
        v.anchor = GridBagConstraints.LAST_LINE_START;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 3;
        v.gridy = 0;
        panelcomp1.add(lmwholesale, v);
        v.gridy++;
        panelcomp1.add(tmwholesale, v);
        v.gridy++;
        panelcomp1.add(lmcategory, v);
        v.gridy++;
        panelcomp1.add(tmcategory, v);
        v.gridy++;
        panelcomp1.add(lmdateexpry, v);
        v.gridy++;
        panelcomp1.add(dateexpry, v);
        panelcomp1.setBorder(new TitledBorder("Product Details"));

        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 50, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        panelcomp2.add(licon1, v);
        v.insets = new Insets(0, 0, 0, 0);
        v.gridy++;
        panelcomp2.add(lmquantity, v);
        v.gridy++;
        panelcomp2.add(tmquantity, v);
        v.gridy++;
        panelcomp2.add(lmcost, v);
        v.gridy++;
        panelcomp2.add(tmcost, v);
        v.gridy++;
        panelcomp2.add(lmtotal, v);
        v.insets = new Insets(0, 0, 20, 0);
        v.gridy++;
        panelcomp2.add(tmtotal, v);
        v.gridy++;
        v.anchor = GridBagConstraints.WEST;
        v.insets = new Insets(0, 0, 0, 0);
        panelcomp2.add(badd, v);
        v.anchor = GridBagConstraints.EAST;
        v.insets = new Insets(0, 0, 0, 0);
        panelcomp2.add(bcancel, v);
        panelcomp2.setBorder(new TitledBorder("Product Cost"));

        mpanelmain.add(panelmenu, "North");
        mpanelmain.add(panelcomp1, "West");
        mpanelmain.add(panelcomp2, "Center");
        mpanelmain.setBackground(Color.blue.brighter());
        mpanelmain.setBorder(new TitledBorder(""));

//        //actions
        tmcost.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                sumCost();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                sumCost();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        badd.addActionListener(e -> {
            addingtosstore();
        });
        //end of actions
        //setting frame
        FNewDrug = new JFrame("Pharmacy System");
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("nimbusBase", Color.blue);
        } catch (Exception c) {
        }
        FNewDrug.setUndecorated(true);
        FNewDrug.setIconImage(iconimage);
        FNewDrug.add(mpanelmain);
        FNewDrug.setVisible(true);
        FNewDrug.setSize(900, 560);
//        FNewDrug.setSize(screenSize.width, screenSize.height);
//        FNewDrug.revalidate();
//        FNewDrug.pack();
//        FNewDrug.revalidate();
        FNewDrug.setLocationRelativeTo(null);
        FNewDrug.setResizable(true);
        FNewDrug.revalidate();
        FNewDrug.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        bback.addActionListener(e -> {
            FNewDrug.setVisible(false);
            Sections ds = new Sections();
            ds.Operations();
        });
        bcancel.addActionListener(e -> {
            cancelProcess();
        });
    }

//    public static void main(String[] args) {
//        AddMedicine madd = new AddMedicine();
//        madd.NewDrug();
//    }
}
