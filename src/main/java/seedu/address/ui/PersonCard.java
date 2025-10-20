package seedu.address.ui;

import java.util.Comparator;
import java.util.function.Consumer;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.logic.util.ApplicationLinkLauncher;
import seedu.address.model.person.Person;

/**
 * An UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */
    public final Person person;

    private final Consumer<String> feedbackConsumer;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Hyperlink email;
    @FXML
    private Hyperlink telegram;
    @FXML
    private Hyperlink github;
    @FXML
    private FlowPane tags;

    /**
     * Creates a {@code PersonCode} with the given {@code Person} and index to display.
     * @param person            The person whose details are to be displayed.
     * @param displayedIndex    The index of the person in the list.
     * @param feedbackConsumer  The consumer to handle feedback messages.
     */
    public PersonCard(Person person, int displayedIndex, Consumer<String> feedbackConsumer) {
        super(FXML);
        this.person = person;
        this.feedbackConsumer = feedbackConsumer;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);

        // Optional Field: Email
        if (person.getEmail().isEmpty()) {
            email.setVisible(false);
            email.setManaged(false);
        } else {
            email.setVisible(true);
            email.setManaged(true);
            email.setText(person.getEmail().value);
        }

        // Optional Field: Telegram
        if (person.getTelegram().isEmpty()) {
            telegram.setVisible(false);
            telegram.setManaged(false);
        } else {
            String fieldName = "Telegram: ";
            telegram.setVisible(true);
            telegram.setManaged(true);
            telegram.setText(fieldName + person.getTelegram().value);
        }

        // Optional Field: Github
        if (person.getGithub().isEmpty()) {
            github.setVisible(false);
            github.setManaged(false);
        } else {
            String fieldName = "Github: ";
            github.setVisible(true);
            github.setManaged(true);
            github.setText(fieldName + person.getGithub().value);
        }

        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
    }


    /**
     * Launches the email application with the person's email address.
     * FeedbackConsumer will set resultDisplay based on success/failure of launch.
     */
    @FXML
    public void launchEmail() {
        feedbackConsumer.accept(ApplicationLinkLauncher.launchEmail(person.getEmail().value).getMessage());
    }

    /**
     * Launches the telegram application with the person's telegram handle.
     * FeedbackConsumer will set resultDisplay based on success/failure of launch.
     */
    @FXML
    public void launchTelegram() {
        feedbackConsumer.accept(ApplicationLinkLauncher.launchTelegram(person.getTelegram().value).getMessage());
    }

    /**
     * Launches the github page with the person's github username.
     * FeedbackConsumer will set resultDisplay based on success/failure of launch.
     */
    @FXML
    public void launchGithub() {
        feedbackConsumer.accept(ApplicationLinkLauncher.launchGithub(person.getGithub().value).getMessage());
    }

}
