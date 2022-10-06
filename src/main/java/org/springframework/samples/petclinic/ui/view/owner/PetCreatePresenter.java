package org.springframework.samples.petclinic.ui.view.owner;

import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.samples.petclinic.backend.SqsPublisher;
import org.springframework.samples.petclinic.backend.owner.Owner;
import org.springframework.samples.petclinic.backend.owner.OwnerRepository;
import org.springframework.samples.petclinic.backend.owner.Pet;
import org.springframework.samples.petclinic.backend.owner.PetRepository;

@RouteScope
@SpringComponent
public class PetCreatePresenter extends PetFormPresenter<PetCreateView> {

	private final SqsPublisher sqsPublisher;

    PetCreatePresenter(OwnerRepository ownerRepository,
					   PetRepository petRepository,
					   SqsPublisher sqsPublisher) {
        super(ownerRepository, petRepository);
		this.sqsPublisher = sqsPublisher;
	}

    @Override
    public void initModel(Integer ownerId, Integer petId) {
        Owner owner = ownerRepository.findById(ownerId);

        model = new PetFormDto(ownerId, owner.getFirstName() + " " + owner.getLastName());

        view.show(model);
    }

    @Override
    public void save() {
        Pet pet = new Pet();
        pet.setName(model.getPetName());
        pet.setBirthDate(model.getPetBirthDate());
        pet.setType(model.getPetType());

        Owner owner = ownerRepository.findById(model.getOwnerId());
        owner.addPet(pet);

        petRepository.save(pet);

		// ----------------------- SEND SQS MESSAGE -----------------------
		sqsPublisher.publish(String.format("{" +
												  "\"action\"=\"added\"," +
												  "\"petName\"=\"%s\"" +
												  "}",
										   pet.getName()));
		// ----------------------- SEND SQS MESSAGE -----------------------

        view.navigateToOwnerDetails(model.getOwnerId());
    }

}
