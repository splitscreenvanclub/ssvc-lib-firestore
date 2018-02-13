package uk.org.ssvc.firestore.integration.repository.v1.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.org.ssvc.core.domain.model.member.Member;
import uk.org.ssvc.core.domain.model.member.RenewalDate;

import java.time.Instant;
import java.time.ZonedDateTime;

import static java.time.ZoneOffset.UTC;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MemberDocument {

    private String firstName;
    private String lastName;
    private AddressDocument address;
    private ContactDetailsDocument contactDetails;
    private long expiryDate;

    public MemberDocument(Member member) {
        firstName = member.getFirstName();
        lastName = member.getLastName();
        address = new AddressDocument(member.getAddress());
        contactDetails = new ContactDetailsDocument(member.getContactDetails());
        expiryDate = member.getRenewalDate().getExpiryDate().atStartOfDay(UTC).toEpochSecond();
    }

    public Member toDomain(String id) {
        return new Member(
            id,
            firstName,
            lastName,
            emptySet(),
            address.toDomain(),
            contactDetails.toDomain(),
            new RenewalDate(ZonedDateTime.ofInstant(Instant.ofEpochSecond(expiryDate), UTC).toLocalDate()),
            emptySet(),
            emptyList(),
            emptySet(),
            emptySet());
    }

}
