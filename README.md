# PrimeApp
PrimeApp is a cryptographic **prime number generation** and _performance_ testing **Android** *App* using the Miller Rabin test.
## Introduction
Cryptographic algorithms as **RSA** and **DH** require large primes (up to several 100 digits long). This **Java** project implements an efficient prime number generation (**[NIST FIPS-186-4](http://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.186-4.pdf)**) and analyses its performance comparing to the well known __Bouncy Castle library__. This sophisticated project evaluates and documents the performance in **great detail** for a physical Android device and the Android studio supplied emulator. (The emulator is faster!)

Please take a look into the [35 page full documentation](https://github.com/hengxti/PrimeApp/blob/master/Prime_Generation_v2_Documentation.pdf) for more Information.

![Screenshot](https://github.com/hengxti/PrimeApp/blob/master/Screenshot_20160827-200837.png)

## Candidate creation
The Miller rabin test needs candidates integers to test if they are prime or not. Here is a diagram how the candidates are  generated. Even candidates are ignored, because even numbers can never be prime (trivial devisable by 2).
![diagramm](https://github.com/hengxti/PrimeApp/blob/master/generation.png)

## Author
Martin Hengstberger m.hengstberger 'at' gmx.at . This is one of my university projects at Johannes Kepler University Linz.

## Installation 
Complie code into an Android apk or use the apk in the repository. And put it on your Android smart phone. Note that you need to go to Settings -> Application -> tab "Allow Apps from unknown sources" and open the App from the luncher. See [the documentation](https://github.com/hengxti/PrimeApp/blob/master/Prime_Generation_v2_Documentation.pdf) section installation, for a more details description.

## License 
Copyright protected. Other licenses and commercial support are available upon request at m.hengstberger 'at' gmx.at. 

## Results
This diagram shows the times needed to generate 1000 prime numbers (each 1024 Bit long) using the Miller Rabin test. It is clearly visible that sometimes many candidates are tested before a prime is recognized, thoses are large statistical outliars. The average generation time is much lower. 
![results](https://github.com/hengxti/PrimeApp/blob/master/1024bitdata.png)

## Programm process
1. generate a number of the desired length
2. test the number if it is prime or not with the library implementation
3. test the same number if it is prime or not with my own implementation
4. compare my implementation's performance with the libraries. Also compare the results for correctness - They always had the same result -> my implementation is as correct as the library.
![process](https://github.com/hengxti/PrimeApp/blob/master/process.png)

## Pseudo Code Miller Rabin test
![peseudocode](https://github.com/hengxti/PrimeApp/blob/master/pseudocode.png)

