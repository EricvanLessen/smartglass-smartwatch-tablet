package com.example.eric.application;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Description of the class:
 *
 * VARIABLES:
 * private double [] processes holds the four processes and processes[4]
 * holds the delay until the first process starts
 *
 * interval:
 * duration in which one alarm occurs
 *
 * Math.random():
 * This function returns a random number  between 0.0 and 1.0
 * (e.g. 0.6435).


 * BEHAVIOUR:
 * The class provides four processes.
 * Four random numbers are generated and then smoothed, hence
 * process durations do not differ much.
 */

public class ProzessProvider {

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // class variables and parameters                                                //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    private double [] processes;

    /**
    delay and minProcessLength can be modified here
     */

    //1min delay
    private final double delay= 60000;

    // all the processes sum up to 420.000 ms which (seven minutes)
    // the shortest process has length 0.143 * 420000 ms, about 1 minute
    private final double minProcessLength = 0.143;

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // constructors, getters, setters                                                //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    ProzessProvider() {

        double a1;
        double a2;
        double a3;
        double a4;

        //generate random data
        a1 = Math.random();
        a2 = Math.random();
        a3 = Math.random();
        a4 = Math.random();


        processes = new double[5];
        processes[0]=a1;
        processes[1]=a2;
        processes[2]=a3;
        processes[3]=a4;

        //normalize
        processes = normalize(processes);

        //smooth random data
        while( Math.min( processes[0] , Math.min( processes[1] ,
                Math.min( processes[2] , processes[3] ))) < minProcessLength ) {

            //add 0.025 to min duration, subtract 0.025 from max duration
            processes = smooth(processes);

            //normalize again
            processes = normalize(processes);
        }

        //stretch durations to 7 minutes alltogether
        for(int i = 0; i<4; i++){
            processes[i] *= 480000 - delay;
        }

        processes[4]=delay;
    }

    protected int[] getProcesses(){
        int[] _processes = new int[5];
        for(int i = 1; i<5 ;i++)
            _processes[i] = (int) processes[i];
        return _processes;
    }

    //normalize the process duration
    private double[] normalize(double[] processes){
        for(int i = 0; i<4; i++){
            processes[i] = processes[i] /
                    (processes[1] + processes[2] + processes[3] + processes[4]);
        }
        return processes;
    }

    //smooth the process duration
    private double[] smooth(double[] processes){
        double max = Math.max(processes[0], Math.max(processes[1] ,
                Math.max(processes[2], processes[3])));
        double min = Math.min(processes[0], Math.min(processes[1],
                Math.min(processes[2] , processes[3] )));
        for(int i = 0; i<= 4; i++){
            if(processes[i]==max)
                processes[i]-=0.05;
        }
        for(int i = 0; i<4; i++){
            if(processes[i]==min)
                processes[i]+=0.05;
        }
        return processes;
    }
}
