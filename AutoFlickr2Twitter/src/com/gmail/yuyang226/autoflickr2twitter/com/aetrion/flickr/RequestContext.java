/*
 * Copyright (c) 2005 Aetrion LLC.
 */

package com.gmail.yuyang226.autoflickr2twitter.com.aetrion.flickr;

import java.util.ArrayList;
import java.util.List;

import com.gmail.yuyang226.autoflickr2twitter.com.aetrion.flickr.auth.Auth;

/**
 * A thread local variable used to hold contextual information used in requests.  To get an instance of this class use
 * RequestContext.getRequestContext().  The method will return a RequestContext object which is only usable within the
 * current thread.
 *
 * @author Anthony Eden
 */
public class RequestContext {

    private static RequestContextThreadLocal threadLocal =
            new RequestContextThreadLocal();

    private Auth auth;
    private List<String> extras;

    /**
     * Get the RequestContext instance for the current Thread.
     *
     * @return The RequestContext
     */
    public static RequestContext getRequestContext() {
        return (RequestContext) threadLocal.get();
    }

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    /**
     * Get the List of extra return values requested.
     *
     * @return List of extra return values requested
     */
    public List<String> getExtras() {
        if (extras == null) extras = new ArrayList<String>();
        return extras;
    }

    public void setExtras(List<String> extras) {
        this.extras = extras;
    }

    private static class RequestContextThreadLocal extends ThreadLocal<RequestContext> {

        protected RequestContext initialValue() {
            return new RequestContext();
        }

    }

}