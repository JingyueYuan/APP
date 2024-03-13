# -*- coding: utf-8 -*-
"""
Created on Wed Apr  7 21:00:45 2021
from skimage import morphology
@author: Jiake Fu
"""
import io
import cv2
from PIL import Image
import numpy as np
import tensorflow as tf
from tensorflow import keras
from tensorflow.keras.preprocessing.image import img_to_array,load_img
import sys
sys.path.append("./airModel")
sys.path.append("./underWaterModel")
from end_To_EndModel import classEndModel,formEndModel
from segemModel import myOwnNetwork2
from skimage import morphology
from matplotlib import pyplot as plt
from numpy.ma import cos, sin
import random
import math



global graph,sess
graph = tf.get_default_graph()
sess = keras.backend.get_session()

def changeImg2Bytes(img):
    imagebytes = io.BytesIO()
    img.save(imagebytes,format="png")
    imagebytes = imagebytes.getvalue()
    return imagebytes


def airModel():
    model = myOwnNetwork2()
    model.load_weights("./airModel/asppnet.h5")
    model.summary()
    return model


def mergeMask(thresh1):
    area=0
    mask =np.zeros((224,224,3))

    mask_rgb = [[0,0,0],[128,0,0]]
    for i in range(thresh1.shape[0]):
        for j in range(thresh1.shape[1]):
            if thresh1[i][j] == 1:
                mask[i][j] = mask_rgb[1]
                area+=1
   
    #aa = Image.fromarray(np.uint8(mask))
    #return aa
    return mask,area



def modelPredict(model,path,Ratio=224):
    rows ,cols, originalImage = firstImageSolve(path)

    finalResult = np.zeros_like(originalImage,np.uint8)

    AreaTotal = 0
    for i in range(rows):
        for j in range(cols):
            box = (j*Ratio,i*Ratio,(j+1)*Ratio,(i+1)*Ratio)
            tempImage = originalImage.crop(box)
            tempImage = np.array(tempImage)/255.0
            tempImage = np.expand_dims(tempImage,axis=0)
            with sess.as_default():
                with graph.as_default():
                    predict = model.predict(tempImage)
            pred = np.squeeze(predict)
            ret,thresh1 = cv2.threshold(pred,0.5,1,cv2.THRESH_BINARY)
            mask,area = mergeMask(thresh1)
            finalResult[i*Ratio:(i+1)*Ratio,j*Ratio:(j+1)*Ratio,:] = mask
            AreaTotal+=area
    

    return finalResult,AreaTotal


def thinImage(img):
    aa = morphology.skeletonize(img)
    return aa+0.


def firstImageSolve(path):

    image = load_img(path)
    width = image.size[0]
    height = image.size[1]
    cols = int(width/224)
    if(width-cols*224)>=112:
        cols+=1
    rows = int(height/224)
    if(height-rows*224)>=112:
        rows+=1
    originalImage = image.resize((cols*224,rows*224))

    return rows,cols,originalImage
 

