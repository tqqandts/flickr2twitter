/*
 * Copyright (c) 2005 Aetrion LLC.
 */
package com.gmail.yuyang226.autoflickr2twitter.com.aetrion.flickr;

import java.io.Serializable;

/**
 * Exception which wraps a Flickr error.
 *
 * @author Anthony Eden
 */
public class FlickrException extends Exception implements Serializable {

	private static final long serialVersionUID = 7958091410349084831L;
	private String errorCode;
	private String errorMessage;
	
    public FlickrException() {
		super();
	}

	public FlickrException(String errorCode, String errorMessage) {
        super(errorCode + ": " + errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
