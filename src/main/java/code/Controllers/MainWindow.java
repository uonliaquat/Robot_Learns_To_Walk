package code.Controllers;

import code.Communication.Bluetooth;
import code.Constants;
import code.FileHandling.FileHandling;
import code.GeneticAlgorithm.Chromosome;
import code.GeneticAlgorithm.GeneticAlgorithm;
import code.GeneticAlgorithm.Population;
import code.NeuralNetwork.NeuralNetwork;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

public class MainWindow implements Initializable {

    @FXML
    private AnchorPane parentNode;

    @FXML
    private HBox hBox;

    @FXML
    private Button generateBtn, nextBtn, saveBtn, loadBtn, previousBtn, testBtn,stopTestBtn,tuneBtn,fitnessBtn;

    @FXML
    private Text networkNoText, generationNoText, loadedText, bluetoothText;

    @FXML
    private Line joint1, joint2, joint3, joint4;

    @FXML
    private TextField fitnessField;

    public static MainWindow mainWindow;

    private static int NEURON_RADIUS = 20;
    private static int VBOX_SPACING = 20;
    private static int HBOX_SPACING = 200;

    private GeneticAlgorithm ga;
    private int generationNo, networkNo;
    private List<VBox> layers;
    private List<List<Vector<Line>>> weights;
    private Vector<Double> input;
    private Chromosome chromosome;
    private Bluetooth bluetooth;
    private boolean isTesting;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainWindow = this;
        isTesting = false;
        input = new Vector<>();
        for(int i = 0; i < Constants.IL_SIZE; i++){
            input.add(0d);
        }

        layers = new ArrayList<>();
        weights = new ArrayList<>();

        Vector<Integer> topology = new Vector<>();
        topology.add(Constants.IL_SIZE);
        topology.add(Constants.HL_SIZE);
        topology.add(Constants.OL_SIZE);
        initializeNeuralNetwork(new NeuralNetwork(topology));

