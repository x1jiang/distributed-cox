package cox;
import java.io.Serializable;
import java.util.Vector;

import Jama.Matrix;

/**
 * This class is used for Client to send its computation result d and e
 * @author Wenchao,Challen
 *
 */
public class DataIn2 implements Serializable{
	private static final long serialVersionUID = 4914067667825094632L;
	private Matrix D=null;		// theta
	private Matrix E=null;		// thetaZ or beta
	private Vector< Matrix > F=null; //thetaZZ
	private Matrix G=null;		// sumZ
	private Matrix H=null;		// DI
	private String taskName = null;
	private String type = null;	//beta or sur or fn or end 


	public DataIn2(Matrix d, Matrix e, Vector< Matrix > f, Matrix g, Matrix h, String taskName, String type) {
		super();
		D = d;
		E = e;
		F = f;
		G = g;
		H = h;
		this.taskName = taskName;
		this.type = type;
	}
	public Matrix getD() {
		return D;
	}
	public Matrix getE() {
		return E;
	}
	public Vector< Matrix > getF() {
		return F;
	}
	public Matrix getG() {
		return G;
	}
	public Matrix getH() {
		return H;
	}
	public String getTaskName() {
		return taskName;
	}
	public String getType() {
		return type;
	}

}
