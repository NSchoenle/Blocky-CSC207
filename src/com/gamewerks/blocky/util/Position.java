package com.gamewerks.blocky.util;

public class Position {
    public int row;
    public int col;
    
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    public Position add(int row, int col) {
        return new Position(this.row + row, this.col + col);
    }
    
    public String toString() { return String.format("(row = %d, col = %d)", row, col); } 
}
