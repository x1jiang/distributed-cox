package cox;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import javax.swing.*;
import javax.swing.border.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
//import netscape.javascript.*;

public class DoTestApplet extends JApplet implements ActionListener{
	private JScrollPane scrolling = null;
	private JTextPane fileBox = null;
	private JButton butFile = null;
	private JTextField tfFilename = null;
	private JButton butLoad = null;
	private JButton butSend = null;
	private final String LOAD = "load";
	private final String SEND = "send";
	private final String FILE = "file";
	private String name_ = "";
	private String passWord_ = "";
	private String task_ = "";
	private String email_ = "";
	private String root_ = "";
	private String parameters_ = "";
	private String featureNo_ = "";
	private String kernel_ = "";
	private String noFeature_ = "";
	private String noRecord_ = "";
	private String target_ = "";
	private String property_="";
	private String fileName_="";
	
	public void init()
	{
		try
		{
			task_ = getParameter("task");
			email_ = getParameter("email");
			root_ = getParameter("root");
			parameters_ = getParameter("param");
			//the input parameter may have two names but not in the same time jwc 10.5
			if(getParameter("showFilePath")!=null){
				fileName_ = getParameter("showFilePath");
			}
			else if(getParameter("userFilePath")!=null){
				fileName_ = getParameter("userFilePath");
			}
			
			setSize(100, 400);
			setBackground(new Color(203, 203, 203));
			jbInit();	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception
	{
		//add file panel
		JPanel filePane = new JPanel();
		filePane.setBackground(new Color(203, 203, 203));
		Border etchedBdr = BorderFactory.createEtchedBorder();
		Border titledBdr = BorderFactory.createTitledBorder(etchedBdr, "Step 1: Data File");
		Border emptyBdr  = BorderFactory.createEmptyBorder(10,10,10,10);
		Border compoundBdr=BorderFactory.createCompoundBorder(titledBdr, emptyBdr);
		filePane.setBorder(compoundBdr);
		filePane.setLayout(new GridBagLayout());
		GridBagConstraints c1 = new GridBagConstraints();
		tfFilename = new JTextField();
		tfFilename.setText(fileName_);
		tfFilename.setSize(206, 29);
		c1.fill = GridBagConstraints.HORIZONTAL;
		c1.weightx = 1.0;
		c1.gridwidth = 1;
		c1.gridx = 0;
		c1.gridy = 0;
		
		filePane.add(tfFilename, c1);
		//button "browse"
		butFile = new JButton();
		butFile.setText("Browse");
		butFile.setSize(180, 29);
		c1.fill = GridBagConstraints.NONE;
		//c1.anchor = GridBagConstraints.HORIZONTAL;
		c1.weightx = 0.1;
		c1.gridwidth = 1;
		c1.gridx = 1;
		c1.gridy = 0;
		butFile.setActionCommand(FILE);
		butFile.addActionListener(this);
		filePane.add(butFile, c1);
//----------"load" button is deleted by jwc 11.5------------------		
//		butLoad = new JButton();
//		butLoad.setText("Load");
//		butLoad.setSize(180, 29);
//		c1.fill = GridBagConstraints.NONE;
//		c1.anchor = GridBagConstraints.EAST;
//		c1.weightx = 0.1;
//		c1.gridwidth = GridBagConstraints.REMAINDER;
//		c1.gridx = 2;
//		c1.gridy = 0;
//		butLoad.setActionCommand(LOAD);
//		butLoad.addActionListener(this);
//		filePane.add(butLoad);		

		add(filePane, BorderLayout.NORTH);
		
	}
	
	public String getFilename(){
		return tfFilename.getText();
	}
	public String getAttributes(){
		return property_;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getActionCommand().equals(FILE))
		{
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(null);
			int returnVal = fc.showDialog(DoTestApplet.this, "Select");
			//fileName_ = "data.txt";		This variable is initiated in init();
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				File aFile = fc.getSelectedFile();
				fileName_ = aFile.getPath();
			}
			tfFilename.setText(fileName_);
			
			//Here is the function of "load", combine them together 11.5 jwc
			try{
				FileInputStream data = new FileInputStream(fileName_);
				DataInputStream data_file = new DataInputStream (data);
				property_ =data_file.readLine();			//added by lph
				System.out.println("input3: " + property_);
				data_file.close();
				
			//using javascript function
//				JOptionPane jo = new JOptionPane();
//				jo.showMessageDialog(null, "Here comes the javascript code");
				getAppletContext().showDocument(new URL("javascript:window.accessAppletMethod()"));
//				JSObject window=JSObject.getWindow(this);
//				 window.eval("alert(/'This alert comes from Java!/')");
//				 window.call("accessAppletMethod", null);
				
			}catch(Exception exc){
				property_ = property_ + exc.getMessage();
			}
		}
//		if (e.getActionCommand().equals(LOAD))
//		{
//			try{
//				String filename = tfFilename.getText();
//				//when filename is written into String, it convert from \ to \\. So, no need to convert \\ to /
////				filename = filename.replaceAll("\\\\", "/");
//				System.out.println("input1: " + filename);
//				FileInputStream data = new FileInputStream(filename);
//				DataInputStream data_file = new DataInputStream (data);
//				property_ =data_file.readLine();			//added by lph
//				System.out.println("input3: " + property_);
//				data_file.close();
//			}catch(Exception exc){
//				property_ = property_ + exc.getMessage();
//			}
//		}
	}
}
