# -*- coding: utf-8 -*-
"""
Created on Fri Apr  2 14:51:21 2021

@author: Jiake Fu
"""
import io
from PIL import  Image
from flask import Flask,make_response
from flask import request
from flask_sqlalchemy import SQLAlchemy
import os
import cv2
import numpy as np
import time

from FlaskUtils import airModel ,modelPredict,thinImage,underWaterModel,underImagePredict


app = Flask(__name__)
basedir=os.path.abspath(os.path.dirname(__file__))
app.config['SQLALCHEMY_DATABASE_URI'] =\
 'sqlite:///' + os.path.join(basedir, 'data.sqlite')
app.config['SQLALCHEMY_COMMIT_ON_TEARDOWN']=False
app.config['SQLALCHEMY_TRACK_MODIFICATIONS']=True
db = SQLAlchemy(app)

modelAir = airModel()
modelUnder = underWaterModel()


class User(db.Model):
    __tablename__ ="users"
    id = db.Column(db.Integer,primary_key =True)
    name = db.Column(db.String(64),unique = True,index= True)
    password = db.Column(db.String(64))
    connectinfom = db.Column(db.String(64))
    def __repr__(self):
        return '<User %r>' % self.name

@app.route('/')
def test():

    if not User.query.filter_by(name="4561").first():
        newUser = User(name="4561",password="fjkjkf")
        db.session.add(newUser)
        db.session.commit()
    
    user = User.query.filter_by(name="4561").first()
    
    return "加载成功"+ user.name


@app.route("/forgetPS",methods = ["POST"])
def forgetPS():
    username=request.form['username']
    passWord=request.form['password']
    user = User.query.filter_by(name = username).first()
    if user:
        #修改成功
        user.password = passWord
        db.session.add(user)
        db.session.commit()
        return "159"
    else:

        return "357"

#此方法处理用户 注册逻辑
@app.route('/register',methods=['POST'])
def register():
    username=request.form['username']
    print(username)
    password=request.form['password']
    print(password)
    user = User.query.filter_by(name= username).first()

    if not user:

        newUser = User(name=username,password=password)
        db.session.add(newUser)
        db.session.commit()
        return "123"
    else:

        return "789"


@app.route('/login',methods=['POST'])
def login():
    username=request.form['username']
    password=request.form['password']
    print(username,password)
    user = User.query.filter_by(name=username).first()
    print(user)

    if user:

        if user.password == password:
            
            return "147"
        else:    
            print(user.password)
            return "258"
    
    else:

        return '369'
    

@app.route("/airImage",methods =["POST"])
def airImage():
    time1 = time.time()

    uploadDir = os.path.join(basedir,"uploadImage/airImage")
    if not os.path.exists(uploadDir):
        os.makedirs(uploadDir)

    listName = len(os.listdir(uploadDir))
    imageName = "upload_"+str(listName+1)+"_.png"
    newPath = os.path.join(uploadDir,imageName)

    img = request.files.get("files")
    img.save(newPath) 

    image,totalArea = modelPredict(modelAir,newPath)
   
    image = thinImage(np.array(image))
    totalLength = np.count_nonzero(image)
    
    image = Image.fromarray(np.uint8(image*255))

    if image.mode== "F":
        image = image.convert("L")
    imagebytes = io.BytesIO()
    image.save(imagebytes,format="png")
    imagebytes = imagebytes.getvalue()
    resp = make_response()
    resp.data = imagebytes
    resp.headers['Content-Type'] = 'image/jpeg'
    resp.headers["area"] = totalArea
    resp.headers["length"] = totalLength
    if totalLength==0:
        resp.headers["mean_width"] = 0.0
        resp.headers["real_width"] = 0.0
    else:
        resp.headers["mean_width"] = round(totalArea/totalLength,3)
        resp.headers["real_width"] = round(totalArea/totalLength*0.01179648,3)
    print("总的用时：",time.time()-time1)
    return resp


@app.route("/suggetst",methods =["post"])
def suggetInfom():
    username=request.form['username']
    connectinfom=request.form['connectinfom']
    print(username,connectinfom)
    user = User.query.filter_by(name=username).first()

    user.connectinfom = connectinfom
    db.session.add(user)
    db.session.commit()
    return ""


@app.route("/underwaterImage",methods = ["POST"])
def underwaterImage():
    time1 = time.time()
    uploadDir = os.path.join(basedir,"uploadImage/underwaterImage")
    if not os.path.exists(uploadDir):
        os.makedirs(uploadDir)
    listName = len(os.listdir(uploadDir))
    imageName = "upload_"+str(listName+1)+"_.jpg"
    newPath = os.path.join(uploadDir,imageName)

    img = request.files.get("files")
    img.save(newPath)
    image, totalArea = underImagePredict(modelUnder,newPath)
    
    totalLength = 0
    image = Image.fromarray(image)

    if image.mode== "F":
        image = image.convert("L")
    imagebytes = io.BytesIO()
    image.save(imagebytes,format="png")
    imagebytes = imagebytes.getvalue()
    resp = make_response()
    resp.data = imagebytes
    resp.headers['Content-Type'] = 'image/jpeg'
    resp.headers["area"] = totalArea
    resp.headers["length"] = totalLength
    if totalLength==0:
        resp.headers["mean_width"] = 0.0
    else:
        resp.headers["mean_width"] = round(totalArea/totalLength,3)
        resp.headers["real_width"] = round(totalArea/totalLength*0.01179648,3)
    print("总的用时：",time.time()-time1)
    return resp

if __name__ == '__main__':
    db.create_all()
    app.run(host='0.0.0.0',port=5000)