package uk.org.ssvc.firestore.integration.repository.v1.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.org.ssvc.core.domain.model.member.MemberAssociate;

import java.time.Instant;
import java.time.ZonedDateTime;

import static java.time.ZoneOffset.UTC;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberAssociateDocument {

    private String firstName;
    private String lastName;
    private long dateOfBirth;

    public MemberAssociateDocument(MemberAssociate domain) {
        firstName = domain.getFirstName();
        lastName = domain.getLastName();
        dateOfBirth = domain.getDateOfBirth().atStartOfDay(UTC).toEpochSecond();
    }

    public MemberAssociate toDomain() {
        return new MemberAssociate(
            firstName,
            lastName,
            ZonedDateTime.ofInstant(Instant.ofEpochSecond(dateOfBirth), UTC).toLocalDate()
        );
    }

}
