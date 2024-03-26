package ru.nsu.fit.demo;

public final class Rectangle {
    private int width = 1;
    private int height = 1;
    public int area(){
        return width * height;
    }
    public void setWidth(int width){
        this.width = width;
    }
    public void setHeight(int height){
        this.height = height;
    }
}
