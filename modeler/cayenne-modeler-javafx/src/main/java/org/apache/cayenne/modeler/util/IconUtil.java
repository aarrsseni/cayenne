package org.apache.cayenne.modeler.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.modeler.ModelerConstants;

public final class IconUtil {

    protected static Image domainImage = createImage("dbMore.png");
    protected static Image dataMapImage = createImage("original.png");
    protected static Image dbEntityImage = createImage("dbEntity.png");


    private static Image createImage(String name) {
        Image image = new Image(String.valueOf(IconUtil.class.getClassLoader()
                .getResource(ModelerConstants.RESOURCE_PATH + name)));
        return image;
    }

    public static ImageView imageForObject(Object object) {
        if(object == null) {
            return null;
        }
        ImageView imageView = new ImageView();
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);
        if(object instanceof DataChannelDescriptor) {
            imageView.setImage(domainImage);
            return imageView;
        } else if(object instanceof DataMap) {
            imageView.setImage(dataMapImage);
            return imageView;
        } else if(object instanceof DbEntity) {
            imageView.setImage(dbEntityImage);
            return imageView;
        }
        return null;
    }
}
