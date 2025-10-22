package seedu.address.storage;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Email;
import seedu.address.model.person.Github;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.PreferredCommunicationMode;
import seedu.address.model.person.Telegram;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String phone;
    private final String email;
    private final String telegram;
    private final String github;
    private final String preferredCommunicationMode;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name,
                             @JsonProperty("phone") String phone,
                             @JsonProperty("email") String email,
                             @JsonProperty("telegram") String telegram,
                             @JsonProperty("github") String github,
                             @JsonProperty("preferredCommunicationMode") String preferredCommunicationMode,
                             @JsonProperty("tags") List<JsonAdaptedTag> tags) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.telegram = telegram;
        this.github = github;
        this.preferredCommunicationMode = preferredCommunicationMode;
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        telegram = source.getTelegram().value;
        github = source.getGithub().value;
        preferredCommunicationMode = source.getPreferredMode().name();
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            personTags.add(tag.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        if (!Telegram.isValidTelegram(telegram)) {
            throw new IllegalValueException(Telegram.MESSAGE_CONSTRAINTS);
        }
        final Telegram modelTelegram = new Telegram(telegram);

        if (!Github.isValidGithub(github)) {
            throw new IllegalValueException(Github.MESSAGE_CONSTRAINTS);
        }
        final Github modelGithub = new Github(github);

        final Set<Tag> modelTags = new HashSet<>(personTags);

        // Validate preferred mode against available contact options
        Set<PreferredCommunicationMode> availableModes = getAvailableModes();
        boolean allowNone = true;
        if (!PreferredCommunicationMode.isValidMode(preferredCommunicationMode, availableModes, allowNone)) {
            throw new IllegalValueException(PreferredCommunicationMode.MESSAGE_CONSTRAINTS);
        }
        final PreferredCommunicationMode modelPreferredMode = PreferredCommunicationMode.of(preferredCommunicationMode);


        return new Person(modelName, modelPhone, modelEmail, modelTelegram, modelGithub, modelPreferredMode, modelTags);
    }

    /**
     * Determines which communication modes are available based on non-empty contact fields.
     * Phone is always included. Email, Telegram, and GitHub are added only if their values are not blank.
     *
     * @return a set of available {@code PreferredCommunicationMode} values
     */
    public Set<PreferredCommunicationMode> getAvailableModes() {
        Set<PreferredCommunicationMode> availableModes = EnumSet.noneOf(PreferredCommunicationMode.class);

        // Phone is compulsory
        availableModes.add(PreferredCommunicationMode.PHONE);

        // Optional modes based on non-empty fields
        boolean hasEmail = email != null && !email.isBlank();
        boolean hasTelegram = telegram != null && !telegram.isBlank();
        boolean hasGithub = github != null && !github.isBlank();

        if (hasEmail) {
            availableModes.add(PreferredCommunicationMode.EMAIL);
        }
        if (hasTelegram) {
            availableModes.add(PreferredCommunicationMode.TELEGRAM);
        }
        if (hasGithub) {
            availableModes.add(PreferredCommunicationMode.GITHUB);
        }

        return availableModes;
    }

}
