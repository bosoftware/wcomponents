package com.github.openborders.validation; 

import java.io.Serializable;

import com.github.openborders.UIContext;
import com.github.openborders.WComponent;

/**
 * <p>Stores an error, warning or informational message produced as part of WComponent validation.
 * Each diagnostic will can be associated with particular user context and component.</p>
 * 
 * <p>In the case of a cross-field error the component may not be able to be specified, and may be null.</p> 
 * 
 * @author James Gifford
 * @since 1.0.0
 */
public interface Diagnostic extends Serializable
{
    /** Indicates that the diagnostic is an "informational message". */
    int INFO = 0;

    /** Indicates that the diagnostic is a "warning message". */
    int WARNING = 1;

    /** Indicates that the diagnostic is an "error message". */
    int ERROR = 2;

    /**
     * @return an indicator of the severity of the problem.
     */
    int getSeverity();

    /**
     * Retrieves the formatted error message.
     * @return a format message describing the situation.
     */
    String getDescription();
    
    /**
     * The user context in which the Diagnostic was issued.
     * @return the context in which the diagnostic occurred.
     */
    UIContext getContext();

    /**
     * The "field "in the Business Object that produced the diagnostic, if there is one.
     * This may be null in the case of cross field validation diagnostics.
     * @return the field associated with the diagnostic, or null.
     */
    WComponent getComponent();
}