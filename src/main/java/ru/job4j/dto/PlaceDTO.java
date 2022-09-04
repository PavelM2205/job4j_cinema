package ru.job4j.dto;

import java.util.Objects;

public class PlaceDTO {
    private int id;
    private int row;
    private int cell;
    private boolean isTaken = false;

    public PlaceDTO() {
    }

    public PlaceDTO(int id, int row, int cell) {
        this.id = id;
        this.row = row;
        this.cell = cell;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCell() {
        return cell;
    }

    public void setCell(int cell) {
        this.cell = cell;
    }

    public boolean isTaken() {
        return isTaken;
    }

    public void setTaken(boolean taken) {
        isTaken = taken;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PlaceDTO placeDTO = (PlaceDTO) o;
        return row == placeDTO.row && cell == placeDTO.cell;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, cell);
    }

    @Override
    public String toString() {
        return "Place{" + "id=" + id
                + ", row=" + row
                + ", cell=" + cell
                + ", isTaken=" + isTaken + '}';
    }
}
