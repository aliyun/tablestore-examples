package com.aliyun.tablestore.grid.utils;

import com.aliyun.tablestore.grid.model.grid.Grid2D;
import com.aliyun.tablestore.grid.model.grid.Plane;
import com.aliyun.tablestore.grid.model.grid.Point;
import com.aliyun.tablestore.grid.model.grid.Range;
import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class BlockUtil {

    public static List<Grid2D> splitGrid2DToBlocks(Grid2D grid2D, int xSplitCount, int ySplitCount) throws InvalidRangeException {
        Array array = grid2D.toArray();
        int blockXSize = (grid2D.getPlane().getxRange().getSize() - 1) / xSplitCount + 1;
        int blockYSize = (grid2D.getPlane().getyRange().getSize() - 1) / ySplitCount + 1;
        List<Grid2D> result = new ArrayList<Grid2D>();
        for (int i = 0; i < xSplitCount; i++) {
            int startX = i * blockXSize;
            int endX = Math.min(grid2D.getPlane().getxRange().getSize(), startX + blockXSize);
            if (startX >= grid2D.getPlane().getxRange().getSize()) {
                break;
            }
            for (int j = 0; j < ySplitCount; j++) {
                int startY = j * blockYSize;
                int endY = Math.min(grid2D.getPlane().getyRange().getSize(), startY + blockYSize);
                if (startY >= grid2D.getPlane().getyRange().getSize()) {
                    break;
                }
                int[] origin = new int[] { startX, startY };
                int[] shape = new int[] { endX - startX, endY - startY };
                Array section = array.section(origin, shape);
                Grid2D block = new Grid2D(section.getDataAsByteBuffer(), grid2D.getDataType(), origin, shape);
                result.add(block);
            }
        }
        return result;
    }

    public static List<Point> calcBlockPointsCanCoverSubPlane(Plane plane, Plane subPlane, int xSplitCount, int ySplitCount) {
        int blockXSize = (plane.getxRange().getSize() - 1) / xSplitCount + 1;
        int blockYSize = (plane.getyRange().getSize() - 1) / ySplitCount + 1;

        Range xBlockIndexRange = new Range(
                subPlane.getxRange().getStart() / blockXSize,
                (subPlane.getxRange().getEnd() - 1) / blockXSize + 1);
        Range yBlockIndexRange = new Range(
                subPlane.getyRange().getStart() / blockYSize,
                (subPlane.getyRange().getEnd() - 1) / blockYSize + 1);

        List<Point> points = new ArrayList<Point>();
        for (int xIdx = xBlockIndexRange.getStart(); xIdx < xBlockIndexRange.getEnd(); xIdx++) {
            for (int yIdx = yBlockIndexRange.getStart(); yIdx < yBlockIndexRange.getEnd(); yIdx++) {
                Point point = new Point(xIdx * blockXSize, yIdx * blockYSize);
                points.add(point);
            }
        }
        return points;
    }

    public static Grid2D buildGrid2DFromBlocks(Plane plane, DataType dataType, List<Grid2D> blocks, byte[] buffer, int pos) {
        int size = plane.getxRange().getSize() * plane.getyRange().getSize() * dataType.getSize();
        if (buffer.length - pos < size) {
            throw new IllegalArgumentException("buffer not enough");
        }
        int count = 0;
        for (Grid2D block : blocks) {
            Plane blockPlane = block.getPlane();
            for (int x = Math.max(blockPlane.getxRange().getStart(), plane.getxRange().getStart());
                    x < Math.min(blockPlane.getxRange().getEnd(), plane.getxRange().getEnd()); x++) {
                for (int y = Math.max(blockPlane.getyRange().getStart(), plane.getyRange().getStart());
                        y < Math.min(blockPlane.getyRange().getEnd(), plane.getyRange().getEnd()); y++) {
                    int posInBlock = dataType.getSize() * ((x - blockPlane.getxRange().getStart()) * (blockPlane.getyRange().getSize()) + (y - blockPlane.getyRange().getStart())) ;
                    int posInData = dataType.getSize() * ((x - plane.getxRange().getStart()) * plane.getyRange().getSize() + (y - plane.getyRange().getStart()));
                    System.arraycopy(block.getDataAsByteArray(), posInBlock, buffer, pos + posInData, dataType.getSize());
                    count += dataType.getSize();
                }
            }
        }
        if (count != size) {
            throw new RuntimeException("the blocks does not contain enough data");
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, pos, size);
        return new Grid2D(byteBuffer, dataType, plane.getOrigin(), plane.getShape());
    }
}
