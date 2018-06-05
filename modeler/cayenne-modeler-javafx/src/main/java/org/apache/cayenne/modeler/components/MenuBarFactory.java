package org.apache.cayenne.modeler.components;

import com.google.inject.Inject;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import org.apache.cayenne.modeler.action.NewProjectAction;
import org.apache.cayenne.modeler.action.OpenProjectAction;
import org.apache.cayenne.modeler.action.SaveAsAction;
import org.apache.cayenne.modeler.services.DataMapService;
import org.apache.cayenne.modeler.services.DbEntityService;
import org.apache.cayenne.modeler.services.ObjEntityService;
import org.apache.cayenne.modeler.util.IconUtil;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MenuBarFactory {

    private MenuBar menuBar;

    @Inject
    private SaveAsAction saveAction;

    @Inject
    private NewProjectAction newProjectAction;

    @Inject
    private OpenProjectAction openProjectAction;

    @Inject
    private DataMapService dataMapService;

    @Inject
    private DbEntityService dbEntityService;

    @Inject
    private ObjEntityService objEntityService;

    public void setMenuBar(MenuBar menuBar){
        this.menuBar = menuBar;
    }

    public void createMenuBar(){
        Menu fileMenuItem = new Menu("File");
        MenuItem newProjectMenuItem = new MenuItem("New Project", IconUtil.imageForString("NewProject"));
        newProjectMenuItem.setOnAction(e -> newProjectAction.handle(e));
        MenuItem openProjectMenuItem = new MenuItem("Open Project", IconUtil.imageForString("OpenProject"));
        openProjectMenuItem.setOnAction(e -> openProjectAction.handle(e));
        MenuItem saveProjectMenuItem = new MenuItem("Save", IconUtil.imageForString("Save"));
        saveProjectMenuItem.setOnAction(e -> saveAction.handle(e));
        fileMenuItem.getItems().addAll(newProjectMenuItem, openProjectMenuItem, saveProjectMenuItem);

        Menu editMenuItem = new Menu("Edit");
        Menu projectMenuItem = new Menu("Project");
        MenuItem createDataMapMenuItem = new MenuItem("Create DataMap", IconUtil.imageForString("DataMap"));
        createDataMapMenuItem.setOnAction(e -> dataMapService.createDataMap());
        MenuItem createDbEntityMenuItem = new MenuItem("Create DbEntity", IconUtil.imageForString("DbEntity"));
        createDbEntityMenuItem.setOnAction(e -> dbEntityService.createDbEntity());
        MenuItem createObjEntityMenuItem = new MenuItem("Create ObjEntity", IconUtil.imageForString("ObjEntity"));
        createObjEntityMenuItem.setOnAction(e -> objEntityService.createObjEntity());
        projectMenuItem.getItems().addAll(createDataMapMenuItem, createDbEntityMenuItem, createObjEntityMenuItem);

        Menu toolsMenuItem = new Menu("Tools");
        Menu helpMenuItem = new Menu("Help");
        MenuItem documentationMenuItem = new MenuItem("Documentation");
        documentationMenuItem.setOnAction(action -> {
            try {
                URI uri = new URI("https://cayenne.apache.org/docs/4.1/getting-started-guide/");
                Desktop.getDesktop().browse(uri);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
        helpMenuItem.getItems().addAll(documentationMenuItem);

        menuBar.getMenus().addAll(fileMenuItem, editMenuItem, projectMenuItem, toolsMenuItem, helpMenuItem);
    }
}
