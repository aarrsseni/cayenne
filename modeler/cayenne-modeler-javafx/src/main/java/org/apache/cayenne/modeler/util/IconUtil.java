package org.apache.cayenne.modeler.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.modeler.ModelerConstants;

public final class IconUtil {

    protected static ImageView domainImage = createImage("dbMore.png");


    private static ImageView createImage(String name) {
        ImageView imageView = new ImageView(new Image(String.valueOf(IconUtil.class.getClassLoader()
                .getResource(ModelerConstants.RESOURCE_PATH + name))));
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        return imageView;
    }

    public static ImageView imageForObject(Object object) {
        if(object == null) {
            return null;
        }

        if(object instanceof DataChannelDescriptor) {
            return domainImage;
        }
        return null;
    }
}
