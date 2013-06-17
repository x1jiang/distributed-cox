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
//061213

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
	private String Yaxis="";
	private String Xaxis="";
	private String betaString = "0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5";
	private String surString = "1.0#2.0#3.0#4.0#5.0#6.0#7.0#8.0#9.0#10.0#11.0#12.0#13.0#14.0#15.0#16.0#17.0#18.0" +
			"#19.0#20.0#21.0#22.0#23.0#24.0#25.0#26.0#27.0#28.0#29.0#31.0#32.0#33.0#34.0#35.0#36.0#37.0#38.0#39.0" +
			"#40.0#41.0#42.0#44.0#45.0#47.0#48.0#49.0#50.0#51.0#52.0#53.0#54.0#55.0#56.0#57.0#58.0#59.0#60.0#61.0" +
			"#62.0#63.0#64.0#65.0#66.0#67.0#68.0#69.0#70.0#71.0#72.0#73.0#74.0#75.0#76.0#77.0#78.0#79.0#80.0#81.0" +
			"#82.0#83.0#85.0#86.0#88.0#89.0#90.0#92.0#93.0#95.0#96.0#98.0#99.0#102.0#103.0#104.0#105.0#106.0#107.0";
	private String fnString = "0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5" +
			"#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5" +
			"#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5" +
			"#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5#0.5" +
			"#0.5";
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
			if(getParameter("fnString")!=null){
				fnString = getParameter("fnString");
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
	public String getYaxis(){
		return Yaxis;
	}
	public String getXaxis(){
		return Xaxis;
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
				Z = new Matrix(two_dim_vec_to_arr(ZTDv)).getMatrix(0,full_n-1,0,m-1);
				//assume beta as 0.5 for all
				String[] bs = betaString.split("#");
				double[][] betaValue = new double[bs.length][1];
				for(i=0; i<bs.length; i++){
					betaValue[i][0] = Double.parseDouble(bs[i]);
				}
				Matrix beta = new Matrix(betaValue);
				String[] fs = fnString.split("#");
				double[][] fnValue = new double[fs.length][1];
				for(i=0; i<fs.length; i++){
					fnValue[i][0] = Double.parseDouble(fs[i]);
				}
				Matrix fn = new Matrix(fnValue);
				String[] ss = surString.split("#");
				double[][] surValue = new double[ss.length][1];
				for(i=0; i<ss.length; i++){
					surValue[i][0] = Double.parseDouble(ss[i]);
				}
				Matrix sur = new Matrix(surValue);
				n= ss.length;
				System.out.println("print received beta");
				beta.print(7,7);
				System.out.println("print received Baseline Fn");
				fn.print(7,7);
				System.out.println("print received Survival Time");
				sur.print(7,7);
				Xaxis = Xaxis + "[";
				for(i=0;i<n-1;i++){
					Xaxis = Xaxis + sur.get(i,0) + ", ";
				}
				Xaxis = Xaxis + sur.get(n-1,0) + "]";		
//				beta = new Matrix(m, 1, 0.5);
				//Initialize result = Y
				// calculate P <- 1 + exp(-x%*%beta0)
				//assume result = T first
				String ResultString = "";
				String OutputString = "";
				double[] result = new double[full_n];
				//calculate survival time
				fn.timesEquals(-1);
				exp(fn.getArray());
				Matrix P = Z.times(beta);
				exp(P.getArray());
				Matrix SurvivalFn = new Matrix(n,full_n,0);
				for(i=0;i<n;i++){
					for(j=0;j<full_n;j++){
						SurvivalFn.set(i,j,Math.pow(fn.get(i,0),P.get(j,0)));
					}
				}
				//ResultString=ResultString+"<table><tr><td>Survival Time</td><td>Survival Function</td></tr>";
				OutputString=OutputString+"\tTime\tSurvival Function\n";
				System.out.println("\tTime\tSurvival Function");
				for(i=0;i<n;i++){
					System.out.print("\t"+sur.get(i,0));
					//ResultString=ResultString+"<tr><td>"+sur.get(i, 0)+"</td>";
					OutputString=OutputString+"\t"+sur.get(i, 0);
					for (j=0; j<full_n; j++) {
						//System.out.println("\t"+sur.get(i, 0)+"\t"+SurvivalFn.get(i, 0));
						OutputString=OutputString+"\t"+SurvivalFn.get(i,j);
						//ResultString=ResultString+"<td>"+SurvivalFn.get(i,j)+"</td>";
						System.out.print("\t"+SurvivalFn.get(i,j));
					}OutputString=OutputString+"\n";
					//ResultString=ResultString+"</tr>";
					System.out.print("\n");
				}
				//ResultString=ResultString+"</table>";
				
				for(j=0;j<full_n;j++){
					if(j==0){
						Yaxis = Yaxis + "[{";
					}else{
						Yaxis = Yaxis + ",{";
					}
					Yaxis = Yaxis + "name: 'Record_" + (j+1) +"', data: [";
					for(i=0;i<n;i++){
						if(i==n-1){
							Yaxis = Yaxis + SurvivalFn.get(i,j) + "]}";
						}else{
							Yaxis = Yaxis + SurvivalFn.get(i,j) + ",";
						}
					}
				}Yaxis = Yaxis + "]";
				
				System.out.println("Yaxis is: "+Yaxis);
				System.out.println("Xaxis is: "+Xaxis);
				//System.out.println("ResultString is: "+ResultString);
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