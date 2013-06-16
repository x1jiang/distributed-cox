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


public class Procedure2Applet_back extends JApplet
{

	private TextArea fileBox = null;
	String dataPath ;
	String taskName;
	String root_property;
	int maxIteration;
	double epsilon ;
	int taskStatus;

	public void init()
	{
		try
		{
			setSize(500, 500);
			setBackground(new Color(203, 203, 203));
			jbInit();	
			//System.setOut(new GUIPrintStream(System.out, fileBox));
			//-----------------------------------------------------
			(new Thread(new WriteData())).start();
//			SetParameters( "C:/Users/Wenchao/Documents/ca_part2", "abc11", "http://localhost:8080/glore2/", "100", "1.0E-6", "1");
//			StartComputation();
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
		sendPane.setSize(500,500);
		sendPane.setBackground(new Color(203, 203, 203));
		Border etchedBdr3 = BorderFactory.createEtchedBorder();
		Border titledBdr3 = BorderFactory.createTitledBorder(etchedBdr3, "------------------------------------------On-going iterations----------------------------------------");
		Border emptyBdr3  = BorderFactory.createEmptyBorder(10,20,20,20);
		Border compoundBdr3=BorderFactory.createCompoundBorder(titledBdr3, emptyBdr3);
		sendPane.setBorder(compoundBdr3);
		sendPane.setLayout(new GridBagLayout());
		GridBagConstraints c3 = new GridBagConstraints(); 
		
		JLabel resultsLabel = new JLabel("----------Iteration information----------");
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
	
	public void StartComputation(){
		(new Thread(new WriteData())).start();
		System.out.println("computation started");
	}
	 
	public void SetParameters(String dataPath, String taskName, String root_property, String maxIteration, String epsilon, String taskStatus){
		this.dataPath = dataPath;
		this.taskName = taskName;
		this.root_property = root_property;
		this.maxIteration = Integer.parseInt(maxIteration);
		this.epsilon = Double.parseDouble(epsilon);
		this.taskStatus = Integer.parseInt(taskStatus);
		
	}
	public String GetParameters(){
		String str = "11";
		str += getParameter("dataPath");
		str += getParameter("taskName");
		str += getParameter("root_property");
		return str;
	}
	class WriteData implements Runnable{
		public void run(){
			try{
			Thread.sleep(1000);
//			fileBox.setText("go");
			PrintStream save = System.out;
			System.setOut(new GUIPrintStream(System.out, fileBox));
//			System.setErr(new GUIPrintStream(System.out, fileBox));
//			String dataPath = getParameter("dataPath");
//			String taskName = getParameter("taskName");
//			String root_property = getParameter("root_property");
//			int maxIteration=Integer.parseInt(getParameter("maxIteration"));
//			double epsilon =Double.valueOf(getParameter("epsilon")) ;
//			int taskStatus = Integer.parseInt(getParameter("taskStatus"));
			new CoxClient2(taskName, dataPath, root_property, maxIteration, epsilon, taskStatus);
//			getAppletContext().showDocument(new URL("javascript:window.enableButton()"));
			
			System.setOut(save);
//			System.setErr(save);
			System.out.println("get back");
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}


}

