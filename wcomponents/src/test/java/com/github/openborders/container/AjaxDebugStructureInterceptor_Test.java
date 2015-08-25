package com.github.openborders.container;

import java.io.IOException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.github.openborders.AjaxHelper;
import com.github.openborders.UIContext;
import com.github.openborders.WApplication;
import com.github.openborders.WButton;
import com.github.openborders.WLabel;
import com.github.openborders.container.AjaxDebugStructureInterceptor;
import com.github.openborders.container.AjaxInterceptor;
import com.github.openborders.container.AjaxPageShellInterceptor;
import com.github.openborders.container.AjaxSetupInterceptor;
import com.github.openborders.render.webxml.AbstractWebXmlRendererTestCase;
import com.github.openborders.servlet.WServlet;
import com.github.openborders.servlet.WebXmlRenderContext;
import com.github.openborders.util.Config;
import com.github.openborders.util.mock.MockRequest;
import com.github.openborders.util.mock.MockResponse;

/**
 * AjaxDebugStructureInterceptor_Test - unit tests for {@link AjaxDebugStructureInterceptor}.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class AjaxDebugStructureInterceptor_Test extends AbstractWebXmlRendererTestCase
{
    @Test
    public void testInterceptorDisabled() throws XpathException, SAXException, IOException
    {
        Config.getInstance().setProperty("wcomponent.debug.enabled", "false");
        Config.notifyListeners();
        
        MyApp app = new MyApp();
        app.setLocked(true);
        
        // No debug info should be rendered
        String xml = doAjaxRequest(app);
        assertXpathEvaluatesTo("0", "count(//ui:debug)", xml);
    }

    @Test
    public void testInterceptor() throws XpathException, SAXException, IOException
    {
        Config.getInstance().setProperty("wcomponent.debug.enabled", "true");
        Config.getInstance().setProperty("wcomponent.debug.clientSide.enabled", "true");
        Config.notifyListeners();

        MyApp app = new MyApp();
        app.setLocked(true);
        
        // Only the label should have debug info rendered
        String xml = doAjaxRequest(app);
        assertXpathEvaluatesTo("1", "count(//ui:debug/ui:debugInfo)", xml);
        assertXpathEvaluatesTo(app.target.getId(), "//ui:debug/ui:debugInfo/@for", xml);
        assertXpathEvaluatesTo(app.target.getClass().getName(), "//ui:debug/ui:debugInfo/@class", xml);
        assertXpathEvaluatesTo(app.target.getClass().getName(), "//ui:debug/ui:debugInfo/@type", xml);
        assertXpathEvaluatesTo("true", "//ui:debug/ui:debugInfo/ui:debugDetail[@key='defaultState']/@value", xml);
    }
    
    /** 
     * Does an AJAX request for the app. 
     * @param app the MyApp instance to do an AJAX request for.
     * @return the AJAX xml output.
     */
    private String doAjaxRequest(final MyApp app)
    {
        UIContext uic = createUIContext();
        uic.setUI(app);
        setActiveContext(uic);

        // Create interceptors
        AjaxSetupInterceptor ajaxSetupInterceptor = new AjaxSetupInterceptor();
        AjaxPageShellInterceptor ajaxPageInterceptor = new AjaxPageShellInterceptor();               
        AjaxInterceptor ajaxInterceptor = new AjaxInterceptor();
        AjaxDebugStructureInterceptor debugInterceptor = new AjaxDebugStructureInterceptor();

        // Create chain
        debugInterceptor.setBackingComponent(ajaxInterceptor);
        ajaxPageInterceptor.setBackingComponent(debugInterceptor);
        ajaxSetupInterceptor.setBackingComponent(ajaxPageInterceptor);
        ajaxSetupInterceptor.attachUI(app);
        
        // Action phase
        MockRequest request = new MockRequest();
        AjaxHelper.registerComponent(app.target.getId(), request, app.trigger.getId());
        request.setParameter(WServlet.AJAX_TRIGGER_PARAM_NAME, app.trigger.getId());
        
        ajaxSetupInterceptor.serviceRequest(request);
        ajaxSetupInterceptor.preparePaint(request);

        // Render phase
        MockResponse response = new MockResponse();
        ajaxSetupInterceptor.attachResponse(response);        
        ajaxSetupInterceptor.paint(new WebXmlRenderContext(response.getWriter()));
        
        return response.getWriterOutput();
    }

    /** A simple test UI which is AJAX-enabled. */
    private static final class MyApp extends WApplication
    {
        /** An AJAX trigger. */
        private final WButton trigger = new WButton("trigger");
        
        /** An AJAX target. */
        private final WLabel target = new WLabel("target");

        /** Creates the test app. */
        public MyApp()
        {
            trigger.setAjaxTarget(target);
            add(trigger);
            add(target);
        }
    }
}