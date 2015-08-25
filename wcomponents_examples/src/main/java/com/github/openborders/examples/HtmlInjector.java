package com.github.openborders.examples; 

import com.github.openborders.Action;
import com.github.openborders.ActionEvent;
import com.github.openborders.WButton;
import com.github.openborders.WContainer;
import com.github.openborders.WHorizontalRule;
import com.github.openborders.WPanel;
import com.github.openborders.WText;
import com.github.openborders.WTextArea;

/** 
 * Demonstrates a method to inject HTML mark-up directly into a generated page 
 * using a {@link WText}. It also serves as a demonstration of why untrusted
 * input should never be be displayed using {@link WText} with encoding disabled.  
 * 
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class HtmlInjector extends WContainer
{
    /**
     * Creates a HtmlInjector.
     */
    public HtmlInjector()
    {
        final WTextArea htmlEditor = new WTextArea();
        htmlEditor.setRows(8);
        htmlEditor.setColumns(80);
        
        final WText injectedHtml = new WText();
        injectedHtml.setEncodeText(false); // This disables text escaping, and can lead to XSS attacks
        
        WButton injectBtn = new WButton("Inject");
        
        injectBtn.setAction(new Action() 
        {
            public void execute(final ActionEvent event)
            {
                String html = htmlEditor.getText();
                injectedHtml.setText(html);
            }
        });
        
        add(htmlEditor);
        add(injectBtn);
        add(new WHorizontalRule());
        
        WPanel injectedHtmlPanel = new WPanel(WPanel.Type.BLOCK);
        injectedHtmlPanel.add(injectedHtml);
        add(injectedHtmlPanel, "injection");
    }
}