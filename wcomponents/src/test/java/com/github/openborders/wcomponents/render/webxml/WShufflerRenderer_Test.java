package com.github.openborders.wcomponents.render.webxml;

import java.io.IOException;
import java.util.Arrays;

import junit.framework.Assert;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.github.openborders.wcomponents.WShuffler;

/**
 * Junit test case for {@link WShufflerRenderer}.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WShufflerRenderer_Test extends AbstractWebXmlRendererTestCase
{
    @Test
    public void testRendererCorrectlyConfigured()
    {
        WShuffler component = new WShuffler();
        Assert.assertTrue("Incorrect renderer supplied", getWebXmlRenderer(component) instanceof WShufflerRenderer);
    }

    @Test
    public void testDoPaint() throws IOException, SAXException, XpathException
    {
        String OPTION_A = "A";
        String OPTION_B = "B";
        String OPTION_C = "C";
        String OPTION_X = "X";

        // No options.
        WShuffler shuffler = new WShuffler();
        assertSchemaMatch(shuffler);
        assertXpathEvaluatesTo("0", "count(//ui:shuffler/ui:option)", shuffler);

        // Default options
        shuffler.setOptions(Arrays.asList(new String[] { OPTION_A, OPTION_B, OPTION_C }));
        assertSchemaMatch(shuffler);
        assertXpathEvaluatesTo("3", "count(//ui:shuffler/ui:option)", shuffler);
        assertXpathEvaluatesTo(OPTION_A, "//ui:shuffler/ui:option[1]", shuffler);
        assertXpathEvaluatesTo(OPTION_B, "//ui:shuffler/ui:option[2]", shuffler);
        assertXpathEvaluatesTo(OPTION_C, "//ui:shuffler/ui:option[3]", shuffler);
        assertXpathEvaluatesTo("0", "//ui:shuffler/ui:option[1]/@value", shuffler);
        assertXpathEvaluatesTo("1", "//ui:shuffler/ui:option[2]/@value", shuffler);
        assertXpathEvaluatesTo("2", "//ui:shuffler/ui:option[3]/@value", shuffler);
        assertXpathEvaluatesTo("", "//ui:shuffler/@readOnly", shuffler);
        assertXpathEvaluatesTo("", "//ui:shuffler/@disabled", shuffler);
        assertXpathEvaluatesTo("", "//ui:shuffler/@toolTip", shuffler);
        assertXpathEvaluatesTo("", "//ui:shuffler/@rows", shuffler);

        // Check with user context
        assertSchemaMatch(shuffler);
        assertXpathEvaluatesTo("3", "count(//ui:shuffler/ui:option)", shuffler);
        assertXpathEvaluatesTo(OPTION_A, "//ui:shuffler/ui:option[1]", shuffler);
        assertXpathEvaluatesTo(OPTION_B, "//ui:shuffler/ui:option[2]", shuffler);
        assertXpathEvaluatesTo(OPTION_C, "//ui:shuffler/ui:option[3]", shuffler);
        assertXpathEvaluatesTo("0", "//ui:shuffler/ui:option[1]/@value", shuffler);
        assertXpathEvaluatesTo("1", "//ui:shuffler/ui:option[2]/@value", shuffler);
        assertXpathEvaluatesTo("2", "//ui:shuffler/ui:option[3]/@value", shuffler);

        // User specific options
        shuffler.setOptions(Arrays.asList(new String[] { OPTION_X }));
        assertXpathEvaluatesTo("1", "count(//ui:shuffler/ui:option)", shuffler);
        assertXpathEvaluatesTo(OPTION_X, "//ui:shuffler/ui:option[1]", shuffler);
        assertXpathEvaluatesTo("0", "//ui:shuffler/ui:option[1]/@value", shuffler);

        // Check optional attributes
        shuffler.setReadOnly(true);
        shuffler.setDisabled(true);
        shuffler.setToolTip("TITLE");
        shuffler.setRows(2);
        assertSchemaMatch(shuffler);
        assertXpathEvaluatesTo("true", "//ui:shuffler/@readOnly", shuffler);
        assertXpathEvaluatesTo("true", "//ui:shuffler/@disabled", shuffler);
        assertXpathEvaluatesTo("TITLE", "//ui:shuffler/@toolTip", shuffler);
        assertXpathEvaluatesTo("2", "//ui:shuffler/@rows", shuffler);

    }

    @Test
    public void testXssEscaping() throws IOException, SAXException, XpathException
    {
        WShuffler shuffler = new WShuffler(Arrays.asList(new String[] { getInvalidCharSequence(), getMaliciousContent() }));
        
        assertSafeContent(shuffler);
        
        shuffler.setToolTip(getMaliciousAttribute("ui:shuffler"));
        assertSafeContent(shuffler);
        
        shuffler.setAccessibleText(getMaliciousAttribute("ui:shuffler"));
        assertSafeContent(shuffler);
    }
    
    @Test
    public void testSpecialCharacters() throws IOException, SAXException, XpathException
    {
        String optionA = "<A";
        String optionB = "B&B";
        String optionC = "C";

        WShuffler shuffler = new WShuffler(Arrays.asList(new String[] { optionA, optionB, optionC }));
        assertSchemaMatch(shuffler);
        assertXpathEvaluatesTo("3", "count(//ui:shuffler/ui:option)", shuffler);
    }
}