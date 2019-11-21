package com.simulation.pedestrian.cell;

import com.badlogic.gdx.math.Vector2;
import com.simulation.pedestrian.util.Tuple;

import java.util.ArrayList;

/**
 * This class manages multiple Cell classes for the representation of simulation areas and obstacle areas.
 */
public class CellsMap {
    /** cells in scale */
    private ArrayList<Cell> cellList;

    /** matrix of <@code cellList> */
    private ArrayList<ArrayList<Cell>> matrixCells;

    /** simulation scale */
    private Vector2 scale;

    /** the interval of this cell */
    private int cellInterval;

    /**
     * Instantiation requires simulation scale and cell class interval arguments
     *
     * @param scale        simulation scale
     * @param cellInterval cell-interval
     */
    public CellsMap(Vector2 scale, int cellInterval) {
        this.cellList = new ArrayList<>();
        this.matrixCells = new ArrayList<>();
        this.scale = scale;
        this.cellInterval = cellInterval;
        newCells();
    }

    /**
     * instantiate a scale cell class
     */
    private void newCells() {
        for (int scaleX = 0; scaleX <= scale.x; scaleX += cellInterval) {
            ArrayList<Cell> array = new ArrayList<>();
            for (int scaleY = 0; scaleY <= scale.y; scaleY += cellInterval) {
                Cell cell = new Cell(new Vector2(scaleX, scaleY), cellInterval);
                cellList.add(cell);
                array.add(cell);
            }
            matrixCells.add(array);
        }
    }

    /**
     * Returns the cell at the corresponding position
     *
     * @param x target x-position
     * @param y target y-position
     * @return target position Cell
     */
    public Cell getCell(float x, float y) {
        Tuple<Integer> index = getIndex(new Vector2(x, y));
        return getMatrixCells().get(index.valueA).get(index.valueB);
    }

    /**
     * Returns the cell at the corresponding position
     *
     * @param targetPosition target position
     * @return target position Cell
     */
    public Cell getCell(Vector2 targetPosition) {
        return getCell(targetPosition.x, targetPosition.y);
    }

    /**
     * return the cells in this CellMap
     *
     * @return cell list
     */
    public ArrayList<Cell> getCells() {
        return cellList;
    }

    /**
     * returns a list of cells represented by a matrix
     *
     * @return matrix cell list
     */
    public ArrayList<ArrayList<Cell>> getMatrixCells() {
        return matrixCells;
    }

    /**
     * eturns the cells corresponding to the index
     *
     * @param i index i
     * @param j index j
     * @return matrix cell
     */
    public Cell getMatrixCell(int i, int j) {
        return matrixCells.get(i).get(j);
    }

    /**
     * return index of the cell at the target position
     *
     * @param targetPositionX target x-position
     * @param targetPositionY target y-position
     * @return cell index
     */
    public Tuple<Integer> getIndex(float targetPositionX, float targetPositionY) {
        //線形探索
        int x = 0, y = 0;
        for (int i = 0; i < matrixCells.size(); i++) {
            if (targetPositionX < matrixCells.get(i).get(0).getRightBottomPoint().x) {
                break;
            }
            x++;
        }
//        for (ArrayList<Cell> cell : matrixCells) {
//            if (targetPositionX < cell.get(0).getRightBottomPoint().x) {
//                break;
//            }
//            x++;
//        }
        for (int i = 0; i < matrixCells.get(0).size(); i++) {
            if (targetPositionY < matrixCells.get(0).get(i).getRightTopPoint().y) {
                break;
            }
            y++;
        }
//        for (ArrayList<Cell> matrixCell : matrixCells) {
//            if (targetPositionY < matrixCell.get(0).getRightTopPoint().y) {
//                break;
//            }
//            y++;
//        }
        return new Tuple<>(x, y);
    }

    /**
     * Returns the index corresponding to the target cell
     *
     * @param targetPosition target position x, y
     * @return cell index
     */
    public Tuple<Integer> getIndex(Vector2 targetPosition) {
        return getIndex(targetPosition.x, targetPosition.y);
    }

    /**
     * Returns the index corresponding to the target cell
     *
     * @param targetCell target cell
     * @return cell index
     */
    public Tuple getIndex(Cell targetCell) {
        return getIndex(targetCell.getCenterPoint());
    }

    /**
     * @return index of the left-bottom cell
     */
    public Tuple getLeftBottomIndex() {
        return new Tuple<>(0, 0);
    }

    /**
     * @return index of the left-top cell
     */
    public Tuple getLeftTopIndex() {
        return new Tuple<>(0, matrixCells.get(0).size() - 1);
    }

    /**
     * @return index of the right-bottom cell
     */
    public Tuple getRightBottomIndex() {
        return new Tuple<>(matrixCells.size() - 1, 0);
    }

    /**
     * @return index of the right-top cell
     */
    public Tuple getRightTopIndex() {
        return new Tuple<>(matrixCells.size() - 1, matrixCells.get(0).size() - 1);
    }

    /**
     * return last cell in the configuration area
     *
     * @return last index of cell
     */
    public Tuple getLastIndex() {
        return getRightTopIndex();
    }

    /**
     * return interval of cell
     *
     * @return cell-interval
     */
    public float getCellInterval() {
        return cellInterval;
    }

    /**
     * return scale value and cell-interval value
     *
     * @return scale and cellInterval
     */
    @Override
    public String toString() {
        return "Constructor is " + "scale=" + scale + ", =" + ", cellInterval" + cellInterval;
    }
}
