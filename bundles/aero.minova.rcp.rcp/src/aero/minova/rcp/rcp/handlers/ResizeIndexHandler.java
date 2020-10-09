package aero.minova.rcp.rcp.handlers;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;

import aero.minova.rcp.core.ui.PartsID;
import aero.minova.rcp.rcp.parts.XMLIndexPart;

public class ResizeIndexHandler {

	@Inject
	private EModelService model;

	@Execute
	public void execute(MPart mpart, MPerspective mPerspective) {

		List<MPart> findElements = model.findElements(mPerspective, PartsID.INDEX_PART, MPart.class);
		XMLIndexPart indexPart = (XMLIndexPart) findElements.get(0).getObject();
		NatTable table = indexPart.getNatTable();
		AffineTransform affinetransform = new AffineTransform();
		// TODO: Bezug der Font aus den Preferences
		Font font = new Font("Arial", Font.PLAIN, 12);
		FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
		DataLayer rowHeaderDataLayer = null;
		ILayer l = table.getLayer();
		Collection co = l.getUnderlyingLayersByRowPosition(1);
		ArrayList<ILayer> al = new ArrayList();
		al.addAll(co);
		for (ILayer il : al) {
			if (il.toString().equals("RowHeaderLayer")) {
				rowHeaderDataLayer = (DataLayer) il.getUnderlyingLayerByPosition(0, 0);
			}
		}
		for (int column = 1; column < table.getColumnCount(); column++) {
			int expandedWidth = 0;
			for (int row = 0; row < table.getRowCount() - 1; row++) {
				String cellContent = table.getCellByPosition(column, row).getDataValue().toString();
				int cellWidth = (int) (font.getStringBounds(cellContent, frc).getWidth());
				if (expandedWidth < cellWidth) {
					expandedWidth = cellWidth;
				}
			}
			// TODO: Die Methode setColumWidthByPosition verändert nicht die Breite der
			// Column
			System.out.println(l.getColumnWidthByPosition(column));
			System.out.println(expandedWidth);
			rowHeaderDataLayer.setColumnWidthByPosition(column, expandedWidth);
			table.refresh();
			System.out.println(l.getColumnWidthByPosition(column));
		}
	}
}
