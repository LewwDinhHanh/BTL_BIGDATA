import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class KReducer extends Reducer<LongWritable, PointWritable, Text, Text> {

    private final Text newCentroidId = new Text();
    private final Text newCentroidValue = new Text();

    public void reduce(LongWritable centroidId, Iterable<PointWritable> partialSums, Context context)
            throws IOException, InterruptedException {

        int totalPoint = 0; // Khởi tạo biến để lưu số điểm
        PointWritable ptFinalSum = new PointWritable(); // Khởi tạo biến để lưu tổng số điểm

        // Sử dụng Iterator để duyệt qua các điểm
        Iterator<PointWritable> iterator = partialSums.iterator();

        // Lặp qua từng điểm
        while (iterator.hasNext()) {
            PointWritable point = iterator.next();
            if (totalPoint == 0) {
                ptFinalSum = PointWritable.copy(point); // Lưu điểm đầu tiên vào ptFinalSum
            } else {
                ptFinalSum.sum(point);  // Cộng các điểm lại
            }
            totalPoint++; // Tăng số lượng điểm
        }

        // Tính trung bình
        ptFinalSum.calcAverage();

        // Tạo thông tin kết quả
        newCentroidId.set(centroidId.toString());
        String outputValue = ptFinalSum.toString() + " | Total Points: " + totalPoint;
        newCentroidValue.set(outputValue); // Kết hợp giá trị trung bình và số điểm

        // Ghi kết quả vào context
        context.write(newCentroidId, newCentroidValue);
    }
}
