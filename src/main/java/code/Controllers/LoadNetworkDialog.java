package code.Controllers;

import code.Constants;
import code.FileHandling.FileHandling;
import code.GeneticAlgorithm.Chromosome;
import code.GeneticAlgorithm.Population;
import code.NeuralNetwork.NeuralNetwork;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.*;

public class LoadNetworkDialog implements Initializable {
    private NeuralNetwork neuralNetwork;
    private Population population;
    @FXML
    private TreeView treeView;

    private String NETWORK_PATH = FileHandling.NETWORK_FOLDER;
    private String GENERATION_PATH = FileHandling.GENERATION_FOLDER;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showFileNames();
        treeView.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent mouseEvent)
            {
                if(mouseEvent.getClickCount() == 2)
                {
                    TreeItem<String> item = (TreeItem<String>) treeView.getSelectionModel().getSelectedItem();
                    TreeItem<String> parent = item.getParent();

                    //Load Data
                    if(parent.getValue().contains("Generation") && item.getValue().contains("Network")){
                        List<List<Vector<Double>>>  weights = loadNetwork(GENERATION_PATH + "/" + parent.getValue() + "/"+ item.getValue());
                        neuralNetwork = new NeuralNetwork(weights, 0);
                        MainWindow.mainWindow.drawNeuralNetwork(neuralNetwork);
                        MainWindow.mainWindow.setLoadedText("Network Loaded from file");
                    }
                    else if(parent.getValue().equals("Generations") && item.getValue().contains("Generation")){
                        try {
                            loadGeneration(GENERATION_PATH + "/" + item.getValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        List<List<Vector<Double>>>  weights = loadNetwork(NETWORK_PATH + "/" + item.getValue());
                        neuralNetwork = new NeuralNetwork(weights, 0);
                        MainWindow.mainWindow.drawNeuralNetwork(neuralNetwork);
                        MainWindow.mainWindow.setLoadedText("Network Loaded from file");
                    }

                    Stage stage = (Stage) treeView.getScene().getWindow();
                    stage.close();

                }
            }
        });
    }


    private void loadGeneration(String path) throws Exception {
        String gNo_str = "";
        for(int i = path.length() - 1; i >= 0; i--){
            if(path.charAt(i) == 'n'){
                break;
            }
            gNo_str = path.charAt(i) + gNo_str;
        }
        int gNo = Integer.parseInt(gNo_str);

        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        Arrays.sort(listOfFiles);
        int index = 0;
        Chromosome[] chromosomes = new Chromosome[Constants.POPULATION_SIZE];
        List<NeuralNetwork> neuralNetworks = new ArrayList<>();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                List<List<Vector<Double>>> weights = loadNetwork(path + "/" + file.getName());
                neuralNetwork = new NeuralNetwork(weights, 0);
                chromosomes[index] = new Chromosome(neuralNetwork, getFitnessValue(file.getName()));
                index++;
            }
        }

        MainWindow.mainWindow.setPopulation(chromosomes, gNo);

    }

    private  List<List<Vector<Double>>> loadNetwork(String path){
        List<List<Vector<Double>>> weights = new ArrayList<>();
        Vector<Vector<Double>> layer = new Vector<>();
        Vector<Double> w = new Vector<>();
        for(int i = 0; i < Constants.IL_SIZE; i++){
            w.add(0d);
            layer.add(w);
        }
        weights.add(layer);
        layer = new Vector<>();
        try {
            FileInputStream file = new FileInputStream(new File(path));

            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);

            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);

            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext())
            {
                Row row = rowIterator.next();
                //For each row, iterate through all the columns
                Iterator<Cell> cellIterator = row.cellIterator();

                w = new Vector<>();
                while (cellIterator.hasNext())
                {
                    Cell cell = cellIterator.next();
                    if(cell.getStringCellValue().equals("//")){
                        weights.add(layer);
                        layer = new Vector<>();
                        break;
                    }
                    w.add(Double.parseDouble(cell.getStringCellValue()));
                }
                if(w.size() > 0){
                    layer.add(w);
                }
            }
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return weights;
    }

    private void showFileNames() {
        TreeItem<String> root = new TreeItem<>("Root");

        TreeItem<String> networks = new TreeItem<>("Networks");
        TreeItem<String> generations = new TreeItem<>("Generations");

        List<String> networksNames = FileHandling.readNetworks();
        if(networksNames != null) {
            for (int i = 0; i < networksNames.size(); i++) {
                TreeItem<String> item = new TreeItem<>(networksNames.get(i));
                networks.getChildren().add(item);
            }
            root.getChildren().add(networks);
        }
        root.getChildren().add(generations);
        treeView.setRoot(root);

        Map<String, List<String>> generationsData = FileHandling.readGenerations();
        for (Map.Entry<String, List<String>> entry : generationsData.entrySet()) {
            TreeItem<String> gen = new TreeItem<>(entry.getKey());
            List<String> net = entry.getValue();
            for (int i = 0; i < net.size(); i++) {
                TreeItem<String> item = new TreeItem<>(net.get(i));
                gen.getChildren().add(item);
            }
            generations.getChildren().add(gen);
        }
    }

    private int getFitnessValue(String str){
        String result = "";
        int i = str.length() - 6;
        while(str.charAt(i) != '_'){
            result = str.charAt(i) + result;
            i--;
        }
        return Integer.parseInt(result);
    }
}
