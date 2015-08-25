package com.github.openborders; 

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;

import com.github.openborders.util.DebugUtil;

/** 
 * <p>This class holds the current (active) UIContext.</p>
 * 
 * <p>WComponents store user (session) component models within the UIContext, and the 
 * UIContextHolder provides a central location for components to read from. It is the 
 * responsibility of the WComponent container (e.g. Servlet) to initialise the holder 
 * at the start of each WComponent lifecycle, and clean it up afterwards.</p>
 * 
 * <p>Applications should not normally need to use this class directly. If application
 * code needs access to the UIContext, it must only call {@link #getCurrent()}. Do not
 * call any other method within this class.</p>
 * 
 * <p>The UIContextHolder uses a stack internally, to allow for components which utilise
 * "SubUIContexts", for repeated data.</p> 
 * 
 * @author Yiannis Paschalidis 
 * @since 1.0.0
 */
public class UIContextHolder
{
    /** 
     * The UIContexts are stored on a thread-local. This is safe, as WComponent request
     * processing and rendering are single-threaded per user.
     */
    private static ThreadLocal<Stack<UIContext>> contextStack = new ThreadLocal<Stack<UIContext>>();
    
    /** 
     * This is used for debugging only. It contains all UIContexts which are in an active thread. 
     */
    //TODO: JDK1.6+ Set<UIContxt> allContexts = Collections.newSetFromMap(new WeakHashMap<UIContext, Boolean>());
    private static Map<UIContext, ?> allActiveContexts = Collections.synchronizedMap(new WeakHashMap<UIContext, Object>());
    
    /**
     * Pushes a context onto the top of stack. This method is called by internal
     * WComponent containers (e.g. WServlet) at the start of processing a request.
     * and by components which repeat content. 
     * 
     * @param uic the UIContext to push.
     */
    public static void pushContext(final UIContext uic)
    {
        if (DebugUtil.isDebugFeaturesEnabled())
        {
            allActiveContexts.put(UIContextDelegate.getPrimaryUIContext(uic), null);
        }
        
        getStack().push(uic);
    }
    
    /** 
     * For debugging purposes only - returns all active UIContexts.
     * @return all active UIContexts within the VM. 
     */
    static List<UIContext> getActiveContexts()
    {
        if (DebugUtil.isDebugFeaturesEnabled())
        {
            return new ArrayList<UIContext>(allActiveContexts.keySet());
        }
        
        return null;
    }
    
    /**
     * Removes a context from the top of the stack. This method is only
     * called by components which repeat content. 
     * 
     * @return the UIContext which was removed.
     */
    public static UIContext popContext()
    {
        return getStack().pop();
    }
    
    /**
     * Retrieves the current effective UIContext. Note that this method will return null
     * if called from outside normal request processing, for example during the intial
     * application UI construction. 
     * 
     * @return the current effective UIContext.
     */
    public static UIContext getCurrent()
    {
        Stack<UIContext> stack = contextStack.get();
        
        if (stack == null || stack.isEmpty())
        {
            return null;
        }
        
        return getStack().peek();
    }
    
    /**
     * Retrieves the internal stack, creating it if necessary.
     * @return the internal stack
     */
    private static Stack<UIContext> getStack()
    {
        Stack<UIContext> stack = contextStack.get();
        
        if (stack == null)
        {
            stack = new Stack<UIContext>();
            contextStack.set(stack);
        }
        
        return stack;
    }

    /**
     * Clears the UIContext stack. This method is called by internal
     * WComponent containers (e.g. WServlet) after a request has been processed. 
     */
    public static void reset()
    {
        if (DebugUtil.isDebugFeaturesEnabled())
        {
            UIContext context = getCurrent();
            allActiveContexts.remove(context);
        }
        
        contextStack.remove();
    }
}