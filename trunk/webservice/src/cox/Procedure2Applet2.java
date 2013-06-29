package cox;
import java.io.*;
import java.awt.BorderLayout;
import java.awt.Color;
//import java.awt.Cursor;
//import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
//import java.net.URL;
//import java.sql.*;
//import java.net.URLConnection;
//import java.net.MalformedURLException;
import java.awt.*;
//import java.awt.event.*;
//import java.applet.*;
import javax.swing.*;
import javax.swing.border.*;

//import java.security.AccessController;
//import java.security.PrivilegedAction;
//import java.util.Vector;
//
//import Jama.Matrix;


public class Procedure2Applet2 extends JApplet
{

	private JScrollPane scrolling = null;
	private TextArea fileBox = null;
	private JButton butFile = null;
	private JTextField tfFilename = null;
	private JButton butLoad = null;
	private JButton butSend = null;
	private final String LOAD = "load";
	private final String SEND = "send";
	private final String FILE = "file";
	private String task_ = "";
	private String email_ = "";
	private String root_ = "";
	private String parameters_ = "";
	private String featureNo_ = "";
	private String kernel_ = "";
	private String noFeature_ = "";
	private String noRecord_ = "";
	private String target_ = "";
	private String oldValue = "old";

	public void init()
	{
		try
		{
			setSize(500, 500);
			setBackground(new Color(255, 255, 255));
			jbInit();	
			//System.setOut(new GUIPrintStream(System.out, fileBox));
			//-----------------------------------------------------
			(new Thread(new WriteData())).start();
			System.out.println("before redirect");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}


	private void jbInit() throws Exception
	{
		JPanel sendPane = new JPanel();
//		sendPane.setSize(500,500);
//		sendPane.setBackground(new Color(203, 203, 203));
		Border etchedBdr3 = BorderFactory.createEtchedBorder();
		Border titledBdr3 = BorderFactory.createTitledBorder(etchedBdr3, "------------------------------------------On-going iterations----------------------------------------");
		Border emptyBdr3  = BorderFactory.createEmptyBorder(10,20,20,20);
		Border compoundBdr3=BorderFactory.createCompoundBorder(titledBdr3, emptyBdr3);
		sendPane.setBorder(compoundBdr3);
		sendPane.setLayout(new GridBagLayout());
		GridBagConstraints c3 = new GridBagConstraints(); 
		
		JLabel resultsLabel = new JLabel("----------Iteration information Test----------");
		c3.fill = GridBagConstraints.CENTER;//HORIZONTAL
		c3.anchor = GridBagConstraints.CENTER;
		c3.weightx =4;
		c3.gridx =0;
		c3.gridy = 0;
		c3.gridwidth = 0;
		sendPane.add(resultsLabel, c3);
		fileBox = new TextArea();
		fileBox.setSize(300, 500);
		fileBox.setText("");
		fileBox.setEditable(true);
//		scrolling = new JScrollPane(fileBox);
		c3.fill = GridBagConstraints.HORIZONTAL;
		c3.weightx =0;
		c3.gridwidth=2;
		c3.gridx = 0;
		c3.gridy = 1;
		c3.ipady = 200;
//		sendPane.add(scrolling, c3);
//		add(sendPane, BorderLayout.NORTH);
		sendPane.add(fileBox, c3);
		add(sendPane, BorderLayout.NORTH);
		
	}
	
	public void setParameter(String dataPath, String taskName, String root_property, String maxIteration, String epsilon,
			String taskStatus){
		System.out.println(dataPath + "\t" + taskName + "\t" + root_property + "\t" + maxIteration + "\t" + 
				epsilon + "\t" + taskStatus);
//		new GloreClient2(taskName, dataPath, root_property, Integer.parseInt(maxIteration), Double.parseDouble(epsilon), Integer.parseInt(taskStatus));
		new CoxClient2("a6", "E:/MyProgram/JavaWorkspace/WebGlore/data/IPDLR_2.txt", "http://localhost:8080/webglore2/", 20, 0.00001, 1);
	}
	 
	class WriteData implements Runnable{
		public void run(){
			try{
			Thread.sleep(1000);
//			fileBox.setText("go");
			PrintStream save = System.out;
			System.setOut(new GUIPrintStream(System.out, fileBox));
			System.setErr(new GUIPrintStream(System.out, fileBox));
			
//			getAppletContext().showDocument(new URL("javascript:window.setAppletParameter()"));
			System.out.println(oldValue);
			
			
			new CoxClient2("a6", "E:/MyProgram/JavaWorkspace/WebGlore/data/IPDLR_2.txt", "http://localhost:8080/webglore2/", 20, 0.00001, 1);
			
//			String dataPath = getParameter("dataPath");
//			String taskName = getParameter("taskName");
//			String root_property = getParameter("root_property");
//			int maxIteration=Integer.parseInt(getParameter("maxIteration"));
//			double epsilon =Double.valueOf(getParameter("epsilon")) ;
//			int taskStatus = Integer.parseInt(getParameter("taskStatus"));
//			new GloreClient2(taskName, dataPath, root_property, maxIteration, epsilon, taskStatus);
//			getAppletContext().showDocument(new URL("javascript:window.enableButton()"));
			
//			System.setOut(save);
//			System.setErr(save);
			System.out.println("come back");
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}


}

