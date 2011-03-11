package org.vaadin.jonatan.contexthelp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;

/**
 * The ContextHelp component offers contextual help for fields or groups of
 * fields. By default, the help bubbles are opened by the user by pressing the
 * F1 key. You can also specify to programmatically open certain help bubbles
 * (e.g. via a button click) or to have the help bubble follow focus.
 * 
 * @author Jonatan Kronqvist / IT Mill Ltd
 * 
 */
@com.vaadin.ui.ClientWidget(org.vaadin.jonatan.contexthelp.widgetset.client.ui.VContextHelp.class)
public class ContextHelp extends AbstractComponent {
	private static final long serialVersionUID = 3852216539762314709L;

	private static int helpComponentIdCounter = 0;

	private HashMap<String, String> helpHTML;
	private HashMap<String, Placement> placements;
	private List<Component> components;

	private String selectedComponentId = "";
	private boolean hidden = true;

	private boolean followFocus = false;

	// These need to be available in the Placement enum in VContextHelp
	public enum Placement {
		RIGHT, LEFT, ABOVE, BELOW;
	}

	public ContextHelp() {
		helpHTML = new HashMap<String, String>();
		placements = new HashMap<String, Placement>();
		components = new ArrayList<Component>();
	}

	@SuppressWarnings("rawtypes")
	public void changeVariables(Object source, Map variables) {
		if (variables.containsKey("selectedComponentId")) {
			selectedComponentId = (String) variables.get("selectedComponentId");
		}
		if (variables.containsKey("hidden")) {
			hidden = (Boolean) variables.get("hidden");
		}
		requestRepaint();
	}

	public void paintContent(PaintTarget target) throws PaintException {
		target.addVariable(this, "selectedComponentId", selectedComponentId);
		target.addVariable(this, "hidden", hidden);
		if (selectedComponentId != null && !selectedComponentId.equals("")
				&& helpHTML.containsKey(selectedComponentId)) {
			target.addAttribute("helpText", helpHTML.get(selectedComponentId));
			if (placements.containsKey(selectedComponentId)) {
				target.addAttribute("placement",
						placements.get(selectedComponentId).name());
			}
		}
		target.addAttribute("followFocus", followFocus);
	}

	/**
	 * Registers a help text for a given component. The help text is in HTML
	 * format and can be formatted as such and styled with inline CSS. It is
	 * also possible to provide classnames for elements in the HTML and provide
	 * the CSS rules in the Vaadin theme.
	 * 
	 * @param component
	 *            the component for which to register the help text.
	 * @param help
	 *            the help text in HTML.
	 */
	public void addHelpForComponent(Component component, String help) {
		if (component.getDebugId() == null) {
			component.setDebugId(generateComponentId());
		}
		components.add(component);
		helpHTML.put(component.getDebugId(), help);
	}

	/**
	 * Registers a help text for a given component. The help text is in HTML
	 * format and can be formatted as such and styled with inline CSS. It is
	 * also possible to provide classnames for elements in the HTML and provide
	 * the CSS rules in the Vaadin theme.
	 * 
	 * @param component
	 *            the component for which to register the help text.
	 * @param help
	 *            the help text in HTML.
	 * @param placement
	 *            where the help bubble should be placed.
	 */
	public void addHelpForComponent(Component component, String help,
			Placement placement) {
		if (component.getDebugId() == null) {
			component.setDebugId(generateComponentId());
		}
		components.add(component);
		helpHTML.put(component.getDebugId(), help);
		setPlacement(component, placement);
	}

	private String generateComponentId() {
		return "help_" + helpComponentIdCounter++;
	}

	/**
	 * Programmatically show the help bubble for a given component.
	 * 
	 * @param component
	 *            the component for which to show the help bubble.
	 */
	public void showHelpFor(Component component) {
		if (component.getDebugId() != null
				&& helpHTML.containsKey(component.getDebugId())) {
			selectedComponentId = component.getDebugId();
			hidden = false;
			requestRepaint();
		}
	}

	/**
	 * This setting causes the help bubble to be displayed when a field is
	 * focused, and removed when the focus moves away from the field. If focus
	 * moves to a new field, the help bubble for this new field is shown.
	 * 
	 * @param followFocus
	 *            true (enabled) or false (disabled).
	 */
	public void setFollowFocus(boolean followFocus) {
		this.followFocus = followFocus;
		this.selectedComponentId = "";
		requestRepaint();
	}

	/**
	 * @return whether the help bubble follows focus or is opened with the F1
	 *         key.
	 */
	public boolean isFollowFocus() {
		return followFocus;
	}

	/**
	 * Specifies where the help bubble should be placed in relation to the
	 * component. This is only active for the specified component. The default
	 * is to place the help bubble to the right of components and if it doesn't
	 * fit there, ContextHelp attempts to place it below followed by above the
	 * component.
	 * 
	 * @param component
	 *            the component for which to define the placement of the help
	 *            bubble.
	 * @param placement
	 *            the placement of the help bubble.
	 */
	public void setPlacement(Component component, Placement placement) {
		placements.put(component.getDebugId(), placement);
	}
}
