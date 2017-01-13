package net.hengstberger.primeapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import org.spongycastle.util.BigIntegers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.ArrayList;

import static org.spongycastle.math.Primes.isMRProbablePrime;

// author: Martin Hengstberger 2016
// All rights reserved ©

public class MainActivity extends AppCompatActivity {
    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    private File CSVfile;
    private FileWriter fileWriter;
    private int inputlength;
    private ProgressDialog progressBar;
    private Handler progressBarbHandler = new Handler();
    private int primesGenerated = 0;
    private int iterationCnount = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView txtView = (TextView) findViewById(R.id.output_textView);
        NumberPicker np = (NumberPicker) findViewById(R.id.numberPicker);
        final String[] values = {"4", "8", "1024", "2048", "3072"};
        inputlength = 4;
        np.setDisplayedValues(values);
        np.setMinValue(0);
        np.setMaxValue(values.length - 1);
        np.setWrapSelectorWheel(true);

        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                inputlength = Integer.parseInt(values[newVal]);
            }
        });

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        CSVfile = new File(dir, "PrimeTimeMeasurements.txt");
        if (CSVfile.exists()) {
            CSVfile.delete();
        }
        try {
            CSVfile.createNewFile();
        } catch (IOException e) {
            txtView.setText("Exception encountered: " + e.getMessage());
            e.printStackTrace();
        }


        final Button buttonProbable = (Button) findViewById(R.id.run_probable);
        buttonProbable.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    try {
                        fileWriter = new FileWriter(CSVfile);
                    } catch (IOException e) {
                        txtView.setText("Exception encountered: " + e.getMessage());
                        e.printStackTrace();
                    }

                    progressBar = new ProgressDialog(v.getContext());
                    progressBar.setCancelable(false);
                    progressBar.setMessage("Generating Prime ...");
                    progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressBar.show();
                    new Thread(new Runnable() {
                        public void run() {
                            final String resultString = GenerateProbablePrime(inputlength);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView txtView = (TextView) findViewById(R.id.output_textView);
                                    txtView.setText(resultString);
                                    try {
                                        fileWriter.append("\n");
                                        fileWriter.flush();
                                        fileWriter.close();

                                        progressBar.dismiss();
                                    } catch (IOException e) {
                                        txtView.setText("Exception encountered: " + e.getMessage());
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }).start();

                } catch (Exception e) {
                    e.printStackTrace();
                    txtView.setText("Exception encountered: " + e.getMessage());
                }

            }
        });

        final Button button1000Probable = (Button) findViewById(R.id.run_probable_1000);
        button1000Probable.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    try {
                        fileWriter = new FileWriter(CSVfile);
                    } catch (IOException e) {
                        txtView.setText("Exception encountered: " + e.getMessage());
                        e.printStackTrace();
                    }

                    progressBar = new ProgressDialog(v.getContext());
                    progressBar.setCancelable(false);
                    progressBar.setMessage("Generating Primes ...");
                    progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressBar.setProgressNumberFormat("%1d/%2d");
                    progressBar.setProgress(0);
                    progressBar.setMax(iterationCnount);
                    progressBar.show();

                    final long startTime = System.currentTimeMillis();

                    new Thread(new Runnable() {
                        public void run() {

                            for (primesGenerated = 0; primesGenerated < iterationCnount; primesGenerated++) {
                                GenerateProbablePrime(inputlength);
                                progressBarbHandler.post(new Runnable() {
                                    public void run() {
                                        progressBar.setProgress(primesGenerated);
                                    }
                                });
                            }
                            if (primesGenerated >= iterationCnount) {
                                progressBar.dismiss();
                                long endTime = System.currentTimeMillis();
                                final long totalDifftime = endTime - startTime;
                                try {
                                    fileWriter.append("\n");
                                    fileWriter.flush();
                                    fileWriter.close();
                                } catch (IOException e) {
                                    final String msg = e.getMessage();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            txtView.setText("Exception encountered: " + msg);
                                        }
                                    });
                                    e.printStackTrace();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        TextView txtView = (TextView) findViewById(R.id.output_textView);
                                        txtView.setText(primesGenerated + " Primes generated in " + ((float) totalDifftime / (float) 1000) + "s " +
                                                "time table generated in Downloads folder (" + CSVfile.getAbsolutePath() + ")");
                                    }
                                });

                            }

                        }
                    }).start();

                } catch (Exception e) {
                    txtView.setText("Exception encountered: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * Generates and tests a probable prime and finally timings are written to a CSV file
     *
     * @param length Bit length
     * @return String with the prime and additional information
     */
    private String GenerateProbablePrime(int length) {
        SecureRandom random = new SecureRandom();
        try {
            BigInteger candidate = BigInteger.ZERO;
            ArrayList<TripleLong> timingresults = new ArrayList<>();
            timingresults.ensureCapacity(1000);
            boolean isLibPrime = false;
            boolean isMyPrime = false;
            long i = 1;
            long totalTimeStart = System.currentTimeMillis();
            long sumLibMRtime = 0, sumMyMRtime = 0;
            while (isLibPrime == false && i < 10000) {
                try {
                    candidate = getRandomPrimeCandidate(random, length);
                } catch (NoSuchAlgorithmException e) {
                    return "Error: " + e.getMessage();
                }
                long libMRtimeStart = System.currentTimeMillis(); // nanoTime()
                //candidate= candidate.nextProbablePrime(); // debug only
                isLibPrime = isMRProbablePrime(candidate, random, 64);
                long libMRtimeEnd = System.currentTimeMillis();
                long libMRdiff = libMRtimeEnd - libMRtimeStart;
                sumLibMRtime += libMRdiff;
                isMyPrime = MyMRPrimeTest(candidate, random, 64);
                long myMRtimeEnd = System.currentTimeMillis();
                long myMRdiff = myMRtimeEnd - libMRtimeEnd;
                sumMyMRtime += myMRdiff;
                i++;
            }
            long totalTimeEnd = System.currentTimeMillis();
            long totalTimeDiff = totalTimeEnd - totalTimeStart;
            long totalMyMRtime = totalTimeDiff - sumLibMRtime;
            long totalLibMRtime = totalTimeDiff - sumMyMRtime;
            long totalGenTime = totalTimeDiff - sumLibMRtime - sumMyMRtime;
            // write times to a file
            TripleLong tripleLong = new TripleLong(totalGenTime, totalLibMRtime, totalMyMRtime);
            timingresults.add(tripleLong);
            try {
                if (fileWriter != null) {
                    fileWriter.append(totalMyMRtime + "," + totalLibMRtime + ";");
                }
            } catch (IOException e) {
                return "File Write Error " + e.getMessage();
            }

            // Test with my Miller Rabin Test
            String myMRResult;
            if (isMyPrime) {
                myMRResult = " passed myMR";
            } else {
                myMRResult = " failed myMR";
            }

            // Test with Library Miller Rabin Test
            String libMRResult;
            if (isLibPrime) {
                libMRResult = " passed LibMR";
            } else {
                libMRResult = " failed LibMR";
            }

            String numberAndBits = candidate.toString() + "\n" + "N = " + (candidate.bitLength()) + " bits ";
            String resultString = numberAndBits + " i=" + i + " attempts" + libMRResult + ";" + myMRResult + ";" +
                    " Total time Generation: " + totalGenTime + "ms;" +
                    " Total time LibMR: " + totalLibMRtime + "ms;" +
                    " Total time MyMR: " + totalMyMRtime + "ms,";

            return resultString;

        } catch (Exception e) {
            return "Error encountered: " + e.getMessage();
        }

    }

    /**
     * @param random random bit generator
     * @param length Bit length of the desired prime candidate
     * @return a prime candidate
     * @throws NoSuchAlgorithmException
     */
    BigInteger getRandomPrimeCandidate(SecureRandom random, int length) throws NoSuchAlgorithmException {
        if (length <= 1) { // lower limit
            return new BigInteger("2");
        }
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(random.generateSeed(256));
        byte[] digest = messageDigest.digest();
        BigInteger candidate = BigInteger.ZERO;
        byte[] magnitude = new byte[1]; // big endian
        switch (length) {
            case 4:
                byte halfByte = (byte) (digest[0] & (1 + 2 + 4 + 8) | 8); // set MSB -> at least 4 Bit long (8) and mask 17 for setting the higher bits 0
                halfByte = (byte) (halfByte | 1); // set LSB (-> odd)
                magnitude[0] = halfByte;
                break;
            case 8:
                byte oneByte = (byte) (digest[0] | 0x01); // set MSB -> at least 8 Bit long
                oneByte = (byte) (oneByte | 128); // set LSB (-> odd)
                magnitude[0] = oneByte;
                break;
            case 1024:
            case 2048:
            case 3072:
                ByteBuffer buffer = ByteBuffer.allocate(length / 8);
                while (buffer.remaining() > 0) {
                    messageDigest.update(random.generateSeed(256));
                    byte[] curDigest = messageDigest.digest();
                    buffer.put(curDigest);
                }
                magnitude = buffer.array();
                magnitude[magnitude.length - 1] |= 0x01; // set LSB 1 --> odd
                magnitude[0] |= 128; // set MSB 1
                break;
            default:
                return BigInteger.ZERO;  // illegal length
        }

        final int signum = 1;
        candidate = new BigInteger(signum, magnitude);

        return candidate;
    }


    /**
     * My Miller Rabin Test
     *
     * @param n          prime candidate
     * @param random     Random bit generator
     * @param iterations quality parameter that determines how likely prime the candidate is
     * @return true if n is probable prime, otherwise flase
     */
    boolean MyMRPrimeTest(BigInteger n, SecureRandom random, int iterations) {

        final BigInteger ZERO = BigInteger.ZERO;
        final BigInteger ONE = BigInteger.ONE;
        final BigInteger TWO = new BigInteger("2");

        if (n.compareTo(TWO) == -1) { // n < 2 are forbidden
            return false;
        }
        if (n.testBit(0) == false) { // if LSB == 0 then n is a even number -> forbidden
            return false; // trivialy divisible by 2
        }

        int s = 0;
        BigInteger d = n.subtract(ONE);
        while (d.mod(TWO).equals(ZERO)) {
            s++;
            d = d.divide(TWO);
        }

        for (int i = 0; i < iterations; i++) {
            BigInteger a = BigIntegers.createRandomInRange(TWO, n.subtract(TWO), random); // random a in range [2,n-2]
            BigInteger x = a.modPow(d, n); // x = a^(d) mod n
            if (x.equals(ONE) || x.equals(n.subtract(ONE))) {
                continue; // no proof for composite found
            }
            int j;
            for (j = 1; j < s; j++) {
                x = x.modPow(TWO, n); // x = a^(2) mod n
                if (x.equals(ONE)) { // x == 1
                    return false;
                }
                if (x.equals(n.subtract(ONE))) { // x == n-1
                    break;
                }

            }
            if (j == s) // x == n-1 did not occur -> composite
                return false;
        }
        return true; // probably prime

    }


}
