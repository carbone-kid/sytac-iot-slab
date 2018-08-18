# Sytac IoT sLab

## Programming workflow
1. If you on Windows install Bonjour service from Apple. On Mac skip this step.
2. If you don't have them installed yet, install JDK 8 and Maven
3. Install Putty
4. Flash this image to 8GB micro SD card and insert it into Raspberry Pi Zero W (to make it shorter RPi)
5. Connect RPi to your computer, open Putty and create a new session
    * Host name: rpi-1.local
    * Password: sytac-1
    * Port: 22
    * Click "Open" to connect to the RPi
6. To build the java project and upload it to RPi execute the command `mvn clean package`
7. To run the program on RPi in open Putty terminal execute the script `/home/pi/run_app.sh`
8. To close the program execute the command `ps -ef | grep java` to find the program process ID and than execute `kill <process id>`
9. When you finished programming and want the program to be ran on Linux startup add the program to `crontab`
    * Execute `crontab -e`
    * Add this to the end of the config: `@reboot /home/pi/run_app.sh >> /home/pi/slab.log &`
    * Execute `sudo reboot now` to reboot your RPi
    
## Project overview
The project is making use of Pi4J library to access GPIO pins on the RPi, and Ubidots cloud IoT frontend to remotely operate the GPIO pins.
If you build the project and run the application on RPi, the application will: 
1. Try to obtain access token from Ubidots using an API key. 
2. Then, from Ubidots, it will try to get the value of the variable `wheel-engaged` for the device `weel`
3. Then it will try to get the value of the variable `wheel-direction` for the device `weel`
4. Then the values of the `wheel-engaged` and `wheel-direction` variables is supplied to the method Motor.go(), which toggles GPIO pins on the RPi.
    
Please put your API key and the GPIO pins numbers you want to use into the `application.properties` file.

## What to do with Ubidots 
1. Create an account on `ubidots.com`. They offer 1 month trial without asking for your credit card, that's why Google IoT was not chosen for this workshop.
2. Create a device with the name `wheel`
3. Inside of the device `wheel` create 2 variables: `wheel-direction` and `wheel-engaged`
4. Create a dashboard
5. On the dashboard create 2 controls with the type `Switch` and assign previously created variables on them. You should see 2 buttons. If you click on the button it should change the last value of the assigned for it variable.

## What to do in the Java project
Take your API key from Ubidots and put it into the `application.properties` file instead of the existing one.
Build the project and run the application on RPi, the output of the program should give you something like this:
```
   Motor engaged: true; Direction: 1
   Motor engaged: true; Direction: 1
   Motor engaged: true; Direction: 1
```
This means the program is successfully fetching values of the `wheel-direction` and `wheel-engaged` variables from Ubidots.
Now if you will be pushing buttons you created on Ubidots you should see the output of the program reflects it.

## What to do with hardware
0. Never short out the +/- from the power supply
1. Look at the pinout of the `Raspberry Pi Zero W` on the Pi4J project website to find the pins on the board
2. Look at the pinout of the L298N Dual H Bridge
3. Connect ground pins of RPi and L298N together 
4. Connect RPi GPIO pins you've used to IN1 and IN2 (or IN3, IN4) on the L298N board
5. Connect OUT1 and OUT2 (or OUT3 and OUT4) to the motor
6. Connect negative contact of the power supply to the ground pin of the L298N board
7. Connect positive contact of the power supply to the +12V pin of the L298N board

## Tips and tricks
* To see the GPIO pins status execute `gpio readall` in the RPi console
