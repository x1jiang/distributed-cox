package cox;
//061213
import java.io.Serializable;

public class DataOut implements Serializable
{
	private String name_ = null;
    private String data_ = null;

	public DataOut (String name, String data)
	{
		super();
		name_ = name;
		data_ = data;
		
	}

	public String getData()
	{
		return data_;
	}

	public String getName()
	{
		return name_;
	}
}
