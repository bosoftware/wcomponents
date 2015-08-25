package com.github.openborders.examples;

import com.github.openborders.Action;
import com.github.openborders.ActionEvent;
import com.github.openborders.WButton;
import com.github.openborders.WContainer;
import com.github.openborders.WLabel;
import com.github.openborders.WTextField;

/**
 * Same idea as {@link TextDuplicator}, but with a 
 * velocity template to pretty things up.
 * 
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class TextDuplicator_VelocityImpl extends WContainer
{
    /** The text field which the actions modify the state of. */
    private final WTextField textFld = new WTextField();

    /**
     * Creates a TextDuplicator_VelocityImpl with the default label text.
     */
    public TextDuplicator_VelocityImpl()
    {
        this("Pretty Duplicator");
    }

    /**
     * Creates a TextDuplicator_VelocityImpl with the specified label text.
     * 
     * @param name the name label text
     */
    public TextDuplicator_VelocityImpl(final String name)
    {
        // This is the line of code that associates this component with a
        // velocity template.  A simple mapping is applied to the given class
        // to derive the name of a velocity template.
        // In this case, com.github.openborders.examples.TextDuplicatorPretty
        // maps to the template com/github/openborders/examples/TextDuplicator_VelocityImpl.vm
        setTemplate(TextDuplicator_VelocityImpl.class);

        WButton dupBtn = new WButton("Duplicate");
        WButton clrBtn = new WButton("Clear");
        
        add(new WLabel(name, textFld), "label");
        add(textFld, "text");
        add(dupBtn, "duplicateButton");
        add(clrBtn, "clearButton");
        
        dupBtn.setAction(new DuplicateAction());
        clrBtn.setAction(new ClearAction());
    }

    /**
     * This action duplicates the text in the text field.
     * @author Martin Shevchenko
     */
    private final class DuplicateAction implements Action
    {
        /**
         * Executes the action which duplicates the text.
         * 
         * @param event details about the event that occured.
         */
        public void execute(final ActionEvent event)
        {
            // Get the text entered by the user.
            String text = textFld.getText();
            
            // Now duplicate it.
            textFld.setText(text + text);
        }
    }

    /**
     * This action clears out the text in the text field.
     * @author Martin Shevchenko
     */
    private final class ClearAction implements Action
    {
        /**
         * Executes the action which clears the text.
         * 
         * @param event details about the event that occured.
         */
        public void execute(final ActionEvent event)
        {
            textFld.setText("");
        }
    }
}