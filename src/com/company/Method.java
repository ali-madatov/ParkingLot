package com.company;

public class Method {
    //returns true if b overlaps a
    public static boolean IsContainedIn(VehicleSlot a, VehicleSlot b){
        return a.x >= b.x && a.y >= b.y
                && a.x+a.width <= b.x+b.width
                && a.y+a.height <= b.y+b.height;
    }
}
