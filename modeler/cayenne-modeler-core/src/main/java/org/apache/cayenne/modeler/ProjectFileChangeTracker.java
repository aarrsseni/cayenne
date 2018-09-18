/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/
package org.apache.cayenne.modeler;

import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.modeler.event.ProjectFileOnChangeTrackerEvent;
import org.apache.cayenne.project.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ProjectWatchdog class is responsible for tracking changes in cayenne.xml and
 * other Cayenne project files
 *
 */
public class ProjectFileChangeTracker extends Thread {

    private static final Logger log = LoggerFactory.getLogger(ProjectFileChangeTracker.class);

    /**
     * The default delay between every file modification check
     */
    private static final long DEFAULT_DELAY = 4000;

    /**
     * The names of the files to observe for changes.
     */
    protected Map<String, FileInfo> files;
    protected boolean paused;
    protected boolean isShownRemoveDialog;
    protected boolean isShownChangeDialog;
    protected ProjectController mediator;
    public ProjectFileChangeTracker(ProjectController mediator) {

        this.files = new ConcurrentHashMap<>();
        this.mediator = mediator;

        setName("cayenne-modeler-file-change-tracker");
    }

    /**
     * Reloads files to watch from the project. Useful when project's structure
     * has changed
     */
    public void reconfigure() {
        pauseWatching();

        removeAllFiles();

        Project project = mediator.getProject();

        // check if project exists and has been saved at least once.
        if (project != null && project.getConfigurationResource() != null) {
            String projectPath = project.getConfigurationResource().getURL().getPath() + File.separator;
            addFile(projectPath);

            for (DataMap dm : ((DataChannelDescriptor) project.getRootNode()).getDataMaps()) {
                if (dm.getConfigurationSource() != null) {
                    // if DataMap is in separate file, monitor it
                    addFile(dm.getConfigurationSource().getURL().getPath());
                }
            }

        }

        resumeWatching();
    }

    /**
     * Adds a new file to watch
     *
     * @param location
     *            path of file
     */
    public void addFile(String location) {
        try {
            files.put(location, new FileInfo(location));
        } catch (SecurityException e) {
            log.error("SecurityException adding file " + location, e);
        }
    }

    /**
     * Turns off watching for a specified file
     *
     * @param location
     *            path of file
     */
    public void removeFile(String location) {
        files.remove(location);
    }

    /**
     * Turns off watching for all files
     */
    public void removeAllFiles() {
        files.clear();
    }

    protected void check() {
        if (paused) {
            return;
        }

        boolean hasChanges = false;
        boolean hasDeletions = false;

        for (Iterator<FileInfo> it = files.values().iterator(); it.hasNext();) {
            FileInfo fi = it.next();

            boolean fileExists;
            try {
                fileExists = fi.getFile().exists();
            } catch (SecurityException e) {
                log.error("SecurityException checking file " + fi.getFile().getPath(), e);

                // we still process with other files
                continue;
            }

            if (fileExists) {
                // this can also throw a SecurityException
                long l = fi.getFile().lastModified();
                if (l > fi.getLastModified()) {
                    // however, if we reached this point this is very unlikely.
                    fi.setLastModified(l);
                    hasChanges = true;
                }
            }
            // the file has been removed
            else if (fi.getLastModified() != -1) {
                hasDeletions = true;
                it.remove(); // no point to watch the file now
            }
        }

        if (hasDeletions && !isShownRemoveDialog) {
            mediator.fireEvent(new ProjectFileOnChangeTrackerEvent(this, this, "Remove"));
        } else if (hasChanges && !isShownChangeDialog) {
            mediator.fireEvent(new ProjectFileOnChangeTrackerEvent(this, this, "Change"));
        }
    }

    public void setShownRemoveDialog(boolean isShownRemoveDialog) {
        this.isShownRemoveDialog = isShownRemoveDialog;
    }

    public void setShownChangeDialog(boolean isShownChangeDialog) {
        this.isShownChangeDialog = isShownChangeDialog;
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(DEFAULT_DELAY);
                check();
            } catch (InterruptedException e) {
                // someone asked to stop
                return;
            }
        }
    }

    /**
     * Tells watcher to pause watching for some time. Useful before changing
     * files
     */
    public void pauseWatching() {
        paused = true;
    }

    /**
     * Resumes watching for files
     */
    public void resumeWatching() {
        paused = false;
    }

    /**
     * Class to store information about files (last modification time & File
     * pointer)
     */
    protected class FileInfo {

        /**
         * Exact java.io.File object, may not be null
         */
        File file;

        /**
         * Time the file was modified
         */
        long lastModified;

        /**
         * Creates new object
         *
         * @param location
         *            the file path
         */
        public FileInfo(String location) {
            file = new File(location);
            lastModified = file.exists() ? file.lastModified() : -1;
        }

        public File getFile() {
            return file;
        }

        public long getLastModified() {
            return lastModified;
        }

        public void setLastModified(long l) {
            lastModified = l;
        }
    }

}
