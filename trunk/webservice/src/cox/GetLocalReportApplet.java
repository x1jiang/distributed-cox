package cox;
//061213

import Jama.Matrix;
import java.applet.AppletContext;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;

import java.io.*;
import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Cursor;
//import java.awt.Graphics;
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;

import java.net.URL;


//import java.sql.*;
import java.net.HttpURLConnection;
//import java.net.URLConnection;
//import java.net.MalformedURLException;
//import java.awt.*;
import java.awt.event.*;
//import java.applet.*;
import javax.swing.*;
//import javax.swing.border.*;

//import java.security.AccessController;
//import java.security.PrivilegedAction;

import java.util.Vector;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JPanel;

public class GetLocalReportApplet extends JApplet
        implements ActionListener
{
    private JButton getLocalReport;
    private final String GLR = "GLR";
    private String dataPath = null;
    private String email = null;
    private String taskName = null;
    private String root_property;
    private int maxIteration;
    private double epsilon;
    private int taskStatus;
    private String property;
    private String URL = null;

    public void init()
    {
        try {
            setSize(100, 100);
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void jbInit()
            throws Exception
    {
        JPanel filePane = new JPanel();
        this.getLocalReport = new JButton("Get Local Report");
        this.getLocalReport.setEnabled(false);
        this.getLocalReport.setActionCommand("GLR");
        this.getLocalReport.addActionListener(this);
        filePane.add(this.getLocalReport);
        add(filePane, "North");
/*
	private void jbInit() throws Exception
	{
		JPanel filePane = new JPanel();
		getLocalReport = new JButton("Get Local Report");
		getLocalReport.setEnabled(false);
		getLocalReport.setActionCommand(GLR);
		getLocalReport.addActionListener(this);
		filePane.add(getLocalReport);
		add(filePane, BorderLayout.NORTH);
		
		email = getParameter("email");
		dataPath = getParameter("dataPath");
		taskName = getParameter("taskName");
		root_property = getParameter("root_property");
		maxIteration=Integer.parseInt(getParameter("maxIteration"));
		epsilon =Double.valueOf(getParameter("epsilon")) ;
		taskStatus = Integer.parseInt(getParameter("taskStatus"));
		property = getParameter("property");
		createReportAddress = getParameter("createReportAddress");
		
		System.out.println("all Parameters: "+ email + "\t" + dataPath + "\t" + taskName + "\t" + root_property + "\t"
		+ maxIteration + "\t" + epsilon + "\t" + taskStatus + "\t" + property);
		
//		email="w6jiang@ucsd.edu";
//		dataPath="C:/Users/Wenchao/Documents/ca_part2";
//		taskName="c20";
//		maxIteration = 20;
//		epsilon = 0.000001;
//		property = "A1\tA2\tA3";
		
	}
*/

        this.email = getParameter("email");
        this.dataPath = getParameter("dataPath");
        this.taskName = getParameter("taskName");
        this.root_property = getParameter("root_property");
        this.maxIteration = Integer.parseInt(getParameter("maxIteration"));
        this.epsilon = Double.valueOf(getParameter("epsilon")).doubleValue();
        this.taskStatus = Integer.parseInt(getParameter("taskStatus"));
        this.property = getParameter("property");
    }

    public void actionPerformed(ActionEvent ae)
    {
        if (ae.getActionCommand().equals("GLR"))
            createLocalReport();
    }

    private void createLocalReport()
    {
        try
        {
            String[] vars = this.property.split("#");
            int fm = vars.length;
            System.out.println("fm is: " + fm);

            Vector Xv = new Vector();
            Vector Yv = new Vector();
            int m = -1;

            int iter = 0;

            String file_name = this.dataPath;
            System.out.println("Using data file '" + file_name + "'.");

            FileInputStream file_stream = new FileInputStream(file_name);
            DataInputStream file_in = new DataInputStream(file_stream);
            BufferedReader file_br = new BufferedReader(new InputStreamReader(file_in));

            int n = 0;

            file_br.readLine();
            String file_line;
            while ((file_line = file_br.readLine()) != null)
            {
                // String file_line;
                n++;
                String[] line_tokens = file_line.split("\t");

                if (m == -1) {
                    m = line_tokens.length;
                }
                else if (m != line_tokens.length) {
                    System.out.println("ERROR: data file dimensions don't match on line " +
                            n + ".");
                    System.exit(-1);
                }

                Vector xrow = new Vector();
                xrow.add(Double.valueOf(1.0D));
                for (int i = 0; i < line_tokens.length - 1; i++) {
                    xrow.add(new Double(line_tokens[i]));
                }
                Xv.add(xrow);
                Yv.add(new Double(line_tokens[(line_tokens.length - 1)]));
            }

            file_in.close();

            Matrix beta0 = new Matrix(m, 1, -1.0D);
            Matrix beta1 = new Matrix(m, 1, 0.0D);
            Matrix cov_matrix = new Matrix(m, m);
            Matrix aucValue = new Matrix(1, 1, 0.0D);

            double[][] Xa = two_dim_vec_to_arr(Xv);
            double[] Ya = one_dim_vec_to_arr(Yv);

            Matrix X = new Matrix(Xa);
            Matrix Y = new Matrix(Ya, Ya.length);

            System.out.println("maximum Iteration:" + this.maxIteration);

            while (max_abs(beta1.minus(beta0).getArray()) > this.epsilon)
            {
                if (iter == this.maxIteration)
                {
                    System.out.println("Has reached the maximum iteration number!");
                    break;
                }

                System.out.println("value: " + max_abs(beta1
                        .minus(beta0).getArray()));

                System.out.println("Iteration " + iter);

                beta0 = beta1.copy();

                Matrix P = X.times(-1.0D).times(beta0);
                exp(P.getArray());
                add_one(P.getArray());
                div_one(P.getArray());

                Matrix W = P.copy();
                W.timesEquals(-1.0D);
                add_one(W.getArray());
                W.arrayTimesEquals(P);
                W = W.transpose();
                W = diag(W.getArray()[0]);

                Matrix D = X.transpose().times(W).times(X);

                Matrix E = X.transpose().times(Y.plus(P.uminus()));

                Matrix temp_a = E;
                Matrix temp_b = D;
                Matrix temp_c = diag(1.0E-007D, fm);

                temp_b = temp_b.plus(temp_c);
                temp_b = temp_b.inverse();
                temp_b = temp_b.times(temp_a);
                beta1 = beta0.plus(temp_b);

                beta1.print(8, 8);
                iter++;
            }

            Matrix hat_beta = beta1.copy();

            Matrix P = X.times(-1.0D).times(hat_beta);
            exp(P.getArray());
            add_one(P.getArray());
            div_one(P.getArray());

            Matrix W = P.copy();
            W.timesEquals(-1.0D);
            add_one(W.getArray());
            W.arrayTimesEquals(P);
            W = W.transpose();
            W = diag(W.getArray()[0]);

            Matrix D = X.transpose().times(W).times(X);

            Matrix temp_b = D;
            Matrix temp_c = diag(1.0E-007D, m);

            temp_b = temp_b.plus(temp_c);
            cov_matrix = temp_b.inverse();

            Matrix scoreMatrix = X.times(beta1);
            double[][] combine = new double[n][2];
            for (int i = 0; i < n; i++) {
                combine[i][0] = (1.0D / (1.0D + Math.pow(2.718281828459045D, -1.0D * scoreMatrix.get(i, 0))));
                combine[i][1] = Y.get(i, 0);
            }
            Matrix aucData = new Matrix(combine);
            Vector aucDataCombine = new Vector();
            for (int j = 0; j < aucData.getRowDimension(); j++) {
                double[] row = new double[2];
                row[0] = aucData.get(j, 0);
                row[1] = aucData.get(j, 1);
                aucDataCombine.add(row);
            }

            sort(aucDataCombine);

            String BOUNDARY = "---------WebKitFormBoundaryL1WMwaoHvOv9WaJT";

            URL url = new URL("http://dbmi-engine.ucsd.edu/webcalibsis/upload3.php?taskName=" + this.taskName + "_" + this.email);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            conn.setRequestProperty("Charsert", "UTF-8");

            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            OutputStream out = new DataOutputStream(conn.getOutputStream());
            byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            StringBuilder sb = new StringBuilder();
            sb.append("--");
            sb.append(BOUNDARY);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\"predictionFile\";filename=\"try1\"\r\n");
            sb.append("Content-Type:text/plain\r\n\r\n");
            byte[] data = sb.toString().getBytes();
            out.write(data);
            for (int i = 0; i < aucDataCombine.size(); i++) {
                System.out.println(((double[])aucDataCombine.get(i))[0] + "\t" + ((double[])aucDataCombine.get(i))[1]);
                out.write((((double[])aucDataCombine.get(i))[0] + "\t" + ((double[])aucDataCombine.get(i))[1]).getBytes());
                out.write("\n".getBytes());
            }
            out.write("\r\n".getBytes());
            sb = new StringBuilder();
            sb.append("--");
            sb.append(BOUNDARY);
            sb.append("\r\n");

            sb.append("Content-Disposition: form-data;name=\"modelFile\";filename=\"try2\"\r\n");
            sb.append("Content-Type:text/plain\r\n\r\n");
            data = sb.toString().getBytes();
            out.write(data);

            for (int i = 0; i < vars.length - 1; i++) {
                out.write((vars[i] + ", ").getBytes());
            }
            out.write(vars[(vars.length - 1)].getBytes());
            out.write("\n".getBytes());

            for (int i = 0; i < beta1.getRowDimension() - 1; i++) {
                out.write((beta1.get(i, 0) + ", ").getBytes());
            }
            out.write((beta1.get( (beta1.getRowDimension() -1), 0) +"").getBytes());
            out.write("\n".getBytes());

            for (int i = 0; i < cov_matrix.getRowDimension(); i++) {
                for (int j = 0; j < cov_matrix.getColumnDimension() - 1; j++) {
                    out.write((cov_matrix.get(i, j) + ", ").getBytes());
                }
                out.write((cov_matrix.get(i, (cov_matrix.getColumnDimension() -1)) +"").getBytes());
                out.write("\n".getBytes());
            }

            out.write(end_data);
            out.flush();
            out.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            System.out.println("URL is :" + conn.getURL());
            this.URL = conn.getURL().toString();
            getAppletContext().showDocument(conn.getURL());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getURL() {
        return this.URL;
    }

    public void setEnable() {
        this.getLocalReport.setEnabled(true);
    }

    private double max_abs(double[][] matrix)
    {
        boolean set = false;
        double max = 0.0D;

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++)
            {
                if (!set) {
                    max = Math.abs(matrix[i][j]);
                    set = true;
                }
                else if (Math.abs(matrix[i][j]) > max) {
                    max = Math.abs(matrix[i][j]);
                }
            }
        }

        return max;
    }

    private double[][] two_dim_vec_to_arr(Vector<Vector<Double>> V)
    {
        double[][] A = new double[V.size()][];

        for (int i = 0; i < V.size(); i++) {
            A[i] = one_dim_vec_to_arr((Vector)V.get(i));
        }

        return A;
    }

    private double[] one_dim_vec_to_arr(Vector<Double> V)
    {
        int size = V.size();

        double[] A = new double[size];

        for (int i = 0; i < size; i++) {
            A[i] = ((Double)V.get(i)).doubleValue();
        }

        return A;
    }

    private void exp(double[][] A)
    {
        for (int i = 0; i < A.length; i++)
            for (int j = 0; j < A[i].length; j++)
                A[i][j] = Math.exp(A[i][j]);
    }

    private void add_one(double[][] A)
    {
        for (int i = 0; i < A.length; i++)
            for (int j = 0; j < A[i].length; j++)
                A[i][j] = (1.0D + A[i][j]);
    }

    private void div_one(double[][] A)
    {
        for (int i = 0; i < A.length; i++)
            for (int j = 0; j < A[i].length; j++)
                A[i][j] = (1.0D / A[i][j]);
    }

    private Matrix diag(double[] A)
    {
        int n = A.length;

        Matrix M = new Matrix(n, n, 0.0D);
        for (int i = 0; i < n; i++) {
            M.set(i, i, A[i]);
        }
        return M;
    }

    private Matrix diag(double v, int n)
    {
        double[][] A = new double[n][n];
        for (int i = 0; i < n; i++) {
            A[i][i] = v;
        }
        return new Matrix(A);
    }
    public void sort(Vector<double[]> v) {
        for (int i = 0; i < v.size(); i++)
            for (int j = i; j < v.size(); j++)
                if (((double[])v.get(i))[0] > ((double[])v.get(j))[0])
                {
                    double temp1 = ((double[])v.get(i))[0];
                    double temp2 = ((double[])v.get(i))[1];
                    ((double[])v.get(i))[0] = ((double[])v.get(j))[0];
                    ((double[])v.get(i))[1] = ((double[])v.get(j))[1];
                    ((double[])v.get(j))[0] = temp1;
                    ((double[])v.get(j))[1] = temp2;
                }
    }
}