# PrimeApp
## Overview
PrimeApp is a cryptographic **prime number generation** and _performance_ testing **Android** *App* using the Miller Rabin test.
Cryptographic algorithms as RSA and DH require large primes (up to several hundred digits long). This Java project implements a efficient prime number generation (**NIST FIPS-186-4**) and analyses its performance comparing to the well known _Bouncy Castle library_. This sophisticated project evaluates and documents the performance in **great detail** for a physical Android device and the Android studio supplied emulator. (The emulator is faster!)

Please take a look into the [35 page full documentation](https://github.com/hengxti/PrimeApp/blob/master/Prime_Generation_v2_Documentation.pdf) for more Information.

![Screenshot](https://github.com/hengxti/PrimeApp/blob/master/Screenshot_20160827-200837.png)

## Candidate creation
The Miller rabin test needs candidate integers to test. Here is a diagram how candidates can be generated.
![diagramm](https://github.com/hengxti/PrimeApp/blob/master/generation.png)

## Author
Martin Hengstberger m.hengstberger 'at' gmx.at . This was one of my university projects at Johannes Kepler University Linz.

## Installation 
Complie code into an Android apk or use the apk in the repository. See [the documentation pdf](https://github.com/hengxti/PrimeApp/blob/master/Prime_Generation_v2_Documentation.pdf) section installation.

## License 
Copyright protected. Other licenses are available upon request.

## Programm process
![process](https://github.com/hengxti/PrimeApp/blob/master/process.png)

## Pseudo Code Miller Rabin test
![peseudocode](https://github.com/hengxti/PrimeApp/blob/master/pseudocode.png)

