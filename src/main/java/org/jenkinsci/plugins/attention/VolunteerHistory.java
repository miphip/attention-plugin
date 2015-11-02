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

    public void volunteerOperation(
            int buildNumber, String user,
            String volunteer, boolean is_team, String comment) {
        UserOperation op = new UserOperation(buildNumber, user, UserOperation.Type.VOLUNTEER);
        op.setVolunteer(volunteer);
        op.setVolunteer_is_team(is_team);
        op.setComment(comment);
        addUserOperation(op);
    }

    public void unVolunteerOperation(
            int buildNumber, String user,
            String volunteer, boolean is_team) {
        UserOperation op = new UserOperation(buildNumber, user, UserOperation.Type.UN_VOLUNTEER);
        op.setVolunteer(volunteer);
        op.setVolunteer_is_team(is_team);
        addUserOperation(op);
    }

    public void fixSubmittedOperation(int buildNumber, String user) {
        addUserOperation(new UserOperation(buildNumber, user, UserOperation.Type.FIX_SUBMITTED));
    }

    public void noFixSubmittedOperation(int buildNumber, String user) {
        addUserOperation(new UserOperation(buildNumber, user, UserOperation.Type.NO_FIX_SUBMITTED));
    }

    public void intermittentOperation(int buildNumber, String user) {
        addUserOperation(new UserOperation(buildNumber, user, UserOperation.Type.INTERMITTENT));
    }

    public void notIntermittentOperation(int buildNumber, String user) {
        addUserOperation(new UserOperation(buildNumber, user, UserOperation.Type.NOT_INTERMITTENT));
    }

    public void greenBuildOperation(int buildNumber) {
        addUserOperation(new UserOperation(buildNumber, null, UserOperation.Type.GREEN_BUILD));
    }

    @Exported(visibility = 3)
    public List<UserOperation> getUserOperations() {
        return Collections.unmodifiableList(userOperations);
    }

    private synchronized void addUserOperation(UserOperation op) {
        userOperations.add(op);
        save();
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
