package com.github.openborders; 

import java.util.Arrays;

import com.github.openborders.AbstractBasicTableModel;
import com.github.openborders.Action;
import com.github.openborders.ActionEvent;
import com.github.openborders.AdapterBasicTableModel;
import com.github.openborders.DefaultTransientDataContainer;
import com.github.openborders.DefaultWComponent;
import com.github.openborders.RadioButtonGroup;
import com.github.openborders.SimpleTableDataModel;
import com.github.openborders.WAbbrText;
import com.github.openborders.WAjaxControl;
import com.github.openborders.WAjaxPollingRegion;
import com.github.openborders.WApplication;
import com.github.openborders.WAudio;
import com.github.openborders.WBeanContainer;
import com.github.openborders.WButton;
import com.github.openborders.WCancelButton;
import com.github.openborders.WCardManager;
import com.github.openborders.WCheckBox;
import com.github.openborders.WCheckBoxSelect;
import com.github.openborders.WCollapsible;
import com.github.openborders.WCollapsibleToggle;
import com.github.openborders.WColumn;
import com.github.openborders.WColumnLayout;
import com.github.openborders.WConfirmationButton;
import com.github.openborders.WContainer;
import com.github.openborders.WContent;
import com.github.openborders.WDataTable;
import com.github.openborders.WDateField;
import com.github.openborders.WDecoratedLabel;
import com.github.openborders.WDefinitionList;
import com.github.openborders.WDialog;
import com.github.openborders.WDropdown;
import com.github.openborders.WEmailField;
import com.github.openborders.WFieldLayout;
import com.github.openborders.WFieldSet;
import com.github.openborders.WFileWidget;
import com.github.openborders.WFilterText;
import com.github.openborders.WHeading;
import com.github.openborders.WHorizontalRule;
import com.github.openborders.WImage;
import com.github.openborders.WInternalLink;
import com.github.openborders.WInvisibleContainer;
import com.github.openborders.WLabel;
import com.github.openborders.WLink;
import com.github.openborders.WMenu;
import com.github.openborders.WMenuItem;
import com.github.openborders.WMenuItemGroup;
import com.github.openborders.WMessageBox;
import com.github.openborders.WMessages;
import com.github.openborders.WMultiDropdown;
import com.github.openborders.WMultiFileWidget;
import com.github.openborders.WMultiSelect;
import com.github.openborders.WMultiSelectPair;
import com.github.openborders.WMultiTextField;
import com.github.openborders.WNumberField;
import com.github.openborders.WPanel;
import com.github.openborders.WPartialDateField;
import com.github.openborders.WPasswordField;
import com.github.openborders.WPhoneNumberField;
import com.github.openborders.WPopup;
import com.github.openborders.WPrintButton;
import com.github.openborders.WProgressBar;
import com.github.openborders.WRadioButtonSelect;
import com.github.openborders.WRepeater;
import com.github.openborders.WRow;
import com.github.openborders.WSelectToggle;
import com.github.openborders.WSeparator;
import com.github.openborders.WShuffler;
import com.github.openborders.WSingleSelect;
import com.github.openborders.WSkipLinks;
import com.github.openborders.WStyledText;
import com.github.openborders.WSubMenu;
import com.github.openborders.WSuggestions;
import com.github.openborders.WTabSet;
import com.github.openborders.WTable;
import com.github.openborders.WTableColumn;
import com.github.openborders.WText;
import com.github.openborders.WTextArea;
import com.github.openborders.WTextField;
import com.github.openborders.WVideo;
import com.github.openborders.WWindow;
import com.github.openborders.WDataTable.ActionConstraint;
import com.github.openborders.subordinate.WSubordinateControl;
import com.github.openborders.validation.WValidationErrors;

