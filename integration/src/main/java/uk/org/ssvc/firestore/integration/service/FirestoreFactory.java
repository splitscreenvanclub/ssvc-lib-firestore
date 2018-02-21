package uk.org.ssvc.firestore.integration.service;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import uk.org.ssvc.core.domain.exception.SsvcExternalServiceException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class FirestoreFactory {

    private String clientId;
    private String clientEmail;
    private String privateKey;
    private String privateKeyId;
    private String projectId;

    @Inject
    public FirestoreFactory(@Named("google.clientId") String clientId,
                            @Named("google.clientEmail") String clientEmail,
                            @Named("google.privateKey") String privateKey,
                            @Named("google.privateKeyId") String privateKeyId,
                            @Named("google.projectId") String projectId) {
        this.clientId = clientId;
        this.clientEmail = clientEmail;
        this.privateKey = privateKey;
        this.privateKeyId = privateKeyId;
        this.projectId = projectId;
    }

    public Firestore create() {
        try {
            ServiceAccountCredentials credentials = ServiceAccountCredentials.fromPkcs8(
                clientId, clientEmail, privateKey, privateKeyId, null, null, null);
            FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setProjectId(projectId)
                .build();
            FirebaseApp.initializeApp(options);

            return FirestoreClient.getFirestore();
        }
        catch (Exception e) {
            throw new SsvcExternalServiceException("Failed to launch Firestore client", e);
        }
    }

}
