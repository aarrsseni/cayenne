package org.apache.cayenne.modeler.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.modeler.ModelerConstants;

public final class IconUtil {

    public static Image domainImage = createImage("dbMore.png");
    public static Image dataMapImage = createImage("original.png");
    public static Image dbEntityImage = createImage("dbEntity.png");
    public static Image objEntityImage = createImage("newObjentity.png");
    public static Image projectImage = createImage("new.png");
    public static Image openProjectImage = createImage("open.png");
    public static Image saveProjectImage = createImage("floppy.png");


    private static Image createImage(String name) {
        Image image = new Image(String.valueOf(IconUtil.class.getClassLoader()
                .getResource(ModelerConstants.RESOURCE_PATH + name)));
        return image;
    }

    public static ImageView imageForObject(Object object) {
        if(object == null) {
            return null;
        }

        if(object instanceof DataChannelDescriptor) {
            return createIcon(domainImage);
        } else if(object instanceof DataMap) {
            return createIcon(dataMapImage);
        } else if(object instanceof DbEntity) {
            return createIcon(dbEntityImage);
        } else if(object instanceof ObjEntity) {
            return createIcon(objEntityImage);
        }
        return null;
    }

    public static ImageView createIcon(Image image){
        ImageView imageView = new ImageView();
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        imageView.setImage(image);
        return imageView;
    }
}
