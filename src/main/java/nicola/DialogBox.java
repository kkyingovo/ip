package nicola;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainWindow.class.getResource(
                            "/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to load DialogBox.fxml", e);
        }

        dialog.setText(text);
        displayPicture.setImage(img);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    /**
     * Creates and styles a response from Nicola.
     *
     * @param text response text
     * @param img Nicola's image
     * @param commandType command that produced the response
     * @return styled Nicola dialog box
     */
    public static DialogBox getNicolaDialog(
            String text,
            Image img,
            String commandType
    ) {
        var db = new DialogBox(text, img);
        db.flip();
        db.changeDialogStyle(commandType);
        return db;
    }

    /**
     * Changes Nicola's bubble colour based on the command.
     *
     * @param commandType command most recently processed
     */
    private void changeDialogStyle(String commandType) {
        switch (commandType) {
            case "todo":
            case "deadline":
            case "event":
                dialog.getStyleClass().add("add-label");
                break;

            case "mark":
            case "unmark":
                dialog.getStyleClass().add("marked-label");
                break;

            case "delete":
                dialog.getStyleClass().add("delete-label");
                break;

            default:
                // Keep the normal reply-label colour.
                break;
        }
    }
}
