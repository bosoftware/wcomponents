package com.github.openborders.examples.theme;

import com.github.openborders.RadioButtonGroup;
import com.github.openborders.WButton;
import com.github.openborders.WCheckBox;
import com.github.openborders.WContainer;
import com.github.openborders.WDropdown;
import com.github.openborders.WHorizontalRule;
import com.github.openborders.WLabel;
import com.github.openborders.WLink;
import com.github.openborders.WRadioButton;
import com.github.openborders.WText;
import com.github.openborders.WTextField;

/**
 * <p>
 * This example demonstrates usage of access keys with the {@link WButton} and {@link WLabel}
 * components.
 * </p>
 * <p> The contained examples include:
 * <ul class="bullets">
 *     <li>Labels, links and buttons can be assigned an access key.</li>
 *     <li>Labels, links and buttons will highlight the access key character in the label/link/button text.</li>
 *     <li>The first matching character in the label/link/button text is highlighted to indicate the access key.</li>
 *     <li>If no title is set on component the label/link/button title will be the access key combination.</li>
 * </ul>
 * </p>
 * 
 * @author Christina Harris
 * @since 15/04/2009
 */
public class AccessKeyExample extends WContainer
{
    /**
     * Creates an AccessKeyExample.
     */
    public AccessKeyExample()
    {
        // A button with access key, default highlighted character.
        
        WText text = new WText("<p>A button with accesskey <b>'a'</b>, the default accesskey title are used.</p>");
        text.setEncodeText(false);        
        add(text);

        WButton button = new WButton("Save As", 'a');
        add(button);

        // A link with access key, override default highlighted character.
        add(new WHorizontalRule());
        text = new WText("<p>A link with accesskey <b>'e'</b>. No title is set so the accesskey title is used.</p>");
        text.setEncodeText(false);        
        add(text);

        WLink link = new WLink("Example", "http://www.example.com");
        link.setAccessKey('p');
        link.setOpenNewWindow(false);
        add(link);

        // A checkbox with access key.
        add(new WHorizontalRule());
        text = new WText("<p>A checkbox with accesskey <b>'b'</b>. Default accesskey index. No title is set so the accesskey title will be used.</p>");
        text.setEncodeText(false);        
        add(text);

        WCheckBox checkBox = new WCheckBox();
        WLabel checkBoxLabel = new WLabel("Check box", checkBox);
        checkBoxLabel.setAccessKey('b');
        add(checkBoxLabel);
        add(checkBox);

        // A radio button label with access key
        add(new WHorizontalRule());
        text = new WText("<p>A radio button label with accesskey <b>'x'</b>. The accesskey is not in the labels text so no character is highlighted. A title is set so the accesskey title will not be used.</p><br/>");
        text.setEncodeText(false);        
        add(text);

        RadioButtonGroup group = new RadioButtonGroup();
        add(group);
        WRadioButton radioButton = group.addRadioButton(1);
        WLabel radioButtonLabel = new WLabel("Radio Button", 'x', radioButton);
        radioButtonLabel.setToolTip("My tool tip");
        add(radioButtonLabel);
        add(radioButton);

        // A text field with access key, no access key highlight.
        add(new WHorizontalRule());
        text = new WText("<p>A text input label with accesskey <b>'f'</b>. No title is set so the accesskey title will be used.</p><br/>");
        text.setEncodeText(false);        
        add(text);

        WTextField textField = new WTextField();
        WLabel textFieldLabel = new WLabel("Text Field", 'f', textField);
        add(textFieldLabel);
        add(textField);

        // A dropdown field label with an accesskey.
        add(new WHorizontalRule());
        text = new WText("<p>A dropdown label with accesskey <b>'o'</b>. A title is set so this will be used.</p><br/>");
        text.setEncodeText(false);        
        add(text);
                      
        WDropdown dropdown = new WDropdown(new String[] { "Monday", "Tuesday", "Wednesday", "Thursday" });
        WLabel dropdownLabel = new WLabel("Dropdown", 'o', dropdown);
        add(dropdownLabel);
        add(dropdown);
    }
}