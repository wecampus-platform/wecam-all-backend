package org.example.wecambackend.service.client.common.filesave;

public enum FilePath {
    AFFILIATION("affiliation"),
    NEW_STUDENT("new-student"),
    CURRENT_STUDENT("current-student"),
    PROFILE("profile"),
    PROFILE_THUMB("profile-thumb"),
    PRESIDENT_AUTH("president-auth"),
    MEETINGS("meetings");

    private final String dirName;

    FilePath(String dirName) {
        this.dirName = dirName;
    }

    public String getDirName() {
        return dirName;
    }
}
