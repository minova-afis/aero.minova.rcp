package aero.minova.rcp.rcp.handlers;

import java.util.Locale;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.basic.impl.PartImpl;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.nebula.widgets.opal.textassist.TextAssist;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.css.widgets.MinovaSection;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MSection;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.rcp.util.TabUtil;
import aero.minova.rcp.rcp.util.TextAssistUtil;

public class SetNewTabHandler {

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.SELECT_ALL_CONTROLS)
	boolean selectAllControls;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.DATE_UTIL)
	String datePattern;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.TIME_UTIL)
	String timePattern;

	@Inject
	TranslationService translationService;
	
	@Inject
	Locale locale;

	@Execute
	public void execute(EModelService modelService, MWindow window) {
		// Wir holen und das PerspectiveStack, das alle Perspektiven enthält
		MPerspectiveStack perspectiveStack = (MPerspectiveStack) modelService.find("aero.minova.rcp.rcp.perspectivestack", window);

		// Für jede Perspektive wird die TabListe aktualisiert
		for (MPerspective perspective : perspectiveStack.getChildren()) {
			PartImpl detailPart = (PartImpl) modelService.find("aero.minova.rcp.rcp.part.details", perspective);
			Composite detail = (Composite) ((WFCDetailPart) detailPart.getObject()).getComposite().getData(Constants.SECTION_PARENT);
			((Composite) detail.getData(Constants.PART_COMPOSITE))
					.setTabList(TabUtil.getTabListForPart(((Composite) detail.getData(Constants.PART_COMPOSITE)), selectAllControls));
			Control[] sections = detail.getChildren();
			for (Control minovaSection : sections) {
				((MinovaSection) minovaSection).setTabList(
						TabUtil.getTabListForSection((MinovaSection) minovaSection, (MSection) minovaSection.getData(Constants.MSECTION), selectAllControls));
				Composite compo = null;
				for (Control control : ((MinovaSection) minovaSection).getChildren()) {
					if (control instanceof Composite) {
						compo = (Composite) control;
					}
				}
				for (Control control : compo.getChildren()) {
					if (control instanceof TextAssist) {
						TextAssist text = (TextAssist) control;
						switch ((String) text.getData(Constants.TEXTASSIST_TYPE)) {
						case Constants.TEXTASSIST_TYPE_DATE:
							text.setContentProvider(TextAssistUtil.getDateTextAssistProvider((MField) text.getData(Constants.CONTROL_FIELD), translationService,
									locale, datePattern));
							break;
						case Constants.TEXTASSIST_TYPE_TIME:
							text.setContentProvider(TextAssistUtil.getTimeTextAssistProvider((MField) text.getData(Constants.CONTROL_FIELD), translationService,
									locale, timePattern));
							break;
						case Constants.TEXTASSIST_TYPE_DATE_TIME:
							text.setContentProvider(TextAssistUtil.getTextAssistProvider((MField) text.getData(Constants.CONTROL_FIELD), translationService,
									locale, datePattern, timePattern));
							break;
						default:
							break;
						}
						text.setContentProvider(null);
					}
				}
			}
		}
	}
}
