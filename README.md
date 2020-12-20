# FitnessMate

FitnessMate is an Android fitness-tracking mobile application with the features of online automatic activity identification and energy expenditure estimation. 
Every five seconds the activity perfomed is automatically recognized and the calories expended on that activity are aggregated. The user is able to check at any time of the day the calories burned in total and per activity.

FitnessMate automatically detects 5 types of activities: walking, jogging, cycling and walking upstairs/downstairs providing 
the calories consumed on each exercise. 

Developed as part of my thesis: "Automatic caloric expenditure estimation with smartphone’s built-in sensors".

# System Overview

<p align="center"><img src="https://user-images.githubusercontent.com/5056125/102712745-7b7c8900-4317-11eb-9022-069661a36321.png" width="500"></p>


# Further Details
* Upon installation in the smartphone, FitnessMate will be running as a background service collecting sensing data at 20Hz and recognizing the activity and aggregating its calories consumed every five seconds. 

* In order to preserve smartphone battery life, we also provide a power saving mode, where if Others activities are detected continuosly over
a two-minutes window, the data collection stops for five minutes and then the activity recognition service restarts.

* To perform the online activity classifcation we are using the Android version of the library for support vector machines Android LIBSVM (source: https://github.com/yctung/AndroidLibSvm), with the kernel parameter set to "linear".

* Furthermore, we used the data collection described in the table below to build the classification model (i.e., training data), which is embedded in the application.

<p align="center"><img src="https://user-images.githubusercontent.com/5056125/102713162-2a21c900-431a-11eb-940b-553052d92b51.png" width="400"></p>

* The user is allowed to provide information related to his height, weigth, and gender to increase the accuracy of the calories estimation.
<p align="center"><img src="https://user-images.githubusercontent.com/5056125/102713746-7c64e900-431e-11eb-9b8d-a9e6f10d64f7.png" width="200"></p>

* The calories consumed are detailed by activity and in total per day, and can be checked by the user at any time. Besides calories expended, information about the number of steps, distance traveled, and floors climbed is also provided by the system to increase user's motivation.
<p align="center"><img src="https://user-images.githubusercontent.com/5056125/102713712-49225a00-431e-11eb-8845-70d16fa2d527.png" width="200"></p>

* Finally, the application comes with an export button, where the collected data from accelerometer, gyroscope and barometer sensors is available to the user either for research purposes or further analysis.
