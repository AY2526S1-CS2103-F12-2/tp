package seedu.address.ui;

import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.Person;

/**
 * Panel containing the list of persons.
 */
public class PersonListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(PersonListPanel.class);

    @FXML
    private ListView<Person> personListView;

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList}.
     */
    public PersonListPanel(ObservableList<Person> personList) {
        super(FXML);
        personListView.setItems(personList);
        personListView.setCellFactory(listView -> new PersonListViewCell());
    }

    /**
     * Selects the next person in the list based on the selection model.
     */
    public void goToNextPerson() {
        int currentIndex = personListView.getSelectionModel().getSelectedIndex();
        if (currentIndex < personListView.getItems().size() - 1) {
            personListView.getSelectionModel().select(currentIndex + 1);
            personListView.scrollTo(currentIndex + 1);
        }
    }

    /**
     * Selects the previous person in the list based on the selection model.
     */
    public void goToPreviousPerson() {
        int currentIndex = personListView.getSelectionModel().getSelectedIndex();
        if (currentIndex > 0) {
            personListView.getSelectionModel().select(currentIndex - 1);
            personListView.scrollTo(currentIndex - 1);
        }
    }

    /**
     * Returns the currently selected person.
     */
    public Person getSelectedPerson() {
        return personListView.getSelectionModel().getSelectedItem();
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Person} using a {@code PersonCard}.
     */
    class PersonListViewCell extends ListCell<Person> {
        @Override
        protected void updateItem(Person person, boolean empty) {
            super.updateItem(person, empty);

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new PersonCard(person, getIndex() + 1).getRoot());
            }
        }
    }

}
