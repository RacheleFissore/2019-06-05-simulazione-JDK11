/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.crimes;

import java.net.URL;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import it.polito.tdp.crimes.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="boxAnno"
    private ComboBox<Integer> boxAnno; // Value injected by FXMLLoader

    @FXML // fx:id="boxMese"
    private ComboBox<Integer> boxMese; // Value injected by FXMLLoader

    @FXML // fx:id="boxGiorno"
    private ComboBox<Integer> boxGiorno; // Value injected by FXMLLoader

    @FXML // fx:id="btnCreaReteCittadina"
    private Button btnCreaReteCittadina; // Value injected by FXMLLoader

    @FXML // fx:id="btnSimula"
    private Button btnSimula; // Value injected by FXMLLoader

    @FXML // fx:id="txtN"
    private TextField txtN; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCreaReteCittadina(ActionEvent event) {
    	if(boxAnno.getValue() != null) {
    		model.creaGrafo(boxAnno.getValue());
    		
    		txtResult.appendText("# VERTICI: " + model.getNVertici() + "\n# ARCHI: " + model.getNArchi() + "\n");
    		txtResult.appendText(model.viciniAdiacenti());
    	}
    }

    @FXML
    void doSimula(ActionEvent event) {
    	txtResult.clear();
    	Integer anno, mese, giorno, n=null;
    	
    	try {
			n = Integer.parseInt(txtN.getText());
		} catch (NumberFormatException e) {
			txtResult.appendText("Formato N non corretto");
		}
    	
    	anno = boxAnno.getValue();
    	mese = boxMese.getValue();
    	giorno = boxGiorno.getValue();
    	
    	if(anno == null || mese == null || giorno == null) {
    		txtResult.appendText("Inserire tutti i campi");
    		return;
    	} 
    	
    	try {
			LocalDate.of(anno, mese, giorno); // Questo metodo se non ha èparametri corretti scatena un'eccezione
		} catch (DateTimeException e) {
			txtResult.appendText("Data non corretta");
		}
    	
    	txtResult.appendText("Simulo con " + n + " agenti");
    	txtResult.appendText("\nCRIMINI MAL GESTITI "  + model.simula(anno, mese, giorno, n));
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert boxAnno != null : "fx:id=\"boxAnno\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxGiorno != null : "fx:id=\"boxGiorno\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCreaReteCittadina != null : "fx:id=\"btnCreaReteCittadina\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnSimula != null : "fx:id=\"btnSimula\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtN != null : "fx:id=\"txtN\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    	
    	boxAnno.getItems().addAll(model.getAnni());
    	boxMese.getItems().addAll(model.getMesi());
    	boxGiorno.getItems().addAll(model.getGiorni());
    }
}
