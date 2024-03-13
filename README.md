# APP
In order to achieve faster and smarter detection of cracks in the dam, this study utilized Android Studio to deploy the lightweight segmentation model to Android mobile devices, developing it as a mobile APP. The flow of using APP is shown as illustrated in the figure.
![image](https://github.com/JingyueYuan/APP/blob/a6d447fe691b7c1f2df34db75cd51e30c5477604/Fig.%201.jpg)
The mobile can take pictures of cracks in the dam or directly select the pictures of cracks in the phone. The pictures would be cropped first, and then be uploaded to the PC server for processing the pictures. The server first preprocesses the image, and then uses the segmentation model to segment the image. After obtaining the segmentation, pixel-level results would be calculated according to the existing methods for quantifying the geometric features of the cracks. The pictures of the skeleton of the cracks as well as the crack area, length, average width and actual width (The first three are measured in pixels and the last one is in millimeters.) is displayed in the interface. In the interface of the result, the description of this crack could also be input and we could save them into the database.
![image](https://github.com/JingyueYuan/APP/blob/main/%E5%9B%BE2.jpg)
Before running APP, you need to run Serverflask.py to start the server.

# Environment
Android Studio
Python=3.6 
Tensorflow=1.14
