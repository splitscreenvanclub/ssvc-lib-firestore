package uk.org.ssvc.firestore.integration.repository.v1;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteBatch;
import com.google.cloud.firestore.WriteResult;
import lombok.extern.slf4j.Slf4j;
import uk.org.ssvc.core.domain.exception.SsvcServerException;
import uk.org.ssvc.core.domain.model.member.Member;
import uk.org.ssvc.core.domain.model.member.search.MemberFilterCriteria;
import uk.org.ssvc.core.domain.repository.MemberRepository;
import uk.org.ssvc.firestore.integration.repository.v1.document.MemberDocument;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.time.ZoneOffset.UTC;
import static java.util.stream.Collectors.toList;
import static uk.org.ssvc.core.domain.model.member.search.MemberSearchField.EXPIRY_AFTER;
import static uk.org.ssvc.core.domain.model.member.search.MemberSearchField.EXPIRY_BEFORE;

@Singleton
@Slf4j
public class FirestoreMemberRepository implements MemberRepository {

    private static final int MAX_BATCH_SIZE = 500;
    private static final String MEMBER_COLLECTION = "members";

    private final Firestore db;

    @Inject
    public FirestoreMemberRepository(Firestore db) {
        this.db = db;
    }

    @Override
    public Member findById(String id) {
        ApiFuture<DocumentSnapshot> future = db.collection(MEMBER_COLLECTION).document(id).get();

        try {
            DocumentSnapshot docSnapshot = future.get();
            return docSnapshot.toObject(MemberDocument.class).toDomain(docSnapshot.getId());
        }
        catch (Exception e) {
            throw new SsvcServerException("Failed to find member by id=" + id, e);
        }
    }

    @Override
    public List<Member> findByCriteria(MemberFilterCriteria criteria) {
        Query membersQuery = db.collection(MEMBER_COLLECTION);

        if (criteria.valueFor(EXPIRY_AFTER).isPresent()) {
            membersQuery = membersQuery.whereGreaterThan("expiryDate", criteria.valueFor(EXPIRY_AFTER).get().atStartOfDay().toEpochSecond(UTC));
        }
        if (criteria.valueFor(EXPIRY_BEFORE).isPresent()) {
            membersQuery = membersQuery.whereLessThan("expiryDate", criteria.valueFor(EXPIRY_BEFORE).get().atStartOfDay().toEpochSecond(UTC));
        }

        ApiFuture<QuerySnapshot> future = membersQuery.get();

        try {
            return future.get().getDocuments().stream()
                .map(docSnapshot -> docSnapshot.toObject(MemberDocument.class).toDomain(docSnapshot.getId()))
                .collect(toList());
        }
        catch (Exception e) {
            throw new SsvcServerException("Failed to find members by query", e);
        }
    }

    @Override
    public List<Member> findAll() {
        ApiFuture<QuerySnapshot> future = db.collection(MEMBER_COLLECTION).get();

        try {
            return future.get().getDocuments().stream()
                .map(docSnapshot -> docSnapshot.toObject(MemberDocument.class).toDomain(docSnapshot.getId()))
                .collect(toList());
        }
        catch (Exception e) {
            throw new SsvcServerException("Failed to find all members", e);
        }
    }

    @Override
    public void add(Member member) {
        ApiFuture<WriteResult> future = db.collection(MEMBER_COLLECTION)
            .document(member.getId()).set(new MemberDocument(member));

        try {
            log.info("Successfully added member id={} updateTime={}",
                member.getId(), future.get().getUpdateTime());
        }
        catch (Exception e) {
            throw new SsvcServerException("Failed to add member", e);
        }
    }

    public void addAll(Collection<Member> members) {
        CollectionReference collectionRef = db.collection(MEMBER_COLLECTION);
        List<Member> remainingMembers = new ArrayList<>(members);

        while (!remainingMembers.isEmpty()) {
            int maxIndex = Math.min(MAX_BATCH_SIZE, remainingMembers.size());
            List<Member> batchedMembers = remainingMembers.subList(0, maxIndex);
            WriteBatch batch = db.batch();

            batchedMembers.forEach(m -> batch.set(
                collectionRef.document(m.getId()), new MemberDocument(m)));

            try {
                batch.commit().get();
                log.info("Successfully added batch of members batchSize={} lastId={}",
                    batchedMembers.size(), batchedMembers.get(batchedMembers.size()-1).getId());
                remainingMembers.removeAll(batchedMembers);
            }
            catch (Exception e) {
                throw new SsvcServerException("Failed to add all members", e);
            }
        }

        log.info("Completed writing all {} members to Google Firestore", members.size());
    }

}
