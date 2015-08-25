package com.github.openborders.container;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.openborders.ErrorCodeEscape;
import com.github.openborders.Escape;
import com.github.openborders.Request;
import com.github.openborders.UIContextHolder;
import com.github.openborders.util.I18nUtilities;
import com.github.openborders.util.InternalMessages;

/**
 * This interceptor catches exceptions and sets them as an error code.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class AjaxErrorInterceptor extends InterceptorComponent
{
    /** The logger instance for this class. */
    private static final Log log = LogFactory.getLog(AjaxErrorInterceptor.class);

    /** {@inheritDoc} */
    @Override
    public void serviceRequest(final Request request)
    {
        try
        {
            super.serviceRequest(request);
        }
        catch (Escape escape)
        {
            throw escape;
        }
        catch (Exception e)
        {
            log.error("Error processing AJAX request in action phase. " + e.getMessage(), e);
            handleError();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void preparePaint(final Request request)
    {
        try
        {
            super.preparePaint(request);
        }
        catch (Escape escape)
        {
            throw escape;
        }
        catch (Exception e)
        {
            log.error("Error processing AJAX request in prepare paint. " + e.getMessage(), e);
            handleError();
        }
    }

    /**
     * Throw the default error code.
     */
    private void handleError()
    {
        String msg = I18nUtilities
            .format(UIContextHolder.getCurrent().getLocale(), InternalMessages.DEFAULT_AJAX_ERROR);
        throw new ErrorCodeEscape(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg);
    }
}