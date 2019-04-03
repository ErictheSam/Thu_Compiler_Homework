package decaf.error;

import decaf.Location;

/**
 * example：undeclared variable 'python'<br>
 * PA2
 */
public class UndeclVarError extends DecafError {

	private String name;

	public UndeclVarError(Location location, String name) {//只有一个Varible和一个Error
		super(location);
		this.name = name;
	}

	@Override
	protected String getErrMsg() {
		return "undeclared variable '" + name + "'";
	}

}
