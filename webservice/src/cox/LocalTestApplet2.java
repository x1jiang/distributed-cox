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
//062613

public class LocalTestApplet2 extends JApplet implements ActionListener{
	private JScrollPane scrolling = null;
	private JTextPane fileBox = null;
	private JButton butFile = null;
	private JTextField tfFilename = null;
	private JButton butLoad = null;
	private final String LOAD = "load";
	private final String FILE = "file";
	
	private String RorF = "byRecords";	//RecordFeature
	private String FS= "0#3#6";		//FeatureStr
	private String CS ="66#0#0";	//CutoffStr
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
	
	private String Xaxis="";
	private String Yaxis="";
	private int Step=0;
	
	private Matrix range;
	private Matrix SurvivalFn;
	private Matrix sur, Z;
	private int n, full_n=0;
	
	private String property_ = null;
	private String fileName_ = "";
	private String OutputStr ="";
	
	public void init() {
		try{
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
			if(getParameter("RecordFeature")!=null){
				RorF = getParameter("RecordFeature");
			}
			if(getParameter("FeatureStr")!=null){
				FS = getParameter("FeatureStr");
			}
			if(getParameter("CutoffStr")!=null){
				CS = getParameter("CutoffStr");
			}
			setSize(100, 400);
			setBackground(new Color(255, 255, 255));
			jbInit();	
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	//get parameters from Test2.jsp
	public void sendRorF(String s){	RorF = s;}
	public void sendFS(String s){	FS = s;}
	public void sendCS(String s){	CS = s;}
	public String getYaxis(){	return Yaxis;}
	public String getXaxis(){	return Xaxis;}
	public int getStep(){		return Step;}
	public String getRorF(){	return RorF;}
	public String getFSCS(){	return FS+"**"+CS;}
	public String getDim(){		return n+"**"+full_n;}
	public String getPro(){		return property_;}
	public String getFile(){	return fileName_;}
	public String getOut(){		return OutputStr;}
	
	private void jbInit() throws Exception{
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
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(FILE)){
			Frame f=new Frame();
			FileDialog fd = new FileDialog(f, "Select File", FileDialog.LOAD);
			fd.setVisible(true);
			if(fd.getFile()!=null)
			{
				fileName_ = fd.getDirectory() + fd.getFile();
			}
			tfFilename.setText(fileName_);
			
			try{
				FileInputStream data = new FileInputStream(fileName_);
				DataInputStream data_file = new DataInputStream (data);
				property_ =data_file.readLine();			//added by lph
				System.out.println("Property: " + property_);
				data_file.close();			
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
				int m=-1, i, j;
				
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
				
				//calculate range of data
				range = new Matrix(3,m,0);
				range.setMatrix(1,1,0,m-1,Z.getMatrix(0,0,0,m-1));
				range.setMatrix(2,2,0,m-1,Z.getMatrix(0,0,0,m-1));
				int isBinary;
				for(i=0;i<Z.getColumnDimension();i++){
					isBinary = 1;
					for(j=0;j<Z.getRowDimension();j++){
						if(Z.get(j,i)!=0 && Z.get(j,i)!=1){
							isBinary = 0;
						}
						if(Z.get(j,i)>range.get(1,i)){
							range.set(1,i,Z.get(j, i));
						}
						if(Z.get(j,i)<range.get(2,i)){
							range.set(2,i,Z.get(j, i));
						}
					}
					if(isBinary == 1){
						range.set(0,i,1);
					}
				}
				
				//get beta, sur, fn
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
				sur = new Matrix(surValue);
				
				//get Xaxis
				n= ss.length;
				Step = n/10;
				Xaxis = Xaxis + "[";
				for(i=0;i<n-1;i++){
					Xaxis = Xaxis + sur.get(i,0) + ", ";
				}
				Xaxis = Xaxis + sur.get(n-1,0) + "]";		
				
				//calculate survival function
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
				
				//get property
				String[] proStr = property_.split("\t");	
				
				//get OutputStr, Yaxis
				if (RorF.equals("byRecords")){
					OutputStr=OutputStr+"\tTime\tSurvival Function\r\n";
					for(i=0;i<n;i++){
						OutputStr=OutputStr+"\t"+sur.get(i, 0);
						for (j=0; j<full_n; j++) {
							OutputStr=OutputStr+"\t"+SurvivalFn.get(i,j);
						}OutputStr=OutputStr+"\r\n";
					}
					for(j=0;j<full_n;j++){
						if(j==0){
							Yaxis = Yaxis + "[{";
						}else{
							Yaxis = Yaxis + ",{";
						}
						Yaxis = Yaxis + "name: 'Record_" + (j+1) +"', data: [";
						for(i=0;i<n;i++){
							if(i==n-1){
								Yaxis = Yaxis + Math.rint(SurvivalFn.get(i,j)*10000)/100 + "]}";
							}else{
								Yaxis = Yaxis + Math.rint(SurvivalFn.get(i,j)*10000)/100 + ",";
							}
						}
					}Yaxis = Yaxis + "]";
				}else if (RorF.equals("byFeatures")){
					//get feature, cutoff
					String[] feaStr = FS.split("#");
					double[][] feaValue = new double[feaStr.length][1];
					for(i=0; i<feaStr.length; i++){
						feaValue[i][0] = Double.parseDouble(feaStr[i]);
					}
					Matrix fea = new Matrix(feaValue);
					String[] cutStr = CS.split("#");
					double[][] cutValue = new double[cutStr.length][1];
					for(i=0; i<cutStr.length; i++){
						cutValue[i][0] = Double.parseDouble(cutStr[i]);
					}
					Matrix cut = new Matrix(cutValue);
					//produce FeatureBaseFn
					Matrix FeatureBaseFn = new Matrix(n,feaStr.length*2,0);
					Matrix temp_a;
					Matrix temp_b;
					int flag_a,flag_b;
					for(int k=0;k<feaStr.length;k++){
						temp_a = new Matrix(n,1,0);
						temp_b = new Matrix(n,1,0);
						flag_a=0; flag_b=0;
						if(range.get(0,(int)feaValue[k][0])==1){	//Feature is binary
							System.out.println("Feature is binary");
							for(j=0; j<full_n; j++){
								if(Z.get(j,(int)feaValue[k][0])==0){
									temp_a.plusEquals(SurvivalFn.getMatrix(0,n-1,j,j));
									flag_a += 1;
								}else{
									temp_b.plusEquals(SurvivalFn.getMatrix(0,n-1,j,j));
									flag_b += 1;
								}
							}
						}else{										//Feature is continuous
							//if cutoff isn't within range, make it middle number
							if(cutValue[k][0]>range.get(1,(int)feaValue[k][0]) 
									|| cutValue[k][0]<range.get(2,(int)feaValue[k][0])){
								cutValue[k][0] = (range.get(1,(int)feaValue[k][0]) + 
										range.get(2,(int)feaValue[k][0]))/2;
							}
							for(j=0; j<full_n; j++){
								if(Z.get(j,(int)feaValue[k][0])<cutValue[k][0]){
									temp_a.plusEquals(SurvivalFn.getMatrix(0,n-1,j,j));
									flag_a += 1;
								}else{
									temp_b.plusEquals(SurvivalFn.getMatrix(0,n-1,j,j));
									flag_b += 1;
								}
							}
						}
						if(flag_a!=0){
							for(i=0;i<n;i++){
								FeatureBaseFn.set(i,2*k,temp_a.get(i,0)/flag_a);
							}
						}
						if(flag_b!=0){
							for(i=0;i<n;i++){
								FeatureBaseFn.set(i,2*k+1,temp_b.get(i,0)/flag_b);
							}
						}					
					}			
					FeatureBaseFn.print(3, 3);
					//get OutputStr
					OutputStr=OutputStr+"\tTime";
					for(int k=0;k<feaStr.length;k++){
						if(range.get(0,(int)feaValue[k][0])==1){
							OutputStr=OutputStr+"\t"+proStr[(int)feaValue[k][0]]+"=0";
							OutputStr=OutputStr+"\t"+proStr[(int)feaValue[k][0]]+"=1";
						}else{
							OutputStr=OutputStr+"\t"+proStr[(int)feaValue[k][0]]+"<"+cutValue[k][0];
							OutputStr=OutputStr+"\t"+proStr[(int)feaValue[k][0]]+">"+cutValue[k][0];
						}
					}OutputStr=OutputStr+"\r\n";
					for(i=0;i<n;i++){
						OutputStr=OutputStr+"\t"+sur.get(i, 0);
						for(int k=0;k<(feaStr.length)*2;k++){
							OutputStr=OutputStr+"\t"+FeatureBaseFn.get(i,k);
						}OutputStr=OutputStr+"\r\n";
					}
					
					//get Yaxis
					for(int k=0;k<(feaStr.length)*2;k++){
						if(k==0){
							Yaxis = Yaxis + "[{";
						}else{
							Yaxis = Yaxis + ",{";
						}
						if(range.get(0,(int)feaValue[k/2][0])==1){
							Yaxis = Yaxis + "name: '" + proStr[(int)feaValue[k/2][0]]+"="+ (k%2) +"', data: [";
						}else{
							if(k%2==0){
								Yaxis = Yaxis + "name: '" + proStr[(int)feaValue[k/2][0]]+"<"+cutValue[k/2][0]+"', data: [";
							}else{
								Yaxis = Yaxis + "name: '" + proStr[(int)feaValue[k/2][0]]+">"+cutValue[k/2][0]+"', data: [";
							}
						}
						for(i=0;i<n;i++){
							if(i==n-1){
								Yaxis = Yaxis + Math.rint(FeatureBaseFn.get(i,k)*10000)/100 + "]}";
							}else{
								Yaxis = Yaxis + Math.rint(FeatureBaseFn.get(i,k)*10000)/100 + ",";
							}
						}
					}Yaxis = Yaxis + "]";
				}else{
					OutputStr = OutputStr + "never enter byRecords or byFeatures";
				}
				System.out.println(Xaxis);
				System.out.println(Yaxis);
				System.out.println(Step);
				System.out.println(OutputStr);
				getAppletContext().showDocument(new URL("javascript:window.accessAppletResult()"));

				Frame f=new Frame();
				FileDialog fd = new FileDialog(f, "Save Test Result", FileDialog.SAVE);
				fd.setVisible(true);
				try{
					 File f1 = new File(fd.getDirectory(), fd.getFile());
					 BufferedWriter out = new BufferedWriter(new FileWriter(f1));
					 out.write(OutputStr);
					 out.close();
					 OutputStr = "";
				}catch (IOException e2){
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