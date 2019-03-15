package com.example.Project;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.file.Paths;

public class Main {

    private static final String DATA_FILE_NAME1 = "E:\\Varsha\\IntelliJ Projects\\DMTMProject\\src\\com\\example\\Project\\small-data-1\\data-1.txt";
    private static final String PARA_FILE_NAME1= "E:\\Varsha\\IntelliJ Projects\\DMTMProject\\src\\com\\example\\Project\\small-data-1\\para1-1.txt";
    private static final String PARA_FILE_NAME12 = "E:\\Varsha\\IntelliJ Projects\\DMTMProject\\src\\com\\example\\Project\\small-data-1\\para1-2.txt";

    private static final String DATA_FILE_NAME2 = "E:\\Varsha\\IntelliJ Projects\\DMTMProject\\src\\com\\example\\Project\\large-data-2\\data2.txt";
    private static final String PARA_FILE_NAME21 = "E:\\Varsha\\IntelliJ Projects\\DMTMProject\\src\\com\\example\\Project\\large-data-2\\para2-1.txt";
    private static final String PARA_FILE_NAME22 = "E:\\Varsha\\IntelliJ Projects\\DMTMProject\\src\\com\\example\\Project\\large-data-2\\para2-2.txt";
    private static final String SDC = "SDC";
    public static void main(String[] args) throws  Exception {
        List<Sequence> dataSequence = readData(DATA_FILE_NAME2);
        Map<Integer, Double> misMap = readParameter(PARA_FILE_NAME21);
        Double sdc = misMap.remove(-1);
        PrintWriter writer = new PrintWriter("E:\\Varsha\\IntelliJ Projects\\DMTMProject\\src\\com\\example\\Project\\ramanathan_krishnamoorthy_varsha_jayaraman_large_para2.txt", "UTF-8");
        MS_GSP ms_gsp = new MS_GSP(misMap, sdc, dataSequence);
        Map<Integer, List<Sequence>> kLengthSeqList =  ms_gsp.execute();
        for (Map.Entry<Integer, List<Sequence>> entry : kLengthSeqList.entrySet()) {
            writer.println(" Number of Length " + entry.getKey() + " Frequency Sequences: " + entry.getValue().size());
            System.out.println(" Number of Length " + entry.getKey() + " Frequency Sequences: " + entry.getValue().size());
            for (Sequence se : entry.getValue()) {
                writer.println(se.toString());
                System.out.println(se.toString());
            }
        }
    //    PrintWriter writer = new PrintWriter("E:\\Varsha\\IntelliJ Projects\\DMTMProject\\src\\com\\example\\Project\\the-file-name.txt", "UTF-8");
    //    writer.println("The first line");
    //    writer.println("The second line");
        writer.close();
    }

    //Parsing parameter file
    private static Map<Integer, Double> readParameter(String fileName)  throws Exception {
        final String SPLIT_CONSTANT = " = ";
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        String inputStr = null;
        Map<Integer, Double> parameterMap = new HashMap<>();
        while ((inputStr = br.readLine()) != null) {
            if(inputStr.contains(SDC)) {
                parameterMap.put(-1, getDoubleValue(inputStr.split(SPLIT_CONSTANT)[1]));
                break;
            }
            parameterMap.put(getItemValue(inputStr.split(SPLIT_CONSTANT)[0]),
                    getDoubleValue(inputStr.split(SPLIT_CONSTANT)[1]));
        }
        return parameterMap;
    }

    private static Double getDoubleValue(String misStr) {
        return Double.parseDouble(misStr);
    }

    private static Integer getItemValue(String itemStr) {
        return Integer.parseInt(itemStr.replace("MIS(", "").replace(")", ""));
    }

    //Parsing Input Value
    private static List<Sequence> readData(String fileName) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        String inputStr = null;
        List<Sequence> sequenceList = new ArrayList<>();
        while((inputStr = br.readLine()) != null) {
            Sequence s = new Sequence();
            List<List<Integer>> elementList = new ArrayList<>();
        //    System.out.println(inputStr+" "+(inputStr.length()-2));
            String elementStr[]= inputStr.substring(2, inputStr.length() -2).split("}\\{");
            for(String element : elementStr) {
                List<Integer> itemSet = new ArrayList<>();
                String itemStrArray[] = element.split(", ");
                for (String itemStr : itemStrArray) {
                    itemSet.add(Integer.parseInt(itemStr));
                }
                elementList.add(itemSet);
            }
            s.setElements(elementList);
            sequenceList.add(s);
        }
        return sequenceList;
    }
}





