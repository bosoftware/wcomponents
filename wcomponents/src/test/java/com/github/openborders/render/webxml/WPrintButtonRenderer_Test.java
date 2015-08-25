package com.github.openborders.render.webxml;

import java.io.IOException;

import junit.framework.Assert;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.github.openborders.WPrintButton;
import com.github.openborders.render.webxml.WPrintButtonRenderer;

/**
 * Junit test case for {@link WPrintButtonRenderer}.
 * 
 * @author Yiannis Paschalidis 
 * @since 1.0.0
 */
public class WPrintButtonRenderer_Test extends AbstractWebXmlRendererTestCase
{
    @Test
    public void testRendererCorrectlyConfigured()
    {
        WPrintButton component = new WPrintButton("dummy");
        Assert.assertTrue("Incorrect renderer supplied", getWebXmlRenderer(component) instanceof WPrintButtonRenderer);
    }    
    
    @Test
    public void testDoPaint() throws IOException, SAXException, XpathException
    {
        WPrintButton button = new WPrintButton("Print");
        assertSchemaMatch(button);
        assertXpathExists("//ui:printButton", button);
    }
    
    @Test
    public void testXssEscaping() throws IOException, SAXException, XpathException
    {
        WPrintButton button = new WPrintButton(getMaliciousContent());
        
        assertSafeContent(button);
        
        button.setToolTip(getMaliciousAttribute("ui:printButton"));
        assertSafeContent(button);
        
        button.setAccessibleText(getMaliciousAttribute("ui:printButton"));
        assertSafeContent(button);
    }
}