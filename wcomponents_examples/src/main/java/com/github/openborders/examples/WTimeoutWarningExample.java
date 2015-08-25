package com.github.openborders.examples;

import javax.servlet.http.HttpSession;

import com.github.openborders.WContainer;
import com.github.openborders.WTimeoutWarning;

import com.github.openborders.examples.common.ExplanatoryText;
/**
 * Demonstrate WTimeoutWarning
 * <p>
 * The UI will display a "warning" message to the user that their session will soon expire. Once the session timeout has
 * elapsed, an "expired" message will be displayed to the user.
 * </p>
 * <p>
 * The timeout value is usually set to the session timeout {@link HttpSession#getMaxInactiveInterval()}, but to
 * demonstrate the messages, the timeout has been set to 120 seconds.
 * </p>
 * 
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WTimeoutWarningExample extends WContainer
{

    private static final int TIMEOUT_PERIOD = 120;
    
    private static final int WARNING_PERIOD = 30;
    
    public WTimeoutWarningExample()
    {
        add(new WTimeoutWarning(TIMEOUT_PERIOD, WARNING_PERIOD));
        
        add(new ExplanatoryText("This is a demonstration of the 'session timeout' warning. It will display a warning message after 90 seconds and a session timeout message after 120 seconds. It does not actually end your session."));
    }

}