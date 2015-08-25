package com.github.openborders.render.webxml;

import com.github.openborders.AjaxHelper;
import com.github.openborders.HeadingLevel;
import com.github.openborders.WCollapsible;
import com.github.openborders.WComponent;
import com.github.openborders.XmlStringBuilder;
import com.github.openborders.WCollapsible.CollapsibleMode;
import com.github.openborders.servlet.WebXmlRenderContext;
import com.github.openborders.util.SystemException;

/**
 * The Renderer for {@link WCollapsible}.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WCollapsibleRenderer extends AbstractWebXmlRenderer
{
    /**
     * Paints the given WCollapsible.
     * 
     * @param component the WCollapsible to paint.
     * @param renderContext the RenderContext to paint to.
     */
    @Override
    public void doRender(final WComponent component, final WebXmlRenderContext renderContext)
    {
        WCollapsible collapsible = (WCollapsible) component;
        XmlStringBuilder xml = renderContext.getWriter();        
        WComponent content = collapsible.getContent();
        boolean collapsed = collapsible.isCollapsed();
        
        xml.appendTagOpen("ui:collapsible");
        xml.appendAttribute("id", component.getId());
        xml.appendOptionalAttribute("track", component.isTracking(), "true");
        xml.appendAttribute("groupName", collapsible.getGroupName());
        xml.appendOptionalAttribute("collapsed", collapsed, "true");
        xml.appendOptionalAttribute("hidden", collapsible.isHidden(), "true");
        
        switch (collapsible.getMode())
        {
            case CLIENT:
                xml.appendAttribute("mode", "client");
                break;
            case LAZY:
                xml.appendAttribute("mode", "lazy");
                break;
            case EAGER:
                xml.appendAttribute("mode", "eager");
                break;
            case DYNAMIC:
                xml.appendAttribute("mode", "dynamic");
                break;
            case SERVER:
                xml.appendAttribute("mode", "server");
                break;
            default:
                throw new SystemException("Unknown collapsible mode: " + collapsible.getMode());
        }
        
        HeadingLevel level = collapsible.getHeadingLevel(); 
        if (level != null)
        {
            xml.appendAttribute("level", level.getLevel());
        }
        
        xml.appendClose();
        
        // Render margin
        MarginRendererUtil.renderMargin(collapsible, renderContext);
        
        // Label
        collapsible.getDecoratedLabel().paint(renderContext);

        // Content
        xml.appendTagOpen("ui:content");
        xml.appendAttribute("id", component.getId() + "-content");
        xml.appendClose();
        
        // Render content if not EAGER Mode or is EAGER and is the current AJAX trigger
        if (CollapsibleMode.EAGER != collapsible.getMode() || AjaxHelper.isCurrentAjaxTrigger(collapsible))
        {
            // Visibility of content set in prepare paint
            content.paint(renderContext);
        }
        
        xml.appendEndTag("ui:content");
        
        xml.appendEndTag("ui:collapsible");
    }
    
}