package com.hibu.bragger.wsdl;

public class BraggerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BraggerException() {
	}

	public BraggerException(String arg0) {
		super(arg0);
	}

	public BraggerException(Throwable arg0) {
		super(arg0);
	}

	public BraggerException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
