package railway_connectivity;

import java.io.File;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Vai ao diretório atual procurar .csv
        String csvDir = (args.length > 0 ? args[0] : ".");
        File cwd = new File(csvDir);
        System.out.println("Trying to find .csv files in: " + cwd.getAbsolutePath());

        // Verifica se existe e é diretório
        if (!cwd.exists() || !cwd.isDirectory()) {
            System.err.println("Directory do not exist or isn't a valid directory: " + cwd.getAbsolutePath());
            System.exit(1);
        }

        // Lista todos os ficheiros .csv
        File[] csvFiles = cwd.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
        if (csvFiles == null || csvFiles.length < 2) {
            System.err.println("There needs to be at least 2 .csv here (1 for stations,1 for lines): " + cwd.getAbsolutePath());
            System.exit(1);
        }

        System.out.println("CSV Files Found:");
        for (File f : csvFiles) {
            System.out.println(" - " + f.getName());
        }

        // Pede o nome dos ficheiros ao utilizador
        String stationsFile = askFileName(sc, csvFiles, "Enter station CSV file name: ");
        String linesFile = askFileName(sc, csvFiles, "Enter lines CSV file name: ");

        File stations = new File(cwd, stationsFile);
        File lines = new File(cwd, linesFile);
        if (!stations.exists() || !lines.exists()) {
            System.err.println("Um dos ficheiros selecionados não existe. Saindo...");
            System.exit(1);
        }

        while (true) {
            System.out.println("\n=== Menu ===");
            System.out.println("1. Verify if a train can travel between two stations (US13)");
            System.out.println("2. Verify if a route that passes once, and only once, by each railway line exists (US14)");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            String op = sc.nextLine();

            if (op.equals("1")) {
                System.out.println("Implementar aqui US13");
                break;
            } else if (op.equals("2")) {
                System.out.println("Implementar aqui US14");
                break;
            } else if (op.equals("3")) {
                System.out.println("Closing...");
                break;
            } else {
                System.out.println("Opção inválida. Por favor tente novamente.");
            }
        }
        sc.close();
    }

    // Pede ao user o nome de um ficheiro .csv dentro do diretório especificado.
    private static String askFileName(Scanner sc, File[] files, String question) {
        System.out.print(question);
        String name;
        while (true) {
            name = sc.nextLine();
            for (File f : files) {
                if (f.getName().equals(name)) {
                    return name;
                }
            }
            System.out.println("File not found. Please enter one of the listed names.");
            System.out.print(question);
        }
    }
}