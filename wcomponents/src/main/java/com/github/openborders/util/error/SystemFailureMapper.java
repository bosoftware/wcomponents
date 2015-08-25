package com.github.openborders.util.error;

import com.github.openborders.Message;

/**
 * Interface that defines the mapping from system level exception to user 
 * friendly message. 
 * 
 * @author Binusha Perera
 * @since 1.0.0
 * 
 * @see com.github.openborders.util.error.impl.DefaultSystemFailureMapper
 */
public interface SystemFailureMapper
{
    /**
     * Convert a system exception to a user friendly error message.
     * 
     * @param throwable the exception to convert.
     * @return ErrorMessage object that captures the user friendly description.
     */
    Message toMessage(Throwable throwable);
}