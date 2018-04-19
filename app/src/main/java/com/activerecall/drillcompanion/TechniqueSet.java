package com.activerecall.drillcompanion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Steven on 3/7/2018.
 */

public class TechniqueSet {

    private SpokenName techSetName;
    private int techniqueCount;
    private ArrayList<Technique> techniques = new ArrayList<>();

    public static final TechniqueSet SohnPpaeKi = new TechniqueSet(new SpokenName("Sohn Ppae Ki","Sohn Pae Ki"),5);
    public static final TechniqueSet KiBohnSoo = new TechniqueSet(new SpokenName("Ki Bohn Soo","Key Bohn Sue"),15);

    public TechniqueSet(SpokenName techSetName, int techniqueCount){
        this.techSetName = techSetName;
        this.techniqueCount = techniqueCount;

        for (int i = 0; i < techniqueCount; i++) {
            techniques.add(new Technique(i+1, new SpokenName(null,null), techSetName));
        }
    }

    public String getWrittenName() {return techSetName.name;}

    public String getPronunciation() {return techSetName.pronunciation;}

    public int getTechniqueCount(){return techniqueCount;}

    public ArrayList<Technique> getOrderedTechniques() {
        return techniques;
    }

    public ArrayList<Technique> getRandomTechniques() {

        ArrayList<Technique> tempTechs = new ArrayList<>();

        tempTechs.addAll(techniques);

        long seed = System.nanoTime();
        Collections.shuffle(tempTechs, new Random(seed));

        return tempTechs;
    }
}
