package cox;

import java.io.Serializable;

public class DataIn implements Serializable
{
	private String name = null;
	private String passWord = null;
	private String task_ = null;
	private String email_ = null;
	private String kernel_ = null;
	private String noFeature_ = null;
	private String noRecord_ = null;
	private String target_ = null;
	
	public DataIn (String name, String passWord, String task, String email, String kernel, String noFeature, String noRecord, String target)
	{
		super();
		this.name = name;
		this.passWord = passWord;
		task_ = task;
		email_ = email;
		kernel_ = kernel;
		noFeature_ = noFeature;
		noRecord_ = noRecord;
		target_ = target;
	}
	
	public DataIn (String task, String email, String kernel, String noFeature, String noRecord, String target)
	{
		super();
		task_ = task;
		email_ = email;
		kernel_ = kernel;
		noFeature_ = noFeature;
		noRecord_ = noRecord;
		target_ = target;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getTarget()
	{
		return target_;
	}
	
	public String getTask()
	{
		return task_;
	}

	public String getEmail()
	{
		return email_;
	}

	public String getKernel()
	{
		return kernel_;
	} 
	
	public String getNoFeature()
	{
		return noFeature_;
	}
	
	public String getNoRecord()
	{
		return noRecord_;
	}
}
