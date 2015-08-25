package com.github.openborders.examples.table;

import java.util.List;

import com.github.openborders.Action;
import com.github.openborders.ActionEvent;
import com.github.openborders.Request;
import com.github.openborders.SimpleBeanBoundTableModel;
import com.github.openborders.WButton;
import com.github.openborders.WDateField;
import com.github.openborders.WPanel;
import com.github.openborders.WStyledText;
import com.github.openborders.WTable;
import com.github.openborders.WTableColumn;
import com.github.openborders.WTextField;

/**
 * This example demonstrates a {@link WTable} that is bean bound and editable.
 * <p>
 * Uses {@link SimpleBeanBoundTableModel} to handle the bean binding.
 * </p>
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class SimpleEditableTableExample extends WPanel
{
    /** The table used in the example. */
    private final WTable table;

    /**
     * Create example.
     */
    public SimpleEditableTableExample()
    {
        table = new WTable();

        // Column - First name
        WTextField textField = new WTextField();
        textField.setAccessibleText("First name");
        table.addColumn(new WTableColumn("First name", textField));

        // Column - Last name
        textField = new WTextField();
        textField.setAccessibleText("Last name");
        table.addColumn(new WTableColumn("Last name", textField));

        // Column - Date field
        WDateField dateField = new WDateField();
        dateField.setAccessibleText("Date of birth");
        table.addColumn(new WTableColumn("Date of birth", dateField));

        // Editable
        table.setEditable(true);

        // Setup model
        SimpleBeanBoundTableModel model = new SimpleBeanBoundTableModel(new String[] { "firstName", "lastName",
                                                                                      "dateOfBirth" });
        model.setEditable(true);
        table.setTableModel(model);

        add(table);

        // Create a component to display the table data in text format
        final WStyledText dataOutput = new WStyledText()
        {
            @Override
            public String getText()
            {
                StringBuffer buf = new StringBuffer("Saved data:\n");

                for (PersonBean person : (List<PersonBean>) table.getBean())
                {
                    buf.append(person.toString());
                    buf.append('\n');
                }

                return buf.toString();
            };
        };

        dataOutput.setWhitespaceMode(WStyledText.WhitespaceMode.PRESERVE);

        // Create a button to trigger the display of the data
        WButton displayButton = new WButton("Save data");
        displayButton.setAction(new Action()
        {
            public void execute(final ActionEvent event)
            {
                table.updateBeanValue();
            }
        });

        add(displayButton);
        add(dataOutput);
    }

    /**
     * Override preparePaintComponent in order to set up the example data the first time that the example is accessed by
     * each user.
     * 
     * @param request the request being responded to.
     */
    @Override
    protected void preparePaintComponent(final Request request)
    {
        super.preparePaintComponent(request);
        if (!isInitialised())
        {
            // Set the data as the bean on the table
            table.setBean(ExampleDataUtil.createExampleData());
            setInitialised(true);
        }
    }

}