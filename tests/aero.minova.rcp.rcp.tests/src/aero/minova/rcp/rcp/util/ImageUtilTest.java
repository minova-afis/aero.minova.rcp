package aero.minova.rcp.rcp.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import aero.minova.rcp.preferences.ApplicationPreferences;

class ImageUtilTest {

	@BeforeEach
	void init() {
		InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE).put(ApplicationPreferences.FONT_SIZE, "S");
	}

	@Test
	void testGetImageDescriptorFromImagesBundle() {
		ImageDescriptor imageDescriptorFromImagesBundle = ImageUtil.getImageDescriptorFromImagesBundle("Book.Command");
		assertEquals(16, imageDescriptorFromImagesBundle.getImageData(100).width);
		assertEquals(16, imageDescriptorFromImagesBundle.getImageData(100).height);
	}

	@Test
	void testGetToolBarImageDescriptorFromImagesBundle() {
		ImageDescriptor imageDescriptorFromImagesBundle = ImageUtil.getToolBarImageDescriptorFromImagesBundle("Book.Command");
		assertEquals(24, imageDescriptorFromImagesBundle.getImageData(100).width);
		assertEquals(24, imageDescriptorFromImagesBundle.getImageData(100).height);
	}

}
