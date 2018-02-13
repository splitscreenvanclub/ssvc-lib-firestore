package uk.org.ssvc.firestore.integration.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import uk.org.ssvc.core.domain.exception.SsvcExternalServiceException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FirestoreFactory {

    @Inject
    public FirestoreFactory() {
    }

    public Firestore create() {
        try {
            GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
            FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .build();
            FirebaseApp.initializeApp(options);

            return FirestoreClient.getFirestore();
        }
        catch (Exception e) {
            throw new SsvcExternalServiceException("Failed to launch Firestore client", e);
        }
    }

}
