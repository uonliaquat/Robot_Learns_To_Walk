package code.FileHandling;

import code.GeneticAlgorithm.Chromosome;
import code.GeneticAlgorithm.Population;
import javafx.scene.control.Alert;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class FileHandling {

    public static final String NETWORK_FOLDER ="/Users/uonliaquat/Desktop/IntelliJ_IDEA/NeuralNetowrkWithGeneticAlgo(SelfLearningRobot)/Networks";
    public static final String GENERATION_FOLDER ="/Users/uonliaquat/Desktop/IntelliJ_IDEA/NeuralNetowrkWithGeneticAlgo(SelfLearningRobot)/Generations";
    private static String SORRY= "Sorry!";
    private static String CONGRATULATIONS = "Congratulations!";
    private static String NETWORK_SAVED= "Network Saved Successfully!";
    private static String COULD_NOT_SAVE_NETWORK = "Couldn't save Network";
    private static String GENERATION_SAVED= "Generation Saved Successfully!";
    private static String COULD_NOT_SAVE_GENERATION = "Couldn't save Generation";

    public static void saveNetwork(String networkName, Chromosome chromosome) throws IOException {
        networkName = networkName + "_" + chromosome.getFitness();
        if (Files.exists(Paths.get(NETWORK_FOLDER))) {
            if (createSheet(networkName, chromosome, "Networks/")) {
                showDialog(NETWORK_SAVED, CONGRATULATIONS);
            } else {
                showDialog(COULD_NOT_SAVE_NETWORK, SORRY);
            }
        }
        else {
            File newFolder = new File(NETWORK_FOLDER);
            boolean created = newFolder.mkdir();
            if (created) {
                if (createSheet(networkName, chromosome, "Networks/")) {
                    showDialog(NETWORK_SAVED, CONGRATULATIONS);
                } else {
                    showDialog(COULD_NOT_SAVE_NETWORK, SORRY);
                }

            } else {
                showDialog(COULD_NOT_SAVE_NETWORK, SORRY);
            }
        }
    }

    public static void saveGeneration(int gNo, Population generation) throws IOException {


        if (Files.exists(Paths.get(GENERATION_FOLDER))) {
            String path = GENERATION_FOLDER + "/Generation" + gNo;
            File newFolder = new File(path);
            if(Files.exists(Paths.get(path))){
                deleteDirectory(newFolder);
            }

            boolean created = newFolder.mkdir();
            if (created) {
                int count = 0;
                for(int i = 0; i < generation.getChromosomes().length; i++) {
                    String networkName = "Network" + (i+1) + "_" + generation.getChromosomes()[i].getFitness();
                    createSheet(networkName, generation.getChromosomes()[i], "Generations/Generation" + gNo +"/");
                    count++;
                }
                if(count == 100){
                    showDialog(GENERATION_SAVED, CONGRATULATIONS);
                }
                else if(count > 0){
                    showDialog("Could only save " + count + " Networks", CONGRATULATIONS);
                }
                else{
                    showDialog(COULD_NOT_SAVE_GENERATION, SORRY);
                }

            } else {
                showDialog(COULD_NOT_SAVE_GENERATION, SORRY);
            }
        } else {
            File newFolder = new File(GENERATION_FOLDER);
            boolean created = newFolder.mkdir();
            if (created) {
                newFolder = new File(GENERATION_FOLDER + "/Generation"+  gNo);
                created = newFolder.mkdir();
                if (created) {
                    for(int i = 0; i < generation.getChromosomes().length; i++) {
                        String networkName = "Network" + (i+1) + "_" + generation.getChromosomes()[i].getFitness();
                        createSheet(networkName , generation.getChromosomes()[i],"Generations/Generation" + gNo +"/");
                    }
                    showDialog(GENERATION_SAVED, CONGRATULATIONS);

                } else {
                    showDialog(COULD_NOT_SAVE_NETWORK, SORRY);
                }
            } else {
                showDialog(COULD_NOT_SAVE_GENERATION, SORRY);
            }
        }
    }


    public static boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDirectory(children[i]);
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }



    private static boolean createSheet(String fileName, Chromosome chromosome, String path) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(fileName);
        Map<String, Object[]> data = new LinkedHashMap<>();
        int index = 0;
        for(int i = 1; i < chromosome.getChromosome().getLayers().size(); i++){
            for(int j = 0; j < chromosome.getChromosome().getLayers().get(i).getNoOfNeurons(); j++){
                Object object[] = new Object[chromosome.getChromosome().getLayers().get(i).getNeurons().get(j).getWeights().size()];
                for(int k = 0; k < chromosome.getChromosome().getLayers().get(i).getNeurons().get(j).getWeights().size(); k++){
                    double weight = chromosome.getChromosome().getLayers().get(i).getNeurons().get(j).getWeights().get(k);
                    object[k] = weight;
                }
                data.put(index+"", object);
                index++;
            }
            Object object[] = new Object[1];
            object[0] = "//";
            data.put(index +"", object);
            index++;
        }


        Set<String> keyset = data.keySet();
        int rownum = 0;
        for (String key : keyset) {
            Row ro = sheet.createRow(rownum++);
            Object[] objArr = data.get(key);
            int cellnum = 0;
            for (Object obj : objArr) {
                Cell cell = ro.createCell(cellnum++);
                cell.setCellValue(obj.toString());
            }
        }
        try {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File(path + fileName + ".xlsx"));
            workbook.write(out);
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
    private static void showDialog(String output, String status){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Status");
        alert.setHeaderText(status);
        alert.setContentText(output);

        alert.showAndWait();
    }

    public static List<String> readNetworks(){
        File folder = new File(NETWORK_FOLDER);
        File[] listOfFiles = folder.listFiles();
        if(listOfFiles != null) {
            List<String> networkNames = new ArrayList<>();
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].getName().contains(".xlsx")) {
                    networkNames.add(listOfFiles[i].getName());
                }
            }
            return networkNames;
        }
        return null;
    }

    public static Map<String, List<String>> readGenerations(){
        File folder = new File(GENERATION_FOLDER);
        File[] listOfFolders = folder.listFiles();
        Map<String, List<String>> map = new HashMap<>();
        for(int i = 0; i < listOfFolders.length; i++){

            File subFolder = new File(listOfFolders[i].getPath());
            if(!subFolder.getName().contains(".DS_Store")) {
                File[] listOfFiles = subFolder.listFiles();
                List<String> networksNames = new ArrayList<>();
                for (int j = 0; j < listOfFiles.length; j++) {
                    networksNames.add(listOfFiles[j].getName());
                }
                map.put(subFolder.getName(), networksNames);
            }
        }
        return map;
    }
}