def max_circle(f):
    img = cv2.imread(f, cv2.IMREAD_COLOR)
    img_gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

    contous, hierarchy = cv2.findContours(img_gray, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
   
    Max_width = []
    for c in contous:
        left_x = min(c[:, 0, 0])
        right_x = max(c[:, 0, 0])
        down_y = max(c[:, 0, 1])
        up_y = min(c[:, 0, 1])
       
        upper_r = min(right_x - left_x, down_y - up_y) / 2
        if(upper_r<=0.5):
            continue

        precision = math.sqrt((right_x - left_x) ** 2 + (down_y - up_y) ** 2) / (2 ** 13)

        Nx = 50
        Ny = 50
        pixel_X = np.linspace(left_x, right_x, Nx)
        pixel_Y = np.linspace(up_y, down_y, Ny)
 


        xx, yy = np.meshgrid(pixel_X, pixel_Y)

        in_list = []
        for i in range(pixel_X.shape[0]):
            for j in range(pixel_X.shape[0]):
                if cv2.pointPolygonTest(c, (xx[i][j], yy[i][j]), False) > 0:
                    in_list.append((xx[i][j], yy[i][j]))
        in_point = np.array(in_list)
        pixel_X = in_point[:, 0]
        pixel_Y = in_point[:, 1]

        N = len(in_point)
        rand_index = random.sample(range(N), N // 100)
        rand_index.sort()
        radius = 0.05
        big_r = upper_r
        center = None
        for id in rand_index:
            tr = iterated_optimal_incircle_radius_get(c, in_point[id][0], in_point[id][1], radius, big_r, precision)
            if tr > radius:
                radius = tr
                center = (in_point[id][0], in_point[id][1])

        loops_index = [i for i in range(N) if i not in rand_index]
        for id in loops_index:
            tr = iterated_optimal_incircle_radius_get(c, in_point[id][0], in_point[id][1], radius, big_r, precision)
            if tr > radius:
                radius = tr
                center = (in_point[id][0], in_point[id][1])

        plot_x = np.linspace(0, 2 * math.pi, 100)
        circle_X = center[0] + radius * cos(plot_x)
        circle_Y = center[1] + radius * sin(plot_x)
        Max_width.append(radius*2)    
        '''
        print(radius * 2)
        plt.figure()
        plt.imshow(img_gray)
        plt.plot(circle_X, circle_Y)
        plt.show()
        '''
    return max(Max_width)
 
def iterated_optimal_incircle_radius_get(contous, pixelx, pixely, small_r, big_r, precision):
    radius = small_r
    L = np.linspace(0, 2 * math.pi, 360)
    circle_X = pixelx + radius * cos(L)
    circle_Y = pixely + radius * sin(L)
    for i in range(len(circle_Y)):
        if cv2.pointPolygonTest(contous, (circle_X[i], circle_Y[i]), False) < 0:
            return 0
    while big_r - small_r >= precision:
        half_r = (small_r + big_r) / 2
        circle_X = pixelx + half_r * cos(L)
        circle_Y = pixely + half_r * sin(L)
        if_out = False
        for i in range(len(circle_Y)):
            if cv2.pointPolygonTest(contous, (circle_X[i], circle_Y[i]), False) < 0:
                big_r = half_r
                if_out = True
        if not if_out:
            small_r = half_r
    radius = small_r
    return radius       


def underWaterModel():
    model = classEndModel(r"")
    model.load_weights("./underWaterModel/classModel.h5")
    model.summary()
    return model

def UWNet():
    model = formEndModel()
    model.load_weights("./underWaterModel/endModel.h5")
    return model

def underImageSolve(path,Ratio=224):
    image = load_img(path)
    width = image.size[0]
    height = image.size[1]
    cols = int(width/Ratio)
    if(width-cols*Ratio)>=112:
        cols+=1
    rows = int(height/Ratio)
    if(height-rows*Ratio)>=112:
        rows+=1

    if cols*Ratio>2240:
        newWidth = 2240
        cols=10
    else:
        newWidth = cols*Ratio
    if rows*Ratio>1792:
        newHeight = 1792
        rows=8
    else:
        newHeight=rows*Ratio
    originalImage = image.resize((newWidth,newHeight))
    return rows,cols,originalImage

def underImagePredict(model,path,Ratio=224):
    
    rows ,cols, originalImage = underImageSolve(path)

    finalResult = np.zeros_like(originalImage,np.uint8)
    enhanceModel = UWNet()

    AreaTotal = 0
    lenthTotal = 0
    
    for i in range(rows):
        for j in range(cols):
            box = (j*Ratio,i*Ratio,(j+1)*Ratio,(i+1)*Ratio)
            tempImage = originalImage.crop(box)
            tempImage = np.array(tempImage)/255.0
            tempImage = np.expand_dims(tempImage,axis=0)
            with sess.as_default():
                with graph.as_default():
                    predict = model.predict(tempImage)

            result = np.argmax(predict)

            if result == 0:

                enhanceModel = enhanceModel
                enhanceImage = enhanceModel.predict(tempImage)
                enhanceImage = (np.squeeze(enhanceImage*255)).astype(np.uint8)
                finalResult[i*Ratio:(i+1)*Ratio,j*Ratio:(j+1)*Ratio,:] = enhanceImage

                
            else:
                nonecrack = np.zeros((Ratio,Ratio,3),np.uint8)
                finalResult[i*Ratio:(i+1)*Ratio,j*Ratio:(j+1)*Ratio,:] = nonecrack

    return finalResult,AreaTotal

if __name__=="__main__":
    model = airModel()
    model.summary()
    '''
    img1 =cv2.imread(r"D:\labelImage\crack_png\00558.png")
    img = Image.open(r"D:\labelImage\crack_png\00558.png")
    img = np.array(img)
    aa = thinImage(img)
    cv2.imshow("result", aa)
    cv2.waitKey(0)
    '''