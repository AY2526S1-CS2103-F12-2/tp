package seedu.address.ui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;

/**
 * An UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";

    private static final String LAUNCH_EMAIL_PREFIX = "mailto:";
    private static final String LAUNCH_TELEGRAM_PREFIX = "https://t.me/";
    private static final String LAUNCH_GITHUB_PREFIX = "http://github.com/";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
//    @FXML
//    private Label email;
//    @FXML
//    private Label telegram;
//    @FXML
//    private Label github;
    @FXML
    private FlowPane tags;

    @FXML
    private Hyperlink email;
    @FXML
    private Hyperlink telegram;
    @FXML
    private Hyperlink github;

    /**
     * Creates a {@code PersonCode} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
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


    public void launchEmail() {
        launchApplicationLink(LAUNCH_EMAIL_PREFIX + person.getEmail().value);
    }

    public void launchTelegram() {
        launchApplicationLink(LAUNCH_TELEGRAM_PREFIX + person.getTelegram().value);
    }

    public void launchGithub() {
        launchApplicationLink(LAUNCH_GITHUB_PREFIX + person.getGithub().value);
    }

    public void launchApplicationLink(String link) {
        try {
            URI uri = parseToUri(link);
            openLink(uri);
            // Result Display SUCCESS
        } catch (URISyntaxException | IOException e) {
            System.out.println("FAHH");
            // Result Display ERROR
        }
    }

    public URI parseToUri(String link) throws URISyntaxException {
        return new URI(link);
    }

    public void openLink(URI uri) throws IOException {
        Desktop.getDesktop().browse(uri);
    }

}
