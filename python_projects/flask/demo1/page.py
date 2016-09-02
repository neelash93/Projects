from flask import Flask, url_for, render_template, request;
from app import app;
import redis;

#coonect to redis database  redis is a key-value database
r = redis.StrictRedis(host = "localhost", port = 6379, db = 0, charset = "utf-8", decode_responses = True)
#r = redis.StrictRedis("localhost", 6379, 0)

#server/
@app.route('/')
def hello():    
    createLink = "<a href = '" + url_for('create') + "'>Create a question</a>"
    return """<html>
                <head>
                    <title>Hellow World</title>
                </head>
                <body>
                    <h1>bioinformatics</h1>
                    <h2>Fackbook App</h2>
                    """ + createLink + """
                </body>
              </html>"""

#server/create
@app.route('/create', methods=["GET", "POST"])
def create():
    if request.method == 'GET':
        #send the user form
        return render_template('CreateQuestion.html')
    elif request.method == 'POST':
        #read form data
        title = request.form['title']
        question = request.form['question']
        anwser = request.form['anwser']
        #store into database
        r.set(title + ":question", question)
        r.set(title + ":answer", anwser)
        return render_template('CreatedQuestion.html', quest = question)
    else:
        return "<h2>Invalid requestion</h2>"

#server/question/<title>
@app.route('/question/<title>', methods=["GET", "POST"])
def question(title):
    if request.method == "GET":
        #send user the form
       # question = "Question here."
        #read the question from  the database
        question = r.get(title + ":question")
        return render_template("AnwserQuestion.html", quest = question)
    elif request.method == "POST":
        #user attempt to anwser the question. Check if they are correct
        submittedAnwser = request.form["submittedAnwser"]

        anwser = r.get(title + ":answer")
        #get the anwser from the database

        if submittedAnwser == anwser:
            return render_template("Correct.html")
        else:
            return render_template("Incorrect.html", anw = anwser, subanw = submittedAnwser)
    else:
        return "<h2>Invalid requestion</h2>"