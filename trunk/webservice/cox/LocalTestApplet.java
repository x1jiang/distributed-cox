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
import java.lang.Math;
import Jama.Matrix;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Vector;

public class LocalTestApplet extends JApplet implements ActionListener{
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
	private String ResultString="";
	private String betaString = "0.5#0.5#0.5";
	private String surString = "";
	private String OutputString="";//added lph
	
	public void init()
	{
		try
		{
			//the input parameter may have two names but not in the same time jwc 10.5
			if(getParameter("showFilePath")!=null){
				fileName_ = getParameter("showFilePath");
			}
			if(getParameter("userFilePath")!=null){
				fileName_ = getParameter("userFilePath");
			}
			if(getParameter("betaString")!=null){
				betaString = getParameter("betaString");
			}
			if(getParameter("surString")!=null){
				surString = getParameter("surString");
			}
			
			setSize(100, 400);
			setBackground(new Color(255, 255, 255));
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
		filePane.setBackground(new Color(255, 255 ,255 ));
		Border etchedBdr = BorderFactory.createEtchedBorder();
		Border titledBdr = BorderFactory.createTitledBorder(etchedBdr, "Upload your test data file:");
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
		
		butLoad = new JButton();
		butLoad.setText("Submit");
		butLoad.setSize(180, 29);
		c1.fill = GridBagConstraints.NONE;
		c1.anchor = GridBagConstraints.EAST;
		c1.weightx = 0.1;
		c1.gridwidth = GridBagConstraints.REMAINDER;
		c1.gridx = 2;
		c1.gridy = 0;
		butLoad.setActionCommand(LOAD);
		butLoad.addActionListener(this);
		filePane.add(butLoad);		

		add(filePane, BorderLayout.NORTH);
		
	}
	
	public String getFilename(){
		return tfFilename.getText();
	}
	public String getAttributes(){
		return property_;
	}
	public String getResults(){
		return ResultString;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getActionCommand().equals(FILE))
		{
			Frame f=new Frame();
			FileDialog fd = new FileDialog(f, "Select File", FileDialog.LOAD);
			fd.setVisible(true);
			if(fd.getFile()!=null)
			{
				fileName_ = fd.getDirectory() + fd.getFile();
			}
			tfFilename.setText(fileName_);
			
			//Here is the function of "load", combine them together 11.5 jwc
			try{
				FileInputStream data = new FileInputStream(fileName_);
				DataInputStream data_file = new DataInputStream (data);
				property_ =data_file.readLine();			//added by lph
				System.out.println("Property: " + property_);
				data_file.close();
			//	getAppletContext().showDocument(new URL("javascript:window.accessAppletMethod()"));
			}catch(Exception exc){
				property_ = property_ + exc.getMessage();
			}
		}
		if (e.getActionCommand().equals(LOAD))
		{
			try{
			    // used for reading the data file
			    FileInputStream file_stream;
			    DataInputStream file_in;
			    BufferedReader file_br;
			    String file_line;
			    String[] line_tokens;
				
			    Vector<Double> ZTDrow;
				Vector< Vector<Double> > ZTDv = new Vector< Vector<Double> >();
				double[][] ZTDa;
				double[] DIa;
				double[][][] ZZa;
				int[] index, nonDeltaDI;
				Matrix ZTD, Z, T, Delta, DI, sumZ, sumZ_line, ZZ_line, ZZ, ZZv0;
				Matrix beta0, beta1, ZB, thetaZ, theta, thetaZtmp, thetatmp, Gv, G;
				Matrix TZTZ, TZTZ_line, TZTZtmp, neghessian;
				Matrix Tuniq=null;
				Vector< Matrix > ZZv, ZZtmp, thetaZZ, TZTZv;
				int m=-1, full_n=0, n, i, j, iter=0, k, ki;

			    file_stream = new FileInputStream(fileName_);
			    file_in = new DataInputStream(file_stream);
			    file_br = new BufferedReader(new InputStreamReader(file_in));
			    file_line = file_br.readLine();// property line
			    
			    System.out.println("Using data file '" + fileName_ + "'.");
			    System.out.println("betaString is '" + betaString + "'.");
			    System.out.println("SurString is '" + surString + "'.");
			    
				while ((file_line = file_br.readLine()) != null) {
					full_n = full_n + 1;
					line_tokens = file_line.split("\t");
					if (m == -1) {
						m = line_tokens.length - 2;
					}else if (m != line_tokens.length - 2) {
						System.out.println("ERROR: data file dimensions don't " +
										   "match on line " + full_n + ".");
						System.exit(-1);
					}
					ZTDrow = new Vector<Double>();	
					for (i = 0; i < line_tokens.length; i++) {
						ZTDrow.add(new Double(line_tokens[i]));					
					}
					ZTDv.add(ZTDrow);
			    }
				file_in.close();

				T = new Matrix(two_dim_vec_to_arr(ZTDv)).getMatrix(0,full_n-1,m,m);
				//assume beta as 0.5 for all
				String[] ss = betaString.split("#");
				double[][] betaValue = new double[ss.length][1];
				for(i=0; i<ss.length; i++){
					betaValue[i][0] = Double.parseDouble(ss[i]);
				}
				Matrix beta = new Matrix(betaValue);
//				beta = new Matrix(m, 1, 0.5);
				//Initialize result = Y
				// calculate P <- 1 + exp(-x%*%beta0)
				//assume result = T first
				String ResultString = "";
				String OutputString = "";
				double[] result = new double[full_n];
				ResultString=ResultString+"<table><tr><td>Predicted Probability</td><td>Y-value</td></tr>";
				//	ResultString=ResultString+"\t"+"Predicted Probability  & "+"\t"+"Y value"+"<br>";
					OutputString=OutputString+"\t"+"Predicted Probability  & "+"\t"+"Y value"+"\n";

					for (k=0; k<full_n; k++) {
						result[k]=T.get(k,0);
						ResultString=ResultString+"<tr><td>"+result[k]+"</td><td>"+T.get(k,0)+"</td></tr>";
						System.out.println(k+1+"\t"+result[k]+"\t"+T.get(k,0));
						OutputString=OutputString+"\t"+result[k]+"\t"+T.get(k,0)+"\n";
					}
					ResultString=ResultString+"</table>";
					getAppletContext().showDocument(new URL("javascript:window.accessAppletResult()"));
					
					Frame f=new Frame();
					FileDialog fd = new FileDialog(f, "Save Test Result", FileDialog.SAVE);
					fd.setVisible(true);
					try
					{
						 File f1 = new File(fd.getDirectory(), fd.getFile());
						 BufferedWriter out = new BufferedWriter(new FileWriter(f1));
//						 FileOutputStream out = new FileOutputStream(f1);
						 out.write(OutputString);
						 out.close();
						 OutputString = "";
					}
					catch (IOException e2)
					{
						e2.printStackTrace();
					}
					
					
	
			}catch(Exception exc){
			
			}
		}
	}
/* Set each element of the 2D double array to e^a where a is the value of an element. */
public static void exp(double[][] A) {
	int i,j;
	for (i = 0; i < A.length; i++) {
		for (j = 0; j < A[i].length; j++) {
			A[i][j] = Math.exp(A[i][j]);
		}
	}
}	
/* Set each element of the 2D double array to 1 + a where a is the value of
 an element. */
public static void add_one(double[][] A) {
	int i,j;
	for (i = 0; i < A.length; i++) {
		for (j = 0; j < A[i].length; j++) {
			A[i][j] = 1 + A[i][j];
		}
	}
}	
/* Set each element of the 2D double array to 1/a where a is the value of
 an element. */
public static void div_one(double[][] A) {
	int i,j;
	for (i = 0; i < A.length; i++) {
		for (j = 0; j < A[i].length; j++) {
			A[i][j] = 1.0 / A[i][j];
		}
	}
}
/* Convert a 2D vector of Doubles into a 2D array of doubles. */
public static double[][] two_dim_vec_to_arr(Vector< Vector<Double> >V) {
	// allocate part of the array
	double[][] A = new double[V.size()][];
	int i;
	
	// allocate and convert rows of the vector
	for (i = 0; i < V.size(); i++) {
		A[i] = one_dim_vec_to_arr(V.get(i));
	}
	
	// return 2D array
	return A;
}
/* Convert a Vector of Doubles into an array of doubles. */
public static double[] one_dim_vec_to_arr(Vector<Double> V) {
	int size = V.size();
	int i;
	double[] A = new double[size];
	
	for (i = 0; i < size; i++) {
		A[i] = (V.get(i)).doubleValue();
	}
	
	return A;
}
}