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

    private transient File historyFile;

    private LinkedList<UserOperation> userOperations;

    public VolunteerHistory(File configDir) {
        this.historyFile = new File(configDir, "VolunteerHistory.xml");
        if (historyFile.exists()) {
            load();
        } else {
            userOperations = new LinkedList<>();
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
            new XmlFile(historyFile).unmarshal(this);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to load "+ historyFile, e);
        }
    }

    public synchronized void save() {
        try {
            new XmlFile(historyFile).write(this);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to save "+ historyFile,e);
        }
    }
}
