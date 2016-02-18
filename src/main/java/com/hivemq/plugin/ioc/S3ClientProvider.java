package com.hivemq.plugin.ioc;

import com.amazonaws.auth.*;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.hivemq.plugin.configuration.AuthenticationType;
import com.hivemq.plugin.configuration.Configuration;
import com.hivemq.spi.exceptions.UnrecoverableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;

/**
 * @author Christoph Schäbel
 */
public class S3ClientProvider implements Provider<AmazonS3> {

    private static final Logger log = LoggerFactory.getLogger(S3ClientProvider.class);

    private final Configuration configuration;

    @Inject
    public S3ClientProvider(final Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public AmazonS3 get() {

        final AuthenticationType authenticationType = configuration.getAuthenticationType();
        if (authenticationType == null) {
            log.error("S3 credentials type not configured, shutting down HiveMQ");
            throw new UnrecoverableException();
        }

        final AWSCredentials credentials;
        try {
            credentials = getAwsCredentials(authenticationType);
        } catch (Exception e) {
            log.error("Not able to authenticate with S3, shutting down HiveMQ");
            throw new UnrecoverableException();
        }

        final AmazonS3 s3 = new AmazonS3Client(credentials);

        final Regions regions = configuration.getRegion();
        if (regions == null) {
            log.error("S3 region is not configured, shutting down HiveMQ");
            throw new UnrecoverableException(false);
        }

        final Region region = Region.getRegion(regions);
        s3.setRegion(region);

        final String bucketName = configuration.getBucketName();

        if (bucketName == null) {
            log.error("S3 Bucket name is not configured, shutting down HiveMQ");
            throw new UnrecoverableException(false);
        }


        try {

            if (!s3.doesBucketExist(bucketName)) {
                log.error("S3 Bucket {} does not exist, shutting down HiveMQ", bucketName);
                throw new UnrecoverableException(false);
            }
        } catch (AmazonS3Exception e) {
            for (Map.Entry<String, String> entry : e.getAdditionalDetails().entrySet()) {
                log.debug("Additional Error information {} : {}", entry.getKey(), entry.getValue());
            }
            log.error("Error at checking if S3 bucket {} exists", bucketName, e);
        }

        return s3;
    }

    private AWSCredentials getAwsCredentials(final AuthenticationType authenticationType) {
        final AWSCredentials credentials;
        switch (authenticationType) {
            case DEFAULT:
                credentials = new DefaultAWSCredentialsProviderChain().getCredentials();
                break;
            case ENVIRONMENT_VARIABLES:
                credentials = new EnvironmentVariableCredentialsProvider().getCredentials();
                break;
            case JAVA_SYSTEM_PROPERTIES:
                credentials = new SystemPropertiesCredentialsProvider().getCredentials();
                break;
            case USER_CREDENTIALS_FILE:
                credentials = new ProfileCredentialsProvider().getCredentials();
                break;
            case INSTANCE_PROFILE_CREDENTIALS:
                credentials = new InstanceProfileCredentialsProvider().getCredentials();
                break;
            case ACCESS_KEY:
            case TEMPORARY_SESSION:
                final String accessKey = configuration.getAccessKeyId();
                final String secretAccessKey = configuration.getSecretAccessKey();
                if (authenticationType == AuthenticationType.ACCESS_KEY) {
                    credentials = new BasicAWSCredentials(accessKey, secretAccessKey);
                    break;
                }

                final String sessionToken = configuration.getSessionToken();
                credentials = new BasicSessionCredentials(accessKey, secretAccessKey, sessionToken);
                break;
            default:
                throw new IllegalArgumentException("Unknown credentials type");
        }
        return credentials;
    }

}
