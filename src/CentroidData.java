
public class CentroidData {
	public PointWritable[] centroids;
	public int[] pointsInCentroids;

    public CentroidData(PointWritable[] centroids, int[] pointsInCentroids) {
        this.centroids = centroids;
        this.pointsInCentroids = pointsInCentroids;
    }
}
