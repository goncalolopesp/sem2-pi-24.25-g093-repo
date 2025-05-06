package ui.console.menu;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Represents an item in the console menu, linking a textual description
 * to a Runnable UI action.
 */

public class MenuItem {
    private final String description;
    private final Runnable ui;

    /**
     * Constructs a new MenuItem with the given description and UI action.
     *
     * @param description the non-blank text to display for this menu item
     * @param ui          the non-null Runnable action to execute for this item
     * @throws IllegalArgumentException if description is null, empty, or blank,
     *                                  or if ui is null
     */

    public MenuItem(String description, Runnable ui) {
        if (StringUtils.isBlank(description)) {
            throw new IllegalArgumentException("MenuItem description cannot be null or empty.");
        }
        if (Objects.isNull(ui)) {
            throw new IllegalArgumentException("MenuItem does not support a null UI.");
        }

        this.description = description;
        this.ui = ui;
    }

    /**
     * Executes the UI action associated with this menu item.
     */
    public void run() {
        this.ui.run();
    }

    /**
     * Returns the string representation of this MenuItem, which is its description.
     *
     * @return the description of this menu item
     */
    public String toString() {
        return this.description;
    }
}
