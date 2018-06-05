package org.apache.cayenne.modeler.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.modeler.ModelerConstants;

public final class IconUtil {

    protected static Image domainImage = createImage("dbMore.png");
    protected static Image dataMapImage = createImage("original.png");
    protected static Image dbEntityImage = createImage("dbEntity.png");
    protected static Image objEntityImage = createImage("newObjentity.png");
    protected static Image projectImage = createImage("new.png");
    protected static Image openProjectImage = createImage("open.png");
    private static Image saveProjectImage = createImage("floppy.png");


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
        } else if(object instanceof ObjEntity) {
            imageView.setImage(objEntityImage);
            return imageView;
        }
        return null;
    }

    public static ImageView imageForString(String str){
        ImageView imageView = new ImageView();
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);
      if(str.equals("NewProject")){
            imageView.setImage(projectImage);
            return imageView;
      } else if(str.equals("OpenProject")){
          imageView.setImage(openProjectImage);
          return imageView;
      } else if(str.equals("Save")){
          imageView.setImage(saveProjectImage);
          return imageView;
      } else if(str.equals("DataMap")){
          imageView.setImage(dataMapImage);
          return imageView;
      } else if(str.equals("DbEntity")){
          imageView.setImage(dbEntityImage);
          return imageView;
      } else if(str.equals("ObjEntity")){
          imageView.setImage(objEntityImage);
          return imageView;
      }
        return null;
    }
}
