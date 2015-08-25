package com.github.openborders.render.webxml;

import com.github.openborders.Renderer;
import com.github.openborders.WComponent;
import com.github.openborders.WStyledText;
import com.github.openborders.WebUtilities;
import com.github.openborders.XmlStringBuilder;
import com.github.openborders.servlet.WebXmlRenderContext;
import com.github.openborders.util.Util;

/**
 * {@link Renderer} for the {@link WStyledText} component.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WStyledTextRenderer extends AbstractWebXmlRenderer
{
    /**
     * Paints the given WStyledText.
     * 
     * @param component the WStyledText to paint.
     * @param renderContext the RenderContext to paint to.
     */
    @Override
    public void doRender(final WComponent component, final WebXmlRenderContext renderContext)
    {
        WStyledText text = (WStyledText) component;
        XmlStringBuilder xml = renderContext.getWriter();
        String textString = text.getText();
        
        if (textString != null && textString.length() > 0)
        {
            xml.appendTagOpen("ui:text");
            
            switch (text.getType())
            {
                case EMPHASISED:
                    xml.appendAttribute("type", "emphasised");
                    break;
                    
                case HIGH_PRIORITY:
                    xml.appendAttribute("type", "highPriority");
                    break;
                    
                case LOW_PRIORITY:
                    xml.appendAttribute("type", "lowPriority");
                    break;
                    
                case MEDIUM_PRIORITY:
                    xml.appendAttribute("type", "mediumPriority");
                    break;
                    
                case ACTIVE_INDICATOR:
                    xml.appendAttribute("type", "activeIndicator");
                    break;
                    
                case MATCH_INDICATOR:
                    xml.appendAttribute("type", "matchIndicator");
                    break;
                    
                case INSERT:
                    xml.appendAttribute("type", "insert");
                    break;

                case DELETE:
                    xml.appendAttribute("type", "delete");
                    break;                    
                
                case MANDATORY_INDICATOR:
                    xml.appendAttribute("type", "mandatoryIndicator");
                    break;                    
                    
                case PLAIN:
                default:
                    xml.appendAttribute("type", "plain");
                    break;
            }
            
            switch (text.getWhitespaceMode())
            {                    
                case PARAGRAPHS:
                    xml.appendAttribute("space", "paragraphs");
                    break;
                    
                case PRESERVE:
                    xml.appendAttribute("space", "preserve");
                    break;
                    
                case DEFAULT:
                    break;
                    
                default:
                    throw new IllegalArgumentException("Unknown white space mode: " + text.getWhitespaceMode()); 
            }
            
            xml.appendClose();
            
            switch (text.getWhitespaceMode())
            {                    
                case PARAGRAPHS:
                    writeParagraphs(WebUtilities.encode(textString), xml);
                    break;
                    
                case PRESERVE:
                case DEFAULT:
                    xml.append(WebUtilities.encode(textString));
                    break;
                    
                default:
                    throw new IllegalArgumentException("Unknown white space mode: " + text.getWhitespaceMode()); 
            }
            
            xml.appendEndTag("ui:text");
        }
    }
    
    /**
     * Writes out paragraph delimted content.
     * 
     * @param text the String content to output.
     * @param xml the XmlStringBuilder to paint to.
     */
    private static void writeParagraphs(final String text, final XmlStringBuilder xml)
    {
        if (!Util.empty(text))
        {
            int start = 0;
            int end = text.length() - 1;

            // Set the start index to the first non-linebreak, so we don't emit leading ui:nl tags
            for (; start < end; start++)
            {
                char c = text.charAt(start);
                
                if (c != '\n' && c != '\r')
                {
                    break;
                }
            }
            
            // Set the end index to the last non-linebreak, so we don't emit trailing ui:nl tags
            for (; start < end; end--)
            {
                char c = text.charAt(end);
                
                if (c != '\n' && c != '\r')
                {
                    break;
                }
            }

            char lastChar = 0;

            for (int i = start; i <= end; i++)
            {
                char c = text.charAt(i);

                if (c == '\n' || c == '\r')
                {
                    if (lastChar != 0)
                    {
                        xml.write("<ui:nl/>");
                    }

                    lastChar = 0;
                }
                else
                {
                    xml.write(c);
                    lastChar = c;
                }
            }
        }
    }
}