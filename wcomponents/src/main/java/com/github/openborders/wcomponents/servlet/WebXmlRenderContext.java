package com.github.openborders.wcomponents.servlet; 

import java.io.PrintWriter;
import java.util.Locale;

import com.github.openborders.wcomponents.RenderContext;
import com.github.openborders.wcomponents.XmlStringBuilder;

/** 
 * The WComponent web-xml render context. 
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WebXmlRenderContext implements RenderContext
{
    /** The PrintWriter where the rendered output should be sent to. */
    private final XmlStringBuilder writer;
    
    /**
     * Creates a WebXmlRenderContext.
     * @param writer the PrintWriter where the rendered output should be sent to.
     */
    public WebXmlRenderContext(final PrintWriter writer)
    {
        if (writer instanceof XmlStringBuilder)
        {
            this.writer = (XmlStringBuilder) writer;
        }
        else
        {
            this.writer = new XmlStringBuilder(writer);
        }
    }
    
    /**
     * Creates a WebXmlRenderContext.
     * @param writer the PrintWriter where the rendered output should be sent to.
     * @param locale the Locale to use for translating messages.
     */
    public WebXmlRenderContext(final PrintWriter writer, final Locale locale)
    {
        this.writer = new XmlStringBuilder(writer, locale);
    }
    
    /** {@inheritDoc} */
    public String getRenderPackage()
    {
        return "com.github.openborders.wcomponents.render.webxml";
    }

    /**
     * @return the PrintWriter where the rendered output should be sent to.
     */
    public XmlStringBuilder getWriter()
    {
        return writer;
    }
}