/** 
 * This container holds one of each component, and is used for performance
 * regression testing.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class AllComponents extends WApplication
{
    /**
     * Creates a container with all the components in it.
     */
    public AllComponents()
    {
        this(1);
    }

    /**
     * Creates a container with all the components in it repeated the number of times specified.
     * @param repetitions the number of times to repeat the components.
     */
    public AllComponents(final int repetitions)
    {
        for (int i = 0; i < repetitions; i++)
        {
            addComponents();
        }
    }
    
    /**
     * Adds the components to the container.
     */
    private void addComponents()
    {
        add(new DefaultTransientDataContainer(new WText("Transient data container content")));
        add(new DefaultWComponent());
        add(createRadioButtonGroup());
        add(new WAbbrText());
        add(createAjaxControl());
        add(new WAjaxPollingRegion(123));
        add(new WAudio());
        add(new WBeanContainer());
        add(new WButton("Button text"));
        add(new WCancelButton());
        add(new WCardManager());
        add(new WCheckBox());
        add(new WCheckBoxSelect(new String[] { "a", "b", "c", "d" }));
        add(new WCollapsible(new WText("Collapsible content"), "Collapsible heading"));
        add(new WCollapsibleToggle());
        add(new WColumnLayout("Column layout heading"));
        add(new WConfirmationButton("Confirmation button"));
        add(new WContainer());
        add(new WContent());
        //add(new WContentLink("Content link text")); // TODO: need to set content and cache key so that rendering is predictable
        add(createDataTable());
        add(new WDateField());
        add(new WDecoratedLabel("Decorated label text"));
        add(new WDefinitionList());
        add(new WDialog(new WText("Dialog content")));
        add(new WDropdown(new String[] { "a", "b", "c", "d" }));
        add(new WEmailField());
        add(createFieldLayout());
        add(new WFieldSet("Field set title"));
        add(new WFileWidget());
        //add(new WFilterControl(new WDecoratedLabel("Filter control label"))); // TODO: Needs target component
        add(new WFilterText("search", "replace"));
        add(new WHeading(WHeading.SECTION, "WHeading title"));
        add(new WHorizontalRule());
        add(new WImage("/image/x1.gif", "example image"));
        add(new WInternalLink("Internal link text", this));
        add(new WInvisibleContainer());
        add(new WLabel("Label text"));
        add(new WLink("link text", "http://localhost"));
        //add(new WList(WList.Type.STACKED)); // TODO: createWList with data
        add(createMenu());
        add(new WMessageBox(WMessageBox.INFO, "WMessageBox message"));
        add(new WMessages());
        add(new WMultiDropdown(new String[] { "a", "b", "c", "d" }));
        add(new WMultiFileWidget());
        add(new WMultiSelect(new String[] { "a", "b", "c", "d" }));
        add(new WMultiSelectPair(new String[] { "a", "b", "c", "d" }));
        add(new WMultiTextField(new String[] { "a", "b", "c", "d" }));
        add(new WNumberField());
        add(new WPanel());
        add(new WPartialDateField());
        add(new WPasswordField());
        add(new WPhoneNumberField());
        add(new WPopup());
        add(new WPrintButton("Print button text"));
        add(new WProgressBar(100));
        add(new WRadioButtonSelect(new String[] { "a", "b", "c", "d" }));
        add(new WRepeater(new WText())); // TODO: set data
        add(createWRow());
        add(new WSelectToggle(false, this));
        add(new WSeparator());
        add(new WShuffler(Arrays.asList("a", "b", "c", "d" )));
        add(new WSingleSelect(new String[] { "a", "b", "c", "d" }));
        add(new WSkipLinks());
        add(new WStyledText("styled text", WStyledText.Type.EMPHASISED));
        add(new WSubordinateControl());
        add(new WSuggestions(Arrays.asList("a", "b", "c", "d" )));
        add(createTabSet());
        add(createWTable());
        add(new WText("text"));
        add(new WTextArea());
        add(new WTextField());
        add(new WValidationErrors());
        add(new WVideo());
        add(new WWindow());
    }

    /**
     * Creates a container with a RadioButtonGroup and a few WRadioButtons.
     * @return a new container with a RadioButtonGroup.
     */
    private WContainer createRadioButtonGroup()
    {
        WContainer container = new WContainer();
        RadioButtonGroup group = new RadioButtonGroup();
        container.add(group);
        
        container.add(group.addRadioButton("option1"));
        container.add(group.addRadioButton("option2"));
        container.add(group.addRadioButton("option3"));
        
        return container;
    }

    /**
     * Creates a WRow containing WColumns with content.
     * @return a new WRow with some content.
     */
    private WRow createWRow()
    {
        WRow row = new WRow();
        
        WColumn col = new WColumn(30);
        col.add(new WText("30%"));
        row.add(col);
        
        col = new WColumn(70);
        col.add(new WText("70%"));
        row.add(col);
        
        return row;
    }

    /**
     * Creates a container with a correctly configured WAjaxControl.
     * @return a new container with a WAjaxControl. 
     */
    private static WContainer createAjaxControl()
    {
        WContainer container = new WContainer();
        
        WDropdown trigger = new WDropdown(new String[] { "1", "2", "3", "4" });
        WDropdown target = new WDropdown(new String[] { "5", "6", "7", "8" });
        container.add(trigger);
        container.add(target);        
        container.add(new WAjaxControl(trigger, target));
        
        return container;
    }

    /**
     * Creates a simple table with a data model set.
     * @return a new table with content.
     */
    private static WDataTable createDataTable()
    {
        WDataTable table = new WDataTable();
        table.addColumn(new WTableColumn("Column 1", WText.class));
        table.addColumn(new WTableColumn("Column 2", new WTextField()));

        table.setDataModel(new SimpleTableDataModel(new String[][]
        {
           { "row1col1", "row1col2" },
           { "row2col1", "row2col2" },
           { "row3col1", "row3col2" },
           { "row4col1", "row4col2" },
           { "row5col1", "row5col2" }
        }));
        
        table.setSelectMode(WDataTable.SelectMode.SINGLE);
        
        WButton action = new WButton("Action");
        table.addAction(action);        
        table.addActionConstraint(action, new ActionConstraint(1, -1, true, "At least one row must be selected to use this function"));
        
        return table;
    }

    /**
     * Creates a field layout with a couple of fields.
     * @return a new field layout with content.
     */
    private WFieldLayout createFieldLayout()
    {
        WFieldLayout fieldLayout = new WFieldLayout();        
        fieldLayout.addField("Field 1", new WTextField());
        fieldLayout.addField("Field 2", new WTextArea()).getLabel().setHint("hint");
        
        return fieldLayout;
    }

    /**
     * Creates a new menu with all types of menu content.
     * @return a new menu with content.
     */
    private static WMenu createMenu()
    {
        WMenu menu = new WMenu(WMenu.MenuType.TREE);
        menu.add(new WMenuItem("top-level menu item", new Action()
        {
            @Override
            public void execute(final ActionEvent event)
            {
            }
        }));
        
        WSubMenu subMenu = new WSubMenu("subMenu modes");
        menu.add(subMenu);
        
        for (WSubMenu.MenuMode mode : WSubMenu.MenuMode.values())
        {
            WSubMenu subSubMenu = new WSubMenu(mode.toString());
            subSubMenu.setMode(mode);
            subSubMenu.add(new WMenuItem("2nd-level item"));
            subMenu.add(subSubMenu);
        }
        
        subMenu = new WSubMenu("Menu group");
        menu.add(subMenu);
        
        subMenu.add(new WMenuItem("non-grouped item"));
        
        WMenuItemGroup group = new WMenuItemGroup("menu item group");
        group.add(new WMenuItem("item 1"));
        group.addSeparator();
        group.add(new WMenuItem("item 2", "http://localhost"));
        subMenu.add(group);
        
        return menu;
    }

    /**
     * Creates a new tabset with all the tab types.
     * @return a new tabset with content.
     */
    private static WTabSet createTabSet()
    {
        WTabSet tabSet = new WTabSet();
        
        for (WTabSet.TabMode mode : WTabSet.TabMode.values())
        {
            if (tabSet.getTotalTabs() == 1)
            {
                // Add a single separator
                tabSet.addSeparator();
            }
            
            tabSet.addTab(new WText(mode + " content"), mode + " tab", mode);
        }
        
        return tabSet;
    }
    
    /**
     * Creates a simple table with a data model set.
     * 
     * @return a new table with content.
     */
    private static WTable createWTable()
    {
        WTable table = new WTable();
        table.addColumn(new WTableColumn("Column 1", WText.class));
        table.addColumn(new WTableColumn("Column 2", new WTextField()));
        table.setTableModel(new AdapterBasicTableModel(new MyModel()));
        return table;
    }
    
    /**
     * Basic model.
     */
    private static final class MyModel extends AbstractBasicTableModel
    {
        /** Data. */
        private final String[][] data = new String[][]
                {
                   { "row1col1", "row1col2" },
                   { "row2col1", "row2col2" },
                   { "row3col1", "row3col2" },
                   { "row4col1", "row4col2" },
                   { "row5col1", "row5col2" }
                };
        
        /** {@inheritDoc} */
        @Override
        public Object getValueAt(final int row, final int col)
        {
            return data[row][col];
        }

        /** {@inheritDoc} */
        @Override
        public int getRowCount()
        {
            return data.length;
        }

    }
    
}