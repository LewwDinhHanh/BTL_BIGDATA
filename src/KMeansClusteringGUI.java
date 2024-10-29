import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;

public class KMeansClusteringGUI extends JFrame {
    private JButton loadCentroidsButton;
    private JButton loadDataButton;
    private JButton runClusteringButton;
    private JTextArea resultArea;
    private List<double[]> centroids;
    private List<double[]> dataPoints;

    public KMeansClusteringGUI() {
        setTitle("K-Means Clustering");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        loadCentroidsButton = new JButton("Load Centroids");
        loadDataButton = new JButton("Load Data Points");
        runClusteringButton = new JButton("Run Clustering");
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        JPanel panel = new JPanel();
        panel.add(loadCentroidsButton);
        panel.add(loadDataButton);
        panel.add(runClusteringButton);

        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadCentroidsButton.addActionListener(new LoadCentroidsAction());
        loadDataButton.addActionListener(new LoadDataPointsAction());
        runClusteringButton.addActionListener(new RunClusteringAction());
    }

    private class LoadCentroidsAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    centroids = readCentroids(file.getAbsolutePath());
                    resultArea.append("Loaded centroids from " + file.getName() + "\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    resultArea.append("Error loading centroids\n");
                }
            }
        }
    }

    private class LoadDataPointsAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    dataPoints = readDataPoints(file.getAbsolutePath());
                    resultArea.append("Loaded data points from " + file.getName() + "\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    resultArea.append("Error loading data points\n");
                }
            }
        }
    }

    private class RunClusteringAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (centroids == null || dataPoints == null) {
                resultArea.append("Please load both centroids and data points first.\n");
                return;
            }

            // Map lưu trữ các điểm dữ liệu cho từng cụm
            Map<Integer, List<double[]>> clusters = new HashMap<>();
            for (int i = 0; i < centroids.size(); i++) {
                clusters.put(i, new ArrayList<>());
            }

            // Phân cụm cho từng điểm
            for (double[] point : dataPoints) {
                int closestCentroidIndex = findClosestCentroid(point, centroids);
                clusters.get(closestCentroidIndex).add(point);
            }

            // Xuất kết quả phân cụm
            resultArea.setText("");
            for (Map.Entry<Integer, List<double[]>> entry : clusters.entrySet()) {
                int clusterIndex = entry.getKey();
                List<double[]> pointsInCluster = entry.getValue();
                
                resultArea.append("\n\nTotal points in Cluster " + clusterIndex + ": " + pointsInCluster.size());
                resultArea.append("\nCluster " + clusterIndex + " contains points:\n");
                for (double[] point : pointsInCluster) {
                    resultArea.append(Arrays.toString(point) + "\n");
                }
                
            }
        }
    }

    private List<double[]> readCentroids(String filePath) throws IOException {
        List<double[]> centroids = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.replace("(", "").replace(")", "").split(",");
                double[] centroid = new double[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    centroid[i] = Double.parseDouble(parts[i].trim());
                }
                centroids.add(centroid);
            }
        }
        return centroids;
    }

    private List<double[]> readDataPoints(String filePath) throws IOException {
        List<double[]> dataPoints = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.replace("(", "").replace(")", "").split(",");
                double[] point = new double[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    point[i] = Double.parseDouble(parts[i].trim());
                }
                dataPoints.add(point);
            }
        }
        return dataPoints;
    }

    private int findClosestCentroid(double[] point, List<double[]> centroids) {
        int closestIndex = -1;
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < centroids.size(); i++) {
            double[] centroid = centroids.get(i);
            double distance = 0.0;
            for (int j = 0; j < point.length; j++) {
                distance += Math.pow(point[j] - centroid[j], 2);
            }
            distance = Math.sqrt(distance);

            if (distance < minDistance) {
                minDistance = distance;
                closestIndex = i;
            }
        }

        return closestIndex;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            KMeansClusteringGUI gui = new KMeansClusteringGUI();
            gui.setVisible(true);
        });
    }
}
