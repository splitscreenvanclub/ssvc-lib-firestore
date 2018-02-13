package uk.org.ssvc.email.integration.firestore.v1.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.org.ssvc.core.domain.model.ContactDetails;
import uk.org.ssvc.core.domain.model.TelephoneNumber;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Optional.ofNullable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContactDetailsDocument {

    private String emailAddress;
    private String telNumber1;
    private String telNumber2;
    private String telNumber3;
    private String telNumber4;

    public ContactDetailsDocument(ContactDetails contactDetails) {
        List<TelephoneNumber> numbers = contactDetails.getTelephoneNumbers();

        emailAddress = contactDetails.getEmailAddress();

        telNumber1 = numbers.size() > 0 ? numbers.get(0).getNumber() : null;
        telNumber2 = numbers.size() > 1 ? numbers.get(1).getNumber() : null;
        telNumber3 = numbers.size() > 2 ? numbers.get(2).getNumber() : null;
        telNumber4 = numbers.size() > 3 ? numbers.get(3).getNumber() : null;
    }

    public ContactDetails toDomain() {
        List<TelephoneNumber> numbers = new ArrayList<>();
        Consumer<String> addToList = no -> numbers.add(new TelephoneNumber(no));

        ofNullable(telNumber1).ifPresent(addToList);
        ofNullable(telNumber2).ifPresent(addToList);
        ofNullable(telNumber3).ifPresent(addToList);
        ofNullable(telNumber4).ifPresent(addToList);

        return new ContactDetails(numbers, emailAddress);
    }

}