        bluetooth = new Bluetooth();
        if(bluetooth.Connect()){
            bluetoothText.setText("Bluetooth Connected");
            bluetoothText.setFill(Color.BLUE);
        }
        else{
            tuneBtn.setDisable(true);
        }

    }

    @FXML
    private void processButtons(ActionEvent actionEvent) throws IOException {
        if(actionEvent.getSource().equals(generateBtn)){
            nextBtn.setDisable(false);
            previousBtn.setDisable(false);
            fitnessBtn.setDisable(false);
            saveBtn.setDisable(false);
            loadedText.setVisible(false);
            networkNo = 0;
            if(ga == null){
                generationNo = 0;
                ga = new GeneticAlgorithm();
            }
            else{
                ga.produceNextGen();
                generationNo++;
            }
            chromosome = ga.getPopulation().getChromosomes()[networkNo];
            drawNeuralNetwork(chromosome.getChromosome());
            setJointAngles(chromosome.getChromosome());
            networkNoText.setText((networkNo + 1) + "/" + Constants.POPULATION_SIZE);
            generationNoText.setText((generationNo + 1) + "");
        }
        else if(actionEvent.getSource().equals(nextBtn)){
            if(ga != null && networkNo != Constants.POPULATION_SIZE - 1) {
                networkNo++;
                chromosome = ga.getPopulation().getChromosomes()[networkNo];
                drawNeuralNetwork(chromosome.getChromosome());
                setJointAngles(chromosome.getChromosome());
                networkNoText.setText((networkNo + 1) + "/" + Constants.POPULATION_SIZE);
            }
            else if(networkNo == Constants.POPULATION_SIZE - 1){
                errorDialog("You only have " + Constants.POPULATION_SIZE + " chromosomes", "");
            }
            else if(ga == null){
                errorDialog("You don't have any generation yet!", "Create a generation first!");
            }
        }
        else if(actionEvent.getSource().equals(previousBtn)){
            if(ga != null && networkNo > 0) {
                networkNo--;
                chromosome = ga.getPopulation().getChromosomes()[networkNo];
                drawNeuralNetwork(chromosome.getChromosome());
                setJointAngles(chromosome.getChromosome());
                networkNoText.setText((networkNo + 1) + "/" + Constants.POPULATION_SIZE);
            }
            else if(ga == null){
                errorDialog("You don't have any generation yet!", "Create a generation first!");
            }
        }
        else if(actionEvent.getSource().equals(saveBtn)){
            if(ga != null) {
                saveDialog();
            }
            else{
                errorDialog("You don't have any generation yet!", "Create a generation to save!");
            }
        }
        else if(actionEvent.getSource().equals(loadBtn)){
            Parent root = FXMLLoader.load(getClass().getResource("/LoadNetworkDialog.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Load your Generation or Network");
            stage.setScene(scene);
            stage.show();
        }
        else if(actionEvent.getSource().equals(fitnessBtn)){
            int fitness = Integer.parseInt(fitnessField.getText());
            chromosome.setFitness(fitness);
        }
        else if(actionEvent.getSource().equals(tuneBtn)){
            bluetooth.sendCommand(500);
        }
        else if(actionEvent.getSource().equals(testBtn)) {
            isTesting = true;
            Vector<Double> output = chromosome.getChromosome().getOutput();
            Vector<Integer> angles = scaleOutput(output);
            final int[] index = {0};
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = index[0]; i < index[0] + 4; i++) {
                        bluetooth.sendCommand(angles.get(i));
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    index[0] = index[0] + 4;
                    if (index[0] == 12) {
                        index[0] = 0;
                    }
                }
            });
            thread.start();
        }
        else if(actionEvent.getSource().equals(stopTestBtn)){
            isTesting = false;
        }
    }

    public void drawNeuralNetwork(NeuralNetwork neuralNetwork){
        neuralNetwork.feedForward(input);
        drawNeurons(neuralNetwork);
        drawWeights(neuralNetwork);
    }

    private void drawNeurons(NeuralNetwork neuralNetwork){
        for(int i = 0; i < neuralNetwork.getLayers().size(); i++){
            VBox vBox = layers.get(i);
            for(int j = 0; j < vBox.getChildren().size(); j++){
                StackPane stackPane = (StackPane) vBox.getChildren().get(j);
                Circle neuron = (Circle) stackPane.getChildren().get(0);
                Text text = (Text) stackPane.getChildren().get(1);
                int shade = (int)( Math.pow(neuralNetwork.getLayers().get(i).getNeurons().get(j).getOutput(),2) * 255);
                neuron.setFill(Color.rgb(90, shade, 50, .50));
                DecimalFormat df2 = new DecimalFormat("#.##");
                text.setText( df2.format(neuralNetwork.getLayers().get(i).getNeurons().get(j).getOutput()) + "");

            }
        }
    }

    private void drawWeights(NeuralNetwork neuralNetwork){
        for(int i = 0; i < neuralNetwork.getLayers().size() - 1; i++){
            List<Vector<Line>> list = weights.get(i);
            for(int j = 0; j < list.size(); j++){
                Vector<Line> vector = list.get(j);
                for(int k = 0; k < vector.size(); k++){
                    Line line = vector.get(k);
                    Double weight = neuralNetwork.getLayers().get(i+1).getNeurons().get(j).getWeights().get(k);
                    int shade = (int)(Math.pow(weight,2)* 255);
                    line.setStroke(Color.rgb(shade, 150 , 50, 1));
                    line.setStrokeWidth(weight*2);
                }
            }
        }
    }


    private void initializeNeuralNetwork(NeuralNetwork neuralNetwork){
        hBox.setSpacing(HBOX_SPACING);
        initializeNeurons(neuralNetwork);
        initializeWeights(neuralNetwork);
    }

    private void initializeNeurons(NeuralNetwork neuralNetwork){

        for(int i = 0; i < neuralNetwork.getLayers().size(); i++){
            VBox vBox = new VBox();
            vBox.setAlignment(Pos.CENTER);
            vBox.setSpacing(VBOX_SPACING);
            for(int j = 0; j < neuralNetwork.getLayers().get(i).getNoOfNeurons(); j++){
                Circle neuron = new Circle(0,0,NEURON_RADIUS);
                int shade = (int) (Math.pow(neuralNetwork.getLayers().get(i).getNeurons().get(j).getOutput(),2) * 255);
                neuron.setFill(Color.rgb(90, shade, 50, .50));
                Text text = new Text("" + neuralNetwork.getLayers().get(i).getNeurons().get(j).getOutput());
                StackPane stackPane = new StackPane(neuron, text);
                vBox.getChildren().add(stackPane);
            }
            hBox.getChildren().add(vBox);
            layers.add(vBox);
        }
    }

    private void initializeWeights(NeuralNetwork neuralNetwork){
        int sx = (int) hBox.getLayoutX() + NEURON_RADIUS * 2 + HBOX_SPACING, sy = 245, ex =(int) hBox.getLayoutX() + NEURON_RADIUS * 2, ey  = 360;
        int temp_ey = ey;
        for(int i = 0; i < neuralNetwork.getLayers().size() - 1; i++){
            List<Vector<Line>> layer = new ArrayList<>();
            for(int j = 0; j < neuralNetwork.getLayers().get(i + 1).getNoOfNeurons(); j++){
                Vector<Line> weight = new Vector<>();
                for(int k = 0; k < neuralNetwork.getLayers().get(i).getNoOfNeurons(); k++){
                    Line line = new Line(sx,sy,ex,ey);
                    List<List<Vector<Double>>>  weights  =neuralNetwork.getWeights();
                    double w = weights.get(i + 1).get(j).get(k);
                    int shade = (int)(Math.pow(w,2)* 255);
                    line.setStroke(Color.rgb(shade, 150 , 50, 1));
                    line.setStrokeWidth(w*2);
                    parentNode.getChildren().add(line);
                    ey = ey + VBOX_SPACING + NEURON_RADIUS * 2;
                    weight.add(line);
                }
                layer.add(weight);
                sy = sy + VBOX_SPACING + NEURON_RADIUS * 2;
                ey = temp_ey;
            }
            weights.add(layer);
            sx = (int) (sx * 1.945);
            sy = sy/6;
            ex = (int)(ex * 5.27);
            ey = (int)(ey/1.5);
            temp_ey = ey;
        }
    }

    private void setJointAngles(NeuralNetwork neuralNetwork){
        int layersSize =  neuralNetwork.getLayers().size();
        Vector<Double> outputAngles = neuralNetwork.getLayers().get(layersSize - 1).getOutput();
        double[] angles = new double[outputAngles.size()];
        for(int i = 0; i < outputAngles.size(); i++){
            angles[i] = outputAngles.get(i) * 100;
            angles[i] = angles[i] - 50;
        }

        joint1.setRotate(angles[0]);
        joint2.setRotate(angles[1]);
        joint3.setRotate(angles[2]);
        joint4.setRotate(angles[3]);
    }

    private void errorDialog(String header, String content) throws IOException {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    private void saveDialog() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save");
        alert.setHeaderText("Choose your option");

        ButtonType saveNetwork = new ButtonType("Save Network");
        ButtonType saveGeneration  = new ButtonType("Save Generation");

        alert.getButtonTypes().setAll(saveNetwork, saveGeneration);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == saveNetwork) {
            //save Network
            Chromosome chromosome = ga.getPopulation().getChromosomes()[networkNo];
            FileHandling.saveNetwork("Network_" + (generationNo + 1) + "_" + networkNo, chromosome);
            alert.close();
        } else if (result.get() == saveGeneration) {
            //save Generation
            Population generation = ga.getPopulation();
            FileHandling.saveGeneration(generationNo,generation);
            alert.close();
        }
    }

    public void setLoadedText(String loadedText) {
        this.loadedText.setText(loadedText);
        this.loadedText.setVisible(true);
        nextBtn.setDisable(true);
        previousBtn.setDisable(true);
        fitnessBtn.setDisable(true);
        saveBtn.setDisable(true);
    }

    public void setPopulation(Chromosome[] chromosomes, int gNo) throws Exception {
        generationNo = gNo;
        ga = new GeneticAlgorithm(chromosomes);
        networkNo = 0;
        chromosome = ga.getPopulation().getChromosomes()[networkNo];
        drawNeuralNetwork(chromosome.getChromosome());
        generationNoText.setText(gNo + "");
        networkNoText.setText((networkNo+1) + "");
        loadedText.setText("Generation Loaded From File");
        loadedText.setVisible(true);
    }

    private Vector<Integer> scaleOutput(Vector<Double> values){
        Vector<Integer> angles = new Vector<>();
        for(int i = 0; i < values.size(); i++){
            int r = (int) (values.get(i) * 100);
            //r = r - 50;
            angles.add(r);
        }
        return angles;
    }
}
