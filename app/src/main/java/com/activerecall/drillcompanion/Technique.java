package com.activerecall.drillcompanion;

/**
 * Created by Steven on 3/7/2018.
 */

class Technique {
    private int order;
    private SpokenName techniqueName;
    private SpokenName techSetName;
    public boolean includeSetName = true;

    public Technique(int order, SpokenName techniqueName, SpokenName techSetName){
        this.order = order;
        this.techniqueName = techniqueName;
        this.techSetName = techSetName;
    }

    public Technique() {}

    public String getWrittenName(){

        String name;

        // if the technique has a name, use it. Otherwise, use it's order
        if (techniqueName.name == null) {
            name = String.valueOf(order);
        } else {
            name = techniqueName.name;
        }

        // Include techSetName in result if flag is set
        if(includeSetName){
            return techSetName.name + " " + name;
        }
        return name;
    }

    public String getPhoneticName(){
        String name;

        // if the technique has a name, use it. Otherwise, use it's order
        if (techniqueName.pronunciation == null) {
            name = String.valueOf(order);
        } else {
            name = techniqueName.pronunciation;
        }

        // Include techSetName in result if flag is set
        if(includeSetName){
            if(techSetName.pronunciation == null){
                name = techSetName.name + " " + name;
            } else {
                name = techSetName.pronunciation + " " +name;
            }

        }
        return name;
    }
}
