package uk.org.ssvc.firestore.integration.repository.v1.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.org.ssvc.core.domain.model.member.Member;
import uk.org.ssvc.core.domain.model.member.RenewalDate;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

import static java.time.ZoneOffset.UTC;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberDocument {

    private String firstName;
    private String lastName;
    private AddressDocument address;
    private ContactDetailsDocument contactDetails;
    private List<MemberAssociateDocument> associates;
    private long expiryDate;

    public MemberDocument(Member member) {
        firstName = member.getFirstName();
        lastName = member.getLastName();
        address = new AddressDocument(member.getAddress());
        contactDetails = new ContactDetailsDocument(member.getContactDetails());
        associates = member.getAssociates().stream()
            .map(MemberAssociateDocument::new)
            .collect(toList());
        expiryDate = member.getRenewalDate().getExpiryDate().atStartOfDay(UTC).toEpochSecond();
    }

    public Member toDomain(String id) {
        return new Member(
            id,
            firstName,
            lastName,
            associates == null ? emptySet() : associates.stream().map(MemberAssociateDocument::toDomain).collect(toSet()),
            address.toDomain(),
            contactDetails.toDomain(),
            new RenewalDate(ZonedDateTime.ofInstant(Instant.ofEpochSecond(expiryDate), UTC).toLocalDate()),
            emptySet(),
            emptyList(),
            emptySet(),
            emptySet());
    }

}
