package uk.org.ssvc.email.integration.firestore.v1.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.org.ssvc.core.domain.model.Address;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddressDocument {

    private String lineOne;
    private String lineTwo;
    private String lineThree;
    private String lineFour;
    private String county;
    private String region;
    private String postcode;

    public AddressDocument(Address address) {
        lineOne = address.getLineOne();
        lineTwo = address.getLineTwo();
        lineThree = address.getLineThree();
        lineFour = address.getLineFour();
        county = address.getCounty();
        region = address.getRegion();
        postcode = address.getPostcode();
    }

    public Address toDomain() {
        return new Address(
            lineOne,
            lineTwo,
            lineThree,
            lineFour,
            county,
            region,
            postcode
        );
    }

}
