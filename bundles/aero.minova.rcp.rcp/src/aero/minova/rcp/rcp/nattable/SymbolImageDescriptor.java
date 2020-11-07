package aero.minova.rcp.rcp.nattable;

import java.util.Objects;

import org.eclipse.jface.resource.ColorDescriptor;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageDataProvider;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

/**
 * An {@link ImageDescriptor} that provides the ability to render a given symbol
 * from a font as an icon
 *
 * @since 3.22
 *
 */
public class SymbolImageDescriptor extends ImageDescriptor implements ImageDataProvider {

	private final String symbolString;
	private final FontDescriptor fontDescriptor;
	private final ColorDescriptor colorDescriptor;
	private final int initilaSize;

	/**
	 * @param symbolString    the symbol to use
	 * @param colorDescriptor the color that should be used for rendering the
	 *                        symbol, if <code>null</code> the system color
	 *                        {@link SWT#COLOR_BLACK} will be used
	 * @param fontDescriptor  the font for rendering the symbol if <code>null</code>
	 *                        the default font will be used
	 * @param initialSize     the initial size used for 100% rendering
	 *
	 */
	SymbolImageDescriptor(String symbolString, ColorDescriptor colorDescriptor, FontDescriptor fontDescriptor,
			int initialSize) {

		this.symbolString = Objects.requireNonNull(symbolString, "symbolString can't be null"); //$NON-NLS-1$
		this.fontDescriptor = fontDescriptor;
		this.colorDescriptor = colorDescriptor;
		this.initilaSize = initialSize;
	}

	@Override
	public Image createImage(boolean returnMissingImageOnError, Device device) {
		return createImage(returnMissingImageOnError, device, 100);
	}

	private Image createImage(boolean returnMissingImageOnError, Device device, int zoom) {
		try {
			int res = (initilaSize * zoom) / 100;
			final ImageData transparentImage = new ImageData(res, res, 24, new PaletteData(0xFF, 0xFF00, 0xFF0000));
			transparentImage.alphaData = new byte[res * res];
			Image image = new Image(device, transparentImage);
			GC gc = new GC(image);
			try {
				gc.setTextAntialias(SWT.ON);
				Point dpi = device.getDPI();
				int points = res * 72 / dpi.y;
				Font font;
				if (fontDescriptor != null) {
					font = fontDescriptor.setHeight(points).createFont(device);
				} else {
					font = FontDescriptor.createFrom(device.getSystemFont()).setHeight(points).createFont(device);
				}
				Color color;
				if (colorDescriptor != null) {
					color = colorDescriptor.createColor(device);
				} else {
					color = device.getSystemColor(SWT.COLOR_BLACK);
				}
				gc.setFont(font);
				gc.setForeground(color);
				gc.setBackground(device.getSystemColor(SWT.COLOR_TRANSPARENT));
				Point extent = gc.stringExtent(symbolString);
				int x = res / 2 - extent.x / 2;
				int y = res / 2 - extent.y / 2;
				gc.drawString(symbolString, x, y, true);
				if (colorDescriptor != null) {
					color.dispose();
				}
				font.dispose();
			} finally {
				gc.dispose();
			}
			return image;
		} catch (IllegalArgumentException | SWTException e) {
			if (returnMissingImageOnError) {
				try {
					return new Image(device, DEFAULT_IMAGE_DATA);
				} catch (SWTException swt) {
					return null;
				}
			}
			return null;
		}
	}

	@Override
	public ImageData getImageData(int zoom) {
		Image image = createImage(false, Display.getCurrent(), zoom);
		if (image != null) {
			ImageData data = image.getImageData();
			image.dispose();
			return data;
		}
		return null;
	}

	/**
	 * Creates a new SymbolImageDescriptor with identical properties but a different
	 * color
	 *
	 * @param colorDescriptor
	 * @return a {@link SymbolImageDescriptor} with the given color used for
	 *         rendering
	 */
	public SymbolImageDescriptor withColor(ColorDescriptor colorDescriptor) {
		if (Objects.equals(colorDescriptor, this.colorDescriptor)) {
			return this;
		}
		return new SymbolImageDescriptor(symbolString, colorDescriptor, fontDescriptor, initilaSize);
	}

	/**
	 * Creates a new SymbolImageDescriptor with identical properties but a symbol
	 *
	 * @param symbol the new symbol
	 * @return a {@link SymbolImageDescriptor} with the given symbol used for
	 *         rendering
	 */
	public SymbolImageDescriptor withSymbol(String symbol) {
		if (Objects.equals(symbol, this.symbolString)) {
			return this;
		}
		return new SymbolImageDescriptor(symbol, colorDescriptor, fontDescriptor, initilaSize);
	}

}
