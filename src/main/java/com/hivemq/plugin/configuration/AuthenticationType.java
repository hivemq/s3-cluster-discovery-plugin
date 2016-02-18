package com.hivemq.plugin.configuration;

/**
 * @author Christoph Schäbel
 */
public enum AuthenticationType {

    DEFAULT("default"),
    ENVIRONMENT_VARIABLES("environment_variables"),
    JAVA_SYSTEM_PROPERTIES("java_system_properties"),
    USER_CREDENTIALS_FILE("user_credentials_file"),
    INSTANCE_PROFILE_CREDENTIALS("instance_profile_credentials"),
    ACCESS_KEY("access_key"),
    TEMPORARY_SESSION("temporary_session");

    private String name;

    AuthenticationType(final String name) {
        this.name = name;
    }

    public static AuthenticationType fromName(final String name) {

        for (AuthenticationType type : values()) {
            if (name.equals(type.getName())) {
                return type;
            }
        }

        throw new IllegalArgumentException("Unknown credentials type " + name);
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }
}
