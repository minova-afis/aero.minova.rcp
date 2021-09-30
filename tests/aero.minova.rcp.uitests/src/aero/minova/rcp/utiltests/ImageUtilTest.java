package aero.minova.rcp.utiltests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import aero.minova.rcp.dataservice.ImageUtil;
import aero.minova.rcp.preferences.ApplicationPreferences;

class ImageUtilTest {

	@BeforeEach
	@Ignore
	void init() {
		InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE).put(ApplicationPreferences.FONT_SIZE, "S");
	}

	@Test
	@Ignore
	void testGetImageDescriptorFromImagesBundle() {

		ImageDescriptor imageDescriptorFromImagesBundle = ImageUtil.getImageDescriptorFromImagesBundle("Book.Command", false);
		assertEquals(16, imageDescriptorFromImagesBundle.getImageData(100).width);
		assertEquals(16, imageDescriptorFromImagesBundle.getImageData(100).height);
	}

	@Test
	@Ignore
	void testGetToolBarImageDescriptorFromImagesBundle() {
		ImageDescriptor imageDescriptorFromImagesBundle = ImageUtil.getImageDescriptorFromImagesBundle("Book.Command", true);
		assertEquals(24, imageDescriptorFromImagesBundle.getImageData(100).width);
		assertEquals(24, imageDescriptorFromImagesBundle.getImageData(100).height);
	}

}
