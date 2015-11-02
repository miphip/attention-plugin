package org.jenkinsci.plugins.attention;

import hudson.XmlFile;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ExportedBean
public final class VolunteerHistory {

    private static final Logger LOGGER = Logger.getLogger(VolunteerHistory.class.getName());

    private transient File configFile;

    private LinkedList<UserOperation> userOperations;

    public VolunteerHistory(File configDir) {
        this.configFile = new File(configDir, "VolunteerHistory.xml");
        if (configFile.exists()) {
            load();
        } else {
            userOperations = new LinkedList<UserOperation>();
        }
    }

    public synchronized void add(UserOperation op) {
        userOperations.add(op);
        save();
    }

    @Exported(visibility = 3)
    public List<UserOperation> getUserOperations() {
        return Collections.unmodifiableList(userOperations);
    }

    private synchronized void load() {
        try {
            new XmlFile(configFile).unmarshal(this);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to load "+configFile, e);
        }
    }

    public synchronized void save() {
        try {
            new XmlFile(configFile).write(this);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to save "+configFile,e);
        }
    }
}
