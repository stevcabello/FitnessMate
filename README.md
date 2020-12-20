# FitnessMate

FitnessMate is an Android fitness-tracking mobile application with the features of online automatic activity identification and energy expenditure estimation. 
Every five seconds the activity perfomed is automatically recognized and the calories expended on that activity are aggregated. The user is able to check at any time of the day the calories burned in total and per activity.

FitnessMate automatically detects 5 types of activities: walking, jogging, cycling and walking upstairs/downstairs providing 
the calories consumed on each exercise. 

Developed as part of my thesis: "Automatic caloric expenditure estimation with smartphone’s built-in sensors".

# System Overview

![Alt text](https://user-images.githubusercontent.com/5056125/102712745-7b7c8900-4317-11eb-9022-069661a36321.png?raw=true "Overview")


# Further Details
* Upon installation in the smartphone, FitnessMate will be running as a background service collecting sensing data at 20Hz and recognizing the activity and aggregating its calories consumed every five seconds. 

* In order to preserve smartphone battery life, we also provide a power saving mode, where if Others activities are detected continuosly over
a two-minutes window, the data collection stops for five minutes and then the activity recognition service restarts.

* To perform the online activity classifcation we are using the Android version of the library for support vector machines Android LIBSVM (source: https://github.com/yctung/AndroidLibSvm), with the kernel parameter set to "linear".

* Furthermore, we used the data collection described in the table below to build the classification model (i.e., training data), which is embedded in the application.

<p align="center"><img src="https://user-images.githubusercontent.com/5056125/102713162-2a21c900-431a-11eb-940b-553052d92b51.png" width="400"></p>

