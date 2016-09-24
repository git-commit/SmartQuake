package de.ferienakademie.smartquake.excitation;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Records and replays stored acceleration data by Listening to the SensorExcitation-Object
 */
public class ExcitationManager implements SensorEventListener, AccelerationProvider {
    private ArrayList<AccelData> readings;
    private AccelData currAccel;
    private int currPos;
    private long baseTime;
    private double timestep;
    private int tick;

    public ExcitationManager() {
        readings = new ArrayList<>();
        currAccel = new AccelData();
        readings.add(currAccel);
        currPos = 0;
        baseTime = Long.MAX_VALUE;
        timestep = 1000;
        tick = 0;
    }

    /**
     * has to be called before the replay is started
     */
    public void initReplay() {
        currPos = 0;
        tick = 0;
        currAccel = readings.get(0);
    }


    /**
     * This has to be called before the Simulation starts and
     * before the Listener is registered
     */
    public void initSensors() {
        readings = new ArrayList<>();
        currAccel = new AccelData();
        currPos = 0;
        readings.add(currAccel);
    }

    /**
     * Records the sensor data
     *
     * @param event change of the accelerometer indicators
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        currAccel = new AccelData(event.values[0], event.values[1], event.timestamp - baseTime);
        // put new element to the queue of sensor measurements
        readings.add(currAccel);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //not useful
    }


    @Override
    public double[] getAcceleration() {
        AccelData temp = getAccelerationMeasurement();
        return new double[]{temp.xAcceleration, temp.yAcceleration};
    }

    @Override
    public AccelData getAccelerationMeasurement() {
        long currTime = (long) (tick * timestep);
        while (readings.size() - 1 > currPos
                && readings.get(currPos).timestamp < currTime) {
            ++currPos;
        }
        tick++;

        return readings.get(currPos);
    }

    /**
     * @param timeStamp timeStamp at the beginning of the Simulation in nanoseconds
     * @param timeStep  timeStep of the simulation in nanoseconds
     */
    @Override
    public void initTime(long timeStamp, double timeStep) {
        this.baseTime = timeStamp;
        this.timestep = timeStep;
    }

    /**
     * Store the data to a file
     * @param outputStream reference to a stream passing readings to internal storage
     */
    public void saveFile(OutputStream outputStream) {
        String readingString;
        OutputStreamWriter outputStreamReader;
        BufferedWriter bufferedWriter;
        try {
            outputStreamReader = new OutputStreamWriter(outputStream);
            bufferedWriter = new BufferedWriter(outputStreamReader);
            for (int i = 0; i < readings.size(); i++) {
                readingString = String.format(Locale.ENGLISH, "%d %f %f\n", readings.get(i).timestamp,
                        readings.get(i).xAcceleration, readings.get(i).yAcceleration);
                bufferedWriter.write(readingString);
            }
            bufferedWriter.flush();
            //outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Load acceleration data from a file
     * @param inputStream stream that passes readings from a file to excitation manager
     */
    public void loadFile(InputStream inputStream) {
        readings = new ArrayList<>();
        currAccel = new AccelData();
        AccelData curReading = new AccelData();
        String readingString;
        String[] readStringSplit;
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;
        try {
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            readingString = bufferedReader.readLine();
            while (readingString != null) {
                readStringSplit = readingString.split(" ");

                curReading.timestamp = Long.parseLong(readStringSplit[0]);
                curReading.xAcceleration = Double.parseDouble(readStringSplit[1]);
                curReading.yAcceleration = Double.parseDouble(readStringSplit[2]);

                readings.add(new AccelData(curReading));
                readingString = bufferedReader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * If the given timestamp lies between to timesteps in the list this method uses linear
     * interpolation between the two values to calculate the return value
     *
     * @param queue_pos the position from which accelerometer readings will be read
     * @param timestamp time point for which interpolation of measurements are prefromed
     * @return weighted average of the two accelerometer measurements around time point of interest
     */
    private double[] interpolate(int queue_pos, long timestamp) {
        AccelData curr = readings.get(queue_pos);
        AccelData prev = readings.get(queue_pos - 1);
        long factor = (timestamp - prev.timestamp) / (curr.timestamp - prev.timestamp);
        return new double[]{prev.xAcceleration +
                (curr.xAcceleration - prev.yAcceleration) * factor,
                prev.yAcceleration + (curr.yAcceleration - prev.yAcceleration) * factor};

    }
}