package cox;
import java.io.Serializable;
import Jama.Matrix;
//061213
/**
 * This data structure is used to store a matrix for beta and covariance
 * @author Wenchao
 *
 */
public class DataOut2 implements Serializable{

	private Matrix matrix=null;		//This m can be beta and covariance
	private String type=null;		//"beta" or "cov"
	private String taskName = null;
	
	public String getTaskName() {
		return taskName;
	}
	public DataOut2(Matrix matrix, String type, String taskName) {
		super();
		this.matrix = matrix;
		this.type = type;
		this.taskName = taskName;
	}
	public Matrix getMatrix() {
		return matrix;
	}
	public String getType() {
		return type;
	}

}
